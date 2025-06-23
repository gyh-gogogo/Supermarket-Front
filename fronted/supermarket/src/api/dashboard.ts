import request from './request'

export interface DashboardStats {
  todaySales: number
  todayOrders: number
  totalProducts: number
  totalMembers: number
  salesChange?: number
  ordersChange?: number
}

export interface SystemOverview {
  totalProducts: number
  totalMembers: number
  activeMembers: number
  lowStockCount: number
}

export const dashboardApi = {
  // 获取今日统计数据
  getTodayStats: () => {
    console.log('📊 调用今日统计API')
    return request.get('/api/dashboard/today-stats')
  },

  // 获取系统概览数据
  getSystemOverview: () => {
    console.log('📋 调用系统概览API')
    return request.get('/api/dashboard/overview')
  },

  // 获取低库存商品
  getLowStockProducts: (minStockLevel: number = 20, limit: number = 10) => {
    console.log('⚠️ 调用低库存商品API')
    return request.get('/api/dashboard/low-stock', {
      params: { minStockLevel, limit }
    })
  },

  // 获取最近活动记录
  getRecentActivities: (limit: number = 10) => {
    console.log('🕒 调用最近活动API')
    return request.get('/api/dashboard/recent-activities', {
      params: { limit }
    })
  },

  // 获取销售趋势图表数据
  getSalesChart: (days: number = 7) => {
    console.log('📈 调用销售趋势API')
    return request.get('/api/dashboard/sales-chart', {
      params: { days }
    })
  },

  // 获取热销商品排行
  getTopProducts: (limit: number = 10) => {
    console.log('🏆 调用热销商品API')
    return request.get('/api/dashboard/top-products', {
      params: { limit }
    })
  }
}