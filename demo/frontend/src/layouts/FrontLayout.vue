<template>
  <!-- 前台布局：顶部导航 + 主内容区 -->
  <div class="front-layout">
    <header class="header">
      <div class="container header-inner">
        <router-link to="/" class="logo">丽水非遗</router-link>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/projects">非遗项目</router-link>
          <router-link to="/inheritors">传承人</router-link>
          <router-link to="/news">非遗资讯</router-link>
          <router-link to="/activities">非遗活动</router-link>
          <router-link to="/stories">传承故事</router-link>
          <router-link to="/ai">AI 咨询</router-link>
        </nav>
        <div class="user-area">
          <template v-if="auth.isLoggedIn">
            <div class="user-info">
              <img
                v-if="avatarSrc"
                :src="avatarSrc"
                alt="头像"
                class="user-avatar"
              />
              <span class="nickname">{{ auth.user?.nickname || auth.user?.username }}</span>
            </div>
            <router-link to="/profile" class="link-profile">个人信息</router-link>
            <router-link to="/favorites" class="link-fav">我的收藏</router-link>
            <router-link v-if="auth.isAdmin" to="/admin" class="link-admin">进入后台管理</router-link>
            <button class="btn btn-default btn-logout" @click="logout">退出</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-default">登录</router-link>
            <router-link to="/register" class="btn btn-primary">注册</router-link>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="container">丽水市非物质文化遗产平台 © 2026</div>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const avatarSrc = computed(() => {
  const avatar = auth.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  const base = import.meta.env.VITE_API_BASE_URL || ''
  return base ? base.replace(/\/$/, '') + avatar : avatar
})

onMounted(() => {
  if (auth.isLoggedIn && !auth.user) auth.fetchUser()
})

function logout() {
  auth.clearAuth()
  router.push('/login')
}
</script>

<style scoped>
.front-layout { min-height: 100vh; display: flex; flex-direction: column; }
.header { background: #c41e3a; color: #fff; padding: 12px 0; }
.header-inner { display: flex; align-items: center; justify-content: space-between; }
.logo { font-size: 20px; font-weight: bold; color: #fff; }
.nav { display: flex; gap: 24px; }
.nav a { color: rgba(255,255,255,0.9); }
.nav a.router-link-active { color: #fff; font-weight: bold; }
.user-area { display: flex; align-items: center; gap: 12px; }
.user-info { display: flex; align-items: center; gap: 8px; }
.user-avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; border: 1px solid rgba(255,255,255,0.7); background: rgba(255,255,255,0.2); }
.user-area .nickname { color: #fff; }
.user-area .link-profile { color: rgba(255,255,255,0.9); text-decoration: none; font-size: 14px; }
.user-area .link-profile:hover { color: #fff; text-decoration: underline; }
.user-area .link-fav { color: rgba(255,255,255,0.9); text-decoration: none; font-size: 14px; }
.user-area .link-fav:hover { color: #fff; text-decoration: underline; }
.user-area .link-admin { color: rgba(255,255,255,0.95); text-decoration: none; font-size: 14px; margin-right: 4px; }
.user-area .link-admin:hover { color: #fff; text-decoration: underline; }
.user-area .btn { padding: 6px 12px; }
.btn-logout { margin-left: 4px; }
.main { flex: 1; padding: 24px 0; }
.footer { background: #333; color: #999; padding: 16px; text-align: center; font-size: 13px; }
</style>
