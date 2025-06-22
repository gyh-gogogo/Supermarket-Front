<template>
  <div class="members">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>👥 会员管理</span>
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
              v-model="searchForm.memberName" 
              placeholder="请输入会员姓名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input 
              v-model="searchForm.phone" 
              placeholder="请输入手机号"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="会员等级">
            <el-select v-model="searchForm.memberLevel" placeholder="请选择会员等级" clearable>
              <el-option 
                v-for="level in memberLevels" 
                :key="level.value" 
                :label="level.label" 
                :value="level.value" 
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="总会员数" :value="stats.totalMembers" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="活跃会员" :value="stats.activeMembers" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="总积分" :value="stats.totalPoints" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic title="总消费额" :value="stats.totalConsumption" :precision="2" prefix="¥" />
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 表格 -->
      <div class="table-container">
        <el-table :data="tableData" v-loading="loading" style="width: 100%">
          <el-table-column prop="memberCode" label="会员编号" width="120" />
          <el-table-column prop="memberName" label="姓名" min-width="100" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
          <el-table-column prop="memberLevel" label="等级" width="120">
            <template #default="{ row }">
              <el-tag :type="getLevelType(row.memberLevel)">
                {{ getLevelText(row.memberLevel) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="points" label="积分" width="80" align="center" />
          <el-table-column prop="totalConsumption" label="累计消费" width="120" align="right">
            <template #default="{ row }">
              ¥{{ row.totalConsumption?.toFixed(2) || '0.00' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
                {{ row.status === 'active' ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" width="160" />
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" type="warning" @click="handlePoints(row)">
                <el-icon><Star /></el-icon>
                积分
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员姓名" prop="memberName">
              <el-input v-model="form.memberName" placeholder="请输入会员姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员等级" prop="memberLevel">
              <el-select v-model="form.memberLevel" placeholder="请选择会员等级" style="width: 100%">
                <el-option 
                  v-for="level in memberLevels" 
                  :key="level.value" 
                  :label="level.label" 
                  :value="level.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="isEdit">
          <el-col :span="12">
            <el-form-item label="当前积分">
              <el-input-number 
                v-model="form.points" 
                :min="0" 
                style="width: 100%"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="正常" value="active" />
                <el-option label="停用" value="inactive" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确认
        </el-button>
      </template>
    </el-dialog>

    <!-- 会员详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="会员详情" width="800px">
      <div v-if="selectedMember">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="会员编号">{{ selectedMember.memberCode }}</el-descriptions-item>
          <el-descriptions-item label="会员姓名">{{ selectedMember.memberName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedMember.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedMember.email || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="会员等级">
            <el-tag :type="getLevelType(selectedMember.memberLevel)">
              {{ getLevelText(selectedMember.memberLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前积分">{{ selectedMember.points }}</el-descriptions-item>
          <el-descriptions-item label="累计消费">¥{{ selectedMember.totalConsumption?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ selectedMember.createdAt }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 积分操作对话框 -->
    <el-dialog v-model="pointsDialogVisible" title="积分操作" width="400px">
      <el-form ref="pointsFormRef" :model="pointsForm" :rules="pointsRules" label-width="100px">
        <el-form-item label="会员姓名">
          <el-input :value="selectedMember?.memberName" disabled />
        </el-form-item>
        <el-form-item label="当前积分">
          <el-input :value="selectedMember?.points" disabled />
        </el-form-item>
        <el-form-item label="操作类型" prop="operation">
          <el-radio-group v-model="pointsForm.operation">
            <el-radio label="add">增加积分</el-radio>
            <el-radio label="subtract">扣减积分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量" prop="points">
          <el-input-number 
            v-model="pointsForm.points" 
            :min="1" 
            :max="pointsForm.operation === 'subtract' ? selectedMember?.points : 999999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="操作说明">
          <el-input 
            v-model="pointsForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入操作说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePointsSubmit" :loading="submitLoading">
          确认操作
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, View, Edit, Star, Delete } from '@element-plus/icons-vue'
import { memberApi, type Member } from '../api/member'
import { deleteMessage } from '../api/request'

// 数据状态
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Member[]>([])
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const pointsDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const pointsFormRef = ref()
const selectedMember = ref<Member | null>(null)

// 搜索表单
const searchForm = reactive({
  memberName: '',
  phone: '',
  memberLevel: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 统计信息
const stats = reactive({
  totalMembers: 0,
  activeMembers: 0,
  totalPoints: 0,
  totalConsumption: 0
})

// 会员等级选项
const memberLevels = ref([
  { value: 'bronze', label: '普通会员' },
  { value: 'silver', label: '银卡会员' },
  { value: 'gold', label: '金卡会员' },
  { value: 'diamond', label: '钻石会员' }
])

// 表单数据
const form = reactive<Member>({
  memberName: '',
  phone: '',
  email: '',
  memberLevel: 'bronze',
  points: 0,
  status: 'active'
})

// 积分操作表单
const pointsForm = reactive({
  operation: 'add',
  points: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  memberName: [
    { required: true, message: '请输入会员姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  memberLevel: [
    { required: true, message: '请选择会员等级', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 积分操作验证规则
const pointsRules = {
  operation: [
    { required: true, message: '请选择操作类型', trigger: 'change' }
  ],
  points: [
    { required: true, message: '请输入积分数量', trigger: 'blur' }
  ]
}

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      memberName: searchForm.memberName || undefined,
      phone: searchForm.phone || undefined,
      memberLevel: searchForm.memberLevel || undefined
    }
    
    console.log('📤 发送会员查询请求:', params)
    const response = await memberApi.getPage(params)
    console.log('📥 收到会员查询响应:', response)
    
    if (response && response.success) {
      const data = response.data
      tableData.value = data?.records || []
      pagination.total = data?.total || 0
      
      // 更新统计信息
      stats.totalMembers = data?.total || 0
      stats.activeMembers = tableData.value.filter(m => m.status === 'active').length
      stats.totalPoints = tableData.value.reduce((sum, m) => sum + (m.points || 0), 0)
      stats.totalConsumption = tableData.value.reduce((sum, m) => sum + (m.totalConsumption || 0), 0)
      
      console.log(`✅ 会员数据加载成功: ${tableData.value.length} 条记录`)
      ElMessage.success(`加载了 ${tableData.value.length} 条会员记录`)
    } else {
      throw new Error(response?.message || '后端返回失败状态')
    }
  } catch (error: any) {
    console.error('❌ 加载会员数据失败:', error)
    ElMessage.warning('无法连接到后端服务，使用模拟数据')
    
    // 使用模拟数据
    tableData.value = [
      {
        memberId: 1,
        memberCode: 'M001',
        memberName: '张三',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        memberLevel: 'gold',
        points: 1250,
        totalConsumption: 3500.50,
        status: 'active',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        memberId: 2,
        memberCode: 'M002',
        memberName: '李四',
        phone: '13800138002',
        email: 'lisi@example.com',
        memberLevel: 'silver',
        points: 890,
        totalConsumption: 2100.80,
        status: 'active',
        createdAt: '2024-02-20 14:15:00'
      }
    ]
    pagination.total = tableData.value.length
    
    // 更新统计信息
    stats.totalMembers = tableData.value.length
    stats.activeMembers = tableData.value.filter(m => m.status === 'active').length
    stats.totalPoints = tableData.value.reduce((sum, m) => sum + (m.points || 0), 0)
    stats.totalConsumption = tableData.value.reduce((sum, m) => sum + (m.totalConsumption || 0), 0)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.memberName = ''
  searchForm.phone = ''
  searchForm.memberLevel = ''
  pagination.current = 1
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增会员'
  isEdit.value = false
  Object.assign(form, {
    memberName: '',
    phone: '',
    email: '',
    memberLevel: 'bronze',
    points: 0,
    status: 'active'
  })
  dialogVisible.value = true
}

const handleView = (row: Member) => {
  selectedMember.value = row
  detailDialogVisible.value = true
}

const handleEdit = (row: Member) => {
  dialogTitle.value = '编辑会员'
  isEdit.value = true
  Object.assign(form, row)
  selectedMember.value = row
  dialogVisible.value = true
}

const handlePoints = (row: Member) => {
  selectedMember.value = row
  Object.assign(pointsForm, {
    operation: 'add',
    points: 0,
    remark: ''
  })
  pointsDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    console.log('📤 提交会员数据:', form)
    
    // 移除权限检查，直接提交
    let response: any
    
    if (isEdit.value) {
      response = await memberApi.update(form.memberId!, form)
      console.log('📝 更新响应:', response)
    } else {
      // 创建新会员时，确保删除ID字段
      const newMember = { ...form }
      delete newMember.memberId
      
      console.log('➕ 创建会员数据:', newMember)
      response = await memberApi.create(newMember)
      console.log('➕ 创建响应:', response)
    }
    
    if (response && response.success) {
      ElMessage.success(isEdit.value ? '会员更新成功' : '会员创建成功')
      dialogVisible.value = false
      loadData()
    } else {
      throw new Error(response?.message || '操作失败')
    }
  } catch (error: any) {
    console.error('❌ 提交失败:', error)
    
    // 简化错误处理
    if (error.message?.includes('手机号')) {
      ElMessage.error('手机号已存在，请使用其他手机号')
    } else {
      ElMessage.error(`操作失败: ${error.message || '网络连接错误'}`)
    }
  } finally {
    submitLoading.value = false
  }
}

const handlePointsSubmit = async () => {
  if (!pointsFormRef.value) return
  
  try {
    await pointsFormRef.value.validate()
    submitLoading.value = true
    
    console.log('📤 提交积分操作:', pointsForm)
    const response = await memberApi.updatePoints(
      selectedMember.value!.memberId!,
      pointsForm.points,
      pointsForm.operation as 'add' | 'subtract'
    )
    
    if (response && response.success) {
      ElMessage.success('积分操作成功')
      pointsDialogVisible.value = false
      loadData()
    } else {
      throw new Error(response?.message || '积分操作失败')
    }
  } catch (error: any) {
    console.error('❌ 积分操作失败:', error)
    
    // 改进错误处理，提供更详细的错误信息
    let errorMessage = '积分操作失败'
    
    if (error.response) {
      // HTTP 错误响应
      const status = error.response.status
      const statusText = error.response.statusText || '未知错误'
      
      if (status === 404) {
        errorMessage = '积分操作接口不存在，请检查后端服务'
      } else if (status === 500) {
        errorMessage = '服务器内部错误，请稍后重试'
      } else if (status === 403) {
        errorMessage = '权限不足，无法执行积分操作'
      } else {
        errorMessage = `请求失败 (${status}): ${statusText}`
      }
    } else if (error.message) {
      errorMessage = error.message
    }
    
    ElMessage.error(errorMessage)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: Member) => {
  try {
    await ElMessageBox.confirm(
      deleteMessage.confirm(row.memberName, '会员'),
      '⚠️ 危险操作：删除会员确认',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger',
        customClass: 'delete-confirm-dialog',
        dangerouslyUseHTMLString: true
      }
    )
    
    console.log('💥 删除会员:', row.memberId, row.memberName)
    const response = await memberApi.delete(row.memberId!)
    
    if (response && response.success) {
      deleteMessage.success(row.memberName, '会员')
      loadData()
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('❌ 删除失败:', error)
      deleteMessage.error(error.message || '网络错误')
    }
  }
}

const getLevelType = (level: string) => {
  const types: Record<string, string> = {
    'bronze': '',
    'silver': 'info',
    'gold': 'warning',
    'diamond': 'success'
  }
  return types[level] || ''
}

const getLevelText = (level: string) => {
  const texts: Record<string, string> = {
    'bronze': '普通会员',
    'silver': '银卡会员',
    'gold': '金卡会员',
    'diamond': '钻石会员'
  }
  return texts[level] || level
}

const getMemberLevelColor = (level: string) => {
  return getLevelType(level)
}

const getMemberLevelText = (level: string) => {
  return getLevelText(level)
}

const getPaymentMethodText = (method: string) => {
  const texts: Record<string, string> = {
    'cash': '现金',
    'card': '银行卡',
    'alipay': '支付宝',
    'wechat': '微信支付'
  }
  return texts[method] || method
}

// 初始化
onMounted(() => {
  console.log('🎉 会员管理页面已加载')
  loadData()
})
</script>

<style scoped>
.members {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-area {
  margin: 20px 0;
}

.stats-section {
  margin: 20px 0;
}

.stat-card {
  text-align: center;
}

.table-container {
  margin: 20px 0;
}

.pagination {
  text-align: right;
  margin: 20px 0;
}

.delete-confirm-dialog .el-message-box__content {
  font-size: 16px;
  line-height: 1.5;
}

.delete-confirm-dialog .el-message-box__btns {
  text-align: center;
}

.delete-confirm-dialog .el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.delete-confirm-dialog .el-button--danger:hover {
  background-color: #f44336;
  border-color: #f44336;
}
</style>