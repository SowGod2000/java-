<template>
    <div class="restaurant-layout">
      
      <router-view /> <!-- 其他內容動態放這裡 -->
      <!--<chatRoom />-->  <!--固定有聊天室-->
  
    </div>
  </template>
  
  <script setup>
  import chatRoom from '@/components/chatRoom.vue'
  </script>
  
  <style scoped>
  .restaurant-layout {
    display: flex;
    flex-direction: column;
  }
  </style>