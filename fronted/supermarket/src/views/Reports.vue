<template>
  <div class="reports">
    <!-- 报表选择卡片 -->
    <div class="report-cards">
      <div class="report-card active">
        <div class="card-icon">💰</div>
        <div class="card-title">销售报表</div>
        <div class="card-desc">查看销售数据和趋势</div>
      </div>
    </div>

    <!-- 销售报表 -->
    <div class="report-content">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>📊 销售报表总览</span>
            <el-button type="primary" @click="loadSalesReport" :loading="reportLoading">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </template>
        
        <!-- 销售统计卡片 -->
        <div class="stats-row" v-loading="reportLoading">
          <div class="stat-item">
            <div class="stat-value">¥{{ salesReport.summary?.totalRevenue?.toFixed(2) || '0.00' }}</div>
            <div class="stat-label">总营业额</div>
            <div class="stat-desc">所有订单累计收入</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ salesReport.summary?.totalOrders || 0 }}</div>
            <div class="stat-label">总订单数</div>
            <div class="stat-desc">累计完成订单量</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ salesReport.summary?.avgOrderValue?.toFixed(2) || '0.00' }}</div>
            <div class="stat-label">平均客单价</div>
            <div class="stat-desc">每笔订单平均金额</div>
          </div>
        </div>
        
        <!-- 销售趋势图表 -->
        <div class="chart-section">
          <h3>📈 销售趋势图</h3>
          <div class="chart-controls">
            <el-radio-group v-model="chartDays" @change="loadSalesChart">
              <el-radio-button :label="7">近7天</el-radio-button>
              <el-radio-button :label="15">近15天</el-radio-button>
              <el-radio-button :label="30">近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div class="chart-container" v-loading="chartLoading">
            <canvas ref="salesChartRef" id="salesChart" width="800" height="300"></canvas>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { dashboardApi, type SalesReportData, type SalesChartData } from '../api/dashboard'

const activeReport = ref('sales')
const reportLoading = ref(false)
const chartLoading = ref(false)

// 图表相关
const chartDays = ref(7)
const salesChartRef = ref<HTMLCanvasElement>()

// 销售报表数据 - 只保留统计汇总
const salesReport = reactive<SalesReportData>({
  summary: {
    totalRevenue: 0,
    totalOrders: 0,
    avgOrderValue: 0,
    totalItems: 0
  },
  records: [],
  total: 0,
  current: 1,
  size: 10
})

// 加载销售报表数据 - 移除日期参数
const loadSalesReport = async () => {
  reportLoading.value = true
  try {
    console.log('📊 开始加载销售报表统计数据...')
    
    const response = await dashboardApi.getSalesReport({})
    console.log('📊 销售报表响应:', response)
    
    if (response && response.success) {
      const data = response.data
      Object.assign(salesReport.summary, {
        totalRevenue: data.summary?.totalRevenue || 0,
        totalOrders: data.summary?.totalOrders || 0,
        avgOrderValue: data.summary?.avgOrderValue || 0
      })
      
      console.log('✅ 销售报表统计加载成功')
      ElMessage.success('销售统计数据刷新成功')
    } else {
      throw new Error(response?.message || '获取销售统计失败')
    }
  } catch (error: any) {
    console.error('❌ 加载销售统计失败:', error)
    ElMessage.warning('无法连接到后端服务，请检查网络')
    
    // 后端不可用时的默认数据
    Object.assign(salesReport.summary, {
      totalRevenue: 156780.50,
      totalOrders: 1234,
      avgOrderValue: 127.16
    })
  } finally {
    reportLoading.value = false
  }
}

// 加载销售趋势图表
const loadSalesChart = async () => {
  chartLoading.value = true
  try {
    console.log('📈 开始加载销售趋势数据...')
    
    const response = await dashboardApi.getSalesChart(chartDays.value)
    console.log('📈 销售趋势响应:', response)
    
    if (response && response.success) {
      const data = response.data
      await nextTick()
      renderSalesChart(data)
      
      console.log('✅ 销售趋势图表加载成功')
    } else {
      throw new Error(response?.message || '获取销售趋势失败')
    }
  } catch (error: any) {
    console.error('❌ 加载销售趋势失败:', error)
    const mockData = generateMockChartData(chartDays.value)
    await nextTick()
    renderSalesChart(mockData)
  } finally {
    chartLoading.value = false
  }
}

// 生成模拟图表数据
const generateMockChartData = (days: number) => {
  const dates = []
  const sales = []
  const orders = []
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(Date.now() - i * 24 * 60 * 60 * 1000)
    dates.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }))
    sales.push(Math.random() * 10000 + 5000)
    orders.push(Math.floor(Math.random() * 50 + 20))
  }
  
  return { dates, sales, orders }
}

