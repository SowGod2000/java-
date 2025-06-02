<!-- FirebaseOtpVerify.vue -->
<template>
    <div style="margin-top: 60px">
      <el-input v-model="otp" placeholder="請輸入驗證碼" />
      <el-button @click="verifyOtp">驗證並登入</el-button>
    </div>
  </template>
  
  <script>
  import { auth } from "../member/firebase";
  
  export default {
    data() {
      return {
        otp: "",
      };
    },
    mounted() {
      const token = localStorage.getItem('jwt') // 這是你後端給的 JWT
        fetch("http://localhost:8080/Member/firebase-send-otp", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`, // 用 Authorization header 傳
          }
        })
        .then((res) => res.json())
        .then((data) => {
          console.log("已請求發送簡訊 OTP", data);
        })
        .catch((err) => {
          console.error("發送 OTP 失敗", err);
        });
    },
    methods: {
      verifyOtp() {
        auth.currentUser.getIdToken().then((idToken) => {
          fetch("http://localhost:8080/Member/firebase-otp-verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              token: idToken,
              otp: this.otp,
            }),
          })
            .then((res) => res.json())
            .then((data) => {
              console.log("後端回傳 JWT:", data.token);
              // 儲存 JWT，例如 localStorage.setItem('token', data.token)
            })
            .catch((err) => {
              console.error("OTP 驗證失敗", err);
            });
        });
      },
    },
  };
  </script>
  