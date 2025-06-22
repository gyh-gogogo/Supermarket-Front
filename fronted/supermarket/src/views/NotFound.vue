<template>
  <div class="not-found">
    <div class="error-container">
      <div class="error-icon">🔍</div>
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在或已被移动。</p>
      
      <div class="actions">
        <el-button type="primary" @click="goHome">
          🏠 返回首页
        </el-button>
        <el-button @click="goBack">
          ← 返回上页
        </el-button>
      </div>
      
      <div class="suggestions">
        <h3>您可能想要：</h3>
        <div class="suggestion-links">
          <router-link to="/dashboard" class="suggestion-item">
            <span class="suggestion-icon">📊</span>
            <span>查看仪表盘</span>
          </router-link>
          <router-link to="/cashier" class="suggestion-item" v-if="hasPermission('cashier')">
            <span class="suggestion-icon">🛒</span>
            <span>前往收银台</span>
          </router-link>
          <router-link to="/products" class="suggestion-item" v-if="hasPermission('products')">
            <span class="suggestion-icon">📦</span>
            <span>管理商品</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElButton } from 'element-plus'

const router = useRouter()

// 获取当前用户信息
const currentUser = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
})

// 权限检查
const hasPermission = (permission: string) => {
  return currentUser.value?.permissions?.includes(permission) || false
}

const goHome = () => {
  const role = currentUser.value?.role
  switch (role) {
    case 'admin':
      router.push('/dashboard')
      break
    case 'manager':
      router.push('/products')
      break
    case 'cashier':
      router.push('/cashier')
      break
    default:
      router.push('/dashboard')
  }
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.error-container {
  background: white;
  padding: 50px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  max-width: 500px;
  width: 100%;
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

h1 {
  font-size: 6rem;
  font-weight: bold;
  color: #e74c3c;
  margin: 0 0 10px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 2rem;
  color: #2c3e50;
  margin: 0 0 15px 0;
}

p {
  color: #666;
  font-size: 1.1rem;
  margin-bottom: 30px;
  line-height: 1.6;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 40px;
}

.suggestions {
  border-top: 1px solid #e6e6e6;
  padding-top: 30px;
}

.suggestions h3 {
  color: #2c3e50;
  margin: 0 0 20px 0;
  font-size: 1.2rem;
}

.suggestion-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  text-decoration: none;
  color: #2c3e50;
  transition: all 0.3s ease;
}

.suggestion-item:hover {
  background: #e9ecef;
  transform: translateX(5px);
}

.suggestion-icon {
  font-size: 1.2rem;
}

@media (max-width: 768px) {
  .error-container {
    padding: 30px 20px;
  }
  
  h1 {
    font-size: 4rem;
  }
  
  h2 {
    font-size: 1.5rem;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>
