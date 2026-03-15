<template>
  <div class="profile-page container">
    <h1 class="page-title">个人信息</h1>
    <p class="page-desc">修改昵称、头像或登录密码</p>

    <form v-if="user" @submit.prevent="submit" class="profile-form">
      <div class="form-item">
        <label>账号</label>
        <input :value="user.username" type="text" disabled class="readonly" />
        <span class="hint">账号不可修改</span>
      </div>
      <div class="form-item">
        <label>昵称</label>
        <input v-model="form.nickname" type="text" placeholder="选填，显示在页面上" maxlength="50" />
      </div>
      <div class="form-item">
        <label>头像</label>
        <div class="image-picker">
          <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="onAvatarChange" />
          <button type="button" class="btn btn-default" @click="avatarInput?.click()">选择图片</button>
          <span v-if="form.avatar" class="path">{{ form.avatar }}</span>
        </div>
        <div v-if="form.avatar" class="avatar-preview">
          <img :src="avatarPreviewSrc" alt="头像预览" @error="avatarError = true" />
          <span v-if="avatarError" class="preview-err">图片加载失败</span>
        </div>
      </div>
      <div class="form-item">
        <label>新密码</label>
        <input v-model="form.newPassword" type="password" placeholder="不修改请留空" autocomplete="new-password" />
      </div>
      <div class="form-item">
        <label>确认新密码</label>
        <input v-model="form.confirmPassword" type="password" placeholder="再次输入新密码" autocomplete="new-password" />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <p v-if="success" class="success">保存成功</p>
      <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
        {{ loading ? '保存中...' : '保 存' }}
      </button>
    </form>
    <p v-else class="loading-tip">加载中...</p>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCurrentUser, updateProfile, uploadImage } from '../api'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const user = ref(null)
const avatarInput = ref(null)
const form = reactive({
  nickname: '',
  avatar: '',
  newPassword: '',
  confirmPassword: '',
})
const err = ref('')
const success = ref(false)
const loading = ref(false)
const avatarError = ref(false)
const avatarPreviewSrc = ref('')

function getPreviewSrc(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  const base = import.meta.env.VITE_API_BASE_URL || ''
  return base ? base.replace(/\/$/, '') + path : path
}

async function onAvatarChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  avatarError.value = false
  try {
    const res = await uploadImage(file)
    if (res.success && res.url) {
      form.avatar = res.url
      avatarPreviewSrc.value = getPreviewSrc(res.url)
    } else {
      err.value = res.message || '上传失败'
    }
  } catch (e) {
    err.value = e.response?.data?.message || '上传失败'
  } finally {
    e.target.value = ''
  }
}

onMounted(async () => {
  try {
    const res = await getCurrentUser()
    if (res.success && res.data) {
      user.value = res.data
      form.nickname = res.data.nickname || ''
      form.avatar = res.data.avatar || ''
      avatarPreviewSrc.value = getPreviewSrc(res.data.avatar)
    }
  } catch (_) {
    err.value = '获取用户信息失败'
  }
})

async function submit() {
  err.value = ''
  success.value = false
  if (form.newPassword && form.newPassword !== form.confirmPassword) {
    err.value = '两次输入的新密码不一致'
    return
  }
  if (form.newPassword && form.newPassword.length < 6) {
    err.value = '新密码至少 6 位'
    return
  }
  loading.value = true
  try {
    const payload = {
      nickname: form.nickname || undefined,
      avatar: form.avatar || undefined,
      newPassword: form.newPassword || undefined,
    }
    const res = await updateProfile(payload)
    if (res.success && res.data) {
      auth.setUser(res.data)
      success.value = true
    } else {
      err.value = res.message || '保存失败'
    }
  } catch (e) {
    err.value = e.response?.data?.message || '保存失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 480px;
  margin: 0 auto;
  padding: 24px 16px;
}
.page-title {
  font-size: 1.5rem;
  color: #2c1810;
  margin-bottom: 6px;
}
.page-desc {
  font-size: 14px;
  color: #6b5b4f;
  margin-bottom: 28px;
}
.profile-form .form-item {
  margin-bottom: 20px;
}
.profile-form .form-item label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}
.profile-form .form-item input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
}
.profile-form .form-item input:focus {
  outline: none;
  border-color: #c41e3a;
}
.profile-form .form-item input.readonly {
  background: #f5f5f5;
  color: #666;
  cursor: not-allowed;
}
.hint {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
  display: block;
}
.image-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.image-picker .hidden { display: none; }
.image-picker .path { font-size: 12px; color: #666; word-break: break-all; }
.avatar-preview {
  margin-top: 10px;
}
.avatar-preview img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}
.avatar-preview .preview-err {
  font-size: 12px;
  color: #c41e3a;
}
.err {
  color: #c41e3a;
  font-size: 13px;
  margin-bottom: 12px;
}
.success {
  color: #2d6a4f;
  font-size: 14px;
  margin-bottom: 12px;
}
.btn-block {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 8px;
}
.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.loading-tip {
  color: #666;
  text-align: center;
  padding: 24px;
}
</style>
