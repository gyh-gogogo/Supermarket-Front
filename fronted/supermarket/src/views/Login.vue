<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>🏪 超市管理系统</h1>
        <p>请选择您的角色并登录</p>
      </div>

      <div class="login-form">
        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="rules"
          label-width="0"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
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
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 角色说明 -->
        <div class="role-info">
          <h3>测试账号</h3>
          <div class="role-item">
            <span class="role-tag admin">系统管理员</span>
            <span>admin / admin123</span>
            <small>全部功能权限</small>
          </div>
          <div class="role-item">
            <span class="role-tag manager">商品管理员</span>
            <span>manager / manager123</span>
            <small>商品管理、销售报表</small>
          </div>
          <div class="role-item">
            <span class="role-tag cashier">收银员</span>
            <span>cashier / cashier123</span>
            <small>收银台功能</small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref<InstanceType<typeof ElForm>>()
const loginLoading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

// 模拟用户数据库
const users = [
  {
    username: 'admin',
    password: 'admin123',
    role: 'admin',
    name: '系统管理员',
    permissions: ['dashboard', 'products', 'members', 'users', 'cashier', 'reports']
  },
  {
    username: 'manager',
    password: 'manager123',
    role: 'manager',
    name: '商品管理员',
    permissions: ['dashboard', 'products', 'reports']
  },
  {
    username: 'cashier',
    password: 'cashier123',
    role: 'cashier',
    name: '收银员',
    permissions: ['cashier']
  }
]

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loginLoading.value = true

    console.log('🔐 尝试登录:', loginForm.username)

    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 验证用户信息
    const user = users.find(u => 
      u.username === loginForm.username && 
      u.password === loginForm.password
    )

    if (user) {
      // 登录成功，保存用户信息
      const userInfo = {
        username: user.username,
        name: user.name,
        role: user.role,
        permissions: user.permissions,
        loginTime: new Date().toISOString()
      }

      // 保存到localStorage
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      localStorage.setItem('isLoggedIn', 'true')

      ElMessage.success(`欢迎您，${user.name}！`)

      // 根据角色跳转到不同页面
      switch (user.role) {
        case 'admin':
          router.push('/dashboard') // 系统管理员看到完整的仪表盘
          break
        case 'manager':
          router.push('/products') // 商品管理员直接进入商品管理
          break
        case 'cashier':
          router.push('/cashier') // 收银员直接进入收银台
          break
        default:
          router.push('/dashboard')
      }
    } else {
      ElMessage.error('用户名或密码错误')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请重试')
  } finally {
    loginLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 450px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 2.2rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login-header p {
  color: #666;
  margin: 0;
  font-size: 1rem;
}

.login-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 25px;
}

.el-input {
  border-radius: 12px;
}

.el-input .el-input__wrapper {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.el-input .el-input__wrapper:hover {
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.el-input .el-input__wrapper.is-focus {
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  border-color: #667eea;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.role-info {
  margin-top: 30px;
  padding: 25px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.role-info h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.1rem;
  text-align: center;
}

.role-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 15px;
  padding: 12px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.role-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.role-item:last-child {
  margin-bottom: 0;
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  color: white;
  width: fit-content;
}

.role-tag.admin {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
}

.role-tag.manager {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
}

.role-tag.cashier {
  background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
}

.role-item span:nth-child(2) {
  font-family: 'Courier New', monospace;
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.9rem;
}

.role-item small {
  color: #666;
  font-size: 0.8rem;
}

@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
    margin: 10px;
  }
  
  .login-header h1 {
    font-size: 1.8rem;
  }
  
  .role-info {
    padding: 20px;
  }
}
</style>
