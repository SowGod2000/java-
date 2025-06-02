<template>
    <v-container>
        <v-data-table :headers="headers" :items="filteredOrders" :items-per-page="10" class="elevation-1">
            <!-- 上方工具列 -->
            <template v-slot:top>
                <v-toolbar flat>
                    <v-toolbar-title>訂房訂單管理</v-toolbar-title>
                    <v-spacer></v-spacer>

                    <!-- 搜尋欄 -->
                    <v-text-field v-model="search" label="搜尋訂單" prepend-icon="mdi-magnify" single-line hide-details
                        dense style="max-width: 200px"></v-text-field>

                    <!-- 篩選按鈕 -->
                    <v-btn color="primary" @click="filterMode = 'todayCheckIn'">今日入住</v-btn>
                    <v-btn color="primary" @click="filterMode = 'todayCheckOut'">今日退房</v-btn>
                    <v-btn color="secondary" @click="filterMode = 'all'">全部訂單</v-btn>
                    <v-btn color="primary" @click="findAllRooms">查看空房</v-btn>
                </v-toolbar>
            </template>

            <!-- 訂單明細 -->
            <template v-slot:item.details="{ item }">
                <v-btn icon @click="openDetailDialog(item)">
                    明細
                </v-btn>
            </template>

            <!-- 操作按鈕 -->
            <template v-slot:item.actions="{ item }">
                <v-btn icon @click="editBookingOrder(item)">
                    <v-icon color="blue">mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon @click="deleteBookingOrder(item)">
                    <v-icon color="red">mdi-delete</v-icon>
                </v-btn>
            </template>

            <!-- 入住按鈕 -->
            <template v-slot:item.checkin="{ item }">
                <v-btn icon @click="openCheckInDialog(item)"
                    :disabled="item.paymentStatus !== '已付款' || item.status !== '已成立' || !!item.checkInTime">
                    入住
                </v-btn>
            </template>

            <!-- 退房按鈕 -->
            <template v-slot:item.checkout="{ item }">
                <v-btn icon @click="openCheckOutDialog(item)"
                    :disabled="item.paymentStatus !== '已付款' || item.status !== '已成立' || !item.checkInTime || !!item.checkOutTime">
                    退房
                </v-btn>
            </template>

        </v-data-table>

        <!-- 匯出 CSV 按鈕 -->
        <v-btn color="success" class="mt-4" @click="exportToCSV">
            匯出 CSV
        </v-btn>



        <!-- 🔍 明細彈窗 -->
        <v-dialog v-model="detailDialog" max-width="500px">
            <v-card>
                <v-card-title>訂單明細</v-card-title>
                <v-card-text v-if="selectedOrder">
                    <p><strong>入住時間：</strong>{{ formatDateTime(selectedOrder.checkInTime) || '尚未入住' }}</p>
                    <p><strong>退房時間：</strong>{{ formatDateTime(selectedOrder.checkOutTime) || '尚未退房' }}</p>
                    <p><strong>備註：</strong>{{ selectedOrder.note || '無' }}</p>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" @click="detailDialog = false">關閉</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <!-- ✅ 入住確認彈窗 -->
        <v-dialog v-model="checkInDialog" max-width="400px">
            <v-card>
                <v-card-title>入住確認</v-card-title>
                <v-card-text>
                    <div v-if="memberInfo">
                        <p><strong>會員姓名：</strong>{{ memberInfo.name }}</p>
                        <p><strong>身分證字號：</strong>{{ memberInfo.idCard }}</p>
                        <p><strong>Email：</strong>{{ memberInfo.email }}</p>
                        <p><strong>電話：</strong>{{ memberInfo.phone }}</p>
                        <!-- 依你的欄位加更多 -->
                    </div>
                    <div v-else>
                        <p>載入會員資料中或查無此會員。</p>
                    </div>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey" @click="checkInDialog = false">取消</v-btn>
                    <v-btn color="green" @click="confirmCheckIn">確認入住</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <!-- ✅ 退房確認彈窗 -->
        <v-dialog v-model="checkOutDialog" max-width="400px">
            <v-card>
                <v-card-title>退房確認</v-card-title>
                <v-card-text>
                    <div v-if="memberInfo">
                        <p><strong>會員姓名：</strong>{{ memberInfo.name }}</p>
                        <p><strong>身分證字號：</strong>{{ memberInfo.idCard }}</p>
                        <p><strong>Email：</strong>{{ memberInfo.email }}</p>
                        <p><strong>電話：</strong>{{ memberInfo.phone }}</p>
                        <!-- 依你的欄位加更多 -->
                    </div>
                    <div v-else>
                        <p>載入會員資料中或查無此會員。</p>
                    </div>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey" @click="checkOutDialog = false">取消</v-btn>
                    <v-btn color="red" @click="confirmCheckOut">確認退房</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

    </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'


const router = useRouter()
const headers = [
    { title: '訂單編號', value: 'bookingOrderId' },
    { title: '會員編號', value: 'memberId' },
    { title: '入住日', value: 'checkInDate' },
    { title: '退房日', value: 'checkOutDate' },
    { title: '房型名稱', value: 'roomtype.roomtypeName' },
    { title: '售價', value: 'pricePerRoom' },
    { title: '房間數', value: 'quantity' },
    { title: '總金額', value: 'totalPrice' },
    { title: '訂單狀態', value: 'status' },
    { title: '付款狀態', value: 'paymentStatus' },
    { title: '付款方式', value: 'paymentMethod' },
    { title: '訂單明細', value: 'details', sortable: false },
    { title: '操作', value: 'actions', sortable: false },
    { title: '入住', value: 'checkin', sortable: false },
    { title: '退房', value: 'checkout', sortable: false },
]

