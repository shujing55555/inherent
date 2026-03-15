<template>
  <div class="projects-page container">
    <h2>我的收藏 · 非遗项目</h2>
    <p class="hint">这里显示你收藏的非遗项目，点击卡片可以进入项目详情。</p>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="!list.length" class="empty">你还没有收藏任何非遗项目。</div>
    <div v-else class="list">
      <div
        v-for="item in list"
        :key="item.id"
        class="card"
        @click="$router.push(`/projects/${item.id}`)"
      >
        <img
          v-if="item.images"
          :src="firstImage(item.images)"
          class="cover"
          alt=""
        />
        <div v-else class="cover placeholder">暂无图片</div>
        <div class="info">
          <h3>{{ item.title }}</h3>
          <p class="category">
            {{ item.category || '未标注区县' }}
            <span v-if="item.ichType" class="type-badge">{{ item.ichType }}</span>
            <span v-if="item.heritageLevel" class="level-badge">{{ item.heritageLevel }}</span>
          </p>
          <p class="meta">
            批准时间：{{ formatApprovalYear(item.approvalTime) }}
          </p>
          <p class="desc">{{ (item.description || '').slice(0, 80) }}...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavoriteProjects } from '../api'

const list = ref([])
const loading = ref(false)
const baseUrl = import.meta.env.DEV ? '' : ''

function firstImage(images) {
  const parts = String(images)
    .split(',')
    .map(s => s.trim())
    .filter(Boolean)
  if (!parts.length) return ''
  const url = parts[0]
  return url.startsWith('http') ? url : baseUrl + url
}

function formatApprovalYear(t) {
  if (!t) return '—'
  const d = new Date(t)
  const y = d.getFullYear()
  if (Number.isNaN(y)) return '—'
  return String(y)
}

async function load() {
  loading.value = true
  try {
    const res = await getFavoriteProjects()
    if (res.success && Array.isArray(res.data)) {
      list.value = res.data
    } else {
      list.value = []
    }
  } catch (_) {
    list.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.projects-page h2 {
  margin-bottom: 8px;
}
.hint {
  margin: 0 0 16px;
  font-size: 13px;
  color: #6b7280;
}
.list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
}
.card .cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
}
.card .cover.placeholder {
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}
.card .info {
  padding: 12px;
}
.card .info h3 {
  font-size: 16px;
  margin-bottom: 6px;
}
.card .info .category {
  font-size: 12px;
  color: #c41e3a;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.level-badge {
  padding: 2px 6px;
  border-radius: 999px;
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 11px;
}
.type-badge {
  padding: 2px 6px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  border: 1px solid #bae6fd;
  font-size: 11px;
}
.card .info .meta {
  font-size: 12px;
  color: #888;
  margin-bottom: 6px;
}
.card .info .desc {
  font-size: 13px;
  color: #666;
}
.loading,
.empty {
  padding: 24px 0;
  text-align: center;
  color: #666;
}
</style>

