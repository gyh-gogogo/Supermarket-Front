<template>
  <div class="members">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>👥 会员管理</span>
          <el-button type="primary" @click="showAddDialog">
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
        <el-table-column prop="memberName" label="姓名" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="memberLevel" label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.memberLevel)">
              {{ row.memberLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="totalConsumption" label="累计消费" width="100">
          <template #default="{ row }">
            ¥{{ row.totalConsumption?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
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
        <el-form-item label="会员姓名" prop="memberName">
          <el-input v-model="form.memberName" placeholder="请输入会员姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="form.memberLevel" placeholder="请选择会员等级" style="width: 100%">
            <el-option label="普通会员" value="普通会员" />
            <el-option label="银卡会员" value="银卡会员" />
            <el-option label="金卡会员" value="金卡会员" />
            <el-option label="钻石会员" value="钻石会员" />
          </el-select>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Member {
  memberId?: number
  memberCode?: string
  memberName: string
  phone: string
  memberLevel: string
  points?: number
  totalConsumption?: number
}

const loading = ref(false)
const tableData = ref<Member[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({
  memberName: '',
  phone: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive<Member>({
  memberName: '',
  phone: '',
  memberLevel: '普通会员'
})

const rules = {
  memberName: [{ required: true, message: '请输入会员姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  memberLevel: [{ required: true, message: '请选择会员等级', trigger: 'change' }]
}

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    tableData.value = [
      {
        memberId: 1,
        memberCode: 'M001',
        memberName: '张三',
        phone: '13800138001',
        memberLevel: '银卡会员',
        points: 1250,
        totalConsumption: 3500.50
      },
      {
        memberId: 2,
        memberCode: 'M002',
        memberName: '李四',
        phone: '13800138002',
        memberLevel: '普通会员',
        points: 890,
        totalConsumption: 2100.80
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
  searchForm.memberName = ''
  searchForm.phone = ''
  loadData()
}

const showAddDialog = () => {
  dialogTitle.value = '新增会员'
  isEdit.value = false
  Object.assign(form, {
    memberName: '',
    phone: '',
    memberLevel: '普通会员'
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
    // 用户取消
  }
}

const getLevelType = (level: string) => {
  const types: Record<string, string> = {
    '普通会员': '',
    '银卡会员': 'info',
    '金卡会员': 'warning',
    '钻石会员': 'success'
  }
  return types[level] || ''
}

onMounted(() => {
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
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>