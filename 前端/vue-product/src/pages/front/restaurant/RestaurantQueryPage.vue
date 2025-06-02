<template>
  <div class="page-background">
  <v-container class="py-10 mt-navbar">
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card class="pa-6" elevation="4" v-if="!showResult">
          <v-card-title class="text-h5 font-weight-bold mb-6 text-center">
            查詢我的訂位
          </v-card-title>

          <v-form v-if="!userStore.user" @submit.prevent="searchReservation">
            <v-text-field
              v-model="phone"
              label="電話"
              required
              outlined
              dense
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              required
              outlined
              dense
              class="mb-4"
            ></v-text-field>
<!-- variant="text" color="black" -->
            <v-btn type="submit" color="error" block size="large">
              查詢訂位
            </v-btn>

            <v-alert type="warning" v-if="noResult" class="mt-4 text-center" border="start" color="warning" variant="tonal">
              查無訂位紀錄，請確認電話與 Email 是否正確。
            </v-alert>
          </v-form>
        </v-card>

        <!-- 查詢結果 -->
        <v-card v-else class="pa-6" elevation="4">
          <v-card-title class="text-h5 font-weight-bold mb-6 text-center">
            訂位結果
          </v-card-title>

          <v-btn v-if="!userStore.user" color="error" @click="resetSearch" class="mb-4" block size="large">
            重新查詢
          </v-btn>

          <div class="table-wrapper">
            <v-data-table
              :headers="headers"
              :items="reservations"
              class="custom-table"
              density="comfortable"
            >
              <template v-slot:item.tables="{ item }">
                {{ item.tables?.map(t => t.tableCode).join(', ') || '-' }}
              </template>
              <template v-slot:item.restaurantTimeslot.startTime="{ item }">
                {{ item.restaurantTimeslot?.startTime || '-' }}
              </template>
            </v-data-table>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
  
</div>
 
</template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  //會員資料
  import { useUserStore } from '@/stores/userStore'
 
  import axios from 'axios'
  //會員資料
 const userStore = useUserStore()

  const phone = ref('')
  const email = ref('')
  const reservations = ref([])
  const noResult = ref(false)
  const showResult = ref(false)
  
  const headers = [
    { title: '訂位編號', value: 'orderId' },
    { title: '顧客名稱', value: 'customerName' },
    { title: '電話', value: 'phone' },
    { title: 'Email', value: 'mail' },
    { title: '訂位日期', value: 'orderdate' },
    { title: '時段', value: 'restaurantTimeslot.startTime' },
    { title: '大人人數', value: 'adult' },
    { title: '小孩人數', value: 'child' },
    { title: '座位', value: 'tables' },
    { title: '預估金額', value: 'expectedPrice' },
    { title: '訂金', value: 'deposit' },
    { title: '備註', value: 'note' },
    { title: '訂位狀態', value: 'orderStatus' },
  ]

  const route = useRoute()

  onMounted(async () => {
  // 初始化 Pinia 會員資料
  await userStore.initUserFromApi()

  // 從網址 query 帶入
  const phoneParam = route.query.phone
  const emailParam = route.query.email

  if (phoneParam && emailParam) {
    phone.value = phoneParam
    email.value = emailParam
    searchReservation(true)
    return
  }

  // 如果已登入，從會員資料查
  if (userStore.user?.phone && userStore.user?.email) {
    phone.value = userStore.user.phone
    email.value = userStore.user.email
    searchReservation(true)
  } else {
    console.warn('尚未登入，無法查詢訂位')
  }
})
  
async function searchReservation(isAuto = false) {
  if (!phone.value || !email.value) {
    if (!isAuto) {
      alert('請輸入電話和Email')
    }
    noResult.value = true
    return
  }
  try {
    const res = await axios.get('http://localhost:8080/firstHotel/restaurant/searchOrder', {
      params: {
        phone: phone.value,
        email: email.value
      }
    })
    reservations.value = res.data
    if (reservations.value.length === 0) {
      noResult.value = true
    } else {
      noResult.value = false
      showResult.value = true
    }
  } catch (error) {
    console.error('查詢失敗', error)
    if (!isAuto) {
      alert('查詢失敗，請確認電話和Email是否正確！')
    }
    noResult.value = true
  }
}
  
  function resetSearch() {
    phone.value = ''
    email.value = ''
    reservations.value = []
    noResult.value = false
    showResult.value = false
  }
  </script>
  
  <style scoped>

  /* ① 背景層 */
  .page-background {
  /* 這裡放你的背景圖 */
  background-image: url('https://images.unsplash.com/photo-1579027989536-b7b1f875659b?q=80&w=2940&auto=format&fit=crop&ixlib=rb-4.0.3');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  /* 確保它能撐高整個視窗 */
  min-height: 100vh;

  /* 如果有固定 navbar，需要往下推一點 */
  padding-top: 80px; /* 跟 .mt-navbar 一致 */
}

/* ② 卡片玻璃化（半透明＋模糊）*/
:deep(.v-card) {
  background-color: rgba(255,255,255,0.6);  /* 半透明白 */
  backdrop-filter: blur(12px);              /* 背後小模糊 */
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.2);
  max-width: 480px; /* 卡片不會太寬 */
  margin: auto;     /* 左右置中 */
}

/* ③ 讓卡片裡的內容也透一點 */
:deep(.v-main),
:deep(.v-container),
:deep(.v-row),
:deep(.v-col) {
  background-color: transparent;
}


  .mt-navbar {
  margin-top: 80px; /* 可以自己調，例如70px或90px，看你的Navbar高度 */
}
/*  讓整個頁面字體更乾淨統一 */
:deep(.v-data-table) {
  font-size: 16px;
}

/* 表頭字體加粗、置中、不換行 */
:deep(.v-data-table-header th) {
  font-size: 16px;
  font-weight: bold;
  white-space: nowrap;
  text-align: center;
  background-color: #f5f5f5;
}

/* 表格內文字置中，不換行 */
:deep(.v-data-table td) {
  text-align: center;
  white-space: nowrap;
  padding: 12px 8px;
}

/* 表格包裹器，加上滾動條 */
.table-wrapper {
  overflow-x: auto;
  max-width: 100%;
  margin: auto;
}

/*  查詢按鈕美化 */
.v-btn {
  letter-spacing: 1px;
  font-weight: bold;
}

/*  卡片之間間距 */
.v-card {
  border-radius: 16px;
}

/* 警告訊息美化 */
:deep(.v-alert) {
  font-size: 16px;
}

/* 響應式調整 (小裝置也美觀) */
@media (max-width: 600px) {
  .v-card {
    padding: 20px !important;
  }
}
</style>