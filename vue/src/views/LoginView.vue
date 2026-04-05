<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { loginApi, registerApi } from '@/api/library'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const isRegister = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: '',
  phone: '',
  email: ''
})

async function submit() {
  errorMsg.value = ''
  loading.value = true
  try {
    if (isRegister.value) {
      await registerApi({
        username: form.username,
        password: form.password,
        phone: form.phone,
        email: form.email,
        role: 0,
        status: 1
      })
      isRegister.value = false
      errorMsg.value = '注册成功，请登录'
      return
    }

    const result = await loginApi({
      username: form.username,
      password: form.password
    })
    auth.setAuth(result.data)
    router.push({ name: 'dashboard' })
  } catch (error) {
    errorMsg.value = error.message || '操作失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrap">
    <div class="login-card">
      <h2>{{ isRegister ? '用户注册' : '系统登录' }}</h2>
      <p style="color: var(--color-text-subtle); margin-bottom: 12px">
        {{ isRegister ? '默认注册为普通用户' : '管理员与用户共用登录入口' }}
      </p>

      <div class="form-grid">
        <input v-model="form.username" placeholder="用户名" />
        <input v-model="form.password" placeholder="密码" type="password" />
        <template v-if="isRegister">
          <input v-model="form.phone" placeholder="手机号" />
          <input v-model="form.email" placeholder="邮箱" />
        </template>
      </div>

      <div style="margin-top: 12px; display: flex; gap: 10px">
        <button class="btn btn-primary" :disabled="loading" @click="submit">
          {{ loading ? '处理中...' : isRegister ? '注册' : '登录' }}
        </button>
        <button class="btn" @click="isRegister = !isRegister">
          {{ isRegister ? '返回登录' : '去注册' }}
        </button>
      </div>

      <div v-if="errorMsg" style="margin-top: 10px; color: #c62828">{{ errorMsg }}</div>
      <div style="margin-top: 14px; color: #60728a; font-size: 13px">
        提示: 默认管理员账号通常为 admin/admin
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #f4f7fb 60%);
}

.login-card {
  width: 480px;
  max-width: calc(100vw - 24px);
  background: #fff;
  border-radius: 14px;
  border: 1px solid var(--color-border);
  padding: 24px;
}

h2 {
  margin-bottom: 6px;
  color: #114b8a;
}
</style>

