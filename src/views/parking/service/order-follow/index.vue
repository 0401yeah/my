<template>
  <div class="order-follow-page flex flex-col gap-4 pb-5">
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

    <ElCard class="flex-1 art-table-card" shadow="never">
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
              filename="服务工单数据"
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
          <template #orderInfo="{ row }">
            <div>
              <div class="font-medium">{{ row.orderNo }}</div>
              <div class="text-xs text-gray-400">{{ row.serviceName }}</div>
            </div>
          </template>

          <template #serviceType="{ row }">
            <ElTag v-if="row.serviceType === 'valet'" type="primary">代泊服务</ElTag>
            <ElTag v-else-if="row.serviceType === 'carwash'" type="success">洗车服务</ElTag>
            <ElTag v-else-if="row.serviceType === 'monthly'" type="warning">月卡服务</ElTag>
            <ElTag v-else type="info">其他</ElTag>
          </template>

          <template #status="{ row }">
            <ElTag v-if="row.status === 'pending'" type="warning">{{ row.statusText }}</ElTag>
            <ElTag v-else-if="row.status === 'processing'" type="primary">{{ row.statusText }}</ElTag>
            <ElTag v-else-if="row.status === 'completed'" type="success">{{ row.statusText }}</ElTag>
            <ElTag v-else type="info">{{ row.statusText }}</ElTag>
          </template>

          <template #operation="{ row }">
            <div class="flex items-center">
              <ArtButtonTable type="view" :row="row" @click="handleView(row)" />
              <ArtButtonTable 
                v-if="row.status === 'pending' || row.status === 'processing'" 
                type="edit" 
                :row="row" 
                @click="handleFollow(row)" 
              />
              <ArtButtonTable 
                v-if="row.status === 'pending'" 
                type="delete" 
                :row="row" 
                @click="handleCancel(row)" 
              />
            </div>
          </template>
        </ArtTable>
      </div>
    </ElCard>

    <ElDialog v-model="detailDialogVisible" title="工单详情" width="700px">
      <ElDescriptions :column="2" border v-if="currentOrder">
        <ElDescriptionsItem label="工单编号">{{ currentOrder.orderNo }}</ElDescriptionsItem>
        <ElDescriptionsItem label="服务类型">
          <ElTag v-if="currentOrder.serviceType === 'valet'" type="primary">代泊服务</ElTag>
          <ElTag v-else-if="currentOrder.serviceType === 'carwash'" type="success">洗车服务</ElTag>
          <ElTag v-else-if="currentOrder.serviceType === 'monthly'" type="warning">月卡服务</ElTag>
          <ElTag v-else type="info">其他</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="车牌号">{{ currentOrder.plateNumber }}</ElDescriptionsItem>
        <ElDescriptionsItem label="用户名">{{ currentOrder.username }}</ElDescriptionsItem>
        <ElDescriptionsItem label="停车场">{{ currentOrder.parkingLotName }}</ElDescriptionsItem>
        <ElDescriptionsItem label="车位号">{{ currentOrder.parkedSpaceNo || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="预约时间">{{ currentOrder.appointmentTime }}</ElDescriptionsItem>
        <ElDescriptionsItem label="工单状态">
            <ElTag v-if="currentOrder.status === 'pending'" type="warning">{{ currentOrder.statusText }}</ElTag>
            <ElTag v-else-if="currentOrder.status === 'processing'" type="primary">{{ currentOrder.statusText }}</ElTag>
            <ElTag v-else-if="currentOrder.status === 'completed'" type="success">{{ currentOrder.statusText }}</ElTag>
            <ElTag v-else type="info">{{ currentOrder.statusText }}</ElTag>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="创建时间">{{ currentOrder.gmtCreate }}</ElDescriptionsItem>
        </ElDescriptions>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>

    <ElDialog v-model="followDialogVisible" title="工单跟进" width="600px">
      <ElForm :model="followForm" label-width="100px">
        <ElFormItem label="工单编号">
          <ElInput v-model="followForm.orderNo" disabled />
        </ElFormItem>
        <ElFormItem label="车牌号">
          <ElInput v-model="followForm.plateNumber" disabled />
        </ElFormItem>
        <ElFormItem label="服务类型">
          <ElInput v-model="followForm.serviceTypeName" disabled />
        </ElFormItem>
        <ElFormItem label="工单状态" required>
          <ElSelect v-model="followForm.status" placeholder="请选择工单状态" style="width: 100%">
            <ElOption v-if="followForm.serviceType === 'valet'" label="待接车" value="pending" />
            <ElOption v-if="followForm.serviceType === 'valet'" label="泊车中" value="processing" />
            <ElOption v-if="followForm.serviceType === 'valet'" label="已停妥" value="completed" />
            <ElOption v-if="followForm.serviceType === 'carwash'" label="待清洗" value="pending" />
            <ElOption v-if="followForm.serviceType === 'carwash'" label="清洗中" value="processing" />
            <ElOption v-if="followForm.serviceType === 'carwash'" label="已完成质检" value="completed" />
          </ElSelect>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="followDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitFollow">保存</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { useTable } from '@/hooks/core/useTable'
import { valetOrderApi, carwashOrderApi, lotApi } from '@/api/business'
import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
import ArtTable from '@/components/core/tables/art-table/index.vue'
import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
import ArtExcelExport from '@/components/core/forms/art-excel-export/index.vue'

defineOptions({ name: 'ServiceOrderFollow' })

const selectedRows = ref<any[]>([])
const tableRef = ref()
const searchBarRef = ref()

const detailDialogVisible = ref(false)
const followDialogVisible = ref(false)
const currentOrder = ref<any>(null)
const followForm = ref<any>({
  id: null,
  orderNo: '',
  plateNumber: '',
  serviceTypeName: '',
  status: '',
  fee: 0,
  remark: ''
})

const searchFormState = ref({
  orderNo: '',
  plateNumber: '',
  status: '',
  createTime: []
})

const searchItems = computed(() => [
  {
    key: 'orderNo',
    label: '工单编号',
    type: 'input',
    props: { placeholder: '请输入工单编号' }
  },
  {
    key: 'plateNumber',
    label: '车牌号',
    type: 'input',
    props: { placeholder: '请输入车牌号' }
  },

  {
    key: 'status',
    label: '工单状态',
    type: 'select',
    options: [
      { label: '全部', value: '' },
      { label: '待处理', value: 'pending' },
      { label: '进行中', value: 'processing' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ]
  },
  {
    key: 'createTime',
    label: '创建时间',
    type: 'daterange',
    props: {
      type: 'datetimerange',
      rangeSeparator: '至',
      startPlaceholder: '开始日期',
      endPlaceholder: '结束日期',
      clearable: true
    }
  }
])

const parkingLots = ref<any[]>([])

const fetchParkingLots = async () => {
  try {
    const res: any = await lotApi.fetchLotList({})
    if (res && res.records) {
      parkingLots.value = res.records
    }
  } catch (error) {
    console.error('获取停车场列表失败:', error)
  }
}

const fetchAllOrders = async (params: any) => {
  await fetchParkingLots()
  
  const parkingLotMap = new Map()
  parkingLots.value.forEach((lot: any) => {
    parkingLotMap.set(lot.id, lot.name)
  })
  
  const [valetRes, carwashRes]: any[] = await Promise.all([
    valetOrderApi.fetchValetOrderList({ current: 1, size: 1000 }),
    carwashOrderApi.fetchCarwashOrderList({ current: 1, size: 1000 })
  ])
  
  const allOrders: any[] = []
  
  if (valetRes?.records) {
    valetRes.records.forEach((item: any) => {
      let status = 'pending'
      let statusText = '待接车'
      if (item.status === 0) { status = 'pending'; statusText = '待接车' }
      else if (item.status === 1) { status = 'processing'; statusText = '泊车中' }
      else if (item.status === 2) { status = 'completed'; statusText = '已停妥' }
      else if (item.status === 3) { status = 'processing'; statusText = '车主呼叫取车' }
      else if (item.status === 4) { status = 'completed'; statusText = '已交还车主' }
      
      allOrders.push({
        id: `valet_${item.id}`,
        orderNo: item.orderNo,
        plateNumber: item.plateNumber,
        username: '-',
        serviceType: 'valet',
        serviceName: '代泊服务',
        parkingLotName: parkingLotMap.get(item.parkingLotId) || '-',
        parkedSpaceNo: '-',
        appointmentTime: item.appointmentTime || item.gmtCreate,
        status: status,
        statusText: statusText,
        fee: 0,
        remark: '',
        gmtCreate: item.gmtCreate
      })
    })
  }
  
  if (carwashRes?.records) {
    carwashRes.records.forEach((item: any) => {
      let status = 'pending'
      let statusText = '待清洗'
      if (item.status === 0) { status = 'pending'; statusText = '待清洗' }
      else if (item.status === 1) { status = 'processing'; statusText = '清洗中' }
      else if (item.status === 2) { status = 'completed'; statusText = '已完成质检' }
      
      const washTypeNames: any = { 1: '外观普洗', 2: '内外精洗', 3: '打蜡美容' }
      
      allOrders.push({
        id: `carwash_${item.id}`,
        orderNo: item.orderNo,
        plateNumber: item.plateNumber,
        username: '-',
        serviceType: 'carwash',
        serviceName: washTypeNames[item.washType] || '洗车服务',
        parkingLotName: parkingLotMap.get(item.parkingLotId) || '-',
        parkedSpaceNo: '-',
        appointmentTime: item.appointmentTime || item.gmtCreate,
        status: status,
        statusText: statusText,
        fee: 0,
        remark: '',
        gmtCreate: item.gmtCreate
      })
    })
  }
  
  allOrders.sort((a, b) => new Date(b.gmtCreate).getTime() - new Date(a.gmtCreate).getTime())
  
  let filteredOrders = allOrders
  
  if (params.orderNo) {
    filteredOrders = filteredOrders.filter((order: any) => 
      order.orderNo.toLowerCase().includes(params.orderNo.toLowerCase())
    )
  }
  
  if (params.plateNumber) {
    filteredOrders = filteredOrders.filter((order: any) => 
      order.plateNumber.toLowerCase().includes(params.plateNumber.toLowerCase())
    )
  }
  

  
  if (params.status) {
    filteredOrders = filteredOrders.filter((order: any) => 
      order.status === params.status
    )
  }
  
  if (params.createTime && params.createTime.length === 2) {
    const startTime = new Date(params.createTime[0]).getTime()
    const endTime = new Date(params.createTime[1]).getTime()
    filteredOrders = filteredOrders.filter((order: any) => {
      const orderTime = new Date(order.gmtCreate).getTime()
      return orderTime >= startTime && orderTime <= endTime
    })
  }
  
  const current = params.current || 1
  const size = params.size || 20
  const start = (current - 1) * size
  const end = start + size
  
  return {
    records: filteredOrders.slice(start, end),
    total: filteredOrders.length
  }
}

const {
  data, loading, pagination, handleSizeChange, handleCurrentChange,
  searchParams, resetSearchParams, getData, refreshData, columns, columnChecks
} = useTable({
  core: {
    apiFn: fetchAllOrders,
    apiParams: {
      current: 1,
      size: 20
    },
    columnsFactory: () => [
        { type: 'selection', width: 50, align: 'center' },
        { type: 'globalIndex', width: 60, label: '序号', align: 'center' },
        { prop: 'orderInfo', label: '工单信息', minWidth: 180, useSlot: true },
        { prop: 'plateNumber', label: '车牌号', width: 120, align: 'center' },
        { prop: 'serviceType', label: '服务类型', width: 120, useSlot: true, align: 'center' },
        { prop: 'parkingLotName', label: '停车场', width: 150, align: 'center' },
        { prop: 'status', label: '状态', width: 120, useSlot: true, align: 'center' },
        { prop: 'gmtCreate', label: '创建时间', minWidth: 160, sortable: true, align: 'center' },
        { prop: 'operation', label: '操作', width: 160, useSlot: true, fixed: 'right', align: 'left' }
      ]
  }
})

const exportColumns = computed(() => [
  { label: '工单编号', prop: 'orderNo' },
  { label: '车牌号', prop: 'plateNumber' },
  { label: '服务类型', prop: 'serviceName' },
  { label: '停车场', prop: 'parkingLotName' },
  { label: '状态', prop: 'statusText' },
  { label: '创建时间', prop: 'gmtCreate' }
])

const handleSelectionChange = (selection: any[]) => selectedRows.value = selection
const handleRowClick = (row: any) => {}
const handleSortChange = (sortInfo: any) => {}

const handleSearch = async () => {
  Object.assign(searchParams, {
    orderNo: searchFormState.value.orderNo,
    plateNumber: searchFormState.value.plateNumber,
    status: searchFormState.value.status,
    current: 1
  })
  
  if (searchFormState.value.createTime && searchFormState.value.createTime.length === 2) {
    searchParams.createTime = searchFormState.value.createTime
  } else {
    delete searchParams.createTime
  }
  
  getData()
}

const handleReset = () => {
  resetSearchParams()
  searchFormState.value = { orderNo: '', plateNumber: '', status: '', createTime: [] }
  getData()
}

const handleRefresh = () => refreshData()

const handleView = (row: any) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

const handleFollow = (row: any) => {
  followForm.value = {
    id: row.id,
    orderNo: row.orderNo,
    plateNumber: row.plateNumber,
    serviceTypeName: row.serviceName,
    serviceType: row.serviceType,
    status: row.status,
    fee: row.fee,
    remark: row.remark
  }
  followDialogVisible.value = true
}

const submitFollow = async () => {
  try {
    const [type, id] = followForm.value.id.split('_')
    
    if (type === 'valet') {
      const statusMap: any = { pending: 0, processing: 1, completed: 2 }
      await valetOrderApi.updateValetOrder({
        id: parseInt(id),
        status: statusMap[followForm.value.status]
      })
    } else if (type === 'carwash') {
      const statusMap: any = { pending: 0, processing: 1, completed: 2 }
      await carwashOrderApi.updateCarwashOrder({
        id: parseInt(id),
        status: statusMap[followForm.value.status]
      })
    }
    
    ElMessage.success('跟进成功')
    followDialogVisible.value = false
    refreshData()
  } catch (error) {
    console.error('跟进失败:', error)
    ElMessage.error('跟进失败，请重试')
  }
}

const handleCancel = (row: any) => {
  ElMessageBox.confirm('确定要取消该工单吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const [type, id] = row.id.split('_')
      
      if (type === 'valet') {
        await valetOrderApi.deleteValetOrder(parseInt(id))
      } else if (type === 'carwash') {
        await carwashOrderApi.deleteCarwashOrder(parseInt(id))
      }
      
      ElMessage.success('取消成功')
      refreshData()
    } catch (error) {
      console.error('取消失败:', error)
      ElMessage.error('取消失败，请重试')
    }
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

const handleExportSuccess = () => {
  ElMessage.success('导出成功')
}
</script>

<style scoped>
.order-follow-page {
  padding: 24px;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  background-color: #f8fafc;
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

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
  background-color: #f8fafc !important;
}
</style>
