import { defineStore } from 'pinia'
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

export interface User {
  id: number
  username: string
  name: string
  role: string
  token: string
  loginTime?: string
}

export const useUserStore = defineStore('user', () => {
  // 状态
  const currentUser = ref<User | null>(null)
  const isInitialized = ref(false)
  const loading = ref(false)
  
  // 计算属性
  const isLoggedIn = computed(() => !!currentUser.value && !!currentUser.value.token)
  const userRole = computed(() => currentUser.value?.role || '')
  const userName = computed(() => currentUser.value?.name || '')
  const isAdmin = computed(() => currentUser.value?.role === 'admin')
  const isCashier = computed(() => currentUser.value?.role === 'cashier')
  const isManager = computed(() => currentUser.value?.role === 'manager')
  
  // 登录
  const login = async (user: User): Promise<boolean> => {
    try {
      loading.value = true
      
      // 模拟登录延迟
      await new Promise(resolve => setTimeout(resolve, 800))
      
      // 设置登录时间
      user.loginTime = new Date().toISOString()
      
      // 更新用户状态
      currentUser.value = user
      
      // 持久化存储
      localStorage.setItem('supermarket_user', JSON.stringify(user))
      localStorage.setItem('supermarket_login_time', user.loginTime)
      
      console.log('✅ 用户登录成功:', user.name, '- 角色:', user.role)
      
      // 显示欢迎消息
      await nextTick()
      ElMessage({
        message: `欢迎回来，${user.name}！`,
        type: 'success',
        duration: 2000,
        showClose: true
      })
      
      return true
    } catch (error) {
      console.error('❌ 登录失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }
  
  // 登出
  const logout = async (): Promise<void> => {
    try {
      loading.value = true
      
      if (currentUser.value) {
        console.log('👋 用户登出:', currentUser.value.name)
        
        // 显示登出消息
        ElMessage({
          message: `再见，${currentUser.value.name}！`,
          type: 'info',
          duration: 1500,
          showClose: true
        })
      }
      
      // 清空状态
      currentUser.value = null
      
      // 清除持久化数据
      localStorage.removeItem('supermarket_user')
      localStorage.removeItem('supermarket_login_time')
      
      // 延迟确保状态更新
      await new Promise(resolve => setTimeout(resolve, 300))
      
    } catch (error) {
      console.error('❌ 登出失败:', error)
    } finally {
      loading.value = false
    }
  }
  
  // 初始化用户状态（从localStorage恢复）
  const initUser = (): void => {
    try {
      const savedUser = localStorage.getItem('supermarket_user')
      const loginTime = localStorage.getItem('supermarket_login_time')
      
      if (savedUser && loginTime) {
        const user = JSON.parse(savedUser)
        
        // 检查登录是否过期（24小时）
        const loginDate = new Date(loginTime)
        const now = new Date()
        const hoursDiff = (now.getTime() - loginDate.getTime()) / (1000 * 60 * 60)
        
        if (hoursDiff > 24) {
          // 登录已过期
          console.log('⏰ 登录已过期，清除状态')
          localStorage.removeItem('supermarket_user')
          localStorage.removeItem('supermarket_login_time')
        } else {
          // 恢复用户状态
          currentUser.value = user
          console.log('🔄 用户状态恢复:', user.name, '- 剩余时间:', Math.round(24 - hoursDiff), '小时')
        }
      }
    } catch (error) {
      console.error('❌ 用户状态恢复失败:', error)
      // 清理损坏的数据
      localStorage.removeItem('supermarket_user')
      localStorage.removeItem('supermarket_login_time')
    } finally {
      isInitialized.value = true
    }
  }
  
  // 检查权限
  const hasPermission = (requiredRoles: string[]): boolean => {
    if (!isLoggedIn.value) return false
    return requiredRoles.includes(currentUser.value!.role)
  }
  
  // 检查特定功能权限
  const canAccessCashier = computed(() => hasPermission(['admin', 'cashier']))
  const canAccessProducts = computed(() => hasPermission(['admin', 'manager']))
  const canAccessMembers = computed(() => hasPermission(['admin']))
  const canAccessPurchases = computed(() => hasPermission(['admin', 'manager']))
  const canAccessReports = computed(() => isLoggedIn.value) // 所有登录用户
  
  // 强制重新登录
  const forceLogin = (): void => {
    currentUser.value = null
    localStorage.removeItem('supermarket_user')
    localStorage.removeItem('supermarket_login_time')
    ElMessage({
      message: '会话已过期，请重新登录',
      type: 'warning',
      duration: 3000,
      showClose: true
    })
  }
  
  // 切换用户（便于测试不同角色）
  const switchUser = async (username: string, password: string): Promise<boolean> => {
    // 先登出当前用户
    await logout()
    
    // 模拟用户数据
    const users = [
      { id: 1, username: 'admin', password: '123456', name: '系统管理员', role: 'admin' },
      { id: 2, username: 'manager', password: '123456', name: '商品管理员', role: 'manager' },
      { id: 3, username: 'cashier', password: '123456', name: '收银员', role: 'cashier' }
    ]
    
    const user = users.find(u => u.username === username && u.password === password)
    
    if (user) {
      const newUser: User = {
        id: user.id,
        username: user.username,
        name: user.name,
        role: user.role,
        token: `token_${user.username}_${Date.now()}`
      }
      
      return await login(newUser)
    }
    
    return false
  }
  
  return {
    // 状态
    currentUser,
    isInitialized,
    loading,
    
    // 计算属性
    isLoggedIn,
    userRole,
    userName,
    isAdmin,
    isCashier,
    isManager,
    canAccessCashier,
    canAccessProducts,
    canAccessMembers,
    canAccessPurchases,
    canAccessReports,
    
    // 方法
    login,
    logout,
    initUser,
    hasPermission,
    forceLogin,
    switchUser
  }
})
