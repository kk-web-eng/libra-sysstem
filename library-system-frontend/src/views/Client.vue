<template>
  <main class="client-page">
    <!-- 顶部区域：品牌、返回后台入口、关键词和分类筛选。 -->
    <header class="client-hero">
      <nav class="client-nav">
        <div class="brand">
          <el-icon><Reading /></el-icon>
          <span>云图书馆</span>
        </div>
        <router-link class="back-link" to="/dashboard">
          <el-icon><Back /></el-icon>
          <span>返回数据总览</span>
        </router-link>
      </nav>
      <section class="hero-content">
        <p>PUBLIC CATALOG</p>
        <h1>馆藏查询</h1>
        <span>检索馆藏图书，查看可借数量、出版社、分类与书架位置。</span>
        <!-- 回车、搜索按钮和清空输入框都会调用 search。 -->
        <div class="search-bar">
          <el-input v-model="keyword" size="large" placeholder="输入书名或 ISBN" clearable :prefix-icon="Search" @keyup.enter="search" @clear="search" />
          <el-button size="large" type="primary" :icon="Search" @click="search">搜索</el-button>
        </div>
        <!-- 分类按钮来自数据库，空字符串表示查询全部分类。 -->
        <div class="category-filter">
          <button :class="{ active: !category }" @click="chooseCategory('')">全部</button>
          <button v-for="item in categories" :key="item" :class="{ active: category === item }" @click="chooseCategory(item)">{{ item }}</button>
        </div>
      </section>
    </header>

    <!-- 馆藏目录：公开页面，不登录也可以查询。 -->
    <section class="catalog-section">
      <div class="catalog-head">
        <div>
          <h2>馆藏目录</h2>
          <p>共找到 {{ total }} 本相关图书</p>
        </div>
        <el-button :icon="Refresh" @click="fetch" :loading="loading">刷新</el-button>
      </div>

      <!-- 请求结束且没有结果时显示空状态。 -->
      <el-empty v-if="!loading && books.length === 0" description="暂无匹配图书" />
      <div v-else class="book-grid" v-loading="loading">
        <!-- v-for 根据后端返回的每本图书生成一张馆藏卡片。 -->
        <article v-for="book in books" :key="book.id" class="book-card">
          <div class="book-cover">
            <span>{{ coverName(book.bookName) }}</span>
          </div>
          <div class="book-info">
            <div class="book-topline">
              <el-tag size="small" effect="light">{{ book.category || '未分类' }}</el-tag>
              <el-tag size="small" :type="book.availableCount > 0 ? 'success' : 'info'">{{ book.availableCount > 0 ? '可借' : '暂无库存' }}</el-tag>
            </div>
            <h3>{{ book.bookName }}</h3>
            <p>{{ book.author || '佚名' }} · {{ book.publisher || '未知出版社' }}</p>
            <div class="book-meta">
              <span><el-icon><Tickets /></el-icon>{{ book.isbn || '无 ISBN' }}</span>
              <span><el-icon><Location /></el-icon>{{ book.location || '暂无位置' }}</span>
            </div>
            <div class="stock-row">
              <span>可借 {{ book.availableCount || 0 }} / 馆藏 {{ book.totalCount || 0 }}</span>
              <el-progress :percentage="stockPercent(book)" :show-text="false" :stroke-width="8" />
            </div>
          </div>
        </article>
      </div>

      <!-- 分页器修改 current 或 size 后重新查询。 -->
      <div class="pager-wrap">
        <el-pagination
          v-model:current-page="current"
          v-model:page-size="size"
          :page-sizes="[8, 12, 24]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetch"
          @current-change="fetch"
        />
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Back, Location, Refresh, Search, Tickets } from '@element-plus/icons-vue'
import { getPublicBooks, getPublicCategories } from '@/api'

// -------------------- 查询和分页状态 --------------------
const loading = ref(false)
const books = ref([])
const keyword = ref('')
const category = ref('')
const categories = ref([])
const current = ref(1)
const size = ref(12)
const total = ref(0)

/** 取书名前两个字符，生成没有真实封面时的文字封面。 */
function coverName(name = '') {
  return name.trim().slice(0, 2) || '书'
}

/** 计算当前可借数量占馆藏总数的百分比。 */
function stockPercent(book) {
  if (!book.totalCount) return 0
  return Math.round(((book.availableCount || 0) / book.totalCount) * 100)
}

