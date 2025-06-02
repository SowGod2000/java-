<template>
  <div class="container py-5">
    <h1 class="mb-4">🛍️ 商品商城</h1>

    <!-- 分類篩選與搜尋 -->
    <div class="d-flex flex-wrap gap-2 mb-4">
      <select v-model="category" class="form-select" style="max-width: 200px" @change="fetchProducts">
        <option value="">全部分類</option>
        <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
      </select>
      <input v-model="search" class="form-control" style="max-width: 300px" placeholder="搜尋商品名稱" @keyup.enter="fetchProducts" />
      <button class="btn btn-secondary" @click="fetchProducts">搜尋</button>
    </div>

    <!-- 商品列表 -->
    <div class="row">
      <div class="col-md-3 mb-4" v-for="product in products" :key="product.pid">
        <div class="card h-100 shadow-sm">
          <img :src="product.pimage" class="card-img-top" alt="商品圖片" style="height: 160px; object-fit: cover" />
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">{{ product.pname }}</h5>
            <p class="card-text">{{ product.pdescription }}</p>
            <p class="fw-bold text-danger mb-1">NT$ {{ product.pprice.toLocaleString() }}</p>
            <button class="btn btn-outline-primary mt-auto" @click="addToCart(product)">加入購物車 🛒</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const products = ref([])
const search = ref('')
const category = ref('')
const categories = ref(['點心', '甜點', '飲品', '禮盒']) // 可從後端動態載入

// 取得商品資料
const fetchProducts = async () => {
  const params = new URLSearchParams()
  if (search.value) params.append('search', search.value)
  if (category.value) params.append('category', category.value)
  const res = await fetch(`/api/product?${params}`)
  const data = await res.json()
  products.value = data.products || []
}

// 修正：將 pid 改為 productId，才會被後端正確接收
const addToCart = async (product) => {
  await fetch('/api/cart/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productId: product.pid, quantity: 1 }) // ✅ 修正這裡
  })
  showCartMessage()
}

// 顯示提示訊息
const showCartMessage = () => {
  const msg = document.createElement('div')
  msg.innerText = '✅ 已加入購物車'
  msg.style.position = 'fixed'
  msg.style.top = '10px'
  msg.style.right = '10px'
  msg.style.background = 'rgba(0,0,0,0.7)'
  msg.style.color = '#fff'
  msg.style.padding = '10px 20px'
  msg.style.borderRadius = '8px'
  msg.style.zIndex = 9999
  document.body.appendChild(msg)
  setTimeout(() => msg.remove(), 1500)
}

onMounted(fetchProducts)
</script>

<style scoped>
.card-title {
  font-size: 1.1rem;
}
</style>
