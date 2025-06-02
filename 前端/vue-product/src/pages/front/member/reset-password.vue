<template>
  <el-card class="reset-password-container">
    <template #header>
      <h2>重設密碼</h2>
    </template>

    <el-alert
      v-if="successMessage"
      :title="successMessage"
      type="success"
      show-icon
      class="mb-3"
    />
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      show-icon
      class="mb-3"
    />

    <el-form @submit.prevent="submitReset">
      <el-form-item label="新密碼" required>
        <el-input
          v-model="newPassword"
          type="password"
          placeholder="請輸入新密碼"
          show-password
        />
        <small class="hint" v-if="newPassword && newPassword.length < 6">
          密碼至少需 6 個字元
        </small>
      </el-form-item>

      <el-form-item label="確認密碼" required>
        <el-input
          v-model="confirmPassword"
          type="password"
          placeholder="再次輸入新密碼"
          show-password
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitReset">送出</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import router from '@/router'

export default {
  name: 'ResetPassword',
  data() {
    return {
      token: '',
      newPassword: '',
      confirmPassword: '',
      successMessage: '',
      errorMessage: ''
    }
  },
  mounted() {
    this.token = this.$route.query.token
    if (!this.token) {
      this.errorMessage = '無效的重設連結，請從電子郵件重新點擊。'
    }
  },
  methods: {
    async submitReset() {
      if (!this.newPassword || this.newPassword.length < 6) {
        this.errorMessage = '密碼至少需 6 個字元'
        this.successMessage = ''
        return
      }

      if (this.newPassword !== this.confirmPassword) {
        this.errorMessage = '兩次輸入的密碼不一致'
        this.successMessage = ''
        return
      }

      try {
        const response = await fetch('http://localhost:8080/Member/reset-password', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            token: this.token,
            newPassword: this.newPassword
          })
        })

        const responseText = await response.text()

        if (response.ok) {
          this.successMessage = responseText || '密碼已成功重設，將自動跳轉至登入頁...'
          this.errorMessage = ''
          setTimeout(() => {
            router.push('/login')
          }, 3000)
        } else {
          this.errorMessage = responseText || '重設密碼失敗'
          this.successMessage = ''
        }
      } catch (err) {
        this.errorMessage = '發生錯誤，請稍後再試'
        this.successMessage = ''
      }
    }
  }
}
</script>

<style scoped>
.reset-password-container {
  max-width: 500px;
  margin: 80px auto;
  padding: 20px;
}
.mb-3 {
  margin-bottom: 20px;
}
.hint {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}
</style>
