<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'

onMounted(() => {
  const userStore = useUserStore()
  userStore.initUserFromApi()
})

</script>

