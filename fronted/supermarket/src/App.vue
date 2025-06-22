<template>
  <div id="app">
    <!-- 登录页面 -->
    <router-view v-if="$route.path === '/login'" />
    
    <!-- 主应用界面 -->
    <div v-else class="layout">
      <!-- 侧边栏 -->
      <div v-if="isLoggedIn && currentUser" class="sidebar">
        <div class="logo">
          <h2>🏪 超市管理</h2>
          <div class="user-role">
            <el-tag :type="getRoleType(currentUser?.role)">
              {{ getRoleText(currentUser?.role) }}
            </el-tag>
          </div>
        </div>
        <nav class="nav-menu">
          <router-link 
            v-if="hasPermission('dashboard')" 
            to="/dashboard" 
            class="nav-item" 
            active-class="active"
          >
            <span class="nav-icon">📊</span>
            <span class="nav-text">仪表盘</span>
          </router-link>
          
          <router-link 
            v-if="hasPermission('cashier')" 
            to="/cashier" 
            class="nav-item" 
            active-class="active"
          >
            <span class="nav-icon">🛒</span>
            <span class="nav-text">收银台</span>
          </router-link>
          
          <router-link 
            v-if="hasPermission('products')" 
            to="/products" 
            class="nav-item" 
            active-class="active"
          >
            <span class="nav-icon">📦</span>
            <span class="nav-text">商品管理</span>
          </router-link>
          
          <router-link 
            v-if="hasPermission('members')" 
            to="/members" 
            class="nav-item" 
            active-class="active"
          >
            <span class="nav-icon">👤</span>
            <span class="nav-text">会员管理</span>
          </router-link>
          
          <router-link 
            v-if="hasPermission('reports')" 
            to="/reports" 
            class="nav-item" 
            active-class="active"
          >
            <span class="nav-icon">📈</span>
            <span class="nav-text">销售报表</span>
          </router-link>
          
          <router-link 
            v-if="hasPermission('users')" 
            to="/users" 
            class="nav-item" 
            active-class="active"
          >
            <span class="nav-icon">👥</span>
            <span class="nav-text">用户管理</span>
          </router-link>
        </nav>
      </div>

      <!-- 主要内容区域 -->
      <div v-if="isLoggedIn && currentUser" class="main-content">
        <!-- 顶部栏 -->
        <header class="header">
          <div class="header-left">
            <h1 class="page-title">{{ getPageTitle() }}</h1>
          </div>
          <div class="header-right">
            <span class="user-info">{{ currentUser?.name }}</span>
            <el-button type="primary" size="small" @click="logout">退出登录</el-button>
          </div>
        </header>

        <!-- 页面内容 -->
        <main class="content">
          <router-view />
        </main>
      </div>

      <!-- 加载状态 -->
      <div v-else class="loading-container">
        <div class="loading-content">
          <h2>🏪 超市管理系统</h2>
          <p>正在检查登录状态...</p>
          <div class="loading-spinner"></div>
          <el-button type="primary" @click="goToLogin" style="margin-top: 20px;">
            前往登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElButton, ElTag, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 用户登录状态
const isLoggedIn = ref(false)
const currentUser = ref<{
  username: string
  name: string
  role: string
  permissions: string[]
} | null>(null)

// 检查登录状态
const checkLoginStatus = () => {
  const loginStatus = localStorage.getItem('isLoggedIn')
  const userInfoStr = localStorage.getItem('userInfo')
  
  if (loginStatus === 'true' && userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      if (userInfo && userInfo.username && userInfo.role) {
        currentUser.value = userInfo
        isLoggedIn.value = true
        console.log('✅ 用户已登录:', userInfo)
        return true
      }
    } catch (error) {
      console.error('❌ 用户信息解析失败:', error)
    }
  }
  
  console.log('❌ 用户未登录或信息无效')
  isLoggedIn.value = false
  currentUser.value = null
  return false
}

// 权限检查
const hasPermission = (permission: string) => {
  return currentUser.value?.permissions?.includes(permission) || false
}

// 获取页面标题
const getPageTitle = () => {
  const titles: Record<string, string> = {
    '/': '仪表盘',
    '/login': '用户登录',
    '/dashboard': '仪表盘',
    '/cashier': '收银台',
    '/products': '商品管理',
    '/members': '会员管理',
    '/reports': '销售报表',
    '/users': '用户管理'
  }
  return titles[route.path] || '超市管理系统'
}

// 获取角色标签类型
const getRoleType = (role?: string) => {
  const types: Record<string, string> = {
    'admin': 'danger',
    'manager': 'warning',
    'cashier': 'success'
  }
  return types[role || ''] || 'info'
}

// 获取角色文本
const getRoleText = (role?: string) => {
  const texts: Record<string, string> = {
    'admin': '系统管理员',
    'manager': '商品管理员',
    'cashier': '收银员'
  }
  return texts[role || ''] || '未知角色'
}

// 退出登录
const logout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userInfo')
  currentUser.value = null
  isLoggedIn.value = false
  router.push('/login')
  ElMessage.success('已安全退出登录')
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 生命周期
onMounted(() => {
  checkLoginStatus()
})

// 监听路由变化
watch(() => route.path, () => {
  if (route.path !== '/login') {
    checkLoginStatus()
  }
}, { immediate: true })
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #f5f7fa;
}

#app {
  height: 100vh;
  overflow: hidden;
}

.layout {
  display: flex;
  height: 100vh;
  transition: all 0.3s ease;
}

.sidebar {
  width: 220px;
  background: #2c3e50;
  color: white;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
}

.logo {
  padding: 25px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.logo h2 {
  font-size: 1.3rem;
  font-weight: 600;
}

.user-role {
  margin-top: 8px;
  text-align: center;
}

.nav-menu {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 25px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border-left-color: #3498db;
}

.nav-item.active {
  background: rgba(52, 152, 219, 0.2);
  color: white;
  border-left-color: #3498db;
}

.nav-icon {
  font-size: 1.2rem;
  margin-right: 12px;
  width: 20px;
}

.nav-text {
  font-size: 0.95rem;
}

.main-content {
  margin-left: 220px;
  width: calc(100vw - 220px);
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: white;
  padding: 0 25px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 1.4rem;
  color: #2c3e50;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  color: #666;
  font-size: 0.9rem;
}

.content {
  flex: 1;
  overflow-y: auto;
  background: #f5f7fa;
}

.loading-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.loading-content {
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.loading-content h2 {
  color: #2c3e50;
  margin-bottom: 15px;
}

.loading-content p {
  color: #666;
  margin-bottom: 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  
  .nav-item {
    padding: 12px 20px;
  }
  
  .nav-text {
    font-size: 0.9rem;
  }
  
  .header {
    padding: 0 15px;
  }
  
  .page-title {
    font-size: 1.2rem;
  }
}
</style>


