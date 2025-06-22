import request from './request'

export interface DashboardStats {
  todaySales: number
  todayOrders: number
  todayCustomers: number
  totalProducts: number
  totalMembers: number
  lowStockCount: number
}

export interface LowStockProduct {
  productId: number
  productName: string
  currentStock: number
  minStockLevel: number
  stockStatus: string
  urgencyLevel: string
}

export interface RecentActivity {
  id: number
  type: string
  description: string
  time: string
  operator: string
}

export const dashboardApi = {
  // 获取今日统计数据 - 调用SpringBoot今日统计API
  getTodayStats: () => {
    console.log('📊 调用SpringBoot今日统计API')
    
    // 发送到SpringBoot的 GET /api/dashboard/today-stats
    return request.get('/dashboard/today-stats')
  },

  // 获取低库存商品 - 调用SpringBoot的getLowStockProducts方法
  getLowStockProducts: (params: {
    minStockLevel?: number
    limit?: number
  } = {}) => {
    console.log('⚠️ 调用SpringBoot低库存商品API:', params)
    
    // 发送到SpringBoot的 GET /api/dashboard/low-stock
    return request.get('/dashboard/low-stock', { 
      params: {
        minStockLevel: params.minStockLevel || 20,
        limit: params.limit || 10
      }
    })
  },

  // 获取最近活动 - 调用SpringBoot的getRecentActivities方法
  getRecentActivities: (limit: number = 10) => {
    console.log('🕒 调用SpringBoot最近活动API:', limit)
    
    // 发送到SpringBoot的 GET /api/dashboard/recent-activities
    return request.get('/dashboard/recent-activities', { params: { limit } })
  },

  // 获取系统概览 - 调用SpringBoot的getSystemOverview方法
  getSystemOverview: () => {
    console.log('📋 调用SpringBoot系统概览API')
    
    // 发送到SpringBoot的 GET /api/dashboard/overview
    return request.get('/dashboard/overview')
  },

  // 获取销售图表数据 - 调用SpringBoot的getSalesChart方法
  getSalesChart: (days: number = 7) => {
    console.log('📈 调用SpringBoot销售图表API:', days)
    
    // 发送到SpringBoot的 GET /api/dashboard/sales-chart
    return request.get('/dashboard/sales-chart', { params: { days } })
  },

  // 获取热销商品排行 - 调用SpringBoot的getTopProducts方法
  getTopProducts: (limit: number = 10) => {
    console.log('🏆 调用SpringBoot热销商品API:', limit)
    
    // 发送到SpringBoot的 GET /api/dashboard/top-products
    return request.get('/dashboard/top-products', { params: { limit } })
  }
}