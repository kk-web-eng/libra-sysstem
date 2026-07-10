<template>
  <div class="page-shell">
    <!-- 借书登记：管理员可以在一个表单中填写读者、图书和日期。 -->
    <section class="page-panel borrow-panel">
      <div class="toolbar">
        <div>
          <div class="section-title">借书登记</div>
          <p>填写读者和图书信息后，直接生成借阅记录</p>
        </div>
      </div>

      <!-- rules 负责前端校验；后端仍会再次检查电话、库存和日期。 -->
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
              <!-- remote 表示输入关键词时调用 searchBooks 从后端搜索图书。 -->
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
                <!-- 没有可借库存的图书仍可显示，但不能被选中。 -->
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

    <!-- 借阅记录：支持关键词、状态筛选、分页、归还和续借。 -->
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

      <!-- 表格中的一行对应 borrow_record 表中的一条记录。 -->
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
        <!-- 只有“借阅中”状态可以续借或还书。 -->
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

    <!-- 续借弹窗：选择的新日期仍不能超过原借出日期后的 6 个月。 -->
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

// -------------------- 记录查询状态 --------------------
const loading = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('0')
const keyword = ref('')
// -------------------- 借书与续借状态 --------------------
const formRef = ref()
const borrowLoading = ref(false)
const bookOptions = ref([])
const bookSearchLoading = ref(false)
const renewDialog = ref(false)
const selectedRecord = ref(null)
const renewDueDate = ref('')

// 把 Date 转成 YYYY-MM-DD，后端使用 LocalDate 接收这个格式。
const formatDate = date => date.toISOString().slice(0, 10)

/** 计算从今天开始若干天后的日期。 */
function daysFromNow(days) {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return formatDate(date)
}
/** 创建指定月数后的新日期，用于计算 6 个月上限。 */
function addMonths(date, months) {
  const next = new Date(date)
  next.setMonth(next.getMonth() + months)
  return next
}
/** 清除时分秒，只比较日期，避免同一天因时间不同被误判。 */
function normalize(date) {
  const next = new Date(date)
  next.setHours(0, 0, 0, 0)
  return next
}
// 默认借出日期为今天，应还日期为 30 天后；管理员可以继续修改。
const defaultForm = () => ({
  readerName: '',
  readerPhone: '',
  bookIsbn: '',
  bookName: '',
  borrowDate: formatDate(new Date()),
  dueDate: daysFromNow(30)
})
// reactive 让表单字段变化后立即反映到页面。
const borrowForm = reactive(defaultForm())

// 前端表单校验：电话要求 10 位数字，其余业务规则由后端再次确认。
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

/** 数字状态转成中文。 */
function statusText(status) {
  return status === 0 ? '借阅中' : status === 1 ? '已归还' : '逾期'
}

/** 为不同状态选择标签颜色。 */
function statusType(status) {
  return status === 0 ? 'warning' : status === 1 ? 'success' : 'danger'
}

/**
 * 分页查询借阅记录。
 * statusFilter 为空时查询全部，否则转换成数字传给后端。
 * map 中的备用文字用于兼容早期没有保存姓名或书名的历史记录。
 */
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

/**
 * 远程搜索图书。
 * 用户输入编号或书名时才请求后端，空关键词直接清空候选项。
 */
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

/** 选择书籍编号后，自动把对应书名填入表单。 */
function chooseBook(isbn) {
  const book = bookOptions.value.find(item => item.isbn === isbn)
  if (book) borrowForm.bookName = book.bookName
}

/** 清空借书表单，并恢复今天和 30 天后的默认日期。 */
function resetForm() {
  Object.assign(borrowForm, defaultForm())
  bookOptions.value = []
  formRef.value?.clearValidate()
}

/**
 * 管理员登记借书：
 * 1. 校验输入；2. 检查应还日不早于借出日；3. 调用借书接口；
 * 4. 清空表单并刷新“借阅中”记录。
 * 后端会在事务中生成记录并把图书可借数量减 1。
 */
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

/**
 * 归还图书。
 * 后端会把状态改为已归还、写入实际归还日期，并把库存加 1。
 */
async function doReturn(id) {
  await returnBook(id)
  ElMessage.success('还书成功')
  fetch()
}

/** 打开续借弹窗，并用当前应还日期作为初始值。 */
function openRenew(row) {
  selectedRecord.value = row
  renewDueDate.value = row.dueDate
  renewDialog.value = true
}

/** 提交新的应还日期，成功后关闭弹窗并刷新记录。 */
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

/**
 * 借书日期限制：应还日期不能早于借出日期，也不能超过借出日期后 6 个月。
 * 这是日期选择器的提示限制，后端 validateBorrowRange 才是最终校验。
 */
function disabledBorrowDueDate(date) {
  const current = normalize(date)
  const borrowDate = normalize(new Date(borrowForm.borrowDate || new Date()))
  return current < borrowDate || current > normalize(addMonths(borrowDate, 6))
}

/**
 * 续借日期限制：不能早于今天，上限从原借出日期开始计算。
 * 例如已经借了 5 个月，最多只能再选择到第 6 个月，而不是重新获得 6 个月。
 */
function disabledRenewDate(date) {
  const current = normalize(date)
  const today = normalize(new Date())
  const borrowDate = selectedRecord.value?.borrowDate ? normalize(new Date(selectedRecord.value.borrowDate)) : today
  return current < today || current > normalize(addMonths(borrowDate, 6))
}

// 页面打开时默认加载“借阅中”的记录。
onMounted(fetch)
</script>

<style scoped>
/* 借书表单和借阅表格的局部布局样式。 */
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
