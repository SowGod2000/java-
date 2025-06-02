<template >
    <v-container>
      <!-- ✅ 加上 overflow-x，避免破版 -->
      <div style="overflow-x: auto;">
        
        <v-data-table
          :headers="headers"
          :items="filteredOrders"
          :items-per-page="10"
          class="elevation-1 text-caption">
        <template v-slot:item.tables="{ item }">
  {{ item.tables?.map(t => t.tableCode).join(', ') || '' }}
</template>
        
          <!-- 工具列 -->
          <template v-slot:top>
            <v-toolbar flat>
              <v-toolbar-title>餐廳訂位管理</v-toolbar-title>
              <v-spacer></v-spacer>
  
  
              <!-- 預約訂按鈕 -->
              <v-btn color="primary" @click="openNewOrderDialog">預約訂位</v-btn>
              <!-- 黑名單查詢 -->
              <v-btn color="primary" @click="onBlacklistButtonClick">黑名單查詢</v-btn>
               <!-- 取消訂位查詢 -->
               <v-btn color="primary" @click="onCancelOrderButtonClick">取消訂位查詢</v-btn>
               <!-- 匯出訂單csv -->
               <v-btn color="primary" @click="downloadCsv">
  匯出訂單<v-icon>mdi-download</v-icon>
</v-btn>
               
              <!-- 搜尋欄 -->
              <v-text-field
                v-model="search"
                label="搜尋訂單"
                prepend-icon="mdi-magnify"
                single-line
                hide-details
                dense
                style="max-width: 200px"
              ></v-text-field>
            </v-toolbar>
          </template>
  
          <!-- 操作按鈕 -->
          <template v-slot:item.actions="{ item }">
            <div class="d-flex align-center">
            <v-btn icon @click="openeditRestaurantOrderDialog(item)">
              <v-icon color="blue">mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon @click=" opencancelRestaurantOrder(item)">
              <v-icon color="red">mdi-delete</v-icon>
            </v-btn>
            </div>
          </template>
  
          <!-- 報到按鈕 -->
          <template v-slot:item.checkin="{ item }">
            <div class="d-flex align-center">
            <v-btn icon @click="openCheckInDialog(item)">報到</v-btn>
            
          <!-- </template> -->
  
          <!-- 未到按鈕 -->
          <!-- <template v-slot:item.checkout="{ item }"> -->
            <v-btn icon @click="openabsentDialog(item)">未到</v-btn>
            </div>
          </template>

        </v-data-table>
        <!-- 黑名單 -->
        <div v-if="showBlacklist">
  <v-data-table
    :headers="blacklistHeaders"
    :items="blacklistData"
    :items-per-page="10"
    class="elevation-1 text-caption"
  >
    <template v-slot:item.bannedTime="{ item }">
      <span>{{ new Date(item.bannedTime).toLocaleString() }}</span>
    </template>

    <template v-slot:item.blackliststatus="{ item }">
      <span>{{ item.blackliststatus === 1 ? '停權' : '解除黑名單' }}</span>
    </template>
    
     <!-- 解除黑名單按鈕 -->
     <template v-slot:item.actions="{ item }">
        <v-btn
          small
          color="error"
          @click="unban(item)"
        >
          解除
        </v-btn>
      </template>

  </v-data-table>
</div><!-- 黑名單 -->

<!-- 查詢取消訂單 -->
<div v-if="showCancelOrders">
  <v-data-table
    :headers="cancelOrderHeaders"
    :items="cancelOrders"
    :items-per-page="10"
    class="elevation-1 text-caption"
  >
    <!-- 你可以加一些欄位顯示，比如座位、時間等等 -->
    <template v-slot:item.orderdate="{ item }">
      <span>{{ new Date(item.orderdate).toLocaleDateString() }}</span>
    </template>
    <template v-slot:item.orderStatus="{ item }">
      <span>{{ item.orderStatus }}</span>
    </template>
  </v-data-table>
