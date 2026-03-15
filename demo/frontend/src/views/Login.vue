<template>
  <div class="login-page">
    <h2 class="form-title">用户登录</h2>

    <form @submit.prevent="submit" class="auth-form">
      <div class="form-item">
        <label>账号</label>
        <input
          v-model="form.username"
          type="text"
          placeholder="4-10 位，字母开头，仅字母和数字"
          autocomplete="username"
          required
        />
      </div>
      <div class="form-item">
        <label>密码</label>
        <input
          v-model="form.password"
          type="password"
          placeholder="3-12 位字符"
          autocomplete="current-password"
          required
        />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
        {{ loading ? '登录中...' : '登 录' }}
      </button>
    </form>
    <p class="tip">
      还没有账号？
      <router-link to="/register">立即注册</router-link>
    </p>
    <p class="admin-entry">
      <router-link to="/login_admin">管理员入口 →</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '../api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = reactive({ username: '', password: '' })
const err = ref('')
const loading = ref(false)

async function submit() {
  err.value = ''
  loading.value = true
  try {
    const usernameReg = /^[A-Za-z][A-Za-z0-9]{3,9}$/
    if (!usernameReg.test(form.username)) {
      err.value = '账号需以字母开头，仅包含字母和数字，长度4-10位'
      loading.value = false
      return
    }
    if (!form.password || form.password.length < 3 || form.password.length > 12) {
      err.value = '密码长度需为3-12位'
      loading.value = false
      return
    }

    const res = await login(form)
    if (res.success && res.data) {
      auth.setAuth(res.data)
      router.push(route.query.redirect || '/')
    }
  } catch (e) {
    err.value = e.response?.data?.message || '登录失败，请检查账号密码'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  padding: 0;
}
.form-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 6px;
  text-align: center;
}
.form-desc {
  font-size: 13px;
  color: #888;
  margin-bottom: 24px;
  text-align: center;
}
.auth-form {
  margin-bottom: 16px;
}
.form-item {
  margin-bottom: 18px;
}
.form-item label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}
.form-item input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.2s;
}
.form-item input:focus {
  outline: none;
  border-color: #c41e3a;
}
.err {
  color: #c41e3a;
  font-size: 13px;
  margin-bottom: 12px;
  text-align: center;
}
.btn-block {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 8px;
}
.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.tip {
  text-align: center;
  font-size: 14px;
  color: #666;
}
.tip a {
  color: #c41e3a;
  font-weight: 500;
}
.admin-entry {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  text-align: center;
  font-size: 13px;
  color: #888;
}
.admin-entry a {
  color: #888;
  text-decoration: none;
}
.admin-entry a:hover {
  color: #1a1a2e;
}
</style>
