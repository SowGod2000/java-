<template>
  <div style="background-color: #f5f5f5; min-height: 100vh;">
    <!-- 上方查詢區 -->
    <div style="background-color: white; box-shadow: 0px 10px 8px rgba(0, 0, 0, 0.4);">
      <v-container class="py-6" style="max-width: 980px;" fluid>
        <v-sheet class="pa-4" color="white" elevation="0" rounded style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
          <v-row align="center" justify="space-between" no-gutters>
            <v-col class="pr-2">
              <v-text-field v-model="checkInDate" label="入住日" type="date" :min="minDate" dense outlined></v-text-field>
            </v-col>
            <v-col class="px-2">
              <v-text-field v-model="checkOutDate" label="退房日" type="date" :min="minCheckOutDate" dense
                outlined></v-text-field>

            </v-col>
            <v-col cols="auto" class="pl-2">
              <v-btn color="black" class="white--text" height="36" style="min-width: 100px; font-size: 16px;"
                @click="searchRooms">
                查詢空房
              </v-btn>
            </v-col>
          </v-row>
        </v-sheet>
      </v-container>
    </div>

    <!-- 下方查詢結果區 -->
    <v-container class="mt-6" style="max-width: 950px; background-color: white;">
      <v-row justify="center">
        <v-col v-for="room in roomList" :key="room.roomtypeId" cols="12" style="max-width: 900px;">
          <v-card class="room-card d-flex flex-column flex-md-row mb-6" outlined elevation="10" rounded>


            <!-- 房型圖片 -->
            <div
              style="width: 400px; height: 250px; overflow: hidden; border-top-left-radius: 8px; border-bottom-left-radius: 8px;">
              <img :src="'data:image/jpeg;base64,' + room.roomImage"
                style="width: 100%; height: 100%; object-fit: cover; display: block; padding-bottom: 15px;"
                alt="房型圖片" />
            </div>

            <!-- 房型內容 -->
            <v-card-text class="flex-grow-1 pa-4" style="padding-bottom: 80px;">
              <h3 style="font-size: 36px;position: absolute; top: 10px;left: 425px;" class="font-weight-bold mb-2">{{
                room.roomtypeName }}</h3>
              <hr style="position: relative; top: 50px;">
              <div style="font-size: 26px;position: absolute; top: 110px;left: 425px;" class="mb-4 font-weight-bold">
                價格：{{
                  room.pricePerNight }} 元 / 晚
              </div>
              <div style="position: absolute; bottom: 5px; right: 250px; width: 200px;">
                <v-select v-model="room.selectedQuantity"
                  :items="Array.from({ length: room.availableRooms + 1 }, (_, i) => i)" label="預訂房間數" dense outlined
                  style="max-width: 200px;"></v-select>
              </div>
              <!-- 房型資訊按鈕 -->
              <div style="position: absolute; top: 15px; right: 30px;">
                <v-btn style="font-size: 14px;" @click="showRoomInfo(room)">
                  房型資訊
                </v-btn>
              </div>

            </v-card-text>
            <!-- 立即預訂按鈕 -->
            <div style="position: absolute; bottom: 30px; right: 30px;">
              <v-btn color="black" style="font-size: 16px;"
                @click="goToBooking(room.roomtypeId, room.roomtypeName, room.pricePerNight, room.selectedQuantity)">
                立即預訂
              </v-btn>
            </div>
          </v-card>
        </v-col>
      </v-row>

      <div v-if="roomList.length === 0" class="text-center my-5 text-grey">
        查無可用房型
      </div>
      <!-- 房型資訊的 modal -->
      <v-dialog v-model="infoDialog" max-width="500">
        <v-card>
          <v-card-title class="headline">房型資訊</v-card-title>
          <v-card-text>
            <div><strong>房型名稱：</strong>{{ selectedRoom?.roomtypeName }}</div>
            <div><strong>房型描述：</strong>{{ selectedRoom?.description }}</div>
            <div><strong>房型大小：</strong>{{ selectedRoom?.roomSize }} 平方公尺</div>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn text color="primary" @click="infoDialog = false">關閉</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- 查詢中顯示動畫 -->
      <v-row justify="center" v-if="isLoading">
        <v-col cols="12" class="text-center py-10">
          <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
          <div class="mt-4 text-subtitle-1">查詢中，請稍候...</div>
        </v-col>
      </v-row>

      <!-- 查詢結果 -->
      <v-row justify="center" v-else>
        <v-col v-for="room in roomList" :key="room.roomtypeId" cols="12" style="max-width: 900px;">
          <!-- room-card 原本內容 -->
        </v-col>
      </v-row>


    </v-container>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const checkInDate = ref('')
