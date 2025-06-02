<template>
  <div class="page-background">
  <v-container class="py-10 form-container">
    <v-row justify="center">
      <v-col cols="12" md="8" lg="6">
        <v-card class="pa-6" elevation="5" rounded="xl">
          <v-card-title class="text-h4 text-center font-weight-bold mb-6">
            餐廳訂位
          </v-card-title>

          <v-form ref="formRef" @submit.prevent="submitForm">
            <v-text-field
              v-model="form.name"
              label="顧客姓名"
              required
              variant="outlined"
              class="mb-4"
              :rules="[requiredRule]"
            />
            <v-text-field
              v-model="form.phone"
              label="電話"
              type="tel"
              required
              variant="outlined"
              class="mb-4"
              :rules="[requiredRule, phoneRule]"
            />
            <v-text-field
              v-model="form.mail"
              label="E-mail"
              type="email"
              required
              variant="outlined"
              class="mb-4"
              :rules="[requiredRule, emailRule]"
            />
            <v-text-field
              v-model="form.adult"
              label="成人人數"
              type="number"
              min="0"
              required
              variant="outlined"
              class="mb-4"
              :rules="[requiredRule]"
            />
            <v-text-field
              v-model="form.child"
              label="兒童人數"
              type="number"
              min="0"
              required
              variant="outlined"
              class="mb-4"
            />
            <v-text-field
              v-model="form.orderdate"
              label="用餐日期"
              type="date"
              required
              variant="outlined"
              class="mb-4"
              :rules="[requiredRule]"
            />
            <v-select
              v-model="form.slotId"
              :items="timeslots"
              item-title="label"
              item-value="id"
              label="用餐時段"
              variant="outlined"
              class="mb-4"
              :rules="[requiredRule]"
              required
            />
            
            <div class="d-flex align-center mb-3">
              <v-icon class="me-2">mdi-chair-school</v-icon> 座位表：
              <v-btn color="error" class="ml-2" @click="dialog = true">選擇座位</v-btn>
              <span v-if="selectedSeatCodes">（已選：{{ selectedSeatCodes }}）</span>
            </div>
            <div v-if="errorMessage" class="error-text mb-4">
              {{ errorMessage }}
            </div>

            <v-textarea
              v-model="form.note"
              label="備註"
              rows="3"
              variant="outlined"
              class="mb-4"
            />

            <v-btn
              type="submit"
              variant="text" color="black"
              block
              size="large"
              class="mt-6"
              :loading="loading"
              :disabled="loading"
            >
              送出訂單
            </v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>

    <!-- Dialog  -->
    <v-dialog v-model="dialog" max-width="800px">
  <v-card class="rounded-xl">
    <v-card-title class="text-h5 font-weight-bold text-center">
      選擇座位
    </v-card-title>

    <v-card-text>
      <v-container>
        <v-row>
          <v-col
            v-for="(seat, index) in seats"
            :key="seat.id"
            cols="6"
            md="5"
            class="d-flex justify-center"
          >
            <v-card
              class="seat-card text-center"
              :class="{
                'seat-selected': seat.selected,
                'seat-disabled': seat.disabled
              }"
              @click="!seat.disabled && toggleSeat(index)"
              elevation="2"
            >
              <v-card-text class="pa-4">
                <v-icon size="32" class="mb-2">mdi-chair-school</v-icon>
                <div>{{ seat.code }}</div>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>

    <v-card-actions class="justify-end">
      <v-btn text @click="dialog = false">取消</v-btn>
      <v-btn color="primary" @click="dialog = false">確定</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

  </v-container>
</div>

<!-- 畫面全遮的 loading -->
<v-overlay
  :model-value="loading"
  class="align-center justify-center"
  persistent
  opacity="0.6"
>
  <v-progress-circular
    indeterminate
    size="64"
    color="primary"
  ></v-progress-circular>
</v-overlay>

</template>

<script setup>
import { ref, reactive, computed,watch,onMounted  } from 'vue';
import { useUserStore } from '@/stores/userStore'

// 會員資料
const userStore = useUserStore()

const dialog = ref(false);
const errorMessage = ref('');
const loading = ref(false);

const timeslots = [
  { id: 1, label: '11:00' },
  { id: 2, label: '12:00' },
  { id: 3, label: '17:00' },
  { id: 4, label: '18:00' },
  { id: 5, label: '19:00' }
]
//email格式
const emailRule = v => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return regex.test(v) || '請輸入正確的 Email'
}

//把每個欄位加上rule
const requiredRule = v => !!v || '此欄位為必填'

//手機號碼限制
const phoneRule = v => /^09\d{8}$/.test(v) || '請輸入正確的手機號碼（09XX-XXX-XXX）';

const form = reactive({
  name: '',
  phone: '',
  mail: '',
  orderdate: '',
  slotId: '',
  adult: 1,
  child: 0,
  note: ''
})

