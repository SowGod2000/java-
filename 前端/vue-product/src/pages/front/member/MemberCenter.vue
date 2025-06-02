<template>
  <div class="member-center-wrapper">
    <el-card>
      <el-menu mode="horizontal" :default-active="activeTab" @select="handleTabChange">
        <el-menu-item index="profile">個人資訊</el-menu-item>
        <el-menu-item index="bookingOrders">訂房訂單</el-menu-item>
        <el-menu-item index="restaurantOrders">餐聽訂單</el-menu-item>
        <el-menu-item index="orders">商品訂單</el-menu-item>
      </el-menu>

      <component :is="currentComponent" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed , onMounted} from 'vue'
import { useRoute } from 'vue-router'

import ProfileTab from './Profile.vue'
import BookingOrdersTab from '../room/BookingOrdersTab.vue'
import RestaurantOrdersTab from '../restaurant/RestaurantOrdersTab.vue'
import ProductOrdersTab from './ProductOrders.vue' // ✅ 注意檔名要正確


const route = useRoute()

const activeTab = ref('profile')

const handleTabChange = (key) => {
  activeTab.value = key
}

const currentComponent = computed(() => {
  return {
    profile: ProfileTab,
    bookingOrders: BookingOrdersTab,
    restaurantOrders: RestaurantOrdersTab,
    orders: ProductOrdersTab,

  }[activeTab.value]
})



onMounted(() => {
  // 如果 URL 有 tab 參數，就切換到該 tab
  const tabParam = route.query.tab
  if (tabParam) {
    activeTab.value = tabParam
  }
})
</script>
<style scoped>
/* .member-center-wrapper {
  padding-top: 80px;
} */

.el-menu {
  display: flex;
  justify-content: center;
}

.el-menu-item {
  margin: 0 10px;
}
</style>
