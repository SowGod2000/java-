<template>
  <div class="container">
    <div class="header-container">
      <h1>會員資料</h1>
      <el-button @click="logout" type="danger">登出</el-button>
    </div>

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

    <el-table :data="members" stripe border style="width: 100%" v-if="members.length > 0">
      <el-table-column label="姓名" prop="name" width="120" />
      <el-table-column label="電話" prop="phone" width="120" />
      <el-table-column label="電子郵件" prop="email" width="180" />
      <el-table-column label="生日" prop="birthDate" width="120" />
      <el-table-column label="地址" prop="address" width="200" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button @click="editMember(row.memberID)" size="small" type="primary">修改</el-button>
          <el-button @click="deleteMember(row.memberID)" size="small" type="danger">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else description="無資料" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const members = ref([])
const searchForm = ref({ name: '', phone: '', email: '' })
const token = localStorage.getItem('jwt')

if (!token) {
  router.push('/login')
}

const searchMembers = async () => {
  const params = new URLSearchParams()
  if (searchForm.value.name) params.append('name', searchForm.value.name)
  if (searchForm.value.phone) params.append('phone', searchForm.value.phone)
  if (searchForm.value.email) params.append('email', searchForm.value.email)

  try {
    const res = await fetch(`http://localhost:8080/Member/search?${params.toString()}`, { method: 'GET' })
    const data = await res.json()
    members.value = data.map(member => {
      if (member.birthDate) {
        member.birthDate = new Date(member.birthDate).toISOString().split('T')[0]
      }
      return member
    })
  } catch (err) {
    console.error('查詢錯誤：', err)
  }
}

const editMember = (id) => {
  router.push(`/UpdateMember?id=${id}`)
}

const deleteMember = async (id) => {
  if (confirm('確定要刪除此會員嗎？')) {
    try {
      const res = await fetch(`http://localhost:8080/Member/delete?memberID=${id}`, { method: 'DELETE' })
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

const logout = () => {
  localStorage.removeItem('jwt')
  router.push('/login')
}

onMounted(() => {
  searchMembers()
})
</script>

<style scoped>
.container {
  padding: 2rem;
  max-width: 900px;
  margin: 100px auto;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
