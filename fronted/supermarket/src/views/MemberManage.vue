<!-- filepath: c:\Users\gaoyuhang\Desktop\Supermarket Front\fronted\supermarket\src\views\MemberManage.vue -->
<template>
  <div class="member-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增会员
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form inline>
          <el-form-item label="会员姓名">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入会员姓名"
              clearable
            />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input 
              v-model="searchForm.phone" 
              placeholder="请输入手机号"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="memberCode" label="会员编号" width="120" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="totalSpent" label="累计消费" width="100">
          <template #default="{ row }">
            ¥{{ row.totalSpent?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会员姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入会员姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
import { Plus } from '@element-plus/icons-vue'

interface Member {
  memberId?: number
  memberCode?: string
  name: string
  phone: string
  email?: string
  points?: number
  totalSpent?: number
  status?: string
  createdAt?: string
}

// 数据
const loading = ref(false)
const tableData = ref<Member[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  name: '',
  phone: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive<Member>({
  name: '',
  phone: '',
  email: ''
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入会员姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 方法
const loadData = () => {
  loading.value = true
  // 模拟数据加载
  setTimeout(() => {
    tableData.value = [
      {
        memberId: 1,
        memberCode: 'M001',
        name: '张三',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        points: 1250,
        totalSpent: 3500.50,
        status: 'active',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        memberId: 2,
        memberCode: 'M002',
        name: '李四',
        phone: '13800138002',
        email: 'lisi@example.com',
        points: 890,
        totalSpent: 2100.80,
        status: 'active',
        createdAt: '2024-02-20 14:15:00'
      }
    ]
    pagination.total = tableData.value.length
    loading.value = false
  }, 500)
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.phone = ''
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增会员'
  isEdit.value = false
  Object.assign(form, {
    name: '',
    phone: '',
    email: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: Member) => {
  dialogTitle.value = '编辑会员'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate()
  
  // 模拟提交
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row: Member) => {
  try {
    await ElMessageBox.confirm('确定要删除这个会员吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消删除
  }
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.member-manage {
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

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>