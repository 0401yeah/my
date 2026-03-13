<template>
  <div class="expense-page flex flex-col gap-6 pb-5">
    <div class="search-container">
      <ArtSearchBar
        ref="searchBarRef"
        v-model="searchFormState"
        :items="searchItems"
        :is-expand="false"
        :show-reset-button="true"
        :show-search-button="true"
        label-position="left"
        label-width="90"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>

    <ElCard class="flex-1 art-table-card" shadow="never">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="refreshData"
        layout="refresh,size,fullscreen,columns,settings"
      >
        <template #left>
          <ElSpace size="small">
            <ElButton type="primary" @click="handleAdd" v-ripple>
              <ElIcon><Plus /></ElIcon> 新增支出
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="data"
        :columns="columns"
        @selection-change="(val: any[]) => selectedRows = val"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #category="{ row }">
          <ElTag :type="getCategoryTagType(row.category)" size="small">
            {{ row.category }}
          </ElTag>
        </template>

        <template #amount="{ row }">
          <span class="text-red-600 font-medium">¥{{ row.amount?.toFixed(2) }}</span>
        </template>

        <template #status="{ row }">
          <div class="flex items-center space-x-1">
            <ElTag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </ElTag>
            <el-button v-if="row.status === 0" type="primary" size="small" @click.stop="handleApprove(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" type="danger" size="small" @click.stop="handleApprove(row, 2)">拒绝</el-button>
          </div>
        </template>

        <template #operation="{ row }">
          <div class="flex space-x-1">
            <ArtButtonTable type="edit" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <ElDialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="520px" 
      center
      :close-on-click-modal="false"
    >
      <ElForm 
        :model="formData" 
        :rules="rules" 
        ref="formRef" 
        label-position="top"
      >
        <ElRow :gutter="20">
          <ElCol :span="24" v-if="dialogType !== 'add'">
            <ElFormItem label="支出单号">
              <ElInput v-model="formData.expenseNo" disabled />
            </ElFormItem>
          </ElCol>
          <ElCol :span="24">
            <ElFormItem label="支出类别" prop="category">
              <ElSelect v-model="formData.category" placeholder="请选择支出类别" class="w-full">
                <ElOption label="设备维护费" value="设备维护费" />
                <ElOption label="电费" value="电费" />
                <ElOption label="人工成本" value="人工成本" />
                <ElOption label="退款金额" value="退款金额" />
                <ElOption label="其他支出" value="其他支出" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="支出金额" prop="amount">
              <ElInputNumber 
                v-model="formData.amount" 
                :precision="2" 
                :min="0" 
                class="w-full"
                placeholder="请输入金额"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="关联停车场">
              <ElSelect 
                v-model="formData.parkingLotId" 
                placeholder="请选择停车场" 
                clearable 
                class="w-full"
              >
                <ElOption
                  v-for="lot in parkingLots"
                  :key="lot.id"
                  :label="lot.name"
                  :value="lot.id"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="24">
            <ElFormItem label="支出说明">
              <ElInput
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入支出说明"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="24" v-if="dialogType !== 'add'">
            <ElFormItem label="状态">
              <ElTag :type="getStatusTagType(formData.status)">
                {{ getStatusText(formData.status) }}
              </ElTag>
            </ElFormItem>
          </ElCol>
        </ElRow>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit" :loading="submitLoading">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import { expenseApi } from '@/api/expense'
import { formatTime } from '@/utils/time'

defineOptions({ name: 'FinanceExpensePage' })

const searchFormState = ref({ category: '', status: '' })
const searchItems = computed(() => [
  {
    key: 'category',
    label: '支出类别',
    type: 'select',
    props: { placeholder: '全部类别', style: { width: '150px' } },
    options: [
      { label: '全部', value: '' },
      { label: '设备维护费', value: '设备维护费' },
      { label: '电费', value: '电费' },
      { label: '人工成本', value: '人工成本' },
      { label: '退款金额', value: '退款金额' },
      { label: '其他支出', value: '其他支出' }
    ]
  },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    labelWidth: 42,
    options: [
      { label: '全部', value: '' },
      { label: '待审核', value: '0' },
      { label: '已通过', value: '1' },
      { label: '已拒绝', value: '2' }
    ]
  }
])

