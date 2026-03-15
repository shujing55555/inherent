/**
 * Axios 封装：请求头携带 Token、401 时清除登录并跳转
 */
import axios from 'axios'

const request = axios.create({
  baseURL: '',
  timeout: 15000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (err) => Promise.reject(err)
)

request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('user')
      if (!err.config.url.includes('/auth/')) {
        const isAdmin = window.location.pathname.startsWith('/admin')
        window.location.href = isAdmin ? '/login_admin' : '/login'
      }
    }
    return Promise.reject(err)
  }
)

export default request
