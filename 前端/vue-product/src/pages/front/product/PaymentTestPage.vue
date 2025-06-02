<template>
  <v-container class="fill-height d-flex justify-center align-center">
    <v-card class="pa-6 text-center" elevation="3" max-width="500">
      <v-progress-circular indeterminate color="primary" size="60" class="mb-4" />
      <h2 class="text-h6 mb-2"> 導向綠界付款中...</h2>
      <p>請稍候，我們正在為您準備付款頁面</p>

      <!-- 錯誤顯示 -->
      <v-alert v-if="error" type="error" class="mt-4" border="start">
        ❌ 發生錯誤：{{ error }}
      </v-alert>

      <!-- 綠界表單 -->
      <form ref="ecpayForm" :action="ecpayUrl" method="POST" style="display: none">
        <input
          v-for="(value, key) in paymentData"
          :key="key"
          :name="key"
          :value="value"
          type="hidden"
        />
      </form>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const orderId = route.query.orderId
const ecpayForm = ref(null)
const ecpayUrl = 'https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'
const paymentData = ref({})
const error = ref(null)

const submitToEcpay = async () => {
  try {
    const res = await fetch(`http://localhost:8080/api/ecpay/payment-info?orderId=${orderId}`)
    if (!res.ok) throw new Error(await res.text())
    paymentData.value = await res.json()
    await nextTick()
    ecpayForm.value.submit()
  } catch (err) {
    error.value = err.message
  }
}

onMounted(submitToEcpay)
</script>