<template>
  <div class="container">
    <h2 class="text-center mb-4">修改會員資料</h2>

    <el-form
      ref="formRef"
      :model="member"
      :rules="rules"
      label-width="90px"
      status-icon
    >
      <el-form-item label="姓名" prop="name">
        <el-input v-model="member.name" placeholder="請輸入姓名" />
      </el-form-item>

      <el-form-item label="電話" prop="phone">
        <el-input v-model="member.phone" placeholder="請輸入電話" />
      </el-form-item>

      <el-form-item label="Email" prop="email">
        <el-input v-model="member.email" placeholder="請輸入電子郵件" />
      </el-form-item>

      <el-form-item label="生日" prop="birthDate">
        <el-date-picker
          v-model="member.birthDate"
          type="date"
          placeholder="選擇生日"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

      <el-form-item label="地址" prop="address">
        <el-input v-model="member.address" placeholder="請輸入地址" />
      </el-form-item>

      <el-form-item label="身分證" prop="idCard">
        <el-input v-model="member.idCard" placeholder="請輸入身份證字號" />
      </el-form-item>

      <el-form-item label="密碼" prop="password">
        <el-input
          v-model="member.password"
          type="password"
          show-password
          placeholder="請輸入密碼"
        />
      </el-form-item>
      <el-form-item label="角色" v-if="member.role === 'admin' || member.role === 'superadmin'">
          <el-select v-model="member.role" placeholder="選擇角色">
            <el-option label="一般會員" value="user" />
            <el-option label="一般員工" value="employee" />
            <el-option label="管理員" value="admin" />
          </el-select>
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

// Router
const route = useRoute()
const router = useRouter()
const memberId = route.query.id

// Form Data
const member = ref({
  name: '',
  phone: '',
  email: '',
  birthDate: '',
  address: '',
  idCard: '',
  password: '',
  role: ''
})
const responseMessage = ref('')
const formRef = ref(null)

// 驗證規則
const rules = {
  name: [{ required: true, message: '請輸入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '請輸入電話', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (!/^09\d{8}$/.test(value)) {
          callback(new Error('請輸入正確的台灣手機號碼 (格式如 0912345678)'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '請輸入電子郵件', trigger: 'blur' },
    { type: 'email', message: '電子郵件格式錯誤', trigger: ['blur', 'change'] }
  ],
  birthDate: [{ required: true, message: '請選擇生日', trigger: 'change' }],
  address: [
    { required: true, message: '請輸入地址', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (!/(市|縣)/.test(value) || !/(區|鄉|鎮)/.test(value)) {
          callback(new Error('請輸入完整的地址（需包含市/縣 + 區/鄉/鎮）'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ],
  idCard: [{ required: true, message: '請輸入身份證字號', trigger: 'blur' }],
  password: [{ required: true, message: '請輸入密碼', trigger: 'blur' }]
}

// JWT 驗證
function getJwtFromCookie() {
  const cookie = document.cookie.split('; ').find(row => row.startsWith('jwt='))
  return cookie ? cookie.split('=')[1] : null
}

const token = getJwtFromCookie()
if (!token) {
  router.push('/login')
}

// 是否滿 18 歲
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
  
    
    if (data.operationLogs) {
      delete data.operationLogs; // 删除 operationLogs 字段
    }
    
    if (data.birthDate) {
      data.birthDate = data.birthDate.split('T')[0]
    }
    member.value = data
  } catch (error) {
    console.error('取得會員資料錯誤:', error)
  }
}

// 更新會員資料
const updateMember = () => {
  responseMessage.value = ''

  formRef.value.validate(async (valid) => {
    if (!valid) return

    if (!isOver18(member.value.birthDate)) {
      responseMessage.value = '會員年齡必須大於18歲'
      return
    }
   
    
    try {
      const res = await fetch(`http://localhost:8080/Member/update/${memberId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(member.value),
  credentials: 'include'
      })

      if (res.ok) {
        alert('會員資料已更新')
        router.push('/backend/memberlist')
      } else {
        responseMessage.value = '更新失敗，請稍後再試'
      }
    } catch (error) {
      console.error('更新錯誤：', error)
      responseMessage.value = '更新失敗，伺服器發生錯誤'
    }
  })
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
