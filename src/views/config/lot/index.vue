<template>
  <div class="lot-page flex flex-col gap-6 pb-5">
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
            <ElButton type="primary" @click="handleAddLot" v-ripple>
              <ElIcon><Plus /></ElIcon> 新增停车场
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
        :data="data"
        :columns="columns"
        @selection-change="(val: any[]) => selectedRows = val"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #lotInfo="{ row }">
          <div class="lot-info-cell">
            <div class="name-text" :title="row.name">{{ row.name }}</div>
          </div>
        </template>

        <template #status="{ row }">
          <div class="status-container">
            <ElTag :type="row.status === 1 ? 'success' : 'info'" effect="plain" size="small">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </ElTag>
            <ElButton 
              v-if="row.status === 1"
              size="small" 
              @click="handleCloseLot(row)"
              type="danger"
              style="padding: 0 10px; height: 22px; font-size: 12px;"
            >
              关闭
            </ElButton>
            <ElButton 
              v-else
              size="small" 
              @click="handleOpenLot(row)"
              type="primary"
              style="padding: 0 10px; height: 22px; font-size: 12px;"
            >
              开放
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
      custom-class="lot-dialog"
      :show-close="false"
    >
      <!-- 自定义头部 -->
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="dialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <MapPin class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">{{ formData.id ? '编辑停车场' : '新增停车场' }}</h2>
              <p class="header-subtitle">{{ formData.id ? '修改停车场信息' : '添加新的停车场' }}</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 表单内容区 -->
      <div class="form-body">
        <div class="form-grid">
          <div class="form-item full-width">
            <label class="form-label">停车场名称 <span class="required">*</span></label>
            <el-input 
              v-model="formData.name"
              placeholder="请输入停车场名称"
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label class="form-label">总车位数 <span class="required">*</span></label>
            <el-input 
              v-model.number="formData.parkingSpaceNumber"
              type="number"
              placeholder="请输入车位数"
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
              <el-option label="开放" :value="1" />
              <el-option label="关闭" :value="0" />
            </el-select>
          </div>



          <div class="form-item full-width">
            <label class="form-label">地址信息</label>
            <el-input 
              v-model="formData.address"
              placeholder="请输入停车场地址"
              class="form-input"
            />
          </div>

          <div v-if="formData.id" class="form-item">
            <label class="form-label">创建时间</label>
            <el-input 
              :model-value="formData.gmtCreate ? new Date(formData.gmtCreate).toLocaleString() : '-'"
              disabled
              class="form-input"
              size="small"
            />
          </div>

          <div v-if="formData.id" class="form-item">
            <label class="form-label">更新时间</label>
            <el-input 
              :model-value="formData.gmtModified ? new Date(formData.gmtModified).toLocaleString() : '-'"
              disabled
              class="form-input"
              size="small"
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
import { ref, computed } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { X, MapPin } from 'lucide-vue-next'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import { lotApi } from '@/api/business'

defineOptions({ name: 'ParkingLotManage' })

const searchFormState = ref({ name: '', status: '' })
const searchItems = computed(() => [
  {
    key: 'name',
    label: '停车场名称',
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
      { label: '开放', value: '1' },
      { label: '关闭', value: '0' }
    ]
  }
])

