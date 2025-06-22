import { ElMessage } from 'element-plus'

export interface MessageOptions {
  message: string
  type?: 'success' | 'warning' | 'error' | 'info'
  duration?: number
  showClose?: boolean
  customClass?: string
}

// 购物车相关消息
export const cartMessage = {
  add: (productName: string) => {
    ElMessage({
      message: `${productName} 已添加到购物车`,
      type: 'success',
      duration: 2500,
      showClose: true,
      customClass: 'cart-add-message'
    })
  },
  
  remove: (productName: string) => {
    ElMessage({
      message: `${productName} 已从购物车移除`,
      type: 'warning',
      duration: 2500,
      showClose: true,
      customClass: 'cart-remove-message'
    })
  },
  
  clear: () => {
    ElMessage({
      message: '购物车已清空',
      type: 'info',
      duration: 2500,
      showClose: true,
      customClass: 'cart-remove-message'
    })
  },
  
  insufficient: (productName: string) => {
    ElMessage({
      message: `${productName} 库存不足`,
      type: 'error',
      duration: 3000,
      showClose: true,
      customClass: 'cart-remove-message'
    })
  }
}

// 通用消息提示
export const showMessage = (options: MessageOptions) => {
  ElMessage({
    message: options.message,
    type: options.type || 'info',
    duration: options.duration || 3000,
    showClose: options.showClose !== false,
    customClass: options.customClass || 'custom-message'
  })
}

// 操作成功消息
export const successMessage = (message: string) => {
  showMessage({
    message,
    type: 'success',
    customClass: 'custom-message'
  })
}

// 操作失败消息
export const errorMessage = (message: string) => {
  showMessage({
    message,
    type: 'error',
    customClass: 'custom-message'
  })
}

// 警告消息
export const warningMessage = (message: string) => {
  showMessage({
    message,
    type: 'warning',
    customClass: 'custom-message'
  })
}
