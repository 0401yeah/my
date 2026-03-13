<template>
  <div class="record-page">
    <ArtSearchBar
      ref="searchBarRef"
      v-model="searchFormState"
      :items="searchItems"
      :is-expand="false"
      :show-expand="true"
      :show-reset-button="true"
      :show-search-button="true"
      @search="handleSearch"
      @reset="handleReset"
      label-width="90px"
    />

    <ElCard class="flex-1 art-table-card mt-4" shadow="never">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="handleAddRecord" v-ripple class="create-button">
              <ElIcon><Plus /></ElIcon>
              新增停车记录
            </ElButton>
            <ArtExcelExport
              :data="data as any"
              :columns="exportColumns as any"
              filename="停车记录数据"
              :auto-index="true"
              button-text="导出"
              @export-success="handleExportSuccess"
            />
          </ElSpace>
        </template>
      </ArtTableHeader>

      <div class="table-container">
        <ArtTable
          ref="tableRef"
          :loading="loading"
          :data="data"
          :columns="columns"
          :pagination="pagination"
          @selection-change="handleSelectionChange"
          @row-click="handleRowClick"
          @sort-change="handleSortChange"
          @pagination:size-change="handleSizeChange"
          @pagination:current-change="handleCurrentChange"
        >
          <template #plateNumber="{ row }">
            <div class="info-cell">
              <span class="plate-no">{{ row.plateNumber || '-' }}</span>
            </div>
          </template>

          <template #operation="{ row }">
            <div class="flex items-center">
              <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
              <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
            </div>
          </template>
        </ArtTable>
      </div>
    </ElCard>

    <!-- 编辑/新建对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      custom-class="record-dialog"
      :show-close="false"
    >
      <!-- 自定义头部 -->
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="dialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <CarFront class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">{{ formData.plateNumber ? '编辑停车记录' : '新增停车记录' }}</h2>
              <p class="header-subtitle">{{ formData.plateNumber ? '修改停车记录信息' : '手动补录车辆进出场信息' }}</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 表单内容区 -->
      <div class="form-body">
        <div class="form-grid">
          <div class="form-item">
            <label class="form-label">车牌号 <span class="required">*</span></label>
            <el-input 
              v-model="formData.plateNumber"
              @input="formData.plateNumber = formData.plateNumber.toUpperCase()"
              placeholder="请输入车牌号"
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
              <el-option label="泰州学院南门地下车库" value="1" />
              <el-option label="泰州学院行政楼地面车场" value="2" />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">停车位</label>
            <el-input 
              v-model="formData.spaceNo"
              placeholder="例如: A-001"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">状态</label>
            <el-select 
              v-model="formData.status"
              placeholder="请选择状态"
              class="form-input"
            >
              <el-option label="停车中" value="0" />
              <el-option label="已出场" value="1" />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">入场时间 <span class="required">*</span></label>
            <el-date-picker
              v-model="formData.gmtInto"
              type="datetime"
              placeholder="请选择入场时间"
              style="width: 100%"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">出场时间</label>
            <el-date-picker
              v-model="formData.gmtOut"
              type="datetime"
              placeholder="请选择出场时间"
              style="width: 100%"
              class="form-input"
            />
          </div>

          <div class="form-item full-width">
            <label class="form-label">停车费用 (元)</label>
            <el-input 
              v-model="formData.payAmount"
              placeholder="0.00"
              class="form-input"
            >
              <template #prefix>
                <DollarSign class="w-4 h-4 text-slate-400" />
              </template>
            </el-input>
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
            {{ formData.plateNumber ? '保存修改' : '保存记录' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Timer } from '@element-plus/icons-vue'
import { X, CarFront, MapPin, Clock, DollarSign, Calendar, ChevronDown } from 'lucide-vue-next'
import { ElMessageBox, ElMessage, ElIcon, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElRow, ElCol, ElDatePicker } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
import ArtTable from '@/components/core/tables/art-table/index.vue'
import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
import ArtExcelExport from '@/components/core/forms/art-excel-export/index.vue'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { recordApi } from '@/api/business'
import { formatTime } from '@/utils/time'

defineOptions({ name: 'Record' })

const tableRef = ref()
const searchBarRef = ref()
const dialogVisible = ref(false)
const formRef = ref()
const selectedRows = ref<any[]>([])

const formData = ref({ plateNumber: '', parkingLotId: '1', spaceNo: '', status: '0', gmtInto: '', gmtOut: '', payAmount: '0.00' })
const searchFormState = ref({ plateNumber: '', parkingLotId: '', status: '' })
const statusMap: Record<number, string> = { 0: '停车中', 1: '已出场' }
const parkingLotMap: Record<string, string> = { '1': '泰州学院南门地下车库', '2': '泰州学院行政楼地面车场' }

const searchItems = computed(() => [
  { key: 'plateNumber', label: '车牌号', type: 'input' },
  { key: 'parkingLotId', label: '停车场', type: 'select', options: [{ label: '全部', value: '' }, { label: '南门地下', value: '1' }] },
  { key: 'status', label: '状态', type: 'select', options: [{ label: '全部', value: '' }, { label: '停车中', value: '0' }, { label: '已出场', value: '1' }] }
])

const exportColumns = computed(() => ({
  plateNumber: { title: '车牌号' },
  parkingLotId: { title: '停车场', formatter: (v: string) => parkingLotMap[v] || '-' },
  spaceNo: { title: '停车位' },
  gmtInto: { title: '入场时间' },
  gmtOut: { title: '出场时间' },
  stayMinutes: { title: '时长' },
  payAmount: { title: '费用' },
  status: { title: '状态', formatter: (v: number) => statusMap[v] }
}))

const {
  data, loading, pagination, handleSizeChange, handleCurrentChange,
  searchParams, resetSearchParams, getData, refreshData, columns, columnChecks
} = useTable({
  core: {
    apiFn: recordApi.fetchRecordList,
    apiParams: { current: 1, size: 20, ...searchFormState.value },
    columnsFactory: () => [
      { type: 'selection', width: 50, align: 'center' },
      { type: 'globalIndex', width: 70, label: '序号', align: 'center' },
      {
        prop: 'plateNumber',
        label: '车辆信息',
        minWidth: 120,
        useSlot: true
      },
      { prop: 'gmtInto', label: '入场时间', minWidth: 160, align: 'center', formatter: (row: any) => formatTime(row.gmtInto) },
      { prop: 'gmtOut', label: '出场时间', minWidth: 160, align: 'center', formatter: (row: any) => formatTime(row.gmtOut) },
      { prop: 'stayMinutes', label: '停车时长(分钟)', minWidth: 120, align: 'center' },
      { prop: 'payAmount', label: '停车费用(元)', minWidth: 120, align: 'center', formatter: (row: any) => (row.payAmount || 0).toFixed(2) },
      { prop: 'parkingLotId', label: '停车场', minWidth: 180, formatter: (row: any) => parkingLotMap[row.parkingLotId] || '-' },
      { prop: 'spaceNo', label: '停车位', minWidth: 100, align: 'center' },
      { prop: 'status', label: '状态', minWidth: 90, align: 'center', formatter: (row: any) => statusMap[row.status] || '-' },
      {
        prop: 'operation',
        label: '操作',
        width: 150,
        useSlot: true,
        fixed: 'right',
        align: 'center'
      }
    ]
  }
})

const handleSearch = () => { Object.assign(searchParams, searchFormState.value); getData() }
const handleReset = () => { resetSearchParams(); handleSearch() }
const handleRefresh = () => refreshData()
const handleSelectionChange = (val: any[]) => { selectedRows.value = val }
const handleExportSuccess = () => ElMessage.success('导出成功')
const handleEdit = (row: any) => {
  formData.value = {
    ...row,
    parkingLotId: String(row.parkingLotId),
    status: String(row.status)
  }
  dialogVisible.value = true
}
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除吗？`, '警告', { type: 'warning' }).then(async () => {
    await recordApi.deleteRecord(row.id); refreshData(); ElMessage.success('已删除')
  })
}
const handleAddRecord = () => { dialogVisible.value = true }
const handleSubmit = async () => {
  await formRef.value.validate()
  const submitData = {
    ...formData.value,
    parkingLotId: Number(formData.value.parkingLotId),
    status: Number(formData.value.status)
  }
  await recordApi.updateRecord(submitData)
  dialogVisible.value = false; refreshData(); ElMessage.success('操作成功')
}

const handleRowClick = (row: any) => {
}

const handleSortChange = (sortInfo: any) => {
}

const rules = { plateNumber: [{ required: true, message: '请输入车牌', trigger: 'blur' }] }
</script>

<style scoped>
  .record-page {
    padding: 24px;
    height: 100%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    background-color: #f8fafc;
  }

  .mt-4 {
    margin-top: 24px;
  }

  .art-table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: none;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow: hidden;
  }

  .table-container {
    flex: 1;
    overflow: hidden;
    margin-top: 16px;
  }
  
  .justify-center {
    justify-content: center;
  }

  /* 操作按钮样式 */
  :deep(.el-table__row) {
    transition: all 0.3s ease;
  }
  
  :deep(.el-table__row:hover) {
    background-color: #f8fafc !important;
  }
  
  /* 动画效果 */
  @keyframes zoomIn {
    from { opacity: 0; transform: scale(0.95); }
    to { opacity: 1; transform: scale(1); }
  }
  
  .animate-zoom-in {
    animation: zoomIn 0.3s ease-out forwards;
  }

  /* 自定义滚动条 */
  :deep(.overflow-y-auto) {
    scrollbar-width: thin;
    scrollbar-color: #e2e8f0 #f8fafc;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar) {
    width: 6px;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar-track) {
    background: #f8fafc;
    border-radius: 3px;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar-thumb) {
    background: #e2e8f0;
    border-radius: 3px;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar-thumb:hover) {
    background: #cbd5e1;
  }

  /* 弹窗样式 */
  :deep(.record-dialog) {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.record-dialog .el-dialog__header) {
    padding: 0;
  }
  
  :deep(.record-dialog .el-dialog__body) {
    padding: 16px 20px;
  }
  
  :deep(.record-dialog .el-dialog__footer) {
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

  /* 创建按钮 */
  .create-button {
    border-radius: 12px;
    padding: 8px 16px;
    font-weight: 500;
  }

  /* 响应式调整 */
  @media (max-width: 768px) {
    .record-page {
      padding: 16px;
    }
    
    :deep(.record-dialog) {
      width: 95% !important;
    }
    
    :deep(.record-dialog .el-dialog__body) {
      padding: 0 16px 24px;
    }
    
    :deep(.record-dialog .el-dialog__footer) {
      padding: 0 16px 24px;
    }
    
    .dialog-header {
      padding: 24px 16px;
    }
    
    .close-button {
      right: 16px;
      top: 16px;
    }
    
    .header-icon {
      width: 50px;
      height: 50px;
    }
    
    .header-title {
      font-size: 20px;
    }
    
    .form-content {
      padding: 24px 0 0;
      gap: 20px;
    }
    
    .form-module {
      padding: 20px;
      border-radius: 24px;
    }
    
    .form-row {
      grid-template-columns: 1fr;
      gap: 16px;
    }
    
    .form-section.full-width {
      grid-column: span 1;
    }
    
    .dialog-footer {
      gap: 12px;
    }
  }

  .info-cell {
    display: flex;
    flex-direction: column;
  }

  .plate-no {
    color: #606266; 
    font-weight: 500;
  }
</style>