const {
  data, loading, pagination, columns, columnChecks,
  handleSizeChange, handleCurrentChange, getData, refreshData, searchParams
} = useTable({
  core: {
    apiFn: lotApi.fetchLotList,
    columnsFactory: () => [
      { type: 'selection', width: 50 },
      {
        type: 'globalIndex', 
        label: '序号', 
        width: 60
      },
      { prop: 'lotInfo', label: '停车场信息', minWidth: 180, useSlot: true },

      {
        prop: 'available', 
        label: '实时余位', 
        width: 140,
        formatter: (row: any) => `${row.availableSpaceNumber} / ${row.parkingSpaceNumber}`
      },
      {
        prop: 'usageRate', 
        label: '使用率', 
        formatter: (row: any) => {
          if (row.parkingSpaceNumber === 0) return '0%';
          const rate = ((row.parkingSpaceNumber - row.availableSpaceNumber) / row.parkingSpaceNumber) * 100;
          return `${rate.toFixed(1)}%`;
        }
      },
      {
        prop: 'address', 
        label: '地址信息', 
        minWidth: 280, 
        formatter: (row: any) => {
          return row.address || row.addr || row.location || '';
        } 
      },
      { prop: 'status', label: '状态', useSlot: true, width: 120 },
      {
        prop: 'gmtCreate', 
        label: '创建时间', 
        width: 170,
        formatter: (row: any) => {
          if (!row.gmtCreate) return '';
          const date = new Date(row.gmtCreate);
          return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
        }
      },
      {
        prop: 'gmtModified', 
        label: '更新时间', 
        width: 170,
        formatter: (row: any) => {
          if (!row.gmtModified) return '';
          const date = new Date(row.gmtModified);
          return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
        }
      },
      { prop: 'operation', label: '操作', width: 120, useSlot: true, fixed: 'right' }
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

const dialogVisible = ref(false)
const dialogType = ref<'add' | 'view' | 'edit'>('add')
const formRef = ref()
const selectedRows = ref<any[]>([])
const formData = ref<any>({ 
  name: '', 
  parkingSpaceNumber: 0, 
  availableSpaceNumber: 0, 
  status: 1, 
  orgId: 1, 
  address: '' 
})

const rules = {
  name: [{ required: true, message: '必填', trigger: 'blur' }],
  parkingSpaceNumber: [{ required: true, message: '必填', trigger: 'blur' }]
}

const handleAddLot = () => {
  formData.value = { 
    name: '', 
    parkingSpaceNumber: 0, 
    availableSpaceNumber: 0, 
    status: 1, 
    orgId: 1, 
    address: '' 
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

const handleSubmit = async () => {
  await formRef.value.validate()
  const api = formData.value.id ? lotApi.updateLot : lotApi.addLot
  await api(formData.value)
  ElMessage.success('操作成功')
  dialogVisible.value = false
  refreshData()
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定删除该停车场吗？', '提示').then(async () => {
    await lotApi.deleteLot(row.id)
    refreshData()
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm('确定批量删除吗？').then(async () => {
    for (const row of selectedRows.value) {
      await lotApi.deleteLot(row.id)
    }
    refreshData()
    selectedRows.value = []
  })
}

const handleCloseLot = (row: any) => {
  ElMessageBox.confirm('确定关闭该停车场吗？', '提示').then(async () => {
    await lotApi.updateLot({ ...row, status: 0 })
    ElMessage.success('关闭成功')
    refreshData()
  })
}

const handleOpenLot = (row: any) => {
  ElMessageBox.confirm('确定开放该停车场吗？', '提示').then(async () => {
    await lotApi.updateLot({ ...row, status: 1 })
    ElMessage.success('开放成功')
    refreshData()
  })
}
</script>

<style scoped>
.lot-page { padding: 20px; }
.search-container { background: #fff; padding: 18px; border-radius: 8px; }

/* 停车场名称不换行处理 */
.lot-info-cell {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 状态容器样式 */
.status-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name-text {
  font-weight: 500;
  color: #333;
  white-space: nowrap;      /* 强制不换行 */
  overflow: hidden;         /* 超出隐藏 */
  text-overflow: ellipsis;  /* 显示省略号 */
}

.org-text {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

/* 调整搜索栏状态文字贴合度 */
.search-container :deep(.el-form-item) { margin-right: 20px !important; }
.search-container :deep(.el-form-item:nth-child(2) .el-form-item__label) {
  padding-right: 6px !important;
}

/* 弹窗样式 */
:deep(.lot-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

:deep(.lot-dialog .el-dialog__header) {
  padding: 0;
}

:deep(.lot-dialog .el-dialog__body) {
  padding: 16px 20px;
}

:deep(.lot-dialog .el-dialog__footer) {
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

/* 确保状态列内容左对齐 */
:deep(.el-table__header th:nth-child(8) .cell),
:deep(.el-table__row td:nth-child(8) .cell) {
  text-align: left !important;
  padding-left: 20px !important;
}

/* 确保操作列内容左对齐 */
:deep(.el-table__header th:last-child .cell),
:deep(.el-table__row td:last-child .cell) {
  text-align: left !important;
  padding-left: 20px !important;
}
</style>