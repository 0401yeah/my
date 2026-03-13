<template>
  <div class="order-page">
    <art-table-header :title="'订单明细查询'" :show-back="false">
      <template #right>
        <el-button type="primary" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出Excel
        </el-button>
      </template>
    </art-table-header>

    <art-search-bar
      :model="searchForm"
      :items="searchItems"
      :loading="searchLoading"
      @search="handleSearch"
      @reset="handleReset"
      :show-time-range="true"
      :time-range-label="'订单时间'"
      v-model:start-time="searchForm.startTime"
      v-model:end-time="searchForm.endTime"
    />

    <el-card class="mt-4">
      <art-table
        v-loading="tableLoading"
        :columns="columns"
        :data="tableData"
        :total="pagination.total"
        :page-size="pagination.pageSize"
        :current-page="pagination.currentPage"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :border="true"
      >
        <template #operation="{ row }">
          <div class="flex items-center">
            <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
            <ArtButtonTable type="refund" :row="row" @click="handleRefund(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>
      </art-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ currentOrder.licensePlate }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ getOrderTypeText(currentOrder.type) }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusTagType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ Number(currentOrder.amount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ Number(currentOrder.discountFee || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ Number(currentOrder.amount - (currentOrder.discountFee || 0)).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ getPaymentMethodText(currentOrder.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="停车场">{{ currentOrder.parkManageName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入场时间">{{ formatTime(currentOrder.gmtInto) }}</el-descriptions-item>
        <el-descriptions-item label="出场时间">{{ formatTime(currentOrder.gmtOut) }}</el-descriptions-item>
        <el-descriptions-item label="停留时间">{{ currentOrder.stayMinutes ? `${currentOrder.stayMinutes}分钟` : '-' }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.orderTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑订单对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑订单" width="500px">
      <el-form :model="editForm" label-width="100px" v-if="editForm">
        <el-form-item label="订单编号">
          <el-input v-model="editForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="车牌号">
          <el-input v-model="editForm.licensePlate" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="editForm.status" placeholder="请选择订单状态">
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input-number v-model="editForm.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="优惠金额">
          <el-input-number v-model="editForm.discountFee" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 退款确认对话框 -->
    <el-dialog v-model="refundDialogVisible" title="订单退款" width="500px">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="订单编号">
          <el-input v-model="refundForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="车牌号">
          <el-input v-model="refundForm.licensePlate" disabled />
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input :value="`¥${Number(refundForm.amount || 0).toFixed(2)}`" disabled />
        </el-form-item>
        <el-form-item label="退款原因">
          <el-select v-model="refundForm.reason" placeholder="请选择退款原因">
            <el-option label="重复扣费" value="重复扣费" />
            <el-option label="系统故障" value="系统故障" />
            <el-option label="用户申请" value="用户申请" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款说明">
          <el-input v-model="refundForm.remark" type="textarea" :rows="3" placeholder="请输入退款说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitRefund" :loading="refundLoading">确认退款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { Download, View, Refresh } from '@element-plus/icons-vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { orderApi, lotApi } from '@/api/business'
  import { formatTime } from '@/utils/time'

  interface Order {
    id: number
    orderNo: string
    licensePlate: string
    parkManageId: string
    parkManageName: string
    type: number
    amount: number
    originalFee: number
    discountFee: number
    status: number
    paymentMethod: number
    orderTime: string
    payTime: string
    remark: string
    gmtInto: string
    gmtOut: string
    stayMinutes: number
  }

  const searchForm = ref({
    orderNo: '',
    licensePlate: '',
    type: '',
    status: '',
    paymentMethod: '',
    startTime: '',
    endTime: ''
  })

  const searchLoading = ref(false)
  const tableLoading = ref(false)

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
      key: 'licensePlate',
      label: '车牌号',
      type: 'input',
      props: {
        placeholder: '请输入车牌号'
      }
    },
    {
      key: 'type',
      label: '订单类型',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '临停缴费', value: '0' },
        { label: '泊车服务', value: '1' },
        { label: '洗车服务', value: '2' }
      ]
    },
    {
      key: 'status',
      label: '订单状态',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '未支付', value: '0' },
        { label: '已支付', value: '1' }
      ]
    },
    {
      key: 'paymentMethod',
      label: '支付方式',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '微信支付', value: '1' },
        { label: '支付宝', value: '2' },
        { label: '现金', value: '3' }
      ]
    }
  ])

  const columns = computed<any[]>(() => [
    {
      label: '序号',
      type: 'globalIndex',
      width: 80,
      align: 'center',
      fixed: 'left'
    },
    {
      prop: 'orderNo',
      label: '订单编号',
      minWidth: 200
    },
    {
      prop: 'licensePlate',
      label: '车牌号',
      minWidth: 120
    },

    {
      prop: 'type',
      label: '订单类型',
      minWidth: 100,
      formatter: (row: any) => getOrderTypeText(row.type)
    },
    {
      prop: 'amount',
      label: '金额',
      minWidth: 100,
      formatter: (row: any) => `¥${Number(row.amount || 0).toFixed(2)}`
    },
    {
      prop: 'status',
      label: '订单状态',
      minWidth: 100,
      formatter: (row: any) => getStatusText(row.status)
    },
    {
      prop: 'paymentMethod',
      label: '支付方式',
      minWidth: 120,
      formatter: (row: any) => getPaymentMethodText(row.paymentMethod)
    },
    {
      prop: 'parkManageName',
      label: '停车场',
      minWidth: 150,
      formatter: (row: any) => row.parkManageName || '-'
    },
    {
      prop: 'gmtInto',
      label: '入场时间',
      minWidth: 180,
      formatter: (row: any) => formatTime(row.gmtInto)
    },
    {
      prop: 'gmtOut',
      label: '出场时间',
      minWidth: 180,
      formatter: (row: any) => formatTime(row.gmtOut)
    },
    {
      prop: 'stayMinutes',
      label: '停留时间(分钟)',
      minWidth: 120,
      formatter: (row: any) => row.stayMinutes || '-'
    },
    {
      prop: 'orderTime',
      label: '下单时间',
      minWidth: 180,
      formatter: (row: any) => formatTime(row.orderTime)
    },
    {
      prop: 'operation',
      label: '操作',
      width: 150,
      useSlot: true,
      fixed: 'right'
    }
  ])

  const tableData = ref<Order[]>([])

  const pagination = ref({
    currentPage: 1,
    pageSize: 10,
    total: 0
  })

  // 停车场列表和映射
  const parkingLots = ref<any[]>([])
  const parkingLotMap = ref<Record<number, string>>({})

  // 详情对话框
  const detailDialogVisible = ref(false)
  const currentOrder = ref<Order | null>(null)

  // 编辑对话框
  const editDialogVisible = ref(false)
  const editLoading = ref(false)
  const editForm = ref<Order | null>(null)

  // 退款对话框
  const refundDialogVisible = ref(false)
  const refundLoading = ref(false)
  const refundForm = ref({
    id: 0,
    orderNo: '',
    licensePlate: '',
    amount: 0,
    reason: '',
    remark: ''
  })

  // 辅助函数
  const getOrderTypeText = (type: number) => {
    const typeMap: Record<number, string> = {
      0: '临停缴费',
      1: '泊车服务',
      2: '洗车服务',
      3: '月卡服务'
    }
    return typeMap[type] || '未知'
  }

  const getStatusText = (status: number) => {
    const statusMap: Record<number, string> = {
      0: '未支付',
      1: '已支付',
      2: '已退款',
      3: '已取消'
    }
    return statusMap[status] || '未知'
  }

  const getStatusTagType = (status: number) => {
    const typeMap: Record<number, 'warning' | 'success' | 'info' | 'danger'> = {
      0: 'warning',
      1: 'success',
      2: 'info',
      3: 'danger'
    }
    return typeMap[status] || 'info'
  }

  const getPaymentMethodText = (method: number) => {
    const methodMap: Record<number, string> = {
      0: '微信',
      1: '支付宝',
      2: '现金',
      3: '免费抵扣'
    }
    return methodMap[method] || '-'  }

  // 获取停车场列表并创建映射
  const fetchParkingLots = async () => {
    try {
      const response: any = await lotApi.fetchLotList({})
      if (response && response.records) {
        parkingLots.value = response.records
        // 创建停车场ID到名称的映射
        parkingLotMap.value = {}
        response.records.forEach((lot: any) => {
          parkingLotMap.value[lot.id] = lot.name
        })
      }
    } catch (error) {
      console.error('获取停车场列表失败:', error)
    }
  }

  const handleSearch = async () => {
    searchLoading.value = true
    tableLoading.value = true

    try {
      const params = {
        current: pagination.value.currentPage,
        size: pagination.value.pageSize,
        orderNo: searchForm.value.orderNo,
        plateNumber: searchForm.value.licensePlate,
        orderType: searchForm.value.type ? parseInt(searchForm.value.type) : null,
        status: searchForm.value.status ? parseInt(searchForm.value.status) : null
      }

      const response: any = await orderApi.fetchOrderDetailList(params)

      if (response && response.records) {
        tableData.value = response.records.map((item: any) => {
          // 检查item中所有可能的停车场名称字段
          let parkingLotName = item.parkingLotName || item.parkManageName || item.parkName || item.lotName || item.park || item.parkingLot || item.lot || ''
          
          // 如果没有名称，但有停车场ID，则从映射中获取
          if (!parkingLotName && item.parkingLotId) {
            parkingLotName = parkingLotMap.value[item.parkingLotId] || ''
          }
          
          return {
            id: item.id,
            orderNo: item.orderNo || '',
            licensePlate: item.plateNumber || '',
            type: item.orderType || 0,
            amount: item.payAmount || item.totalFee || 0,
            originalFee: item.originalAmount || item.originalFee || item.originalCost || 0,
            discountFee: item.discountAmount || item.discountFee || 0,
            status: item.status || 0,
            paymentMethod: item.payWay || 0,
            orderTime: item.gmtCreate || '',
            payTime: item.payTime || '',
            remark: item.remark || '',
            parkManageName: parkingLotName,
            gmtInto: item.gmtInto || '',
            gmtOut: item.gmtOut || '',
            stayMinutes: item.stayMinutes || 0
          }
        })
        pagination.value.total = response.total || 0
      } else {
        tableData.value = []
        pagination.value.total = 0
        ElMessage.error('获取订单列表失败')
      }
    } catch (error) {
      tableData.value = []
      pagination.value.total = 0
      ElMessage.error('网络错误，请稍后重试')
    } finally {
      searchLoading.value = false
      tableLoading.value = false
    }
  }

  const handleReset = () => {
    searchForm.value = {
      orderNo: '',
      licensePlate: '',
      type: '',
      status: '',
      paymentMethod: '',
      startTime: '',
      endTime: ''
    }
    pagination.value.currentPage = 1
    handleSearch()
  }

  const handleSizeChange = (size: number) => {
    pagination.value.pageSize = size
    handleSearch()
  }

  const handleCurrentChange = (current: number) => {
    pagination.value.currentPage = current
    handleSearch()
  }

  const handleExport = () => {
  ElMessage.success('导出成功')
}

  // 查看详情
  const handleDetail = (row: Order) => {
    currentOrder.value = row
    detailDialogVisible.value = true
  }

  // 编辑订单
  const handleEdit = (row: Order) => {
    editForm.value = { ...row }
    editDialogVisible.value = true
  }

  const submitEdit = async () => {
    if (!editForm.value) return

    editLoading.value = true
    try {
      await orderApi.updateOrder({
        id: editForm.value.id,
        plateNumber: editForm.value.licensePlate,
        status: editForm.value.status,
        totalFee: editForm.value.amount,
        discountFee: editForm.value.discountFee,
        remark: editForm.value.remark
      })
      ElMessage.success('订单更新成功')
      editDialogVisible.value = false
      handleSearch()
    } catch (error) {
      ElMessage.error('更新订单失败')
    } finally {
      editLoading.value = false
    }
  }

  // 退款
  const handleRefund = (row: Order) => {
    refundForm.value = {
      id: row.id,
      orderNo: row.orderNo,
      licensePlate: row.licensePlate,
      amount: row.amount,
      reason: '',
      remark: ''
    }
    refundDialogVisible.value = true
  }

  const submitRefund = async () => {
    if (!refundForm.value.reason) {
      ElMessage.warning('请选择退款原因')
      return
    }

    refundLoading.value = true
    try {
      await orderApi.updateOrderStatus(refundForm.value.id, 2)
      ElMessage.success('退款成功')
      refundDialogVisible.value = false
      handleSearch()
    } catch (error) {
      ElMessage.error('退款失败')
    } finally {
      refundLoading.value = false
    }
  }

  const handleDelete = (row: Order) => {
    ElMessageBox.confirm('确定要删除该订单吗？', '删除订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }).then(async () => {
      try {
        await orderApi.deleteOrder(row.id)
        ElMessage.success('删除成功')
        handleSearch()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
  }

  onMounted(async () => {
    // 先获取停车场列表，创建映射
    await fetchParkingLots()
    // 再获取订单列表
    handleSearch()
  })

  defineOptions({ name: 'Order' })
</script>

<style scoped>
  .order-page {
    padding: 20px;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
</style>