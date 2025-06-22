import request from './request'

export interface Purchase {
  purchaseId?: number
  purchaseNumber?: string
  supplierName: string
  totalAmount: number
  operatorId: number
  notes?: string
  purchaseDate?: string
  operatorName?: string
}

export interface PurchasePageParams {
  current: number
  size: number
  purchaseNumber?: string
  supplierName?: string
}

export const purchaseApi = {
  // 分页查询进货记录
  getPage(params: PurchasePageParams) {
    return request.get('/purchases/page', { params })
  },

  // 创建进货记录
  create(data: Purchase) {
    return request.post('/purchases', data)
  },

  // 更新进货记录
  update(id: number, data: Purchase) {
    return request.put(`/purchases/${id}`, data)
  },

  // 删除进货记录
  delete(id: number) {
    return request.delete(`/purchases/${id}`)
  },

  // 根据ID获取进货记录
  getById(id: number) {
    return request.get(`/purchases/${id}`)
  }
}
