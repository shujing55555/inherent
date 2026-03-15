<template>
  <div class="detail container" v-if="item">
    <button class="btn btn-default back" @click="$router.push('/news')">返回列表</button>
    <h1>{{ item.title }}</h1>
    <p class="meta">{{ formatTime(item.publishTime) }} {{ item.source ? ' · ' + item.source : '' }}</p>
    <img v-if="item.coverImage" :src="item.coverImage.startsWith('http') ? item.coverImage : baseUrl + item.coverImage" class="cover" alt="" />
    <div class="content">
      <p v-for="(para, idx) in contentParas" :key="idx">
        {{ para }}
      </p>
    </div>
  </div>
  <div v-else class="loading container">加载中...</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNewsById } from '../api'

const route = useRoute()
const item = ref(null)
const baseUrl = import.meta.env.DEV ? '' : ''

const contentParas = computed(() => {
  const text = item.value?.content || ''
  return text
    .split(/\r?\n/)
    .map(p => p.trim())
    .filter(p => p.length > 0)
})

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toISOString().slice(0, 10)
}

onMounted(async () => {
  try {
    const res = await getNewsById(route.params.id)
    item.value = res.data
  } catch (_) {
    item.value = null
  }
})
</script>

<style scoped>
.detail { max-width: 800px; }
.back { margin-bottom: 16px; }
.detail h1 { margin-bottom: 8px; }
.meta { color: #666; margin-bottom: 16px; }
.cover { max-width: 100%; border-radius: 8px; margin-bottom: 24px; }
.content { line-height: 1.8; }
.content p {
  margin: 0 0 8px;
  text-indent: 2em;
  white-space: pre-wrap;
}
.loading { padding: 48px; text-align: center; }
</style>
