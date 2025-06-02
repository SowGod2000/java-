<template>
  <div class="container py-5">
    <h2 class="mb-4">💼 我的購物車</h2>

    <table class="table table-bordered align-middle" v-if="cartItems.length">
      <thead class="table-light">
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
          <td><img :src="item.imageUrl" style="height: 60px" /></td>
          <td>{{ item.productName }}</td>
          <td>NT$ {{ item.price.toLocaleString() }}</td>
          <td>
            <input type="number" v-model.number="item.quantity" @change="updateQuantity(item)" class="form-control" min="1" style="width: 80px" />
          </td>
          <td>NT$ {{ (item.price * item.quantity).toLocaleString() }}</td>
          <td>
            <button class="btn btn-sm btn-danger" @click="removeItem(item.productId)">刪除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-else class="alert alert-info">目前購物車是空的。</div>

    <div v-if="cartItems.length && userStore.user" class="mt-4">
      <h4>總金額：NT$ {{ total.toLocaleString() }}</h4>

      <form class="row g-2 mt-3" @submit.prevent="checkout">
       <div class="col-md-6">
  <p><strong>姓名：</strong>{{ userStore.user.name }}</p>
</div>
<div class="col-md-6">
  <p><strong>Email：</strong>{{ userStore.user.email }}</p>
</div>

        <div class="col-12">
          <button class="btn btn-success">結帳</button>
          <button class="btn btn-outline-secondary ms-2" type="button" @click="clearCart">清空購物車</button>
        </div>
      </form>
    </div>

    <!-- 綠界表單：送出用 -->
    <form ref="ecpayForm" :action="ecpayUrl" method="POST" style="display: none">
      <input v-for="(value, key) in paymentData" :key="key" :name="key" :value="value" type="hidden" />
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const cart = ref({ cartItems: [], totalPrice: 0 })
const cartItems = computed(() => cart.value.cartItems || [])
const total = computed(() => cart.value.totalPrice || 0)

const ecpayForm = ref(null)
const ecpayUrl = 'https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'
const paymentData = ref({})

const fetchCart = async () => {
  try {
    const res = await fetch('/api/cart', {
      headers: userStore.authHeader, // ✅ 自動帶 token
      credentials: 'include'
    })
    cart.value = await res.json()
  } catch (err) {
    console.error('❌ 無法取得購物車', err)
  }
}

const updateQuantity = async (item) => {
  await fetch(`/api/cart/updateQuantity/${item.productId}?quantity=${item.quantity}`, {
    method: 'PUT',
    headers: userStore.authHeader,
    credentials: 'include'
  })
  fetchCart()
}

const removeItem = async (productId) => {
  await fetch(`/api/cart/remove/${productId}`, {
    method: 'DELETE',
    headers: userStore.authHeader,
    credentials: 'include'
  })
  fetchCart()
}

const clearCart = async () => {
  await fetch('/api/cart/clear', {
    method: 'DELETE',
    headers: userStore.authHeader,
    credentials: 'include'
  })
  fetchCart()
}

const checkout = async () => {
  try {
    if (!userStore.user) {
      alert('請先登入會員')
      return
    }

    const items = cartItems.value.map(item => ({
      productId: item.productId,
      productName: item.productName,
      price: item.price,
      quantity: item.quantity,
      imageUrl: item.imageUrl
    }))

    const payload = {
      customerName: userStore.user.name, // ✅ 自動帶會員名稱
      email: userStore.user.email,       // ✅ 自動帶會員Email
      items
    }

    const res = await fetch('/api/orders/checkout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...userStore.authHeader  // ✅ 自動帶 token
      },
      body: JSON.stringify(payload),
      credentials: 'include'
    })

    if (!res.ok) {
      const msg = await res.text()
      throw new Error(`後端錯誤：${msg}`)
    }

    const order = await res.json()

    // 取得綠界付款資訊
    const paymentRes = await fetch(`/api/ecpay/payment-info?orderId=${order.id}`, {
      headers: userStore.authHeader,
      credentials: 'include'
    })
    if (!paymentRes.ok) throw new Error('無法取得金流資料')

    paymentData.value = await paymentRes.json()

    // ✅ 轉跳付款頁
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

<style scoped>
img {
  max-height: 60px;
}
</style>
