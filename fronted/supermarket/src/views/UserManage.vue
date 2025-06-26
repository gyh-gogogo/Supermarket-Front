<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\UserManage.vue -->
<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>👥 用户管理</span>
          
        </div>
      </template>

      

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="name" label="姓名" />
        
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissions" label="权限" min-width="200">
          <template #default="{ row }">
            <el-tag 
              v-for="perm in row.permissions" 
              :key="perm" 
              size="small" 
              style="margin-right: 4px;"
            >
              {{ getPermissionText(perm) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            
            <el-button size="small" type="warning" @click="handleResetPassword(row)">重置密码</el-button>
            
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%" @change="updatePermissions">
            <el-option label="系统管理员" value="admin" />
            <el-option label="商品管理员" value="manager" />
            <el-option label="收银员" value="cashier" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限" prop="permissions">
          <el-checkbox-group v-model="form.permissions">
            <el-checkbox label="dashboard">仪表盘</el-checkbox>
            <el-checkbox label="cashier">收银台</el-checkbox>
            <el-checkbox label="products">商品管理</el-checkbox>
            <el-checkbox label="members">会员管理</el-checkbox>
            <el-checkbox label="reports">销售报表</el-checkbox>
            <el-checkbox label="users">用户管理</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface User {
  username: string
  name: string
  role: string
  permissions: string[]
  password?: string
}

// 数据
const loading = ref(false)
const tableData = ref<User[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  username: '',
  role: ''
})

// 表单数据
const form = reactive<User>({
  username: '',
  name: '',
  role: '',
  permissions: [],
  password: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 预设角色权限
const rolePermissions: Record<string, string[]> = {
  admin: ['dashboard', 'products', 'members', 'users', 'cashier', 'reports'],
  manager: ['dashboard', 'products', 'reports'],
  cashier: ['cashier']
}

// 方法
const loadData = () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    tableData.value = [
      {
        username: 'admin',
        name: '系统管理员',
        role: 'admin',
        permissions: ['dashboard', 'products', 'members', 'users', 'cashier', 'reports']
      },
      {
        username: 'manager',
        name: '商品管理员',
        role: 'manager',
        permissions: ['dashboard', 'products', 'reports']
      },
      {
        username: 'cashier',
        name: '收银员',
        role: 'cashier',
        permissions: ['cashier']
      }
    ]
    loading.value = false
  }, 500)
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.role = ''
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  Object.assign(form, {
    username: '',
    name: '',
    role: '',
    permissions: [],
    password: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: User) => {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

const updatePermissions = () => {
  if (form.role && rolePermissions[form.role]) {
    form.permissions = [...rolePermissions[form.role]]
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()
}

const handleResetPassword = async (row: User) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('密码重置成功，新密码为：123456')
  } catch (error) {
    // 用户取消
  }
}

const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消删除
  }
}

const getRoleType = (role: string) => {
  const types: Record<string, string> = {
    admin: 'danger',
    manager: 'warning',
    cashier: 'success'
  }
  return types[role] || 'info'
}

const getRoleText = (role: string) => {
  const texts: Record<string, string> = {
    admin: '系统管理员',
    manager: '商品管理员',
    cashier: '收银员'
  }
  return texts[role] || role
}

const getPermissionText = (permission: string) => {
  const texts: Record<string, string> = {
    dashboard: '仪表盘',
    cashier: '收银台',
    products: '商品管理',
    members: '会员管理',
    reports: '销售报表',
    users: '用户管理'
  }
  return texts[permission] || permission
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin-bottom: 20px;
}
</style>