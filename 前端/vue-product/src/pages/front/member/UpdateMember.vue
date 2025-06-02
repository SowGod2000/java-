<template>
    <div class="container">
      <h2 class="text-center mb-4">修改會員資料</h2>
  
      <el-form :model="member" label-width="90px" status-icon>
        <el-form-item label="姓名" required>
          <el-input v-model="member.name" placeholder="請輸入姓名" />
        </el-form-item>
  
        <el-form-item label="電話" required>
          <el-input v-model="member.phone" placeholder="請輸入電話" />
          <div v-if="member.phone && !isValidPhone(member.phone)" class="error-text">
            手機格式錯誤（正確格式：09xxxxxxxx）
          </div>
        </el-form-item>
  
        <el-form-item label="Email" required>
          <el-input v-model="member.email" placeholder="請輸入電子郵件" />
          <div v-if="member.email && !isValidEmail(member.email)" class="error-text">
            電子郵件格式錯誤
          </div>
        </el-form-item>
  
        <el-form-item label="生日" required>
          <el-date-picker
            v-model="member.birthDate"
            type="date"
            placeholder="選擇生日"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
  
        <el-form-item label="地址" required>
          <el-input v-model="member.address" placeholder="請輸入地址" />
          <div v-if="member.address && !isValidAddress(member.address)" class="error-text">
            地址不完整，請包含市/縣與區/鄉/鎮
          </div>
        </el-form-item>
  
        <el-form-item label="身分證" required>
          <el-input v-model="member.idCard" placeholder="請輸入身份證字號" />
        </el-form-item>
  
        <el-form-item label="密碼" required>
          <el-input v-model="member.password" type="password" show-password placeholder="請輸入密碼" />
        </el-form-item>
  
        <el-form-item>
          <el-button type="primary" @click="updateMember">修改資料</el-button>
        </el-form-item>
  
        <div v-if="responseMessage" :style="{ color: 'red', marginBottom: '1rem' }">
          {{ responseMessage }}
        </div>
      </el-form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  
  const route = useRoute()
  const router = useRouter()
  const memberId = route.query.id
  
  const member = ref({
    name: '',
    phone: '',
    email: '',
    birthDate: '',
    address: '',
    idCard: '',
    password: ''
  })
  
  const responseMessage = ref('')
  
  // 驗證邏輯
  function isValidPhone(phone) {
    return /^09\d{8}$/.test(phone)
  }
  
  function isValidEmail(email) {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
    return emailPattern.test(email)
  }
  
  function isValidAddress(address) {
    return /(市|縣)/.test(address) && /(區|鄉|鎮)/.test(address)
  }
  
  function isOver18(birthDate) {
    const today = new Date()
    const birth = new Date(birthDate)
    const age = today.getFullYear() - birth.getFullYear()
    const m = today.getMonth() - birth.getMonth()
    return age > 18 || (age === 18 && m >= 0)
  }
  
  // 取得會員資料
  const fetchMember = async () => {
    try {
      const res = await fetch(`http://localhost:8080/Member/${memberId}`)
      const data = await res.json()
      if (data.birthDate) {
        data.birthDate = data.birthDate.split('T')[0] // 去掉時間部分
      }
      member.value = data
    } catch (error) {
      console.error('取得會員資料錯誤:', error)
    }
  }
  
  // 更新會員資料
  const updateMember = async () => {
    responseMessage.value = ''
  
    if (!isOver18(member.value.birthDate)) {
      responseMessage.value = '會員年齡必須大於18歲'
      return
    }
  
    if (!isValidAddress(member.value.address)) {
      responseMessage.value = '請輸入完整的地址（需包含市/縣 + 區/鄉/鎮）'
      return
    }
  
    if (!isValidPhone(member.value.phone)) {
      responseMessage.value = '請輸入正確的台灣手機號碼 (格式如 0912345678)'
      return
    }
  
    if (!isValidEmail(member.value.email)) {
      responseMessage.value = '請輸入有效的電子郵件格式'
      return
    }
  
    try {
      const res = await fetch(`http://localhost:8080/Member/update/${memberId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(member.value)
      })
  
      if (res.ok) {
        alert('會員資料已更新')
        router.push('/memberlist')
      } else {
        responseMessage.value = '更新失敗，請稍後再試'
      }
    } catch (error) {
      console.error('更新錯誤：', error)
      responseMessage.value = '更新失敗，伺服器發生錯誤'
    }
  }
  
  onMounted(() => {
    if (!memberId) {
      alert('找不到會員 ID')
      router.push('/memberlist')
    } else {
      fetchMember()
    }
  })
  </script>
  
  <style scoped>
  .container {
    max-width: 600px;
    margin: 80px auto;
    padding: 2rem;
    background: #f5f7fa;
    border-radius: 8px;
    box-shadow: 0 0 10px #ddd;
  }
  .error-text {
    font-size: 0.85rem;
    color: red;
    margin-top: 5px;
  }
  </style>
  