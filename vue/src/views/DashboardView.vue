<script setup>
import { computed, onMounted, reactive } from 'vue'
import { getBookPageApi, getBorrowPageApi, getUserPageApi } from '@/api/library'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const stats = reactive({
  books: 0,
  activeBorrow: 0,
  users: 0
})

const cards = computed(() => {
  const common = [
    { title: '图书总数', value: stats.books },
    { title: '借阅中记录', value: stats.activeBorrow }
  ]
  if (auth.isAdmin) {
    common.push({ title: '用户总数', value: stats.users })
  }
  return common
})

onMounted(async () => {
  try {
    const [bookRes, borrowRes] = await Promise.all([
      getBookPageApi({ pageNum: 1, pageSize: 1 }),
      getBorrowPageApi({ pageNum: 1, pageSize: 1, status: 1 })
    ])

    stats.books = bookRes.data?.total || 0
    stats.activeBorrow = borrowRes.data?.total || 0

    if (auth.isAdmin) {
      const userRes = await getUserPageApi({ pageNum: 1, pageSize: 1 })
      stats.users = userRes.data?.total || 0
    }
  } catch (_) {
    // Dashboard can still render even if a stat request fails.
  }
})
</script>

<template>
  <div>
    <div class="panel">
      <div class="panel-title">欢迎使用图书管理平台</div>
      <p>当前用户: {{ auth.username }}，角色: {{ auth.isAdmin ? '管理员' : '普通用户' }}</p>
    </div>

    <div class="card-grid">
      <div class="card" v-for="card in cards" :key="card.title">
        <div style="color: var(--color-text-subtle)">{{ card.title }}</div>
        <div style="font-size: 28px; color: #11508f; font-weight: 600">{{ card.value }}</div>
      </div>
    </div>
  </div>
</template>

