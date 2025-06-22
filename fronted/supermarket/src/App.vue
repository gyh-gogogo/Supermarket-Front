<template>
  <div id="app">
    <div class="layout">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="logo">
          <h2>🏪 超市管理</h2>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item" active-class="active">
            <span class="nav-icon">📊</span>
            <span class="nav-text">仪表盘</span>
          </router-link>
          <router-link to="/cashier" class="nav-item" active-class="active">
            <span class="nav-icon">🛒</span>
            <span class="nav-text">收银台</span>
          </router-link>
          <router-link to="/products" class="nav-item" active-class="active">
            <span class="nav-icon">📦</span>
            <span class="nav-text">商品管理</span>
          </router-link>
          <router-link to="/members" class="nav-item" active-class="active">
            <span class="nav-icon">👤</span>
            <span class="nav-text">会员管理</span>
          </router-link>
          <router-link to="/reports" class="nav-item" active-class="active">
            <span class="nav-icon">📈</span>
            <span class="nav-text">销售报表</span>
          </router-link>
        </nav>
      </div>

      <!-- 主要内容区域 -->
      <div class="main-content">
        <!-- 顶部栏 -->
        <header class="header">
          <div class="header-left">
            <h1 class="page-title">{{ getPageTitle() }}</h1>
          </div>
          <div class="header-right">
            <span class="user-info">管理员</span>
            <el-button type="primary" size="small" @click="logout">退出</el-button>
          </div>
        </header>

        <!-- 页面内容 -->
        <main class="content">
          <router-view />
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElButton, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const getPageTitle = () => {
  const titles: Record<string, string> = {
    '/': '仪表盘',
    '/dashboard': '仪表盘',
    '/cashier': '收银台',
    '/products': '商品管理',
    '/members': '会员管理',
    '/reports': '销售报表'
  }
  return titles[route.path] || '超市管理系统'
}

const logout = () => {
  ElMessage.success('已退出登录')
  // 这里可以添加实际的登出逻辑
}
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
}

.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
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
  flex: 1;
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
