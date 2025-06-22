import request from './request'

export interface Product {
  productId?: number
  productName: string
  barcode?: string
  price: number
  costPrice?: number
  stockQuantity: number
  minStockLevel?: number
  description?: string
  status?: string
  createdAt?: string
  updatedAt?: string
}

export interface ProductPageParams {
  current: number
  size: number
  productName?: string
}

export const productApi = {
  // 分页查询商品
  getPage(params: ProductPageParams) {
    console.log('🔍 调用商品分页API:', params)
    return request.get('/products/page', { params })
  },

  // 创建商品
  create(data: Product) {
    console.log('➕ 调用创建商品API:', data)
    return request.post('/products', data)
  },

  // 更新商品
  update(id: number, data: Product) {
    console.log('📝 调用更新商品API:', id, data)
    return request.put(`/products/${id}`, data)
  },

  // 删除商品
  delete(id: number) {
    console.log('🗑️ 调用删除商品API:', id)
    return request.delete(`/products/${id}`)
  },

  // 根据ID获取商品
  getById(id: number) {
    console.log('🔍 调用商品详情API:', id)
    return request.get(`/products/${id}`)
  },

  // 收银台专用：获取所有有库存的商品
  getCashierProducts: (params?: { productName?: string; barcode?: string }) => {
    console.log('🛒 调用收银台商品查询API:', params)
    return request.get('/products/cashier', { params })
  },

  // 收银台专用：全文搜索商品
  searchProducts: (keyword: string) => {
    console.log('🔍 调用商品搜索API:', keyword)
    return request.get('/products/search', { params: { keyword } })
  }
}
