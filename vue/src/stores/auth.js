import { defineStore } from 'pinia'

const TOKEN_KEY = 'library_token'
const USER_KEY = 'library_user'

function parseUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw)
  } catch (_) {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: parseUser()
  }),
  getters: {
    isAdmin: (state) => Number(state.user?.role) >= 1,
    username: (state) => state.user?.username || ''
  },
  actions: {
    setAuth(user) {
      this.user = user
      this.token = user?.token || ''
      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(USER_KEY, JSON.stringify(user || null))
    },
    logout() {
      this.user = null
      this.token = ''
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})