// 資料
const bookingOrder = ref([])
const search = ref('')
const filterMode = ref('all') // all | todayCheckIn | todayCheckOut

//入住找會員資料
const memberInfo = ref(null)

// 初始化資料
onMounted(fetchBookingOrders)
async function fetchBookingOrders() {
    try {
        const res = await axios.get('http://localhost:8080/bookingOrder/findAll')
        bookingOrder.value = res.data
        console.log(bookingOrder.value);

    } catch (err) {
        console.error('取得訂房訂單資料失敗:', err)
    }
}

// 取得今天日期 yyyy-mm-dd
function getTodayDateStr() {
    const today = new Date()
    return today.toISOString().split('T')[0]
}

// 過濾資料
const filteredOrders = computed(() => {
    const keyword = search.value.toLowerCase()
    const todayStr = getTodayDateStr()

    return bookingOrder.value.filter(order => {
        const matchKeyword = !keyword || Object.values(order).some(val =>
            String(val).toLowerCase().includes(keyword)
        )

        let matchFilter = true
        if (filterMode.value === 'todayCheckIn') {
            matchFilter = order.checkInDate?.startsWith(todayStr)
        } else if (filterMode.value === 'todayCheckOut') {
            matchFilter = order.checkOutDate?.startsWith(todayStr)
        }

        return matchKeyword && matchFilter
    })
})

// 🔍 明細 Dialog
const detailDialog = ref(false)
const selectedOrder = ref(null)
function openDetailDialog(order) {
    selectedOrder.value = order
    detailDialog.value = true
}

// ✅ 入住 Dialog 控制
const checkInDialog = ref(false)
const selectedCheckInOrder = ref(null)

async function openCheckInDialog(order) {
    selectedCheckInOrder.value = order
    checkInDialog.value = true

    try {
        const res = await axios.get(`http://localhost:8080/Member/${order.memberId}`)
        memberInfo.value = res.data
    } catch (err) {
        console.error('取得會員資料失敗:', err)
        memberInfo.value = null
    }
}


// ✅ 退房 Dialog 控制
const checkOutDialog = ref(false)
const selectedCheckOutOrder = ref(null)

async function openCheckOutDialog(order) {
    selectedCheckOutOrder.value = order
    checkOutDialog.value = true

    try {
        const res = await axios.get(`http://localhost:8080/Member/${order.memberId}`)
        memberInfo.value = res.data
    } catch (err) {
        console.error('取得會員資料失敗:', err)
        memberInfo.value = null
    }
}


//入住按鈕呼叫的API
async function confirmCheckIn() {
    if (!selectedCheckInOrder.value) return
    const id = selectedCheckInOrder.value.bookingOrderId

    try {
        await axios.put(`http://localhost:8080/bookingOrder/checkIn/${id}`)
        console.log('入住成功')
        checkInDialog.value = false
        await fetchBookingOrders() // 重新載入資料
    } catch (err) {
        console.error('入住失敗:', err)
    }
}

//退房按鈕呼叫的API
async function confirmCheckOut() {
    if (!selectedCheckOutOrder.value) return
    const id = selectedCheckOutOrder.value.bookingOrderId

    try {
        await axios.put(`http://localhost:8080/bookingOrder/checkOut/${id}`)
        console.log('退房成功')
        checkOutDialog.value = false
        await fetchBookingOrders() // 重新取得最新資料
    } catch (err) {
        console.error('退房失敗:', err)
    }
}


// 操作按鈕
function findAllRooms() {
    router.push({ name: 'findRoom' })
}
function editBookingOrder(order) {
    router.push({ name: 'UpdateBookingOrder', params: { id: order.bookingOrderId } })
}
function deleteBookingOrder(order) {
    console.log('刪除訂單', order)
}
//調整訂單明細的格式
function formatDateTime(datetimeStr) {
    if (!datetimeStr) return ''
    const date = new Date(datetimeStr)
    const yyyy = date.getFullYear()
    const mm = String(date.getMonth() + 1).padStart(2, '0')
    const dd = String(date.getDate()).padStart(2, '0')
    const hh = String(date.getHours()).padStart(2, '0')
    const min = String(date.getMinutes()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd} ${hh}:${min}`
}

//匯出CSV功能
function exportToCSV() {
    const orders = filteredOrders.value
    if (!orders.length) return

    const headers = [
        '訂單編號', '會員編號', '入住日', '退房日', '房型名稱', '售價', '房間數',
        '總金額', '訂單狀態', '付款狀態', '付款方式'
    ]

    const rows = orders.map(order => [
        order.bookingOrderId,
        order.memberId,
        order.checkInDate,
        order.checkOutDate,
        order.roomtype?.roomtypeName || '',
        order.pricePerRoom,
        order.quantity,
        order.totalPrice,
        order.status,
        order.paymentStatus,
        order.paymentMethod
    ])

    let csvContent = '\uFEFF' + headers.join(',') + '\n' + rows.map(row => row.map(String).join(',')).join('\n')

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.setAttribute('href', url)
    link.setAttribute('download', '訂房訂單.csv')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
}


</script>