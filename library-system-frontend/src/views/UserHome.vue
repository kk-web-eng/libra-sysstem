<template>
  <main class="user-page">
    <header class="user-head">
      <div>
        <p>云图书馆</p>
        <h1>馆藏借阅</h1>
      </div>
      <div class="head-actions">
        <el-tag>{{ readerName || '用户' }}</el-tag>
        <el-button @click="logout">退出</el-button>
      </div>
    </header>

    <section class="catalog">
      <div class="toolbar">
        <div>
          <div class="section-title">馆藏查询</div>
          <p>选择分类或搜索图书后可以直接借书</p>
        </div>
        <div class="filters">
          <el-select v-model="category" clearable placeholder="分类" @change="search">
            <el-option v-for="item in categories" :key="item" :label="item" :value="item" />
          </el-select>
          <el-input v-model="keyword" placeholder="书名或编号" clearable :prefix-icon="Search" @keyup.enter="search" @clear="search" />
        </div>
      </div>

      <div class="book-grid" v-loading="bookLoading">
        <article v-for="book in books" :key="book.id" class="book-card">
          <el-tag size="small">{{ book.category || '未分类' }}</el-tag>
          <h3>{{ book.bookName }}</h3>
          <p class="book-line">{{ book.isbn }} · {{ book.author || '佚名' }}</p>
          <p class="book-desc">{{ book.description || '暂无简介' }}</p>
          <span>可借 {{ book.availableCount || 0 }} / 馆藏 {{ book.totalCount || 0 }}</span>
          <el-button type="primary" :disabled="book.availableCount <= 0" @click="openBorrow(book)">借书</el-button>
        </article>
      </div>
    </section>

    <section class="records">
      <div class="toolbar">
        <div>
          <div class="section-title">我的借阅</div>
          <p>查看还书日期，并可自定义续借后的还书日期</p>
        </div>
        <el-button :icon="Refresh" @click="fetchBorrows">刷新</el-button>
      </div>
      <el-table :data="records" v-loading="recordLoading" stripe>
        <el-table-column prop="bookIsbn" label="书籍编号" width="150" />
        <el-table-column prop="bookName" label="书籍名称" min-width="180" />
        <el-table-column prop="borrowDate" label="借出日期" width="120" />
        <el-table-column prop="dueDate" label="还书日期" width="120" />
        <el-table-column prop="returnDate" label="实际归还" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button v-if="row.status === 0" link type="primary" @click="openRenew(row)">续借</el-button></template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="borrowDialog" title="选择还书日期" width="420px">
      <el-form label-position="top">
        <el-form-item label="图书">
          <el-input :model-value="selectedBook ? selectedBook.bookName : ''" disabled />
        </el-form-item>
        <el-form-item label="还书日期">
          <el-date-picker v-model="borrowDueDate" value-format="YYYY-MM-DD" type="date" class="full-control" :disabled-date="disabledBorrowDate" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialog = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="confirmBorrow">确认借书</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="renewDialog" title="选择续借后的还书日期" width="420px">
      <el-form label-position="top">
        <el-form-item label="图书">
          <el-input :model-value="selectedRecord ? selectedRecord.bookName : ''" disabled />
        </el-form-item>
        <el-form-item label="还书日期">
          <el-date-picker v-model="renewDueDate" value-format="YYYY-MM-DD" type="date" class="full-control" :disabled-date="disabledRenewDate" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewDialog = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="confirmRenew">确认续借</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getPublicBooks, getPublicCategories, getUserBorrows, renewUserBorrow, userBorrow, userLogout } from '@/api'

const router = useRouter()
const readerName = sessionStorage.getItem('readerName')
const keyword = ref('')
const category = ref('')
const categories = ref([])
const books = ref([])
const records = ref([])
const bookLoading = ref(false)
const recordLoading = ref(false)
const actionLoading = ref(false)
const borrowDialog = ref(false)
const renewDialog = ref(false)
const selectedBook = ref(null)
const selectedRecord = ref(null)
const borrowDueDate = ref('')
const renewDueDate = ref('')

const toDateString = date => date.toISOString().slice(0, 10)
function addMonths(date, months) {
  const next = new Date(date)
  next.setMonth(next.getMonth() + months)
  return next
}
function addDays(date, days) {
  const next = new Date(date)
  next.setDate(next.getDate() + days)
  return next
}
function normalize(date) {
  const next = new Date(date)
  next.setHours(0, 0, 0, 0)
  return next
}

