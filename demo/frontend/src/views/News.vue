<template>
  <div class="news-page container">
    <h2>非遗资讯</h2>
    <div class="toolbar">
      <div class="region-tabs">
        <button type="button" :class="{ active: !region }" @click="region = ''; load(0)">全部</button>
        <button v-for="r in LISHUI_REGIONS" :key="r" type="button" :class="{ active: region === r }" @click="region = r; load(0)">{{ r }}</button>
      </div>
    </div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="list">
      <div v-for="item in list" :key="item.id" class="card" @click="$router.push(`/news/${item.id}`)">
        <img v-if="item.coverImage" :src="item.coverImage.startsWith('http') ? item.coverImage : baseUrl + item.coverImage" class="cover" alt="" />
        <div class="info">
          <h3>{{ item.title }}</h3>
          <p class="meta">{{ formatTime(item.publishTime) }}{{ item.region ? ' · ' + item.region : '' }}{{ item.source ? ' · ' + item.source : '' }}</p>
          <p class="desc">{{ (item.content || '').replace(/<[^>]+>/g, '').slice(0, 100) }}...</p>
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
import { getNewsList } from '../api'
import { LISHUI_REGIONS } from '../constants/regions'

const list = ref([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const region = ref('')
const baseUrl = import.meta.env.DEV ? '' : ''

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toISOString().slice(0, 10)
}

async function load(p = 0) {
  page.value = p
  loading.value = true
  try {
    const res = await getNewsList({ region: region.value || undefined, page: p, size: 10 })
    list.value = res.data || []
    totalPages.value = res.totalPages || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => load(0))
</script>

<style scoped>
.news-page h2 { margin-bottom: 16px; }
.toolbar { margin-bottom: 20px; }
.region-tabs { display: flex; flex-wrap: wrap; gap: 8px; }
.region-tabs button { padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; background: #fff; cursor: pointer; font-size: 14px; }
.region-tabs button:hover { border-color: #c41e3a; color: #c41e3a; }
.region-tabs button.active { background: #c41e3a; color: #fff; border-color: #c41e3a; }
.list { display: grid; gap: 16px; }
.card { display: flex; gap: 16px; background: #fff; padding: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); cursor: pointer; }
.card .cover { width: 160px; height: 100px; object-fit: cover; border-radius: 6px; flex-shrink: 0; }
.card .info { flex: 1; min-width: 0; }
.card .info h3 { margin-bottom: 6px; }
.card .info .meta { font-size: 12px; color: #999; margin-bottom: 6px; }
.card .info .desc { font-size: 13px; color: #666; }
.pagination { margin-top: 24px; display: flex; align-items: center; justify-content: center; gap: 16px; }
.loading { padding: 48px; text-align: center; }
</style>
