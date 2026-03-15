<template>
  <div class="activities-page container">
    <h2>非遗活动</h2>
    <div class="toolbar">
      <div class="region-tabs">
        <button type="button" :class="{ active: !region }" @click="region = ''; load(0)">全部</button>
        <button v-for="r in LISHUI_REGIONS" :key="r" type="button" :class="{ active: region === r }" @click="region = r; load(0)">{{ r }}</button>
      </div>
    </div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="grid">
      <div v-for="item in list" :key="item.id" class="card" @click="$router.push(`/activities/${item.id}`)">
        <div class="cover-wrap">
          <img v-if="item.coverImage" :src="imgSrc(item.coverImage)" class="cover" alt="" />
          <div v-else class="cover placeholder">暂无图片</div>
        </div>
        <h3 class="title">{{ item.title }}</h3>
        <p class="meta">{{ item.region || '—' }} · {{ item.location || '待定' }}</p>
        <div class="footer">
          <span :class="['status', statusClass(item)]">{{ activityStatus(item) }}</span>
          <span class="date-range">
            报名：{{ dateRange(item.startTime, item.endTime) }} · 活动：{{ formatDate(item.createTime) }}
          </span>
        </div>
      </div>
    </div>
    <div v-if="totalPages > 1" class="pagination">
      <button class="btn btn-default" :disabled="page === 0" @click="load(page - 1)">上一页</button>
      <span>{{ page + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-default" :disabled="page >= totalPages - 1" @click="load(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getActivityList } from '../api'
import { LISHUI_REGIONS } from '../constants/regions'

const list = ref([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const region = ref('')

function imgSrc(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : (import.meta.env.DEV ? '' : '') + url
}

/** 活动状态：报名中(未开始)、进行中、已结束 */
function activityStatus(item) {
  const now = new Date().getTime()
  const start = item.startTime ? new Date(item.startTime).getTime() : null
  const end = item.endTime ? new Date(item.endTime).getTime() : null
  if (end != null && now > end) return '已结束'
  if (start != null && end != null && now >= start && now <= end) return '进行中'
  if (start != null && now < start) return '报名中'
  return '—'
}

function statusClass(item) {
  const s = activityStatus(item)
  if (s === '已结束') return 'status-ended'
  if (s === '进行中') return 'status-ongoing'
  if (s === '报名中') return 'status-open'
  return 'status-none'
}

function dateRange(start, end) {
  const s = start ? new Date(start).toISOString().slice(0, 10) : '—'
  const e = end ? new Date(end).toISOString().slice(0, 10) : '—'
  return `${s} ~ ${e}`
}

function formatDate(t) {
  if (!t) return '—'
  return new Date(t).toISOString().slice(0, 10)
}

async function load(p = 0) {
  page.value = p
  loading.value = true
  try {
    const res = await getActivityList({ region: region.value || undefined, page: p, size: 10 })
    list.value = res.data || []
    totalPages.value = res.totalPages || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => load(0))
</script>

<style scoped>
.activities-page h2 { margin-bottom: 16px; }
.toolbar { margin-bottom: 20px; }
.region-tabs { display: flex; flex-wrap: wrap; gap: 8px; }
.region-tabs button { padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; background: #fff; cursor: pointer; font-size: 14px; }
.region-tabs button:hover { border-color: #c41e3a; color: #c41e3a; }
.region-tabs button.active { background: #c41e3a; color: #fff; border-color: #c41e3a; }
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); cursor: pointer; overflow: hidden; display: flex; flex-direction: column; }
.cover-wrap { width: 100%; aspect-ratio: 4/3; overflow: hidden; }
.card .cover { width: 100%; height: 100%; object-fit: cover; }
.cover.placeholder { width: 100%; height: 100%; background: #f0f0f0; display: flex; align-items: center; justify-content: center; color: #999; font-size: 14px; }
.card .title { padding: 12px 12px 0; font-size: 15px; margin: 0; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card .meta { padding: 6px 12px 0; font-size: 13px; color: #666; margin: 0; }
.card .footer { padding: 10px 12px 12px; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 6px; }
.status { font-size: 12px; padding: 2px 8px; border-radius: 4px; color: #fff; }
.status-ended { background: #c41e3a; }
.status-ongoing { background: #52c41a; }
.status-open { background: #1890ff; }
.status-none { background: #999; }
.date-range { font-size: 12px; color: #666; }
.pagination { margin-top: 24px; display: flex; align-items: center; justify-content: center; gap: 16px; }
.loading { padding: 48px; text-align: center; }
@media (max-width: 1200px) { .grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .grid { grid-template-columns: 1fr; } }
</style>
