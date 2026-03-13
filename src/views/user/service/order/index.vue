<template>
  <div class="user-service-order flex flex-col gap-4 pb-5">
    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <div class="card-title">服务订单</div>
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
        <template #orderInfo="{ row }">
          <div>
            <div class="font-medium">{{ row.orderNo }}</div>
            <div class="text-xs text-gray-400">{{ row.serviceName }}</div>
          </div>
        </template>

        <template #serviceType="{ row }">
          <ElTag v-if="row.serviceType === 'parking'" type="primary">停车服务</ElTag>
          <ElTag v-else-if="row.serviceType === 'valet'" type="success">代泊服务</ElTag>
          <ElTag v-else-if="row.serviceType === 'monthly'" type="warning">月租服务</ElTag>
          <ElTag v-else type="info">其他</ElTag>
        </template>

        <template #amount="{ row }">
          <span class="text-red-500 font-bold">¥{{ row.amount }}</span>
        </template>

        <template #status="{ row }">
          <ElTag v-if="row.status === 'pending'" type="warning">待支付</ElTag>
          <ElTag v-else-if="row.status === 'paid'" type="primary">已支付</ElTag>
          <ElTag v-else-if="row.status === 'completed'" type="success">已完成</ElTag>
          <ElTag v-else-if="row.status === 'cancelled'" type="info">已取消</ElTag>
          <ElTag v-else type="danger">已退款</ElTag>
        </template>

        <template #operation="{ row }">
          <div class="flex gap-2">
            <ElButton v-if="row.status === 'pending' && row.amount > 0" type="primary" link size="small" @click="handlePay(row)">
              支付
            </ElButton>
            <ElButton type="info" link size="small" @click="handleDetail(row)">
              详情
            </ElButton>
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <!-- 支付弹窗 -->
    <ElDialog
      v-model="paymentDialogVisible"
      title="订单支付"
      width="360px"
      center
      :show-close="false"
    >
      <div class="py-4 px-4">
        <!-- 标题 -->
        <div class="text-center mb-4">
          <h3 class="text-xl font-bold text-gray-800 mb-1">确认支付</h3>
          <p class="text-sm text-gray-500">请核对您的订单信息并扫描二维码</p>
        </div>
        
        <!-- 订单信息 -->
        <div class="space-y-3 mb-4">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">产品项目</span>
            <span class="font-medium">{{ currentPayItem?.serviceName }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId || generateOrderId() }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-500">支付总额</span>
            <span class="text-2xl font-bold">¥{{ currentPayItem?.amount }}</span>
          </div>
        </div>
        
        <!-- 二维码 -->
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
        
        <!-- 完成支付按钮 -->
        <button 
          class="w-full bg-gray-900 hover:bg-gray-800 text-white py-3 rounded-lg font-medium transition-colors mb-3"
          @click="simulatePayment"
        >
          我已完成支付
        </button>
        
        <!-- 客服提示 -->
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
        <!-- 成功图标 -->
        <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="#10b981" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
        </div>
        
        <!-- 成功信息 -->
        <h3 class="text-xl font-bold text-gray-800 mb-2">支付成功</h3>
        <p class="text-sm text-gray-500 mb-6 text-center">
          您的订单已支付成功，感谢您的使用！
        </p>
        
        <!-- 订单信息 -->
        <div class="w-full bg-gray-50 rounded-lg p-4 mb-6">
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">套餐名称</span>
            <span class="font-medium">{{ currentPayItem?.serviceName }}</span>
          </div>
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">支付金额</span>
            <span class="font-bold text-green-600">¥{{ currentPayItem?.amount }}</span>
          </div>
        </div>
        
        <!-- 确认按钮 -->
        <button 
          class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition-colors"
          @click="paymentSuccessVisible = false; handleRefresh()"
        >
          确定
        </button>
      </div>
    </ElDialog>

    <ElDialog v-model="detailDialogVisible" title="订单详情" width="500px">
      <ElDescriptions :column="1" border>
        <ElDescriptionsItem label="订单号">{{ currentDetail?.orderNo }}</ElDescriptionsItem>
        <ElDescriptionsItem label="服务名称">{{ currentDetail?.serviceName }}</ElDescriptionsItem>
        <ElDescriptionsItem label="服务类型">
          <ElTag v-if="currentDetail?.serviceType === 'parking'" type="primary">停车服务</ElTag>
          <ElTag v-else-if="currentDetail?.serviceType === 'valet'" type="success">代泊服务</ElTag>
          <ElTag v-else-if="currentDetail?.serviceType === 'monthly'" type="warning">月租服务</ElTag>
          <ElTag v-else type="info">其他</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="车牌号">{{ currentDetail?.plateNumber }}</ElDescriptionsItem>
        <ElDescriptionsItem label="停车场">{{ currentDetail?.parkingLotName }}</ElDescriptionsItem>
        <ElDescriptionsItem label="订单金额">
          <span class="text-red-500 font-bold">¥{{ currentDetail?.amount }}</span>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="状态">
          <ElTag v-if="currentDetail?.status === 'pending'" type="warning">待支付</ElTag>
          <ElTag v-else-if="currentDetail?.status === 'paid'" type="primary">已支付</ElTag>
          <ElTag v-else-if="currentDetail?.status === 'completed'" type="success">已完成</ElTag>
          <ElTag v-else-if="currentDetail?.status === 'cancelled'" type="info">已取消</ElTag>
          <ElTag v-else type="danger">已退款</ElTag>
        </ElDescriptionsItem>
        <ElDescriptionsItem label="支付方式">{{ currentDetail?.payMethod || '未支付' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="支付时间">{{ currentDetail?.payTime || '未支付' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="创建时间">{{ currentDetail?.gmtCreate }}</ElDescriptionsItem>
      </ElDescriptions>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import QrcodeVue from 'qrcode.vue'
import { vasOrderApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'UserServiceOrder' })

interface ServiceOrderItem {
  id: number
  orderNo: string
  serviceName: string
  serviceType: string
  plateNumber: string
  parkingLotName: string
  amount: number
  status: string
  payMethod: string
  payTime: string
  gmtCreate: string
}

const tableRef = ref()
const loading = ref(false)
const tableData = ref<ServiceOrderItem[]>([])
const detailDialogVisible = ref(false)
const currentDetail = ref<ServiceOrderItem | null>(null)

const paymentDialogVisible = ref(false)
const paymentSuccessVisible = ref(false)
const currentPayItem = ref<ServiceOrderItem | null>(null)
const currentOrderId = ref('')
const userStore = useUserStore()

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = ref([
  { type: 'globalIndex' as const, width: 60, label: '序号' },
  { prop: 'orderInfo', label: '订单信息', minWidth: 200, useSlot: true },
  { prop: 'serviceType', label: '服务类型', width: 120, useSlot: true },
  { prop: 'plateNumber', label: '车牌号', width: 120 },
  { prop: 'parkingLotName', label: '停车场', width: 150 },
  { prop: 'amount', label: '金额', width: 100, useSlot: true },
  { prop: 'status', label: '状态', width: 100, useSlot: true },
  { prop: 'gmtCreate', label: '创建时间', width: 180 },
  { prop: 'operation', label: '操作', width: 120, useSlot: true, fixed: 'right' as const }
])

const columnChecks = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await vasOrderApi.fetchVasOrderList({
      current: pagination.value.current,
      size: pagination.value.size
    })
    if (res && res.records) {
      tableData.value = res.records.map((item: any) => ({
        id: item.id,
        orderNo: item.orderNo,
        serviceName: item.body,
        serviceType: item.orderType === 3 ? 'monthly' : item.orderType === 1 ? 'valet' : 'parking',
        plateNumber: item.plateNumber,
        parkingLotName: '中心停车场', // 实际应该从API获取
        amount: item.payAmount,
        status: item.status === 0 ? 'pending' : item.status === 1 ? 'paid' : item.status === 2 ? 'completed' : 'cancelled',
        payMethod: item.payWay === 0 ? '微信支付' : item.payWay === 1 ? '支付宝' : '',
        payTime: item.payTime || '',
        gmtCreate: item.gmtCreate
      }))
      pagination.value.total = res.total || 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
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

const handlePay = (row: ServiceOrderItem) => {
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
    const currentUserId = userStore.info?.userId || 5
    const orderData = {
      plateNumber: currentPayItem.value.plateNumber,
      orderType: currentPayItem.value.serviceType === 'monthly' ? 3 : currentPayItem.value.serviceType === 'valet' ? 1 : 0,
      body: currentPayItem.value.serviceName,
      payAmount: currentPayItem.value.amount,
      originalAmount: currentPayItem.value.amount,
      discountAmount: 0,
      status: 1,
      payWay: Math.floor(Math.random() * 2),
      orderNo: generateOrderId(),
      parkingLotId: 0,
      userId: currentUserId
    }
    
    const res = await vasOrderApi.addVasOrder(orderData)
    
    if (res) {
      currentOrderId.value = (res as any)?.orderId || (res as any)?.id || generateOrderId()
      paymentDialogVisible.value = false
      paymentSuccessVisible.value = true
    } else {
      ElMessage.error('创建订单失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  }
}

const handleDetail = (row: ServiceOrderItem) => {
  currentDetail.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-service-order {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
