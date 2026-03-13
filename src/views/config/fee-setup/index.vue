<template>
  <div class="fee-setup-page flex flex-col gap-6 pb-5">
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
            <ElButton type="primary" @click="handleAddFeeStandard" v-ripple>
              <ElIcon><Plus /></ElIcon> 新增收费标准
            </ElButton>
            <ElButton @click="handleBatchDelete" :disabled="selectedRows.length === 0">
              <ElIcon><Delete /></ElIcon> 批量删除
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="tableData as Record<string, any>[]"
        :columns="columns"
        @selection-change="(val: any[]) => selectedRows = val"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <div class="status-container">
            <ElTag :type="row.status === 1 ? 'success' : 'info'" effect="plain" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </ElTag>
            <ElButton 
              v-if="row.status === 1"
              size="small" 
              @click="handleDisableFeeStandard(row)"
              type="danger"
              style="padding: 0 10px; height: 22px; font-size: 12px;"
            >
              禁用
            </ElButton>
            <ElButton 
              v-else
              size="small" 
              @click="handleEnableFeeStandard(row)"
              type="primary"
              style="padding: 0 10px; height: 22px; font-size: 12px;"
            >
              启用
            </ElButton>
          </div>
        </template>

        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <ElDialog 
      v-model="dialogVisible" 
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      custom-class="fee-dialog"
      :show-close="false"
    >
      <!-- 自定义头部 -->
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="dialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <DollarSign class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">{{ formData.id ? '编辑收费标准' : '新增收费标准' }}</h2>
              <p class="header-subtitle">{{ formData.id ? '修改收费标准信息' : '添加新的收费标准' }}</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 表单内容区 -->
      <div class="form-body">
        <div class="form-grid">
          <div class="form-item full-width">
            <label class="form-label">收费标准名称 <span class="required">*</span></label>
            <el-input 
              v-model="formData.name"
              placeholder="请输入收费标准名称"
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label class="form-label">停车场 <span class="required">*</span></label>
            <el-select 
              v-model="formData.parkingLotId"
              placeholder="请选择停车场"
              class="form-input"
            >
              <el-option 
                v-for="lot in parkingLots" 
                :key="lot.id" 
                :label="lot.name" 
                :value="lot.id" 
              />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">免费时间(分) <span class="required">*</span></label>
            <el-input 
              v-model.number="formData.freeMinutes"
              type="number"
              placeholder="请输入免费时间"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">首时费(元) <span class="required">*</span></label>
            <el-input 
              v-model.number="formData.firstHourFee"
              type="number"
              placeholder="请输入首时费"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">续时费(元/h) <span class="required">*</span></label>
            <el-input 
              v-model.number="formData.afterFirstHourFee"
              type="number"
              placeholder="请输入续时费"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">日封顶(元) <span class="required">*</span></label>
            <el-input 
              v-model.number="formData.dayMaxFee"
              type="number"
              placeholder="请输入日封顶费"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">状态 <span class="required">*</span></label>
            <el-select 
              v-model="formData.status"
              placeholder="请选择状态"
              class="form-input"
            >
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </div>

          <div class="form-item full-width">
            <label class="form-label">描述</label>
            <el-input 
              v-model="formData.description"
              placeholder="请输入描述信息"
              type="textarea"
              :rows="3"
              class="form-input"
            />
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button type="primary" @click="handleSubmit" class="submit-btn">
            {{ formData.id ? '保存修改' : '创建完成' }}
          </el-button>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { X, DollarSign } from 'lucide-vue-next'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import { feeStandardApi, lotApi } from '@/api/business'

defineOptions({ name: 'FeeSetup' })

const searchFormState = ref({ name: '', status: '' })
const parkingLots = ref<any[]>([])
const selectedRows = ref<any[]>([])

const dialogVisible = ref(false)
const dialogType = ref<'add' | 'view' | 'edit'>('add')
const formData = ref<any>({
  id: undefined,
  name: '',
  type: 1,
  price: 0,
  description: '',
  parkingLotId: null,
  freeMinutes: 0,
  firstHourFee: 0,
  afterFirstHourFee: 0,
  dayMaxFee: 0,
  status: 1
})

