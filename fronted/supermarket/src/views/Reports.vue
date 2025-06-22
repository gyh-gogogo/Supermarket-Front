<template>
  <div class="reports">
    <!-- 报表选择卡片 -->
    <div class="report-cards">
      <div class="report-card" @click="activeReport = 'sales'">
        <div class="card-icon">💰</div>
        <div class="card-title">销售报表</div>
        <div class="card-desc">查看销售数据和趋势</div>
      </div>
      
      <div class="report-card" @click="activeReport = 'products'">
        <div class="card-icon">📦</div>
        <div class="card-title">商品报表</div>
        <div class="card-desc">商品销量和库存分析</div>
      </div>
      
      <div class="report-card" @click="activeReport = 'members'">
        <div class="card-icon">👥</div>
        <div class="card-title">会员报表</div>
        <div class="card-desc">会员消费情况统计</div>
      </div>
    </div>

    <!-- 销售报表 -->
    <div v-if="activeReport === 'sales'" class="report-content">
      <el-card>
        <template #header>
          <span>📊 销售报表</span>
        </template>
        
        <div class="stats-row">
          <div class="stat-item">
            <div class="stat-value">¥{{ salesStats.todayRevenue.toFixed(2) }}</div>
            <div class="stat-label">今日营业额</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ salesStats.todayOrders }}</div>
            <div class="stat-label">今日订单数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ salesStats.avgOrderValue.toFixed(2) }}</div>
            <div class="stat-label">平均客单价</div>
          </div>
        </div>
        
        <div class="chart-container">
          <div class="chart-placeholder">
            📈 销售趋势图 (图表组件待集成)
          </div>
        </div>
      </el-card>
    </div>

    <!-- 商品报表 -->
    <div v-if="activeReport === 'products'" class="report-content">
      <el-card>
        <template #header>
          <span>📦 商品报表</span>
        </template>
        
        <el-table :data="productStats" style="width: 100%">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="salesCount" label="销售数量" />
          <el-table-column prop="revenue" label="销售金额">
            <template #default="{ row }">
              ¥{{ row.revenue.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="stockQuantity" label="当前库存" />
        </el-table>
      </el-card>
    </div>

    <!-- 会员报表 -->
    <div v-if="activeReport === 'members'" class="report-content">
      <el-card>
        <template #header>
          <span>👥 会员报表</span>
        </template>
        
        <div class="stats-row">
          <div class="stat-item">
            <div class="stat-value">{{ memberStats.totalMembers }}</div>
            <div class="stat-label">总会员数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ memberStats.activeMembers }}</div>
            <div class="stat-label">活跃会员</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ memberStats.avgConsumption.toFixed(2) }}</div>
            <div class="stat-label">平均消费</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const activeReport = ref('sales')

const salesStats = reactive({
  todayRevenue: 12680.50,
  todayOrders: 89,
  avgOrderValue: 142.47
})

const productStats = ref([
  {
    productName: '可口可乐500ml',
    salesCount: 156,
    revenue: 546.00,
    stockQuantity: 85
  },
  {
    productName: '农夫山泉550ml',
    salesCount: 98,
    revenue: 245.00,
    stockQuantity: 120
  }
])

const memberStats = reactive({
  totalMembers: 896,
  activeMembers: 234,
  avgConsumption: 285.50
})

onMounted(() => {
  console.log('📊 报表页面已加载')
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
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #e67e22;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 8px;
}

.chart-placeholder {
  color: #666;
  font-size: 1.2rem;
}
</style>