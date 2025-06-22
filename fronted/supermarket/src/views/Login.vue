<template>
  <div class="login-container">
    <div class="login-card" :class="{ 'loading': userStore.loading }">
      <div class="login-header">
        <h1>超市管理系统</h1>
        <p>请登录您的账户</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            size="large"
            prefix-icon="User"
            :disabled="userStore.loading"
            autocomplete="username"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            size="large"
            prefix-icon="Lock"
            show-password
            :disabled="userStore.loading"
            autocomplete="current-password"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="userStore.loading"
            @click="handleLogin"
            class="login-button"
            :disabled="!loginForm.username || !loginForm.password"
          >
            {{ userStore.loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 快速登录区域 -->
      <div class="demo-accounts" v-if="!userStore.loading">
        <h3>🚀 快速登录</h3>
        <div class="account-grid">
          <div class="account-item" @click="quickLogin('admin', '123456')">
            <div class="role">👑 系统管理员</div>
            <div class="credentials">admin / 123456</div>
            <div class="permissions">全部功能权限</div>
          </div>
          <div class="account-item" @click="quickLogin('manager', '123456')">
            <div class="role">📦 商品管理员</div>
            <div class="credentials">manager / 123456</div>
            <div class="permissions">商品管理、进货、报表</div>
          </div>
          <div class="account-item" @click="quickLogin('cashier', '123456')">
            <div class="role">🛒 收银员</div>
            <div class="credentials">cashier / 123456</div>
            <div class="permissions">收银台功能</div>
          </div>
        </div>
      </div>
      
      <!-- 当前用户切换 -->
      <div class="current-user" v-if="userStore.isLoggedIn">
        <div class="user-info">
          <span class="user-name">{{ userStore.userName }}</span>
          <el-tag :type="getRoleTagType(userStore.userRole)" size="small">
            {{ getRoleLabel(userStore.userRole) }}
          </el-tag>
        </div>
        <el-button @click="handleLogout" size="small" type="danger">
          切换账户
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    console.log('🔐 用户登录:', loginForm.username)
    
    const success = await simulateLogin(loginForm.username, loginForm.password)
    
    if (success) {
      // 登录成功，跳转到首页
      await router.push('/dashboard')
    } else {
      ElMessage.error('用户名或密码错误')
    }
  } catch (error) {
    console.error('登录验证失败:', error)
  }
}

// 模拟登录验证
const simulateLogin = async (username: string, password: string): Promise<boolean> => {
  const demoAccounts = [
    { username: 'admin', password: '123456', role: 'admin', name: '系统管理员' },
    { username: 'manager', password: '123456', role: 'manager', name: '商品管理员' },
    { username: 'cashier', password: '123456', role: 'cashier', name: '收银员' }
  ]
  
  const user = demoAccounts.find(u => u.username === username && u.password === password)
  
  if (user) {
    const loginUser = {
      id: Date.now(),
      username: user.username,
      name: user.name,
      role: user.role,
      token: `token_${user.username}_${Date.now()}`
    }
    
    return await userStore.login(loginUser)
  }
  
  return false
}

// 快速登录
const quickLogin = async (username: string, password: string) => {
  loginForm.username = username
  loginForm.password = password
  await handleLogin()
}

// 登出处理
const handleLogout = async () => {
  await userStore.logout()
  loginForm.username = ''
  loginForm.password = ''
}

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  switch (role) {
    case 'admin': return 'danger'
    case 'manager': return 'warning'
    case 'cashier': return 'success'
    default: return 'info'
  }
}

// 获取角色标签
const getRoleLabel = (role: string) => {
  switch (role) {
    case 'admin': return '系统管理员'
    case 'manager': return '商品管理员'
    case 'cashier': return '收银员'
    default: return '未知角色'
  }
}

// 组件挂载时检查登录状态
onMounted(() => {
  if (userStore.isLoggedIn) {
    console.log('✅ 用户已登录，准备跳转')
    router.push('/dashboard')
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 480px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.login-card.loading {
  opacity: 0.8;
  pointer-events: none;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 2.2em;
  color: #333;
  margin: 0 0 10px 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header p {
  color: #666;
  font-size: 1em;
  margin: 0;
}

.login-form {
  margin-bottom: 25px;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.demo-accounts {
  border-top: 1px solid #eee;
  padding-top: 25px;
  margin-bottom: 20px;
}

.demo-accounts h3 {
  text-align: center;
  color: #666;
  margin: 0 0 15px 0;
  font-size: 1em;
}

.account-grid {
  display: grid;
  gap: 10px;
}

.account-item {
  padding: 12px;
  border: 2px solid #f0f0f0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.account-item:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  transform: translateY(-1px);
}

.account-item .role {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  font-size: 0.9em;
}

.account-item .credentials {
  font-size: 0.8em;
  color: #666;
  font-family: 'Courier New', monospace;
  margin-bottom: 3px;
}

.account-item .permissions {
  font-size: 0.75em;
  color: #999;
}

.current-user {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
  border-left: 4px solid #28a745;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-weight: 600;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    padding: 15px;
  }
  
  .login-card {
    padding: 25px 20px;
  }
  
  .login-header h1 {
    font-size: 1.8em;
  }
  
  .account-item {
    padding: 10px;
  }
}

/* 加载动画 */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.login-card.loading {
  animation: pulse 1.5s ease-in-out infinite;
}
</style>
