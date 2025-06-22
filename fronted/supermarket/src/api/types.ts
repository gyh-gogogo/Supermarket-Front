// API响应类型定义

export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  code?: number
}

export interface PageData<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

export interface PageResponse<T> extends ApiResponse<PageData<T>> {}

// 通用API错误类型
export interface ApiError {
  message: string
  code?: number
  status?: number
}