<template>
  <main class="user-login">
    <!-- 左侧说明区域，同时提供返回管理员登录的入口。 -->
    <section class="hero">
      <router-link class="admin-link" to="/login">管理员登录</router-link>
      <div>
        <p>LIBRARY ACCOUNT</p>
        <h1>用户登录</h1>
        <span>使用 6 位及以上账号登录后，可以查询馆藏、借书和续借。</span>
      </div>
    </section>
    <!-- 登录和注册共用同一个账号密码表单。 -->
    <section class="panel">
      <el-tabs v-model="mode" stretch>
        <el-tab-pane label="登录" name="login" />
        <el-tab-pane label="注册" name="register" />
      </el-tabs>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="account"><el-input v-model="form.account" placeholder="6 位及以上账号" :prefix-icon="User" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password /></el-form-item>
        <el-button class="submit" type="primary" :loading="loading" @click="submit">{{ mode === 'login' ? '登录' : '注册并登录' }}</el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { userLogin, userRegister } from '@/api'

// 登录成功后使用路由进入读者首页。
const router = useRouter()

// mode 的值为 login 或 register，对应上方两个标签页。
const mode = ref('login')
const formRef = ref()
const loading = ref(false)
// 注册只需要账号和密码，不要求填写姓名和电话。
const form = reactive({ account: '', password: '' })

// 账号至少 6 位；可以包含数字、字母或其他符号。密码只要求非空。
const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 6, message: '账号至少 6 位', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/**
 * 登录/注册共用的提交方法。
 * 注册模式会先创建账号，再使用相同账号密码登录。
 * 登录成功后把 readerId 写入 sessionStorage，路由守卫用它判断读者身份。
 */
async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    if (mode.value === 'register') await userRegister({ ...form })
    const res = await userLogin(form.account, form.password)
    // readerId 用于登录判断；readerName 用于页面右上角显示。
    sessionStorage.setItem('readerId', res.data.id)
    sessionStorage.setItem('readerName', res.data.name)
    ElMessage.success(mode.value === 'login' ? '登录成功' : '注册成功')
    router.push('/user')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 读者登录页的双栏布局与响应式样式。 */
.user-login { min-height: 100vh; display: grid; grid-template-columns: minmax(0, 1fr) 420px; background: #eef2f7; }
.hero { position: relative; display: flex; align-items: flex-end; padding: 56px; color: #fff; background: linear-gradient(90deg, rgba(15,23,42,.9), rgba(15,23,42,.38)), url('https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=1600&q=80') center/cover; }
.admin-link { position: absolute; top: 28px; right: 28px; height: 38px; padding: 8px 14px; border: 1px solid rgba(255,255,255,.36); border-radius: 8px; }
.hero p { margin-bottom: 12px; color: #bfdbfe; font-size: 13px; }
.hero h1 { margin-bottom: 14px; font-size: 54px; }
.hero span { color: #e5e7eb; font-size: 18px; }
.panel { display: flex; flex-direction: column; justify-content: center; padding: 48px; background: #fff; }
.submit { width: 100%; height: 44px; }
@media (max-width: 860px) { .user-login { grid-template-columns: 1fr; } .hero { min-height: 260px; padding: 32px 24px; } .panel { padding: 32px 24px; } }
</style>
