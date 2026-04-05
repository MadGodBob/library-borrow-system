<script setup>
import { onMounted, reactive, ref } from 'vue'
import { deleteBorrowApi, getBorrowPageApi, updateBorrowApi } from '@/api/library'

const loading = ref(false)
const errorMsg = ref('')

const filter = reactive({
  userName: '',
  bookTitle: '',
  status: ''
})

const pager = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  records: []
})

const statusMap = {
  1: '借阅中',
  0: '已归还'
}

async function fetchData() {
  loading.value = true
  errorMsg.value = ''
  pager.records = []
  try {
    const res = await getBorrowPageApi({
      pageNum: pager.pageNum,
      pageSize: pager.pageSize,
      userName: filter.userName,
      bookTitle: filter.bookTitle,
      status: filter.status === '' ? undefined : Number(filter.status)
    })
    pager.records = res.data?.records || []
    pager.total = res.data?.total || 0
  } catch (error) {
    errorMsg.value = error.message || '加载借阅记录失败'
  } finally {
    loading.value = false
  }
}

async function markReturned(row) {
  errorMsg.value = ''
  try {
    await updateBorrowApi({
      ...row,
      status: 0,
      returnTime: new Date().toISOString().slice(0, 19)
    })
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '更新借阅状态失败'
  }
}

async function removeBorrow(id) {
  errorMsg.value = ''
  try {
    await deleteBorrowApi(id)
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '删除借阅记录失败'
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
      <div class="panel-title">借阅管理</div>
      <div class="toolbar">
        <input v-model="filter.userName" placeholder="借阅人" />
        <input v-model="filter.bookTitle" placeholder="图书名" />
        <select v-model="filter.status">
          <option value="">全部状态</option>
          <option value="1">借阅中</option>
          <option value="0">已归还</option>
        </select>
        <button class="btn btn-primary" @click="pager.pageNum = 1; fetchData()">查询</button>
      </div>
      <div v-if="errorMsg" style="margin-top: 10px; color: #c62828">{{ errorMsg }}</div>
    </div>

    <div class="panel">
      <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户</th>
            <th>图书</th>
            <th>借阅时间</th>
            <th>归还时间</th>
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
            <td>{{ row.userName }}(ID:{{ row.userId }})</td>
            <td>{{ row.bookTitle }}(ID:{{ row.bookId }})</td>
            <td>{{ row.borrowTime || '-' }}</td>
            <td>{{ row.returnTime || '-' }}</td>
            <td>
              <span class="status-badge" :class="Number(row.status) === 1 ? 'status-active' : 'status-returned'">
                {{ statusMap[row.status] || '未知' }}
              </span>
            </td>
            <td>
              <div class="action-group">
                <button class="btn btn-info" :disabled="Number(row.status) !== 1" @click="markReturned(row)">强制归还</button>
                <button class="btn btn-danger" @click="removeBorrow(row.id)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!loading && pager.records.length === 0">
            <td colspan="7" style="text-align: center; color: #6b7a90">暂无记录</td>
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
  </div>
</template>

