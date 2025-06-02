<template>
  <!-- 絕對定位的 navbar，不再推 App 內容 -->
  <v-app-bar absolute flat :elevation="isScrolled ? 4 : 0" :color="isScrolled || !isHome ? '#8E8E8E' : 'transparent'"
    class="px-4 transition" style="z-index: 10">
    <!-- LOGO -->
    <v-app-bar-title class="text-white">
      <RouterLink to="/" class="text-white text-decoration-none">First Hotel</RouterLink>
    </v-app-bar-title>

    <!-- 手機版選單按鈕 -->
    <v-app-bar-nav-icon @click="drawer = !drawer" class="d-lg-none text-white" />

    <!-- 桌面版選單 -->
    <v-row class="d-none d-lg-flex" align="center" justify="end">
      <v-btn to="/" text exact class="text-white">首頁</v-btn>

      <v-btn v-if="userStore.user?.role != 'user' && userStore.user" to="/backend" text class="text-white">
        後台
      </v-btn>

      <v-btn to="/rooms" text class="text-white">線上訂房</v-btn>

      <v-menu>
        <template #activator="{ props }">
          <v-btn v-bind="props" text class="text-white">
            餐廳訂位
            <v-icon right>mdi-menu-down</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item to="/restaurant/restaurantReserve" link>
            <v-list-item-title>預約訂位</v-list-item-title>
          </v-list-item>
          <v-list-item to="/restaurant/restaurantQueryPage" link>
            <v-list-item-title>訂位查詢</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <v-btn to="/shop" text class="text-white">線上商城</v-btn>
      <v-btn to="/cart" text class="text-white">購物車</v-btn>

      <v-btn v-if="!userStore.user" to="/login" text class="text-white">登入</v-btn>
      <v-btn v-if="!userStore.user" to="/register" text class="text-white">註冊</v-btn>
      <v-btn v-else to="/MemberCenter" text class="text-white">會員專區</v-btn>
    </v-row>
  </v-app-bar>

  <!-- 手機版抽屜仍可保留 app，temporary 模式下會疊在最上層 -->
  <v-navigation-drawer v-model="drawer" temporary app>
    <v-list nav>
      <v-list-item to="/" exact title="首頁" link />

      <v-list-item v-if="userStore.user?.role != 'user' && userStore.user" to="/backend" title="後台" link />

      <v-list-item to="/rooms" title="線上訂房" link />

      <v-list-group>
        <template #activator="{ props }">
          <v-list-item v-bind="props" title="餐廳訂位" />
        </template>
        <v-list-item to="/restaurant/restaurantReserve" title="預約訂位" link />
        <v-list-item to="/restaurant/restaurantQueryPage" title="訂位查詢" link />
      </v-list-group>

      <v-list-item to="/shop" title="線上商城" link />
      <v-list-item to="/cart" title="購物車" link />

      <v-list-item v-if="!userStore.user" to="/login" title="登入" link />
      <v-list-item v-if="!userStore.user" to="/register" title="註冊" link />
      <v-list-item v-else to="/MemberCenter" title="會員專區" link />
    </v-list>
  </v-navigation-drawer>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/userStore'

const userStore = useUserStore()
const drawer = ref(false)
const isScrolled = ref(false)
const route = useRoute()


// 只在首頁、且尚未捲動超過 50px 時保持透明
const isHome = computed(() => route.path === '/')

// 滾動偵測
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onBeforeUnmount(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
.v-btn {
  font-weight: bold;
}

.transition {
  transition: background-color 0.3s ease;
}
</style>
