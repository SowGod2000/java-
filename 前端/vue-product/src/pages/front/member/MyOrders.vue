<template>
  <div class="orders-container">
    <h2 class="mb-4">我的訂單</h2>

    <el-table :data="orders" stripe v-loading="loading" empty-text="目前尚無訂單">
      <el-table-column prop="ordercategory" label="訂單類型" width="150" />
      <el-table-column prop="orderNumber" label="訂單編號" width="180" />
      <el-table-column prop="totalAmount" label="總金額" width="120">
        <template #default="{ row }">
          NT$ {{ row.totalAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下單時間" width="180" />
      <el-table-column prop="status" label="狀態" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PAID' ? 'success' : 'warning'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-alert v-if="error" type="error" :title="error" class="mt-3" show-icon />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const orders = ref([])
const loading = ref(true)
const error = ref('')

const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await fetch('/api/orders/member', {
      headers: userStore.authHeader,
      credentials: 'include'
    })
    if (!res.ok) throw new Error('無法取得訂單資料')
    orders.value = await res.json()
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-container {
  max-width: 1000px;
  margin: 60px auto;
  padding: 20px;
}
.mb-4 {
  margin-bottom: 20px;
}
</style>
