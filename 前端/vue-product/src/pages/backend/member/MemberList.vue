<template>
  <div class="container">
    <div class="header-container">
      <div class="header-left">
        <h1>會員資料</h1>
      </div>
      <div class="header-right">
        <div class="role-info">當前身分：{{ roleName }}</div>
        <el-button @click="logout" type="danger" class="header-button">登出</el-button>
        <el-button 
          v-if="isAdminOrSuperadmin" 
          type="success" 
          @click="openAddDialog" 
          class="header-button">
          新增會員
        </el-button>
        <el-button 
          v-if="isSuperadmin" 
          type="warning" 
          @click="exportLogs" 
          class="header-button">
          匯出控制日誌
        </el-button>
      </div>
    </div>

    <!-- 搜尋 -->
    <el-form :model="searchForm" @submit.native.prevent="searchMembers" label-width="80px" class="search-form">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input v-model="searchForm.name" placeholder="姓名" clearable />
        </el-col>
        <el-col :span="8">
          <el-input v-model="searchForm.phone" placeholder="電話" clearable />
        </el-col>
        <el-col :span="8">
          <el-input v-model="searchForm.email" placeholder="電子郵件" clearable />
        </el-col>
      </el-row>
      <el-button type="primary" @click="searchMembers">查詢</el-button>
    </el-form>

    <!-- 表格 -->
    <el-table :data="pagedMembers" stripe border style="width: 100%" v-if="members.length > 0">
      <el-table-column label="姓名" prop="name" width="120" />
      <el-table-column label="電子郵件" prop="email" width="300" />
      <el-table-column label="角色" prop="role" width="120" />
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button @click="showMemberDetail(row)" size="small" type="info">詳細</el-button>
          <el-button 
            v-if="canEdit" 
            @click="editMember(row.memberID)" 
            size="small" 
            type="primary">
            修改
          </el-button>
          <el-button 
            v-if="isSuperadmin" 
            @click="deleteMember(row.memberID)" 
            size="small" 
            type="danger">
            刪除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分頁 -->
    <el-pagination v-if="members.length > pageSize" background layout="prev, pager, next"
      :total="members.length" :page-size="pageSize" v-model:current-page="currentPage" class="pagination" />

    <!-- 無資料 -->
    <el-empty v-else description="無資料" />

    <!-- 會員詳細 Dialog -->
    <el-dialog v-model="memberDetailDialogVisible" title="會員詳細資料" width="500px">
      <el-form v-if="memberDetailData" label-width="100px">
        <el-form-item label="姓名">
          <span>{{ memberDetailData.name }}</span>
        </el-form-item>
        <el-form-item label="電子郵件">
          <span>{{ memberDetailData.email }}</span>
        </el-form-item>
        <el-form-item label="生日">
          <span>{{ memberDetailData.birthDate }}</span>
        </el-form-item>
        <el-form-item label="身分證">
          <span>{{ memberDetailData.idCard }}</span>
        </el-form-item>
        <el-form-item label="地址">
          <span>{{ memberDetailData.address }}</span>
        </el-form-item>
        <el-form-item label="電話">
          <span>{{ memberDetailData.phone }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDetailDialogVisible = false">關閉</el-button>
      </template>
    </el-dialog>

    <!-- 新增會員 Dialog -->
    <el-dialog v-model="addDialogVisible" title="新增會員" width="500px">
      <el-form :model="newMember" :rules="rules" ref="formRef" label-width="100px">
  <el-form-item label="姓名" prop="name">
    <el-input v-model="newMember.name" />
  </el-form-item>
  <el-form-item label="生日" prop="birthDate">
    <el-date-picker v-model="newMember.birthDate" type="date" placeholder="選擇日期" style="width: 100%;" />
  </el-form-item>
  <el-form-item label="地址" prop="address">
    <el-input v-model="newMember.address" />
  </el-form-item>
  <el-form-item label="email" prop="address">
    <el-input v-model="newMember.email" />
  </el-form-item>
  <el-form-item label="身分證" prop="idNumber">
    <el-input v-model="newMember.idNumber" />
  </el-form-item>
  <el-form-item label="密碼" prop="password">
    <el-input v-model="newMember.password" show-password />
  </el-form-item>
  <el-form-item label="電話" prop="phone">
    <el-input v-model="newMember.phone" />
  </el-form-item>
  <el-form-item label="角色" prop="role">
    <el-select v-model="newMember.role" placeholder="選擇角色">
      <el-option label="一般會員" value="user" />
      <el-option label="一般員工" value="employee" />
      <el-option label="管理員" value="admin" />
    </el-select>
  </el-form-item>

  <!-- 顯示錯誤訊息 -->
  <el-button type="primary" @click="addMember">提交</el-button>
</el-form>

    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const members = ref([])
const searchForm = ref({ name: '', phone: '', email: '' })
const currentPage = ref(1)
const pageSize = 10

const pagedMembers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return members.value.slice(start, end)
})

// 登入者角色
const userRole = computed(() => userStore.user?.role || '')
const roleName = computed(() => {
  if (userRole.value === 'employee') return '一般員工'
  if (userRole.value === 'admin') return '管理員'
  if (userRole.value === 'superadmin') return '超級管理員'
  return ''
})

