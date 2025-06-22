<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\SalesManage.vue -->
<template>
  <div class="sales-manage">
    <el-card>
      <template #header>
        <span>销售记录管理</span>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form inline>
          <el-form-item label="销售单号">
            <el-input 
              v-model="searchForm.saleNumber" 
              placeholder="请输入销售单号"
              clearable
            />
          </el-form-item>
          <el-form-item label="支付方式">
            <el-select v-model="searchForm.paymentMethod" placeholder="请选择支付方式" clearable>
              <el-option label="现金" value="cash" />
              <el-option label="银行卡" value="card" />
              <el-option label="移动支付" value="mobile" />
            </el-select>
          </el-form-item>
          <el-form-item label="销售日期">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
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
        <el-table-column prop="saleNumber" label="销售单号" width="140" />
        <el-table-column prop="saleDate" label="销售时间" width="160" />
        <el-table-column prop="cashierName" label="收银员" width="100" />
        <el-table-column prop="memberName" label="会员" width="100" />
        <el-table-column prop="totalAmount" label="商品金额" width="100" align="right">
          <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠金额" width="100" align="right">
          <template #default="{ row }">¥{{ row.discountAmount?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="finalAmount" label="实收金额" width="100" align="right">
          <template #default="{ row }">¥{{ row.finalAmount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentMethodType(row.paymentMethod)">
              {{ getPaymentMethodText(row.paymentMethod) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看详情</el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="handleRefund(row)"
              :disabled="row.status !== 'completed'"
            >
              退货
            </el-button>
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

    <!-- 销售详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="销售详情" width="800px">
      <div v-if="selectedSale">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="销售单号">{{ selectedSale.saleNumber }}</el-descriptions-item>
          <el-descriptions-item label="销售时间">{{ selectedSale.saleDate }}</el-descriptions-item>
          <el-descriptions-item label="收银员">{{ selectedSale.cashierName }}</el-descriptions-item>
          <el-descriptions-item label="会员">{{ selectedSale.memberName || '非会员' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPaymentMethodText(selectedSale.paymentMethod) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusText(selectedSale.status) }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 10px 0;">商品明细</h4>
        <el-table :data="saleItems" style="width: 100%">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="unitPrice" label="单价" width="100" align="right">
            <template #default="{ row }">¥{{ row.unitPrice.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="totalPrice" label="小计" width="100" align="right">
            <template #default="{ row }">¥{{ row.totalPrice.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        
        <div class="total-summary">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-statistic title="商品金额" :value="selectedSale.totalAmount" :precision="2" prefix="¥" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="优惠金额" :value="selectedSale.discountAmount || 0" :precision="2" prefix="¥" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="实收金额" :value="selectedSale.finalAmount" :precision="2" prefix="¥" />
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Sale {
  saleId: number
  saleNumber: string
  saleDate: string
  cashierName: string
  memberName?: string
  totalAmount: number
  discountAmount?: number
  finalAmount: number
  paymentMethod: string
  status: string
}

// 数据
const loading = ref(false)
const tableData = ref<Sale[]>([])
const selectedSale = ref<Sale | null>(null)
const saleItems = ref<any[]>([])
const detailDialogVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  saleNumber: '',
  paymentMethod: '',
  dateRange: [] as string[]
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 方法
const loadData = () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    tableData.value = [
      {
        saleId: 1,
        saleNumber: 'S20240120001',
        saleDate: '2024-01-20 10:30:00',
        cashierName: '收银员01',
        memberName: '张三',
        totalAmount: 125.50,
        discountAmount: 6.28,
        finalAmount: 119.22,
        paymentMethod: 'cash',
        status: 'completed'
      },
      {
        saleId: 2,
        saleNumber: 'S20240120002',
        saleDate: '2024-01-20 11:15:00',
        cashierName: '收银员01',
        totalAmount: 89.90,
        finalAmount: 89.90,
        paymentMethod: 'card',
        status: 'completed'
      },
      {
        saleId: 3,
        saleNumber: 'S20240120003',
        saleDate: '2024-01-20 14:20:00',
        cashierName: '收银员02',
        memberName: '李四',
        totalAmount: 256.80,
        discountAmount: 12.84,
        finalAmount: 243.96,
        paymentMethod: 'mobile',
        status: 'refunded'
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
  searchForm.saleNumber = ''
  searchForm.paymentMethod = ''
  searchForm.dateRange = []
  loadData()
}

const handleView = (row: Sale) => {
  selectedSale.value = row
  // 模拟加载销售明细
  saleItems.value = [
    { productName: '可口可乐500ml', unitPrice: 3.50, quantity: 2, totalPrice: 7.00 },
    { productName: '矿泉水500ml', unitPrice: 2.00, quantity: 3, totalPrice: 6.00 }
  ]
  detailDialogVisible.value = true
}

const handleRefund = async (row: Sale) => {
  try {
    await ElMessageBox.confirm('确定要对该销售单进行退货吗？', '退货确认', {
      type: 'warning'
    })
    // 模拟退货处理
    row.status = 'refunded'
    ElMessage.success('退货成功')
  } catch (error) {
    // 用户取消
  }
}

const getPaymentMethodType = (method: string) => {
  const types: Record<string, string> = {
    cash: 'success',
    card: 'warning',
    mobile: 'info'
  }
  return types[method] || 'info'
}

const getPaymentMethodText = (method: string) => {
  const texts: Record<string, string> = {
    cash: '现金',
    card: '银行卡',
    mobile: '移动支付'
  }
  return texts[method] || method
}

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    completed: 'success',
    cancelled: 'info',
    refunded: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    completed: '已完成',
    cancelled: '已取消',
    refunded: '已退货'
  }
  return texts[status] || status
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.sales-manage {
  padding: 20px;
}

.search-area {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.total-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
}
</style>