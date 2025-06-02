<template>
  <v-container class="py-5">
    <v-row>
      <!-- 商品列表 -->
      <v-col cols="12" md="9">
        <v-card flat>
          <v-card-title class="text-h5">商品商城</v-card-title>

          <!-- 搜尋 + 分類 + 排序 + 每頁顯示數量 -->
          <v-row class="mb-4" align="center" dense>
            <v-col cols="12" sm="2">
              <v-select
                v-model="categoryId"
                :items="categories"
                item-title="name"
                item-value="id"
                label="分類"
                density="compact"
                clearable
              />
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field
                v-model="search"
                prepend-inner-icon="mdi-magnify"
                label="搜尋商品"
                clearable
              />
            </v-col>
            <v-col cols="6" sm="2">
              <v-select
                v-model="sort"
                :items="sortOptions"
                label="排序方式"
                density="compact"
                clearable
              />
            </v-col>
            <v-col cols="6" sm="2">
              <v-select
                v-model="size"
                :items="[24, 48, 72]"
                label="每頁數量"
                density="compact"
              />
            </v-col>
            <v-col cols="12" sm="2">
              <v-btn color="primary" block @click="fetchProducts">搜尋</v-btn>
            </v-col>
          </v-row>

          <!-- tag 快速搜尋 -->
          <v-row class="mb-2 px-2" v-if="Array.isArray(tags)">
            <v-btn
              v-for="tag in tags"
              :key="tag.name"
              class="tag-button mx-1 my-1"
              @click="searchByTag(tag.name)"
            >
              #{{ tag.name }}
            </v-btn>
          </v-row>

          <!-- 商品總筆數 -->
          <v-row v-if="typeof totalElements === 'number'">
            <v-col cols="12" class="text-grey text-caption pb-1">
              共 {{ totalElements }} 項商品
            </v-col>
          </v-row>

          <!-- 商品卡片 -->
          <v-row v-if="Array.isArray(products)">
            <v-col cols="12" sm="6" md="4" v-for="product in products" :key="product.pid">
              <v-card class="product-card hover-scale elevation-1" flat>
                <v-img :src="IMAGE_BASE + product.pimage" height="200" cover />
                <v-card-text class="d-flex flex-column justify-space-between flex-grow-1">
                  <div>
                    <div class="text-body-1 font-weight-bold text-dark mt-2">{{ product.pname }}</div>
                    <div class="text-grey-darken-1 text-body-2 line-clamp-2 mb-1">{{ product.pdescription }}</div>
                    <div class="mb-2 d-flex flex-wrap">
                      <v-chip
                        v-for="(tag, index) in (product.ptags ? product.ptags.split(',').slice(0, 3) : [])"
                        :key="tag"
                        size="x-small"
                        color="indigo"
                        class="ma-1"
                        variant="tonal"
                      >
                        {{ tag }}
                      </v-chip>
                    </div>
                  </div>
                  <div class="d-flex justify-space-between align-center">
                    <div class="text-red font-weight-bold">NT$ {{ product.pprice.toLocaleString() }}</div>
                    <v-btn size="small" color="primary" @click="addToCart(product)">加入購物車</v-btn>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-card>
      </v-col>

      <!-- 購物車預覽 -->
      <v-expand-transition>
        <v-col cols="12" md="3" v-show="showCartPreview">
          <v-card elevation="2" class="pa-3 sticky-cart">
            <div v-if="Array.isArray(cartItems) && cartItems.length">
              <div
                v-for="item in cartItems"
                :key="item.productId"
                class="cart-item d-flex align-center mb-2"
              >
                <v-img :src="item.imageUrl" width="60" height="60" class="rounded mr-3" />
                <div class="flex-grow-1">
                  <div class="font-weight-bold">{{ item.productName }}</div>
                  <div class="text-caption">
                    {{ item.quantity }} x NT${{ item.price.toLocaleString() }}
                  </div>
                </div>
                <v-btn icon class="delete-btn" @click="removeItem(item.productId)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </div>
              <v-divider class="my-2" />
              <div class="text-end font-weight-bold mb-2">
                總金額：NT$ {{ total.toLocaleString() }}
              </div>
              <v-btn block class="checkout-btn" @click="goToCart">訂單結帳</v-btn>
            </div>
            <div v-else class="text-grey">目前購物車是空的。</div>
          </v-card>
        </v-col>
      </v-expand-transition>
    </v-row>

    <!-- 成功提示 -->
    <v-snackbar v-model="showMessage" timeout="1500" location="top end">
      ✅ 已加入購物車
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const IMAGE_BASE = 'http://localhost:8080/uploads/'

const search = ref('')
const categoryId = ref('')
const sort = ref('')
const size = ref(24)
const totalElements = ref(0)
const categories = ref([])
const tags = ref([])
const products = ref([])
const cart = ref({ cartItems: [], totalPrice: 0 })
const showCartPreview = ref(false)
const showMessage = ref(false)
let cartPreviewTimeout = null

const sortOptions = [
  { title: '上架時間（新 → 舊）', value: 'newest' },
  { title: '上架時間（舊 → 新）', value: 'oldest' },
  { title: '價格（低 → 高）', value: 'price_asc' },
  { title: '價格（高 → 低）', value: 'price_desc' },
]

const cartItems = computed(() => cart.value.cartItems || [])
const total = computed(() => cart.value.totalPrice || 0)

const fetchProducts = async () => {
  const params = new URLSearchParams({ page: 0, size: size.value })
  if (search.value) params.append('search', search.value)
  if (categoryId.value) params.append('categoryId', categoryId.value)
  if (sort.value) params.append('sort', sort.value)
  const res = await fetch(`http://localhost:8080/api/product?${params}`)
  const data = await res.json()
  products.value = data.products || []
  totalElements.value = data.totalElements || 0
}

const fetchCategories = async () => {
  const res = await fetch('http://localhost:8080/api/categories')
  categories.value = await res.json()
}

const fetchTags = async () => {
  const res = await fetch('http://localhost:8080/api/tags')
  tags.value = await res.json()
}

const fetchCart = async () => {
  const res = await fetch('http://localhost:8080/api/cart')
  cart.value = await res.json()
}

const searchByTag = async (tag) => {
  const res = await fetch(`http://localhost:8080/api/product/tags?tag=${tag}`)
  const data = await res.json()
  products.value = data
  totalElements.value = data.length
}

const showCartTemporarily = () => {
  showCartPreview.value = true
  clearTimeout(cartPreviewTimeout)
  cartPreviewTimeout = setTimeout(() => {
    showCartPreview.value = false
  }, 6000)
}

const addToCart = async (product) => {
  await fetch('http://localhost:8080/api/cart/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productId: product.pid, quantity: 1 }),
  })
  showMessage.value = true
  await fetchCart()
  showCartTemporarily()
}

const removeItem = async (productId) => {
  await fetch(`http://localhost:8080/api/cart/remove/${productId}`, { method: 'DELETE' })
  fetchCart()
}

const goToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
  fetchTags()
  fetchCart()
})
</script>

<style scoped>
.product-card {
  transition: transform 0.2s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.hover-scale:hover {
  transform: scale(1.01);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
