<template>
  <div class="user-valet flex flex-col gap-4 pb-5">
    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout=""
        fullClass="art-table-card"
      >
        <template #left>
          <div class="card-title"></div>
        </template>
        <template #right>
          <ElSpace>
            <ElButton type="primary" @click="handleApply">
              <ElIcon><Plus /></ElIcon>
              新建申请
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="tableData"
        :columns="columns"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #parkingInfo="{ row }">
          <div>
            <div class="font-medium">{{ row.parkingLotName }}</div>
            <div class="text-xs text-gray-400">{{ row.parkingLotAddress }}</div>
          </div>
        </template>

        <template #serviceType="{ row }">
          <div class="font-medium">{{ row.serviceItemName || (row.serviceType === 'carwash' ? row.washTypeName : '') || '-' }}</div>
        </template>

        <template #status="{ row }">
          <ElTag v-if="row.status === 'pending'" type="warning">待处理</ElTag>
          <ElTag v-else-if="row.status === 'processing'" type="primary">进行中</ElTag>
          <ElTag v-else-if="row.status === 'completed'" type="success">已完成</ElTag>
          <ElTag v-else type="info">已取消</ElTag>
        </template>

        <template #operation="{ row }">
          <div class="flex gap-2">
            <ElButton type="primary" link size="small" @click="handleEdit(row)">
              <ElIcon><Edit /></ElIcon>
            </ElButton>
            <ElButton v-if="row.status === 'pending'" type="danger" link size="small" @click="handleCancel(row)">
              <ElIcon><Delete /></ElIcon>
            </ElButton>
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <ElDialog v-model="applyDialogVisible" title="" width="500px">
      <!-- 有车辆时的表单 -->
      <div v-if="vehicles.length > 0">
        <ElForm ref="formRef" :model="formData" :rules="formRules" label-width="100px">
          <ElFormItem label="服务类型" prop="serviceType">
            <ElSelect v-model="formData.serviceType" placeholder="请选择服务类型" style="width: 100%">
              <ElOption label="代泊服务" value="valet" />
              <ElOption label="洗车服务" value="carwash" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="服务价格">
            <ElInput v-model="formData.price" placeholder="服务价格" disabled style="width: 100%" />
          </ElFormItem>

          <ElFormItem label="车牌号" prop="plateNumber">
            <ElSelect v-model="formData.plateNumber" placeholder="请选择车辆" style="width: 100%">
              <ElOption v-for="vehicle in vehicles" :key="vehicle.value" :label="vehicle.label" :value="vehicle.value" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="停车场" prop="parkingLotId">
            <ElSelect v-model="formData.parkingLotId" placeholder="请选择停车场" style="width: 100%">
              <ElOption v-for="lot in parkingLots" :key="lot.value" :label="lot.label" :value="lot.value" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="车位号">
            <ElInput v-model="formData.parkedSpaceNo" placeholder="自动获取车位号" disabled style="width: 100%" />
          </ElFormItem>
          <ElFormItem label="预约时间" prop="appointmentTime">
            <ElDatePicker
              v-model="formData.appointmentTime"
              type="datetime"
              placeholder="请选择预约时间"
              style="width: 100%"
            />
          </ElFormItem>
          <ElFormItem label="备注" prop="remark">
            <ElInput v-model="formData.remark" type="textarea" placeholder="请输入备注信息" :rows="3" />
          </ElFormItem>
        </ElForm>
      </div>
      
      <!-- 无车辆时的提示 -->
      <div v-else class="flex flex-col items-start justify-center py-12 px-8">
        <!-- 蓝色横线 -->
        <div class="w-12 h-1 bg-blue-500 mb-8"></div>
        
        <!-- 标题 -->
        <h3 class="text-2xl font-bold text-gray-900 mb-4">暂无车辆</h3>
        
        <!-- 描述 -->
        <p class="text-gray-500 mb-8 max-w-md">
          您还没有绑定任何车辆。绑定车辆后才能申请代泊车服务，享受便捷的停车体验。
        </p>
        
        <!-- 主要按钮 -->
        <button class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg font-semibold transition-all shadow-lg flex items-center gap-2 w-full" @click="goToVehicleManagement">
          快速添加车辆
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14"></path>
            <path d="m12 5 7 7-7 7"></path>
          </svg>
        </button>
      </div>
      <template #footer>
        <ElButton @click="applyDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">提交申请</ElButton>
      </template>
    </ElDialog>

    <ElDialog v-model="editDialogVisible" title="修改申请" width="500px">
      <ElForm ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <ElFormItem label="服务类型" prop="serviceType">
          <ElSelect v-model="formData.serviceType" placeholder="请选择服务类型" style="width: 100%" disabled>
            <ElOption label="代泊服务" value="valet" />
            <ElOption label="洗车服务" value="carwash" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="车牌号" prop="plateNumber">
          <ElSelect v-model="formData.plateNumber" placeholder="请选择车辆" style="width: 100%" disabled>
            <ElOption v-for="vehicle in vehicles" :key="vehicle.value" :label="vehicle.label" :value="vehicle.value" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="停车场" prop="parkingLotId">
          <ElSelect v-model="formData.parkingLotId" placeholder="请选择停车场" style="width: 100%">
            <ElOption v-for="lot in parkingLots" :key="lot.value" :label="lot.label" :value="lot.value" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="车位号">
          <ElInput v-model="formData.parkedSpaceNo" placeholder="车位号" style="width: 100%" />
        </ElFormItem>
        <ElFormItem label="预约时间" prop="appointmentTime">
          <ElDatePicker
            v-model="formData.appointmentTime"
            type="datetime"
            placeholder="请选择预约时间"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="备注" prop="remark">
          <ElInput v-model="formData.remark" type="textarea" placeholder="请输入备注信息" :rows="3" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="editDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleEditSubmit">保存修改</ElButton>
      </template>
    </ElDialog>

    <ElDialog v-model="detailDialogVisible" title="申请详情" width="500px">
      <ElDescriptions :column="1" border>
        <ElDescriptionsItem label="申请单号">{{ currentDetail?.orderNo }}</ElDescriptionsItem>
        <ElDescriptionsItem label="车牌号">{{ currentDetail?.plateNumber }}</ElDescriptionsItem>
        <ElDescriptionsItem label="服务类型">
          <ElTag v-if="currentDetail?.serviceType === 'valet'" type="primary">代泊</ElTag>
          <ElTag v-else-if="currentDetail?.serviceType === 'carwash'" type="success">洗车</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="服务项目">{{ currentDetail?.serviceItemName }}</ElDescriptionsItem>
        <ElDescriptionsItem label="停车场">{{ currentDetail?.parkingLotName }}</ElDescriptionsItem>
        <ElDescriptionsItem label="车位号">{{ currentDetail?.parkedSpaceNo || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="预约时间">{{ currentDetail?.appointmentTime }}</ElDescriptionsItem>
        <ElDescriptionsItem label="状态">
          <ElTag v-if="currentDetail?.status === 'pending'" type="warning">待处理</ElTag>
          <ElTag v-else-if="currentDetail?.status === 'processing'" type="primary">进行中</ElTag>
          <ElTag v-else-if="currentDetail?.status === 'completed'" type="success">已完成</ElTag>
          <ElTag v-else type="info">已取消</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="费用">
          <span class="text-red-500 font-bold">¥{{ currentDetail?.fee || 0 }}</span>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="备注">{{ currentDetail?.remark || '无' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="申请时间">{{ currentDetail?.gmtCreate }}</ElDescriptionsItem>
      </ElDescriptions>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Edit } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { vehicleApi, lotApi, vasOrderApi, valetOrderApi, carwashOrderApi, recordApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'
import { PriceCalculator } from '@/utils/priceCalculator'

defineOptions({ name: 'UserValet' })

const userStore = useUserStore()

const router = useRouter()

interface ValetItem {
  id: number
  orderNo: string
  plateNumber: string
  serviceType: string
  serviceTypeName: string
  serviceItem?: string
  serviceItemName?: string
  washType?: number
  washTypeName?: string
  parkingLotName: string
  parkingLotAddress: string
  parkedSpaceNo?: string
  appointmentTime: string
  status: string
  fee: number
  remark: string
  gmtCreate: string
}

const tableRef = ref()
const formRef = ref()
const loading = ref(false)
const tableData = ref<ValetItem[]>([])
const applyDialogVisible = ref(false)
const editDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentDetail = ref<ValetItem | null>(null)
const currentEditItem = ref<any>(null)

// 车辆列表
interface Vehicle {
  value: string
  label: string
}

const vehicles = ref<Vehicle[]>([])

// 停车场列表
interface ParkingLot {
  value: string
  label: string
  address: string
}

const parkingLots = ref<ParkingLot[]>([])

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

watch(() => formData.value.plateNumber, async (newPlateNumber) => {
  if (newPlateNumber) {
    try {
      const res: any = await recordApi.fetchRecordList({
        plateNumber: newPlateNumber,
        size: 1,
        current: 1
      })
      
      if (res?.records && res.records.length > 0) {
        const latestRecord = res.records[0]
        if (latestRecord.status === 0) {
          formData.value.parkedSpaceNo = latestRecord.spaceNo || ''
          if (latestRecord.parkingLotId) {
            formData.value.parkingLotId = latestRecord.parkingLotId.toString()
          }
        }
      }
    } catch (error) {
      console.error('获取停车位信息失败:', error)
    }
  } else {
    formData.value.parkedSpaceNo = ''
  }
})

watch([() => formData.value.serviceType, () => formData.value.parkingLotId], async ([newServiceType, newParkingLotId]) => {
  if (newServiceType && newParkingLotId) {
    try {
      const priceResult = await PriceCalculator.calculatePrice(newServiceType, parseInt(newParkingLotId))
      formData.value.price = `${priceResult.finalPrice}元`
      if (priceResult.appliedStrategy) {
        ElMessage.success(`已应用运营策略: ${priceResult.appliedStrategy}`)
      }
    } catch (error) {
      console.error('价格计算失败:', error)
      // 失败时使用默认价格
      if (newServiceType === 'valet') {
        formData.value.price = '15元'
      } else if (newServiceType === 'carwash') {
        formData.value.price = '20元'
      } else {
        formData.value.price = ''
      }
    }
  } else {
    formData.value.price = ''
  }
})

const columns = ref([
  { type: 'globalIndex' as const, width: 60, label: '序号' },
  { prop: 'orderNo', label: '申请单号', width: 180 },
  { prop: 'plateNumber', label: '车牌号', width: 120 },
  { prop: 'serviceType', label: '服务项目', width: 120, useSlot: true },
  { prop: 'parkingInfo', label: '停车场', minWidth: 200, useSlot: true },
  { prop: 'parkedSpaceNo', label: '车位号', width: 100 },
  { prop: 'appointmentTime', label: '预约时间', width: 180 },
  { prop: 'status', label: '状态', width: 100, useSlot: true },
  { prop: 'fee', label: '费用', width: 100 },
  { prop: 'operation', label: '操作', width: 120, useSlot: true, fixed: 'right' as const }
])

const columnChecks = ref([])

const formData = ref({
  serviceType: '',
  price: '',
  plateNumber: '',
  parkingLotId: '',
  parkedSpaceNo: '',
  appointmentTime: '',
  remark: ''
})

const formRules = {
  serviceType: [{ required: true, message: '请选择服务类型', trigger: 'change' }],
  plateNumber: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  parkingLotId: [{ required: true, message: '请选择停车场', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    // 获取代泊订单
    const valetRes: any = await valetOrderApi.fetchValetOrderList({
      current: 1,
      size: 100
    })
    
    // 获取洗车订单
    const carwashRes: any = await carwashOrderApi.fetchCarwashOrderList({
      current: 1,
      size: 100
    })
    
    // 获取所有停车记录（用于关联查询车位号）
    const recordRes: any = await recordApi.fetchRecordList({
      current: 1,
      size: 1000
    })
    
    // 创建停车记录的 Map，便于查找：key = 车牌号，value = 最新的停车记录
    const parkingRecordMap = new Map<string, any>()
    if (recordRes?.records) {
      recordRes.records.forEach((record: any) => {
        if (record.plateNumber) {
          const existingRecord = parkingRecordMap.get(record.plateNumber)
          if (!existingRecord || new Date(record.gmtInto) > new Date(existingRecord.gmtInto)) {
            parkingRecordMap.set(record.plateNumber, record)
          }
        }
      })
    }
    
    const allOrders: any[] = []
    
    // 处理代泊订单
    if (valetRes?.records) {
      valetRes.records.forEach((item: any) => {
        let status = 'pending'
        if (item.status === 0) {
          status = 'pending' // 待接车
        } else if (item.status === 1) {
          status = 'processing' // 泊车中
        } else if (item.status === 2) {
          status = 'completed' // 已停妥
        } else if (item.status === 3) {
          status = 'processing' // 车主呼叫取车
        } else if (item.status === 4) {
          status = 'completed' // 已交还车主
        }
        
        const lot = parkingLots.value.find(l => l.value === item.parkingLotId?.toString())
        
        // 从停车记录表中查找车位号
        let parkedSpaceNo = ''
        if (item.plateNumber && parkingRecordMap.has(item.plateNumber)) {
          const record = parkingRecordMap.get(item.plateNumber)
          if (record && record.spaceNo) {
            parkedSpaceNo = record.spaceNo
          }
        }
        
        allOrders.push({
          id: item.id,
          orderNo: item.orderNo,
          plateNumber: item.plateNumber,
          serviceType: 'valet',
          serviceTypeName: '代泊',
          serviceItem: 'park',
          serviceItemName: '代泊',
          parkingLotName: lot?.label || '-',
          parkingLotAddress: lot?.address || '-',
          parkedSpaceNo: parkedSpaceNo,
          appointmentTime: item.appointmentTime || item.gmtCreate,
          status: status,
          fee: item.fee || 15,
          remark: parkedSpaceNo ? `车位: ${parkedSpaceNo}` : '',
          gmtCreate: item.gmtCreate
        })
      })
    }
    
    // 处理洗车订单
    if (carwashRes?.records) {
      carwashRes.records.forEach((item: any) => {
        let status = 'pending'
        if (item.status === 0) {
          status = 'pending' // 待清洗
        } else if (item.status === 1) {
          status = 'processing' // 清洗中
        } else if (item.status === 2) {
          status = 'completed' // 已完成质检
        }
        
        const lot = parkingLots.value.find(l => l.value === item.parkingLotId?.toString())
        
        const washTypeNames = ['', '外观普洗', '内外精洗', '打蜡美容']
        
        // 从停车记录表中查找车位号
        let parkedSpaceNo = ''
        if (item.plateNumber && parkingRecordMap.has(item.plateNumber)) {
          const record = parkingRecordMap.get(item.plateNumber)
          if (record && record.spaceNo) {
            parkedSpaceNo = record.spaceNo
          }
        }
        
        allOrders.push({
          id: item.id,
          orderNo: item.orderNo,
          plateNumber: item.plateNumber,
          serviceType: 'carwash',
          serviceTypeName: '洗车',
          serviceItem: item.washType?.toString(),
          serviceItemName: washTypeNames[item.washType] || '',
          washType: item.washType,
          washTypeName: washTypeNames[item.washType] || '',
          parkingLotName: lot?.label || '-',
          parkingLotAddress: lot?.address || '-',
          parkedSpaceNo: parkedSpaceNo,
          appointmentTime: item.appointmentTime || item.gmtCreate,
          status: status,
          fee: item.fee || 20,
          remark: parkedSpaceNo ? `车位: ${parkedSpaceNo}` : '',
          gmtCreate: item.gmtCreate
        })
      })
    }
    
    // 按创建时间倒序排序
    allOrders.sort((a, b) => new Date(b.gmtCreate).getTime() - new Date(a.gmtCreate).getTime())
    
    // 分页处理
    const start = (pagination.value.current - 1) * pagination.value.size
    const end = start + pagination.value.size
    tableData.value = allOrders.slice(start, end)
    pagination.value.total = allOrders.length
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  pagination.value.current = val
  fetchData()
}

const handleRefresh = () => {
  fetchData()
}

const handleApply = () => {
  formData.value = {
    serviceType: '',
    price: '',
    plateNumber: '',
    parkingLotId: '',
    parkedSpaceNo: '',
    appointmentTime: '',
    remark: ''
  }
  applyDialogVisible.value = true
}

const formatDateTime = (date: any) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const generateOrderId = () => {
  const prefix = 'VS'
  const now = new Date()
  const year = String(now.getFullYear()).slice(-2)
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hour = String(now.getHours()).padStart(2, '0')
  const minute = String(now.getMinutes()).padStart(2, '0')
  const timestamp = `${year}${month}${day}${hour}${minute}`
  const randomSuffix = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  return `${prefix}${timestamp}${randomSuffix}`
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  try {
    if (formData.value.serviceType === 'valet') {
      // 计算价格
      const priceResult = await PriceCalculator.calculatePrice('valet', parseInt(formData.value.parkingLotId))
      // 创建代泊工单
      const valetData: any = {
        orderNo: generateOrderId(),
        plateNumber: formData.value.plateNumber,
        parkingLotId: parseInt(formData.value.parkingLotId),
        appointmentTime: formatDateTime(formData.value.appointmentTime),
        fee: priceResult.finalPrice,
        status: 0
      }
      
      await valetOrderApi.addValetOrder(valetData)
    } else if (formData.value.serviceType === 'carwash') {
      // 计算价格
      const priceResult = await PriceCalculator.calculatePrice('carwash', parseInt(formData.value.parkingLotId))
      // 创建洗车工单
      const carwashData: any = {
        orderNo: generateOrderId(),
        plateNumber: formData.value.plateNumber,
        parkingLotId: parseInt(formData.value.parkingLotId),
        washType: 1, // 默认外观普洗
        appointmentTime: formatDateTime(formData.value.appointmentTime),
        fee: priceResult.finalPrice,
        status: 0
      }
      
      await carwashOrderApi.addCarwashOrder(carwashData)
    }
    
    ElMessage.success('申请提交成功')
    applyDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error('提交申请失败，请重试')
  }
}

const handleCancel = (row: ValetItem) => {
  ElMessageBox.confirm('确定要删除该申请吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      if (row.serviceType === 'valet') {
        await valetOrderApi.deleteValetOrder(row.id)
      } else if (row.serviceType === 'carwash') {
        await carwashOrderApi.deleteCarwashOrder(row.id)
      }
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除申请失败:', error)
      ElMessage.error('删除申请失败')
    }
  }).catch(() => {})
}

const handleEdit = (row: ValetItem) => {
  currentEditItem.value = { ...row }
  formData.value = {
    serviceType: row.serviceType,
    plateNumber: row.plateNumber,
    parkingLotId: parkingLots.value.find(l => l.label === row.parkingLotName)?.value || '',
    parkedSpaceNo: '',
    appointmentTime: row.appointmentTime,
    remark: row.remark || ''
  }
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  try {
    if (currentEditItem.value?.serviceType === 'valet') {
      const valetData: any = {
        id: currentEditItem.value.id,
        plateNumber: formData.value.plateNumber,
        parkingLotId: parseInt(formData.value.parkingLotId),
        appointmentTime: formatDateTime(formData.value.appointmentTime)
      }
      await valetOrderApi.updateValetOrder(valetData)
    } else if (currentEditItem.value?.serviceType === 'carwash') {
      const carwashData: any = {
        id: currentEditItem.value.id,
        plateNumber: formData.value.plateNumber,
        parkingLotId: parseInt(formData.value.parkingLotId),
        washType: currentEditItem.value.washType, // 保持原有值
        appointmentTime: formatDateTime(formData.value.appointmentTime)
      }
      await carwashOrderApi.updateCarwashOrder(carwashData)
    }
    
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('修改失败:', error)
    ElMessage.error('修改失败，请重试')
  }
}

const handleDetail = (row: ValetItem) => {
  currentDetail.value = row
  detailDialogVisible.value = true
}

// 跳转到车辆管理页面
const goToVehicleManagement = () => {
  applyDialogVisible.value = false
  router.push('/user/vehicle/my-vehicle')
}

const fetchVehicles = async () => {
  try {
    const userId = userStore.info?.userId
    if (!userId) {
      return
    }
    
    const res: any = await vehicleApi.fetchVehicleList({
      userId: userId
    })
    
    if (res?.records) {
      vehicles.value = res.records.map((item: any) => ({
        value: item.plateNumber,
        label: item.plateNumber
      }))
    }
  } catch (error) {
    console.error('获取车辆列表失败:', error)
  }
}

const fetchParkingLots = async () => {
  try {
    const res: any = await lotApi.fetchLotList({})
    
    if (res?.records) {
      parkingLots.value = res.records.map((item: any) => ({
        value: item.id.toString(),
        label: item.name,
        address: item.address
      }))
    }
  } catch (error) {
    console.error('获取停车场列表失败:', error)
  }
}

onMounted(() => {
  fetchData()
  fetchVehicles()
  fetchParkingLots()
})
</script>

<style scoped>
.user-valet {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
