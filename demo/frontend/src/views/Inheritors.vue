<template>
  <div class="inheritors-page container">
    <h2>非遗传承人</h2>
    <p class="tip">点击卡片进入详情</p>
    <div class="toolbar">
      <div class="region-tabs">
        <button type="button" :class="{ active: !region }" @click="region = ''; load()">全部</button>
        <button v-for="r in LISHUI_REGIONS" :key="r" type="button" :class="{ active: region === r }" @click="region = r; load()">{{ r }}</button>
      </div>
    </div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="list">
      <router-link v-for="p in list" :key="p.id" :to="`/inheritors/${p.id}`" class="card">
        <div class="avatar-wrap">
          <img v-if="p.avatar" :src="imgSrc(p.avatar)" class="avatar" alt="" />
          <div v-else class="avatar placeholder">暂无图片</div>
        </div>
        <div class="info">
          <h3 class="name">{{ p.name }}</h3>
          <p class="region">{{ p.region || '—' }}</p>
          <p class="intro">{{ introSnippet(p.intro) }}</p>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getInheritorList } from '../api'
import { LISHUI_REGIONS } from '../constants/regions'

const list = ref([])
const loading = ref(false)
const region = ref('')
const baseUrl = import.meta.env.VITE_API_BASE_URL || ''

function imgSrc(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : baseUrl + url
}

function introSnippet(text) {
  if (!text) return '暂无简介'
  const plain = String(text).replace(/<[^>]+>/g, '').trim()
  return plain.length > 60 ? plain.slice(0, 60) + '…' : plain
}

onMounted(load)

async function load() {
  loading.value = true
  try {
    const res = await getInheritorList({ region: region.value || undefined })
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.inheritors-page h2 { margin-bottom: 8px; }
.tip { color: #666; margin-bottom: 12px; }
.toolbar { margin-bottom: 24px; }
.region-tabs { display: flex; flex-wrap: wrap; gap: 8px; }
.region-tabs button { padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; background: #fff; cursor: pointer; font-size: 14px; }
.region-tabs button:hover { border-color: #c41e3a; color: #c41e3a; }
.region-tabs button.active { background: #c41e3a; color: #fff; border-color: #c41e3a; }
.list { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
.card { display: flex; gap: 16px; padding: 16px; background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); text-decoration: none; color: inherit; transition: box-shadow 0.2s, transform 0.2s; }
.card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.12); transform: translateY(-2px); }
.avatar-wrap { flex-shrink: 0; }
.avatar { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; display: block; }
.avatar.placeholder { width: 80px; height: 80px; background: #f0f0f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #999; font-size: 12px; }
.info { flex: 1; min-width: 0; }
.info .name { margin: 0 0 6px; font-size: 18px; font-weight: 600; color: #2c1810; }
.info .region { margin: 0 0 8px; font-size: 13px; color: #c41e3a; }
.info .intro { margin: 0; font-size: 14px; color: #666; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.loading { padding: 48px; text-align: center; }
</style>
