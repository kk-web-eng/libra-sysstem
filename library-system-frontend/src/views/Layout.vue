<template>
  <el-container class="layout-root">
    <el-aside class="layout-aside" width="248px">
      <div class="brand">
        <div class="brand-mark"><Reading /></div>
        <div>
          <strong>云图书馆</strong>
          <span>Library Admin</span>
        </div>
      </div>

      <el-menu :default-active="activeMenu" class="side-menu" router>
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon><span>数据总览</span>
        </el-menu-item>
        <el-menu-item index="/books">
          <el-icon><Collection /></el-icon><span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/readers">
          <el-icon><UserFilled /></el-icon><span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/borrows">
          <el-icon><Notebook /></el-icon><span>借阅管理</span>
        </el-menu-item>
        <el-menu-item index="/admins">
          <el-icon><Avatar /></el-icon><span>管理员管理</span>
        </el-menu-item>
      </el-menu>

      <router-link class="client-link" to="/client">
        <el-icon><Monitor /></el-icon>
        <span>打开馆藏查询</span>
      </router-link>
      <router-link class="client-link user-link" to="/user/login">
        <el-icon><User /></el-icon>
        <span>用户登录</span>
      </router-link>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div>
          <div class="eyebrow">Library Management System</div>
          <h1>{{ $route.meta.title }}</h1>
        </div>
        <div class="header-actions">
          <el-tag effect="plain" round>{{ userStore.realName || userStore.username || '管理员' }}</el-tag>
          <el-button :icon="SwitchButton" @click="doLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { logout } from '@/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)

async function doLogout() {
  await logout().catch(() => {})
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-root { min-height: 100vh; background: #f5f7fb; }
.layout-aside {
  display: flex;
  flex-direction: column;
  padding: 18px 14px;
  background: #172033;
  color: #fff;
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 60px;
  padding: 0 8px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.brand-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 8px;
  background: #3b82f6;
}
.brand-mark .el-icon { width: 23px; height: 23px; }
.brand strong { display: block; font-size: 18px; letter-spacing: 0; }
.brand span { display: block; margin-top: 3px; color: #9ca3af; font-size: 12px; }
.side-menu {
  flex: 1;
  margin-top: 18px;
  border-right: 0;
  background: transparent;
}
.side-menu :deep(.el-menu-item) {
  height: 46px;
  margin: 6px 0;
  border-radius: 8px;
  color: #cbd5e1;
}
.side-menu :deep(.el-menu-item:hover),
.side-menu :deep(.el-menu-item.is-active) {
  background: rgba(59, 130, 246, 0.16);
  color: #fff;
}
.client-link {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 42px;
  padding: 0 14px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  color: #dbeafe;
  background: rgba(255, 255, 255, 0.05);
}
.user-link { margin-top: 8px; }
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 76px;
  padding: 0 28px;
  border-bottom: 1px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(12px);
}
.eyebrow { margin-bottom: 4px; color: #6b7280; font-size: 12px; }
h1 { font-size: 22px; font-weight: 800; color: #111827; }
.header-actions { display: flex; align-items: center; gap: 10px; }
.layout-main { padding: 24px; }
@media (max-width: 860px) {
  .layout-aside { width: 72px !important; padding: 14px 8px; }
  .brand div:last-child, .side-menu span, .client-link span { display: none; }
  .brand { justify-content: center; padding-left: 0; padding-right: 0; }
  .layout-header { padding: 0 16px; }
  .layout-main { padding: 16px; }
}
</style>
