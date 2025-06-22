import request from './request'

export interface Category {
  categoryId?: number
  categoryName: string
  description?: string
  status?: string
  createdAt?: string
  updatedAt?: string
}

export interface CategoryPageParams {
  current: number
  size: number
  categoryName?: string
}

export const categoryApi = {
  // 分页查询分类
  getPage(params: CategoryPageParams) {
    return request.get('/categories/page', { params })
  },

  // 获取所有分类列表
  getList() {
    return request.get('/categories/list')
  },

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
}