// 權限判斷
const isAdminOrSuperadmin = computed(() => userRole.value === 'admin' || userRole.value === 'superadmin')
const isSuperadmin = computed(() => userRole.value === 'superadmin')
const canEdit = computed(() => userRole.value === 'employee'  || userRole.value === 'admin' || userRole.value === 'superadmin')

// 搜尋會員
const searchMembers = async () => {
  const params = new URLSearchParams()
  if (searchForm.value.name) params.append('name', searchForm.value.name)
  if (searchForm.value.phone) params.append('phone', searchForm.value.phone)
  if (searchForm.value.email) params.append('email', searchForm.value.email)

  try {
    const res = await fetch(`http://localhost:8080/Member/search?${params.toString()}`, {
      method: 'GET',
      credentials: 'include'
    })
    if (res.status === 401) {
      router.push('/login')
      return
    }
    const data = await res.json()

    
    

    members.value = data.map(member => {

      if(member.operationLogs)
      {
        delete member.operationLogs;
      }

      if (member.birthDate) {
        member.birthDate = new Date(member.birthDate).toISOString().split('T')[0]
      }
      return member
    })
    currentPage.value = 1
  } catch (err) {
    console.error('查詢錯誤：', err)
  }
}

// 登出
const logout = async () => {
  try {
    await fetch('http://localhost:8080/Member/logout', {
      method: 'POST',
      credentials: 'include',
    })
    document.cookie = "jwt=; Max-Age=0; path=/"
    userStore.clearUser()
    router.push('/login')
    ElMessage.success('已成功登出')
  } catch (err) {
    ElMessage.error('登出失敗')
  }
}

// 會員詳細
const memberDetailDialogVisible = ref(false)
const memberDetailData = ref(null)
const showMemberDetail = (row) => {
  memberDetailData.value = row
  memberDetailDialogVisible.value = true
}

// 修改會員
const editMember = (id) => {
  router.push(`/UpdateMember?id=${id}`)
}

// 刪除會員
const deleteMember = async (id) => {
  if (confirm('確定要刪除此會員嗎？')) {
    try {
      const res = await fetch(`http://localhost:8080/Member/delete?memberID=${id}`, {
        method: 'DELETE',
        credentials: 'include'
      })
      if (res.ok) {
        ElMessage.success('會員已刪除')
        searchMembers()
      } else {
        ElMessage.error('刪除失敗')
      }
    } catch (err) {
      console.error('刪除錯誤：', err)
    }
  }
}

// 新增會員
const addDialogVisible = ref(false)
const newMember = ref({
  name: '', birthDate: '', address: '', idNumber: '', password: '', phone: '', role: ''
})
const openAddDialog = () => {
  newMember.value = { name: '', birthDate: '', address: '', idNumber: '', password: '', phone: '', role: '' }
  addDialogVisible.value = true
}

const formRef = ref()
const rules = reactive({
  name: [{ required: true, message: '請輸入姓名', trigger: 'blur' }],
  birthDate: [{ required: true, message: '請選擇生日', trigger: 'change' }],
  address: [{ required: true, message: '請輸入地址', trigger: 'blur' }],
  idNumber: [{ pattern: /^[A-Z]{1}[1-2]{1}[0-9]{8}$/, message: '身分證格式錯誤', trigger: 'blur' }],
  password: [{ required: true, message: '請輸入密碼', trigger: 'blur' }],
  phone: [{ pattern: /^09\d{8}$/, message: '電話格式錯誤', trigger: 'blur' }],
  role: [{ required: true, message: '請選擇角色', trigger: 'change' }]
})
const addMember = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('請填寫正確的資料')
      return
    }
    try {
      const payload = { ...newMember.value }
      if (payload.birthDate instanceof Date) {
        payload.birthDate = payload.birthDate.toISOString().split('T')[0]
      }
      const res = await fetch('http://localhost:8080/Member/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      })
      if (res.ok) {
        ElMessage.success('新增成功')
        addDialogVisible.value = false
        searchMembers()
      } else {
        const errorText = await res.text()
        ElMessage.error(`新增失敗：${errorText}`)
      }
    } catch (err) {
      console.error('新增錯誤：', err)
    }
  })
}

const exportLogs = async () =>{

  try {
    const res = await fetch('http://localhost:8080/Member/exportLogs', {
      method: 'GET',
      credentials: 'include'
    })

    if (!res.ok) {
      throw new Error('匯出失敗')
    }

    const blob = await res.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'logs.csv'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('控制日誌匯出成功')
  } catch (error) {
    console.error('匯出錯誤：', error)
    ElMessage.error('匯出失敗')
  }

}


// 頁面初始化
onMounted(async () => {
  await userStore.initUserFromApi()
  await searchMembers()
})
</script>

<style scoped>
.container {
  padding: 2rem;
  max-width: 855px;
  margin: 100px auto;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  flex: 1;
  text-align: left;
}

.header-right {
  flex: 2;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}

.role-info {
  margin-right: 20px;
  font-weight: bold;
  font-size: 16px;
}

.header-button {
  margin-left: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
