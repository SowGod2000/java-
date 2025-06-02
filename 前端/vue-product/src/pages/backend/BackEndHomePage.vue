<template>
    <v-app>
        <v-main>
            <v-container class="py-5">
                <h1>First Hotel 後台管理系統</h1>
                <h3>您好 {{ userStore.user.name.substring(1) }}，請選擇您要操作的項目</h3>
                <!-- 可以在這裡放其他後台內容 -->
            </v-container>
        </v-main>
    </v-app>
</template>

<script setup>
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
// 不需要額外的 script 就能跑基本 sidebar
</script>

<style scoped>
/* 你可以自訂樣式 */
</style>