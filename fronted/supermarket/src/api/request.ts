import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    console.log('📤 发送请求:', config.method?.toUpperCase(), config.url, config.data || config.params)
    return config
  },
  (error) => {
    console.error('❌ 请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    console.log('📥 收到响应:', response.status, response.data)
    return response.data
  },
  (error) => {
    console.error('❌ 响应错误:', error)
    
    let errorMessage = '网络请求失败'
    
    if (error.response) {
      const { status, data } = error.response
      console.error(`❌ HTTP ${status}:`, data)
      
      switch (status) {
        case 400:
          errorMessage = data?.message || '请求参数错误'
          break
        case 401:
          errorMessage = '未授权访问，请重新登录'
          break
        case 403:
          errorMessage = '权限不足'
          break
        case 404:
          errorMessage = 'API接口不存在'
          break
        case 500:
          errorMessage = data?.message || 'SpringBoot服务器内部错误'
          break
        default:
          errorMessage = data?.message || `HTTP错误 ${status}`
      }
    } else if (error.request) {
      console.error('❌ 网络请求失败:', error.request)
      errorMessage = `SpringBoot后端连接失败！请检查：
      1. SpringBoot服务是否启动 (端口8080)
      2. 网络连接是否正常
      3. 防火墙设置是否正确`
    } else {
      errorMessage = error.message || '未知错误'
    }
    
    ElMessage({
      message: errorMessage,
      type: 'error',
      duration: 8000,
      showClose: true,
      dangerouslyUseHTMLString: true
    })
    
    return Promise.reject(error)
  }
)

// 测试SpringBoot连接
export const testSpringBootConnection = async () => {
  try {
    console.log('🔗 测试SpringBoot连接...')
    const response = await request.get('/test/hello')
    console.log('✅ SpringBoot连接成功:', response)
    ElMessage.success('SpringBoot后端连接成功！')
    return true
  } catch (error) {
    console.error('❌ SpringBoot连接失败:', error)
    return false
  }
}

// 工具函数
export const showMessage = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'info') => {
  ElMessage({ message, type, duration: 3000, showClose: true })
}

// 删除消息提示
export const deleteMessage = {
  confirm: (name: string, type: string) => {
    return `
      <div style="text-align: center; padding: 20px;">
        <div style="font-size: 48px; margin-bottom: 20px;">⚠️</div>
        <h3 style="color: #dc2626; margin-bottom: 15px;">危险操作警告</h3>
        <p style="margin-bottom: 10px;">您正在执行<strong style="color: #dc2626;">物理删除</strong>操作</p>
        <p style="margin-bottom: 15px;">确定要永久删除${type}：<strong style="color: #2563eb;">${name}</strong> 吗？</p>
        <div style="background: #fee2e2; padding: 15px; border-radius: 8px; margin-top: 15px;">
          <p style="color: #dc2626; margin: 0; font-weight: bold;">⚠️ 此操作不可撤销，请谨慎操作！</p>
        </div>
      </div>
    `
  },
  
  success: (name: string, type: string) => {
    ElMessage({
      message: `${type}"${name}"已永久删除`,
      type: 'success',
      duration: 3000,
      showClose: true,
      customClass: 'delete-success-message'
    })
  },
  
  error: (message: string) => {
    ElMessage({
      message: `删除失败: ${message}`,
      type: 'error',
      duration: 4000,
      showClose: true,
      customClass: 'delete-error-message'
    })
  }
}

export default request