const fetchParkingLots = async () => {
  try {
    const res: any = await lotApi.fetchLotList({})
    parkingLots.value = res.records || []
  } catch (error) {
    console.error('获取停车场列表失败:', error)
    parkingLots.value = []
  }
}

const getParkingLotName = (parkingLotId: number | string) => {
  if (!parkingLotId) return '未设置'
  const id = typeof parkingLotId === 'string' ? parseInt(parkingLotId) : parkingLotId
  const lot = parkingLots.value.find(item => String(item.id) === String(id))
  return lot ? lot.name : `停车场 ${id}`
}

onMounted(async () => {
  await fetchParkingLots()
  refreshData()
})

const searchItems = computed(() => [
  {
    key: 'name',
    label: '收费标准名称',
    type: 'input',
    props: { placeholder: '搜索名称', style: { width: '200px' } }
  },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    labelWidth: 42,
    options: [
      { label: '全部', value: '' },
      { label: '启用', value: '1' },
      { label: '禁用', value: '0' }
    ]
  }
])

const { 
  data: tableData, loading, pagination, columns, columnChecks,
  handleSizeChange, handleCurrentChange, getData, refreshData, searchParams
} = useTable({
  core: {
    apiFn: async (params: any) => {
      // 确保停车场列表加载完成
      if (parkingLots.value.length === 0) {
        await fetchParkingLots()
      }
      const result: any = await feeStandardApi.fetchFeeStandardList(params)
      return result
    },
    columnsFactory: () => [
      { type: 'selection', width: 55 },
      {
        type: 'globalIndex', 
        label: '序号', 
        width: 65 
      },
      {
          prop: 'parking_lot_id', 
          label: '停车场', 
          minWidth: 180,
          formatter: (row: any) => {
            const lotId = row.parking_lot_id || row.parkingLotId;
            
            if (!lotId) return '未设置';

            if (!parkingLots.value || parkingLots.value.length === 0) {
              return `加载中(ID:${lotId})`;
            }

            const lot = parkingLots.value.find(item => String(item.id) === String(lotId));
            
            return lot ? lot.name : `未知停车场(ID:${lotId})`;
          }
        },
      {
        prop: 'name', 
        label: '收费模板', 
        minWidth: 150
      },
      {
        prop: 'freeMinutes', 
        label: '免费(分)', 
        width: 100 
      },
      {
        prop: 'firstHourFee', 
        label: '首时费(元)', 
        width: 110
      },
      {
        prop: 'afterFirstHourFee', 
        label: '续时费(元/h)', 
        width: 120 
      },
      {
        prop: 'dayMaxFee', 
        label: '日封顶(元)', 
        width: 110
      },
      {
        prop: 'status', 
        label: '状态', 
        useSlot: true,
        width: 180 
      },
      {
        prop: 'operation', 
        label: '操作', 
        width: 150,
        useSlot: true,
        fixed: 'right'
      }
    ]
  }
})

const handleSearch = () => {
  Object.assign(searchParams, searchFormState.value)
  getData()
}

const handleReset = () => {
  searchFormState.value = { name: '', status: '' }
  handleSearch()
}

const handleAddFeeStandard = () => {
  formData.value = {
    name: '',
    type: 1,
    price: 0,
    description: ''
  }
  dialogType.value = 'add'
  dialogVisible.value = true
}

