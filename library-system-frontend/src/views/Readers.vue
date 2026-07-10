<template>
  <div class="page-shell">
    <!-- 用户列表：支持搜索、分页、新增、编辑和删除。 -->
    <section class="page-panel table-panel">
      <div class="toolbar table-toolbar">
        <div>
          <div class="section-title">用户管理</div>
          <p>维护用户 6 位及以上账号、密码、姓名、电话和启用状态</p>
        </div>
        <div class="toolbar-actions">
          <el-input v-model="keyword" class="search-input" placeholder="搜索姓名或编号" clearable :prefix-icon="Search" @keyup.enter="fetch" @clear="fetch" />
          <el-button :icon="Refresh" @click="fetch">刷新</el-button>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </div>

      <!-- status=1 显示正常，status=0 显示停用。 -->
      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="readerNo" label="用户账号" width="150" />
        <el-table-column prop="password" label="密码" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="className" label="班级/部门" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="light">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
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

    <!-- 新增和编辑共用表单，编辑时额外显示账号状态开关。 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="账号" prop="readerNo"><el-input v-model="form.readerNo" placeholder="请输入 6 位及以上账号" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" show-password placeholder="请输入密码" /></el-form-item>
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio value="男">男</el-radio><el-radio value="女">女</el-radio></el-radio-group></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" placeholder="请输入联系电话" /></el-form-item>
        <el-form-item label="班级/部门"><el-input v-model="form.className" placeholder="请输入班级或部门" /></el-form-item>
        <el-form-item v-if="isEdit" label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
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
import { addReader, deleteReader, getReaders, updateReader } from '@/api'

// -------------------- 查询与分页状态 --------------------
const loading = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 创建干净的默认表单。状态 1 表示新用户默认启用。
const defaultForm = () => ({ id: null, readerNo: '', password: '', name: '', gender: '男', phone: '', className: '', status: 1 })
const form = reactive(defaultForm())
// 账号至少 6 位；密码不限制组成，但不能为空；姓名为必填项。
const rules = {
  readerNo: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 6, message: '账号至少 6 位', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

/** 分页查询用户，关键词交给后端匹配账号、姓名或电话。 */
async function fetch() {
  loading.value = true
  try {
    const res = await getReaders({ current: current.value, size: size.value, keyword: keyword.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

/** 打开新增或编辑弹窗，并把当前行复制到表单。 */
function openDialog(row) {
  isEdit.value = !!row
  Object.assign(form, row ? { ...row } : defaultForm())
  dialogVisible.value = true
}

/** 先执行前端校验，再调用新增或修改接口。 */
async function doSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) {
    await updateReader({ ...form })
    ElMessage.success('修改成功')
  } else {
    await addReader({ ...form })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetch()
}

/** 确认后删除读者，后端还会检查关联借阅记录。 */
async function doDelete(id) {
  await ElMessageBox.confirm('确定删除这位读者吗？', '提示', { type: 'warning' })
  await deleteReader(id)
  ElMessage.success('删除成功')
  fetch()
}

// 页面挂载完成后立即请求第一页用户数据。
onMounted(fetch)
</script>

<style scoped>
/* 用户管理页面的局部布局样式。 */
.table-panel { padding: 22px; }
.table-toolbar { margin-bottom: 18px; }
.table-toolbar p { margin-top: 6px; color: #6b7280; }
.toolbar-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.search-input { width: 250px; }
.pager-wrap { display: flex; justify-content: flex-end; margin-top: 18px; }
@media (max-width: 720px) { .search-input { width: 100%; } .toolbar-actions { width: 100%; } }
</style>
