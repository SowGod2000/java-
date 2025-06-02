<template>
    <v-container>
        <v-card class="pa-5 mx-auto" max-width="600" elevation="8">
            <v-card-title>訂房資訊確認</v-card-title>
            <v-card-text>
                <v-form>
                    <v-text-field v-model="memberName" label="訂購人姓名" readonly></v-text-field>
                    <v-text-field v-model="memberMail" label="e-mail" readonly></v-text-field>
                    <v-text-field v-model="memberIdCard" label="身分證字號" readonly></v-text-field>
                    <v-text-field v-model="roomtypeName" label="房型名稱" readonly></v-text-field>
                    <v-text-field v-model="checkInDate" label="入住日" readonly></v-text-field>
                    <v-text-field v-model="checkOutDate" label="退房日" readonly></v-text-field>
                    <v-text-field v-model="pricePerNight" label="價格" readonly></v-text-field>
                    <v-text-field v-model="quantity" label="房間數" readonly></v-text-field>
                    <v-text-field v-model="totalPrice" label="總金額" readonly></v-text-field>
                </v-form>

                <form ref="ecpayForm" :action="ecpayUrl" method="POST">
                    <input type="hidden" name="MerchantID" v-model="formData.MerchantID" />
                    <input type="hidden" name="MerchantTradeNo" v-model="formData.MerchantTradeNo" />
                    <input type="hidden" name="MerchantTradeDate" v-model="formData.MerchantTradeDate" />
                    <input type="hidden" name="PaymentType" value="aio" />
                    <input type="hidden" name="TotalAmount" v-model="formData.TotalAmount" />
                    <input type="hidden" name="TradeDesc" value="飯店訂房" />
                    <input type="hidden" name="ItemName" value="預定客房" />
                    <input type="hidden" name="ReturnURL"
                        value="https://92a4-111-243-185-49.ngrok-free.app/ecpay/notify" />
                    <input type="hidden" name="ChoosePayment" value="ALL" />
                    <input type="hidden" name="CheckMacValue" v-model="formData.CheckMacValue" />
                    <input type="hidden" name="EncryptType" value="1" />
                    <input type="hidden" name="ClientBackURL" value="http://localhost:5173/rooms">
                    <input type="hidden" name="CustomField1" v-model="formData.CustomField1" />
                </form>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" @click="submitToECPay">確認預定並付款</v-btn>
            </v-card-actions>
        </v-card>
    </v-container>
</template>

<script setup>
import { ref, watchEffect, reactive, nextTick, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import dayjs from 'dayjs'
import axios from 'axios'

const route = useRoute()
const userStore = useUserStore()

const memberId = ref('')

const memberName = ref('')
const memberMail = ref('')
const memberIdCard = ref('')
const roomtypeId = ref(route.query.roomtypeId || '')
const roomtypeName = ref(route.query.roomtypeName || '')
const checkInDate = ref(route.query.checkInDate || '')
const checkOutDate = ref(route.query.checkOutDate || '')
const pricePerNight = ref(Number(route.query.pricePerNight) || 0)
const quantity = ref(Number(route.query.quantity) || 0)
const totalPrice = ref(0)


watchEffect(() => {
    const days = dayjs(checkOutDate.value).diff(dayjs(checkInDate.value), 'day')
    totalPrice.value = days * pricePerNight.value * quantity.value
})

onMounted(() => {
    console.log(userStore.user);
    memberId.value = userStore.user.memberID
    console.log(memberId.value);

    memberName.value = userStore.user.name
    memberMail.value = userStore.user.email
    memberIdCard.value = userStore.user.idCard




})

const ecpayUrl = 'https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'
const merchantTradeNo = 'TEST' + Date.now()
const merchantTradeDate = dayjs().format('YYYY/MM/DD HH:mm:ss')
const checkMacValue = ref('')
const ecpayForm = ref(null)

const formData = reactive({
    MerchantID: '2000132',
    MerchantTradeNo: '',
    MerchantTradeDate: '',
    PaymentType: 'aio',
    TotalAmount: '1000',
    TradeDesc: '飯店訂房',
    ItemName: '預定客房',
    ReturnURL: 'https://92a4-111-243-185-49.ngrok-free.app/ecpay/notify',
    ChoosePayment: 'ALL',
    CheckMacValue: '',
    EncryptType: '1',
    ClientBackURL: 'http://localhost:5173/rooms',
    CustomField1: '',
});



const submitToECPay = async () => {
    // 準備送出到後端的 DTO
    const orderDto = {
        memberId: memberId.value,
        checkInDate: checkInDate.value,
        checkOutDate: checkOutDate.value,
        roomtypeId: roomtypeId.value,
        pricePerRoom: pricePerNight.value,
        quantity: quantity.value,
        totalPrice: totalPrice.value
    };

    // 先呼叫後端建立訂單
    const orderRes = await axios.post('http://localhost:8080/bookingOrder/insert', orderDto);
    console.log("訂單建立成功", orderRes.data);
    console.log("訂單編號", orderRes.data.bookingOrderId);

    // 準備要送ecpay的參數
    formData.TotalAmount = totalPrice.value;
    formData.MerchantTradeNo = merchantTradeNo;
    formData.MerchantTradeDate = merchantTradeDate;
    formData.CustomField1 = orderRes.data.bookingOrderId;

    const { CheckMacValue, ...restParams } = formData;
    const res = await axios.post('http://localhost:8080/ecpay/getCheckMacValue', restParams);
    console.log(res);

    if (res.data) {
        formData.CheckMacValue = res.data;
        console.log("checkMacValue: " + res.data);
        // 等待 DOM 更新完成後再 submit
        await nextTick()
        ecpayForm.value.submit()
    } else {
        console.log("checkMacValue is null");
    }
}
</script>