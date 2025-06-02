<!-- layouts/FrontendLayout.vue -->
<template>
  <v-app>
    <v-main>
      <Navbar />
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'

onMounted(() => {
  const s1 = document.createElement('script')
  s1.async = true
  s1.src = 'https://embed.tawk.to/6805dbcad6f646191575726f/1ipbe54an'
  s1.charset = 'UTF-8'
  s1.setAttribute('crossorigin', '*')
  document.body.appendChild(s1)
})
</script>