const checkOutDate = ref('')
const roomList = ref([])

// 限制選擇日期不能小於今天
const minDate = ref('')
const minCheckOutDate = ref('')

// 房型資訊 modal 控制
const infoDialog = ref(false)
const selectedRoom = ref(null)

// 查詢動畫相關
const isLoading = ref(false)

const formatDate = (date) => date.toISOString().split('T')[0]

const showRoomInfo = (room) => {
  selectedRoom.value = room
  infoDialog.value = true
}

onMounted(() => {
  const today = new Date()
  const tomorrow = new Date()
  tomorrow.setDate(today.getDate() + 1)

  checkInDate.value = formatDate(today)
  checkOutDate.value = formatDate(tomorrow)
  minDate.value = formatDate(today)
  minCheckOutDate.value = formatDate(tomorrow)

  searchRooms()
})

watch(checkInDate, (newCheckIn) => {
  const checkIn = new Date(newCheckIn)
  const minCheckout = new Date(checkIn)
  minCheckout.setDate(checkIn.getDate() + 1)
  minCheckOutDate.value = formatDate(minCheckout)

  const checkOut = new Date(checkOutDate.value)
  if (!checkOut || checkOut <= checkIn) {
    checkOutDate.value = formatDate(minCheckout)
  }
})

watch(checkOutDate, (newCheckOut) => {
  const checkOut = new Date(newCheckOut)
  const checkIn = new Date(checkInDate.value)

  if (!checkIn || checkOut <= checkIn) {
    const newCheckIn = new Date(checkOut)
    newCheckIn.setDate(checkOut.getDate() - 1)
    checkInDate.value = formatDate(newCheckIn)
  }
})

const searchRooms = async () => {
  try {
    isLoading.value = true
    roomList.value = []

    await new Promise(resolve => setTimeout(resolve, 600))

    const res = await axios.get(`http://localhost:8080/findRoom/${checkInDate.value}/${checkOutDate.value}`)
    roomList.value = res.data.map(room => ({
      ...room,
      selectedQuantity: 0
    }))
  } catch (err) {
    console.error('查詢失敗', err)
  } finally {
    isLoading.value = false
  }
}

// ✅ 修改這裡：導向 `/rooms/booking`
const goToBooking = (roomtypeId, roomtypeName, pricePerNight, quantity) => {

  // 沒登入，導向登入頁
  if (!userStore.user) {
    router.push('/login')
    return
  }

  router.push({
    path: '/rooms/booking',
    query: {
      checkInDate: checkInDate.value,
      checkOutDate: checkOutDate.value,
      roomtypeId,
      roomtypeName,
      pricePerNight,
      quantity,
    }
  })
}
</script>


<style scoped>
.text-grey {
  color: #888;
}

.text-h5 {
  font-size: 24px;
}

.text-subtitle-1 {
  font-size: 18px;
}

.room-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.room-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

/* 查詢動畫相關 */
.room-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease, opacity 0.3s ease;
  opacity: 1;
}

.room-card-enter-active,
.room-card-leave-active {
  transition: opacity 0.3s ease;
}

.room-card-enter-from,
.room-card-leave-to {
  opacity: 0;
}
</style>
