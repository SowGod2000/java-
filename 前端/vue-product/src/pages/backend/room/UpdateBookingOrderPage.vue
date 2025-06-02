<template>
    <v-container>
        <v-card class="pa-5 mx-auto" elevation="2" max-width="800">
            <v-card-title>編輯訂單(*表示可以修改)</v-card-title>
            <v-form @submit.prevent="submitUpdate">
                <v-text-field label="訂單編號" v-model="order.bookingOrderId" readonly />
                <v-text-field label="會員編號" v-model="order.memberId" readonly />
                <v-text-field label="入住日" v-model="order.checkInDate" readonly />
                <v-text-field label="退房日" v-model="order.checkOutDate" readonly />
                <v-text-field label="房型名稱" v-model="order.roomtype.roomtypeName" readonly />
                <v-text-field label="售價*" v-model.number="order.pricePerRoom" />
                <v-text-field label="房間數" v-model="order.quantity" readonly />
                <v-text-field label="總金額" v-model="order.totalPrice" readonly />

                <v-select label="訂單狀態*" v-model="order.status" :items="orderStatusOptions" required />
                <v-select label="付款狀態*" v-model="order.paymentStatus" :items="paymentStatusOptions" required />
                <v-select label="付款方式*" v-model="order.paymentMethod" :items="paymentMethodOptions" required />

                <v-text-field label="實際入住時間" :model-value="formatDateTime(order.checkInTime)" readonly />
                <v-text-field label="實際退房時間" :model-value="formatDateTime(order.checkOutTime)" readonly />
                <v-textarea label="備註*" v-model="order.note" />

                <v-btn type="submit" color="primary">確認修改</v-btn>
                <v-btn color="secondary" @click="router.back()">取消</v-btn>
            </v-form>
        </v-card>
    </v-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

// route
const route = useRoute()
const router = useRouter()
const bookingOrderId = route.params.id

// 訂單資料
const order = ref({
    bookingOrderId: '',
    memberId: '',
    checkInDate: '',
    checkOutDate: '',
    roomtype: { roomtypeName: '' },
    quantity: 1,
    totalPrice: 0,
    pricePerRoom: 0,
    checkInTime: '',
    checkOutTime: '',
    note: '',
    status: '',
    paymentStatus: '',
    paymentMethod: ''
})

// 下拉選單資料
const orderStatusOptions = ['已成立', '已完成', '已取消']
const paymentStatusOptions = ['已付款', '未付款', '已退款']
const paymentMethodOptions = ['無', '信用卡', '匯款', '現金']

// 取得訂單資料
onMounted(async () => {
    try {
        const res = await axios.get(`http://localhost:8080/bookingOrder/findById/${bookingOrderId}`)
        order.value = res.data
    } catch (err) {
        console.error('取得訂單失敗:', err)
    }
})

// 計算總金額
function calculateNights() {
    const checkIn = new Date(order.value.checkInDate)
    const checkOut = new Date(order.value.checkOutDate)
    const diffTime = checkOut - checkIn
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays > 0 ? diffDays : 1
}

watch(() => order.value.pricePerRoom, () => {
    const nights = calculateNights()
    order.value.totalPrice = nights * order.value.quantity * order.value.pricePerRoom
})

//時間格式化
function formatDateTime(datetime) {
    if (!datetime) return ''
    const dt = new Date(datetime)
    const year = dt.getFullYear()
    const month = String(dt.getMonth() + 1).padStart(2, '0')
    const day = String(dt.getDate()).padStart(2, '0')
    const hour = String(dt.getHours()).padStart(2, '0')
    const minute = String(dt.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
}

async function submitUpdate() {
    const status = order.value.status
    const paymentStatus = order.value.paymentStatus
    const paymentMethod = order.value.paymentMethod

    // 驗證邏輯條件
    if (paymentStatus === '未付款') {
        if (paymentMethod !== '無') {
            alert('當付款狀態為「未付款」時，付款方式必須為「無」。')
            return
        }
        if (!(status === '已成立' || status === '已取消')) {
            alert('當付款狀態為「未付款」時，訂單狀態只能為「已成立」或「已取消」。')
            return
        }
    }

    if (paymentStatus === '已退款') {
        if (paymentMethod === '無') {
            alert('當付款狀態為「已退款」時，付款方式不能為「無」。')
            return
        }
        if (status !== '已取消') {
            alert('當付款狀態為「已退款」時，訂單狀態必須為「已取消」。')
            return
        }
    }

    if (paymentStatus === '已付款') {
        if (paymentMethod === '無') {
            alert('當付款狀態為「已付款」時，付款方式不能為「無」。')
            return
        }
        if (status === '已取消') {
            alert('當付款狀態為「已付款」時，訂單狀態不能為「已取消」。')
            return
        }
    }

    // 若都通過檢查，送出更新請求
    try {
        await axios.put(`http://localhost:8080/bookingOrder/update/${order.value.bookingOrderId}`, {
            pricePerRoom: order.value.pricePerRoom,
            totalPrice: order.value.totalPrice,
            note: order.value.note,
            status,
            paymentStatus,
            paymentMethod
        })
        alert('修改成功')
        router.push('/backend/bookingOrder')
    } catch (err) {
        console.error('更新失敗:', err)
    }
}


</script>
