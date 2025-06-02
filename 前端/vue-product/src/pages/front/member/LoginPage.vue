<template>
  <div class="container py-5" style="max-width: 400px; margin: auto;">
    <h2 class="mb-4 text-center">登入</h2>
    <el-form :model="form" label-width="60px">
      <el-form-item label="帳號">
        <el-input v-model="form.email" placeholder="輸入 Email" />
      </el-form-item>
      <el-form-item label="密碼">
        <el-input v-model="form.password" type="password" placeholder="輸入密碼" show-password />
      </el-form-item>


      <el-form-item>
        <el-button type="primary" @click="handleLogin()" style="width: 100%">登入
        </el-button>
      </el-form-item>

      <el-form-item v-if="errorMessage">
        <p class="error-message">{{ errorMessage }}</p>
      </el-form-item>

      <el-form-item v-if="!showOtp">
        <router-link to="/register">註冊會員</router-link>
        <span> | </span>
        <router-link to="/forgotpassword">忘記密碼</router-link>
      </el-form-item>

      <el-form-item v-if="!showOtp">
        <el-button @click="handleGoogleLogin" type="danger" plain style="width: 100%">
          使用 Google 登入
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = ref({
  email: '',
  password: '',
  otp: ''
})
const showOtp = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!form.value.email || !form.value.password) {
    ElMessage.error('請輸入帳號與密碼')
    return
  }

  try {
    const res = await fetch('http://localhost:8080/Member/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        Email: form.value.email, // 注意大小寫要跟後端對齊
        password: form.value.password,
      }),
    })

    const text = await res.text()
    if (res.ok) {
      ElMessage.success('OTP 已發送至手機！請輸入驗證碼')
      showOtp.value = true
      router.push({ path: '/Verifyotp', query: { email: form.value.email } })
    } else {
      errorMessage.value = text || '帳號密碼錯誤'
    }
  } catch (err) {
    console.error(err)
    errorMessage.value = '登入時發生錯誤，請稍後再試'
  }
}



const handleGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}
</script>

<style scoped>
.error-message {
  color: red;
  font-size: 0.9rem;
  text-align: center;
  width: 100%;
}
</style>
