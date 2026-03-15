<template>
  <!-- 登录/注册专用布局：仅展示 Logo 与表单区，无顶部导航 -->
  <div class="auth-layout" :style="authBgStyle">
    <div class="auth-card">
      <router-link to="/login" class="logo">丽水市非物质文化遗产平台</router-link>
      <router-view />
    </div>
    <footer class="auth-footer">丽水市非物质文化遗产平台 © 2026</footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 登录/注册背景图路径（由后端提供），你只需要保证该路径存在即可
const authBgStyle = computed(() => {
  const base = import.meta.env.VITE_API_BASE_URL || ''
  const path = '/pictures/登录注册.jpg' // 这里改成你实际的图片文件名
  const url = base ? base.replace(/\/$/, '') + path : path
  return {
    backgroundImage: `url('${url}')`,
    backgroundSize: 'cover',
    backgroundPosition: 'center center',
    backgroundRepeat: 'no-repeat',
  }
})
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 24px;
}
.auth-card {
  width: 100%;
  max-width: 460px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 32px 32px 36px;
}
.logo {
  display: block;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #c41e3a;
  text-decoration: none;
  margin-bottom: 6px;
}
.logo:hover {
  color: #a01830;
}
.slogan {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-bottom: 28px;
}
.auth-footer {
  margin-top: 24px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}
</style>
