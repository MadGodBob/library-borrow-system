<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getBorrowPageApi, returnBookApi } from '@/api/library'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const loading = ref(false)
const errorMsg = ref('')

const filter = reactive({
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

function canReturn(row) {
  return Number(row.status) === 1
}

async function fetchData() {
  loading.value = true
  errorMsg.value = ''
  pager.records = []
  try {
    const res = await getBorrowPageApi({
      pageNum: pager.pageNum,
      pageSize: pager.pageSize,
      userName: auth.username,
      bookTitle: filter.bookTitle,
      status: filter.status === '' ? undefined : Number(filter.status)
    })
    pager.records = (res.data?.records || []).filter((item) => item.userId === auth.user?.id)
    pager.total = res.data?.total || 0
  } catch (error) {
    errorMsg.value = error.message || '查询借阅记录失败'
  } finally {
    loading.value = false
  }
}

async function returnBook(row) {
  errorMsg.value = ''
  try {
    await returnBookApi({
      userId: auth.user?.id,
      bookId: row.bookId
    })
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '还书失败'
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
      <div class="panel-title">我的借阅</div>
      <div class="toolbar">
        <input v-model="filter.bookTitle" placeholder="图书名称" />
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
            <th>图书ID</th>
            <th>图书名称</th>
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
            <td>{{ row.bookId }}</td>
            <td>{{ row.bookTitle }}</td>
            <td>{{ row.borrowTime || '-' }}</td>
            <td>{{ row.returnTime || '-' }}</td>
            <td>
              <span class="status-badge" :class="canReturn(row) ? 'status-active' : 'status-returned'">
                {{ statusMap[row.status] || '未知' }}
              </span>
            </td>
            <td>
              <button
                class="btn"
                :class="canReturn(row) ? 'btn-info' : ''"
                :disabled="!canReturn(row)"
                @click="returnBook(row)"
              >
                {{ canReturn(row) ? '归还' : '已归还' }}
              </button>
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

