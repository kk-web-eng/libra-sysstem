<template>
  <div class="login-page">
    <section class="login-hero">
      <router-link class="user-entry" to="/user/login">
        <el-icon><User /></el-icon>
        <span>用户登录</span>
      </router-link>
      <div class="hero-copy">
        <p>LIBRARY OPERATIONS</p>
        <h1>云图书馆</h1>
        <span>馆藏、读者与借阅记录集中管理，让日常流转更清楚。</span>
      </div>
    </section>

    <section class="login-panel">
      <div class="panel-head">
        <h2>管理员登录</h2>
        <p>请输入账号密码进入后台</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password @keyup.enter="doLogin" />
        </el-form-item>
        <el-button class="login-button" type="primary" :loading="loading" @click="doLogin">登录后台</el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function doLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form.username, form.password)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) 460px;
  background: #eef2f7;
}
.login-hero {
  position: relative;
  display: flex;
  align-items: flex-end;
  padding: 56px;
  color: #fff;
  background:
    linear-gradient(90deg, rgba(15, 23, 42, 0.9), rgba(15, 23, 42, 0.35)),
    url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=1600&q=80') center/cover;
}
.user-entry {
  position: absolute;
  top: 28px;
  right: 28px;
  display: flex;
  align-items: center;
  gap: 8px;
  height: 38px;
  padding: 0 14px;
  border: 1px solid rgba(255,255,255,.36);
  border-radius: 8px;
  background: rgba(15, 23, 42, 0.34);
}
.hero-copy { max-width: 680px; }
.hero-copy p { margin-bottom: 12px; font-size: 13px; color: #bfdbfe; }
.hero-copy h1 { margin-bottom: 14px; font-size: 58px; line-height: 1; letter-spacing: 0; }
.hero-copy span { display: block; max-width: 560px; color: #e5e7eb; font-size: 18px; line-height: 1.7; }
.login-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 56px 54px;
  background: #fff;
}
.panel-head { margin-bottom: 28px; }
.panel-head h2 { margin-bottom: 8px; color: #111827; font-size: 28px; }
.panel-head p { color: #6b7280; }
.login-button { width: 100%; height: 44px; margin-top: 6px; }
@media (max-width: 900px) {
  .login-page { grid-template-columns: 1fr; }
  .login-hero { min-height: 280px; padding: 34px 24px; }
  .hero-copy h1 { font-size: 42px; }
  .login-panel { padding: 36px 24px; }
}
</style>
