import request from './request'

export interface Sale {
  saleId?: number
  saleNumber?: string
  saleDate?: string
  totalAmount: number
  discountAmount?: number
  finalAmount: number
  paymentMethod: string
  memberId?: number
  cashierId: number
  status?: string
  createdAt?: string
  updatedAt?: string
}

export interface CheckoutData {
  items: Array<{
    productId: number
    productName: string
    barcode: string
    price: number
    quantity: number
    subtotal: number
  }>
  totalAmount: number
  discountAmount: number  // 统一使用这一个字段表示所有优惠金额
  finalAmount: number
  paymentMethod: string
  memberId?: number
  cashierId: number
}

export const saleApi = {
  // 收银台结算
  checkout: (data: CheckoutData) => {
    console.log('💰 调用收银台结算API:', data)
    return request.post('/api/sales/checkout', data)
  }
}

// 支付数据格式化工具
export const paymentUtils = {
  // 格式化支付数据，确保数据类型正确
  formatCheckoutData: (data: any) => {
    return {
      items: (data.items || []).map((item: any) => ({
        productId: Number(item.productId) || 0,
        productName: String(item.productName || ''),
        barcode: String(item.barcode || ''),
        price: Number(item.price) || 0,
        quantity: Number(item.quantity) || 0,
        subtotal: Number(item.subtotal) || 0
      })),
      totalAmount: Number(data.totalAmount) || 0,
      discountAmount: Number(data.discountAmount) || 0,
      finalAmount: Number(data.finalAmount) || 0,
      paymentMethod: String(data.paymentMethod || 'cash'),
      memberId: data.memberId ? Number(data.memberId) : null,
      cashierId: Number(data.cashierId) || 1
    }
  },

  // 验证支付数据
  validateCheckoutData: (data: any) => {
    const errors: string[] = []

    if (!data.items || data.items.length === 0) {
      errors.push('购物车不能为空')
    }

    if (data.finalAmount <= 0) {
      errors.push('结算金额必须大于0')
    }

    if (!data.paymentMethod) {
      errors.push('请选择支付方式')
    }

    if (!data.cashierId) {
      errors.push('收银员信息缺失')
    }

    // 验证商品明细
    data.items?.forEach((item: any, index: number) => {
      if (!item.productId) {
        errors.push(`商品${index + 1}：商品ID缺失`)
      }
      if (!item.productName) {
        errors.push(`商品${index + 1}：商品名称缺失`)
      }
      if (item.price <= 0) {
        errors.push(`商品${index + 1}：价格必须大于0`)
      }
      if (item.quantity <= 0) {
        errors.push(`商品${index + 1}：数量必须大于0`)
      }
    })

    return errors
  },

  // 计算金额
  calculateAmounts: (items: any[], memberLevel?: string) => {
    const totalAmount = items.reduce((sum, item) => 
      sum + (Number(item.price) * Number(item.quantity)), 0)
    
    // 根据会员等级计算折扣
    const discountRate = paymentUtils.getDiscountRate(memberLevel)
    const discountAmount = totalAmount * discountRate
    const finalAmount = totalAmount - discountAmount

    return {
      totalAmount: Number(totalAmount.toFixed(2)),
      discountAmount: Number(discountAmount.toFixed(2)),
      finalAmount: Number(finalAmount.toFixed(2))
    }
  },

  // 获取会员折扣率
  getDiscountRate: (memberLevel?: string) => {
    const discountRates: Record<string, number> = {
      '普通会员': 0.00,
      '银卡会员': 0.05,
      '金卡会员': 0.08,
      '钻石会员': 0.10
    }
    return discountRates[memberLevel || ''] || 0
  },

  // 格式化金额显示
  formatCurrency: (amount: number) => {
    return `¥${Number(amount).toFixed(2)}`
  }
}