</div>
<!-- 查詢取消訂單 -->
      </div>
  
      <!-- 報到確認Dialog -->
      <v-dialog v-model="checkInDialog" max-width="400px">
        <v-card>
          <v-card-title>報到確認</v-card-title>
          <v-card-text>確定要報到了嗎？</v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey" @click="checkInDialog = false">取消</v-btn>
            <v-btn color="green" @click="restaurantCheckin(selectedOrder)">確認</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <!-- 訂位未到Dialog -->
      <v-dialog v-model="absentDialog" max-width="400px">
        <v-card>
          <v-card-title>訂位未到</v-card-title>
          <v-card-text>確定客人未到嗎？</v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey" @click="absentDialog = false">取消</v-btn>
            <v-btn color="red" @click="restaurantAbsent(selectedOrder)">確認</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    
    <!-- 修改Dialog -->
      <v-dialog v-model="editRestaurantOrderDialog" max-width="600px">
  <v-card>
    <v-card-title>{{ formMode === 'add' ? '預約訂位' : '修改訂位' }}</v-card-title>

    <v-card-text>
      <v-form ref="formRef" @submit.prevent="editRestaurantOrderForm">
        <v-row dense>
          <v-col cols="12" md="6">
            <!-- 顧客姓名 -->
            <v-text-field
              v-model="form.name"
              label="顧客姓名"
              variant="outlined"
              :rules="[requiredRule]"
              required
            />

            <!-- 電話 -->
            <v-text-field
              v-model="form.phone"
              label="電話"
              variant="outlined"
              type="tel"
            />

            <!-- Email -->
            <v-text-field
              v-model="form.mail"
              label="Email"
              variant="outlined"
              type="email"
            />

            <!-- 用餐日期 -->
            <v-text-field
              v-model="form.orderdate"
              label="用餐日期"
              variant="outlined"
              type="date"
              :rules="[requiredRule]"
              required
            />

            <!-- 用餐時段 -->
            <v-select
              v-model="form.slotId"
              :items="timeslots"
              item-title="label"
              item-value="id"
              label="用餐時段"
              variant="outlined"
              :rules="[requiredRule]"
              required
            />

            <!-- 成人人數 -->
            <v-text-field
              v-model="form.adult"
              label="成人人數"
              type="number"
              min="0"
              variant="outlined"
              :rules="[requiredRule]"
              required
            />

            <!-- 小孩人數 -->
            <v-text-field
              v-model="form.child"
              label="小孩人數"
              type="number"
              min="0"
              variant="outlined"
            />

            <!-- 選擇座位 -->
            <div class="d-flex align-center my-2">
              <v-icon class="me-2">mdi-chair-school</v-icon> 座位：
              <v-btn color="primary" size="small" class="ms-2" @click="openSeatDialog ">選擇座位</v-btn>
              <span v-if="selectedSeatCodes" class="ms-2">（{{ selectedSeatCodes }}）</span>
            </div>

            <!-- 備註 -->
            <v-textarea
              v-model="form.note"
              label="備註"
              variant="outlined"
              rows="3"
            />
          </v-col>
        </v-row>

        <!-- 錯誤訊息 -->
        <div v-if="errorMessage" style="color: red; margin-top: 10px;">
          {{ errorMessage }}
        </div>
      </v-form>
    </v-card-text>

    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="grey" @click="editRestaurantOrderDialog = false">取消</v-btn>
      <v-btn color="red" @click="submitForm">確認</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

      <!-- 刪除dialog -->
    <v-dialog v-model="cancelRestaurantDialog" max-width="400px">
        <v-card>
          <v-card-title>取消訂位</v-card-title>
          <v-card-text>確定要取消訂位嗎？
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey" @click="cancelRestaurantDialog= false">取消</v-btn>
            <v-btn color="red" @click="cancelRestaurantOrder(selectedOrder)">確認</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- 選位 Dialog -->
    <v-dialog v-model="seatdialog" max-width="800">
      <v-card>
        <v-card-title>座位表</v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col
               v-for="(seat, index) in seatsTemp"
                :key="seat.id"
                cols="4" md="6"
              >
              <v-card
  class="text-center"
  :color="seat.disabled
    ? 'grey-lighten-1'
    : seat.selected ? 'primary' : 'grey-lighten-3'"
  :class="{ 'text-white': seat.selected }"
  @click="!seat.disabled && toggleSeat(index)"
>
                <v-card-text>
                  {{ seat.code }}
                  <v-icon>mdi-chair-school</v-icon>
                </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="seatdialog = false">關閉</v-btn>
          <v-btn color="primary" @click="confirmSeats">確定</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


    
    </v-container>

  
    

  </template>
  
  <script setup>
  import { ref, computed, onMounted,watch,reactive } from 'vue'
  import axios from 'axios'


  
  // 資料區
  const OrderData = ref([])
  const search = ref('')
  
  
  const errorMessage = ref('');
  
  // Dialog控制
  const checkInDialog = ref(false)
  const absentDialog = ref(false)
  const editRestaurantOrderDialog = ref(false)
  const cancelRestaurantDialog = ref(false)
  const seatdialog = ref(false)
  
//黑名單用
//放黑名單資料用
const blacklistData = ref([]);
const showBlacklist = ref(false) // 預設關閉


 
  //切換訂位還有新增訂位時dialog標題
  const formMode = ref('add')  // 'add' = 新增，'edit' = 修改
  const dialogMode = ref('edit') // 'edit' 編輯模式，或 'new' 新增模式
