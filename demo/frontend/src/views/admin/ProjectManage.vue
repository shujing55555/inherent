<template>
  <div>
    <h2>非遗项目管理</h2>
    <div class="search-bar">
      <span class="search-label">条件搜索</span>
      <input v-model="keyword" type="text" placeholder="标题、描述" class="search-input" @keyup.enter="search()" />
      <select v-model="region" class="search-select">
        <option value="">全部区县</option>
        <option v-for="r in LISHUI_REGIONS" :key="r" :value="r">{{ r }}</option>
      </select>
      <select v-model="heritageLevel" class="search-select">
        <option value="">全部级别</option>
        <option v-for="l in HERITAGE_LEVELS" :key="l" :value="l">{{ l }}</option>
      </select>
      <button type="button" class="btn btn-primary" @click="search()">搜索</button>
      <button type="button" class="btn btn-default" @click="resetSearch()">重置</button>
    </div>
    <div class="toolbar">
      <button class="btn btn-primary" @click="showEdit()">新增</button>
    </div>
    <table class="table">
      <thead>
        <tr>
          <th>ID</th><th>标题</th><th>所属区县</th><th>项目类型</th><th>遗产级别</th><th>批准时间</th><th>描述摘要</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in list" :key="p.id">
          <td>{{ p.id }}</td>
          <td class="cell-clip" :title="p.title">{{ truncate(p.title, 25) }}</td>
          <td>{{ p.category }}</td>
          <td>{{ p.ichType || '—' }}</td>
          <td>{{ p.heritageLevel || '—' }}</td>
          <td>{{ formatApprovalYear(p.approvalTime) }}</td>
          <td class="cell-clip" :title="p.description">{{ truncate(p.description, 50) }}</td>
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

    <!-- 新增/编辑弹窗 -->
    <div v-if="editing" class="modal" @click.self="editing = null">
      <div class="modal-box">
        <h3>{{ form.id ? '编辑' : '新增' }}非遗项目</h3>
        <div class="form-item"><label>标题</label><input v-model="form.title" /></div>
        <div class="form-item"><label>所属区县</label>
          <select v-model="form.category">
            <option value="">请选择</option>
            <option v-for="r in LISHUI_REGIONS" :key="r" :value="r">{{ r }}</option>
          </select>
        </div>
        <div class="form-item"><label>项目类型</label>
          <select v-model="form.ichType">
            <option value="">请选择</option>
            <option v-for="t in PROJECT_TYPES" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>
        <div class="form-item"><label>遗产级别</label>
          <select v-model="form.heritageLevel">
            <option value="">请选择</option>
            <option v-for="l in HERITAGE_LEVELS" :key="l" :value="l">{{ l }}</option>
          </select>
        </div>
        <div class="form-item"><label>批准时间</label><input v-model="form.approvalYear" type="number" placeholder="仅填年份，如 2024" min="1900" max="2100" step="1" /></div>
        <div class="form-item"><label>描述</label><textarea v-model="form.description" rows="4"></textarea></div>
        <div class="form-item"><label>图片</label>
          <div class="image-picker">
            <input ref="imagesInput" type="file" accept="image/*" class="hidden" @change="onImageAdd" />
            <button type="button" class="btn btn-default" @click="imagesInput?.click()">选择图片</button>
            <span class="hint">仅支持一张图片</span>
          </div>
          <div v-if="form.images" class="img-preview">
            <img :src="imgSrc(imageList[0])" alt="图片预览" />
          </div>
        </div>
        <div class="form-item"><label>音频URL</label><input v-model="form.audio" /></div>
        <div class="actions">
          <button class="btn btn-default" @click="editing = null">取消</button>
          <button class="btn btn-primary" @click="save">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { adminGetProjects, adminCreateProject, adminUpdateProject, adminDeleteProject, uploadImage } from '../../api'
import { LISHUI_REGIONS } from '../../constants/regions'

const HERITAGE_LEVELS = ['国家级', '省级', '市级', '县级']
const PROJECT_TYPES = [
  '民间文学',
  '传统音乐',
  '传统舞蹈',
  '传统戏剧',
  '曲艺',
  '传统体育、游艺与杂技',
  '传统美术',
  '传统技艺',
  '传统医药',
  '民俗'
]

