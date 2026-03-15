<template>
  <div class="home">
    <!-- 主视觉区 -->
    <section class="hero">
      <div class="hero-bg" aria-hidden="true"></div>
      <div class="hero-inner container">
        <p class="hero-label">丽水非遗 · 薪火相传</p>
        <h1 class="hero-title">丽水市非物质文化遗产平台</h1>
        <p class="hero-intro">了解、传承、弘扬丽水非遗文化</p>
        <div class="hero-divider"></div>
      </div>
    </section>

    <!-- 中间内容：左轮播图 + 右推荐项目（首页单独放大一点） -->
    <section class="main-row">
      <div class="main-left" v-if="carouselList.length > 0">
        <div class="carousel">
          <div class="carousel-track" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
            <router-link
              v-for="p in carouselList"
              :key="p.id"
              :to="`/projects/${p.id}`"
              class="carousel-item"
            >
              <div class="carousel-img-wrap">
                <img
                  v-if="carouselImage(p)"
                  :src="carouselImage(p)"
                  :alt="p.title"
                  class="carousel-img"
                />
                <div v-else class="carousel-placeholder">暂无图片</div>
              </div>
              <div class="carousel-caption">
                <h3>{{ p.title }}</h3>
                <p v-if="p.category" class="carousel-category">{{ p.category }}</p>
              </div>
            </router-link>
          </div>
          <button type="button" class="carousel-btn carousel-prev" aria-label="上一张" @click.stop="prev">‹</button>
          <button type="button" class="carousel-btn carousel-next" aria-label="下一张" @click.stop="next">›</button>
          <div class="carousel-dots">
            <button
              v-for="(_, i) in carouselList"
              :key="i"
              type="button"
              :class="{ active: i === currentIndex }"
              :aria-label="`第${i + 1}张`"
              @click.stop="currentIndex = i"
            />
          </div>
        </div>
      </div>
      <div class="main-right" v-if="recommendList.length">
        <section class="recommend-section">
          <h2 class="recommend-title">为你推荐的非遗项目</h2>
          <p class="recommend-sub">基于你浏览和收藏的非遗类型，为你智能推荐更多项目。</p>
          <div class="recommend-grid">
            <router-link
              v-for="item in recommendList"
              :key="item.id"
              :to="`/projects/${item.id}`"
              class="rec-card"
            >
              <div class="rec-cover-wrap">
                <img v-if="recImage(item)" :src="recImage(item)" :alt="item.title" class="rec-cover" />
                <div v-else class="rec-cover placeholder">暂无图片</div>
              </div>
              <div class="rec-info">
                <h3>{{ item.title }}</h3>
                <p class="rec-meta">
                  <span>{{ item.category || '未标注区县' }}</span>
                  <span v-if="item.ichType" class="type-badge">{{ item.ichType }}</span>
                  <span v-if="item.heritageLevel" class="level-badge">{{ item.heritageLevel }}</span>
                </p>
              </div>
            </router-link>
          </div>
        </section>
      </div>
    </section>

    <!-- 底部一句 -->
    <section class="slogan container">
      <p class="slogan-text">绿水青山处，非遗正当时</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getProjectById, getRecommendProjects } from '../api'

const CAROUSEL_IDS = [1, 2, 3]
const carouselList = ref([])
const currentIndex = ref(0)
let timer = null
const baseUrl = import.meta.env.VITE_API_BASE_URL || ''
const recommendList = ref([])

function carouselImage(p) {
  if (!p || !p.images || !p.images.trim()) return ''
  const first = p.images.split(',')[0].trim()
  if (!first) return ''
  return first.startsWith('http') ? first : (baseUrl ? baseUrl.replace(/\/$/, '') + first : first)
}

function recImage(p) {
  if (!p || !p.images || !p.images.trim()) return ''
  const first = p.images.split(',')[0].trim()
  if (!first) return ''
  return first.startsWith('http') ? first : (baseUrl ? baseUrl.replace(/\/$/, '') + first : first)
}

