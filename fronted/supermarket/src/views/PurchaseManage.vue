<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\PurchaseManage.vue -->
<template>
  <div class="purchase-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>进货管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增进货
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form inline>
          <el-form-item label="进货单号">
            <el-input 
              v-model="searchForm.purchaseNumber" 
              placeholder="请输入进货单号"
              clearable
            />
          </el-form-item>
          <el-form-item label="供应商">
            <el-input 
              v-model="searchForm.supplierName" 
              placeholder="请输入供应商名称"
              clearable
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
        <el-table-column prop="purchaseNumber" label="进货单号" width="140" />
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作员" width="100" />
        <el-table-column prop="purchaseDate" label="进货时间" width="160" />
        <el-table-column prop="notes" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
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
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="供应商" prop="supplierName">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input-number 
            v-model="form.totalAmount" 
            :precision="2" 
            :min="0" 
            style="width: 100%"
            placeholder="请输入总金额"
          />
        </el-form-item>
        <el-form-item label="操作员ID" prop="operatorId">
          <el-input-number 
            v-model="form.operatorId" 
            :min="1" 
            style="width: 100%"
            placeholder="请输入操作员ID"
          />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input 
            v-model="form.notes" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注"
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

interface Purchase {
  purchaseId?: number
  purchaseNumber?: string
  supplierName: string
  totalAmount: number
  operatorId: number
  notes?: string
  purchaseDate?: string
  operatorName?: string
}

// 数据
const loading = ref(false)
const tableData = ref<Purchase[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  purchaseNumber: '',
  supplierName: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive<Purchase>({
  supplierName: '',
  totalAmount: 0,
  operatorId: 1,
  notes: ''
})

// 表单验证规则
const rules = {
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  totalAmount: [{ required: true, message: '请输入总金额', trigger: 'blur' }],
  operatorId: [{ required: true, message: '请输入操作员ID', trigger: 'blur' }]
}

// 方法
const loadData = () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    tableData.value = [
      {
        purchaseId: 1,
        purchaseNumber: 'P20240120001',
        supplierName: '供应商A',
        totalAmount: 1500.00,
        operatorId: 1,
        operatorName: '管理员',
        purchaseDate: '2024-01-20 10:30:00',
        notes: '进货备注'
      },
      {
        purchaseId: 2,
        purchaseNumber: 'P20240120002',
        supplierName: '供应商B',
        totalAmount: 2800.50,
        operatorId: 2,
        operatorName: '商品管理员',
        purchaseDate: '2024-01-20 14:15:00',
        notes: '批量进货'
      }
    ]
    pagination.total = tableData.value.length
    loading.value = false
  }, 500)
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.purchaseNumber = ''
  searchForm.supplierName = ''
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增进货'
  isEdit.value = false
  Object.assign(form, {
    supplierName: '',
    totalAmount: 0,
    operatorId: 1,
    notes: ''
  })
  dialogVisible.value = true
}

const handleView = (row: Purchase) => {
  ElMessage.info('查看进货详情功能待实现')
}

const handleEdit = (row: Purchase) => {
  dialogTitle.value = '编辑进货'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row: Purchase) => {
  try {
    await ElMessageBox.confirm('确定要删除这个进货记录吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消删除
  }
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.purchase-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>