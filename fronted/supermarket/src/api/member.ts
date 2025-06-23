import request from './request'

// 对应SpringBoot的Member实体类
export interface Member {
  memberId?: number
  memberCode?: string
  memberName: string
  phone: string
  email?: string
  memberLevel: string
  points?: number
  totalConsumption?: number
  status?: string
  createdAt?: string
  updatedAt?: string
}

// 分页查询参数
export interface MemberPageParams {
  current?: number
  size?: number
  memberName?: string  // 对应后端的memberName参数
  phone?: string       // 对应后端的phone参数  
  memberLevel?: string // 对应后端的memberLevel参数
}

export const memberApi = {
  // 分页查询会员 - 确保参数名称与后端一致
  getPage: (params: MemberPageParams) => {
    console.log('📤 调用SpringBoot会员分页API:', params)
    return request.get('/api/members/page', { params })
  },

  // 创建会员 - 确保不带权限检查
  create: (data: Omit<Member, 'memberId'>) => {
    console.log('➕ 调用SpringBoot创建会员API:', data)
    
    // 确保数据格式正确，移除所有权限相关字段
    const memberData = {
      memberName: data.memberName,
      phone: data.phone,
      email: data.email || '',
      memberLevel: data.memberLevel || 'bronze',
      points: 0,
      totalConsumption: 0.0,
      status: 'active'
    }
    
    console.log('📤 实际发送的数据:', memberData)
    return request.post('/api/members', memberData)
  },

  // 更新会员 - 对应后端 MemberController.update
  update: (id: number, data: Member) => {
    console.log('📝 调用SpringBoot更新会员API:', id, data)
    return request.put(`/api/members/${id}`, data)
  },

  // 删除会员 - 对应后端 MemberController.delete
  delete: (id: number) => {
    console.log('🗑️ 调用SpringBoot删除会员API:', id)
    return request.delete(`/api/members/${id}`)
  },

  // 根据ID获取会员
  getById: (id: number) => {
    console.log('📤 调用SpringBoot会员详情API:', id)
    return request.get(`/api/members/${id}`)
  },

  // 根据手机号查询会员 - 收银台专用
  getByPhone: (phone: string) => {
    console.log('📱 调用SpringBoot手机号查询会员API:', phone)
    return request.get(`/api/members/phone/${phone}`)
  },

  // 计算会员折扣
  calculateDiscount: (memberId: number, totalAmount: number) => {
    console.log('💰 调用SpringBoot计算会员折扣API:', memberId, totalAmount)
    return request.post('/api/members/calculate-discount', {
      memberId,
      totalAmount
    })
  },

  // 积分操作 - 确保API路径正确
  updatePoints: (id: number, points: number, operation: 'add' | 'subtract') => {
    console.log('💎 调用SpringBoot积分操作API:', id, points, operation)
    
    // 确保参数格式正确
    const requestData = {
      points: Number(points),        // 确保是数字类型
      operation: String(operation),  // 确保是字符串类型
      remark: ''                     // 可选的操作说明
    }
    
    // 修正API路径 - 确保与后端Controller一致
    return request.post(`/api/members/${id}/points/operation`, requestData)
  },

  // 获取会员等级列表
  getLevels: () => {
    console.log('📊 调用会员等级列表API')
    return request.get('/api/members/levels')
  },

  // 获取会员统计信息
  getStatistics: () => {
    console.log('📈 调用会员统计API')
    return request.get('/api/members/statistics')
  },
  

  // 会员消费记录 - 调用SpringBoot的getConsumptionHistory方法
  getConsumptionHistory: (id: number) => {
    console.log('📊 调用SpringBoot会员消费记录API:', id)
    
    // 发送到SpringBoot的 GET /api/members/{id}/consumption
    return request.get(`/api/members/${id}/consumption`)
  },
  
  // 获取消费排行榜 - 调用SpringBoot的getTopSpendingMembers方法
  getTopSpendingMembers: (limit: number = 10) => {
    console.log('🏆 调用SpringBoot消费排行榜API:', limit)
    
    // 发送到SpringBoot的 GET /api/members/top-spending
    return request.get('/members/top-spending', { params: { limit } })
  },
  
  // 获取最近注册会员 - 调用SpringBoot的getRecentRegisteredMembers方法
  getRecentRegisteredMembers: (limit: number = 10) => {
    console.log('🕒 调用SpringBoot最近注册会员API:', limit)
    // 发送到SpringBoot的 GET /api/members/recent
    return request.get('/members/recent', { params: { limit } })
  },

}


