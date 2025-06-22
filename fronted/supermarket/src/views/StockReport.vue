<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\StockReport.vue -->
<template>
  <div class="stock-report">
    <el-card>
      <template #header>
        <span>库存报表</span>
      </template>

      <!-- 查询条件 -->
      <div class="query-section">
        <el-form inline>
          <el-form-item label="商品名称">
            <el-input v-model="searchForm.productName" placeholder="请输入商品名称" clearable />
          </el-form-item>
          <el-form-item label="库存状态">
            <el-select v-model="searchForm.stockStatus" placeholder="请选择库存状态" clearable>
              <el-option label="正常库存" value="normal" />
              <el-option label="低库存预警" value="low" />
              <el-option label="零库存" value="zero" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadStockData">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 库存统计 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="商品总数" :value="summary.totalProducts" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="库存总价值" :value="summary.totalValue" :precision="2" prefix="¥" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="低库存商品" :value="summary.lowStockCount" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="零库存商品" :value="summary.zeroStockCount" />
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 库存明细表 -->
      <div class="detail-section">
        <el-table :data="stockData" v-loading="loading">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="barcode" label="条码" width="120" />
          <el-table-column prop="categoryName" label="分类" width="100" />
          <el-table-column prop="stockQuantity" label="当前库存" width="100">
            <template #default="{ row }">
              <span :class="getStockClass(row)">{{ row.stockQuantity }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="minStockLevel" label="最低库存" width="100" />
          <el-table-column prop="costPrice" label="进货价" width="100">
            <template #default="{ row }">¥{{ row.costPrice?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="price" label="售价" width="100">
            <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="库存价值" width="120">
            <template #default="{ row }">
              ¥{{ (row.stockQuantity * (row.costPrice || 0)).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="库存状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStockStatusType(row)">
                {{ getStockStatusText(row) }}
              </el-tag>
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
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'

interface StockItem {
  productId: number
  productName: string
  barcode: string
  categoryName: string
  stockQuantity: number
  minStockLevel: number
  costPrice?: number
  price: number
}

// 数据
const loading = ref(false)
const stockData = ref<StockItem[]>([])

// 搜索表单
const searchForm = reactive({
  productName: '',
  stockStatus: ''
})

// 统计汇总
const summary = reactive({
  totalProducts: 0,
  totalValue: 0,
  lowStockCount: 0,
  zeroStockCount: 0
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 方法
const loadStockData = () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    stockData.value = [
      {
        productId: 1,
        productName: '可口可乐500ml',
        barcode: '6901028000001',
        categoryName: '食品饮料',
        stockQuantity: 100,
        minStockLevel: 20,
        costPrice: 2.80,
        price: 3.50
      },
      {
        productId: 2,
        productName: '矿泉水500ml',
        barcode: '6901028000002',
        categoryName: '食品饮料',
        stockQuantity: 15,
        minStockLevel: 50,
        costPrice: 1.50,
        price: 2.00
      },
      {
        productId: 3,
        productName: '牙刷',
        barcode: '6901028000003',
        categoryName: '日用百货',
        stockQuantity: 0,
        minStockLevel: 10,
        costPrice: 6.50,
        price: 8.90
      },
      {
        productId: 4,
        productName: '笔记本',
        barcode: '6901028000004',
        categoryName: '图书文具',
        stockQuantity: 30,
        minStockLevel: 5,
        costPrice: 12.00,
        price: 15.80
      }
    ]

    // 计算统计数据
    summary.totalProducts = stockData.value.length
    summary.totalValue = stockData.value.reduce((sum, item) => 
      sum + (item.stockQuantity * (item.costPrice || 0)), 0)
    summary.lowStockCount = stockData.value.filter(item => 
      item.stockQuantity > 0 && item.stockQuantity <= item.minStockLevel).length
    summary.zeroStockCount = stockData.value.filter(item => 
      item.stockQuantity === 0).length

    pagination.total = stockData.value.length
    loading.value = false
  }, 500)
}

const handleReset = () => {
  searchForm.productName = ''
  searchForm.stockStatus = ''
  loadStockData()
}

const getStockClass = (row: StockItem) => {
  if (row.stockQuantity === 0) return 'zero-stock'
  if (row.stockQuantity <= row.minStockLevel) return 'low-stock'
  return 'normal-stock'
}

const getStockStatusType = (row: StockItem) => {
  if (row.stockQuantity === 0) return 'danger'
  if (row.stockQuantity <= row.minStockLevel) return 'warning'
  return 'success'
}

const getStockStatusText = (row: StockItem) => {
  if (row.stockQuantity === 0) return '零库存'
  if (row.stockQuantity <= row.minStockLevel) return '低库存'
  return '正常'
}

// 初始化
onMounted(() => {
  loadStockData()
})
</script>

<style scoped>
.stock-report {
  padding: 20px;
}

.query-section {
  margin-bottom: 20px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
}

.detail-section {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.zero-stock {
  color: #f56c6c;
  font-weight: bold;
}

.low-stock {
  color: #e6a23c;
  font-weight: bold;
}

.normal-stock {
  color: #67c23a;
}
</style>