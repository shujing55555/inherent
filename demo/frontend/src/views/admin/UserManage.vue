<template>
  <div>
    <h2>用户管理</h2>
    <div class="search-bar">
      <span class="search-label">条件搜索</span>
      <input v-model="keyword" type="text" placeholder="账号、昵称" class="search-input" @keyup.enter="search()" />
      <select v-model="roleFilter" class="search-select">
        <option value="">全部角色</option>
        <option value="USER">普通用户</option>
        <option value="ADMIN">管理员</option>
      </select>
      <button type="button" class="btn btn-primary" @click="search()">搜索</button>
      <button type="button" class="btn btn-default" @click="resetSearch()">重置</button>
    </div>
    <div class="toolbar">
      <button class="btn btn-primary" @click="showEdit()">新增用户</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>头像</th>
          <th>账号</th>
          <th>邮箱</th>
          <th>手机号</th>
          <th>昵称</th>
          <th>角色</th>
          <th>注册时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in list" :key="p.id">
          <td>{{ p.id }}</td>
          <td class="td-avatar">
            <img v-if="p.avatar" :src="imgSrc(p.avatar)" alt="头像" class="avatar-thumb" />
            <span v-else class="no-avatar">—</span>
          </td>
          <td>{{ p.username }}</td>
          <td class="cell-clip" :title="p.email">{{ p.email || '—' }}</td>
          <td>{{ p.phone || '—' }}</td>
          <td>{{ p.nickname }}</td>
          <td>{{ p.role }}</td>
          <td>{{ formatTime(p.createTime) }}</td>
          <td>
            <button class="btn btn-default" @click="showEdit(p)">编辑</button>
            <button class="btn btn-default" @click="del(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="editing" class="modal" @click.self="editing = null">
      <div class="modal-box">
        <h3>{{ form.id ? '编辑' : '新增' }}用户</h3>
        <div class="form-item"><label>账号</label><input v-model="form.username" :readonly="!!form.id" /></div>
        <div class="form-item"><label>密码</label><input v-model="form.password" type="password" :placeholder="form.id ? '不填则不修改' : '必填'" /></div>
        <div class="form-item"><label>昵称</label><input v-model="form.nickname" /></div>
        <div class="form-item"><label>头像</label>
          <div class="image-picker">
            <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="onImageChange($event, 'avatar')" />
            <button type="button" class="btn btn-default" @click="avatarInput?.click()">选择图片</button>
          </div>
          <div v-if="form.avatar" class="img-preview">
            <img :src="imgSrc(form.avatar)" alt="头像预览" />
          </div>
        </div>
        <div class="form-item"><label>角色</label>
          <select v-model="form.role">
            <option value="USER">普通用户</option>
            <option value="ADMIN">管理员</option>
          </select>
        </div>
        <div class="actions">
          <button class="btn btn-default" @click="editing = null">取消</button>
          <button class="btn btn-primary" @click="save">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminGetUsers, adminCreateUser, adminUpdateUser, adminDeleteUser, uploadImage } from '../../api'

const list = ref([])
const keyword = ref('')
const roleFilter = ref('')
const editing = ref(null)
const avatarInput = ref(null)
const form = reactive({ id: null, username: '', password: '', nickname: '', avatar: '', email: '', phone: '', role: 'USER' })
async function onImageChange(e, field) {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const res = await uploadImage(file)
    if (res.success && res.url) form[field] = res.url
  } finally {
    e.target.value = ''
  }
}

function imgSrc(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : url
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toISOString().slice(0, 10)
}

async function load() {
  const res = await adminGetUsers({
    keyword: keyword.value || undefined,
    role: roleFilter.value || undefined,
  })
  list.value = res.data || []
}

function search() {
  load()
}

function resetSearch() {
  keyword.value = ''
  roleFilter.value = ''
  load()
}

function showEdit(row) {
  if (row) {
    form.id = row.id
    form.username = row.username
    form.password = ''
    form.nickname = row.nickname || ''
    form.avatar = row.avatar || ''
    form.email = row.email || ''
    form.phone = row.phone || ''
    form.role = row.role || 'USER'
  } else {
    form.id = null
    form.username = form.password = form.nickname = form.avatar = form.email = form.phone = ''
    form.role = 'USER'
  }
  editing.value = {}
}

async function save() {
  if (!form.username) return alert('请填写账号')
  const usernameReg = /^[A-Za-z][A-Za-z0-9]{3,9}$/
  if (!usernameReg.test(form.username)) {
    return alert('账号需以字母开头，仅包含字母和数字，长度4-10位')
  }
  if (!form.id) {
    if (!form.password) return alert('请填写密码')
    if (form.password.length < 3 || form.password.length > 12) {
      return alert('密码长度需为3-12位')
    }
  } else if (form.password && (form.password.length < 3 || form.password.length > 12)) {
    return alert('密码长度需为3-12位')
  }
  if (form.phone && !/^\d{11}$/.test(form.phone)) {
    return alert('手机号必须为11位数字')
  }
  if (form.email) {
    const emailReg = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/
    if (!emailReg.test(form.email)) {
      return alert('邮箱格式不正确')
    }
  }
  try {
    if (form.id) {
      await adminUpdateUser(form.id, {
        nickname: form.nickname,
        avatar: form.avatar,
        role: form.role,
        password: form.password || undefined,
        email: form.email || undefined,
        phone: form.phone || undefined,
      })
    } else {
      await adminCreateUser({
        username: form.username,
        password: form.password,
        nickname: form.nickname,
        avatar: form.avatar,
        email: form.email || undefined,
        phone: form.phone || undefined,
        role: form.role,
      })
    }
    editing.value = null
    load()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  }
}

async function del(id) {
  if (!confirm('确定删除该用户？')) return
  try {
    await adminDeleteUser(id)
    load()
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
  }
}

onMounted(() => load())
</script>

<style scoped>
.search-bar { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; margin-bottom: 12px; padding: 12px; background: #f8f9fa; border-radius: 6px; }
.search-label { font-size: 14px; color: #333; font-weight: 500; }
.search-input { padding: 8px 12px; width: 220px; border: 1px solid #ddd; border-radius: 4px; }
.search-select { padding: 8px 12px; min-width: 120px; border: 1px solid #ddd; border-radius: 4px; }
.toolbar { margin-bottom: 16px; display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.table { width: 100%; border-collapse: collapse; background: #fff; }
.table th, .table td { border: 1px solid #eee; padding: 10px; text-align: left; }
.td-avatar { width: 56px; text-align: center; }
.avatar-thumb { width: 36px; height: 36px; object-fit: cover; border-radius: 50%; display: inline-block; vertical-align: middle; }
.no-avatar { color: #999; }
.cell-clip { max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: #fff; padding: 24px; border-radius: 8px; width: 400px; }
.form-item { margin-bottom: 12px; }
.form-item label { display: block; margin-bottom: 4px; }
.form-item input, .form-item select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.image-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.image-picker .hidden { display: none; }
.img-preview { margin-top: 10px; }
.img-preview img { max-width: 120px; max-height: 120px; object-fit: cover; border-radius: 8px; display: block; border: 1px solid #eee; }
.actions { margin-top: 16px; display: flex; gap: 8px; }
</style>
