import request from './request'

// 对应SpringBoot的Member实体类
export interface Member {
  memberId?: number
  memberCode?: string
  memberName: string
  phone: string  // 对应SpringBoot的phone字段
  points?: number
  totalConsumption?: number
  memberLevel?: string
  status?: string
  createdAt?: string
  updatedAt?: string
}

export const memberApi = {
  // 分页查询会员 - 调用SpringBoot MemberController的getPage方法
  getPage: (params: {
    current?: number
    size?: number
    memberName?: string
    phone?: string
  }) => {
    console.log('👥 调用SpringBoot会员分页API:', params)
    
    // 发送到SpringBoot的 GET /api/members/page
    return request.get('/members/page', { 
      params: {
        current: params.current || 1,
        size: params.size || 10,
        memberName: params.memberName,
        phone: params.phone
      }
    })
  },
  
  // 根据手机号查询会员（收银台专用）- 调用SpringBoot的getByPhone方法
  getByPhone: (phone: string) => {
    console.log('📱 调用SpringBoot根据手机号查询会员API:', phone)
    
    // 发送到SpringBoot的 GET /api/members/phone/{phone}
    return request.get(`/members/phone/${phone}`)
  },
  
  // 根据会员卡号查询会员 - 调用SpringBoot的getByMemberCode方法
  getByMemberCode: (memberCode: string) => {
    console.log('💳 调用SpringBoot根据会员卡号查询API:', memberCode)
    
    // 发送到SpringBoot的 GET /api/members/code/{memberCode}
    return request.get(`/members/code/${memberCode}`)
  },
  
  // 获取会员列表 - 调用SpringBoot的getList方法
  getList: () => {
    console.log('📋 调用SpringBoot会员列表API')
    
    // 发送到SpringBoot的 GET /api/members/list
    return request.get('/members/list')
  },
  
  // 创建会员 - 调用SpringBoot的create方法
  create: (data: Omit<Member, 'memberId'>) => {
    console.log('➕ 调用SpringBoot创建会员API:', data)
    
    // 确保数据格式符合SpringBoot期望
    const memberData = {
      memberName: data.memberName,
      phone: data.phone,
      memberLevel: data.memberLevel || '普通会员',
      points: data.points || 0,
      totalConsumption: data.totalConsumption || 0.0,
      status: data.status || 'active'
    }
    
    // 发送到SpringBoot的 POST /api/members
    return request.post('/members', memberData)
  },
  
  // 更新会员 - 调用SpringBoot的update方法
  update: (id: number, data: Member) => {
    console.log('📝 调用SpringBoot更新会员API:', id, data)
    
    // 发送到SpringBoot的 PUT /api/members/{id}
    return request.put(`/members/${id}`, data)
  },
  
  // 删除会员 - 调用SpringBoot的delete方法
  delete: (id: number) => {
    console.log('🗑️ 调用SpringBoot删除会员API:', id)
    
    // 发送到SpringBoot的 DELETE /api/members/{id}
    return request.delete(`/members/${id}`)
  },
  
  // 根据ID获取会员 - 调用SpringBoot的getById方法
  getById: (id: number) => {
    console.log('🔍 调用SpringBoot会员详情API:', id)
    
    // 发送到SpringBoot的 GET /api/members/{id}
    return request.get(`/members/${id}`)
  },
  
  // 更新会员积分 - 调用SpringBoot的updatePoints方法
  updatePoints: (id: number, points: number) => {
    console.log('💎 调用SpringBoot更新会员积分API:', id, points)
    
    // 发送到SpringBoot的 PUT /api/members/{id}/points
    return request.put(`/members/${id}/points`, { points })
  },
  
  // 会员消费记录 - 调用SpringBoot的getConsumptionHistory方法
  getConsumptionHistory: (id: number) => {
    console.log('📊 调用SpringBoot会员消费记录API:', id)
    
    // 发送到SpringBoot的 GET /api/members/{id}/consumption
    return request.get(`/members/${id}/consumption`)
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
  }
}


