<template>
  <div class="container py-5">
    <h2 class="text-center mb-4">註冊會員</h2>
    <el-form :model="form" label-width="90px" status-icon>
      <el-form-item label="姓名" required>
        <el-input v-model="form.name" placeholder="請輸入姓名" />
      </el-form-item>

      <el-form-item label="電話" required>
        <el-input v-model="form.phone" placeholder="請輸入電話" />
        <div v-if="form.phone && !isValidPhone(form.phone)" class="error-text">
          手機格式錯誤（正確格式：09xxxxxxxx）
        </div>
      </el-form-item>

      <el-form-item label="電子郵件" required>
        <el-input v-model="form.email" placeholder="請輸入電子郵件" />
        <div v-if="form.email && !isValidEmail(form.email)" class="error-text">
          電子郵件格式錯誤
        </div>
      </el-form-item>

      <el-form-item label="生日" required>
        <el-date-picker
          v-model="form.birthDate"
          type="date"
          placeholder="選擇生日"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

      <el-form-item label="地址" required>
        <el-input v-model="form.address" placeholder="請輸入地址" />
        <div v-if="form.address && !isValidAddress(form.address)" class="error-text">
          地址不完整，請包含市/縣與區/鄉/鎮、路/街
        </div>
      </el-form-item>

      <el-form-item label="身分證" required>
        <el-input v-model="form.idCard" placeholder="請輸入身份證字號" />
      </el-form-item>

      <el-form-item label="密碼" required>
        <el-input v-model="form.password" type="password" placeholder="請輸入密碼" show-password />
        <div v-if="form.password" :style="{ color: passwordStrength.color }">
          密碼強度：{{ passwordStrength.text }}
        </div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleRegister">註冊</el-button>
        <el-button @click="fillTestData" style="margin-left: 10px">一鍵填入測試資料</el-button>
      </el-form-item>

      <el-form-item v-if="responseMessage">
        <div :style="{ color: messageColor }">{{ responseMessage }}</div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  name: '',
  phone: '',
  email: '',
  birthDate: '',
  address: '',
  idCard: '',
  password: '',
  role: 'user'
})

const responseMessage = ref('')
const messageColor = ref('red')

const testDataList = ref([
  {
    name: '林小虎',
    phone: '0983629982',
    email: 'test1@example.com',
    birthDate: '1999-02-10',
    address: '台北市大安區復興南路一段390巷10弄5號',
    idCard: 'A123456780',
    password: 'Test@1234',
    role: 'user'
  },
  {
    name: '張美美',
    phone: '0983629982',
    email: 'test2@example.com',
    birthDate: '1995-05-15',
    address: '新竹縣湖口鄉中山路二段188號',
    idCard: 'B223456781',
    password: 'StrongP@ss2',
    role: 'user'
  },
  {
    name: '陳大文',
    phone: '0983629982',
    email: 'test3@example.com',
    birthDate: '2001-09-30',
    address: '嘉義市西區文化路456號',
    idCard: 'C323456782',
    password: 'Hello@2024',
    role: 'user'
  },
  {
    name: '王小明',
    phone: '0983629982',
    email: 'test4@example.com',
    birthDate: '1998-11-20',
    address: '桃園市中壢區延平路527巷20弄8號',
    idCard: 'D423456783',
    password: 'Wang@1234',
    role: 'user'
  }
])

const usedIndexes = ref(new Set())

const fillTestData = () => {
  if (usedIndexes.value.size === testDataList.value.length) {
    responseMessage.value = '已經沒有更多測試資料了！'
    return
  }

  let index
  do {
    index = Math.floor(Math.random() * testDataList.value.length)
  } while (usedIndexes.value.has(index))

  usedIndexes.value.add(index)
  form.value = { ...testDataList.value[index] }
  responseMessage.value = ''
}

// 驗證方法
function isValidPhone(phone) {
  return /^09\d{8}$/.test(phone)
}

function isValidEmail(email) {
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailPattern.test(email)
}

function isValidAddress(address) {
  return /(市|縣)/.test(address)  && /(區|鄉|鎮)/.test(address) && /(路|街)/.test(address) 
}

function isOver18(birthDate) {
  const today = new Date()
  const birth = new Date(birthDate)
  const age = today.getFullYear() - birth.getFullYear()
  const m = today.getMonth() - birth.getMonth()
  return age > 18 || (age === 18 && m >= 0)
}

const passwordStrength = computed(() => {
  const pwd = form.value.password
  let score = 0
  if (pwd.length >= 8) score++
  if (/[A-Z]/.test(pwd)) score++
  if (/[a-z]/.test(pwd)) score++
  if (/[0-9]/.test(pwd)) score++
  if (/[^A-Za-z0-9]/.test(pwd)) score++

  if (score <= 2) return { text: '弱', color: 'red' }
  if (score <= 4) return { text: '中', color: 'orange' }
  return { text: '強', color: 'green' }
})

const apiUrl = 'http://localhost:8080/Member/add'
const checkEmailUrl = 'http://localhost:8080/Member/checkEmail'

const handleRegister = async () => {
  if (!isOver18(form.value.birthDate)) {
    responseMessage.value = '會員年齡必須大於18歲'
    return
  }

  if (!isValidAddress(form.value.address)) {
    responseMessage.value = '請輸入完整的地址（需包含市/縣 + 區/鄉/鎮 + 路/街 ）'
    return
  }

  if (!isValidPhone(form.value.phone)) {
    responseMessage.value = '請輸入正確的台灣手機號碼 (格式如 0912345678)'
    return
  }

  if (!isValidEmail(form.value.email)) {
    responseMessage.value = '請輸入有效的電子郵件格式'
    return
  }

  try {
    const checkRes = await fetch(`${checkEmailUrl}?email=${form.value.email}`)

    if (checkRes.status === 409) {
      responseMessage.value = '此電子郵件已被註冊'
      return
    }

    if (checkRes.status === 200) {
      const res = await fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form.value)
      })

      const data = await res.json()

      if (res.ok) {
        responseMessage.value = '註冊成功！即將前往登入頁面...'
        messageColor.value = 'green'
        setTimeout(() => router.push('/login'), 1500)
      } else {
        responseMessage.value = data.message || '註冊失敗，請檢查輸入資料'
      }
    }
  } catch (err) {
    console.error(err)
    responseMessage.value = '無法連接伺服器，請稍後再試'
  }
}
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 0 auto;
}
.error-text {
  color: red;
  font-size: 0.85rem;
  margin-top: 5px;
}
</style>
