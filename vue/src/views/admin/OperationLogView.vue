<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getLogPageApi } from '@/api/library'

const loading = ref(false)
const errorMsg = ref('')

const filter = reactive({
  userName: '',
  operation: ''
})

const pager = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  records: []
})

const operationTypeMap = {
  0: '用户注册',
  1: '用户登录',
  2: '更新用户',
  3: '删除用户',
  4: '新增图书',
  5: '更新图书',
  6: '删除图书',
  7: '借书',
  8: '还书',
  9: '其他'
}

function formatOperationType(type) {
  return operationTypeMap[type] || `未知类型(${type})`
}

async function fetchData() {
  loading.value = true
  errorMsg.value = ''
  pager.records = []
  try {
    const res = await getLogPageApi({
      pageNum: pager.pageNum,
      pageSize: pager.pageSize,
      userName: filter.userName,
      operation: filter.operation
    })
    pager.records = res.data?.records || []
    pager.total = res.data?.total || 0
  } catch (error) {
    errorMsg.value = error.message || '加载操作日志失败'
  } finally {
    loading.value = false
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
      <div class="panel-title">操作日志</div>
      <div class="toolbar">
        <input v-model="filter.userName" placeholder="操作人" />
        <input v-model="filter.operation" placeholder="操作内容" />
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
            <th>操作人</th>
            <th>类型</th>
            <th>内容</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="loading-cell">加载中...</td>
          </tr>
          <tr v-for="row in pager.records" :key="row.id">
            <td>{{ row.id }}</td>
            <td>{{ row.userName }}(ID:{{ row.userId }})</td>
            <td>{{ formatOperationType(row.operationType) }}</td>
            <td>{{ row.operation }}</td>
            <td>{{ row.timestamp }}</td>
          </tr>
          <tr v-if="!loading && pager.records.length === 0">
            <td colspan="5" style="text-align: center; color: #6b7a90">暂无日志</td>
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

