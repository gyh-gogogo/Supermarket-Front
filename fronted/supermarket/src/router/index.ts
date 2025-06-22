import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true, permissions: ['dashboard'] }
  },
  {
    path: '/cashier',
    name: 'Cashier',
    component: () => import('../views/Cashier.vue'),
    meta: { requiresAuth: true, permissions: ['cashier'] }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('../views/Products.vue'),
    meta: { requiresAuth: true, permissions: ['products'] }
  },
  {
    path: '/members',
    name: 'Members',
    component: () => import('../views/Members.vue'),
    meta: { requiresAuth: true, permissions: ['members'] }
  },
  {
    path: '/reports',
    name: 'Reports',
    component: () => import('../views/Reports.vue'),
    meta: { requiresAuth: true, permissions: ['reports'] }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('../views/UserManage.vue'),
    meta: { requiresAuth: true, permissions: ['users'] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const userInfoStr = localStorage.getItem('userInfo')
  
  console.log('🛡️ 路由守卫:', to.path, '登录状态:', isLoggedIn)
  
  // 如果访问登录页面
  if (to.path === '/login') {
    if (isLoggedIn) {
      // 已登录用户访问登录页，重定向到首页
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }
  
  // 检查权限
  if (to.meta.requiresAuth && to.meta.permissions && userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      const userPermissions = userInfo.permissions || []
      const requiredPermissions = to.meta.permissions as string[]
      
      const hasPermission = requiredPermissions.some(permission => 
        userPermissions.includes(permission)
      )
      
      if (!hasPermission) {
        ElMessage.error('您没有访问该页面的权限')
        // 根据角色重定向到合适的页面
        switch (userInfo.role) {
          case 'cashier':
            next('/cashier')
            break
          case 'manager':
            next('/products')
            break
          case 'admin':
            next('/dashboard')
            break
          default:
            next('/login')
        }
        return
      }
    } catch (error) {
      console.error('权限检查失败:', error)
      next('/login')
      return
    }
  }
  
  next()
})

export default router
