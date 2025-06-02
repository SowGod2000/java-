<template>
    <div class="google-signin-container">
      <!-- Google 登入元件 -->
      <div id="g_id_onload" data-client_id="1029165202290-cgs66m0tfoktoc3q8uftmucr1ubjeauc.apps.googleusercontent.com"
           data-callback="handleCredentialResponse">
      </div>
      <!-- Element Plus 按鈕，顯示 Google 登入 -->
      <el-button class="g_id_signin" type="primary" size="large" @click="handleGoogleLogin">
        使用 Google 登入
      </el-button>
    </div>
  </template>
  
  <script>
  export default {
    mounted() {
      // 當元件掛載時動態加載 Google 的登入 SDK
      const script = document.createElement('script');
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.onload = () => {
        // 初始化 Google Sign-In 按鈕
        window.google.accounts.id.initialize({
          client_id: '1029165202290-cgs66m0tfoktoc3q8uftmucr1ubjeauc.apps.googleusercontent.com',
          callback: this.handleCredentialResponse,
        });
        window.google.accounts.id.renderButton(
          document.querySelector('.g_id_signin'),
          { theme: 'outline', size: 'large' } // 可以設定按鈕外觀
        );
      };
      document.head.appendChild(script);
    },
    
    methods: {
      async handleCredentialResponse(response) {
        const token = response.credential;
  
        // 將 token 發送給 Spring Boot 後端
        try {
          const res = await fetch("http://localhost:8080/Member/google/callback", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              credential: token,
            }),
          });
  
          const data = await res.json();
          console.log("後端回應：", data);
  
          if (data.success) {
            
            this.$router.push('/'); // 登入後導向會員頁面
          } else {
            this.$message.error('登入失敗，請重試');
          }
        } catch (err) {
          console.error('Google 登入處理錯誤：', err);
          this.$message.error('登入時發生錯誤，請稍後再試');
        }
      },
  
      handleGoogleLogin() {
        // 如果需要，可以在這裡額外處理一些 Google 登入按鈕的交互效果
        console.log('Google 登入按鈕被點擊');
      }
    },
  };
  </script>
  
  <style scoped>
  .google-signin-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
  
  .g_id_signin {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  </style>
  