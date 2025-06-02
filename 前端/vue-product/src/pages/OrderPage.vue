<template>
  <div class="container py-5">
    <h2 class="mb-4"> 訂單完成</h2>

    <div v-if="loading" class="alert alert-secondary">
      讀取中，請稍候...
    </div>

    <div v-else-if="order">
      <p><strong> 訂單編號：</strong>{{ order.id }}</p>
      <p><strong> 顧客姓名：</strong>{{ order.customerName }}</p>
      <p><strong> Email：</strong>{{ order.email }}</p>
      <p><strong> 訂單時間：</strong>{{ formatDate(order.orderTime) }}</p>
      <p><strong> 總金額：</strong>NT$ {{ formatPrice(order.totalAmount) }}</p>
      <p><strong> 付款狀態：</strong>
        <span :class="order.paymentStatus === 'PAID' ? 'text-success' : 'text-danger'">
          {{ order.paymentStatus === 'PAID' ? '已付款' : '尚未付款' }}
        </span>
      </p>

      <hr />

      <h5>🛒 商品明細</h5>
      <table class="table table-bordered">
        <thead class="table-light">
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
      </table>
    </div>

    <div v-else class="alert alert-danger">
      ❌ 查無訂單資訊，請確認訂單編號是否正確。
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const order = ref(null)
const loading = ref(true)

const fetchOrder = async () => {
  try {
    const res = await fetch(`/api/orders/${route.params.id}`)
    if (!res.ok) throw new Error('找不到訂單')
    order.value = await res.json()
  } catch (err) {
    order.value = null
    console.error('❌ 無法取得訂單資訊', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => new Date(dateStr).toLocaleString()
const formatPrice = (value) => Number(value).toLocaleString()

onMounted(fetchOrder)
</script>

<style scoped>
.table td, .table th {
  vertical-align: middle;
}
</style>
