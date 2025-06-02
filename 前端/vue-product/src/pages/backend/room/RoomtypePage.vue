<template>
    <v-container>
        <v-data-table :headers="headers" :items="roomtypes" :items-per-page="5" class="elevation-1">

            <!-- 上方工具列 -->
            <template v-slot:top>
                <v-toolbar flat>
                    <v-toolbar-title>房型管理</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" @click="addRoomtype">新增房型</v-btn>
                </v-toolbar>
            </template>

            <!-- 圖片欄位顯示 -->
            <template v-slot:item.roomImage="{ item }">
                <v-img :src="'data:image/jpeg;base64,' + item.roomImage" max-height="80" max-width="120" cover></v-img>
            </template>

            <!-- 操作按鈕 -->
            <template v-slot:item.actions="{ item }">
                <v-btn icon @click="editRoomtype(item)">
                    <v-icon color="blue">mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon @click="deleteRoomtype(item)">
                    <v-icon color="red">mdi-delete</v-icon>
                </v-btn>
            </template>
        </v-data-table>
    </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const headers = [
    { title: '房型編號', value: 'roomtypeId' },
    { title: '房型名稱', value: 'roomtypeName' },
    { title: '房型圖片', value: 'roomImage' }, // 顯示圖片
    { title: '描述', value: 'description' },
    { title: '定價/晚', value: 'pricePerNight' },
    { title: '房間大小', value: 'roomSize' },
    { title: '總房間數', value: 'totalRooms' },
    { title: '操作', value: 'actions', sortable: false },
];

const roomtypes = ref([])

onMounted(async () => {
    try {
        const res = await axios.get('http://localhost:8080/roomtype/findAll')
        roomtypes.value = res.data
    } catch (err) {
        console.error('取得房型資料失敗:', err)
    }
})

function addRoomtype() {
    router.push({ name: 'InsertRoomtype' })
}

function editRoomtype(item) {
    router.push({ name: 'UpdateRoomtype', params: { id: item.roomtypeId } })
}

async function deleteRoomtype(item) {
    const confirmed = confirm(`確定要刪除 ${item.roomtypeName} 嗎？`)
    if (confirmed) {
        try {
            await axios.put(`http://localhost:8080/roomtype/delete/${item.roomtypeId}`)
        } catch (error) {
            console.log(`刪除 ${item.roomtypeId} 失敗`);
        }
        roomtypes.value = roomtypes.value.filter(r => r !== item)
    }
}
</script>