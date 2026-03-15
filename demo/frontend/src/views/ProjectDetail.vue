<template>
  <div class="detail container" v-if="item">
    <button class="btn btn-default back" @click="$router.push('/projects')">返回列表</button>
    <h1>{{ item.title }}</h1>
    <p class="meta">
      分类：{{ item.category || '未分类' }}
      <span v-if="item.ichType">｜类型：{{ item.ichType }}</span>
      <span v-if="item.heritageLevel">｜等级：{{ item.heritageLevel }}</span>
      <span>｜批准时间：{{ formatApprovalYear(item.approvalTime) }}</span>
    </p>
    <div class="meta-actions">
      <button
        class="btn btn-default"
        :class="{ 'btn-primary': isFavorite }"
        :disabled="favLoading"
        @click="toggleFavorite"
      >
        {{ isFavorite ? '取消收藏' : '收藏' }}
      </button>
    </div>
    <div v-if="item.images" class="images">
      <img v-for="(url, i) in item.images.split(',')" :key="i" :src="url.startsWith('http') ? url : baseUrl + url" alt="" />
    </div>
    <div class="content">
      <p v-for="(para, idx) in descriptionParas" :key="idx">
        {{ para }}
      </p>
    </div>
  </div>
  <div v-else class="loading container">加载中...</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProjectById, getFavoriteProjects, addFavoriteProject, removeFavoriteProject } from '../api'

const route = useRoute()
const item = ref(null)
const baseUrl = import.meta.env.DEV ? '' : ''
const isFavorite = ref(false)
const favLoading = ref(false)

const descriptionParas = computed(() => {
  const text = item.value?.description || ''
  return text
    .split(/\r?\n/)
    .map(p => p.trim())
    .filter(p => p.length > 0)
})

function formatApprovalYear(t) {
  if (!t) return '—'
  const d = new Date(t)
  const y = d.getFullYear()
  if (Number.isNaN(y)) return '—'
  return String(y)
}

onMounted(async () => {
  try {
    const res = await getProjectById(route.params.id)
    item.value = res.data
    // 获取收藏状态（登录用户）
    try {
      const favRes = await getFavoriteProjects()
      if (favRes.success && Array.isArray(favRes.data)) {
        isFavorite.value = favRes.data.some(p => p.id === item.value.id)
      }
    } catch {
      isFavorite.value = false
    }
  } catch (_) {
    item.value = null
  }
})

async function toggleFavorite() {
  if (!item.value || favLoading.value) return
  favLoading.value = true
  try {
    if (isFavorite.value) {
      await removeFavoriteProject(item.value.id)
      isFavorite.value = false
    } else {
      await addFavoriteProject(item.value.id)
      isFavorite.value = true
    }
  } catch (e) {
    alert(e.response?.data?.message || '操作失败')
  } finally {
    favLoading.value = false
  }
}
</script>

<style scoped>
.detail { max-width: 800px; }
.back { margin-bottom: 16px; }
.detail h1 { margin-bottom: 8px; }
.meta { color: #666; margin-bottom: 16px; }
.meta-actions { margin-bottom: 16px; }
.images { margin-bottom: 24px; }
.images img { max-width: 100%; border-radius: 8px; margin-bottom: 8px; }
.content { line-height: 1.8; margin-top: 8px; }
.content p {
  margin: 0 0 8px;
  text-indent: 2em;
  white-space: pre-wrap;
}
.audio { margin-top: 24px; padding: 16px; background: #f5f5f5; border-radius: 8px; }
.loading { padding: 48px; text-align: center; }
</style>
