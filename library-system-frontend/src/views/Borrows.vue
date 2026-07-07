<template>
  <div class="page-shell">
    <section class="page-panel borrow-panel">
      <div class="toolbar">
        <div>
          <div class="section-title">借书登记</div>
          <p>填写读者和图书信息后，直接生成借阅记录</p>
        </div>
      </div>

      <el-form ref="formRef" :model="borrowForm" :rules="rules" label-position="top" class="borrow-form">
        <el-row :gutter="16">
          <el-col :xs="24" :md="6">
            <el-form-item label="读者姓名" prop="readerName">
              <el-input v-model="borrowForm.readerName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label="电话" prop="readerPhone">
              <el-input v-model="borrowForm.readerPhone" maxlength="10" placeholder="10 位数字" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label="书籍编号" prop="bookIsbn">
              <el-select
                v-model="borrowForm.bookIsbn"
                filterable
                remote
                clearable
                :remote-method="searchBooks"
                :loading="bookSearchLoading"
                placeholder="搜索或输入编号"
                class="full-control"
                @change="chooseBook"
              >
                <el-option v-for="b in bookOptions" :key="b.id" :label="`${b.isbn} - ${b.bookName}`" :value="b.isbn" :disabled="b.availableCount <= 0">
                  <span>{{ b.isbn }}</span>
                  <span class="option-meta">{{ b.bookName }} | 可借 {{ b.availableCount }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label="书籍名称" prop="bookName">
              <el-input v-model="borrowForm.bookName" placeholder="选择编号后自动填写" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label="借出时间" prop="borrowDate">
              <el-date-picker v-model="borrowForm.borrowDate" value-format="YYYY-MM-DD" type="date" class="full-control" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label="应还时间" prop="dueDate">
              <el-date-picker v-model="borrowForm.dueDate" value-format="YYYY-MM-DD" type="date" class="full-control" :disabled-date="disabledBorrowDueDate" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label=" ">
              <el-button type="primary" :icon="Check" :loading="borrowLoading" class="full-control" @click="doBorrow">确认借书</el-button>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="6">
            <el-form-item label=" ">
              <el-button :icon="RefreshLeft" class="full-control" @click="resetForm">清空</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </section>

    <section class="page-panel table-panel">
      <div class="toolbar table-toolbar">
        <div>
          <div class="section-title">借阅记录</div>
          <p>记录读者姓名、电话、图书编号、书名和借还时间</p>
        </div>
        <div class="toolbar-actions">
          <el-input v-model="keyword" class="search-input" placeholder="搜索姓名/电话/编号/书名" clearable :prefix-icon="Search" @keyup.enter="fetch" @clear="fetch" />
          <el-radio-group v-model="statusFilter" @change="fetch">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">借阅中</el-radio-button>
            <el-radio-button label="1">已归还</el-radio-button>
          </el-radio-group>
          <el-button :icon="Refresh" @click="fetch">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="readerName" label="读者姓名" width="120" />
        <el-table-column prop="readerPhone" label="电话" width="120" />
        <el-table-column prop="bookIsbn" label="书籍编号" width="150" show-overflow-tooltip />
        <el-table-column prop="bookName" label="书籍名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="borrowDate" label="借出时间" width="120" />
        <el-table-column prop="dueDate" label="应还时间" width="120" />
        <el-table-column prop="returnDate" label="归还时间" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" link @click="openRenew(row)">续借</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" link :icon="Select" @click="doReturn(row.id)">还书</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager-wrap">
        <el-pagination
          v-model:current-page="current"
          v-model:page-size="size"
          :page-sizes="[5, 10, 20]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetch"
          @current-change="fetch"
        />
      </div>
    </section>

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
        <el-button type="primary" :loading="borrowLoading" @click="doRenew">确认续借</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Refresh, RefreshLeft, Search, Select } from '@element-plus/icons-vue'
import { borrowBook, getBooks, getBorrows, renewBorrow, returnBook } from '@/api'

const loading = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('0')
const keyword = ref('')
const formRef = ref()
const borrowLoading = ref(false)
const bookOptions = ref([])
const bookSearchLoading = ref(false)
const renewDialog = ref(false)
const selectedRecord = ref(null)
const renewDueDate = ref('')

