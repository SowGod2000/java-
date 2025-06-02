<template>
    <v-container>
        <v-card class="pa-5 mx-auto" max-width="600" elevation="2">
            <v-card-title>訂房資訊確認</v-card-title>
            <v-card-text>
                <v-form>
                    <!-- 訂購人資料 -->
                    <v-text-field v-model="orderName" label="訂購人姓名" required></v-text-field>

                    <v-text-field v-model="memberId" label="會員編號" required></v-text-field>

                    <!-- 顯示查詢參數（只讀但使用 v-model） -->
                    <v-text-field v-model="roomtypeId" label="房型編號" readonly></v-text-field>

                    <v-text-field v-model="checkInDate" label="入住日" readonly></v-text-field>

                    <v-text-field v-model="checkOutDate" label="退房日" readonly></v-text-field>

                    <v-text-field v-model="quantity" label="房間數" readonly></v-text-field>
                </v-form>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" @click="submitBooking">確認預定</v-btn>
            </v-card-actions>
        </v-card>
    </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 將 query 參數包進 ref
const roomtypeId = ref(route.query.roomtypeId || '')
const checkInDate = ref(route.query.checkInDate || '')
const checkOutDate = ref(route.query.checkOutDate || '')
const quantity = ref(route.query.quantity || '')

const orderName = ref('')
const memberId = ref('')

// 假送出方法
const submitBooking = () => {
    console.log('訂購人姓名:', orderName.value)
    console.log('會員編號:', memberId.value)
    console.log('房型編號:', roomtypeId.value)
    console.log('入住日:', checkInDate.value)
    console.log('退房日:', checkOutDate.value)
    console.log('房間數:', quantity.value)
    // 可改為發送 API 請求
}
</script>