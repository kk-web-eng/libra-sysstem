import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: sessionStorage.getItem('userId') || '',
    username: sessionStorage.getItem('username') || '',
    realName: sessionStorage.getItem('realName') || ''
  }),
  actions: {
    setUser(user) {
      this.userId = user.id
      this.username = user.username
      this.realName = user.realName
      sessionStorage.setItem('userId', user.id)
      sessionStorage.setItem('username', user.username)
      sessionStorage.setItem('realName', user.realName)
    },
    logout() {
      this.userId = ''
      this.username = ''
      this.realName = ''
      sessionStorage.clear()
    }
  }
})