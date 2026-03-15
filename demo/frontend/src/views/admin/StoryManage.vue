<template>
  <div>
    <h2>传承故事管理</h2>
    <div class="search-bar">
      <span class="search-label">条件搜索</span>
      <input v-model="keyword" type="text" placeholder="标题、内容" class="search-input" @keyup.enter="search()" />
      <select v-model="region" class="search-select">
        <option value="">全部区县</option>
        <option v-for="r in LISHUI_REGIONS" :key="r" :value="r">{{ r }}</option>
      </select>
      <button type="button" class="btn btn-primary" @click="search()">搜索</button>
      <button type="button" class="btn btn-default" @click="resetSearch()">重置</button>
    </div>
    <div class="toolbar">
      <button class="btn btn-primary" @click="showEdit()">新增</button>
    </div>
    <table class="table">
      <thead>
        <tr><th>ID</th><th>标题</th><th>区县</th><th>内容摘要</th><th>传承人ID</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="p in list" :key="p.id">
          <td>{{ p.id }}</td>
          <td class="cell-clip" :title="p.title">{{ truncate(p.title, 30) }}</td>
          <td>{{ p.region }}</td>
          <td class="cell-clip" :title="p.content">{{ truncate(p.content, 50) }}</td>
          <td>{{ p.inheritorId ?? '-' }}</td>
          <td>
            <button class="btn btn-default" @click="showEdit(p)">编辑</button>
            <button class="btn btn-default" @click="del(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="totalPages > 1" class="pagination">
      <button class="btn btn-default" :disabled="page === 0" @click="load(page - 1)">上一页</button>
      <span>{{ page + 1 }} / {{ totalPages }}</span>
      <button class="btn btn-default" :disabled="page >= totalPages - 1" @click="load(page + 1)">下一页</button>
    </div>

    <div v-if="editing" class="modal" @click.self="editing = null">
      <div class="modal-box">
        <h3>{{ form.id ? '编辑' : '新增' }}传承故事</h3>
        <div class="form-item"><label>标题</label><input v-model="form.title" /></div>
        <div class="form-item"><label>所属区县</label>
          <select v-model="form.region">
            <option value="">请选择</option>
            <option v-for="r in LISHUI_REGIONS" :key="r" :value="r">{{ r }}</option>
          </select>
        </div>
        <div class="form-item"><label>内容</label><textarea v-model="form.content" rows="6"></textarea></div>
        <div class="form-item"><label>传承人ID(可选)</label><input v-model.number="form.inheritorId" type="number" /></div>
        <div class="form-item"><label>封面图</label>
          <div class="image-picker">
            <input ref="coverInput" type="file" accept="image/*" class="hidden" @change="onImageChange($event, 'coverImage')" />
            <button type="button" class="btn btn-default" @click="coverInput?.click()">选择图片</button>
          </div>
          <div v-if="form.coverImage" class="img-preview">
            <img :src="imgSrc(form.coverImage)" alt="封面预览" />
          </div>
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
import { adminGetStories, adminCreateStory, adminUpdateStory, adminDeleteStory, uploadImage } from '../../api'
import { LISHUI_REGIONS } from '../../constants/regions'

const list = ref([])
const page = ref(0)
const totalPages = ref(0)
const region = ref('')
const keyword = ref('')
const editing = ref(null)
const form = reactive({ id: null, title: '', region: '', content: '', inheritorId: null, coverImage: '' })
const coverInput = ref(null)
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

function truncate(s, len) {
  if (s == null || s === '') return ''
  const t = String(s).trim()
  return t.length <= len ? t : t.slice(0, len) + '…'
}

async function load(p = 0) {
  const res = await adminGetStories({ keyword: keyword.value || undefined, region: region.value || undefined, page: p, size: 10 })
  list.value = res.data || []
  totalPages.value = res.totalPages || 0
  page.value = p
}

function search() {
  load(0)
}

function resetSearch() {
  keyword.value = ''
  region.value = ''
  load(0)
}

function showEdit(row) {
  if (row) {
    form.id = row.id
    form.title = row.title
    form.region = row.region || ''
    form.content = row.content || ''
    form.inheritorId = row.inheritorId || ''
    form.coverImage = row.coverImage || ''
  } else {
    form.id = null
    form.title = form.region = form.content = form.coverImage = ''
    form.inheritorId = null
  }
  editing.value = {}
}

async function save() {
  if (!form.title) return alert('请填写标题')
  const payload = { ...form }
  if (!payload.inheritorId) payload.inheritorId = null
  try {
    if (form.id) {
      await adminUpdateStory(form.id, payload)
    } else {
      await adminCreateStory(payload)
    }
    editing.value = null
    load(page.value)
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  }
}

async function del(id) {
  if (!confirm('确定删除？')) return
  try {
    await adminDeleteStory(id)
    load(page.value)
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
  }
}

onMounted(() => load(0))
</script>

<style scoped>
.search-bar { display: flex; flex-wrap: wrap; align-items: center; gap: 10px; margin-bottom: 12px; padding: 12px; background: #f8f9fa; border-radius: 6px; }
.search-label { font-size: 14px; color: #333; font-weight: 500; }
.search-input { padding: 8px 12px; width: 220px; border: 1px solid #ddd; border-radius: 4px; }
.search-select { padding: 8px 12px; min-width: 120px; border: 1px solid #ddd; border-radius: 4px; }
.toolbar { margin-bottom: 16px; display: flex; flex-wrap: wrap; gap: 8px; align-items: center; }
.form-item select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.table { width: 100%; border-collapse: collapse; background: #fff; }
.table th, .table td { border: 1px solid #eee; padding: 10px; text-align: left; }
.cell-clip { max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pagination { margin-top: 16px; display: flex; align-items: center; gap: 12px; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: #fff; padding: 24px; border-radius: 8px; width: 500px; max-height: 90vh; overflow-y: auto; }
.form-item { margin-bottom: 12px; }
.form-item label { display: block; margin-bottom: 4px; }
.form-item input, .form-item textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.image-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.image-picker .hidden { display: none; }
.img-preview { margin-top: 10px; }
.img-preview img { max-width: 200px; max-height: 120px; object-fit: cover; border-radius: 8px; display: block; border: 1px solid #eee; }
.actions { margin-top: 16px; display: flex; gap: 8px; }
</style>
