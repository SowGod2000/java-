<template>
  <v-app>
  

    <v-main>
      <v-container class="fill-height d-flex align-center justify-center">
        <v-card class="pa-6" max-width="1000" width="100%">
          <h2 class="text-h5 mb-6 text-center">餐廳每日訂單量統計</h2>
          <div style="position: relative; height: 400px;">
            <canvas id="orderChart"></canvas>
          </div>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { onMounted } from 'vue'
import axios from 'axios'
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  Title,
  CategoryScale,
  Tooltip,
  Legend,
} from 'chart.js'

// 註冊元件
Chart.register(LineController, LineElement, PointElement, LinearScale, Title, CategoryScale, Tooltip, Legend)

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/firstHotel/restaurant/orders-per-day')
    const data = res.data

    const ctx = document.getElementById('orderChart')

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.map(item => item.orderdate),
        datasets: [
          {
            label: '訂單數量',
            data: data.map(item => item.count),
            fill: false,
            borderColor: 'blue',
            backgroundColor: 'blue',
            tension: 0.3,
            pointBackgroundColor: 'white',
            pointBorderColor: 'blue',
            pointRadius: 4,
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false, // 讓 chart 撐滿 div
        plugins: {
          legend: {
            position: 'top'
          },
          title: {
            display: true,
            text: '每日訂單數量折線圖'
          }
        },
        scales: {
          y: {
            beginAtZero: true, //  y軸從0開始
            ticks: {
              precision: 0 //  不要小數點
            }
          }
        }
      }
    })
  } catch (error) {
    console.error('抓資料失敗:', error)
  }
})
</script>