//checkin、absent 帶入資料
const selectedOrder = ref(null)
//編輯帶資料用
const form = ref({
  orderId: null,
  name: '',
  phone: '',
  mail: '',
  orderdate: '',
  slotId: '',
  adult: 0,
  child: 0,
  note: '',
  tables: [] // 選座位用的
})
// 新增一個 seatsTemp
const seatsTemp = ref([])

const timeslots = [
  { id: 1, label: '11:00' },
  { id: 2, label: '12:00' },
  { id: 3, label: '17:00' },
  { id: 4, label: '18:00' },
  { id: 5, label: '19:00' }
]

const requiredRule = value => {
  return !!value || '此欄位為必填';
}
//座位表編號
const selectedSeatCodes = ref('');
  // 一開始載入資料
  onMounted(async () => {
  await fetchOrders();  // 這是你定義的抓訂單資料的 function
});
async function fetchOrders() {
  try {
    const res = await axios.get('http://localhost:8080/firstHotel/restaurantManage/RestaurantOrders');
    OrderData.value = res.data;
  } catch (error) {
    console.error('取得訂位資料失敗', error);
  }
}
  
  
  //取消訂單
  async function cancelRestaurantOrder(order) {
    console.log(order);
    try {
    const res = await axios.post('http://localhost:8080/firstHotel/restaurantManage/cancelReserve', order)
    
    console.log('取消成功', res.data)
    cancelRestaurantDialog.value = false
    await fetchOrders()
    // 可以做一些通知、重新抓訂單資料
  } catch (error) {
    console.error('取消失敗', error)
  }
}

//checkin
async function restaurantCheckin(order) {
  console.log(order);
  
    try {
    const res = await axios.post('http://localhost:8080/firstHotel/restaurantManage/checkin', order)
    
    
    console.log('報到成功', res.data);
    checkInDialog.value = false;
    await fetchOrders()
    // 可以做一些通知、重新抓訂單資料
  } catch (error) {
    console.error('報到失敗', error)
  }
}

//absent
  async function restaurantAbsent(order){
try{
    const res = await axios.post('http://localhost:8080/firstHotel/restaurantManage/absent',order)
console.log('更新未到成功',res.data);
absentDialog.value = false;
await fetchOrders()
}catch (error){
    console.error('更新未到失敗',error);
    
}

  }
//修改的表單
  async function editRestaurantOrderForm() {
  try {
    console.log('送出表單資料', form.value);

      // 整理送出的格式
      const dataToSend = {
        orderId: form.value.orderId, 
      customerName: form.value.name,
      phone: form.value.phone,
      mail: form.value.mail,
      orderdate: form.value.orderdate,
      slotId: form.value.slotId,
      adult: form.value.adult,
      child: form.value.child,
      note: form.value.note,
      tableIds: form.value.tables.map(t => t.tableId) // 抽出 tableId 陣列
    };
    // 假設後端API是這個，自己改成你正確的URL
    const res = await axios.put('http://localhost:8080/firstHotel/restaurantManage/updateReserve', dataToSend);

    // 這裡因為你現在只是測試Dialog，所以先不要送到後端
    // 直接關掉Dialog看效果
    editRestaurantOrderDialog.value = false;

    // 成功後重新抓資料
    await fetchOrders();

    alert('修改成功！');
  } catch (error) {
    console.error('送出表單失敗', error);
    alert('送出失敗');
  }
}

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


  
  // 搜尋過濾功能
  const filteredOrders = computed(() => {
    const keyword = search.value.toLowerCase()
    return OrderData.value.filter(order =>
      Object.values(order).some(val =>
        String(val).toLowerCase().includes(keyword)
      )
    )
  })
  //座位表dialog
  function confirmSeats() {
  // 將 seatsTemp 的選取狀態同步到 seats
  seats.forEach((seat, index) => {
    seat.selected = seatsTemp.value[index].selected;
  });

  form.value.tables = seats.filter(seat => seat.selected).map(seat => ({
    tableId: seat.id,
    tableCode: seat.code
  }));

  selectedSeatCodes.value = form.value.tables.map(seat => seat.tableCode).join(', ');

  seatdialog.value = false;
}
//讓座位表可以選位子
function toggleSeat(index) {
  const seat = seatsTemp.value[index]

  // 禁用座位不能選
  if (seat.disabled) return;
;
  const totalPeople = Number(form.value.adult) + Number(form.value.child);
  const neededSeats = Math.ceil(totalPeople / 4); // 幾桌

  // 如果要取消已選取的座位，直接允許
  if (seat.selected) {
    seat.selected = false;
    return;
  }

  // 如果選擇數量已經達到需要的桌數，不能再選
  const selectedCount = seatsTemp.value.filter(s => s.selected).length;
  if (selectedCount >= neededSeats) {
    // 選夠了就不給再選
    return;
  }

  // 否則選取這張桌子
  seat.selected = true;
}
async function openSeatDialog() {
  try {
    // 先確認有日期、時段
    if (!form.value.orderdate || !form.value.slotId) {
      alert('請先選擇日期和時段');
      return;
    }

    // 先抓目前被預約的座位
    const res = await fetch(`http://localhost:8080/firstHotel/restaurant/findreservedseat?orderdate=${form.value.orderdate}&slotId=${form.value.slotId}`)
    const disabledIds = await res.json()

    // 重置 temp 座位表，並更新禁用狀態
    seatsTemp.value = seats.map(seat => ({
      ...seat,
      disabled: disabledIds.includes(seat.id),
      selected: false // 先清掉，等等再補回自己的
    }));

    // 再補回「自己訂的座位」
    form.value.tables.forEach(table => {
      const tempSeat = seatsTemp.value.find(seat => seat.id === table.tableId);
      if (tempSeat) {
        tempSeat.selected = true;
        tempSeat.disabled = false; //自己的座位要允許選，不然會被disabled掉
      }
    });

    seatdialog.value = true; // 最後開Dialog
  } catch (err) {
    console.error('載入座位失敗', err);
    alert('載入座位失敗');
  }
}

