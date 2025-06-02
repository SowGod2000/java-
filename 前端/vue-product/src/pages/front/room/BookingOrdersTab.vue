<template>

    <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="bookingOrderId" label="訂單編號" width="120" />
        <el-table-column prop="checkInDate" label="入住日期" />
        <el-table-column prop="checkOutDate" label="退房日期" />
        <el-table-column prop="roomtype.roomtypeName" label="房型名稱" />
        <el-table-column prop="quantity" label="房間數" />
        <el-table-column prop="totalPrice" label="總金額" />
        <el-table-column prop="status" label="訂單狀態" />
        <el-table-column prop="paymentStatus" label="付款狀態" />
        <el-table-column label="操作" width="150">
            <template #default="scope">
                <el-button type="danger" size="small" @click="openConfirmDialog(scope.row.bookingOrderId)"
                    :disabled="!canCancel(scope.row.checkInDate) || isOrderFinalized(scope.row.status)">
                    取消訂單
                </el-button>
            </template>
        </el-table-column>
    </el-table>
    <div v-if="!loading && orders.length === 0" style="text-align: center; margin-top: 20px;">
        尚無訂單紀錄
    </div>

    <!-- 成功提示 -->
    <v-container>
        <v-snackbar v-model="showMessage" timeout="1500" location="top center">
            ✅ 已取消訂單
        </v-snackbar>
    </v-container>

    <!-- 確認取消的對話框 -->
    <el-dialog v-model="confirmDialogVisible" title="取消訂單確認" width="400px" center>
        <div style="text-align: center;">您真的要取消這筆訂單嗎？</div>
        <template #footer>
            <el-button @click="confirmDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmCancelOrder">確認</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import axios from 'axios'
import dayjs from 'dayjs'

const userStore = useUserStore()
const memberId = ref('')

const orders = ref([])
const loading = ref(false)
const showMessage = ref(false)

const confirmDialogVisible = ref(false) // 控制 dialog 顯示
const selectedBookingOrderId = ref(null) // 暫存要取消的訂單ID

const fetchOrders = async () => {
    loading.value = true
    try {
        const response = await axios.get(`http://localhost:8080/bookingOrder/findByMemberId/${memberId.value}`)
        // console.log(response);
        orders.value = response.data
    } catch (error) {
        console.error('取得訂房訂單失敗:', error)
    } finally {
        loading.value = false
    }
}

// 判斷能不能取消 (入住前一天以前才能取消)
const canCancel = (checkInDateStr) => {
    const today = dayjs().startOf('day')
    const checkInDate = dayjs(checkInDateStr).startOf('day')
    return today.isBefore(checkInDate.subtract(1, 'day'))
}

// 判斷訂單是否已完成或已取消
const isOrderFinalized = (status) => {
    return status === '已取消' || status === '已完成'
}

// 點擊「取消訂單」按鈕 -> 打開確認 Dialog
const openConfirmDialog = (bookingOrderId) => {
    selectedBookingOrderId.value = bookingOrderId
    confirmDialogVisible.value = true
}

// 在 Dialog 按下「確認」 -> 執行真正取消
const confirmCancelOrder = async () => {
    if (!selectedBookingOrderId.value) return
    try {
        await axios.put(`http://localhost:8080/bookingOrder/cancel/${selectedBookingOrderId.value}`)
        showMessage.value = true
        fetchOrders() // 取消成功後重新撈一次
    } catch (error) {
        console.error('取消訂單失敗:', error)
        alert('取消失敗，請稍後再試')
    } finally {
        confirmDialogVisible.value = false // 關閉 Dialog
        selectedBookingOrderId.value = null // 清空
    }
}

onMounted(() => {
    memberId.value = userStore.user.memberID
    fetchOrders()
})

</script>