<template>
  <v-container class="py-5">
    <v-card>
      <v-card-title>
        商品管理
        <v-spacer />
        <v-btn color="success" @click="batchUpdateStatus('ACTIVE')">批次上架</v-btn>
        <v-btn color="warning" class="mx-2" @click="batchUpdateStatus('INACTIVE')">批次下架</v-btn>
        <v-btn color="primary" @click="openForm()">新增商品</v-btn>
      </v-card-title>

      <v-card-text>
        <v-row class="mb-4" dense>
          <v-col cols="12" md="4">
            <v-text-field v-model="search" label="搜尋商品名稱" @keyup.enter="fetchProducts" />
          </v-col>
          <v-col cols="12" md="3">
            <v-select v-model="statusFilter" :items="['ACTIVE', 'INACTIVE']" label="商品狀態" clearable @change="fetchProducts" />
          </v-col>
          <v-col cols="12" md="2">
            <v-btn color="secondary" block @click="fetchProducts">搜尋</v-btn>
          </v-col>
        </v-row>

        <v-data-table :headers="headers" :items="products" item-value="pid" show-select v-model="selected" :items-per-page="10" class="elevation-1">
          <template #item.pimage="{ item }">
            <v-img :src="IMAGE_BASE + item.pimage" max-height="60" contain />
          </template>

          <template #item.pstatus="{ item }">
            <v-chip :color="item.pstatus === 'ACTIVE' ? 'green' : 'grey'" dark>{{ item.pstatus }}</v-chip>
          </template>

          <template #item.plistedAt="{ item }">
            {{ formatDate(item.plistedAt) }}
          </template>

          <template #item.pupdatedAt="{ item }">
            {{ formatDate(item.pupdatedAt) }}
          </template>

          <template #item.category="{ item }">
            {{ item.categoryName || '－' }}
          </template>

          <template #item.ptags="{ item }">
            <v-chip v-for="tag in (item.ptags ? item.ptags.split(',') : [])" :key="tag" class="ma-1" size="x-small" color="indigo" variant="tonal">{{ tag }}</v-chip>
          </template>

          <template #item.actions="{ item }">
            <v-btn size="small" color="info" @click="openForm(item)">編輯</v-btn>
            <v-btn size="small" :color="item.pstatus === 'ACTIVE' ? 'warning' : 'success'" class="mx-1" @click="toggleStatus(item)">
              {{ item.pstatus === 'ACTIVE' ? '下架' : '上架' }}
            </v-btn>
            <v-btn size="small" color="error" @click="deleteProduct(item)" :disabled="item.pstatus === 'ACTIVE'">刪除</v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>{{ form.pid ? '編輯商品' : '新增商品' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="form.pname" label="商品名稱" :rules="[v => !!v || '必填']" required />
          <v-text-field v-model.number="form.pprice" label="價格" type="number" :rules="[v => v >= 0 || '價格需為正數']" required />

          <v-file-input
            v-model="uploadFile"
            label="商品圖片 (jpg/png)"
            accept="image/png, image/jpeg"
            @change="uploadImage"
            clearable
            show-size
            chips
            dropzone
          />
          <v-img v-if="form.pimage" :src="IMAGE_BASE + form.pimage" class="mb-2" height="120" cover />

          <v-select v-model="form.pstatus" label="狀態" :items="['ACTIVE', 'INACTIVE']" required />
          <v-select v-model="form.categoryId" :items="categories" item-title="name" item-value="id" label="分類" required />
          <v-combobox v-model="form.ptags" :items="tagOptions.map(t => t.name)" multiple chips label="標籤" clearable hide-selected />
          <v-textarea v-model="form.pdescription" label="商品描述" rows="3" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" @click="saveProduct">儲存</v-btn>
          <v-btn text @click="dialog = false">取消</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const API_BASE = 'http://localhost:8080'
const IMAGE_BASE = API_BASE + '/uploads/'

const products = ref([])
const selected = ref([])
const search = ref('')
const statusFilter = ref('')
const dialog = ref(false)
const form = ref({})
const uploadFile = ref(null)
const categories = ref([])
const tagOptions = ref([])

const headers = [
  { title: '圖片', key: 'pimage' },
  { title: '名稱', key: 'pname' },
  { title: '價格', key: 'pprice' },
  { title: '狀態', key: 'pstatus' },
  { title: '上架時間', key: 'plistedAt' },
  { title: '最後更新', key: 'pupdatedAt' },
  { title: '分類', key: 'category' },
  { title: '標籤', key: 'ptags' },
  { title: '操作', key: 'actions', sortable: false }
]

const fetchProducts = async () => {
  const params = new URLSearchParams({ page: 0, size: 9999 })
  if (search.value) params.append('search', search.value)
  if (statusFilter.value) params.append('status', statusFilter.value)
  const res = await fetch(`${API_BASE}/api/product?${params}`)
  const data = await res.json()
  products.value = data.products || []
  selected.value = []
}

const fetchCategories = async () => {
  const res = await fetch(`${API_BASE}/api/categories`)
  categories.value = await res.json()
}

const fetchTags = async () => {
  const res = await fetch(`${API_BASE}/api/tags`)
  tagOptions.value = await res.json()
}

const openForm = (product = null) => {
  form.value = product
    ? { ...product, ptags: product.ptags?.split(',') || [], categoryId: product.categoryId || null }
    : { pname: '', pprice: 0, pimage: '', pstatus: 'INACTIVE', pdescription: '', ptags: [], categoryId: null }
  uploadFile.value = null
  dialog.value = true
}

const uploadImage = async () => {
  if (!uploadFile.value) return
  const formData = new FormData()
  formData.append('file', uploadFile.value)
  const res = await fetch(`${API_BASE}/api/product/upload-image`, {
    method: 'POST',
    body: formData
  })
  const filename = await res.text()
  form.value.pimage = filename
}

const saveProduct = async () => {
  const method = form.value.pid ? 'PUT' : 'POST'
  const url = form.value.pid ? `${API_BASE}/api/product/${form.value.pid}` : `${API_BASE}/api/product`
  const payload = { ...form.value, ptags: Array.isArray(form.value.ptags) ? form.value.ptags.join(',') : '' }
  await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
  dialog.value = false
  fetchProducts()
}

const toggleStatus = async (product) => {
  const updated = { ...product, pstatus: product.pstatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' }
  await fetch(`${API_BASE}/api/product/${product.pid}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updated)
  })
  fetchProducts()
}

const deleteProduct = async (product) => {
  if (product.pstatus === 'ACTIVE') return alert('❌ 無法刪除上架中的商品！')
  if (confirm(`確定要刪除「${product.pname}」嗎？`)) {
    await fetch(`${API_BASE}/api/product/${product.pid}`, { method: 'DELETE' })
    fetchProducts()
  }
}

const batchUpdateStatus = async (status) => {
  const updates = selected.value.map(pid => {
    const product = products.value.find(p => p.pid === pid)
    if (product && product.pstatus !== status) {
      return fetch(`${API_BASE}/api/product/${product.pid}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...product, pstatus: status })
      })
    }
  }).filter(Boolean)
  await Promise.all(updates)
  fetchProducts()
}

const formatDate = (d) => new Date(d).toLocaleString()

onMounted(() => {
  fetchProducts()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
