<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>🏪 超市管理系统</h1>
        <p>{{ getGreeting() }}，{{ currentUser?.name }}！欢迎使用超市管理系统</p>
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <div class="system-status">
        <div class="status-item">
          <span class="status-dot online"></span>
          <span>系统运行正常</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <div class="stat-card sales-card">
        <div class="card-icon">💰</div>
        <div class="card-content">
          <h3>今日销售额</h3>
          <div class="stat-value">¥{{ formatNumber(todayStats.sales) }}</div>
          <div class="stat-change positive">
            <span>↗</span>
            +{{ todayStats.salesChange }}%
          </div>
        </div>
      </div>

      <div class="stat-card orders-card">
        <div class="card-icon">📦</div>
        <div class="card-content">
          <h3>今日订单数</h3>
          <div class="stat-value">{{ todayStats.orders }}</div>
          <div class="stat-change positive">
            <span>↗</span>
            +{{ todayStats.ordersChange }}%
          </div>
        </div>
      </div>

      <div class="stat-card products-card">
        <div class="card-icon">📋</div>
        <div class="card-content">
          <h3>商品总数</h3>
          <div class="stat-value">{{ todayStats.products }}</div>
          <div class="stat-subtitle">种商品在售</div>
        </div>
      </div>

      <div class="stat-card members-card">
        <div class="card-icon">👥</div>
        <div class="card-content">
          <h3>会员总数</h3>
          <div class="stat-value">{{ todayStats.members }}</div>
          <div class="stat-subtitle">注册会员</div>
        </div>
      </div>
    </div>

    <!-- 快捷操作区域 -->
    <div class="quick-actions" v-if="hasAnyPermission(['cashier', 'products', 'members', 'reports'])">
      <h2>⚡ 快捷操作</h2>
      <div class="actions-grid">
        <div v-if="hasPermission('cashier')" class="action-item" @click="navigateTo('/cashier')">
          <div class="action-icon">🛒</div>
          <div class="action-title">收银台</div>
          <div class="action-desc">快速收银结算</div>
        </div>
        
        <div v-if="hasPermission('products')" class="action-item" @click="navigateTo('/products')">
          <div class="action-icon">📦</div>
          <div class="action-title">商品管理</div>
          <div class="action-desc">添加、编辑商品</div>
        </div>
        
        <div v-if="hasPermission('members')" class="action-item" @click="navigateTo('/members')">
          <div class="action-icon">👤</div>
          <div class="action-title">会员管理</div>
          <div class="action-desc">会员信息维护</div>
        </div>
        
        <div v-if="hasPermission('reports')" class="action-item" @click="navigateTo('/reports')">
          <div class="action-icon">📈</div>
          <div class="action-title">销售报表</div>
          <div class="action-desc">查看销售数据</div>
        </div>
      </div>
    </div>

    <!-- 角色专属信息 -->
    <div class="role-specific-info">
      <div v-if="currentUser?.role === 'admin'" class="admin-panel">
        <h3>🔧 管理员专区</h3>
        <p>您拥有系统所有权限，可以管理用户、商品、会员和查看所有报表。</p>
      </div>
      
      <div v-if="currentUser?.role === 'manager'" class="manager-panel">
        <h3>📊 管理员专区</h3>
        <p>您可以管理商品信息和查看销售报表。</p>
      </div>
      
      <div v-if="currentUser?.role === 'cashier'" class="cashier-panel">
        <h3>💰 收银员专区</h3>
        <p>欢迎使用收银系统，请点击上方收银台开始工作。</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 获取当前用户信息
const currentUser = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
})

// 响应式数据
const currentTime = ref('')

const todayStats = reactive({
  sales: 12680.50,
  salesChange: 8.5,
  orders: 89,
  ordersChange: 12.3,
  products: 1256,
  members: 896
})

// 权限检查方法
const hasPermission = (permission: string) => {
  return currentUser.value?.permissions?.includes(permission) || false
}

const hasAnyPermission = (permissions: string[]) => {
  return permissions.some(permission => hasPermission(permission))
}

// 方法
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

const formatNumber = (num: number) => {
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const navigateTo = (path: string) => {
  router.push(path)
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 生命周期
let timeInterval: number

onMounted(() => {
  updateTime()
  timeInterval = window.setInterval(updateTime, 1000)
  console.log('🎯 仪表盘页面已加载，当前用户:', currentUser.value)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.welcome-content h1 {
  margin: 0 0 10px 0;
  font-size: 2.2rem;
  font-weight: bold;
}

.welcome-content p {
  margin: 0 0 10px 0;
  font-size: 1.1rem;
  opacity: 0.9;
}

.current-time {
  font-size: 0.95rem;
  opacity: 0.8;
}

.system-status {
  text-align: right;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.status-dot.online {
  background: #27ae60;
  box-shadow: 0 0 8px rgba(39, 174, 96, 0.5);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-icon {
  font-size: 2.5rem;
  margin-right: 20px;
  opacity: 0.8;
}

.card-content h3 {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-change {
  font-size: 0.85rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.positive {
  color: #27ae60;
}

.stat-subtitle {
  font-size: 0.85rem;
  color: #666;
}

.sales-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #e74c3c;
}

.orders-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #3498db;
}

.products-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #f39c12;
}

.members-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #27ae60;
}

.quick-actions {
  background: white;
  padding: 25px;
  border-radius: 12px;
  margin-bottom: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.quick-actions h2 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.action-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.action-item:hover {
  background: #e9ecef;
  border-color: #667eea;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 2.5rem;
  margin-bottom: 12px;
}

.action-title {
  font-size: 1rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 6px;
}

.action-desc {
  font-size: 0.85rem;
  color: #666;
}

.recent-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 25px;
}

.recent-activities, .inventory-alerts {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.recent-activities h3, .inventory-alerts h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.activity-list, .alert-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  font-size: 1.3rem;
  margin-right: 12px;
  opacity: 0.7;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 0.9rem;
  color: #2c3e50;
  margin-bottom: 4px;
  line-height: 1.4;
}

.activity-time {
  font-size: 0.8rem;
  color: #999;
}

.inventory-alerts {
  border-left: 4px solid #f39c12;
}

.inventory-alerts h3 {
  color: #f39c12;
}

.alert-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #fff3cd;
  border-radius: 6px;
  border: 1px solid #ffeaa7;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 4px;
  font-size: 0.9rem;
}

.alert-desc {
  font-size: 0.8rem;
  color: #666;
}

.role-specific-info {
  margin-top: 30px;
}

.admin-panel, .manager-panel, .cashier-panel {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.admin-panel {
  border-left: 4px solid #e74c3c;
}

.manager-panel {
  border-left: 4px solid #f39c12;
}

.cashier-panel {
  border-left: 4px solid #27ae60;
}

.role-specific-info h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.role-specific-info p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 15px;
  }
  
  .welcome-banner {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .recent-section {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
}
</style>