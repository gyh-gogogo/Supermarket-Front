<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\CategoryManage.vue -->
<template>
  <div class="category-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品分类管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增分类
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form inline>
          <el-form-item label="分类名称">
            <el-input 
              v-model="searchForm.categoryName" 
              placeholder="请输入分类名称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="categoryId" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { categoryApi, type Category } from '../api/category'
import { deleteMessage } from '../api/request'

// 数据
const loading = ref(false)
const tableData = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  categoryName: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 5, // 修改默认每页显示5条
  total: 0
})

// 表单数据
const form = reactive<Category>({
  categoryName: '',
  description: ''
})

// 表单验证规则
const rules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      categoryName: searchForm.categoryName || undefined
    }
    
    console.log('发送请求参数:', params)
    const res = await categoryApi.getPage(params) as any
    console.log('API响应:', res)
    
    if (res && res.success) {
      tableData.value = res.data?.records || []
      pagination.total = res.data?.total || 0
      console.log('数据加载成功:', tableData.value.length, '条记录')
    } else {
      throw new Error(res?.message || '后端返回数据格式错误')
    }
  } catch (error: any) {
    console.error('加载数据失败:', error)
    ElMessage.warning('无法连接到后端服务，使用模拟数据')
    
    // API失败时使用模拟数据
    tableData.value = [
      {
        categoryId: 1,
        categoryName: '食品饮料',
        description: '各类食品和饮料',
        status: 'active',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        categoryId: 2,
        categoryName: '日用百货',
        description: '日常生活用品',
        status: 'active',
        createdAt: '2024-01-16 14:20:00'
      },
      {
        categoryId: 3,
        categoryName: '服装鞋帽',
        description: '服装和鞋帽类商品',
        status: 'active',
        createdAt: '2024-01-17 09:15:00'
      }
    ]
    pagination.total = tableData.value.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.categoryName = ''
  pagination.current = 1
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增分类'
  isEdit.value = false
  Object.assign(form, {
    categoryName: '',
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: Category) => {
  dialogTitle.value = '编辑分类'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    console.log('提交表单数据:', form)
    let result: any
    
    if (isEdit.value) {
      result = await categoryApi.update(form.categoryId!, form)
      console.log('更新结果:', result)
    } else {
      result = await categoryApi.create(form)
      console.log('创建结果:', result)
    }
    
    if (result && result.data && result.data.success) {
      ElMessage({
        message: isEdit.value ? '分类更新成功' : '分类创建成功',
        type: 'success',
        duration: 2500,
        showClose: true,
        customClass: 'data-save-message'
      })
      dialogVisible.value = false
      await loadData()
    } else {
      throw new Error(result?.data?.message || '操作失败')
    }
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage({
      message: `操作失败: ${error.message || '网络错误，请检查后端服务'}`,
      type: 'error',
      duration: 3000,
      showClose: true,
      customClass: 'operation-error-message'
    })
  }
}

const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      deleteMessage.confirm(row.categoryName, '分类'),
      '⚠️ 危险操作：物理删除确认',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger',
        customClass: 'delete-confirm-dialog',
        dangerouslyUseHTMLString: true
      }
    )
    
    console.log('💥 物理删除分类:', row.categoryId)
    const result = await categoryApi.delete(row.categoryId!)
    console.log('💥 删除结果:', result)
    
    if (result && result.success) {
      deleteMessage.success(row.categoryName, '分类')
      await loadData() // 重新加载数据
    } else {
      throw new Error(result?.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('❌ 删除失败:', error)
      deleteMessage.error(error.message || '网络错误')
    }
  }
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.category-manage {
  padding: 20px;
  min-height: calc(100vh - 40px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.el-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  transition: all 0.4s ease;
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  color: white;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header span::before {
  content: '📂';
  font-size: 24px;
}

.search-area {
  margin-bottom: 25px;
  padding: 25px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.el-form--inline .el-form-item {
  margin-right: 25px;
  margin-bottom: 15px;
}

.el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.el-input {
  transition: all 0.3s ease;
}

.el-input:hover .el-input__wrapper {
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.2);
}

.el-input .el-input__wrapper.is-focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.el-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  background: white;
}

.el-table__header-wrapper {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.el-table th {
  background: transparent !important;
  color: white !important;
  font-weight: 600;
  font-size: 14px;
  border-bottom: none;
  text-align: center;
}

.el-table td {
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
  text-align: center;
}

.el-table tbody tr {
  transition: all 0.3s ease;
}

.el-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.05), rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  transform: scale(1.01);
}

.el-table .el-button {
  margin: 0 4px;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-table .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.el-table .el-button--small {
  font-size: 12px;
}

.pagination {
  margin-top: 30px;
  text-align: center;
  padding: 25px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.el-pagination {
  justify-content: center;
}

.el-pagination .el-pager li {
  border-radius: 8px;
  margin: 0 3px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-pagination .el-pager li:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.el-pagination .el-pager li.is-active {
  background: linear-gradient(135deg, #409eff 0%, #36a3ff 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
}

.el-dialog {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(10px);
}

.el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px 30px;
}

.el-dialog__title {
  font-size: 20px;
  font-weight: 700;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.el-dialog__body {
  padding: 35px 30px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
}

.el-form-item {
  margin-bottom: 25px;
}

.el-textarea .el-textarea__inner {
  border-radius: 12px;
  padding: 15px;
  font-family: inherit;
  transition: all 0.3s ease;
}

.el-textarea .el-textarea__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 自定义消息样式 */
.data-save-message {
  font-size: 14px;
  padding: 10px 15px;
  border-radius: 8px;
  background-color: rgba(76, 175, 80, 0.1);
  color: #4caf50;
  border: 1px solid rgba(76, 175, 80, 0.3);
}

.operation-error-message {
  font-size: 14px;
  padding: 10px 15px;
  border-radius: 8px;
  background-color: rgba(244, 67, 54, 0.1);
  color: #f44336;
  border: 1px solid rgba(244, 67, 54, 0.3);
}

.operation-success-message {
  font-size: 14px;
  padding: 10px 15px;
  border-radius: 8px;
  background-color: rgba(33, 150, 243, 0.1);
  color: #2196f3;
  border: 1px solid rgba(33, 150, 243, 0.3);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .category-manage {
    padding: 10px;
  }
  
  .search-area {
    padding: 15px;
  }
  
  .el-form--inline .el-form-item {
    display: block;
    margin-right: 0;
    width: 100%;
  }
  
  .card-header {
    font-size: 16px;
    flex-direction: column;
    gap: 15px;
  }
  
  .el-table .el-button {
    padding: 6px 12px;
    font-size: 11px;
  }
}

/* 加载状态优化 */
.el-loading-mask {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
}

/* 动画效果 */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.category-manage {
  animation: slideInUp 0.6s ease-out;
}
</style>