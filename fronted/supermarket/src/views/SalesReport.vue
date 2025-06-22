<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\SalesReport.vue -->
<template>
  <div class="sales-report">
    <el-card>
      <template #header>
        <span>销售统计报表</span>
      </template>

      <!-- 查询条件 -->
      <div class="query-section">
        <el-form inline>
          <el-form-item label="查询日期">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadReportData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="总销售额" :value="summary.totalRevenue" :precision="2" prefix="¥" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="订单数量" :value="summary.totalOrders" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="平均客单价" :value="summary.avgOrderValue" :precision="2" prefix="¥" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="商品销量" :value="summary.totalItems" />
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 销售明细表 -->
      <div class="detail-section">
        <h3>销售明细</h3>
        <el-table :data="salesData" v-loading="loading">
          <el-table-column prop="saleNumber" label="销售单号" width="140" />
          <el-table-column prop="saleDate" label="销售时间" width="160" />
          <el-table-column prop="cashierName" label="收银员" width="100" />
          <el-table-column prop="memberName" label="会员" width="100" />
          <el-table-column prop="totalAmount" label="销售金额" width="100">
            <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
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
              <el-tag :type="row.status === 'completed' ? 'success' : 'danger'">
                {{ row.status === 'completed' ? '已完成' : '已取消' }}
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

interface SalesRecord {
  saleNumber: string
  saleDate: string
  cashierName: string
  memberName?: string
  totalAmount: number
  paymentMethod: string
  status: string
}

// 数据
const loading = ref(false)
const dateRange = ref<[string, string]>(['2024-01-01', '2024-12-31'])
const salesData = ref<SalesRecord[]>([])

// 统计汇总
const summary = reactive({
  totalRevenue: 0,
  totalOrders: 0,
  avgOrderValue: 0,
  totalItems: 0
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 方法
const loadReportData = () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    salesData.value = [
      {
        saleNumber: 'S202401001',
        saleDate: '2024-01-15 10:30:00',
        cashierName: '收银员A',
        memberName: '张三',
        totalAmount: 125.50,
        paymentMethod: 'cash',
        status: 'completed'
      },
      {
        saleNumber: 'S202401002',
        saleDate: '2024-01-15 11:15:00',
        cashierName: '收银员A',
        totalAmount: 89.90,
        paymentMethod: 'card',
        status: 'completed'
      },
      {
        saleNumber: 'S202401003',
        saleDate: '2024-01-15 14:20:00',
        cashierName: '收银员B',
        memberName: '李四',
        totalAmount: 256.80,
        paymentMethod: 'mobile',
        status: 'completed'
      }
    ]

    // 计算统计数据
    summary.totalRevenue = salesData.value.reduce((sum, item) => sum + item.totalAmount, 0)
    summary.totalOrders = salesData.value.length
    summary.avgOrderValue = summary.totalOrders > 0 ? summary.totalRevenue / summary.totalOrders : 0
    summary.totalItems = 158 // 模拟商品总数

    pagination.total = salesData.value.length
    loading.value = false
  }, 500)
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

// 初始化
onMounted(() => {
  loadReportData()
})
</script>

<style scoped>
.sales-report {
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

.detail-section h3 {
  margin-bottom: 15px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>