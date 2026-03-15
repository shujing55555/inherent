/**
 * 前端 API：封装所有后端请求
 */
import request from '../utils/request'

export function register(data) {
  return request.post('/api/auth/register', data)
}
export function login(data) {
  return request.post('/api/auth/login', data)
}
export function adminLogin(data) {
  return request.post('/api/auth/admin/login', data)
}
export function getCurrentUser() {
  return request.get('/api/user/me')
}
/** 当前用户修改个人信息（昵称、头像、新密码可选） */
export function updateProfile(data) {
  return request.put('/api/user/me', data)
}

/** 丽水市所辖区县列表（用于下拉、筛选） */
export function getRegions() {
  return request.get('/api/regions')
}

export function getProjectList(params) {
  return request.get('/api/projects', { params })
}
export function getProjectById(id) {
  return request.get(`/api/projects/${id}`)
}
export function getRecommendProjects() {
  return request.get('/api/projects/recommend')
}

// 用户收藏非遗项目
export function getFavoriteProjects() {
  return request.get('/api/user/favorites/projects')
}
export function addFavoriteProject(id) {
  return request.post(`/api/user/favorites/projects/${id}`)
}
export function removeFavoriteProject(id) {
  return request.delete(`/api/user/favorites/projects/${id}`)
}

export function getInheritorList(params) {
  return request.get('/api/inheritors', { params })
}
export function getInheritorById(id) {
  return request.get(`/api/inheritors/${id}`)
}

export function getNewsList(params) {
  return request.get('/api/news', { params })
}
export function getNewsById(id) {
  return request.get(`/api/news/${id}`)
}

export function getActivityList(params) {
  return request.get('/api/activities', { params })
}
export function getActivityById(id) {
  return request.get(`/api/activities/${id}`)
}
export function registerActivity(id) {
  return request.post(`/api/activities/${id}/register`)
}
export function cancelActivityRegistration(id) {
  return request.delete(`/api/activities/${id}/register`)
}

export function getStoryList(params) {
  return request.get('/api/stories', { params })
}
export function getStoryById(id) {
  return request.get(`/api/stories/${id}`)
}

export function aiConsult(data) {
  return request.post('/api/ai/consult', data, { timeout: 60000 })
}

// 后台
export function adminGetUsers(params) {
  return request.get('/api/admin/users', { params: params || {} })
}
export function adminCreateUser(data) {
  return request.post('/api/admin/users', data)
}
export function adminUpdateUser(id, data) {
  return request.put(`/api/admin/users/${id}`, data)
}
export function adminDeleteUser(id) {
  return request.delete(`/api/admin/users/${id}`)
}

export function adminGetProjects(params) {
  return request.get('/api/admin/projects', { params })
}
export function adminCreateProject(data) {
  return request.post('/api/admin/projects', data)
}
export function adminUpdateProject(id, data) {
  return request.put(`/api/admin/projects/${id}`, data)
}
export function adminDeleteProject(id) {
  return request.delete(`/api/admin/projects/${id}`)
}

export function adminGetInheritors(params) {
  return request.get('/api/admin/inheritors', { params })
}
export function adminCreateInheritor(data) {
  return request.post('/api/admin/inheritors', data)
}
export function adminUpdateInheritor(id, data) {
  return request.put(`/api/admin/inheritors/${id}`, data)
}
export function adminDeleteInheritor(id) {
  return request.delete(`/api/admin/inheritors/${id}`)
}

export function adminGetNews(params) {
  return request.get('/api/admin/news', { params })
}
export function adminCreateNews(data) {
  return request.post('/api/admin/news', data)
}
export function adminUpdateNews(id, data) {
  return request.put(`/api/admin/news/${id}`, data)
}
export function adminDeleteNews(id) {
  return request.delete(`/api/admin/news/${id}`)
}

export function adminGetActivities(params) {
  return request.get('/api/admin/activities', { params })
}
export function adminCreateActivity(data) {
  return request.post('/api/admin/activities', data)
}
export function adminUpdateActivity(id, data) {
  return request.put(`/api/admin/activities/${id}`, data)
}
export function adminDeleteActivity(id) {
  return request.delete(`/api/admin/activities/${id}`)
}

export function adminGetStories(params) {
  return request.get('/api/admin/stories', { params })
}
export function adminCreateStory(data) {
  return request.post('/api/admin/stories', data)
}
export function adminUpdateStory(id, data) {
  return request.put(`/api/admin/stories/${id}`, data)
}
export function adminDeleteStory(id) {
  return request.delete(`/api/admin/stories/${id}`)
}

// 推荐权重配置
export function adminGetRecommendWeights() {
  return request.get('/api/admin/recommend/weights')
}
export function adminSaveRecommendWeights(data) {
  return request.post('/api/admin/recommend/weights', data)
}
export function adminGetRecommendAnalytics() {
  return request.get('/api/admin/recommend/analytics')
}

export function adminUploadFile(file) {
  const form = new FormData()
  form.append('file', file)
  return request.post('/api/upload', form)
}
/** 上传图片（登录用户与管理员通用，保存到 pictures 目录） */
export function uploadImage(file) {
  const form = new FormData()
  form.append('file', file)
  return request.post('/api/upload', form)
}

/** 后台统计概览（后台首页数据分析） */
export function adminGetStatsOverview() {
  return request.get('/api/admin/stats/overview')
}

/** 后台行为统计（用户行为可视化） */
export function adminGetBehaviorStats() {
  return request.get('/api/admin/stats/behavior')
}
