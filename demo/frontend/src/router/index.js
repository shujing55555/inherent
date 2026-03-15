/**
 * 路由配置：先登录才能访问除登录/注册外的所有前台页面
 */
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 登录、注册：使用独立布局，无需登录
  {
    path: '/login',
    component: () => import('../layouts/AuthLayout.vue'),
    children: [
      { path: '', name: 'Login', component: () => import('../views/Login.vue'), meta: { title: '登录', public: true } },
    ],
  },
  {
    path: '/register',
    component: () => import('../layouts/AuthLayout.vue'),
    children: [
      { path: '', name: 'Register', component: () => import('../views/Register.vue'), meta: { title: '注册', public: true } },
    ],
  },
  {
    path: '/login_admin',
    component: () => import('../layouts/AuthLayout.vue'),
    children: [
      { path: '', name: 'AdminLogin', component: () => import('../views/admin/AdminLogin.vue'), meta: { title: '管理员登录', public: true } },
    ],
  },
  // 以下所有页面均需登录后访问
  {
    path: '/',
    component: () => import('../layouts/FrontLayout.vue'),
    meta: { requireAuth: true },
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue'), meta: { title: '首页', requireAuth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '个人信息', requireAuth: true } },
      { path: 'favorites', name: 'FavoriteProjects', component: () => import('../views/FavoriteProjects.vue'), meta: { title: '我的收藏', requireAuth: true } },
      { path: 'projects', name: 'Projects', component: () => import('../views/Projects.vue'), meta: { title: '非遗项目', requireAuth: true } },
      { path: 'projects/:id', name: 'ProjectDetail', component: () => import('../views/ProjectDetail.vue'), meta: { title: '项目详情', requireAuth: true } },
      { path: 'inheritors', name: 'Inheritors', component: () => import('../views/Inheritors.vue'), meta: { title: '非遗传承人', requireAuth: true } },
      { path: 'inheritors/:id', name: 'InheritorDetail', component: () => import('../views/InheritorDetail.vue'), meta: { title: '传承人详情', requireAuth: true } },
      { path: 'news', name: 'News', component: () => import('../views/News.vue'), meta: { title: '非遗资讯', requireAuth: true } },
      { path: 'news/:id', name: 'NewsDetail', component: () => import('../views/NewsDetail.vue'), meta: { title: '资讯详情', requireAuth: true } },
      { path: 'activities', name: 'Activities', component: () => import('../views/Activities.vue'), meta: { title: '非遗活动', requireAuth: true } },
      { path: 'activities/:id', name: 'ActivityDetail', component: () => import('../views/ActivityDetail.vue'), meta: { title: '活动详情', requireAuth: true } },
      { path: 'stories', name: 'Stories', component: () => import('../views/Stories.vue'), meta: { title: '传承故事', requireAuth: true } },
      { path: 'stories/:id', name: 'StoryDetail', component: () => import('../views/StoryDetail.vue'), meta: { title: '故事详情', requireAuth: true } },
      { path: 'ai', name: 'AiConsult', component: () => import('../views/AiConsult.vue'), meta: { title: 'AI 咨询', requireAuth: true } },
    ],
  },
  // 后台
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '后台首页', requireAdmin: true } },
      { path: 'projects', name: 'AdminProjects', component: () => import('../views/admin/ProjectManage.vue'), meta: { requireAdmin: true } },
      { path: 'inheritors', name: 'AdminInheritors', component: () => import('../views/admin/InheritorManage.vue'), meta: { requireAdmin: true } },
      { path: 'news', name: 'AdminNews', component: () => import('../views/admin/NewsManage.vue'), meta: { requireAdmin: true } },
      { path: 'activities', name: 'AdminActivities', component: () => import('../views/admin/ActivityManage.vue'), meta: { requireAdmin: true } },
      { path: 'stories', name: 'AdminStories', component: () => import('../views/admin/StoryManage.vue'), meta: { requireAdmin: true } },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/UserManage.vue'), meta: { requireAdmin: true } },
      { path: 'recommend-weights', name: 'AdminRecommendWeights', component: () => import('../views/admin/RecommendWeight.vue'), meta: { requireAdmin: true } },
    ],
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 需登录的前台页面：无 token 则跳转登录，并记录目标路径以便登录后跳回
  if (to.meta.requireAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 已登录用户访问登录/注册页时，直接进入首页
  if ((to.name === 'Login' || to.name === 'Register') && token && role === 'USER') {
    next({ path: '/' })
    return
  }

  // 后台：仅管理员可访问
  if (to.meta.requireAdmin) {
    if (!token || role !== 'ADMIN') {
      next({ name: 'AdminLogin' })
      return
    }
  }

  next()
})

export default router