//座位
const seats = reactive([
  { id: 1, code: 'A1', selected: false, capacity: 4, disabled: false },
  { id: 2, code: 'A2', selected: false, capacity: 4, disabled: false },
  { id: 3, code: 'A3', selected: false, capacity: 4, disabled: false },
  { id: 4, code: 'A4', selected: false, capacity: 4, disabled: false },
  { id: 5, code: 'A5', selected: false, capacity: 4, disabled: false },
  { id: 6, code: 'A6', selected: false, capacity: 4, disabled: false },
  { id: 7, code: 'A7', selected: false, capacity: 4, disabled: false },
  { id: 8, code: 'A8', selected: false, capacity: 4, disabled: false },
  { id: 9, code: 'A9', selected: false, capacity: 4, disabled: false },
  { id: 10, code: 'A10', selected: false, capacity: 4, disabled: false }
])

// 開新增座位Dialog


// 送出新增訂單
async function reserveRestaurantOrderForm() {
  try {
    const dataToSend = {
      customerName: form.value.name,
      phone: form.value.phone,
      mail: form.value.mail,
      orderdate: form.value.orderdate,
      slotId: form.value.slotId,
      adult: form.value.adult,
      child: form.value.child,
      note: form.value.note,
      tableIds: form.value.tables.map(t => t.tableId)
    };

    const res = await axios.post('http://localhost:8080/firstHotel/restaurantManage/reserve', dataToSend);

    editRestaurantOrderDialog.value = false;
    await fetchOrders();
    alert('新增預約成功！');
  } catch (error) {
    console.error('新增預約失敗', error);
    alert('新增失敗');
  }
}
  

//新增
function resetForm() {
  form.value = {
    orderId: null,
    name: '',
    phone: '',
    mail: '',
    orderdate: '',
    slotId: '',
    adult: 1,
    child: 0,
    note: '',
    tables: []
  };
  selectedSeatCodes.value = '';
}

async function submitForm() {
  if (dialogMode.value === 'edit') {
    await editRestaurantOrderForm();  // 修改
  } else if (dialogMode.value === 'new') {
    await reserveRestaurantOrderForm();  // 新增
  }
}
//黑名單
const blacklistHeaders = [
  { title: '黑名單ID', value: 'blacklistId' },
  { title: '電話', value: 'phone' },
  { title: '封鎖原因', value: 'reason' },
  { title: '封鎖時間', value: 'bannedTime' },
  { title: '狀態', value: 'blackliststatus' },
  {title:'操作',value: 'actions'},
];

async function fetchBlacklists() {
  try {
    const res = await axios.get('http://localhost:8080/firstHotel/restaurantManage/findblacklist');
    blacklistData.value = res.data;
    console.log('黑名單資料:', blacklistData.value);
  } catch (error) {
    console.error('取得黑名單失敗', error);
    alert('黑名單資料取得失敗');
  }
}
// 假設 blacklistData 是一個陣列，你想在 methods 裡處理按下按鈕的事
async function unban(item) {
  try {
    const res = await axios.post('http://localhost:8080/firstHotel/restaurantManage/unbanBlacklist', item);
    console.log('解除黑名單成功', res.data);
    alert('已成功解除黑名單');

    // 更新黑名單列表
    await fetchBlacklists();
  } catch (error) {
    console.error('解除黑名單失敗', error);
    alert('解除失敗');
  }
}

