<template>
  <v-container class="py-5">
    <v-card>
      <v-card-title>
        商品訂單管理
        <v-spacer />
        <v-text-field
          v-model="search"
          placeholder="搜尋姓名或 Email"
          prepend-inner-icon="mdi-magnify"
          hide-details
          single-line
          clearable
          class="me-4"
          style="max-width: 300px"
          @keyup.enter="fetchOrders"
        />
        <v-btn icon @click="fetchOrders">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
        <v-btn
          color="black"
          class="ml-4 text-white font-weight-bold rounded"
          prepend-icon="mdi-download"
          @click="exportCSV"
        >
          匯出 CSV
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="orders || []"
          :items-per-page="10"
          class="elevation-1"
        >
          <template #item.totalAmount="{ item }">
            NT$ {{ item.totalAmount?.toLocaleString() }}
          </template>

          <template #item.paymentStatus="{ item }">
            <v-chip
              :color="item.paymentStatus === 'PAID'
                ? 'green'
                : item.paymentStatus === 'CANCELLED'
                ? 'red'
                : 'orange'"
              dark small
            >
              {{
                item.paymentStatus === 'PAID'
                  ? '已付款'
                  : item.paymentStatus === 'CANCELLED'
                  ? '已取消付款'
                  : '待付款'
              }}
            </v-chip>
          </template>

          <template #item.orderStatus="{ item }">
            <v-chip
              :color="item.orderStatus === '已出貨'
                ? 'blue'
                : item.orderStatus === '處理中'
                ? 'green'
                : 'grey'"
              dark small
            >
              {{ item.orderStatus }}
            </v-chip>
          </template>

          <template #item.orderTime="{ item }">
            {{ formatDate(item.orderTime) }}
          </template>

          <template #item.actions="{ item }">
            <v-btn size="small" color="primary" @click="viewDetail(item)">查看</v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- 訂單詳情對話框 -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>訂單詳情 #{{ selectedOrder?.id }}</v-card-title>
        <v-card-text>
          <p><strong>會員姓名：</strong>{{ selectedOrder?.customerName }}</p>
          <p><strong>Email：</strong>{{ selectedOrder?.email }}</p>
          <p><strong>總金額：</strong>NT$ {{ selectedOrder?.totalAmount?.toLocaleString() }}</p>
          <p><strong>付款狀態：</strong>{{ selectedOrder?.paymentStatus }}</p>
          <p><strong>付款方式：</strong>{{ selectedOrder?.paymentMethod || '－' }}</p>
          <p><strong>運送方式：</strong>{{ selectedOrder?.shippingMethod || '－' }}</p>
          <v-select
            v-model="selectedOrder.orderStatus"
            :items="['處理中', '已出貨', '已取消']"
            label="訂單狀態"
            class="mt-3"
            density="compact"
          />
          <v-divider class="my-3" />
          <h5>🛒 商品明細</h5>
          <v-list density="compact">
            <v-list-item v-for="item in selectedOrder?.items" :key="item.productId">
              <v-list-item-content>
                <v-list-item-title>{{ item.productName }}</v-list-item-title>
                <v-list-item-subtitle>{{ item.quantity }} x NT${{ item.price }}</v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" @click="updateOrderStatus">儲存</v-btn>
          <v-btn text @click="dialog = false">關閉</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const orders = ref([])
const dialog = ref(false)
const selectedOrder = ref(null)
const search = ref('')

const headers = [
  { title: '訂單編號', key: 'id' },
  { title: '會員姓名', key: 'customerName' },
  { title: 'Email', key: 'email' },
  { title: '金額', key: 'totalAmount' },
  { title: '付款狀態', key: 'paymentStatus' },
  { title: '訂單狀態', key: 'orderStatus' },
  { title: '成立時間', key: 'orderTime' },
  { title: '操作', key: 'actions', sortable: false }
]

const fetchOrders = async () => {
  try {
    let url = 'http://localhost:8080/api/orders/all'
    if (search.value) {
      url += `?search=${encodeURIComponent(search.value)}`
    }

    const res = await fetch(url)
    if (!res.ok) throw new Error('API 請求失敗')
    orders.value = await res.json()
  } catch (e) {
    console.error('❌ 載入訂單失敗：', e)
    orders.value = []
  }
}

const viewDetail = (order) => {
  selectedOrder.value = { ...order }
  dialog.value = true
}

const updateOrderStatus = async () => {
  if (!selectedOrder.value) return
  const id = selectedOrder.value.id
  const status = selectedOrder.value.orderStatus
  try {
    await fetch(`http://localhost:8080/api/orders/${id}/status?value=${encodeURIComponent(status)}`, {
      method: 'PUT'
    })
    dialog.value = false
    fetchOrders()
  } catch (e) {
    console.error('❌ 更新訂單狀態失敗：', e)
  }
}

const exportCSV = () => {
  const csvContent = [
    ['訂單編號', '會員姓名', 'Email', '總金額', '付款狀態', '訂單狀態', '成立時間'],
    ...orders.value.map((o) => [
      o.id,
      o.customerName,
      o.email,
      o.totalAmount,
      o.paymentStatus,
      o.orderStatus,
      formatDate(o.orderTime)
    ])
  ]
    .map(row => row.join(','))
    .join('\n')

  const timestamp = new Date().toISOString().replace(/[-:T]/g, '').slice(0, 14) // e.g. 20250501143000
  const filename = `訂單清單-${timestamp}.csv`

  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.setAttribute('download', filename)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}


const formatDate = (d) => new Date(d).toLocaleString()

onMounted(fetchOrders)
</script>
