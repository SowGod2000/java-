<template>
  <div class="orders-container">
    <el-card shadow="hover" class="orders-card">
      <h2 class="mb-4">我的商品訂單</h2>

      <el-table
        :data="orders"
        stripe
        border
        v-loading="loading"
        empty-text="目前尚無訂單"
        class="custom-table"
      >
        <el-table-column prop="id" label="訂單編號" width="120" />
        <el-table-column prop="totalAmount" label="總金額" width="120">
          <template #default="{ row }">
            <strong>NT$ {{ row.totalAmount.toLocaleString() }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下單時間" width="180" />
        <el-table-column prop="paymentStatus" label="付款狀態" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.paymentStatus === 'PAID'
                ? 'success'
                : row.paymentStatus === 'CANCELLED'
                ? 'danger'
                : 'warning'"
              effect="light"
            >
              {{
                row.paymentStatus === 'PAID'
                  ? '已付款'
                  : row.paymentStatus === 'CANCELLED'
                  ? '已取消付款'
                  : '待付款'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="訂單狀態" width="100" />
        <el-table-column prop="shippingMethod" label="運送方式" width="120" />
        <el-table-column prop="paymentMethod" label="付款方式" width="120" />

        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button
              size="small"
              style="color: white; background-color: #409eff; border: none;"
              @click="openDialog(row)"
            >
              查看明細
            </el-button>
            <el-button
              v-if="row.orderStatus === '處理中'"
              size="small"
              style="color: white; background-color: #f56c6c; border: none;"
              class="ml-2"
              @click="cancelOrder(row)"
            >
              取消訂單
            </el-button>
            <span
              v-else-if="row.orderStatus === '已取消'"
              class="text-danger ml-2"
            >
              已取消
            </span>
          </template>
        </el-table-column>
      </el-table>

      <el-alert
        v-if="error"
        type="error"
        :title="error"
        class="mt-3"
        show-icon
        closable
      />

      <!-- 🛒 商品明細對話框 -->
      <el-dialog
        v-model="dialogVisible"
        width="500px"
        center
      >
        <el-divider><strong>商品明細</strong></el-divider>

        <el-row
          v-for="item in selectedOrder?.items"
          :key="item.productId"
          class="mb-2"
          justify="space-between"
          align="middle"
          style="font-size: 15px"
        >
          <el-col :span="12">{{ item.productName }}</el-col>
          <el-col :span="6" class="text-right">x {{ item.quantity }}</el-col>
          <el-col :span="6" class="text-right">NT$ {{ item.price.toLocaleString() }}</el-col>
        </el-row>

        <el-divider />
        <div class="text-right font-weight-bold">
          總金額：NT$ {{ selectedOrder?.totalAmount.toLocaleString() }}
        </div>

        <template #footer>
          <el-button type="primary" @click="dialogVisible = false">關閉</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const orders = ref([])
const loading = ref(true)
const error = ref('')
const dialogVisible = ref(false)
const selectedOrder = ref(null)

const fetchOrders = async () => {
  try {
    loading.value = true
    const res = await fetch('http://localhost:8080/api/orders/member', {
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

const openDialog = (order) => {
  selectedOrder.value = order
  dialogVisible.value = true
}

const cancelOrder = async (order) => {
  if (order.orderStatus === '已取消') {
    error.value = '您已取消此訂單'
    return
  }

  try {
    const confirmCancel = window.confirm('確定要取消此訂單嗎？')
    if (!confirmCancel) return

    const res = await fetch(`http://localhost:8080/api/orders/${order.id}/cancel`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...userStore.authHeader
      },
      credentials: 'include'
    })

    if (!res.ok) {
      const msg = await res.text()
      throw new Error(msg || '取消失敗')
    }

    error.value = ''
    fetchOrders()
  } catch (err) {
    error.value = err.message
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-container {
  max-width: 1100px;
  margin: 60px auto;
  padding: 20px;
}

.orders-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.mb-4 {
  margin-bottom: 20px;
  font-weight: 600;
  color: #333;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
  font-size: 14px;
}

.text-danger {
  color: #f56c6c;
}

.text-right {
  text-align: right;
}

.font-weight-bold {
  font-weight: bold;
}
</style>
