import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
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
  },
  // 404 错误页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 优化路由守卫
router.beforeEach(async (to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const requiresAuth = to.meta.requiresAuth !== false
  
  console.log(`🚦 路由守卫: ${from.path} -> ${to.path}, 登录状态: ${isLoggedIn}`)
  
  try {
    // 如果访问登录页
    if (to.path === '/login') {
      if (isLoggedIn) {
        // 已登录用户访问登录页，根据角色重定向
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const redirectPath = getDefaultRoute(userInfo.role)
        console.log(`✅ 已登录用户重定向到: ${redirectPath}`)
        next(redirectPath)
      } else {
        next()
      }
      return
    }
    
    // 如果需要登录验证但未登录
    if (requiresAuth && !isLoggedIn) {
      console.log('❌ 未登录，重定向到登录页')
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    
    // 权限检查
    if (requiresAuth && to.meta.permissions) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const hasPermission = to.meta.permissions.some(permission => 
        userInfo.permissions?.includes(permission)
      )
      
      if (!hasPermission) {
        console.log('❌ 权限不足，重定向到默认页面')
        const defaultRoute = getDefaultRoute(userInfo.role)
        ElMessage.error('您没有访问该页面的权限')
        next(defaultRoute)
        return
      }
    }
    
    console.log(`✅ 路由验证通过: ${to.path}`)
    next()
  } catch (error) {
    console.error('❌ 路由守卫错误:', error)
    localStorage.clear()
    next('/login')
  }
})

// 根据角色获取默认路由
function getDefaultRoute(role: string): string {
  switch (role) {
    case 'admin':
      return '/dashboard'
    case 'manager':
      return '/products'
    case 'cashier':
      return '/cashier'
    default:
      return '/dashboard'
  }
}

export default router