const list = ref([])
const page = ref(0)
const totalPages = ref(0)
const keyword = ref('')
const region = ref('')
const heritageLevel = ref('')
const ichType = ref('')
const editing = ref(null)
const imagesInput = ref(null)
const form = reactive({ title: '', category: '', ichType: '', description: '', images: '', approvalYear: '', heritageLevel: '' })

const imageList = computed(() => {
  if (!form.images) return []
  return form.images.split(',').map(s => s.trim()).filter(Boolean)
})

function imgSrc(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : url
}

function truncate(s, len) {
  if (s == null || s === '') return ''
  const t = String(s).trim()
  return t.length <= len ? t : t.slice(0, len) + '…'
}

function formatTime(t) {
  if (!t) return '—'
  return new Date(t).toISOString().slice(0, 10)
}

/** 批准时间只显示年份 */
function formatApprovalYear(t) {
  if (!t) return '—'
  const y = new Date(t).getFullYear()
  return isNaN(y) ? '—' : String(y)
}

/** 用于 date 输入框：取 YYYY-MM-DD */
function toDateOnly(v) {
  if (!v) return ''
  return new Date(v).toISOString().slice(0, 10)
}

async function onImageAdd(e) {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const res = await uploadImage(file)
    if (res.success && res.url) {
      form.images = res.url
    }
  } finally {
    e.target.value = ''
  }
}

async function load(p = 0) {
  const res = await adminGetProjects({
    keyword: keyword.value || undefined,
    region: region.value || undefined,
    heritageLevel: heritageLevel.value || undefined,
    ichType: ichType.value || undefined,
    page: p,
    size: 10,
  })
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
  heritageLevel.value = ''
  ichType.value = ''
  load(0)
}

function showEdit(row) {
  if (row) {
    form.id = row.id
    form.title = row.title
    form.category = row.category || ''
    form.ichType = row.ichType || ''
    form.description = row.description || ''
    form.images = row.images || ''
    form.approvalYear = row.approvalTime ? String(new Date(row.approvalTime).getFullYear()) : ''
    form.heritageLevel = row.heritageLevel || ''
  } else {
    form.id = null
    form.title = form.category = form.ichType = form.description = form.images = form.heritageLevel = ''
    form.approvalYear = ''
  }
  editing.value = {}
}

async function save() {
  if (!form.title) return alert('请填写标题')
  const payload = {
    title: form.title,
    category: form.category,
    ichType: form.ichType || null,
    description: form.description,
    images: form.images,
    heritageLevel: form.heritageLevel || null,
    approvalTime: form.approvalYear ? (form.approvalYear + '-01-01T00:00:00') : null,
  }
  try {
    if (form.id) {
      await adminUpdateProject(form.id, payload)
    } else {
      await adminCreateProject(payload)
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
    await adminDeleteProject(id)
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
.toolbar { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; align-items: center; }
.table { width: 100%; border-collapse: collapse; background: #fff; }
.table th, .table td { border: 1px solid #eee; padding: 10px; text-align: left; }
.cell-clip { max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pagination { margin-top: 16px; display: flex; align-items: center; gap: 12px; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-box { background: #fff; padding: 24px; border-radius: 8px; width: 500px; max-height: 90vh; overflow-y: auto; }
.modal-box h3 { margin-bottom: 16px; }
.form-item { margin-bottom: 12px; }
.form-item label { display: block; margin-bottom: 4px; font-size: 14px; }
.form-item input, .form-item textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.form-item select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.image-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.image-picker .hidden { display: none; }
.image-picker .hint { font-size: 12px; color: #888; }
.img-preview { margin-top: 10px; }
.img-preview img { max-width: 120px; max-height: 120px; object-fit: cover; border-radius: 8px; display: block; border: 1px solid #eee; }
.img-preview-multi { display: flex; flex-wrap: wrap; gap: 8px; }
.img-preview-multi img { display: block; }
.actions { margin-top: 16px; display: flex; gap: 8px; }
</style>
