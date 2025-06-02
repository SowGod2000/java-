<template>
  <div class="loading-container">
    <el-result
      icon="info"
      title="Google 登入中"
      sub-title="請稍候，正在驗證您的身分..."
    >
      <template #icon>
        <el-icon class="is-loading" :size="50">
          <Loading />
        </el-icon>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

onMounted(() => {
  const token = route.query.token

  if (token) {
    localStorage.setItem('jwt', token)
    ElMessage.success('登入成功！歡迎回來 👋')
    router.push('/')
  } else {
    ElMessage.error('登入失敗，請重新嘗試')
    router.push('/login')
  }
})
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f7fa;
}
</style>
