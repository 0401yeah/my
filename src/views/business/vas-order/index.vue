<template>
  <div class="vas-order-page flex flex-col gap-4 pb-5">
    <!-- 搜索区域 -->
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
    />

    <!-- 表格区域 -->
    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <ElSpace wrap>
            <ArtExcelExport
              :data="data as any"
              :columns="exportColumns as any"
              filename="增值服务订单数据"
              :auto-index="true"
              button-text="导出"
              @export-success="handleExportSuccess"
            />
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="data"
        :columns="columns"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
        @sort-change="handleSortChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <!-- 操作列 -->
        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <!-- 编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      custom-class="vas-order-dialog"
      :show-close="false"
    >
      <!-- 自定义头部 -->
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="dialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <FileText class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">编辑订单</h2>
              <p class="header-subtitle">修改增值服务订单信息</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 表单内容区 -->
      <div class="form-body">
        <div class="form-grid">
          <div class="form-item full-width">
            <label class="form-label">订单编号</label>
            <el-input 
              v-model="formData.orderNo"
              disabled
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label class="form-label">车牌号</label>
            <el-input 
              v-model="formData.plateNumber"
              @input="formData.plateNumber = formData.plateNumber.toUpperCase()"
              placeholder="请输入车牌号"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">订单状态</label>
            <el-select 
              v-model="formData.status"
              placeholder="请选择状态"
              class="form-input"
            >
              <el-option label="未支付" :value="0" />
              <el-option label="已支付" :value="1" />
              <el-option label="已退款" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">总金额 (元)</label>
            <el-input 
              v-model="formData.payAmount"
              placeholder="请输入金额"
              class="form-input"
            >
              <template #prefix>
                <span class="text-slate-400">¥</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">优惠金额 (元)</label>
            <el-input 
              v-model="formData.discountAmount"
              placeholder="请输入优惠金额"
              class="form-input"
            >
              <template #prefix>
                <span class="text-slate-400">¥</span>
              </template>
            </el-input>
          </div>

          <div class="form-item">
            <label class="form-label">支付方式</label>
            <el-select 
              v-model="formData.payWay"
              placeholder="请选择支付方式"
              class="form-input"
            >
              <el-option label="微信" :value="0" />
              <el-option label="支付宝" :value="1" />
              <el-option label="现金" :value="2" />
              <el-option label="免费抵扣" :value="3" />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">创建时间</label>
            <el-input 
              :model-value="formData.gmtCreate ? new Date(formData.gmtCreate).toLocaleString() : '-'"
              disabled
              class="form-input"
              size="small"
            />
          </div>

          <div class="form-item full-width">
            <label class="form-label">订单描述</label>
            <el-input 
              v-model="formData.body"
              type="textarea"
              :rows="2"
              placeholder="请输入订单描述"
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
          <el-button type="primary" @click="handleSubmit" :loading="formLoading" class="submit-btn">
            保存修改
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog 
      v-model="viewDialogVisible" 
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      custom-class="vas-order-dialog"
      :show-close="false"
    >
      <!-- 自定义头部 -->
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="viewDialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <FileText class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">订单详情</h2>
              <p class="header-subtitle">查看增值服务订单信息</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 详情内容区 -->
      <div class="form-body">
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">订单编号</span>
            <span class="detail-value">{{ viewData.orderNo || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">车牌号</span>
            <span class="detail-value">{{ viewData.plateNumber || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">订单状态</span>
            <span class="detail-value">{{ statusMap[viewData.status] || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">总金额</span>
            <span class="detail-value">¥{{ viewData.payAmount || '0.00' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">优惠金额</span>
            <span class="detail-value">¥{{ viewData.discountAmount || '0.00' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">支付方式</span>
            <span class="detail-value">{{ payWayMap[viewData.payWay] || '-' }}</span>
          </div>
          <div class="detail-item full-width">
            <span class="detail-label">订单描述</span>
            <span class="detail-value">{{ viewData.body || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ viewData.gmtCreate ? new Date(viewData.gmtCreate).toLocaleString() : '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">更新时间</span>
            <span class="detail-value">{{ viewData.gmtModified ? new Date(viewData.gmtModified).toLocaleString() : '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="viewDialogVisible = false" class="submit-btn" style="width: 100%">
            关闭
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import { Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
  import { X, FileText } from 'lucide-vue-next'
  import { ElMessage } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import ArtExcelExport from '@/components/core/forms/art-excel-export/index.vue'
  import { vasOrderApi } from '@/api/business'

  defineOptions({ name: 'VasOrder' })

  // 订单类型定义
  interface VasOrder {
    id: number
    orderNo: string
    plateNumber: string
    status: number
    payAmount: string
    discountAmount: string
    payWay: number
    body: string
    gmtCreate: string
    gmtModified?: string
  }

  // API响应类型定义
  interface ApiResponse<T> {
    data: T
    code: number
    message: string
  }

  // 选中的行
  const selectedRows = ref<VasOrder[]>([])

  // 表格实例引用
  const tableRef = ref()
  const searchBarRef = ref()

  // 弹窗状态
  const dialogVisible = ref(false)
  const viewDialogVisible = ref(false)
  const formLoading = ref(false)

  // 表单数据
  const formData = ref({
    id: null as number | null,
    orderNo: '',
    plateNumber: '',
    status: 0,
    payAmount: '',
    discountAmount: '',
    payWay: 0,
    body: '',
    gmtCreate: null as string | null
  })

  // 查看详情数据
  const viewData = ref<VasOrder>({} as VasOrder)

  // 搜索表单状态
  const searchFormState = ref({
    orderNo: '',
    plateNumber: '',
    status: ''
  })

  // 搜索表单配置
  const searchItems = computed(() => [
    {
      key: 'orderNo',
      label: '订单编号',
      type: 'input',
      props: {
        placeholder: '请输入订单编号'
      }
    },
    {
      key: 'plateNumber',
      label: '车牌号',
      type: 'input',
      props: {
        placeholder: '请输入车牌号'
      }
    },
    {
      key: 'status',
      label: '订单状态',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '待处理', value: '0' },
        { label: '处理中', value: '1' },
        { label: '已完成', value: '2' }
      ]
    }
  ])

  // 状态映射
  const statusMap: Record<number, string> = {
    0: '未支付',
    1: '已支付',
    2: '已退款',
    3: '已取消'
  }

  // 支付方式映射
  const payWayMap: Record<number, string> = {
    0: '微信',
    1: '支付宝',
    2: '现金',
    3: '免费抵扣'
  }

  // 导出列配置
  const exportColumns = computed(() => ({
    orderNo: { title: '订单编号', width: 20 },
    plateNumber: { title: '车牌号', width: 15 },
    body: { title: '订单描述', width: 25 },
    payAmount: { title: '总金额', width: 12 },
    discountAmount: { title: '优惠金额', width: 12 },
    status: {
      title: '订单状态',
      width: 10,
      formatter: (value: number) => statusMap[value] || '未知'
    },
    payWay: {
      title: '支付方式',
      width: 10,
      formatter: (value: number) => payWayMap[value] || '未知'
    },
    gmtCreate: { title: '创建时间', width: 20 }
  }))

  // 获取订单列表
  const fetchOrderList = async (params: any): Promise<Api.Common.PaginatedResponse<VasOrder>> => {
    try {
      const response: any = await vasOrderApi.fetchVasOrderList(params)
      return response
    } catch (error) {
      throw error
    }
  }

  // 使用 useTable Hook 管理表格数据
  const {
    data, // 表格数据
    loading, // 加载中状态
    pagination, // 分页信息
    handleSizeChange, // 分页大小变化处理
    handleCurrentChange, // 当前页变化处理
    searchParams, // 搜索参数
    resetSearchParams, // 重置搜索参数
    getData, // 获取数据
    refreshData, // 全量刷新
    columns, // 表格列配置
    columnChecks // 列显示、拖拽配置
  } = useTable({
    // 核心配置
    core: {
      apiFn: fetchOrderList,
      apiParams: {
        current: 1,
        size: 20,
        ...searchFormState.value
      },
      columnsFactory: () => [
        { type: 'selection' as const, width: 40 },
        { type: 'globalIndex' as const, width: 60, label: '序号' },
        { prop: 'orderNo', label: '订单编号', minWidth: 150, sortable: true },
        { prop: 'plateNumber', label: '车牌号', minWidth: 100, sortable: true },
        { prop: 'body', label: '订单描述', minWidth: 150, showOverflowTooltip: true },
        { 
          prop: 'payAmount', 
          label: '总金额', 
          minWidth: 100,
          sortable: true,
          formatter: (row: any) => row.payAmount ? `¥${row.payAmount}` : '¥0.00'
        },
        { 
          prop: 'discountAmount', 
          label: '优惠金额', 
          minWidth: 100,
          sortable: true,
          formatter: (row: any) => row.discountAmount ? `¥${row.discountAmount}` : '¥0.00'
        },
        { 
          prop: 'status', 
          label: '订单状态', 
          minWidth: 100,
          sortable: true,
          formatter: (row: any) => statusMap[row.status] || '未知'
        },
        { 
          prop: 'payWay', 
          label: '支付方式', 
          minWidth: 100,
          formatter: (row: any) => payWayMap[row.payWay] || '未知'
        },
        { 
          prop: 'gmtCreate', 
          label: '创建时间', 
          minWidth: 160,
          sortable: true,
          formatter: (row: any) => {
            return row.gmtCreate ? new Date(row.gmtCreate).toLocaleString() : '未知'
          }
        },
        { 
          prop: 'operation', 
          label: '操作', 
          width: 110,
          useSlot: true,
          fixed: 'right' as const
        }
      ]
    },

    // 生命周期钩子
    hooks: {
      onSuccess: (data: any, response: any) => {
        // 数据加载成功
      },
      onError: (error: any) => {
        // 数据加载失败
      }
    }
  })

  // 事件处理函数
  const handleSelectionChange = (selection: VasOrder[]) => {
    selectedRows.value = selection
  }

  const handleRowClick = (row: VasOrder) => {
  }

  const handleSortChange = (sortInfo: any) => {
  }

  const handleSearch = async () => {
    Object.assign(searchParams, searchFormState.value)
    getData()
  }

  const handleReset = () => {
    resetSearchParams()
  }

  const handleRefresh = () => {
    refreshData()
  }

  // 导出成功处理
  const handleExportSuccess = (filename: string, count: number) => {
    // 导出成功
  }

  // CRUD 操作
  const handleView = async (row: VasOrder) => {
    try {
      const response: any = await vasOrderApi.fetchVasOrderDetail(row.id)
      viewData.value = 'data' in response ? response.data : response
    } catch (error) {
      ElMessage.error('获取订单详情失败')
    }
  }

  const handleEdit = async (row: VasOrder) => {
    try {
      const response: any = await vasOrderApi.fetchVasOrderDetail(row.id)
      const data = 'data' in response ? response.data : response
      formData.value = {
        id: data.id,
        orderNo: data.orderNo || '',
        plateNumber: data.plateNumber || '',
        status: data.status ?? 0,
        payAmount: data.payAmount || '',
        discountAmount: data.discountAmount || '',
        payWay: data.payWay ?? 0,
        body: data.body || '',
        gmtCreate: data.gmtCreate || null
      }
      dialogVisible.value = true
    } catch (error) {
      ElMessage.error('获取订单详情失败')
    }
  }

  const handleSubmit = async () => {
    if (!formData.value.plateNumber) {
      ElMessage.warning('请输入车牌号')
      return
    }
    
    formLoading.value = true
    try {
      await vasOrderApi.updateVasOrder({
        id: formData.value.id,
        plateNumber: formData.value.plateNumber,
        status: formData.value.status,
        payAmount: formData.value.payAmount,
        discountAmount: formData.value.discountAmount,
        payWay: formData.value.payWay,
        body: formData.value.body
      })
      ElMessage.success('修改成功')
      dialogVisible.value = false
      refreshData()
    } catch (error) {
      ElMessage.error('修改失败')
    } finally {
      formLoading.value = false
    }
  }

  const handleDelete = async (row: VasOrder) => {
    try {
      await vasOrderApi.deleteVasOrder(row.id)
      // 删除成功后刷新数据
      refreshData()
    } catch (error) {
      // 删除失败
    }
  }
</script>

<style scoped>
  .vas-order-page {
    padding: 20px;
  }

  /* 弹窗样式 */
  :deep(.vas-order-dialog) {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.vas-order-dialog .el-dialog__header) {
    padding: 0;
  }
  
  :deep(.vas-order-dialog .el-dialog__body) {
    padding: 16px 20px;
  }
  
  :deep(.vas-order-dialog .el-dialog__footer) {
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
  
  :deep(.form-input .el-textarea__inner) {
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    border-radius: 8px;
    padding: 6px 12px;
  }
  
  :deep(.form-input .el-textarea__inner:hover) {
    box-shadow: 0 0 0 1px #6366f1 inset;
  }
  
  :deep(.form-input .el-textarea__inner:focus) {
    box-shadow: 0 0 0 2px #6366f1 inset;
  }

  /* 详情内容 */
  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .detail-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  
  .detail-item.full-width {
    grid-column: span 2;
  }
  
  .detail-label {
    font-size: 12px;
    font-weight: 500;
    color: #94a3b8;
  }
  
  .detail-value {
    font-size: 14px;
    color: #1e293b;
    word-break: break-all;
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