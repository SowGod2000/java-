<template>
  <div class="forgot-password-container">
    <el-card>
      <h2 class="title">忘記密碼</h2>

      <el-alert
        v-if="successMessage"
        type="success"
        :closable="false"
        class="mb-3"
        show-icon
        :title="successMessage"
      />
      <el-alert
        v-if="errorMessage"
        type="error"
        :closable="false"
        class="mb-3"
        show-icon
        :title="errorMessage"
      />

      <el-form @submit.prevent="submitEmail">
        <el-form-item label="請輸入註冊時的 Email：" required>
          <el-input
            v-model="email"
            type="email"
            placeholder="your@email.com"
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitEmail">寄送重設連結</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const email = ref('')
const successMessage = ref('')
const errorMessage = ref('')

const submitEmail = async () => {
  successMessage.value = ''
  errorMessage.value = ''

  try {
    const response = await fetch('http://localhost:8080/Member/forgot-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email: email.value })
    })

    if (!response.ok) {
      const errorText = await response.text()
      errorMessage.value = errorText || '發送失敗，請稍後再試'
      return
    }

    const successText = await response.text()
    successMessage.value = successText
  } catch (err) {
    errorMessage.value = '無法與伺服器連線，請稍後再試'
  }
}
</script>

<style scoped>
.forgot-password-container {
  max-width: 500px;
  margin: 80px auto;
}

.title {
  text-align: center;
  margin-bottom: 20px;
}

.mb-3 {
  margin-bottom: 16px;
}
</style>
