<script setup>
import { onMounted, reactive, ref } from 'vue'
import { deleteBookApi, getBookPageApi, saveBookApi, updateBookApi } from '@/api/library'

const loading = ref(false)
const errorMsg = ref('')
const dialogVisible = ref(false)
const dialogMode = ref('create')

const filter = reactive({
  title: '',
  author: '',
  category: ''
})

const pager = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  records: []
})

const form = reactive({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  publishYear: '',
  category: '',
  status: 1
})

async function fetchData() {
  loading.value = true
  errorMsg.value = ''
  pager.records = []
  try {
    const res = await getBookPageApi({
      pageNum: pager.pageNum,
      pageSize: pager.pageSize,
      title: filter.title,
      author: filter.author,
      category: filter.category
    })
    pager.records = res.data?.records || []
    pager.total = res.data?.total || 0
  } catch (error) {
    errorMsg.value = error.message || '加载图书失败'
  } finally {
    loading.value = false
  }
}

function fillForm(row) {
  form.id = row.id
  form.isbn = row.isbn
  form.title = row.title
  form.author = row.author
  form.publisher = row.publisher || ''
  form.publishYear = row.publishYear || ''
  form.category = row.category || ''
  form.status = row.status ?? 1
}

function openCreateDialog() {
  resetForm()
  dialogMode.value = 'create'
  dialogVisible.value = true
}

function openEditDialog(row) {
  fillForm(row)
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

function closeDialog() {
  dialogVisible.value = false
  resetForm()
}

function resetForm() {
  form.id = null
  form.isbn = ''
  form.title = ''
  form.author = ''
  form.publisher = ''
  form.publishYear = ''
  form.category = ''
  form.status = 1
}

async function submitForm() {
  errorMsg.value = ''
  const payload = {
    ...form,
    publishYear: form.publishYear ? Number(form.publishYear) : null,
    status: Number(form.status)
  }
  try {
    if (form.id) {
      await updateBookApi(payload)
    } else {
      await saveBookApi(payload)
    }
    closeDialog()
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '保存图书失败'
  }
}

async function removeBook(id) {
  errorMsg.value = ''
  try {
    await deleteBookApi(id)
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '删除图书失败'
  }
}

function prevPage() {
  if (pager.pageNum > 1) {
    pager.pageNum -= 1
    fetchData()
  }
}

function nextPage() {
  if (pager.pageNum * pager.pageSize < pager.total) {
    pager.pageNum += 1
    fetchData()
  }
}

function onPageSizeChange() {
  pager.pageNum = 1
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="panel">
      <div class="panel-title">图书管理</div>
      <div class="toolbar">
        <input v-model="filter.title" placeholder="书名" />
        <input v-model="filter.author" placeholder="作者" />
        <input v-model="filter.category" placeholder="类别" />
        <button class="btn btn-primary" @click="pager.pageNum = 1; fetchData()">查询</button>
        <button class="btn btn-info" @click="openCreateDialog">新增图书</button>
      </div>
      <div v-if="errorMsg" style="margin-top: 10px; color: #c62828">{{ errorMsg }}</div>
    </div>

    <div class="panel">
      <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>ISBN</th>
            <th>书名</th>
            <th>作者</th>
            <th>出版社</th>
            <th>出版年</th>
            <th>类别</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="9" class="loading-cell">加载中...</td>
          </tr>
          <tr v-for="row in pager.records" :key="row.id">
            <td>{{ row.id }}</td>
            <td>{{ row.isbn }}</td>
            <td>{{ row.title }}</td>
            <td>{{ row.author }}</td>
            <td>{{ row.publisher || '-' }}</td>
            <td>{{ row.publishYear || '-' }}</td>
            <td>{{ row.category || '-' }}</td>
            <td>
              <span class="status-badge" :class="Number(row.status) === 1 ? 'status-available' : 'status-borrowed'">
                {{ Number(row.status) === 1 ? '可借阅' : '借出中' }}
              </span>
            </td>
            <td>
              <div class="action-group spacious">
                <button class="btn btn-warning" @click="openEditDialog(row)">编辑</button>
                <button class="btn btn-danger" @click="removeBook(row.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!loading && pager.records.length === 0">
            <td colspan="9" style="text-align: center; color: #6b7a90">暂无图书</td>
          </tr>
        </tbody>
      </table>
      </div>

      <div class="pagination">
        <button class="btn" @click="prevPage">上一页</button>
        <span>第 {{ pager.pageNum }} 页 / 共 {{ Math.max(1, Math.ceil(pager.total / pager.pageSize)) }} 页</span>
        <button class="btn" @click="nextPage">下一页</button>
        <select v-model.number="pager.pageSize" @change="onPageSizeChange">
          <option :value="5">5 / 页</option>
          <option :value="10">10 / 页</option>
          <option :value="20">20 / 页</option>
        </select>
      </div>
    </div>

    <div v-if="dialogVisible" class="modal-mask" @click.self="closeDialog">
      <div class="modal-card">
        <div class="modal-title">{{ dialogMode === 'create' ? '新增图书' : '编辑图书' }}</div>
        <div class="form-grid">
          <input v-model="form.isbn" placeholder="ISBN" />
          <input v-model="form.title" placeholder="书名" />
          <input v-model="form.author" placeholder="作者" />
          <input v-model="form.publisher" placeholder="出版社" />
          <input v-model="form.publishYear" placeholder="出版年份" />
          <input v-model="form.category" placeholder="类别" />
          <select v-model="form.status">
            <option :value="1">可借阅</option>
            <option :value="0">借出中</option>
          </select>
        </div>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="submitForm">保存</button>
          <button class="btn" @click="closeDialog">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

