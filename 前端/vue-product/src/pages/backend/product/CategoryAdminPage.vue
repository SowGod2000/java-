<template>
  <v-container class="py-5">
    <v-card>
      <v-card-title>
        分類管理
        <v-spacer />
        <v-btn color="primary" @click="openForm()"> 新增分類</v-btn>
      </v-card-title>

      <v-card-text>
        <v-data-table :headers="headers" :items="categories" item-value="id" class="elevation-1">
          <template #item.actions="{ item }">
            <v-btn size="small" color="info" @click="openForm(item)">編輯</v-btn>
            <v-btn size="small" color="error" class="ml-2" @click="deleteCategory(item.id)">刪除</v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <v-dialog v-model="dialog" max-width="400px">
      <v-card>
        <v-card-title>{{ form.id ? '編輯分類' : '新增分類' }}</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="form.name"
            label="分類名稱"
            :rules="[v => !!v || '分類名稱為必填']"
            required
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" @click="saveCategory">儲存</v-btn>
          <v-btn text @click="dialog = false">取消</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const categories = ref([])
const dialog = ref(false)
const form = ref({})

const headers = [
  { title: 'ID', key: 'id' },
  { title: '分類名稱', key: 'name' },
  { title: '操作', key: 'actions', sortable: false }
]

// 載入分類資料
const fetchCategories = async () => {
  const res = await fetch('http://localhost:8080/api/categories')
  categories.value = await res.json()
}

// 開啟表單（新增 or 編輯）
const openForm = (category = null) => {
  form.value = category ? { ...category } : { name: '' }
  dialog.value = true
}

// 儲存分類
const saveCategory = async () => {
  const method = form.value.id ? 'PUT' : 'POST'
  const url = form.value.id
    ? `http://localhost:8080/api/categories/${form.value.id}`
    : `http://localhost:8080/api/categories`

  await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value)
  })

  dialog.value = false
  fetchCategories()
}


// 刪除分類
const deleteCategory = async (id) => {
  if (confirm('確定要刪除這個分類嗎？')) {
    await fetch(`http://localhost:8080/api/categories/${id}`, { method: 'DELETE' })
    fetchCategories()
  }
}

onMounted(fetchCategories)
</script>

<style scoped>
</style>
