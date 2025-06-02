<template>
  <v-container class="py-5">
    <v-card class="pa-4" elevation="2">
      <v-card-title class="text-h5 mb-4"> 訂單完成</v-card-title>

      <v-alert v-if="loading" type="info" variant="outlined">讀取中，請稍候...</v-alert>

      <div v-else-if="order">
        <v-list dense>
          <v-list-item><v-list-item-title><strong> 訂單編號：</strong> {{ order.id }}</v-list-item-title></v-list-item>
          <v-list-item><v-list-item-title><strong> 顧客姓名：</strong> {{ order.customerName }}</v-list-item-title></v-list-item>
          <v-list-item><v-list-item-title><strong> Email：</strong> {{ order.email }}</v-list-item-title></v-list-item>
          <v-list-item><v-list-item-title><strong> 訂單時間：</strong> {{ formatDate(order.orderTime) }}</v-list-item-title></v-list-item>
          <v-list-item>
            <v-list-item-title><strong> 總金額：</strong> NT$ {{ formatPrice(order.totalAmount) }}</v-list-item-title>
          </v-list-item>
          <v-list-item>
            <v-list-item-title>
              <strong> 付款狀態：</strong>
              <v-chip :color="order.paymentStatus === 'PAID' ? 'green' : 'red'" dark variant="flat" size="small">
                {{ order.paymentStatus === 'PAID' ? '已付款' : '尚未付款' }}
              </v-chip>
            </v-list-item-title>
          </v-list-item>
        </v-list>

        <v-divider class="my-4" />

        <h5 class="mb-3"> 商品明細</h5>
        <v-table density="compact" class="elevation-1">
          <thead>
            <tr>
              <th>名稱</th>
              <th>單價</th>
              <th>數量</th>
              <th>小計</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in order.items" :key="item.productId">
              <td>{{ item.productName }}</td>
              <td>NT$ {{ formatPrice(item.price) }}</td>
              <td>{{ item.quantity }}</td>
              <td>NT$ {{ formatPrice(item.price * item.quantity) }}</td>
            </tr>
          </tbody>
        </v-table>
      </div>

      <v-alert v-else type="error" class="mt-4"> 查無訂單資訊，請確認訂單編號是否正確。</v-alert>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const order = ref(null)
const loading = ref(true)

const fetchOrder = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/orders/${route.params.id}`)
    if (!res.ok) throw new Error('找不到訂單')
    order.value = await res.json()
  } catch (err) {
    order.value = null
    console.error(' 無法取得訂單資訊', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => new Date(dateStr).toLocaleString()
const formatPrice = (value) => Number(value).toLocaleString()

onMounted(fetchOrder)
</script>