<template>
    <v-container>
        <v-card class="pa-5" elevation="2">
            <v-card-title>新增房型</v-card-title>

            <v-form ref="form" @submit.prevent="submitForm" v-model="valid" lazy-validation>
                <!-- 房型名稱 -->
                <v-text-field v-model="roomtype.roomtypeName" label="房型名稱" :rules="[rules.required]"
                    required></v-text-field>

                <!-- 定價 -->
                <v-text-field v-model.number="roomtype.pricePerNight" label="定價 / 晚" type="number"
                    :rules="[rules.required, rules.positive]" required></v-text-field>

                <!-- 上傳圖片 -->
                <v-file-input v-model="roomtype.roomImage" label="上傳房型圖片" accept="image/*"
                    prepend-icon="mdi-camera"></v-file-input>

                <!-- 描述 -->
                <v-textarea v-model="roomtype.description" label="描述"></v-textarea>

                <!-- 房型大小 -->
                <v-text-field v-model.number="roomtype.roomSize" label="房型大小" type="number"
                    :rules="[rules.required, rules.positive]" required></v-text-field>

                <!-- 總房間數 -->
                <v-text-field v-model.number="roomtype.totalRooms" label="總房間數" type="number"
                    :rules="[rules.required, rules.positive]" required></v-text-field>

                <!-- 送出按鈕 -->
                <v-btn color="primary" type="submit" class="mt-4">送出</v-btn>
            </v-form>
        </v-card>
    </v-container>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const valid = ref(false)
const form = ref(null)

const roomtype = ref({
    roomtypeName: '',
    pricePerNight: null,
    roomImage: null, // 原始檔案
    description: '',
    roomSize: null,
    totalRooms: null,
})

const rules = {
    required: (value) => !!value || '此欄位為必填',
    positive: (value) => value > 0 || '必須大於 0',
}

const submitForm = () => {
    if (form.value.validate()) {
        if (roomtype.value.roomImage) {
            const file = roomtype.value.roomImage
            const reader = new FileReader()
            reader.onload = async () => {
                const base64Image = reader.result // 包含 data:image/jpeg;base64,...

                const payload = {
                    ...roomtype.value,
                    roomImage: base64Image,
                }

                await sendToServer(payload)
            }
            reader.readAsDataURL(file)
        } else {
            const payload = {
                ...roomtype.value,
                roomImage: null,
            }

            sendToServer(payload)
        }
    }
}

const sendToServer = async (payload) => {
    try {
        await axios.post('http://localhost:8080/roomtype/insert', payload)
        alert('新增成功')
        router.push('/backend/roomtype')
    } catch (error) {
        console.error('新增失敗:', error)
        alert('新增失敗，請稍後再試')
    }
}
</script>