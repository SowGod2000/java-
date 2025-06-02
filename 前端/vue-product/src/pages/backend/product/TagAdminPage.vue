<template>
  <v-container class="py-5">
    <v-card>
      <v-card-title>
        標籤管理
        <v-spacer />
        <v-btn color="primary" @click="openForm()"> 新增標籤</v-btn>
      </v-card-title>

      <v-card-text>
        <v-data-table :headers="headers" :items="tags" item-value="id" class="elevation-1">
          <template #item.actions="{ item }">
            <v-btn size="small" color="info" @click="openForm(item)">編輯</v-btn>
            <v-btn size="small" color="error" @click="deleteTag(item)">刪除</v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <v-dialog v-model="dialog" max-width="400px">
      <v-card>
        <v-card-title>{{ form.id ? '編輯標籤' : '新增標籤' }}</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="form.name"
            label="標籤名稱"
            :rules="[v => !!v || '標籤名稱為必填']"
            required
            clearable
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" @click="saveTag">儲存</v-btn>
          <v-btn text @click="dialog = false">取消</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const tags = ref([])
const dialog = ref(false)
const form = ref({})
const headers = [
  { title: 'ID', key: 'id' },
  { title: '標籤名稱', key: 'name' },
  { title: '操作', key: 'actions', sortable: false }
]

const fetchTags = async () => {
  const res = await fetch('http://localhost:8080/api/tags')
  if (res.ok) {
    tags.value = await res.json()
  } else {
    alert('❌ 取得標籤失敗')
  }
}

const saveTag = async () => {
  if (!form.value.name?.trim()) return alert('請輸入標籤名稱')

  const method = form.value.id ? 'PUT' : 'POST'
  const url = form.value.id
    ? `http://localhost:8080/api/tags/${form.value.id}`
    : `http://localhost:8080/api/tags`

  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: form.value.name })
  })

  if (!res.ok) {
    const msg = await res.text()
    return alert(`❌ 儲存失敗：${msg}`)
  }

  dialog.value = false
  fetchTags()
}

const deleteTag = async (tag) => {
  if (confirm(`確定要刪除「${tag.name}」標籤？`)) {
    await fetch(`http://localhost:8080/api/tags/${tag.id}`, { method: 'DELETE' })
    fetchTags()
  }
}

const openForm = (tag = null) => {
  form.value = tag ? { ...tag } : { name: '' }
  dialog.value = true
}

onMounted(fetchTags)
</script>
