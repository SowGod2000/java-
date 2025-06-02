<template>
    <v-container class="d-flex flex-column align-center justify-center" style="height: 100vh;">
      <v-card width="400" class="pa-6">
        <v-card-title>OTP 驗證</v-card-title>
        <v-text-field v-model="otp" label="輸入 6 碼 OTP" prepend-icon="mdi-shield-key-outline" />
        <v-btn  :loading="isLoading" color="success" class="mt-4" @click="verifyOtp" block>驗證</v-btn>
        <v-alert v-if="message" type="info" class="mt-4">{{ message }}</v-alert>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const otp = ref('')
const message = ref('')
const email = route.query.email

const verifyOtp = async () => {

  if(!/^\d{6}$/.test(otp.value))
  {
    message.value='請輸入6位數字OTP'
    return
  }

  try {
    const res = await fetch('http://localhost:8080/Member/verify-otp', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        Email: email,
        otp: otp.value,
      }),
      credentials: 'include' 
    })

    if (!res.ok) {
      const errorText = await res.text()
      message.value = errorText || 'OTP 驗證失敗'
      return
    }

    const userData = await res.json()

      
// ✅ 把會員資料存進 Pinia

    window.location.href="/";
      


  } catch (err) {
    console.error(err)
    message.value = '驗證過程出錯，請稍後再試'
  }
}
</script>

  