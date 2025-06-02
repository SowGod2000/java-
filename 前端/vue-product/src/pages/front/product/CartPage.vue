<template>
  <div class="container py-5">
    <h2 class="mb-4"> 我的購物車</h2>

    <v-table v-if="cartItems.length" class="elevation-1">
      <thead>
        <tr>
          <th>圖片</th>
          <th>名稱</th>
          <th>單價</th>
          <th>數量</th>
          <th>小計</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in cartItems" :key="item.productId">
          <td><v-img :src="item.imageUrl" max-height="60" contain /></td>
          <td>{{ item.productName }}</td>
          <td>NT$ {{ item.price.toLocaleString() }}</td>
          <td>
            <v-text-field
              v-model.number="item.quantity"
              type="number"
              min="1"
              hide-details
              style="width: 80px"
              @change="updateQuantity(item)"
            />
          </td>
          <td>NT$ {{ (item.price * item.quantity).toLocaleString() }}</td>
          <td>
            <v-btn color="error" icon @click="removeItem(item.productId)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </td>
        </tr>
      </tbody>
    </v-table>

    <v-alert v-else type="info" class="mt-4">目前購物車是空的。</v-alert>

    <!-- ✅ 顯示會員資訊與選擇 -->
    <v-card v-if="cartItems.length && userStore.user" class="mt-6" elevation="2" color="blue-lighten-5">
      <v-card-title class="text-h6 text-primary">👤 會員資訊</v-card-title>
      <v-card-text>
        <v-row dense>
          <v-col cols="12" sm="6"><strong>姓名：</strong>{{ userStore.user.name }}</v-col>
          <v-col cols="12" sm="6"><strong>Email：</strong>{{ userStore.user.email }}</v-col>
          <v-col cols="12" sm="6"><strong>電話：</strong>{{ userStore.user.phone }}</v-col>
          <v-col cols="12" sm="6"><strong>預設送貨地址：</strong>{{ userStore.user.address }}</v-col>

          <!-- 運送方式選單 -->
          <v-col cols="12" sm="6">
            <v-select
              v-model="shippingMethod"
              :items="['宅配', '7-11取貨']"
              label="運送方式"
              required
            />
          </v-col>

          <!-- 付款方式選單 -->
          <v-col cols="12" sm="6">
            <v-select
              v-model="paymentMethod"
              :items="['信用卡', 'ATM']"
              label="付款方式"
              required
            />
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- ✅ 顯示金額與結帳按鈕 -->
    <div v-if="cartItems.length && userStore.user" class="mt-4">
      <h4 class="mb-4">總金額：NT$ {{ total.toLocaleString() }}</h4>
      <v-btn color="success" @click="checkout">結帳</v-btn>
      <v-btn class="ms-2" variant="outlined" color="grey" @click="clearCart">清空購物車</v-btn>
    </div>

    <!-- ✅ 綠界表單：送出用 -->
    <form ref="ecpayForm" :action="ecpayUrl" method="POST" style="display: none">
      <input v-for="(value, key) in paymentData" :key="key" :name="key" :value="value" type="hidden" />
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const cart = ref({ cartItems: [], totalPrice: 0 })
const cartItems = computed(() => cart.value.cartItems || [])
const total = computed(() => cart.value.totalPrice || 0)

const shippingMethod = ref('')
const paymentMethod = ref('')

const ecpayForm = ref(null)
const ecpayUrl = 'https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'
const paymentData = ref({})

const fetchCart = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/cart', {
      headers: userStore.authHeader,
      credentials: 'include',
    })
    cart.value = await res.json()
  } catch (err) {
    console.error('❌ 無法取得購物車', err)
  }
}

const updateQuantity = async (item) => {
  await fetch(`http://localhost:8080/api/cart/updateQuantity/${item.productId}?quantity=${item.quantity}`, {
    method: 'PUT',
    headers: userStore.authHeader,
    credentials: 'include',
  })
  fetchCart()
}

const removeItem = async (productId) => {
  await fetch(`http://localhost:8080/api/cart/remove/${productId}`, {
    method: 'DELETE',
    headers: userStore.authHeader,
    credentials: 'include',
  })
  fetchCart()
}

const clearCart = async () => {
  await fetch('http://localhost:8080/api/cart/clear', {
    method: 'DELETE',
    headers: userStore.authHeader,
    credentials: 'include',
  })
  fetchCart()
}

const checkout = async () => {
  try {
    if (!userStore.user) {
      alert('請先登入會員')
      return
    }

    if (!shippingMethod.value || !paymentMethod.value) {
      alert('請選擇運送方式與付款方式')
      return
    }

    const items = cartItems.value.map((item) => ({
      productId: item.productId,
      productName: item.productName,
      price: item.price,
      quantity: item.quantity,
      imageUrl: item.imageUrl,
    }))

    const payload = {
      customerName: userStore.user.name,
      email: userStore.user.email,
      shippingMethod: shippingMethod.value,
      paymentMethod: paymentMethod.value,
      items,
    }

    const res = await fetch('http://localhost:8080/api/orders/checkout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...userStore.authHeader,
      },
      body: JSON.stringify(payload),
      credentials: 'include',
    })

    if (!res.ok) {
      const msg = await res.text()
      throw new Error(`後端錯誤：${msg}`)
    }

    const order = await res.json()

    const paymentRes = await fetch(`http://localhost:8080/api/ecpay/payment-info?orderId=${order.id}`, {
      headers: userStore.authHeader,
      credentials: 'include',
    })

    if (!paymentRes.ok) throw new Error('無法取得金流資料')
    paymentData.value = await paymentRes.json()

    window.location.href = `/pay?orderId=${order.id}`
  } catch (err) {
    console.error('❌ 結帳失敗', err)
    alert('❌ 結帳失敗：' + err.message)
  }
}

onMounted(async () => {
  if (!userStore.user) {
    await userStore.initUserFromApi()
  }
  fetchCart()
})
</script>