/** 新搜索从第一页开始，避免保留旧查询的页码。 */
function search() {
  current.value = 1
  fetch()
}

/** 选择分类后立即重新查询。 */
function chooseCategory(value) {
  category.value = value
  search()
}

/**
 * 请求公共馆藏接口。
 * 关键词：分页、关键词、分类、加载状态。
 * 该接口是公开接口，因此访客不登录也能查询。
 */
async function fetch() {
  loading.value = true
  try {
    const res = await getPublicBooks({ current: current.value, size: size.value, keyword: keyword.value, category: category.value })
    books.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

/** 从后端读取当前存在的图书分类，用于生成筛选按钮。 */
async function fetchCategories() {
  const res = await getPublicCategories()
  categories.value = res.data || []
}

// 页面首次显示时加载分类和第一页馆藏。
onMounted(() => {
  fetchCategories()
  fetch()
})
</script>

<style scoped>
/* 公共馆藏页的背景、图书卡片和响应式布局。 */
.client-page {
  min-height: 100vh;
  background: #f5f7fb;
}
.client-hero {
  min-height: 420px;
  color: #fff;
  background:
    linear-gradient(90deg, rgba(15, 23, 42, 0.92), rgba(30, 64, 175, 0.45)),
    url('https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=1800&q=80') center/cover;
}
.client-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1180px;
  margin: 0 auto;
  padding: 24px 24px 0;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
}
.brand { font-size: 20px; font-weight: 800; }
.back-link {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 38px;
  padding: 0 14px;
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: 8px;
  background: rgba(15, 23, 42, 0.28);
}
.hero-content {
  max-width: 1180px;
  margin: 0 auto;
  padding: 88px 24px 54px;
}
.hero-content p { margin-bottom: 12px; color: #bfdbfe; font-size: 13px; }
.hero-content h1 { margin-bottom: 14px; font-size: 54px; line-height: 1; letter-spacing: 0; }
.hero-content > span { display: block; max-width: 620px; color: #e5e7eb; font-size: 18px; line-height: 1.7; }
.search-bar {
  display: grid;
  grid-template-columns: minmax(0, 520px) 116px;
  gap: 12px;
  max-width: 660px;
  margin-top: 28px;
}
.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  max-width: 760px;
  margin-top: 18px;
}
.category-filter button {
  height: 34px;
  padding: 0 14px;
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: 8px;
  color: #e5e7eb;
  background: rgba(15, 23, 42, 0.24);
  cursor: pointer;
}
.category-filter button.active {
  color: #0f172a;
  background: #fff;
}
.catalog-section {
  max-width: 1180px;
  margin: -34px auto 0;
  padding: 0 24px 44px;
}
.catalog-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
  padding: 22px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.08);
}
.catalog-head h2 { margin-bottom: 6px; color: #111827; font-size: 22px; }
.catalog-head p { color: #6b7280; }
.book-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  min-height: 220px;
}
.book-card {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr);
  gap: 16px;
  min-height: 190px;
  padding: 18px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 10px 24px rgba(31, 41, 55, 0.05);
}
.book-cover {
  display: grid;
  place-items: center;
  height: 128px;
  border-radius: 7px;
  color: #fff;
  background: linear-gradient(160deg, #1d4ed8, #0f766e);
  font-size: 24px;
  font-weight: 800;
}
.book-info { min-width: 0; }
.book-topline { display: flex; justify-content: space-between; gap: 8px; margin-bottom: 12px; }
.book-info h3 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8px;
  color: #111827;
  font-size: 18px;
}
.book-info p { color: #6b7280; line-height: 1.5; }
.book-meta {
  display: flex;
  flex-direction: column;
  gap: 7px;
  margin-top: 12px;
  color: #4b5563;
  font-size: 13px;
}
.book-meta span { display: flex; align-items: center; gap: 6px; }
.stock-row { margin-top: 14px; }
.stock-row span { display: block; margin-bottom: 7px; color: #374151; font-size: 13px; }
.pager-wrap { display: flex; justify-content: flex-end; margin-top: 22px; }
@media (max-width: 1020px) { .book-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 680px) {
  .client-hero { min-height: 440px; }
  .hero-content { padding-top: 68px; }
  .hero-content h1 { font-size: 42px; }
  .search-bar { grid-template-columns: 1fr; }
  .catalog-head { align-items: flex-start; flex-direction: column; }
  .book-grid { grid-template-columns: 1fr; }
}
</style>
