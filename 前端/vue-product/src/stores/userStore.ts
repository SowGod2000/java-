// src/stores/userStore.ts
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
  }),
  actions: {
    setUser(userData) {
      this.user = userData
    },
    clearUser() {
      this.user = null
    },
    async initUserFromApi() {
      try {
        const res = await fetch('http://localhost:8080/Member/profile', {
          method: 'GET',
          credentials: 'include',
        })

        if (res.ok) {
          const data = await res.json()
          this.setUser(data)
           console.log(data);
          
        } else {
          this.clearUser()
          console.warn('未登入或 token 無效')
        }
      } catch (err) {
        console.error('取得使用者資料失敗', err)
        this.clearUser()
      }
    },
  },
})
