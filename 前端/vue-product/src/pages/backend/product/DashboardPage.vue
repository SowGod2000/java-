<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h5 font-weight-bold mb-4">商城銷售儀表板</h1>
      </v-col>

      <!-- 數值卡片 -->
      <v-col cols="12" md="4" v-for="card in cards" :key="card.title">
        <v-card class="elevation-2">
          <v-card-title class="d-flex align-center justify-space-between">
            <span class="font-weight-medium">{{ card.title }}</span>
            <v-icon :color="card.color">{{ card.icon }}</v-icon>
          </v-card-title>
          <v-card-text class="text-h6" :class="card.colorClass">
            {{ card.prefix }}{{ format(card.value) }}{{ card.suffix }}
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 每月銷售折線圖 -->
      <v-col cols="12" md="6">
        <v-card class="elevation-2">
          <v-card-title class="font-weight-medium d-flex justify-space-between">
            <span> 每月銷售趨勢</span>
            <v-btn small @click="exportMonthlyCsv" prepend-icon="mdi-download">匯出 CSV</v-btn>
          </v-card-title>
          <v-card-text style="height: 400px">
            <canvas ref="salesCanvas"></canvas>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 分類銷售圓餅圖 -->
      <v-col cols="12" md="6">
        <v-card class="elevation-2">
          <v-card-title class="font-weight-medium d-flex justify-space-between">
            <span> 分類銷售分佈</span>
            <v-btn small @click="exportCategoryCsv" prepend-icon="mdi-download">匯出 CSV</v-btn>
          </v-card-title>
          <v-card-text style="height: 400px">
            <canvas ref="categoryCanvas"></canvas>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 標籤銷售圓餅圖 -->
      <v-col cols="12" md="6">
        <v-card class="elevation-2">
          <v-card-title class="font-weight-medium d-flex justify-space-between">
            <span> 標籤銷售分佈</span>
            <v-btn small @click="exportTagCsv" prepend-icon="mdi-download">匯出 CSV</v-btn>
          </v-card-title>
          <v-card-text style="height: 400px">
            <canvas ref="tagCanvas"></canvas>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import Chart from 'chart.js/auto'

const summary = ref({ todayTotal: 0, thisMonthOrders: 0, totalRevenue: 0 })
const cards = ref([])
const salesCanvas = ref(null)
const categoryCanvas = ref(null)
const tagCanvas = ref(null)
const categoryData = ref([])
const tagData = ref([])
const monthlyData = ref([])

const format = (val) => Number(val || 0).toLocaleString()

const getTimestampFilename = (prefix) => {
  const now = new Date()
  const timestamp = now.toISOString().replace(/[-:]/g, '').split('.')[0]
  return `${prefix}-${timestamp}.csv`
}

const downloadCsv = (csvContent, filename) => {
  // 加上 BOM：\uFEFF
  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}


//  匯出分類銷售 CSV
const exportCategoryCsv = () => {
  if (!categoryData.value.length) {
    alert('目前沒有分類銷售資料可匯出。')
    return
  }

  const headers = ['分類名稱', '銷售總金額']
  const rows = categoryData.value.map(row => [row.category, row.total])
  const csv = [headers, ...rows].map(e => e.join(',')).join('\n')
  downloadCsv(csv, getTimestampFilename('category-sales'))
}

//  匯出標籤銷售 CSV
const exportTagCsv = () => {
  if (!tagData.value.length) {
    alert('目前沒有標籤銷售資料可匯出。')
    return
  }

  const headers = ['標籤名稱', '銷售總金額']
  const rows = tagData.value.map(row => [row.tag, row.total])
  const csv = [headers, ...rows].map(e => e.join(',')).join('\n')
  downloadCsv(csv, getTimestampFilename('tag-sales'))
}

// 📊 匯出每月銷售 CSV
const exportMonthlyCsv = () => {
  if (!monthlyData.value.length) {
    alert('目前沒有每月銷售資料可匯出。')
    return
  }

  const headers = ['月份', '銷售總金額']
  const rows = monthlyData.value.map(row => [row.month, row.total])
  const csv = [headers, ...rows].map(e => e.join(',')).join('\n')
  downloadCsv(csv, getTimestampFilename('monthly-sales'))
}

