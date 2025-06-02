<template>
    <v-container>
        <v-row>
            <v-col cols="12" md="2" style="margin: auto;margin-top: 100px;">
                <v-text-field v-model="selectedDate" label="選擇日期" type="date" @change="fetchChartData" />
            </v-col>
        </v-row>

        <div style="max-width: 1000px; height: 500px; margin: auto;">
            <Bar v-if="chartData" :data="chartData" :options="chartOptions" />
        </div>

    </v-container>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { Bar } from 'vue-chartjs'
import {
    Chart as ChartJS,
    Title,
    Tooltip,
    Legend,
    BarElement,
    CategoryScale,
    LinearScale,
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const selectedDate = ref(new Date().toISOString().substring(0, 10))
const chartData = ref(null)

const chartOptions = {
    responsive: true,
    plugins: {
        legend: { display: false },
        title: {
            display: true,
            text: '各房型入住率',
            font: {
                size: 20 // 標題字體
            }
        }
    },
    scales: {
        x: {
            ticks: {
                font: {
                    size: 14 // x軸標籤
                }
            }
        },
        y: {
            beginAtZero: true,
            max: 1,
            ticks: {
                callback: (val) => `${(val * 100).toFixed(0)}%`,
                font: {
                    size: 14 // y軸標籤
                }
            }
        }
    }
}


async function fetchChartData() {
    // 轉成 Date 物件
    const date = new Date(selectedDate.value)

    // 加一天
    const nextDate = new Date(date)
    nextDate.setDate(date.getDate() + 1)

    // 格式化成 yyyy-MM-dd
    const formatDate = (d) => d.toISOString().substring(0, 10)

    const startDateStr = formatDate(date)
    const endDateStr = formatDate(nextDate)

    const url = `http://localhost:8080/findRoom/${startDateStr}/${endDateStr}`

    const res = await axios.get(url)
    const data = res.data

    console.log(data);


    chartData.value = {
        labels: data.map(item => item.roomtypeName),
        datasets: [
            {
                label: '入住率',
                backgroundColor: '#42A5F5',
                data: data.map(item => (item.orderedRooms / item.totalRooms))
            }
        ]
    }
}


// 初次載入資料
fetchChartData()
</script>