const formatDate = date => date.toISOString().slice(0, 10)
function daysFromNow(days) {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return formatDate(date)
}
function addMonths(date, months) {
  const next = new Date(date)
  next.setMonth(next.getMonth() + months)
  return next
}
function normalize(date) {
  const next = new Date(date)
  next.setHours(0, 0, 0, 0)
  return next
}
const defaultForm = () => ({
  readerName: '',
  readerPhone: '',
  bookIsbn: '',
  bookName: '',
  borrowDate: formatDate(new Date()),
  dueDate: daysFromNow(30)
})
const borrowForm = reactive(defaultForm())
const rules = {
  readerName: [{ required: true, message: '请输入读者姓名', trigger: 'blur' }],
  readerPhone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^\d{10}$/, message: '电话必须是 10 位数字', trigger: 'blur' }
  ],
  bookIsbn: [{ required: true, message: '请输入书籍编号', trigger: 'change' }],
  bookName: [{ required: true, message: '请输入书籍名称', trigger: 'blur' }],
  borrowDate: [{ required: true, message: '请选择借出时间', trigger: 'change' }],
  dueDate: [{ required: true, message: '请选择应还时间', trigger: 'change' }]
}

function statusText(status) {
  return status === 0 ? '借阅中' : status === 1 ? '已归还' : '逾期'
}

function statusType(status) {
  return status === 0 ? 'warning' : status === 1 ? 'success' : 'danger'
}

async function fetch() {
  loading.value = true
  try {
    const params = { current: current.value, size: size.value, keyword: keyword.value }
    if (statusFilter.value !== '') params.status = Number(statusFilter.value)
    const res = await getBorrows(params)
    tableData.value = (res.data.records || []).map(item => ({
      ...item,
      readerName: item.readerName || `读者ID:${item.readerId}`,
      readerPhone: item.readerPhone || '-',
      bookIsbn: item.bookIsbn || '-',
      bookName: item.bookName || `图书ID:${item.bookId}`
    }))
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function searchBooks(query) {
  if (!query) { bookOptions.value = []; return }
  bookSearchLoading.value = true
  try {
    const res = await getBooks({ current: 1, size: 20, keyword: query })
    bookOptions.value = res.data.records || []
  } finally {
    bookSearchLoading.value = false
  }
}

function chooseBook(isbn) {
  const book = bookOptions.value.find(item => item.isbn === isbn)
  if (book) borrowForm.bookName = book.bookName
}

function resetForm() {
  Object.assign(borrowForm, defaultForm())
  bookOptions.value = []
  formRef.value?.clearValidate()
}

async function doBorrow() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (borrowForm.dueDate < borrowForm.borrowDate) {
    ElMessage.warning('应还时间不能早于借出时间')
    return
  }
  borrowLoading.value = true
  try {
    await borrowBook({ ...borrowForm })
    ElMessage.success('借书成功')
    resetForm()
    statusFilter.value = '0'
    fetch()
  } finally {
    borrowLoading.value = false
  }
}

async function doReturn(id) {
  await returnBook(id)
  ElMessage.success('还书成功')
  fetch()
}

function openRenew(row) {
  selectedRecord.value = row
  renewDueDate.value = row.dueDate
  renewDialog.value = true
}

async function doRenew() {
  if (!renewDueDate.value) {
    ElMessage.warning('请选择还书日期')
    return
  }
  borrowLoading.value = true
  try {
    await renewBorrow(selectedRecord.value.id, { dueDate: renewDueDate.value })
    ElMessage.success('续借成功')
    renewDialog.value = false
    fetch()
  } finally {
    borrowLoading.value = false
  }
}

function disabledBorrowDueDate(date) {
  const current = normalize(date)
  const borrowDate = normalize(new Date(borrowForm.borrowDate || new Date()))
  return current < borrowDate || current > normalize(addMonths(borrowDate, 6))
}

function disabledRenewDate(date) {
  const current = normalize(date)
  const today = normalize(new Date())
  const borrowDate = selectedRecord.value?.borrowDate ? normalize(new Date(selectedRecord.value.borrowDate)) : today
  return current < today || current > normalize(addMonths(borrowDate, 6))
}

onMounted(fetch)
</script>

<style scoped>
.borrow-panel, .table-panel { padding: 22px; }
.borrow-panel p, .table-toolbar p { margin-top: 6px; color: #6b7280; }
.borrow-form { margin-top: 18px; }
.full-control { width: 100%; }
.option-meta { margin-left: 8px; color: #909399; }
.table-toolbar { margin-bottom: 18px; }
.toolbar-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.search-input { width: 260px; }
.pager-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
</style>
