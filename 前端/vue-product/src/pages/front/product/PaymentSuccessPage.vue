<template>
  <v-container class="fill-height d-flex justify-center align-center">
    <v-card class="pa-8 text-center" elevation="3" max-width="500">
      <v-icon size="80" color="success">mdi-check-circle-outline</v-icon>
      <h2 class="text-h5 mt-4 mb-2 text-success"> 付款成功</h2>
      <p>感謝您的訂購，商品會儘快出貨！</p>
      <p class="font-weight-bold">訂單編號：{{ orderId }}</p>
      <v-btn color="primary" class="mt-4" to="/">回首頁</v-btn>
      <v-btn variant="outlined" class="mt-2" to="/orders">查看訂單紀錄</v-btn>
    </v-card>
  </v-container>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
const orderId = route.query.orderId || '未知'
</script>

<style scoped>
.text-success {
  color: #4caf50;
}
</style>
