<template>
  <!-- 后台布局：侧边栏 + 顶栏 + 内容区；未登录时只显示 router-view（登录页） -->
  <div class="admin-layout" v-if="isAdminPage && auth.isAdmin">
    <aside class="sidebar">
      <div class="logo">丽水非遗 · 管理后台</div>
      <nav>
        <router-link to="/admin/dashboard">后台首页</router-link>
        <router-link to="/admin/recommend-weights">推荐分析</router-link>
        <router-link to="/admin/projects">非遗项目</router-link>
        <router-link to="/admin/inheritors">传承人</router-link>
        <router-link to="/admin/news">资讯</router-link>
        <router-link to="/admin/activities">活动</router-link>
        <router-link to="/admin/stories">传承故事</router-link>
        <router-link to="/admin/users">用户管理</router-link>
        <div class="nav-divider"></div>
        <a href="/" target="_blank" class="link-user-site">访问用户端 →</a>
      </nav>
    </aside>
    <div class="body">
      <header class="topbar">
        <div class="admin-info">
          <img
            v-if="avatarSrc"
            :src="avatarSrc"
            alt="头像"
            class="admin-avatar"
          />
          <span>管理员：{{ auth.user?.nickname || auth.user?.username }}</span>
        </div>
        <button class="btn btn-default" @click="logout">退出</button>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
  <router-view v-else />
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const isAdminPage = computed(() => route.path.startsWith('/admin') && route.name !== 'AdminLogin')

const avatarSrc = computed(() => {
  const avatar = auth.user?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  const base = import.meta.env.VITE_API_BASE_URL || ''
  return base ? base.replace(/\/$/, '') + avatar : avatar
})

function logout() {
  auth.clearAuth()
  router.push('/login_admin')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: radial-gradient(circle at top left, #243b53 0, #102a43 40%, #0b1726 100%);
}
.sidebar {
  width: 220px;
  background: rgba(10, 20, 40, 0.92);
  color: #fff;
  padding: 18px 0;
  box-shadow: 2px 0 18px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
}
.sidebar .logo {
  padding: 0 20px 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 18px;
  font-size: 15px;
  letter-spacing: 0.02em;
}
.sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.sidebar nav a {
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.65);
  font-size: 14px;
  text-decoration: none;
  transition: background 0.16s ease, color 0.16s ease, padding-left 0.16s ease;
}
.sidebar nav a:hover,
.sidebar nav a.router-link-active {
  color: #fff;
  background: linear-gradient(90deg, rgba(196, 30, 58, 0.85), rgba(255, 111, 97, 0.7));
  padding-left: 24px;
}
.nav-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  margin: 14px 20px;
}
.link-user-site {
  padding: 10px 20px;
  color: #8fd3ff;
  font-size: 13px;
  text-decoration: none;
  display: block;
}
.link-user-site:hover {
  color: #fff;
  background: rgba(15, 52, 96, 0.9);
}
.body {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.topbar {
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.98);
  border-bottom: 1px solid #e5e9f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.12);
}
.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #1f2933;
}
.admin-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e9f0;
  background: #f5f7fa;
}
.content {
  flex: 1;
  padding: 24px;
  background: radial-gradient(circle at top right, #fdf2f2 0, #f5f7fa 40%, #edf2f7 100%);
}
.content > *:first-child {
  margin-top: 0;
}
.content h2 {
  font-size: 20px;
  margin-bottom: 16px;
  color: #1f2933;
}
</style>
