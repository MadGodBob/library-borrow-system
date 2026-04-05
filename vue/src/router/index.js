import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'dashboard',
        component: () => import('@/views/DashboardView.vue')
      },
      {
        path: 'book-center',
        name: 'book-center',
        component: () => import('@/views/book/BookCenterView.vue')
      },
      {
        path: 'my-borrow',
        name: 'my-borrow',
        component: () => import('@/views/borrow/MyBorrowView.vue')
      },
      {
        path: 'admin/users',
        name: 'admin-users',
        component: () => import('@/views/admin/UserManageView.vue'),
        meta: { adminOnly: true }
      },
      {
        path: 'admin/books',
        name: 'admin-books',
        component: () => import('@/views/admin/BookManageView.vue'),
        meta: { adminOnly: true }
      },
      {
        path: 'admin/borrow',
        name: 'admin-borrow',
        component: () => import('@/views/admin/BorrowManageView.vue'),
        meta: { adminOnly: true }
      },
      {
        path: 'admin/logs',
        name: 'admin-logs',
        component: () => import('@/views/admin/OperationLogView.vue'),
        meta: { adminOnly: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.public && auth.token) {
    return { name: 'dashboard' }
  }

  if (to.meta.public) {
    return true
  }

  if (!auth.token) {
    return { name: 'login' }
  }

  if (to.meta.adminOnly && !auth.isAdmin) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