// 渲染销售趋势图表
const renderSalesChart = (data: any) => {
  const canvas = salesChartRef.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  // 清除之前的图表
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 设置画布样式
  const padding = 50
  const chartWidth = canvas.width - 2 * padding
  const chartHeight = canvas.height - 2 * padding
  
  // 计算数据范围
  const maxSales = Math.max(...data.sales)
  const maxOrders = Math.max(...data.orders)
  
  // 绘制坐标轴
  ctx.strokeStyle = '#e6e6e6'
  ctx.lineWidth = 1
  
  // Y轴
  ctx.beginPath()
  ctx.moveTo(padding, padding)
  ctx.lineTo(padding, padding + chartHeight)
  ctx.stroke()
  
  // X轴
  ctx.beginPath()
  ctx.moveTo(padding, padding + chartHeight)
  ctx.lineTo(padding + chartWidth, padding + chartHeight)
  ctx.stroke()
  
  // 绘制销售额折线
  ctx.strokeStyle = '#409eff'
  ctx.lineWidth = 2
  ctx.beginPath()
  
  data.sales.forEach((value: number, index: number) => {
    const x = padding + (index / (data.sales.length - 1)) * chartWidth
    const y = padding + chartHeight - (value / maxSales) * chartHeight
    
    if (index === 0) {
      ctx.moveTo(x, y)
    } else {
      ctx.lineTo(x, y)
    }
  })
  ctx.stroke()
  
  // 绘制订单数折线
  ctx.strokeStyle = '#67c23a'
  ctx.lineWidth = 2
  ctx.beginPath()
  
  data.orders.forEach((value: number, index: number) => {
    const x = padding + (index / (data.orders.length - 1)) * chartWidth
    const y = padding + chartHeight - (value / maxOrders) * chartHeight * 0.3
    
    if (index === 0) {
      ctx.moveTo(x, y)
    } else {
      ctx.lineTo(x, y)
    }
  })
  ctx.stroke()
  
  // 绘制X轴标签
  ctx.fillStyle = '#666'
  ctx.font = '12px Arial'
  ctx.textAlign = 'center'
  
  data.dates.forEach((date: string, index: number) => {
    const x = padding + (index / (data.dates.length - 1)) * chartWidth
    ctx.fillText(date, x, padding + chartHeight + 20)
  })
  
  // 绘制图例
  ctx.textAlign = 'left'
  ctx.fillStyle = '#409eff'
  ctx.fillRect(padding + chartWidth - 150, padding + 10, 15, 3)
  ctx.fillText('销售额', padding + chartWidth - 130, padding + 15)
  
  ctx.fillStyle = '#67c23a'
  ctx.fillRect(padding + chartWidth - 150, padding + 30, 15, 3)
  ctx.fillText('订单数', padding + chartWidth - 130, padding + 35)
}

// 工具方法
const getPaymentMethodType = (method: string) => {
  const types: Record<string, string> = {
    cash: 'success',
    card: 'warning',
    alipay: 'info',
    wechat: 'success'
  }
  return types[method] || 'info'
}

const getPaymentMethodText = (method: string) => {
  const texts: Record<string, string> = {
    cash: '现金',
    card: '银行卡',
    alipay: '支付宝',
    wechat: '微信支付'
  }
  return texts[method] || method
}

// 生命周期
onMounted(async () => {
  console.log('📊 报表页面已加载')
  await loadSalesReport()
  await loadSalesChart()
})
</script>

<style scoped>
.reports {
  padding: 20px;
}

.report-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.report-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.report-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
}

.card-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
}

.card-desc {
  color: #666;
  font-size: 0.9rem;
}

.report-content {
  margin-top: 20px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.stat-item {
  text-align: center;
  padding: 30px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #e67e22;
  margin-bottom: 10px;
  line-height: 1;
}

.stat-label {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-desc {
  font-size: 0.85rem;
  color: #666;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-section {
  margin: 40px 0;
}

.chart-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.chart-controls {
  margin-bottom: 20px;
  text-align: center;
}

.chart-container {
  background: #fafafa;
  border-radius: 8px;
  padding: 20px;
  min-height: 320px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-container canvas {
  border-radius: 4px;
  background: white;
}

.report-card.active {
  border-color: #409eff;
  background: linear-gradient(135deg, #e8f4fd 0%, #f0f9ff 100%);
  transform: translateY(-3px);
  cursor: default;
}
</style>