function prev() {
  currentIndex.value = (currentIndex.value - 1 + carouselList.value.length) % carouselList.value.length
  resetTimer()
}
function next() {
  currentIndex.value = (currentIndex.value + 1) % carouselList.value.length
  resetTimer()
}
function resetTimer() {
  if (timer) clearInterval(timer)
  timer = setInterval(next, 4500)
}
onMounted(async () => {
  const res = await Promise.allSettled(CAROUSEL_IDS.map(id => getProjectById(id)))
  carouselList.value = res
    .filter(r => r.status === 'fulfilled' && r.value?.success && r.value?.data)
    .map(r => r.value.data)
  if (carouselList.value.length > 0) timer = setInterval(next, 4500)

  // 推荐项目（已登录用户为个性化推荐，未登录为热门项目）
  try {
    const recRes = await getRecommendProjects()
    if (recRes.success && Array.isArray(recRes.data)) {
      // 只取前 6 个用于首页推荐
      recommendList.value = recRes.data.slice(0, 6)
    }
  } catch (_) {
    recommendList.value = []
  }
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.home {
  min-height: 100%;
  padding-bottom: 48px;
}

/* 全局容器（用于首页顶部和底部，保持与其它页面一致） */
.container {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 20px;
}

/* ========== 主视觉区 ========== */
.hero {
  position: relative;
  padding: 56px 20px 48px;
  text-align: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(165deg, #faf8f5 0%, #f0ebe3 40%, #e8e2d9 100%);
}

.hero-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    repeating-linear-gradient(
      90deg,
      transparent,
      transparent 48px,
      rgba(196, 30, 58, 0.03) 48px,
      rgba(196, 30, 58, 0.03) 49px
    );
  pointer-events: none;
}

.hero-inner {
  position: relative;
  z-index: 1;
}

.hero-label {
  font-size: 13px;
  letter-spacing: 0.2em;
  color: #8b7355;
  margin-bottom: 12px;
  text-transform: uppercase;
}

.hero-title {
  font-family: 'Noto Serif SC', 'SimSun', 'Songti SC', serif;
  font-size: clamp(1.75rem, 4vw, 2.25rem);
  font-weight: 600;
  color: #2c1810;
  line-height: 1.35;
  margin: 0 0 12px;
}

.hero-intro {
  font-size: 1rem;
  color: #6b5b4f;
  margin: 0 0 24px;
}

.hero-divider {
  width: 64px;
  height: 3px;
  margin: 0 auto;
  background: linear-gradient(90deg, transparent, #c41e3a, transparent);
  border-radius: 2px;
}

/* ========== 中间主内容：左轮播 + 右推荐 ========== */
.main-row {
  max-width: 1400px;
  margin: 8px auto 16px;
  padding: 0 20px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.main-left {
  flex: 1.4;
}
.main-right {
  flex: 1.2;
}

/* ========== 轮播图 ========== */
.carousel {
  position: relative;
  width: 100%;
  overflow: hidden;
  border-radius: 12px;
  background: #f0ebe3;
}
.carousel-track {
  display: flex;
  transition: transform 0.4s ease;
}
.carousel-item {
  flex: 0 0 100%;
  display: block;
  text-decoration: none;
  color: inherit;
  border-radius: 12px;
  overflow: hidden;
}
.carousel-img-wrap {
  width: 100%;
  height: 400px;
  background: #e8e2d9;
}
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.carousel-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8b7355;
  font-size: 14px;
}
.carousel-caption {
  padding: 16px 20px;
  text-align: center;
  background: rgba(44, 24, 16, 0.03);
}
.carousel-caption h3 {
  margin: 0 0 4px;
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c1810;
}
.carousel-category {
  margin: 0;
  font-size: 13px;
  color: #6b5b4f;
}
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: rgba(255,255,255,0.9);
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
  font-size: 24px;
  line-height: 1;
  color: #2c1810;
  cursor: pointer;
  z-index: 2;
  transition: background 0.2s;
}
.carousel-btn:hover {
  background: #fff;
}
.carousel-prev { left: 12px; }
.carousel-next { right: 12px; }
.carousel-dots {
  position: absolute;
  bottom: 16px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 8px;
  z-index: 2;
}
.carousel-dots button {
  width: 8px;
  height: 8px;
  border: none;
  border-radius: 50%;
  background: rgba(255,255,255,0.6);
  cursor: pointer;
  padding: 0;
  transition: background 0.2s, transform 0.2s;
}
.carousel-dots button:hover {
  background: rgba(255,255,255,0.9);
}
.carousel-dots button.active {
  background: #c41e3a;
  transform: scale(1.2);
}

/* ========== 底部标语 ========== */
.slogan {
  padding: 24px 20px;
  text-align: center;
}

.slogan-text {
  font-family: 'Noto Serif SC', 'SimSun', 'Songti SC', serif;
  font-size: 15px;
  color: #8b7355;
  margin: 0;
  letter-spacing: 0.08em;
}

/* 推荐区 */
.recommend-section {
  margin: 0;
}
.recommend-title {
  margin: 0 0 4px;
  font-size: 20px;
}
.recommend-sub {
  margin: 0 0 12px;
  font-size: 13px;
  color: #6b7280;
}
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.rec-card {
  display: block;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}
.rec-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.12);
}
.rec-cover-wrap {
  width: 100%;
  height: 140px;
  background: #f3f4f6;
}
.rec-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.rec-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
}
.rec-info {
  padding: 10px 12px 12px;
}
.rec-info h3 {
  margin: 0 0 4px;
  font-size: 15px;
}
.rec-meta {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
  display: flex;
  flex-wrap: wrap;
  gap: 4px 6px;
}
.type-badge {
  padding: 2px 6px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #0369a1;
  border: 1px solid #bae6fd;
  font-size: 11px;
}
.level-badge {
  padding: 2px 6px;
  border-radius: 999px;
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 11px;
}

@media (max-width: 900px) {
  .main-row {
    flex-direction: column;
  }
  .main-left,
  .main-right {
    width: 100%;
  }
}
</style>
