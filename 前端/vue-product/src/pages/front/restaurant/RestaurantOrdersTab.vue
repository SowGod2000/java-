<template>
  <div class="page-background">
    
    

      <!-- 訂位表格 -->
      <el-table :data="reservations" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderId" label="訂位編號" width="120" />
        <el-table-column prop="customerName" label="顧客名稱" />
        <el-table-column prop="phone" label="電話" />
        <el-table-column prop="mail" label="Email" />
        <el-table-column prop="orderdate" label="訂位日期" />
        <el-table-column label="時段">
          <template #default="scope">
            {{ scope.row.restaurantTimeslot?.startTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="adult" label="大人" />
        <el-table-column prop="child" label="小孩" />
        <el-table-column label="座位">
          <template #default="scope">
            {{ scope.row.tables?.map(t => t.tableCode).join(', ') || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="expectedPrice" label="預估金額" />
        <el-table-column prop="deposit" label="訂金" />
        <el-table-column prop="note" label="備註" />
        <el-table-column prop="orderStatus" label="訂單狀態" />
      </el-table>

      <div v-if="!loading && reservations.length === 0" style="text-align: center; margin-top: 20px;">
        尚無訂位紀錄
      </div>
   

    <el-dialog v-model="errorDialogVisible" title="查詢失敗" width="400px" center>
      <div style="text-align: center;">請確認電話與 Email 是否正確</div>
      <template #footer>
        <el-button type="primary" @click="errorDialogVisible = false">確定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import axios from 'axios'

const userStore = useUserStore()
const route = useRoute()

const phone = ref('')
const email = ref('')
const reservations = ref([])
const loading = ref(false)
const errorDialogVisible = ref(false)

onMounted(async () => {
  const phoneParam = route.query.phone
  const emailParam = route.query.email

  if (phoneParam && emailParam) {
    phone.value = phoneParam
    email.value = emailParam
    await searchReservation(true)
    return
  }

  await userStore.initUserFromApi()
  if (userStore.user?.phone && userStore.user?.email) {
    phone.value = userStore.user.phone
    email.value = userStore.user.email
    await searchReservation(true)
  } else {
    console.warn('尚未登入，無法查詢訂位')
  }
})

async function searchReservation(isAuto = false) {
  if (!phone.value || !email.value) {
    if (!isAuto) errorDialogVisible.value = true
    return
  }

  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/firstHotel/restaurant/searchOrder', {
      params: {
        phone: phone.value,
        email: email.value
      }
    })
    reservations.value = res.data
  } catch (error) {
    console.error('查詢失敗', error)
    if (!isAuto) errorDialogVisible.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>

</style>
