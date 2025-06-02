<template>
  <el-card class="container">
    <template #header>
      <div class="header">
        <span>會員資料</span>
        <el-button type="danger" @click="handleLogout">登出</el-button>
      </div>
    </template>

    <el-form :model="localUser" label-width="80px" v-if="user">
      <el-form-item label="姓名">
        <el-input v-model="localUser.name" />
      </el-form-item>

      <el-form-item label="Email">
        <el-input v-model="localUser.email" type="email" />
      </el-form-item>

      <el-form-item label="電話">
        <el-input v-model="localUser.phone" />
      </el-form-item>

      <el-form-item label="生日">
        <el-input   v-model="localUser.birthDate"  />
      </el-form-item>


      <el-form-item>
        <el-button type="primary" @click="updateProfile">儲存</el-button>
        <el-button type="warning" @click="goToResetPassword" class="ml-2">重新設定密碼</el-button>
      </el-form-item>
    </el-form>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon class="mt-3" />
    <el-alert v-if="successMessage" :title="successMessage" type="success" show-icon class="mt-3" />
  </el-card>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.user)
const localUser = ref(null)
const errorMessage = ref('')
const successMessage = ref('')


// 登出處理
const handleLogout = async () => {
  try {
    await fetch('http://localhost:8080/Member/logout', {
      method: 'POST',
      credentials: 'include',
    })

    document.cookie = 'jwt=; Max-Age=0; path=/'
    userStore.clearUser() // 清空 pinia
    router.push('/login')
    ElMessage.success('已成功登出')
  } catch (err) {
    ElMessage.error('登出失敗')
  }
}



// 頁面加載時，從 API 初始化 user 資料
onMounted(async () => {
  await userStore.initUserFromApi()
  if (!userStore.user) {
    router.push('/login')
  }
})

// 當 user 資料更新時，更新 localUser
watch(
  () => userStore.user,
  (newUser) => {
    if (newUser) {
      localUser.value = { ...newUser }
    }
  },
  { immediate: true }
)

// 修改會員資料
const updateProfile = async () => {
  try {
    const res = await fetch('http://localhost:8080/Member/update', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(localUser.value),
    })

    if (res.ok) {
      successMessage.value = '會員資料已成功更新'
      errorMessage.value = ''
      userStore.setUser(localUser.value)
    } else {
      errorMessage.value = '更新失敗，請檢查資料'
      successMessage.value = ''
    }
  } catch (err) {
    errorMessage.value = '更新發生錯誤: ' + err.message
    successMessage.value = ''
  }
}

// 前往重設密碼頁面
const goToResetPassword = () => {
  router.push('/ChangePassword')
}
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 80px auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-3 {
  margin-top: 20px;
}
</style>
