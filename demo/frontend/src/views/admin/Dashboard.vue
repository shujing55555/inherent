<template>
  <div class="dashboard">
    <div class="dash-header">
      <div>
        <h2>后台首页 · 数据分析</h2>
        <p class="desc">查看当前用户数量、非遗项目与活动在各区县的分布情况。</p>
      </div>
      <div class="quick-links">
        <router-link to="/admin/projects">项目管理</router-link>
        <router-link to="/admin/inheritors">传承人</router-link>
        <router-link to="/admin/activities">活动</router-link>
        <router-link to="/admin/news">资讯</router-link>
        <router-link to="/admin/stories">故事</router-link>
        <router-link to="/admin/users">用户</router-link>
      </div>
    </div>

    <div v-if="loading" class="dash-loading">加载中...</div>
    <div v-else-if="err" class="dash-error">{{ err }}</div>
    <div v-else class="dash-content">
      <section class="stat-cards">
        <div class="stat-card primary">
          <p class="label">注册用户数量</p>
          <p class="value">{{ stats.userCount ?? 0 }}</p>
        </div>
        <div class="stat-card">
          <p class="label">非遗项目总数</p>
          <p class="value">{{ stats.projectCount ?? 0 }}</p>
        </div>
        <div class="stat-card">
          <p class="label">活动总数</p>
          <p class="value">{{ stats.activityCount ?? 0 }}</p>
        </div>
        <div class="stat-card">
          <p class="label">资讯总数</p>
          <p class="value">{{ stats.newsCount ?? 0 }}</p>
        </div>
      </section>

      <section class="tables">
        <div class="table-box">
          <h3>非遗项目 · 区县分布</h3>
          <table>
            <thead>
              <tr>
                <th>区县</th>
                <th>项目数量</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in stats.projectByRegion || []" :key="item.label">
                <td>{{ item.label }}</td>
                <td>{{ item.count }}</td>
              </tr>
              <tr v-if="!stats.projectByRegion || !stats.projectByRegion.length">
                <td colspan="2" class="empty-cell">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="table-box">
          <h3>活动 · 区县分布</h3>
          <table>
            <thead>
              <tr>
                <th>区县</th>
                <th>活动数量</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in stats.activityByRegion || []" :key="item.label">
                <td>{{ item.label }}</td>
                <td>{{ item.count }}</td>
              </tr>
              <tr v-if="!stats.activityByRegion || !stats.activityByRegion.length">
                <td colspan="2" class="empty-cell">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminGetStatsOverview } from '../../api'

const loading = ref(false)
const err = ref('')
const stats = ref({})

onMounted(async () => {
  loading.value = true
  err.value = ''
  try {
    const res = await adminGetStatsOverview()
    if (res.success && res.data) {
      stats.value = res.data
    } else {
      err.value = res.message || '统计数据获取失败'
    }
  } catch (e) {
    err.value = e.response?.data?.message || '统计数据获取失败'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.dash-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
}
.dash-header h2 {
  margin: 0 0 4px;
}
.desc {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}
.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.quick-links a {
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  text-decoration: none;
  background: #f3f4f6;
  color: #374151;
}
.quick-links a:hover {
  background: #e5e7eb;
}
.dash-loading,
.dash-error {
  padding: 40px 0;
  text-align: center;
  font-size: 14px;
}
.dash-error {
  color: #b91c1c;
}
.dash-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 14px;
}
.stat-card {
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  border: 1px solid #e5e7eb;
}
.stat-card.primary {
  background: linear-gradient(135deg, #c41e3a, #f97373);
  color: #fff;
  border-color: transparent;
}
.stat-card .label {
  margin: 0 0 6px;
  font-size: 13px;
  color: #6b7280;
}
.stat-card.primary .label {
  color: rgba(255, 255, 255, 0.9);
}
.stat-card .value {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #111827;
}
.stat-card.primary .value {
  color: #fff;
}
.tables {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}
.table-box {
  padding: 16px 18px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
  border: 1px solid #e5e7eb;
}
.table-box h3 {
  margin: 0 0 10px;
  font-size: 15px;
  color: #111827;
}
.table-box table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.table-box th,
.table-box td {
  padding: 8px 10px;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
}
.table-box th {
  background: #f9fafb;
  color: #4b5563;
}
.empty-cell {
  text-align: center;
  color: #9ca3af;
}
</style>
