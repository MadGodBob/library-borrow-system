<script setup>
import { onMounted, reactive, ref } from 'vue'
import { deleteUserApi, getUserPageApi, saveUserApi, updateUserApi } from '@/api/library'

const loading = ref(false)
const errorMsg = ref('')
const dialogVisible = ref(false)
const dialogMode = ref('create')

const filter = reactive({
  username: '',
  phone: ''
})

const pager = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  records: []
})

const form = reactive({
  id: null,
  username: '',
  password: '',
  phone: '',
  email: '',
  role: 0,
  status: 1
})

async function fetchData() {
  loading.value = true
  errorMsg.value = ''
  pager.records = []
  try {
    const res = await getUserPageApi({
      pageNum: pager.pageNum,
      pageSize: pager.pageSize,
      username: filter.username,
      phone: filter.phone
    })
    pager.records = res.data?.records || []
    pager.total = res.data?.total || 0
  } catch (error) {
    errorMsg.value = error.message || '加载用户失败'
  } finally {
    loading.value = false
  }
}

function fillForm(row) {
  form.id = row.id
  form.username = row.username
  form.password = ''
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.role = row.role ?? 0
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
  form.username = ''
  form.password = ''
  form.phone = ''
  form.email = ''
  form.role = 0
  form.status = 1
}

async function submitForm() {
  errorMsg.value = ''
  try {
    if (form.id) {
      await updateUserApi({ ...form, password: form.password || undefined })
    } else {
      await saveUserApi({ ...form, password: form.password || '123456' })
    }
    closeDialog()
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '保存用户失败'
  }
}

async function removeUser(id) {
  errorMsg.value = ''
  try {
    await deleteUserApi(id)
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '删除用户失败'
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
      <div class="panel-title">用户管理</div>
      <div class="toolbar">
        <input v-model="filter.username" placeholder="用户名" />
        <input v-model="filter.phone" placeholder="手机号" />
        <button class="btn btn-primary" @click="pager.pageNum = 1; fetchData()">查询</button>
        <button class="btn btn-info" @click="openCreateDialog">新增用户</button>
      </div>
      <div v-if="errorMsg" style="margin-top: 10px; color: #c62828">{{ errorMsg }}</div>
    </div>

    <div class="panel">
      <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>手机</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="loading-cell">加载中...</td>
          </tr>
          <tr v-for="row in pager.records" :key="row.id">
            <td>{{ row.id }}</td>
            <td>{{ row.username }}</td>
            <td>{{ row.phone || '-' }}</td>
            <td>{{ row.email || '-' }}</td>
            <td>{{ Number(row.role) >= 1 ? '管理员' : '普通用户' }}</td>
            <td>
              <span class="status-badge" :class="Number(row.status) === 1 ? 'status-normal' : 'status-disabled'">
                {{ Number(row.status) === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>
              <div class="action-group">
                <button class="btn btn-warning" @click="openEditDialog(row)">编辑</button>
                <button class="btn btn-danger" @click="removeUser(row.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!loading && pager.records.length === 0">
            <td colspan="7" style="text-align: center; color: #6b7a90">暂无用户</td>
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
        <div class="modal-title">{{ dialogMode === 'create' ? '新增用户' : '编辑用户' }}</div>
        <div class="form-grid">
          <input v-model="form.username" placeholder="用户名" />
          <input v-model="form.password" placeholder="密码(编辑可留空)" />
          <input v-model="form.phone" placeholder="手机号" />
          <input v-model="form.email" placeholder="邮箱" />
          <select v-model="form.role">
            <option :value="0">普通用户</option>
            <option :value="1">管理员</option>
          </select>
          <select v-model="form.status">
            <option :value="1">正常</option>
            <option :value="0">禁用</option>
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

