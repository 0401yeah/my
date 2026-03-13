<template>
  <div class="user-record flex flex-col gap-4 pb-5">
    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <div class="card-title">个人记录</div>
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
        <template #type="{ row }">
          <ElTag v-if="row.type === 0" type="primary">临停缴费</ElTag>
          <ElTag v-else-if="row.type === 1" type="success">泊车服务</ElTag>
          <ElTag v-else-if="row.type === 2" type="warning">洗车服务</ElTag>
          <ElTag v-else-if="row.type === 3" type="info">月卡服务</ElTag>
          <ElTag v-else type="info">其他</ElTag>
        </template>

        <template #status="{ row }">
          <ElTag v-if="row.status === 0" type="warning">未支付</ElTag>
          <ElTag v-else-if="row.status === 1" type="success">已支付</ElTag>
          <ElTag v-else-if="row.status === 2" type="info">已退款</ElTag>
          <ElTag v-else-if="row.status === 3" type="danger">已取消</ElTag>
          <ElTag v-else type="info">未知</ElTag>
        </template>

        <template #operation="{ row }">
          <div class="flex gap-1">
            <ElButton
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handlePay(row)"
            >
              <ElIcon><Wallet /></ElIcon>
            </ElButton>
            <ElButton type="primary" link size="small" @click="handleDetail(row)">
              <ElIcon><Search /></ElIcon>
            </ElButton>
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <ElDialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <ElDescriptions :column="1" border v-if="currentDetail">
        <ElDescriptionsItem label="订单编号">{{ currentDetail.orderNo || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="车牌号">{{ currentDetail.plateNumber || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="订单类型">
          <ElTag v-if="currentDetail.type === 0" type="primary">临停缴费</ElTag>
          <ElTag v-else-if="currentDetail.type === 1" type="success">泊车服务</ElTag>
          <ElTag v-else-if="currentDetail.type === 2" type="warning">洗车服务</ElTag>
          <ElTag v-else-if="currentDetail.type === 3" type="info">月卡服务</ElTag>
          <ElTag v-else type="info">其他</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="订单状态">
          <ElTag v-if="currentDetail.status === 0" type="warning">未支付</ElTag>
          <ElTag v-else-if="currentDetail.status === 1" type="success">已支付</ElTag>
          <ElTag v-else-if="currentDetail.status === 2" type="info">已退款</ElTag>
          <ElTag v-else-if="currentDetail.status === 3" type="danger">已取消</ElTag>
          <ElTag v-else type="info">未知</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="订单金额">{{ currentDetail.amount || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="停车场">{{ currentDetail.parkManageName || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="车位号">{{ currentDetail.spaceNo || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="时间">{{ currentDetail.time || '-' }}</ElDescriptionsItem>
      </ElDescriptions>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>

    <!-- 支付弹窗 -->
    <ElDialog
      v-model="paymentDialogVisible"
      width="360px"
      center
      :show-close="false"
    >
      <div class="pt-0 px-4 pb-4">
        <div class="text-center mb-4 -mt-2">
          <h3 class="text-2xl font-bold text-gray-800 mb-1">确认支付</h3>
          <p class="text-sm text-gray-500">请核对您的订单信息并扫描二维码</p>
        </div>
        
        <div class="space-y-3 mb-4">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">产品项目</span>
            <span class="font-medium">{{ getServiceTypeName(currentPayItem?.type) }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId || currentPayItem?.orderNo }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-500">支付总额</span>
            <span class="text-2xl font-bold">{{ currentPayItem?.amount }}</span>
          </div>
        </div>
        
        <div class="flex flex-col items-center mb-3">
          <div>
            <div class="w-40 h-40 bg-white p-3 rounded-lg shadow-sm border border-gray-200 flex items-center justify-center">
              <QrcodeVue 
                :value="'这是一个支付二维码'" 
                :size="120"
                level="H"
                render-as="svg"
              />
            </div>
          </div>
          <p class="text-xs text-gray-500 mt-3 text-center">
            支持使用微信支付、支付宝或云闪付扫描
          </p>
        </div>
        
        <button 
          class="w-full bg-gray-900 hover:bg-gray-800 text-white py-3 rounded-lg font-medium transition-colors mb-3"
          @click="simulatePayment"
        >
          我已完成支付
        </button>
        
        <div class="text-center">
          <p class="text-xs text-gray-400">
            遇到问题？<span class="text-blue-500 hover:underline cursor-pointer">联系客服</span>
          </p>
        </div>
      </div>
    </ElDialog>
    
    <!-- 支付成功弹窗 -->
    <ElDialog
      v-model="paymentSuccessVisible"
      width="320px"
      center
      :show-close="false"
    >
      <div class="py-6 px-4 flex flex-col items-center">
        <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="#10b981" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
        </div>
        
        <h3 class="text-xl font-bold text-gray-800 mb-2">支付成功</h3>
        <p class="text-sm text-gray-500 mb-6 text-center">
          您的订单已支付成功，感谢您的使用！
        </p>
        
        <div class="w-full bg-gray-50 rounded-lg p-4 mb-6">
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">套餐名称</span>
            <span class="font-medium">{{ getServiceTypeName(currentPayItem?.type) }}</span>
          </div>
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">支付金额</span>
            <span class="font-bold text-green-600">{{ currentPayItem?.amount }}</span>
          </div>
        </div>
        
        <button 
          class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition-colors"
          @click="paymentSuccessVisible = false; handleRefresh()"
        >
          确定
        </button>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElIcon } from 'element-plus'
import QrcodeVue from 'qrcode.vue'
import { orderApi, recordApi } from '@/api/business'
import { formatTime } from '@/utils/time'
import { Wallet, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'UserRecord' })

interface RecordItem {
  id: number
  orderNo: string
  type: number
  amount: string
  time: string
  status: number
  plateNumber: string
  parkManageName: string
  spaceNo: string
  source?: string
}

const tableRef = ref()
const loading = ref(false)
const tableData = ref<RecordItem[]>([])
const detailDialogVisible = ref(false)
const currentDetail = ref<RecordItem | null>(null)
const paymentDialogVisible = ref(false)
const paymentSuccessVisible = ref(false)
const currentPayItem = ref<RecordItem | null>(null)
const currentOrderId = ref('')
const userStore = useUserStore()

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = ref([
  { type: 'globalIndex' as const, width: 60, label: '序号' },
  { prop: 'orderNo', label: '订单编号', width: 180 },
  { prop: 'plateNumber', label: '车牌号', width: 120 },
  { prop: 'type', label: '记录类型', width: 120, useSlot: true },
  { prop: 'parkManageName', label: '停车场', width: 250 },
  { prop: 'spaceNo', label: '车位号', width: 120 },
  { prop: 'amount', label: '金额', width: 100 },
  { prop: 'time', label: '时间', minWidth: 180 },
  { prop: 'status', label: '状态', width: 100, useSlot: true },
  { prop: 'operation', label: '操作', width: 100, useSlot: true, fixed: 'right' as const }
])

const columnChecks = ref([])

const getServiceTypeName = (type?: number) => {
  const typeMap: Record<number, string> = {
    0: '临停缴费',
    1: '泊车服务',
    2: '洗车服务',
    3: '月卡服务'
  }
  return typeMap[type || 0] || '其他'
}

const handleDetail = (row: RecordItem) => {
  currentDetail.value = row
  detailDialogVisible.value = true
}

const handlePay = (row: RecordItem) => {
  currentPayItem.value = row
  paymentDialogVisible.value = true
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

const simulatePayment = async () => {
  if (!currentPayItem.value) {
    ElMessage.error('请选择订单')
    return
  }
  
  try {
    if (currentPayItem.value.source === 'parking_record') {
      await recordApi.updateRecordPaymentStatus(currentPayItem.value.id, 1)
    } else {
      await orderApi.updateOrderStatus(currentPayItem.value.id, 1)
    }
    currentOrderId.value = generateOrderId()
    paymentDialogVisible.value = false
    paymentSuccessVisible.value = true
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const [orderResponse, recordResponse] = await Promise.all([
      orderApi.fetchOrderDetailList({
        current: pagination.value.current,
        size: pagination.value.size
      }),
      recordApi.fetchRecordList({ current: 1, size: 100 })
    ])

    console.log('订单明细数据:', orderResponse)
    console.log('停车记录数据:', recordResponse)

    const spaceNoMap: Record<number, string> = {}
    if (recordResponse && recordResponse.records) {
      recordResponse.records.forEach((record: any) => {
        if (record.id && record.spaceNo) {
          spaceNoMap[record.id] = record.spaceNo
        }
      })
    }

    if (orderResponse && orderResponse.records) {
      console.log('订单原始数据:', orderResponse.records)
      if (orderResponse.records.length > 0) {
        console.log('第一条记录所有字段:', Object.keys(orderResponse.records[0]))
        console.log('第一条记录完整数据:', orderResponse.records[0])
      }
      tableData.value = orderResponse.records.map((item: any) => ({
        id: item.id,
        orderNo: item.orderNo || '-',
        type: item.orderType || 0,
        amount: `¥${Number(item.payAmount || item.amount || 0).toFixed(2)}`,
        time: formatTime(item.payTime || item.gmtCreate || item.gmtInto || '') || '-',
        status: item.status || 0,
        plateNumber: item.plateNumber || '-',
        parkManageName: item.parkManageName || item.parkingLotName || '-',
        spaceNo: item.spaceNo || item.space || item.parkingSpace || item.parkingSpaceNo || item.spaceNumber || item.parkSpace || spaceNoMap[item.recordId] || spaceNoMap[item.id] || '-',
        source: item.source
      }))
      pagination.value.total = orderResponse.total || 0
    } else {
      tableData.value = []
      pagination.value.total = 0
    }
  } catch (error) {
    console.error('获取记录失败', error)
    tableData.value = []
    pagination.value.total = 0
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-record {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
