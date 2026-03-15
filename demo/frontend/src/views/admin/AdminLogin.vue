<template>
  <div class="admin-login-page">
    <h2 class="form-title">管理员登录</h2>
    <p class="form-desc">请使用管理员账号登录后台</p>
    <form @submit.prevent="submit" class="auth-form">
      <div class="form-item">
        <label>账号</label>
        <input v-model="form.username" type="text" placeholder="管理员账号" required />
      </div>
      <div class="form-item">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="密码" required />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <button type="submit" class="btn btn-primary btn-block">登 录</button>
    </form>
    <p class="tip tip-link"><router-link to="/login">返回用户登录</router-link></p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '../../api'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ username: '', password: '' })
const err = ref('')

async function submit() {
  err.value = ''
  try {
    const res = await adminLogin(form)
    if (res.success && res.data) {
      auth.setAuth(res.data)
      router.push('/admin/dashboard')
    }
  } catch (e) {
    err.value = e.response?.data?.message || '登录失败'
  }
}
</script>

<style scoped>
.admin-login-page {
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
  margin-bottom: 16px;
}
.form-item label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #4a5568;
}
.form-item input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.form-item input:focus {
  outline: none;
  border-color: #c41e3a;
  box-shadow: 0 0 0 1px rgba(196, 30, 58, 0.35);
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
.tip-link {
  margin-top: 18px;
  margin-bottom: 0;
  text-align: center;
}
.tip-link a {
  color: #c41e3a;
  font-size: 13px;
  text-decoration: none;
}
</style>