const handleView = (row: any) => {
  formData.value = { ...row }
  dialogType.value = 'view'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  formData.value = { ...row }
  dialogType.value = 'edit'
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定删除该收费标准吗？', '提示', { type: 'warning' }).then(async () => {
    await feeStandardApi.deleteFeeStandard(row.id)
    ElMessage.success('删除成功')
    refreshData()
  })
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return
  ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 项吗？`, '提示', { type: 'warning' }).then(async () => {
    for (const row of selectedRows.value) {
      await feeStandardApi.deleteFeeStandard(row.id)
    }
    ElMessage.success('批量删除成功')
    refreshData()
    selectedRows.value = []
  })
}

const handleDisableFeeStandard = (row: any) => {
  ElMessageBox.confirm('确定禁用该收费标准吗？', '提示').then(async () => {
    await feeStandardApi.updateFeeStandard({ ...row, status: 0 })
    ElMessage.success('禁用成功')
    refreshData()
  })
}

const handleEnableFeeStandard = (row: any) => {
  ElMessageBox.confirm('确定启用该收费标准吗？', '提示').then(async () => {
    await feeStandardApi.updateFeeStandard({ ...row, status: 1 })
    ElMessage.success('启用成功')
    refreshData()
  })
}

const handleSubmit = async () => {
  try {
    const api = formData.value.id ? feeStandardApi.updateFeeStandard : feeStandardApi.addFeeStandard
    await api(formData.value)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.fee-setup-page { 
  padding: 20px; 
}
.search-container { 
  background: #fff; 
  padding: 18px; 
  border-radius: 8px; 
}

/* 关键：确保表格 100% 宽度 */
:deep(.el-table) {
  width: 100% !important;
}

/* 标题稍微对齐优化：表头与内容间距一致 */
:deep(.el-table .cell) {
  white-space: nowrap;
  padding: 0 12px !important;
  text-align: center; /* 默认居中，方便数值列 */
}

/* 针对长文本列（停车场和模板名）强制靠左对齐，增强阅读性 */
:deep(.el-table__header th:nth-child(3) .cell),
:deep(.el-table__row td:nth-child(3) .cell),
:deep(.el-table__header th:nth-child(4) .cell),
:deep(.el-table__row td:nth-child(4) .cell) {
  text-align: left !important;
  padding-left: 20px !important; /* 稍微增加左内边距，显得更自然 */
}

/* 确保状态列内容左对齐 */
:deep(.el-table__header th:nth-child(9) .cell),
:deep(.el-table__row td:nth-child(9) .cell) {
  text-align: left !important;
  padding-left: 20px !important;
}

/* 确保操作列内容左对齐 */
:deep(.el-table__header th:last-child .cell),
:deep(.el-table__row td:last-child .cell) {
  text-align: left !important;
  padding-left: 20px !important;
}

.search-container :deep(.el-form-item) { 
  margin-right: 24px !important; 
}

/* 状态容器样式 */
.status-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 弹窗样式 */
:deep(.fee-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

:deep(.fee-dialog .el-dialog__header) {
  padding: 0;
}

:deep(.fee-dialog .el-dialog__body) {
  padding: 16px 20px;
}

:deep(.fee-dialog .el-dialog__footer) {
  padding: 0 20px 16px;
}

/* 对话框头部 */
.dialog-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  position: relative;
}

.close-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background-color: #f1f5f9;
  color: #64748b;
  padding: 6px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background-color: #e2e8f0;
  color: #475569;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-right: 40px;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.header-subtitle {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

/* 表单内容 */
.form-body {
  padding: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item.full-width {
  grid-column: span 2;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.required {
  color: #ef4444;
  font-size: 13px;
}

.form-input {
  border-radius: 8px;
}

:deep(.form-input .el-input__wrapper) {
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  border-radius: 8px;
  padding: 6px 12px;
}

:deep(.form-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #6366f1 inset;
}

:deep(.form-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #6366f1 inset;
}

:deep(.form-input .el-select__wrapper) {
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  border-radius: 8px;
  padding: 6px 12px;
}

:deep(.form-input .el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #6366f1 inset;
}

:deep(.form-input .el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #6366f1 inset;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  gap: 12px;
}

.cancel-btn {
  flex: 1;
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #f8fafc;
  border-color: #cbd5e1;
}

.submit-btn {
  flex: 1;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  background-color: #6366f1;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.submit-btn:hover {
  background-color: #4f46e5;
}
</style>