//顯示選擇的座位號
const selectedSeatCodes = computed(() =>
  seats.filter(seat => seat.selected).map(seat => seat.code).join(', ')
)



//計算座位人數
const maxSeatsAllowed = computed(() => {
  const totalPeople = Number(form.adult) + Number(form.child)
  return Math.ceil(totalPeople / 4)
})

const seats = reactive([
  { id: 1, code: 'A1', selected: false, capacity: 4 ,disabled: false},
  { id: 2, code: 'A2', selected: false, capacity: 4 ,disabled: false},
  { id: 3, code: 'A3', selected: false, capacity: 4 ,disabled: false},
  { id: 4, code: 'A4', selected: false, capacity: 4 ,disabled: false},
  { id: 5, code: 'A5', selected: false, capacity: 4 ,disabled: false},
  { id: 6, code: 'A6', selected: false, capacity: 4 ,disabled: false},
  { id: 7, code: 'A7', selected: false, capacity: 4 ,disabled: false},
  { id: 8, code: 'A8', selected: false, capacity: 4 ,disabled: false},
  { id: 9, code: 'A9', selected: false, capacity: 4 ,disabled: false},
  { id: 10, code: 'A10', selected: false, capacity: 4,disabled: false }
])
//座位選擇
const toggleSeat = (index) => {
  const seat = seats[index];

  // 禁用的座位不能選
  if (seat.disabled) return;

  // 當 maxSeatsAllowed == 1，只能選一個
  if (maxSeatsAllowed.value === 1) {
    // 如果點自己就取消
    if (seat.selected) {
      seat.selected = false;
    } else {
      // 否則取消其他的，只選這個
      seats.forEach(s => s.selected = false);
      seat.selected = true;
    }
    return;
  }

  // 當 maxSeatsAllowed > 1，可以選多個座位
  const selectedCount = seats.filter(s => s.selected).length;

  if (!seat.selected && selectedCount >= maxSeatsAllowed.value) {
    // 如果還沒選，但已經到達上限，不給選
    return;
  }

  // 否則就切換選取狀態
  seat.selected = !seat.selected;
};
//座位是否已經被選取
watch(() => [form.orderdate, form.slotId], async ([orderdate, slotId]) => {
  if (!orderdate || !slotId) return

  try {
    const res = await fetch(`http://localhost:8080/firstHotel/restaurant/findreservedseat?orderdate=${orderdate}&slotId=${slotId}`)
    const disabledIds = await res.json()

    seats.forEach(seat => {
      if (disabledIds.includes(seat.id)) {
        seat.disabled = true
        seat.selected = false
      } else {
        seat.disabled = false
      }
    })
  } catch (err) {
    console.error('載入座位失敗', err)
  }
})

watch(
  () => seats.filter(seat => seat.selected).length,
  (selectedCount) => {
    const totalPeople = Number(form.adult) + Number(form.child);
    const neededSeats = Math.ceil(totalPeople / 4);

    if (selectedCount >= neededSeats) {
      // 如果選夠了，就清空錯誤訊息
      errorMessage.value = '';
    }
  }
);



// 清空表單欄位
const resetForm = () => {
  form.name = '';
  form.phone = '';
  form.mail = '';
  form.orderdate = '';
  form.slotId = '';
  form.adult = 1;
  form.child = 0;
  form.note = '';

  // 清空座位選擇
  seats.forEach(seat => {
    seat.selected = false;
    seat.disabled = false; // 如果想重刷，這個也可以一起清除
  });

  // 清空錯誤訊息
  errorMessage.value = '';

  formRef.value.reset();
  formRef.value.resetValidation();
};

//送出表單
const formRef = ref(null)

