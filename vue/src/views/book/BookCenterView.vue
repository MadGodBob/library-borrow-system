<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getBookPageApi, lendBookApi } from '@/api/library'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

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

const loading = ref(false)
const errorMsg = ref('')

const statusMap = {
  1: '可借阅',
  0: '借出中'
}

function canLend(row) {
  return Number(row.status) === 1
}

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

async function lendBook(row) {
  if (!auth.user?.id) {
    return
  }
  errorMsg.value = ''
  try {
    await lendBookApi({
      userId: auth.user.id,
      userName: auth.user.username,
      bookId: row.id,
      bookTitle: row.title
    })
    await fetchData()
  } catch (error) {
    errorMsg.value = error.message || '借书失败'
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
      <div class="panel-title">图书中心</div>
      <div class="toolbar">
        <input v-model="filter.title" placeholder="书名" />
        <input v-model="filter.author" placeholder="作者" />
        <input v-model="filter.category" placeholder="类别" />
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
            <th>ISBN</th>
            <th>书名</th>
            <th>作者</th>
            <th>出版社</th>
            <th>类别</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="8" class="loading-cell">加载中...</td>
          </tr>
          <tr v-for="row in pager.records" :key="row.id">
            <td>{{ row.id }}</td>
            <td>{{ row.isbn }}</td>
            <td>{{ row.title }}</td>
            <td>{{ row.author }}</td>
            <td>{{ row.publisher }}</td>
            <td>{{ row.category }}</td>
            <td>
              <span class="status-badge" :class="canLend(row) ? 'status-available' : 'status-borrowed'">
                {{ statusMap[row.status] || '未知' }}
              </span>
            </td>
            <td>
              <button
                class="btn"
                :class="canLend(row) ? 'btn-success' : ''"
                :disabled="!canLend(row)"
                @click="lendBook(row)"
              >
                {{ canLend(row) ? '借书' : '借出中' }}
              </button>
            </td>
          </tr>
          <tr v-if="!loading && pager.records.length === 0">
            <td colspan="8" style="text-align: center; color: #6b7a90">暂无图书</td>
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

