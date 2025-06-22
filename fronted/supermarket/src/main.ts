import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

// 使用懒加载导入页面组件，避免循环依赖
const Dashboard = () => import('./views/Dashboard.vue')
const Products = () => import('./views/Products.vue')
const Members = () => import('./views/Members.vue')
const Cashier = () => import('./views/Cashier.vue')
const Reports = () => import('./views/Reports.vue')

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/dashboard',
    name: 'DashboardPage',
    component: Dashboard
  },
  {
    path: '/products',
    name: 'Products',
    component: Products
  },
  {
    path: '/members',
    name: 'Members',
    component: Members
  },
  {
    path: '/cashier',
    name: 'Cashier',
    component: Cashier
  },
  {
    path: '/reports',
    name: 'Reports',
    component: Reports
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

app.mount('#app')

console.log('🏪 超市管理系统前端启动成功！')
console.log('📱 访问地址: http://localhost:3000')
