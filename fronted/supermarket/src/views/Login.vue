<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>🏪 超市管理系统</h1>
        <p>请输入您的账号和密码</p>
      </div>

      <div class="login-form">
        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="rules"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              class="login-btn"
              :loading="loginLoading"
              @click="handleLogin"
              native-type="submit"
            >
              {{ loginLoading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginFormRef = ref()
const loginLoading = ref(false)

// 登录表单
const loginForm = ref({
  username: '',
  password: ''
})

// 验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 用户配置 - 所有用户都有members权限
const validUsers = {
  admin: {
    password: '123456',
    role: 'admin',
    name: '系统管理员',
    permissions: ['dashboard', 'products', 'members', 'users', 'cashier', 'reports'],
    defaultRoute: '/dashboard'
  },
  manager: {
    password: '123456',
    role: 'manager',
    name: '商品管理员',
    permissions: ['dashboard', 'products', 'members', 'reports'],
    defaultRoute: '/products'
  },
  cashier: {
    password: '123456',
    role: 'cashier',
    name: '收银员',
    permissions: ['cashier', 'members'],
    defaultRoute: '/cashier'
  }
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loginLoading.value = true

    const user = validUsers[loginForm.value.username]

    if (user && user.password === loginForm.value.password) {
      // 保存登录状态
      const userInfo = {
        username: loginForm.value.username,
        name: user.name,
        role: user.role,
        permissions: user.permissions
      }

      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('userInfo', JSON.stringify(userInfo))

      console.log('✅ 登录成功:', userInfo)
      ElMessage.success(`登录成功，欢迎 ${user.name}`)

      // 延迟跳转，确保状态保存完成
      setTimeout(() => {
        console.log(`🚀 跳转到: ${user.defaultRoute}`)
        router.push(user.defaultRoute)
      }, 300)
    } else {
      ElMessage.error('用户名或密码错误')
    }
  } catch (error) {
    console.error('❌ 登录失败:', error)
    ElMessage.error('登录失败，请重试')
  } finally {
    setTimeout(() => {
      loginLoading.value = false
    }, 300)
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.login-header p {
  color: #666;
  margin: 0;
  font-size: 0.95rem;
}

.login-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 25px;
}

.el-input {
  border-radius: 8px;
}

.el-input .el-input__wrapper {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
}

.el-input .el-input__wrapper:hover {
  border-color: #c0c4cc;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.el-input .el-input__wrapper.is-focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
    margin: 20px;
    max-width: 350px;
  }
  
  .login-header h1 {
    font-size: 1.6rem;
  }
}
</style>