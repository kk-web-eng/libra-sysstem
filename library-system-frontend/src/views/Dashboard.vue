<template>
  <div class="page-shell">
    <section class="hero-band">
      <div>
        <p>今日运营</p>
        <h2>馆藏流转概览</h2>
        <span>快速掌握图书、读者、借阅和逾期情况。</span>
      </div>
      <el-button type="primary" :icon="Refresh" @click="fetch" :loading="loading">刷新</el-button>
    </section>

    <el-row :gutter="18">
      <el-col v-for="card in cards" :key="card.label" :xs="24" :sm="12" :lg="6">
        <div class="metric page-panel">
          <div class="metric-icon" :style="{ background: card.bg, color: card.color }">
            <el-icon><component :is="card.icon" /></el-icon>
          </div>
          <div>
            <p>{{ card.label }}</p>
            <strong>{{ card.value }}</strong>
          </div>
        </div>
      </el-col>
    </el-row>

    <section class="page-panel quick-panel">
      <div class="toolbar">
        <div>
          <div class="section-title">常用入口</div>
          <p>选择一个模块继续处理日常业务</p>
        </div>
      </div>
      <div class="quick-grid">
        <router-link v-for="item in quickLinks" :key="item.path" class="quick-item" :to="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <strong>{{ item.title }}</strong>
          <span>{{ item.desc }}</span>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, markRaw, onMounted, ref } from 'vue'
import { Collection, DataAnalysis, Notebook, Refresh, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import { getDashboard } from '@/api'

const loading = ref(false)
const cards = ref([
  { label: '图书总数', value: 0, icon: markRaw(Collection), color: '#2563eb', bg: '#dbeafe' },
  { label: '读者总数', value: 0, icon: markRaw(UserFilled), color: '#059669', bg: '#d1fae5' },
  { label: '借阅中', value: 0, icon: markRaw(Notebook), color: '#d97706', bg: '#fef3c7' },
  { label: '逾期数量', value: 0, icon: markRaw(WarningFilled), color: '#dc2626', bg: '#fee2e2' }
])
const isSuperAdmin = computed(() => sessionStorage.getItem('role') === 'SUPER_ADMIN' || sessionStorage.getItem('username') === 'admin')
const allQuickLinks = [
  { path: '/books', title: '维护馆藏', desc: '新增、编辑和清点图书库存', icon: markRaw(Collection) },
  { path: '/readers', title: '用户管理', desc: '维护用户账号、密码和联系方式', icon: markRaw(UserFilled) },
  { path: '/borrows', title: '借阅登记', desc: '登记读者信息、图书和借还时间', icon: markRaw(Notebook) },
  { path: '/admins', title: '管理员管理', desc: '维护后台账号和启用状态', icon: markRaw(UserFilled), superAdmin: true },
  { path: '/client', title: '馆藏查询', desc: '查看后台馆藏检索页面', icon: markRaw(DataAnalysis) }
]
const quickLinks = computed(() => allQuickLinks.filter(item => !item.superAdmin || isSuperAdmin.value))

async function fetch() {
  loading.value = true
  try {
    const res = await getDashboard()
    cards.value[0].value = res.data.bookCount || 0
    cards.value[1].value = res.data.readerCount || 0
    cards.value[2].value = res.data.borrowingCount || 0
    cards.value[3].value = res.data.overdueCount || 0
  } finally {
    loading.value = false
  }
}

onMounted(fetch)
</script>

<style scoped>
.hero-band {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 158px;
  padding: 28px;
  border-radius: 8px;
  color: #fff;
  background:
    linear-gradient(90deg, rgba(23, 32, 51, 0.95), rgba(37, 99, 235, 0.62)),
    url('https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=1400&q=80') center/cover;
}
.hero-band p { margin-bottom: 8px; color: #bfdbfe; font-size: 13px; }
.hero-band h2 { margin-bottom: 8px; font-size: 30px; }
.hero-band span { color: #e5e7eb; }
.metric {
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 118px;
  padding: 22px;
}
.metric-icon {
  display: grid;
  place-items: center;
  width: 50px;
  height: 50px;
  border-radius: 8px;
}
.metric-icon .el-icon { width: 26px; height: 26px; }
.metric p { margin-bottom: 8px; color: #6b7280; }
.metric strong { color: #111827; font-size: 30px; }
.quick-panel { padding: 22px; }
.quick-panel p { margin-top: 6px; color: #6b7280; }
.quick-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 132px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}
.quick-item .el-icon { color: #2563eb; font-size: 24px; }
.quick-item span { color: #6b7280; line-height: 1.5; }
@media (max-width: 980px) { .quick-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 560px) { .quick-grid { grid-template-columns: 1fr; } .hero-band { align-items: flex-start; flex-direction: column; } }
</style>
