import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: sessionStorage.getItem('userId') || '',
    username: sessionStorage.getItem('username') || '',
    realName: sessionStorage.getItem('realName') || '',
    role: sessionStorage.getItem('role') || ''
  }),
  actions: {
    setUser(user) {
      const role = user.username === 'admin' ? 'SUPER_ADMIN' : (user.role || 'ADMIN')
      this.userId = user.id
      this.username = user.username
      this.realName = user.realName
      this.role = role
      sessionStorage.setItem('userId', user.id)
      sessionStorage.setItem('username', user.username)
      sessionStorage.setItem('realName', user.realName)
      sessionStorage.setItem('role', role)
    },
    logout() {
      this.userId = ''
      this.username = ''
      this.realName = ''
      this.role = ''
      sessionStorage.clear()
    }
  }
})
