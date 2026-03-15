/**
 * 认证状态：token、角色、用户信息
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')

  function setAuth(data) {
    token.value = data.token
    role.value = data.role
    user.value = { userId: data.userId, username: data.username, nickname: data.nickname, avatar: data.avatar }
    localStorage.setItem('token', data.token)
    localStorage.setItem('role', data.role)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function clearAuth() {
    token.value = ''
    role.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('user')
  }

  /** 更新当前用户信息（修改个人信息后调用，并同步到 localStorage） */
  function setUser(data) {
    if (!data) return
    const next = { ...user.value, ...data }
    if (next.id != null && next.userId == null) next.userId = next.id
    user.value = next
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      const res = await getCurrentUser()
      if (res.success && res.data) user.value = res.data
    } catch (_) {
      clearAuth()
    }
  }

  return { token, role, user, isLoggedIn, isAdmin, setAuth, clearAuth, setUser, fetchUser }
})
