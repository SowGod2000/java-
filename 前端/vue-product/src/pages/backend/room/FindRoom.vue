<template>
    <v-container>
        <v-card class="pa-5 mx-auto" max-width="600" elevation="2">
            <v-card-title>查詢可用房型</v-card-title>
            <v-card-text>
                <v-row>
                    <v-col cols="12" sm="6">
                        <v-text-field v-model="checkInDate" label="入住日" type="date" @change="focusCheckOutDate" />
                    </v-col>
                    <v-col cols="12" sm="6">
                        <v-text-field v-model="checkOutDate" label="退房日" type="date" ref="checkOutDateInput" />
                    </v-col>
                </v-row>
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" @click="searchRooms">查詢空房</v-btn>
            </v-card-actions>
        </v-card>
    </v-container>

    <v-container>
        <v-row>
            <v-col cols="12" sm="6" md="4" v-for="room in visibleRoomList" :key="room.roomtypeId">
                <v-card class="ma-3" elevation="3">
                    <v-card-title>{{ room.roomtypeName }}</v-card-title>
                    <v-card-text>
                        <div>房型介紹：{{ room.description }}</div>
                        <div>定價：{{ room.pricePerNight }} 元/晚</div>
                        <div>總房間數：{{ room.totalRooms }}</div>
                        <div>剩餘房間：{{ room.availableRooms }}</div>

                        <v-select :items="getRoomOptions(room.availableRooms)"
                            v-model="selectedQuantities[room.roomtypeId]" label="請選擇房間數" dense></v-select>

                        <v-btn class="mt-2" color="black" dark @click="reserveRoom(room)">
                            預定
                        </v-btn>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

//路由用
const router = useRouter()

// 日期與房型資料
const checkInDate = ref('')
const checkOutDate = ref('')
const checkOutDateInput = ref(null)
const roomList = ref([])
const selectedQuantities = ref({})

// 預設日期：今天和明天
onMounted(() => {
    const today = new Date()
    const tomorrow = new Date()
    tomorrow.setDate(today.getDate() + 1)
    checkInDate.value = formatDate(today)
    checkOutDate.value = formatDate(tomorrow)
})

// 日期格式 yyyy-MM-dd
function formatDate(date) {
    const yyyy = date.getFullYear()
    const mm = String(date.getMonth() + 1).padStart(2, '0')
    const dd = String(date.getDate()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd}`
}

// 入住日更新時自動修正退房日
function focusCheckOutDate() {
    const checkIn = new Date(checkInDate.value)
    const checkOut = new Date(checkOutDate.value)
    if (checkOut <= checkIn) {
        const newCheckOut = new Date(checkIn)
        newCheckOut.setDate(checkIn.getDate() + 1)
        checkOutDate.value = formatDate(newCheckOut)
    }
    setTimeout(() => {
        checkOutDateInput.value?.focus()
    }, 100)
}

// 查詢房型
async function searchRooms() {
    try {
        const res = await axios.get(
            `http://localhost:8080/findRoom/${checkInDate.value}/${checkOutDate.value}`
        )
        roomList.value = res.data
        // 初始化選擇房數
        selectedQuantities.value = {}
        res.data.forEach((room) => {
            selectedQuantities.value[room.roomtypeId] = 0
        })
        console.log('取得房型資料：', roomList.value)
    } catch (err) {
        console.error('查詢失敗：', err)
    }
}

// 計算選項 0~max
function getRoomOptions(max) {
    return Array.from({ length: max + 1 }, (_, i) => i)
}

// 過濾剩餘房間 > 0
const visibleRoomList = computed(() =>
    roomList.value.filter((room) => room.availableRooms > 0)
)

// 點擊預定
function reserveRoom(room) {
    const quantity = selectedQuantities.value[room.roomtypeId] || 0
    if (quantity === 0) {
        alert('請至少選擇一間')
        return
    }
    router.push({
        path: '/backend/booking',
        query: {
            roomtypeId: room.roomtypeId,
            checkInDate: checkInDate.value,
            checkOutDate: checkOutDate.value,
            quantity: quantity
        }
    })
}

</script>