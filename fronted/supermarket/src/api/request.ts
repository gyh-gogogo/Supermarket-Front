import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例 - 连接SpringBoot后端
const request = axios.create({
  baseURL: 'http://localhost:8080/api',  // SpringBoot默认端口8080
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    console.log('📤 调用SpringBoot API:', config.method?.toUpperCase(), config.url)
    console.log('📤 请求参数:', config.params || config.data)
    
    // 添加认证token（如果有）
    const token = localStorage.getItem('supermarket_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  error => {
    console.error('❌ 请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理SpringBoot返回的Result格式
request.interceptors.response.use(
  response => {
    console.log('📥 SpringBoot响应:', response.config.url, response.status)
    console.log('📥 响应数据:', response.data)
    
    const data = response.data
    
    // 处理SpringBoot的Result<T>格式
    if (data && typeof data === 'object') {
      // 标准的Result格式：{ success: boolean, data: T, message: string }
      if (data.hasOwnProperty('success')) {
        if (data.success) {
          return data // 成功直接返回
        } else {
          // 业务错误，显示后端返回的错误信息
          const errorMsg = data.message || '操作失败'
          ElMessage.error(errorMsg)
          return Promise.reject(new Error(errorMsg))
        }
      }
      
      // 直接返回数据的情况（比如/test/hello接口）
      return {
        success: true,
        data: data,
        message: '请求成功'
      }
    }
    
    return response.data
  },
  error => {
    console.error('❌ SpringBoot API错误:', error)
    
    let errorMessage = '网络连接失败'
    
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
      errorMessage = `SpringBoot后端连接失败！

🔧 请检查：
1. SpringBoot服务是否启动 (http://localhost:8080)
2. 端口8080是否正确
3. 数据库连接是否正常

💡 解决方案：
- 启动SpringBoot: mvn spring-boot:run
- 检查后端日志输出
- 确认application.yml配置`
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

export default request

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

export const deleteMessage = {
  confirm: (itemName: string, type: string) => {
    return `确定要删除 "${itemName}" 这个${type}吗？删除后无法恢复！`
  },
  
  success: (itemName: string, type: string) => {
    ElMessage({
      message: `${type} "${itemName}" 删除成功`,
      type: 'success',
      duration: 3000,
      showClose: true
    })
  },
  
  error: (message: string) => {
    ElMessage({
      message: `删除失败: ${message}`,
      type: 'error',
      duration: 5000,
      showClose: true
    })
  }
}