const submitForm = async () => {
  const selectedSeats = seats.filter(seat => seat.selected)
const selectedTableIds = selectedSeats.map(seat => seat.id)
const totalPeople = Number(form.adult) + Number(form.child)
const totalCapacity = selectedSeats.reduce((sum, seat) => sum + seat.capacity, 0)
loading.value = true; // 送出開始，打開 loading


// 座位數量檢查
if (selectedSeats.length < Math.ceil(totalPeople / 4)) {
    errorMessage.value = `你需要選擇至少 ${Math.ceil(totalPeople / 4)} 張桌子，目前只選了 ${selectedSeats.length} 張！`;
    loading.value = false; // 送出結束，關閉 loading
    return;
  }


const valid = await formRef.value.validate()

if (!valid || !form.slotId || selectedTableIds.length === 0) {
  errorMessage.value = '請完整填寫所有欄位，並選擇至少一張座位！';
  loading.value = false; // 送出結束，關閉 loading
  return;
}

if (totalCapacity < totalPeople) {
  errorMessage.value = `所選座位總共只能容納 ${totalCapacity} 人，請至少選擇 ${Math.ceil(totalPeople / 4)} 張桌子！`;
  loading.value = false;// 送出結束，關閉 loading
  return;
}

// 黑名單檢查
try {
    const checkRes = await fetch(`http://localhost:8080/firstHotel/restaurant/catchBlacklist?phone=${form.phone}`);
    const checkData = await checkRes.json();
    console.log('黑名單查詢結果', checkData);

    if (checkData.blacklistStatus === 1) {
      alert('您多次預訂未到，無法預約，請聯繫客服。');
      loading.value = false;
      return;
    }
  } catch (err) {
    alert('檢查黑名單時發生錯誤，請稍後再試。');
    loading.value = false;
    return;
  }

  const data = {
    customerName: form.name,
    phone: form.phone,
    mail: form.mail,
    orderdate: form.orderdate,
    adult: Number(form.adult),
    child: Number(form.child),
    note: form.note,
    slotId: Number(form.slotId),
    tableIds: selectedTableIds.map(id => Number(id))
  }
  console.log('送出的 data:', data);
  try {
    const res = await fetch('http://localhost:8080/firstHotel/restaurant/reserve', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })

    if (!res.ok) throw new Error('送出失敗')
    const order = await res.json()
    alert('訂單送出成功，準備跳轉至付款頁面！')

    // ➤ 組成綠界參數
    const ecpayParams = {
      MerchantID: "2000132",
      MerchantTradeNo: order.resMerchantTradeNo,
      MerchantTradeDate:formatDateForECPay(new Date()),
      PaymentType: "aio",
      TotalAmount: Number(order.expectedPrice)*0.1,
      TradeDesc: "訂金付款",
      ItemName: "餐廳訂金",
      ReturnURL: "https://92a4-111-243-185-49.ngrok-free.app/RestaurantEcpay/notify",
      ChoosePayment: "ALL",
      ClientBackURL: `http://localhost:5173/MemberCenter?tab=restaurantOrders&phone=${order.phone}&email=${order.mail}`,
      EncryptType: 1
    }
    console.log("MerchantTradeDate:", formatDateForECPay(new Date()));
    //  拿 CheckMacValue
    const checkMacRes = await fetch("http://localhost:8080/RestaurantEcpay/getCheckMacValue", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(ecpayParams)
    });
    const checkMacValue = await checkMacRes.text();
    ecpayParams.CheckMacValue = checkMacValue

    // ➤ 自動送到綠界
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

    Object.entries(ecpayParams).forEach(([key, value]) => {
      const input = document.createElement("input");
      input.type = "hidden";
      input.name = key;
      input.value = value;
      form.appendChild(input);
    });
   
    document.body.appendChild(form);
    form.submit();

  } catch (err) {
    alert('送出失敗：' + err.message)
  } finally {
    loading.value = false
  }
}
function formatDateForECPay(date) {
  const pad = (n) => n.toString().padStart(2, '0');
  return (
    date.getFullYear() + '/' +
    pad(date.getMonth() + 1) + '/' +
    pad(date.getDate()) + ' ' +
    pad(date.getHours()) + ':' +
    pad(date.getMinutes()) + ':' +
    pad(date.getSeconds())
  );
}

onMounted(async () => {
  await userStore.initUserFromApi()

  if (userStore.user) {
    form.name = userStore.user.name || ''
    form.phone = userStore.user.phone || ''
    form.mail = userStore.user.email || ''
  }
})


</script>

<style scoped>

.page-background {
  /* 這裡放你的背景圖 */
  background-image: url('https://images.unsplash.com/photo-1565650834520-0b48a5c83f43?q=80&w=2911&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  /* 確保它能撐高整個視窗 */
  min-height: 100vh;

  /* 如果有固定 navbar，需要往下推一點 */
  padding-top: 80px; /* 跟 .mt-navbar 一致 */
}

/* .form-container {
  background-color: #fafafa; 淡淡背景，看起來高級
  min-height: 100vh;
  margin-top: 80px;
} */
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
.error-text {
  color: red;
  font-size: 14px;
  text-align: center;
}
.v-card-title {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

/* Dialog */
/* 座位卡片統一樣式 */
.seat-card {
  width: 50%;
  min-height: 120px;
  background-color: #f5f5f5;
  border-radius: 12px;
  transition: background-color 0.3s, transform 0.3s;
  cursor: pointer;
}

/* 滑過去 hover 效果 */
.seat-card:hover {
  background-color: #e0e0e0;
  transform: scale(1.03);
}

/* 被選中的座位 */
.seat-selected {
  background-color: #1976d2 !important; /* primary色 */
  color: white;
}

/* 已禁用（被別人訂走）的座位 */
.seat-disabled {
  background-color: #ccc !important;
  color: #777;
  cursor: not-allowed;
  pointer-events: none;
}
</style>