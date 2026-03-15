<template>
  <div>
    <h2>传承人管理</h2>
    <div class="search-bar">
      <span class="search-label">条件搜索</span>
      <input v-model="keyword" type="text" placeholder="姓名、简介、区县" class="search-input" @keyup.enter="search()" />
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
        <tr><th>ID</th><th>姓名</th><th>区县</th><th>简介</th><th>代表作品</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="p in list" :key="p.id">
          <td>{{ p.id }}</td>
          <td>{{ p.name }}</td>
          <td>{{ p.region }}</td>
          <td class="cell-clip" :title="p.intro">{{ truncate(p.intro, 40) }}</td>
          <td class="cell-clip" :title="p.representativeWorks">{{ truncate(p.representativeWorks, 40) }}</td>
          <td>
            <button class="btn btn-default" @click="showEdit(p)">编辑</button>
            <button class="btn btn-default" @click="del(p.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="editing" class="modal" @click.self="editing = null">
      <div class="modal-box">
        <h3>{{ form.id ? '编辑' : '新增' }}传承人</h3>
        <div class="form-item"><label>姓名</label><input v-model="form.name" /></div>
        <div class="form-item"><label>所属区县</label>
          <select v-model="form.region">
            <option value="">请选择</option>
            <option v-for="r in LISHUI_REGIONS" :key="r" :value="r">{{ r }}</option>
          </select>
        </div>
        <div class="form-item"><label>简介</label><textarea v-model="form.intro" rows="3"></textarea></div>
        <div class="form-item"><label>代表作品</label><textarea v-model="form.representativeWorks" rows="3"></textarea></div>
        <div class="form-item"><label>传承故事</label><textarea v-model="form.story" rows="4"></textarea></div>
        <div class="form-item"><label>头像</label>
          <div class="image-picker">
            <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="onImageChange($event, 'avatar')" />
            <button type="button" class="btn btn-default" @click="avatarInput?.click()">选择图片</button>
          </div>
          <div v-if="form.avatar" class="img-preview">
            <img :src="imgSrc(form.avatar)" alt="头像预览" />
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
import { adminGetInheritors, adminCreateInheritor, adminUpdateInheritor, adminDeleteInheritor, uploadImage } from '../../api'
import { LISHUI_REGIONS } from '../../constants/regions'

const list = ref([])
const region = ref('')
const keyword = ref('')
const editing = ref(null)
const avatarInput = ref(null)
const form = reactive({ id: null, name: '', region: '', intro: '', representativeWorks: '', story: '', avatar: '' })

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

async function load() {
  const res = await adminGetInheritors({ keyword: keyword.value || undefined, region: region.value || undefined })
  list.value = res.data || []
}

function search() {
  load()
}

function resetSearch() {
  keyword.value = ''
  region.value = ''
  load()
}

function showEdit(row) {
  if (row) {
    form.id = row.id
    form.name = row.name
    form.region = row.region || ''
    form.intro = row.intro || ''
    form.representativeWorks = row.representativeWorks || ''
    form.story = row.story || ''
    form.avatar = row.avatar || ''
  } else {
    form.id = null
    form.name = form.region = form.intro = form.representativeWorks = form.story = form.avatar = ''
  }
  editing.value = {}
}

async function save() {
  if (!form.name) return alert('请填写姓名')
  try {
    if (form.id) {
      await adminUpdateInheritor(form.id, form)
    } else {
      await adminCreateInheritor(form)
    }
    editing.value = null
    load()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  }
}

async function del(id) {
  if (!confirm('确定删除？')) return
  try {
    await adminDeleteInheritor(id)
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
.cell-clip { max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: #fff; padding: 24px; border-radius: 8px; width: 500px; max-height: 90vh; overflow-y: auto; }
.form-item { margin-bottom: 12px; }
.form-item label { display: block; margin-bottom: 4px; }
.form-item input, .form-item textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.form-item select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.image-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.image-picker .hidden { display: none; }
.img-preview { margin-top: 10px; }
.img-preview img { max-width: 120px; max-height: 120px; object-fit: cover; border-radius: 8px; display: block; border: 1px solid #eee; }
.actions { margin-top: 16px; display: flex; gap: 8px; }
</style>
