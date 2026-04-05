<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const menus = computed(() => {
  const baseMenus = [
    { name: 'dashboard', label: '首页概览' },
    { name: 'book-center', label: '图书中心' },
    { name: 'my-borrow', label: '我的借阅' }
  ]

  if (auth.isAdmin) {
    baseMenus.push(
      { name: 'admin-users', label: '用户管理' },
      { name: 'admin-books', label: '图书管理' },
      { name: 'admin-borrow', label: '借阅管理' },
      { name: 'admin-logs', label: '操作日志' }
    )
  }
  return baseMenus
})

function goLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="layout">
    <aside class="aside">
      <div class="aside-title">图书馆系统</div>
      <RouterLink
        v-for="menu in menus"
        :key="menu.name"
        class="menu-item"
        :class="{ active: route.name === menu.name }"
        :to="{ name: menu.name }"
      >
        {{ menu.label }}
      </RouterLink>
    </aside>

    <div class="main-area">
      <header class="header">
        <div class="header-title">图书管理平台</div>
        <div class="header-right">
          <span class="tag">{{ auth.isAdmin ? '管理员' : '普通用户' }}</span>
          <span>{{ auth.username }}</span>
          <button class="btn" @click="goLogout">退出登录</button>
        </div>
      </header>

      <main class="page">
        <RouterView />
      </main>
    </div>
  </div>
</template>

