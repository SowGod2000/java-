<template>
  <div class="disabled-container">
    <el-result
      icon="warning"
      title="此帳號已被停用"
      sub-title="3 秒後將自動跳回登入頁面..."
    >
      <template #extra>
        <el-button type="primary" @click="router.push('/login')">立即返回登入</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  setTimeout(() => {
    router.push('/login')
  }, 3000)
})
</script>

<style scoped>
.disabled-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-family: 'Arial', sans-serif;
  background-color: #f5f7fa;
}
</style>
