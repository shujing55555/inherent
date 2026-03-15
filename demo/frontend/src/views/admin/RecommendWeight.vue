<template>
  <div class="page">
    <h2>推荐分析</h2>
    <p class="desc">
      在这里可以调整不同非遗项目类型的推荐权重，并查看基于用户浏览与收藏行为的类型分析结果。
    </p>

    <div class="panel-grid">
      <!-- 左侧：权重配置 -->
      <section class="panel">
        <div class="panel-header">
          <h3>权重配置</h3>
          <div class="toolbar">
            <button class="btn btn-primary" :disabled="saving" @click="save">保存设置</button>
            <span v-if="saving" class="saving-text">保存中...</span>
          </div>
        </div>
        <table class="table">
          <thead>
            <tr>
          <th>项目类型（ich_type）</th>
          <th>基础权重</th>
              <th>浏览权重</th>
          <th>收藏权重</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="w in rows" :key="w.ichType">
              <td>{{ w.ichType }}</td>
              <td>
                <input v-model.number="w.baseWeight" type="number" step="0.1" class="number-input" />
              </td>
              <td>
                <input v-model.number="w.viewWeight" type="number" step="0.1" class="number-input" />
              </td>
              <td>
                <input v-model.number="w.favoriteWeight" type="number" step="0.1" class="number-input" />
              </td>
            </tr>
            <tr v-if="!rows.length">
              <td colspan="4" class="empty-cell">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </section>

      <!-- 右侧：浏览与收藏分析 -->
      <section class="panel">
        <div class="panel-header">
          <h3>类型浏览与收藏统计</h3>
        </div>
        <p class="desc small">
          基于用户行为日志与收藏记录，统计每种非遗项目类型被浏览与被收藏的次数，可用于分析哪些类型更受欢迎。
        </p>
        <table class="table">
          <thead>
            <tr>
              <th>项目类型</th>
              <th>浏览次数</th>
              <th>收藏次数</th>
              <th>收藏率（收藏次数 / 浏览次数）</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in stats" :key="s.ichType">
              <td>{{ s.ichType }}</td>
              <td>{{ s.viewCount }}</td>
              <td>{{ s.favoriteCount }}</td>
              <td>{{ s.viewCount ? (s.favoriteCount / s.viewCount).toFixed(2) : '0.00' }}</td>
            </tr>
            <tr v-if="!stats.length">
              <td colspan="4" class="empty-cell">暂无统计数据</td>
            </tr>
          </tbody>
        </table>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminGetRecommendWeights, adminSaveRecommendWeights, adminGetRecommendAnalytics } from '../../api'

const rows = ref([])
const saving = ref(false)
const stats = ref([])

function formatTime(t) {
  if (!t) return '—'
  const d = new Date(t)
  if (Number.isNaN(d.getTime())) return '—'
  return d.toISOString().slice(0, 19).replace('T', ' ')
}

async function load() {
  try {
    const res = await adminGetRecommendWeights()
    if (res.success && Array.isArray(res.data)) {
      rows.value = res.data.map((w) => ({
        ichType: w.ichType,
        baseWeight: w.baseWeight,
        viewWeight: w.viewWeight,
        favoriteWeight: w.favoriteWeight,
      }))
    }
  } catch (e) {
    alert(e.response?.data?.message || '加载推荐权重失败')
  }
}

async function loadStats() {
  try {
    const res = await adminGetRecommendAnalytics()
    if (res.success && res.data && Array.isArray(res.data.typeStats)) {
      stats.value = res.data.typeStats
    } else {
      stats.value = []
    }
  } catch (e) {
    stats.value = []
  }
}

async function save() {
  if (!rows.value.length) return
  saving.value = true
  try {
    const payload = rows.value.map((w) => ({
      ichType: w.ichType,
      baseWeight: Number(w.baseWeight) || 0,
      viewWeight: Number(w.viewWeight) || 0,
      favoriteWeight: Number(w.favoriteWeight) || 0,
    }))
    await adminSaveRecommendWeights(payload)
    alert('保存成功')
    load()
    loadStats()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await load()
  await loadStats()
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.desc {
  margin-top: -4px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #6b7280;
}
.desc.small {
  margin-top: 0;
  margin-bottom: 8px;
}
.panel-grid {
  display: grid;
  grid-template-columns: 2fr 2fr;
  gap: 16px;
}
.panel {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.12);
  border: 1px solid #e5e7eb;
  padding: 14px 16px 16px;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.panel-header h3 {
  margin: 0;
  font-size: 15px;
  color: #111827;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
}
.saving-text {
  font-size: 13px;
  color: #6b7280;
}
.table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}
.table th,
.table td {
  border: 1px solid #eee;
  padding: 8px 10px;
  text-align: left;
  font-size: 13px;
}
.table th {
  background: #f9fafb;
  color: #4b5563;
}
.number-input {
  width: 100%;
  max-width: 100px;
  padding: 4px 6px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
}
.empty-cell {
  text-align: center;
  color: #9ca3af;
}
.section-title {
  margin-top: 24px;
  margin-bottom: 6px;
  font-size: 16px;
}
@media (max-width: 900px) {
  .panel-grid {
    grid-template-columns: 1fr;
  }
}
</style>

