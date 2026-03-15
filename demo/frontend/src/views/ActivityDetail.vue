<template>
  <div class="detail container" v-if="item">
    <button class="btn btn-default back" @click="$router.push('/activities')">返回列表</button>
    <h1>{{ item.title }}</h1>
    <p class="meta">
      报名时间：{{ dateRange(item.startTime, item.endTime) }}
      · 活动时间：{{ formatDate(item.createTime) }}
      · 区县：{{ item.region || '—' }}
      · 地点：{{ item.location || '待定' }}
    </p>
    <div class="status-line">
      <span :class="['status', statusClass]">{{ statusText }}</span>
    </div>
    <img v-if="item.coverImage" :src="item.coverImage.startsWith('http') ? item.coverImage : baseUrl + item.coverImage" class="cover" alt="" />
    <div class="content">
      <p v-for="(para, idx) in paragraphs" :key="idx">
        {{ para }}
      </p>
    </div>
    <div class="actions">
      <template v-if="canParticipate">
        <button v-if="!registered" class="btn btn-primary" :disabled="registering" @click="doRegister">参加活动</button>
        <button v-else class="btn btn-default" :disabled="cancelling" @click="doCancel">取消参加</button>
      </template>
      <template v-else>
        <p class="hint">{{ participationHint }}</p>
      </template>
    </div>
  </div>
  <div v-else class="loading container">加载中...</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getActivityById, registerActivity, cancelActivityRegistration } from '../api'

const route = useRoute()
const item = ref(null)
const registered = ref(false)
const registering = ref(false)
const cancelling = ref(false)
const baseUrl = import.meta.env.DEV ? '' : ''

/** 当前时间是否在报名开始与结束之间，仅此时可报名 */
const canParticipate = computed(() => {
  const a = item.value
  if (!a || !a.startTime || !a.endTime) return false
  const now = new Date().getTime()
  const start = new Date(a.startTime).getTime()
  const end = new Date(a.endTime).getTime()
  return now >= start && now <= end
})

const statusText = computed(() => {
  const a = item.value
  if (!a) return ''
  const now = new Date().getTime()
  const start = a.startTime ? new Date(a.startTime).getTime() : null
  const end = a.endTime ? new Date(a.endTime).getTime() : null
  if (end != null && now > end) return '已结束'
  if (start != null && end != null && now >= start && now <= end) return '进行中'
  if (start != null && now < start) return '报名中'
  return '—'
})

const statusClass = computed(() => {
  const s = statusText.value
  if (s === '已结束') return 'status-ended'
  if (s === '进行中') return 'status-ongoing'
  if (s === '报名中') return 'status-open'
  return 'status-none'
})

const participationHint = computed(() => {
  const s = statusText.value
  if (s === '已结束') return '报名已结束，不可报名。'
  if (s === '报名中') return '报名未开始，暂不可报名。'
  return '当前不可报名。'
})

const paragraphs = computed(() => {
  const text = item.value?.content || ''
  return text
    .split(/\r?\n/)
    .map(p => p.trim())
    .filter(p => p.length > 0)
})

function dateRange(start, end) {
  const s = start ? new Date(start).toISOString().slice(0, 10) : '—'
  const e = end ? new Date(end).toISOString().slice(0, 10) : '—'
  return `${s} — ${e}`
}

function formatDate(t) {
  if (!t) return '—'
  return new Date(t).toISOString().slice(0, 10)
}

async function doRegister() {
  registering.value = true
  try {
    await registerActivity(route.params.id)
    registered.value = true
    alert('报名成功')
  } catch (e) {
    alert(e.response?.data?.message || '报名失败')
  } finally {
    registering.value = false
  }
}

async function doCancel() {
  cancelling.value = true
  try {
    await cancelActivityRegistration(route.params.id)
    registered.value = false
    alert('已取消报名')
  } catch (e) {
    alert(e.response?.data?.message || '取消失败')
  } finally {
    cancelling.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getActivityById(route.params.id)
    item.value = res.data
    registered.value = res.registered === true
  } catch (_) {
    item.value = null
  }
})
</script>

<style scoped>
.detail { max-width: 800px; }
.back { margin-bottom: 16px; }
.detail h1 { margin-bottom: 8px; }
.meta { color: #666; margin-bottom: 8px; }
.status-line { margin-bottom: 16px; }
.status { font-size: 14px; padding: 4px 12px; border-radius: 4px; color: #fff; }
.status-ended { background: #c41e3a; }
.status-ongoing { background: #52c41a; }
.status-open { background: #1890ff; }
.status-none { background: #999; }
.cover { max-width: 100%; border-radius: 8px; margin-bottom: 24px; }
.content { line-height: 1.8; margin-bottom: 24px; }
.content p {
  margin: 0 0 8px;
  text-indent: 2em;
  white-space: pre-wrap;
}
.actions { padding-top: 16px; border-top: 1px solid #eee; }
.actions .hint { color: #999; font-size: 14px; margin: 0; }
.loading { padding: 48px; text-align: center; }
</style>