const {
  data, loading, pagination, columns, columnChecks,
  handleSizeChange, handleCurrentChange, getData, refreshData, searchParams
} = useTable({
  core: {
    apiFn: expenseApi.getList,
    columnsFactory: () => [
      { type: 'selection', width: 50 },
      { type: 'globalIndex', label: '序号', width: 60 },
      { prop: 'expenseNo', label: '支出单号', minWidth: 200 },
      { prop: 'category', label: '支出类别', minWidth: 120, useSlot: true },
      { prop: 'amount', label: '金额(元)', minWidth: 100, useSlot: true },
      { prop: 'parkingLotName', label: '关联停车场', minWidth: 180, formatter: (row: any) => row.parkingLotName || '-' },
      { prop: 'description', label: '支出说明', minWidth: 200, showOverflowTooltip: true },
      { prop: 'status', label: '状态', minWidth: 200, useSlot: true },
      { 
        prop: 'gmtCreate', 
        label: '创建时间', 
        width: 170,
        formatter: (row: any) => formatTime(row.gmtCreate)
      },
      { prop: 'operation', label: '操作', width: 200, useSlot: true, fixed: 'right' }
    ]
  }
})

const handleSearch = () => {
  Object.assign(searchParams, searchFormState.value)
  getData()
}

const handleReset = () => {
  searchFormState.value = { category: '', status: '' }
  handleSearch()
}

const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref()
const submitLoading = ref(false)
const selectedRows = ref<any[]>([])
const parkingLots = ref<any[]>([])

const formData = ref<any>({
  id: undefined,
  expenseNo: '',
  category: '',
  amount: 0,
  parkingLotId: null,
  description: '',
  status: 0
})

const dialogTitle = computed(() => {
  const titles = { add: '新增支出', edit: '编辑支出' }
  return titles[dialogType.value]
})

const rules = {
  category: [{ required: true, message: '请选择支出类别', trigger: 'change' }],
  amount: [{ required: true, message: '请输入支出金额', trigger: 'blur' }]
}

const getCategoryTagType = (category: string) => {
  const types: Record<string, any> = {
    '设备维护费': 'warning',
    '电费': '',
    '人工成本': 'success',
    '退款金额': 'danger',
    '其他支出': 'info'
  }
  return types[category] || 'info'
}

const getStatusTagType = (status: number) => {
  const types: Record<number, any> = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

const fetchParkingLots = async () => {
  try {
    const res: any = await expenseApi.getParkingLots()
    parkingLots.value = res.records || res || []
  } catch (error) {
    // 获取停车场列表失败
  }
}

const handleAdd = () => {
  dialogType.value = 'add'
  formData.value = {
    id: undefined,
    expenseNo: '',
    category: '',
    amount: 0,
    parkingLotId: null,
    description: '',
    status: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  formData.value = { ...row }
  dialogVisible.value = true
}



const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (dialogType.value === 'add') {
      await expenseApi.add(formData.value)
      ElMessage.success('新增成功')
    } else {
      await expenseApi.update(formData.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    refreshData()
  } catch (error) {
    // 提交失败
  } finally {
    submitLoading.value = false
  }
}

const handleApprove = async (row: any, status: number) => {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${action}该支出记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await expenseApi.approve(row.id, status)
    ElMessage.success(`${action}成功`)
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      // 审批失败
    }
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该支出记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await expenseApi.delete(row.id)
    ElMessage.success('删除成功')
    refreshData()
  })
}

onMounted(() => {
  fetchParkingLots()
})
</script>

<style scoped>
.expense-page { 
  padding: 20px; 
  background: #f5f7fa;
  min-height: 100%;
}

.search-container { 
  background: #fff; 
  padding: 18px; 
  border-radius: 8px; 
}

.search-container :deep(.el-form-item) { 
  margin-right: 20px !important; 
}
</style>
