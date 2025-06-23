import request from './request'

export interface Category {
  categoryId?: number
  categoryName: string
  description?: string
  status?: string
  createdAt?: string
  updatedAt?: string
}

export const categoryApi = {
  // 获取所有分类列表
  getList: () => {
    console.log('📤 调用分类列表API')
    return request.get('/api/categories/list')
  },

  // 分页查询分类
  getPage: (params: {
    current?: number
    size?: number
    categoryName?: string
  }) => {
    console.log('📤 调用分类分页API:', params)
    return request.get('/api/categories/page', { params })
  },

  // 创建分类
  create: (data: Omit<Category, 'categoryId'>) => {
    console.log('📤 调用创建分类API:', data)
    return request.post('/api/categories', data)
  },

  // 更新分类
  update: (id: number, data: Category) => {
    console.log('📤 调用更新分类API:', id, data)
    return request.put(`/api/categories/${id}`, data)
  },

  // 删除分类
  delete: (id: number) => {
    console.log('📤 调用删除分类API:', id)
    return request.delete(`/categories/${id}`)
  },

  // 根据ID获取分类
  getById: (id: number) => {
    console.log('📤 调用分类详情API:', id)
    return request.get(`/api/categories/${id}`)
  }
}

