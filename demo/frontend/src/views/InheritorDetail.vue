<template>
  <div class="detail container" v-if="item">
    <button class="btn btn-default back" @click="$router.push('/inheritors')">返回列表</button>
    <div class="head">
      <img v-if="item.avatar" :src="item.avatar.startsWith('http') ? item.avatar : baseUrl + item.avatar" class="avatar" alt="" />
      <div class="info">
        <h1>{{ item.name }}</h1>
        <p v-if="item.region" class="region">{{ item.region }}</p>
        <p v-if="item.intro" class="intro">{{ item.intro }}</p>
      </div>
    </div>
    <section v-if="item.representativeWorks">
      <h3>代表作品</h3>
      <div class="content">
        <p v-for="(para, idx) in worksParas" :key="idx">
          {{ para }}
        </p>
      </div>
    </section>
    <section v-if="item.story">
      <h3>传承故事</h3>
      <div class="content">
        <p v-for="(para, idx) in storyParas" :key="idx">
          {{ para }}
        </p>
      </div>
    </section>
  </div>
  <div v-else class="loading container">加载中...</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getInheritorById } from '../api'

const route = useRoute()
const item = ref(null)
const baseUrl = import.meta.env.DEV ? '' : ''

const worksParas = computed(() => {
  const text = item.value?.representativeWorks || ''
  return text
    .split(/\r?\n/)
    .map(p => p.trim())
    .filter(p => p.length > 0)
})

const storyParas = computed(() => {
  const text = item.value?.story || ''
  return text
    .split(/\r?\n/)
    .map(p => p.trim())
    .filter(p => p.length > 0)
})

onMounted(async () => {
  try {
    const res = await getInheritorById(route.params.id)
    item.value = res.data
  } catch (_) {
    item.value = null
  }
})
</script>

<style scoped>
.detail { max-width: 800px; }
.back { margin-bottom: 16px; }
.head { display: flex; gap: 24px; margin-bottom: 32px; }
.avatar { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; }
.info h1 { margin-bottom: 8px; }
.region { color: #c41e3a; font-size: 15px; margin-bottom: 8px; }
.intro { color: #666; line-height: 1.6; }
section { margin-bottom: 24px; }
section h3 { margin-bottom: 12px; color: #c41e3a; }
.content { line-height: 1.8; }
.content p {
  margin: 0 0 8px;
  text-indent: 2em;
  white-space: pre-wrap;
}
.loading { padding: 48px; text-align: center; }
</style>
