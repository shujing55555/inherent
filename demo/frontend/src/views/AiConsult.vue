<template>
  <div class="ai-page container">
    <h2>非遗 AI 咨询</h2>
    <p class="tip">输入您想了解的丽水非遗相关问题，AI 将为您解答（需登录）</p>
    <div class="chat-box">
      <div v-for="(msg, i) in messages" :key="i" :class="['msg-row', msg.role]">
        <div class="msg">
          <span class="label">{{ msg.role === 'user' ? '我' : 'AI' }}</span>
          <div class="text" v-html="msg.content"></div>
        </div>
      </div>
    </div>
    <form class="input-row" @submit.prevent="send">
      <input v-model="question" type="text" placeholder="输入问题，例如：丽水有哪些非遗项目？" />
      <button type="submit" class="btn btn-primary" :disabled="loading">发送</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { aiConsult } from '../api'

const question = ref('')
const messages = ref([])
const loading = ref(false)

async function send() {
  const q = question.value.trim()
  if (!q || loading.value) return
  messages.value.push({ role: 'user', content: q })
  question.value = ''
  loading.value = true
  try {
    const res = await aiConsult({ question: q })
    const answer = (res && res.data && res.data.answer) ? res.data.answer : (res && res.message) || '暂无回复'
    messages.value.push({ role: 'assistant', content: answer })
  } catch (e) {
    const msg = e.response?.data?.data?.answer || e.response?.data?.message || '请求失败，请稍后再试。'
    messages.value.push({ role: 'assistant', content: msg })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ai-page { max-width: 900px; margin: 0 auto; padding: 0 20px; }
.ai-page h2 { margin-bottom: 8px; }
.tip { color: #666; margin-bottom: 20px; font-size: 14px; }
.chat-box {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  min-height: 420px;
  max-height: 65vh;
  overflow-y: auto;
  margin-bottom: 20px;
  border: 1px solid #eee;
}
.msg-row { margin-bottom: 20px; display: flex; }
.msg-row.user { justify-content: flex-end; }
.msg-row.assistant { justify-content: flex-start; }
.msg { max-width: 75%; }
.msg .label { font-size: 12px; color: #999; margin-bottom: 6px; display: block; }
.msg-row.user .msg .label { text-align: right; }
.msg .text {
  line-height: 1.7;
  white-space: pre-wrap;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 15px;
}
.msg-row.assistant .msg .text {
  background: #f5f2ee;
  color: #2c1810;
  border: 1px solid #e8e2d9;
}
.msg-row.user .msg .text {
  background: #c41e3a;
  color: #fff;
  margin-left: auto;
}
.input-row { display: flex; gap: 12px; }
.input-row input { flex: 1; padding: 12px 16px; border: 1px solid #ddd; border-radius: 8px; font-size: 15px; }
</style>
