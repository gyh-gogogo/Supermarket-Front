<template>
  <div class="products">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📦 商品管理</span>
          <el-button type="primary" @click="showAddDialog">
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
            />
          </el-form-item>
          <el-form-item label="商品分类">
            <el-select 
              v-model="searchForm.categoryId" 
              placeholder="请选择商品分类" 
              clearable
              style="width: 180px;"
            >
              <el-option 
                v-for="category in categories" 
                :key="category.categoryId" 
                :label="category.categoryName" 
                :value="category.categoryId" 
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="productId" label="ID" width="80" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="categoryName" label="商品分类" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.categoryName" type="info" size="small">
              {{ row.categoryName }}
            </el-tag>
            <span v-else class="text-muted">未分类</span>
          </template>
        </el-table-column>
        <el-table-column prop="barcode" label="条码" width="140" />
        <el-table-column prop="price" label="售价" width="100">
          <template #default="{ row }">¥{{ row.price?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="costPrice" label="进价" width="100">
          <template #default="{ row }">¥{{ row.costPrice?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存" width="80" align="center">
          <template #default="{ row }">
            <span :class="getStockClass(row.stockQuantity)">
              {{ row.stockQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
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
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select 
            v-model="form.categoryId" 
            placeholder="请选择商品分类" 
            style="width: 100%"
            clearable
          >
            <el-option 
              v-for="category in categories" 
              :key="category.categoryId" 
              :label="category.categoryName" 
              :value="category.categoryId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品条码" prop="barcode">
          <el-input v-model="form.barcode" placeholder="请输入商品条码" />
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number 
            v-model="form.price" 
            :precision="2" 
            :min="0" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="进货价" prop="costPrice">
          <el-input-number 
            v-model="form.costPrice" 
            :precision="2" 
            :min="0" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input-number 
            v-model="form.stockQuantity" 
            :min="0" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStockLevel">
          <el-input-number 
            v-model="form.minStockLevel" 
            :min="0" 
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi, type Product } from '../api/product'
import { categoryApi, type Category } from '../api/category'

const loading = ref(false)
const tableData = ref<Product[]>([])
const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({
  productName: '',
  categoryId: undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive<Product>({
  productName: '',
  categoryId: undefined,
  barcode: '',
  price: 0,
  costPrice: 0,
  stockQuantity: 0,
  minStockLevel: 10,
  description: ''
})

const rules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

// 创建分类映射对象，用于快速查找分类名称
const categoryMap = computed(() => {
  const map = new Map()
  categories.value.forEach(category => {
    map.set(category.categoryId, category.categoryName)
  })
  return map
})

// 加载分类列表
const loadCategories = async () => {
  try {
    console.log('📤 加载分类列表...')
    const response = await categoryApi.getList()
    
    if (response && response.success) {
      categories.value = response.data || []
      console.log(`✅ 加载了 ${categories.value.length} 个分类`)
    } else {
      throw new Error('API返回失败')
    }
  } catch (error) {
    console.error('❌ 加载分类失败:', error)
    // 使用模拟分类数据
    categories.value = [
      { categoryId: 1, categoryName: '食品饮料', description: '各类食品和饮料' },
      { categoryId: 2, categoryName: '日用百货', description: '日常生活用品' },
      { categoryId: 3, categoryName: '服装鞋帽', description: '服装和鞋帽类商品' },
      { categoryId: 4, categoryName: '图书文具', description: '图书和文具用品' },
      { categoryId: 5, categoryName: '电子产品', description: '电子设备和配件' }
    ]
    console.log('📝 使用模拟分类数据:', categories.value.length, '个')
  }
}

const loadData = async () => {
  loading.value = true
  console.log('🔍 开始加载商品数据...')
  
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      productName: searchForm.productName || undefined,
      categoryId: searchForm.categoryId || undefined
    }
    
    console.log('📤 发送请求参数:', params)
    const response = await productApi.getPage(params)
    console.log('📥 收到API响应:', response)
    
    if (response && response.success) {
      const data = response.data
      if (data && data.records) {
        // 处理商品数据，添加分类名称
        tableData.value = data.records.map(product => ({
          ...product,
          categoryName: product.categoryId ? categoryMap.value.get(product.categoryId) : undefined
        }))
        pagination.total = data.total || 0
        
        console.log(`✅ 数据加载成功: ${tableData.value.length} 条记录，总计: ${pagination.total}`)
        ElMessage.success(`成功加载 ${tableData.value.length} 条商品数据`)
      } else {
        console.warn('⚠️ 响应数据格式异常:', data)
        tableData.value = []
        pagination.total = 0
        ElMessage.warning('数据格式异常，请检查后端接口')
      }
    } else {
      throw new Error(response?.message || '后端返回失败状态')
    }
  } catch (error: any) {
    console.error('❌ 加载商品数据失败:', error)
    ElMessage.error(`数据加载失败: ${error.message}`)
    
    // 显示模拟数据，包含正确的分类信息
    tableData.value = [
      {
        productId: 1,
        productName: '可口可乐500ml',
        categoryId: 1,
        categoryName: '食品饮料',
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
        categoryId: 1,
        categoryName: '食品饮料',
        barcode: '6901028000002',
        price: 2.00,
        costPrice: 1.50,
        stockQuantity: 200,
        minStockLevel: 50,
        description: '天然矿泉水500ml装',
        status: 'active',
        createdAt: '2024-01-16 09:15:00'
      },
      {
        productId: 3,
        productName: '牙刷',
        categoryId: 2,
        categoryName: '日用百货',
        barcode: '6901028000003',
        price: 8.90,
        costPrice: 6.50,
        stockQuantity: 5,
        minStockLevel: 10,
        description: '软毛护齿牙刷',
        status: 'active',
        createdAt: '2024-01-17 14:22:00'
      },
      {
        productId: 4,
        productName: '无分类商品',
        categoryId: null,
        categoryName: undefined,
        barcode: '6901028000004',
        price: 10.00,
        costPrice: 8.00,
        stockQuantity: 50,
        minStockLevel: 10,
        description: '测试无分类商品',
        status: 'active',
        createdAt: '2024-01-18 16:30:00'
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
  searchForm.productName = ''
  searchForm.categoryId = undefined
  pagination.current = 1
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增商品'
  isEdit.value = false
  Object.assign(form, {
    productName: '',
    categoryId: undefined,
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
    let response: any
    
    if (isEdit.value) {
      response = await productApi.update(form.productId!, form)
    } else {
      const newProduct = { ...form }
      delete newProduct.productId
      response = await productApi.create(newProduct)
    }
    
    console.log('📥 提交结果:', response)
    
    if (response && response.success) {
      ElMessage.success(isEdit.value ? '商品更新成功' : '商品创建成功')
      dialogVisible.value = false
      loadData()
    } else {
      throw new Error(response?.message || '操作失败')
    }
  } catch (error: any) {
    console.error('❌ 提交失败:', error)
    ElMessage.error(`操作失败: ${error.message}`)
  }
}

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      type: 'warning'
    })
    
    console.log('💥 删除商品:', row.productId, row.productName)
    const response = await productApi.delete(row.productId!)
    
    if (response && response.success) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('❌ 删除失败:', error)
      ElMessage.error(`删除失败: ${error.message}`)
    }
  }
}

const getStockClass = (stock: number) => {
  if (stock <= 0) return 'stock-zero'
  if (stock <= 10) return 'stock-low'
  return 'stock-normal'
}

// 初始化时先加载分类，再加载商品
onMounted(async () => {
  console.log('🎉 商品管理页面已加载，开始获取数据...')
  await loadCategories()  // 先加载分类
  await loadData()        // 再加载商品
})
</script>

<style scoped>
.products {
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

.text-muted {
  color: #999;
  font-style: italic;
}

.stock-zero {
  color: #f56c6c;
  font-weight: bold;
}

.stock-low {
  color: #e6a23c;
  font-weight: bold;
}

.stock-normal {
  color: #67c23a;
}
</style>