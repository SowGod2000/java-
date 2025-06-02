<template>
    <div class="edit-password-container">
      <h2 class="title">編輯密碼</h2>
  
      <el-form :model="form" @submit.prevent="submit" class="form">
        <el-form-item>
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="舊密碼"
            prefix-icon="el-icon-lock"
            show-password
          />
        </el-form-item>
  
        <el-form-item>
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="新密碼（至少8個字元）"
            prefix-icon="el-icon-lock"
            show-password
          />
        </el-form-item>
  
        <el-form-item>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="確認新密碼"
            prefix-icon="el-icon-lock"
            show-password
          />
        </el-form-item>
  
        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="submit">
            <i class="el-icon-upload"></i> 更新
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </template>
  
  <script setup>
import { reactive } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'
import router from '@/router'

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const memberStore = useUserStore()

const submit = async () => {
  if (form.newPassword !== form.confirmPassword) {
    ElMessage.error('新密碼與確認密碼不一致')
    return
  }

  try {
    // 1. 驗證舊密碼
    const res1 = await fetch('http://localhost:8080/Member/checkPassword', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ oldPassword: form.oldPassword })
    })

    const text1 = await res1.text()
    if (!res1.ok) {
      ElMessage.error(text1)
      return
    }

    // 2. 更新密碼
    const res2 = await fetch('http://localhost:8080/Member/updatePassword', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({
        newPassword: form.newPassword
      })
    })

    const text2 = await res2.text()
    if (res2.ok) {
      ElMessage.success(text2 || '密碼已更新')
      router.push('/MemberCenter')
    } else {
      ElMessage.error(text2 || '更新失敗')
    }
  } catch (err) {
    ElMessage.error('發生錯誤，請稍後再試')
  }
}
</script>

  
  <style scoped>
  .edit-password-container {
    max-width: 500px;
    margin: 80px auto;
    text-align: center;
  }
  
  .title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 40px;
    position: relative;
    display: inline-block;
  }
  
  .title::after {
    content: '';
    width: 30px;
    height: 3px;
    background-color: #c09a5b;
    display: block;
    margin: 8px auto 0;
  }
  
  .form {
    text-align: left;
  }
  
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .submit-btn {
    background-color: #c09a5b;
    border-color: #c09a5b;
    width: 100%;
    font-weight: bold;
  }
  </style>
  