//控制黑名單打開消失
async function onBlacklistButtonClick() {
  showBlacklist.value = !showBlacklist.value;
  if (showBlacklist.value) {
    await fetchBlacklists();  // 如果是打開才去抓資料
  }
}

//查詢取消訂位
// 放取消訂單資料用
const cancelOrders = ref([]);

// 控制取消訂位表格的開關
const showCancelOrders = ref(false);

// 取消訂位查詢的動作
async function onCancelOrderButtonClick() {
  showCancelOrders.value = !showCancelOrders.value;
  if (showCancelOrders.value) {
    try {
      const res = await axios.get('http://localhost:8080/firstHotel/restaurantManage/findorderstatus');
      cancelOrders.value = res.data;
      console.log('取消訂單資料:', cancelOrders.value);
    } catch (error) {
      console.error('取得取消訂單失敗', error);
      alert('取得取消訂單資料失敗');
    }
  }
}
const cancelOrderHeaders = [
  { title: '訂位編號', value: 'orderId' },
  { title: '顧客名稱', value: 'customerName' },
  { title: '電話', value: 'phone' },
  { title: '訂位日期', value: 'orderdate' },
  { title: '狀態', value: 'orderStatus' },
];

//匯出CSV
async function downloadCsv() {
  try {
    const res = await fetch('http://localhost:8080/firstHotel/restaurantManage/exportOrderCsv');

    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    
    const a = document.createElement('a');
    a.href = url;
    a.download = '訂位訂單.csv';
    a.click();

    window.URL.revokeObjectURL(url); // 下載後清除
  } catch (error) {
    console.error('下載CSV失敗', error);
    alert('匯出失敗');
  }
}

  
  //編輯
  function openeditRestaurantOrderDialog(item) {
  console.log('編輯', item);
  dialogMode.value = 'edit'  
  formMode.value = 'edit'; // 切成修改模式
  editRestaurantOrderDialog.value = true;
  form.value.orderId = item.orderId;
  form.value.name = item.customerName;
  form.value.phone = item.phone;
  form.value.mail = item.mail;
  form.value.orderdate = item.orderdate;
  form.value.slotId = item.restaurantTimeslot?.slotId;
  form.value.adult = item.adult;
  form.value.child = item.child;
  form.value.note = item.note;
  form.value.tables = item.tables || [];

  //  把座位選取狀態回填！
  seats.forEach(seat => {
  seat.selected = form.value.tables.some(t => t.tableId === seat.id);
});
selectedSeatCodes.value = form.value.tables.map(t => t.tableCode).join(', ');
}
  function opencancelRestaurantOrder(item) {
    console.log('刪除', item)
    selectedOrder.value = item 
    cancelRestaurantDialog.value = true
  }

  function openabsentDialog(item) {
    console.log('確認未到')
    selectedOrder.value = item 
    absentDialog.value = true
  }
  function openCheckInDialog(item) {
    console.log('確認報到')
    selectedOrder.value = item  // 記住是哪筆
    checkInDialog.value = true  // 打開Dialog
  }
  function openNewOrderDialog() {
  resetForm();
  formMode.value = 'add'; // 切成新增模式
  dialogMode.value = 'new';
  editRestaurantOrderDialog.value = true;
}
//   function confirmCheckOut() {
//     console.log('確認取消')
//     selectedOrder.value = item 
//     checkOutDialog.value = false
//   }
  
  // 表格欄位
  const headers = [
    { title: '訂位編號', value: 'orderId' },
    { title: '顧客名稱', value: 'customerName' },
    { title: '電話', value: 'phone' },
    { title: '信箱', value: 'mail' },
    { title: '訂位日期', value: 'orderdate' },
    {title:'預約時段', value:'restaurantTimeslot.startTime'},
    { title: '大人人數', value: 'adult' },
    { title: '小孩人數', value: 'child' },
    {title:'座位',value:'tables'},
    { title: '預估金額', value: 'expectedPrice' },
    { title: '訂金', value: 'deposit' },
    {title:'備註',value:'note'},
    { title: '訂位狀態', value: 'orderStatus' },
    
    { title: '操作', value: 'actions', sortable: false },
    { title: '報到', value: 'checkin', sortable: false },
    // { title: '未到', value: 'checkout', sortable: false }
  ]



  </script>