const fetchSummary = async () => {
  const res = await fetch('http://localhost:8080/api/dashboard/summary', {
    headers: { 'Accept': 'application/json' },
    credentials: 'include'
  })
  if (res.ok) {
    const data = await res.json()
    summary.value = data
    cards.value = [
      {
        title: '商城今日總收入 (TWD)', value: data.todayTotal,
        color: 'success', icon: 'mdi-cash-multiple',
        prefix: 'NT$ ', suffix: '', colorClass: 'text-success'
      },
      {
        title: '商城本月訂單數量', value: data.thisMonthOrders,
        color: 'info', icon: 'mdi-cart-check',
        prefix: '', suffix: ' 筆', colorClass: 'text-info'
      },
      {
        title: '本月累積收入', value: data.totalRevenue,
        color: 'primary', icon: 'mdi-bank',
        prefix: 'NT$ ', suffix: '', colorClass: 'text-primary'
      }
    ]
  } else {
    const err = await res.text()
    console.error('❌ summary fetch failed:', err)
  }
}

const fetchChart = async () => {
  const res = await fetch('http://localhost:8080/api/dashboard/monthly-sales', {
    headers: { 'Accept': 'application/json' },
    credentials: 'include'
  })
  if (!res.ok) {
    const err = await res.text()
    console.error('❌ chart fetch failed:', err)
    return
  }
  const data = await res.json()
  monthlyData.value = data
  const labels = data.map(e => e.month)
  const totals = data.map(e => e.total)

  await nextTick()

  new Chart(salesCanvas.value, {
    type: 'line',
    data: {
      labels,
      datasets: [
        {
          label: '月成交金額',
          data: totals,
          borderColor: 'rgba(54, 162, 235, 1)',
          backgroundColor: 'rgba(54, 162, 235, 0.2)',
          fill: true,
          tension: 0.4,
          pointRadius: 5,
          pointHoverRadius: 7
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: 'top' } },
      scales: { y: { beginAtZero: true } }
    }
  })
}

const fetchCategorySales = async () => {
  const res = await fetch('http://localhost:8080/api/dashboard/category-sales', {
    headers: { 'Accept': 'application/json' },
    credentials: 'include'
  })
  if (!res.ok) {
    const err = await res.text()
    console.error('❌ category chart fetch failed:', err)
    return
  }
  categoryData.value = await res.json()

  const labels = categoryData.value.map(e => e.category)
  const totals = categoryData.value.map(e => e.total)

  await nextTick()

  new Chart(categoryCanvas.value, {
    type: 'pie',
    data: {
      labels,
      datasets: [{
        label: '分類銷售佔比',
        data: totals,
        backgroundColor: [
          '#FF6384', '#36A2EB', '#FFCE56', '#66BB6A', '#BA68C8', '#FFA726'
        ]
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: 'right' },
        tooltip: {
          callbacks: {
            label: function (context) {
              const label = context.label || ''
              const value = context.parsed
              return `${label}: NT$ ${value.toLocaleString()}`
            }
          }
        }
      }
    }
  })
}


const fetchTagSales = async () => {
  const res = await fetch('http://localhost:8080/api/dashboard/tag-sales', {
    headers: { 'Accept': 'application/json' },
    credentials: 'include'
  })
  if (!res.ok) {
    const err = await res.text()
    console.error('❌ tag chart fetch failed:', err)
    return
  }
  tagData.value = await res.json()

  const labels = tagData.value.map(e => e.tag)
  const totals = tagData.value.map(e => e.total)

  await nextTick()

  new Chart(tagCanvas.value, {
    type: 'pie',
    data: {
      labels,
      datasets: [{
        label: '標籤銷售佔比',
        data: totals,
        backgroundColor: [
          '#EF5350', '#42A5F5', '#FFCA28', '#66BB6A', '#AB47BC', '#FFA726', '#29B6F6'
        ]
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: 'right' },
        tooltip: {
          callbacks: {
            label: function (context) {
              const label = context.label || ''
              const value = context.parsed
              return `${label}: NT$ ${value.toLocaleString()}`
            }
          }
        }
      }
    }
  })
}

onMounted(() => {
  fetchSummary()
  fetchChart()
  fetchCategorySales()
  fetchTagSales()
})
</script>

 
