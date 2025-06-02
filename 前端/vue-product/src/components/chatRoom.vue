<template>
    <div class="chat-container">
      <h1>First Hotel 客服</h1>
  
      <div class="chat-window">
        <div v-for="(msg, index) in messages" :key="index" :class="msg.type">
          <span v-if="msg.type === 'system'">{{ msg.text }}</span>
          <span v-else>
            <strong>{{ msg.sender }}：</strong> {{ msg.text }}
          </span>
        </div>
      </div>
  
      <div class="chat-controls">
        <input v-model="inputMsg" @keydown.enter="sendMessage" placeholder="輸入訊息..." />
        <button @click="connect">連線</button>
        <button @click="disconnect">斷線</button>
        <button @click="sendMessage">送出訊息</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const ws = ref(null)
  const messages = ref([])
  const inputMsg = ref('')
  const myId = ref('') // 自己的 ID
  
  function connect() {
    ws.value = new WebSocket('ws://localhost:8081')
  
    ws.value.onopen = () => {
      console.log('[open connection]')
    }
  
    ws.value.onmessage = (event) => {
      console.log('[Message from server]:', event.data)
      const [sender, text] = event.data.split(': ')
  
      if (sender === '[System]') {
        messages.value.push({ type: 'system', text: text })
      } else {
        // 判斷是不是自己
        const isSelf = sender === myId.value
        messages.value.push({ type: isSelf ? 'self' : 'other', sender, text })
      }
    }
  
    ws.value.onclose = () => {
      console.log('[close connection]')
    }
  
    // 等連上後從伺服器拿 id
    ws.value.addEventListener('message', (event) => {
      if (event.data.includes('is connected')) {
        const match = event.data.match(/\[Client (\w+) is connected\]/)
        if (match) {
          myId.value = match[1]
        }
      }
    })
  }
  
  function sendMessage() {
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(inputMsg.value)
      inputMsg.value = ''
    }
  }
  
  function disconnect() {
    if (ws.value) {
      ws.value.close()
    }
  }
  </script>
  
  <style scoped>
  .chat-container {
    max-width: 600px;
    margin: auto;
    padding: 20px;
  }
  
  .chat-window {
    height: 400px;
    overflow-y: auto;
    border: 1px solid #ccc;
    padding: 10px;
    margin-bottom: 10px;
  }
  
  .chat-controls {
    display: flex;
    gap: 10px;
  }
  
  .self {
    text-align: right;
    color: green;
  }
  
  .other {
    text-align: left;
    color: blue;
  }
  
  .system {
    text-align: center;
    color: gray;
  }
  </style>