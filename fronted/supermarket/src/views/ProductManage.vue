<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\ProductManage.vue -->
<template>
  <div class="product-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品信息管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增商品
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form inline>
          <el-form-item label="商品名称">
            <el-input 
              v-model="searchForm.productName" 
              placeholder="请输入商品名称"
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
      <div class="table-container">
        <el-table :data="tableData" v-loading="loading" style="width: 100%">
          <el-table-column prop="productId" label="ID" width="80" />
          <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="barcode" label="条码" width="140" />
          <el-table-column prop="price" label="售价" width="100" align="right">
            <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="costPrice" label="进价" width="100" align="right">
            <template #default="{ row }">¥{{ row.costPrice?.toFixed(2) || '0.00' }}</template>
          </el-table-column>
          <el-table-column prop="stockQuantity" label="库存" width="80" align="center" />
          <el-table-column prop="createdAt" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品条码" prop="barcode">
              <el-input v-model="form.barcode" placeholder="请输入商品条码" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="售价" prop="price">
              <el-input-number 
                v-model="form.price" 
                :precision="2" 
                :min="0" 
                style="width: 100%"
                placeholder="请输入售价"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="进货价" prop="costPrice">
              <el-input-number 
                v-model="form.costPrice" 
                :precision="2" 
                :min="0" 
                style="width: 100%"
                placeholder="请输入进货价"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number 
                v-model="form.stockQuantity" 
                :min="0" 
                style="width: 100%"
                placeholder="请输入库存数量"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低库存" prop="minStockLevel">
              <el-input-number 
                v-model="form.minStockLevel" 
                :min="0" 
                style="width: 100%"
                placeholder="请输入最低库存"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入商品描述"
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
import { productApi, type Product } from '../api/product'
import { deleteMessage } from '../api/request'

// 数据
const loading = ref(false)
const tableData = ref<Product[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 搜索表单（移除分类搜索）
const searchForm = reactive({
  productName: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 5,
  total: 0
})

// 表单数据（移除分类相关字段）
const form = reactive<Product>({
  productName: '',
  barcode: '',
  price: 0,
  costPrice: 0,
  stockQuantity: 0,
  minStockLevel: 10,
  description: ''
})

// 表单验证规则（移除分类验证）
const rules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品售价', trigger: 'blur' }
  ],
  stockQuantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ]
}

// 方法
const loadData = async () => {
  loading.value = true
  console.log('🔍 开始加载商品数据...')
  
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      productName: searchForm.productName || undefined
    }
    
    console.log('📤 发送请求参数:', params)
    const res = await productApi.getPage(params) as any
    console.log('📥 收到API响应:', res)
    
    if (res && res.success) {
      const records = res.data?.records || []
      const total = res.data?.total || 0
      
      console.log(`✅ 数据加载成功: ${records.length} 条记录，总计: ${total}`)
      tableData.value = records
      pagination.total = total
    } else {
      throw new Error(res?.message || '后端返回数据格式错误')
    }
  } catch (error: any) {
    console.error('❌ 加载商品数据失败:', error)
    ElMessage.warning('无法连接到后端服务，使用模拟数据')
    
    // API失败时使用模拟数据
    tableData.value = [
      {
        productId: 1,
        productName: '可口可乐500ml',
        barcode: '6901028000001',
        price: 3.50,
        costPrice: 2.80,
        stockQuantity: 100,
        minStockLevel: 20,
        description: '经典可口可乐500ml装',
        status: 'active',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        productId: 2,
        productName: '矿泉水500ml',
        barcode: '6901028000002',
        price: 2.00,
        costPrice: 1.50,
        stockQuantity: 200,
        minStockLevel: 50,
        description: '天然矿泉水500ml装',
        status: 'active',
        createdAt: '2024-01-16 09:15:00'
      }
    ]
    pagination.total = tableData.value.length
    console.log('📝 使用模拟数据:', tableData.value.length, '条')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.productName = ''
  pagination.current = 1
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增商品'
  isEdit.value = false
  
  // 重要：新增时清空所有字段，特别是ID
  Object.assign(form, {
    productId: undefined, // 不设置ID，让后端自动生成
    productName: '',
    barcode: '',
    price: 0,
    costPrice: 0,
    stockQuantity: 0,
    minStockLevel: 10,
    description: ''
  })
  
  dialogVisible.value = true
}

const handleEdit = (row: Product) => {
  dialogTitle.value = '编辑商品'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    console.log('📤 提交商品数据:', form)
    let result: any
    
    if (isEdit.value) {
      // 编辑时保留ID
      result = await productApi.update(form.productId!, form)
      console.log('📝 更新结果:', result)
    } else {
      // 新增时删除ID字段，确保不传递给后端
      const newProduct = { ...form }
      delete newProduct.productId // 确保删除ID
      
      result = await productApi.create(newProduct)
      console.log('➕ 创建结果:', result)
    }
    
    if (result && result.success) {
      ElMessage({
        message: isEdit.value ? '商品更新成功' : '商品创建成功',
        type: 'success',
        duration: 2500,
        showClose: true,
        customClass: 'data-save-message'
      })
      dialogVisible.value = false
      await loadData() // 重新加载数据
    } else {
      throw new Error(result?.message || '操作失败')
    }
  } catch (error: any) {
    console.error('❌ 提交失败:', error)
    
    // 特殊处理主键冲突错误
    let errorMessage = error.message || '网络错误，请检查后端服务'
    if (errorMessage.includes('Duplicate entry') && errorMessage.includes('PRIMARY')) {
      errorMessage = '数据冲突：主键重复，请刷新页面后重试'
    } else if (errorMessage.includes('Duplicate entry') && errorMessage.includes('barcode')) {
      errorMessage = '条码重复：该条码已存在，请使用不同的条码'
    }
    
    ElMessage({
      message: `操作失败: ${errorMessage}`,
      type: 'error',
      duration: 4000,
      showClose: true,
      customClass: 'operation-error-message'
    })
  }
}

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      deleteMessage.confirm(row.productName, '商品'),
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
    
    console.log('💥 物理删除商品:', row.productId, row.productName)
    const result = await productApi.delete(row.productId!)
    console.log('💥 删除结果:', result)
    
    if (result && result.success) {
      deleteMessage.success(row.productName, '商品')
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

// 初始化（移除分类加载）
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.product-manage {
  padding: 15px;
  min-height: calc(100vh - 30px);
  max-width: 100%;
}

.el-card {
  width: 100%;
  max-width: 100%;
  overflow: hidden;
}

.table-container {
  width: 100%;
  overflow-x: auto;
  margin: 20px 0;
}

.search-area {
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.el-form--inline {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 10px;
  flex-shrink: 0;
  min-width: 200px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  padding: 0;
  color: white;
  font-size: 18px;
  font-weight: 700;
}

.pagination {
  margin-top: 20px;
  text-align: center;
  padding: 15px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .product-manage {
    padding: 10px;
  }
  
  .el-form--inline {
    flex-direction: column;
    align-items: stretch;
  }
  
  .el-form--inline .el-form-item {
    width: 100%;
    min-width: auto;
  }
  
  .card-header {
    flex-direction: column;
    text-align: center;
  }
}
</style>