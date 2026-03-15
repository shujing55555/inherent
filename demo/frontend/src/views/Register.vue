<template>
  <div class="register-page">
    <h2 class="form-title">用户注册</h2>
    <p class="form-desc">注册时需填写账号、密码、邮箱、手机号和验证码</p>
    <form @submit.prevent="submit" class="auth-form">
      <div class="form-item">
        <label>账号 <span class="required">*</span></label>
        <input
          v-model="form.username"
          type="text"
          placeholder="4-10 位，字母开头，仅字母和数字"
          autocomplete="username"
          required
        />
      </div>
      <div class="form-item">
        <label>密码 <span class="required">*</span></label>
        <input
          v-model="form.password"
          type="password"
          placeholder="3-12 位字符"
          autocomplete="new-password"
          required
        />
      </div>
      <div class="form-item">
        <label>邮箱 <span class="required">*</span></label>
        <input
          v-model="form.email"
          type="email"
          placeholder="请输入邮箱"
          autocomplete="email"
          required
        />
      </div>
      <div class="form-item">
        <label>手机号 <span class="required">*</span></label>
        <input
          v-model="form.phone"
          type="tel"
          placeholder="11 位手机号"
          maxlength="11"
          required
        />
      </div>
      <div class="form-item">
        <label>验证码 <span class="required">*</span></label>
        <input
          v-model="form.verifyCode"
          type="text"
          placeholder="请输入验证码"
          required
        />
      </div>
      <div class="form-item">
        <label>昵称</label>
        <input
          v-model="form.nickname"
          type="text"
          placeholder="选填，默认使用账号"
          autocomplete="nickname"
        />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
        {{ loading ? '注册中...' : '注 册' }}
      </button>
    </form>
    <p class="tip">
      已有账号？
      <router-link to="/login">去登录</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  username: '',
  password: '',
  email: '',
  phone: '',
  verifyCode: '',
  nickname: '',
})
const err = ref('')
const loading = ref(false)

async function submit() {
  err.value = ''
  loading.value = true
  try {
    // 前端简单校验
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
    const phoneReg = /^\d{11}$/
    if (!phoneReg.test(form.phone)) {
      err.value = '手机号必须为11位数字'
      loading.value = false
      return
    }
    if (!form.verifyCode) {
      err.value = '请输入验证码'
      loading.value = false
      return
    }

    const res = await register(form)
    if (res.success && res.data) {
      auth.setAuth(res.data)
      router.push('/')
    }
  } catch (e) {
    err.value = e.response?.data?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
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
.required {
  color: #c41e3a;
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
</style>
