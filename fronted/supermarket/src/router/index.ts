import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  },
  {
    path: '/products',
    name: 'ProductManage',
    component: () => import('../views/ProductManage.vue')
  },
  {
    path: '/members',
    name: 'MemberManage',
    component: () => import('../views/MemberManage.vue')
  },
  {
    path: '/purchases',
    name: 'PurchaseManage',
    component: () => import('../views/PurchaseManage.vue')
  },
  {
    path: '/sales',
    name: 'SalesReport',
    component: () => import('../views/SalesReport.vue')
  },
  {
    path: '/cashier',
    name: 'Cashier',
    component: () => import('../views/Cashier.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
