import request from './request'

export interface Category {
  categoryId?: number
  categoryName: string
  description?: string
  status?: string
  createdAt?: string
}

export const categoryApi = {
  // 分页查询分类
  getPage: (params: any) => request.get('/categories/page', { params }),
  
  // 创建分类
  create: (data: Category) => request.post('/categories', data),
  
  // 更新分类
  update: (id: number, data: Category) => request.put(`/categories/${id}`, data),
  
  // 删除分类
  delete: (id: number) => request.delete(`/categories/${id}`),
  
  // 获取所有分类（用于下拉框）
  getAll: () => request.get('/categories/all')
}
  
  // 创建分类
  create(data: Category) {
    return request.post('/categories', data)
  },

  // 更新分类
  update(id: number, data: Category) {
    return request.put(`/categories/${id}`, data)
  },

  // 删除分类
  delete(id: number) {
    return request.delete(`/categories/${id}`)
  }