async function fetchBooks() {
  bookLoading.value = true
  try {
    books.value = (await getPublicBooks({ current: 1, size: 12, keyword: keyword.value, category: category.value })).data.records || []
  } finally { bookLoading.value = false }
}
function search() { fetchBooks() }
async function fetchCategories() { categories.value = (await getPublicCategories()).data || [] }
async function fetchBorrows() {
  recordLoading.value = true
  try { records.value = (await getUserBorrows({ current: 1, size: 20 })).data.records || [] }
  finally { recordLoading.value = false }
}

function openBorrow(book) {
  selectedBook.value = book
  borrowDueDate.value = toDateString(addDays(new Date(), 30))
  borrowDialog.value = true
}
async function confirmBorrow() {
  if (!borrowDueDate.value) { ElMessage.warning('请选择还书日期'); return }
  actionLoading.value = true
  try {
    await userBorrow(selectedBook.value.id, { dueDate: borrowDueDate.value })
    ElMessage.success('借书成功')
    borrowDialog.value = false
    fetchBooks()
    fetchBorrows()
  } finally { actionLoading.value = false }
}
function openRenew(row) {
  selectedRecord.value = row
  renewDueDate.value = row.dueDate
  renewDialog.value = true
}
async function confirmRenew() {
  if (!renewDueDate.value) { ElMessage.warning('请选择还书日期'); return }
  actionLoading.value = true
  try {
    await renewUserBorrow(selectedRecord.value.id, { dueDate: renewDueDate.value })
    ElMessage.success('续借成功')
    renewDialog.value = false
    fetchBorrows()
  } finally { actionLoading.value = false }
}
function disabledBorrowDate(date) {
  const current = normalize(date)
  const today = normalize(new Date())
  return current < today || current > normalize(addMonths(today, 6))
}
function disabledRenewDate(date) {
  const current = normalize(date)
  const today = normalize(new Date())
  const borrowDate = selectedRecord.value?.borrowDate ? normalize(new Date(selectedRecord.value.borrowDate)) : today
  return current < today || current > normalize(addMonths(borrowDate, 6))
}
function statusText(status) { return status === 0 ? '借阅中' : status === 1 ? '已归还' : '逾期' }
function statusType(status) { return status === 0 ? 'warning' : status === 1 ? 'success' : 'danger' }
async function logout() { await userLogout().catch(() => {}); sessionStorage.removeItem('readerId'); sessionStorage.removeItem('readerName'); router.push('/user/login') }

onMounted(() => { fetchCategories(); fetchBooks(); fetchBorrows() })
</script>

<style scoped>
.user-page { min-height: 100vh; padding: 24px; background: #f5f7fb; }
.user-head, .catalog, .records { max-width: 1180px; margin: 0 auto 18px; }
.user-head { display: flex; align-items: center; justify-content: space-between; padding: 26px; border-radius: 8px; color: #fff; background: #172033; }
.user-head p { color: #bfdbfe; margin-bottom: 6px; }
.user-head h1 { font-size: 30px; }
.head-actions { display: flex; gap: 10px; align-items: center; }
.catalog, .records { padding: 22px; border: 1px solid #e5e7eb; border-radius: 8px; background: #fff; }
.toolbar { display: flex; justify-content: space-between; gap: 12px; flex-wrap: wrap; margin-bottom: 18px; }
.filters { display: flex; gap: 10px; min-width: 420px; }
.section-title { font-size: 18px; font-weight: 700; color: #111827; }
.toolbar p { margin-top: 6px; color: #6b7280; }
.book-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; min-height: 160px; }
.book-card { display: flex; flex-direction: column; gap: 10px; min-height: 260px; padding: 16px; border: 1px solid #e5e7eb; border-radius: 8px; }
.book-card h3 { color: #111827; font-size: 18px; }
.book-line, .book-card span { color: #6b7280; line-height: 1.5; }
.book-desc { flex: 1; color: #4b5563; line-height: 1.6; font-size: 14px; }
.book-card .el-button { margin-top: auto; }
.full-control { width: 100%; }
@media (max-width: 920px) { .book-grid { grid-template-columns: repeat(2, 1fr); } .filters { min-width: 0; width: 100%; } }
@media (max-width: 560px) { .book-grid { grid-template-columns: 1fr; } .user-head { align-items: flex-start; flex-direction: column; gap: 14px; } }
</style>
