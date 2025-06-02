<template>
  <v-container class="fill-height d-flex justify-center align-center">
    <v-card class="pa-8 text-center" elevation="3" max-width="500">
      <v-icon size="80" color="error">mdi-close-circle-outline</v-icon>
      <h2 class="text-h5 mt-4 mb-2 text-error"> 付款失敗或取消</h2>
      <p>請重新下單或聯絡客服</p>
      <v-btn color="primary" variant="outlined" class="mt-4" to="/cart">回購物車</v-btn>
    </v-card>
  </v-container>
</template>
