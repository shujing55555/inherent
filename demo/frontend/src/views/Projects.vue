<template>
  <div class="projects-page container">
    <h2>非遗项目</h2>
    <div class="toolbar">
      <div class="region-tabs">
        <button type="button" :class="{ active: !region }" @click="region = ''; load(0)">全部</button>
        <button v-for="r in LISHUI_REGIONS" :key="r" type="button" :class="{ active: region === r }" @click="region = r; load(0)">{{ r }}</button>
      </div>
      <div class="level-search-row">
        <div class="level-tabs">
          <span class="tabs-label">遗产级别：</span>
          <button
            type="button"
            :class="{ active: !heritageLevel }"
            @click="heritageLevel = ''; load(0)"
          >
            全部
          </button>
          <button
            v-for="l in HERITAGE_LEVELS"
            :key="l"
            type="button"
            :class="{ active: heritageLevel === l }"
            @click="heritageLevel = l; load(0)"
          >
            {{ l }}
          </button>
        </div>
        <div class="type-tabs">
          <span class="tabs-label">项目类型：</span>
          <button
            type="button"
            :class="{ active: !ichType }"
            @click="ichType = ''; load(0)"
          >
            全部
          </button>
          <button
            v-for="t in PROJECT_TYPES"
            :key="t"
            type="button"
            :class="{ active: ichType === t }"
            @click="ichType = t; load(0)"
          >
            {{ t }}
          </button>
        </div>
        <div class="search-row">
          <input v-model="keyword" type="text" placeholder="输入关键词搜索" @keyup.enter="load(0)" />
          <button class="btn btn-primary" @click="load(0)">搜索</button>
        </div>
      </div>
    </div>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="list">
      <div v-for="item in list" :key="item.id" class="card" @click="$router.push(`/projects/${item.id}`)">
        <img v-if="item.images" :src="(item.images.split(',')[0]).startsWith('http') ? item.images.split(',')[0] : baseUrl + item.images.split(',')[0]" class="cover" alt="" />
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
    <div v-if="totalPages > 1" class="pagination">
      <button class="btn btn-default" :disabled="page === 0" @click="load(page - 1)">上一页</button>
      <span>{{ page + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-default" :disabled="page >= totalPages - 1" @click="load(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProjectList } from '../api'
import { LISHUI_REGIONS } from '../constants/regions'

const keyword = ref('')
const region = ref('')
const heritageLevel = ref('')
const ichType = ref('')
const list = ref([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const baseUrl = import.meta.env.DEV ? '' : ''

const HERITAGE_LEVELS = ['国家级', '省级', '市级', '县级']
const PROJECT_TYPES = [
  '民间文学',
  '传统音乐',
  '传统舞蹈',
  '传统戏剧',
  '曲艺',
  '传统体育、游艺与杂技',
  '传统美术',
  '传统技艺',
  '传统医药',
  '民俗'
]

function formatApprovalYear(t) {
  if (!t) return '—'
  const d = new Date(t)
  const y = d.getFullYear()
  if (Number.isNaN(y)) return '—'
  return String(y)
}

async function load(p = 0) {
  page.value = p
  loading.value = true
  try {
    const res = await getProjectList({
      keyword: keyword.value || undefined,
      region: region.value || undefined,
      heritageLevel: heritageLevel.value || undefined,
      ichType: ichType.value || undefined,
      page: p,
      size: 12,
    })
    list.value = res.data || []
    totalPages.value = res.totalPages || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => load(0))
</script>

<style scoped>
.projects-page h2 { margin-bottom: 16px; }
.toolbar { margin-bottom: 24px; display: flex; flex-direction: column; gap: 10px; }
.region-tabs { display: flex; flex-wrap: wrap; gap: 8px; }
.region-tabs button { padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; background: #fff; cursor: pointer; font-size: 14px; }
.region-tabs button:hover { border-color: #c41e3a; color: #c41e3a; }
.region-tabs button.active { background: #c41e3a; color: #fff; border-color: #c41e3a; }
.level-search-row { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; justify-content: space-between; }
.level-tabs,
.type-tabs { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.level-tabs .tabs-label,
.type-tabs .tabs-label { font-size: 13px; color: #555; margin-right: 4px; }
.level-tabs button,
.type-tabs button { padding: 6px 12px; border: 1px solid #ddd; border-radius: 999px; background: #fff; cursor: pointer; font-size: 13px; }
.level-tabs button:hover { border-color: #c41e3a; color: #c41e3a; }
.type-tabs button:hover { border-color: #c41e3a; color: #c41e3a; }
.level-tabs button.active,
.type-tabs button.active { background: #c41e3a; color: #fff; border-color: #c41e3a; }
.search-row { display: flex; gap: 8px; }
.search-row input { flex: 1; min-width: 120px; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
.list { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
.card { background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); cursor: pointer; }
.card .cover { width: 100%; height: 160px; object-fit: cover; }
.card .cover.placeholder { background: #eee; display: flex; align-items: center; justify-content: center; color: #999; }
.card .info { padding: 12px; }
.card .info h3 { font-size: 16px; margin-bottom: 6px; }
.card .info .category { font-size: 12px; color: #c41e3a; margin-bottom: 4px; display: flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.level-badge { padding: 2px 6px; border-radius: 999px; background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; font-size: 11px; }
.type-badge { padding: 2px 6px; border-radius: 999px; background: #e0f2fe; color: #0369a1; border: 1px solid #bae6fd; font-size: 11px; }
.card .info .meta { font-size: 12px; color: #888; margin-bottom: 6px; }
.card .info .desc { font-size: 13px; color: #666; }
.pagination { margin-top: 24px; display: flex; align-items: center; justify-content: center; gap: 16px; }
.loading { text-align: center; padding: 24px; color: #666; }
</style>
