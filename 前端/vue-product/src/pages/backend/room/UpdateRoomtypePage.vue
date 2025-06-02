<template>
    <v-container>
        <v-card class="pa-5" elevation="2">
            <v-card-title>編輯房型</v-card-title>

            <v-form @submit.prevent="updateRoomtype">
                <v-text-field v-model="roomtype.roomtypeName" label="房型名稱" required />
                <v-text-field v-model.number="roomtype.pricePerNight" label="定價 / 晚" type="number" />

                <v-file-input label="上傳房型圖片" accept="image/*" v-model="file" prepend-icon="mdi-camera" />

                <v-textarea v-model="roomtype.description" label="描述" />
                <v-text-field v-model.number="roomtype.roomSize" label="房間大小" type="number" />
                <v-text-field v-model.number="roomtype.totalRooms" label="總房間數" type="number" />

                <v-btn type="submit" color="primary" class="mt-4">儲存變更</v-btn>
                <v-btn @click="goBack" class="mt-4 ml-2">取消</v-btn>
            </v-form>
        </v-card>
    </v-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const roomtypeId = route.params.id
const file = ref(null)

const roomtype = ref({
    roomtypeName: '',
    pricePerNight: 0,
    roomImage: '', // base64 字串
    description: '',
    roomSize: 0,
    totalRooms: 0,
    isDeleted: 0,
})

watch(file, (newFile) => {
    if (newFile) {
        const reader = new FileReader()
        reader.onload = () => {
            const base64 = reader.result.split(',')[1]
            roomtype.value.roomImage = base64
            console.log('轉換後的 base64：', roomtype.value.roomImage.substring(0, 30) + '...') // 印前30字
        }
        reader.readAsDataURL(newFile)
    }
})

onMounted(async () => {
    try {
        const res = await axios.get(`http://localhost:8080/roomtype/${roomtypeId}`)
        roomtype.value = res.data
    } catch (err) {
        console.error('載入房型資料失敗', err)
    }
})

function handleImageUpload(fileInput) {
    let file = null

    if (Array.isArray(fileInput)) {
        file = fileInput[0]
    } else if (fileInput instanceof File) {
        file = fileInput
    }

    if (file) {
        const reader = new FileReader()
        reader.onload = () => {
            const base64 = reader.result.split(',')[1]
            roomtype.value.roomImage = base64
        }
        reader.readAsDataURL(file)
    }
}

async function updateRoomtype() {
    console.log("傳送資料：", roomtype.value)
    try {
        await axios.put(`http://localhost:8080/roomtype/update/${roomtypeId}`, roomtype.value)
        alert('更新成功')
        router.push('/backend/roomtype')
    } catch (err) {
        console.error('更新失敗', err)
        alert('更新失敗')
    }
}

function goBack() {
    router.push('/backend/roomtype')
}
</script>