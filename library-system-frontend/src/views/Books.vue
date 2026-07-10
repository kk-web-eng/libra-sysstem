<template>
  <div class="page-shell">
    <!-- 图书列表区域：包含搜索、分页和增删改操作。 -->
    <section class="page-panel table-panel">
      <div class="toolbar table-toolbar">
        <div>
          <div class="section-title">图书列表</div>
          <p>维护馆藏基础信息、库存数量和书架位置</p>
        </div>
        <div class="toolbar-actions">
          <el-input v-model="keyword" class="search-input" placeholder="搜索书名或 ISBN" clearable :prefix-icon="Search" @keyup.enter="fetch" @clear="fetch" />
          <el-button :icon="Refresh" @click="fetch">刷新</el-button>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增图书</el-button>
        </div>
      </div>

      <!-- 表格数据来自 fetch 方法，loading 为 true 时显示加载动画。 -->
      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="isbn" label="ISBN" width="145" />
        <el-table-column prop="bookName" label="书名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="130" show-overflow-tooltip />
        <el-table-column prop="publisher" label="出版社" width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="110" />
        <!-- 库存列：进度条表示“可借数量 / 馆藏总数”。 -->
        <el-table-column label="库存" width="150">
          <template #default="{ row }">
            <el-progress :percentage="stockPercent(row)" :stroke-width="8" :show-text="false" />
            <span class="stock-text">{{ row.availableCount || 0 }} / {{ row.totalCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link :icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" link :icon="Delete" @click="doDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器修改页码或每页数量后，会重新调用 fetch。 -->
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

    <!-- 新增和编辑共用一个弹窗，通过 isEdit 区分当前操作。 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑图书' : '新增图书'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="ISBN" prop="isbn"><el-input v-model="form.isbn" placeholder="请输入 ISBN 或图书编号" /></el-form-item>
        <el-form-item label="书名" prop="bookName"><el-input v-model="form.bookName" placeholder="请输入书名" /></el-form-item>
        <el-form-item label="作者" prop="author"><el-input v-model="form.author" placeholder="请输入作者" /></el-form-item>
        <el-form-item label="出版社" prop="publisher"><el-input v-model="form.publisher" placeholder="请输入出版社" /></el-form-item>
        <el-form-item label="分类" prop="category"><el-input v-model="form.category" placeholder="请输入分类" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入图书简介" /></el-form-item>
        <el-form-item label="馆藏数量" prop="totalCount"><el-input-number v-model="form.totalCount" :min="1" /></el-form-item>
        <el-form-item label="书架位置"><el-input v-model="form.location" placeholder="例如 A-03-2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { addBook, deleteBook, getBooks, updateBook } from '@/api'

// -------------------- 页面状态 --------------------
// ref 保存会变化的简单值，值变化后页面会自动重新渲染。
const loading = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 每次新增时创建新对象，避免保留上一次编辑的数据。
const defaultForm = () => ({ id: null, isbn: '', bookName: '', author: '', publisher: '', category: '', description: '', totalCount: 1, location: '' })
// reactive 让表单对象中的字段都具有响应式能力。
const form = reactive(defaultForm())
// 保存前由 Element Plus 检查必填项。
const rules = {
  isbn: [{ required: true, message: '请输入 ISBN', trigger: 'blur' }],
  bookName: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入馆藏数量', trigger: 'blur' }]
}

/** 计算库存百分比，用于库存进度条。 */
function stockPercent(row) {
  if (!row.totalCount) return 0
  return Math.round(((row.availableCount || 0) / row.totalCount) * 100)
}

/**
 * 分页查询图书。
 * 关键词：分页、搜索、加载状态。
 * finally 一定会执行，因此请求结束后加载动画总能关闭。
 */
async function fetch() {
  loading.value = true
  try {
    const res = await getBooks({ current: current.value, size: size.value, keyword: keyword.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

/** 传入 row 表示编辑；不传 row 表示新增。 */
function openDialog(row) {
  isEdit.value = !!row
  Object.assign(form, row ? { ...row } : defaultForm())
  dialogVisible.value = true
}

/** 校验表单后，根据 isEdit 调用新增或修改接口。 */
async function doSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) {
    await updateBook({ ...form })
    ElMessage.success('修改成功')
  } else {
    await addBook({ ...form })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetch()
}

/** 删除前先确认，防止误操作。 */
async function doDelete(id) {
  await ElMessageBox.confirm('确定删除这本图书吗？', '提示', { type: 'warning' })
  await deleteBook(id)
  ElMessage.success('删除成功')
  fetch()
}

// 组件第一次显示时自动加载图书列表。
onMounted(fetch)
</script>

<style scoped>
/* scoped 表示这些样式只作用于当前页面。 */
.table-panel { padding: 22px; }
.table-toolbar { margin-bottom: 18px; }
.table-toolbar p { margin-top: 6px; color: #6b7280; }
.toolbar-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.search-input { width: 260px; }
.data-table { width: 100%; }
.stock-text { display: inline-block; margin-top: 5px; color: #6b7280; font-size: 12px; }
.pager-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
@media (max-width: 720px) { .search-input { width: 100%; } .toolbar-actions { width: 100%; } }
</style>
