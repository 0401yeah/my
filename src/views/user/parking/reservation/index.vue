<template>
  <div class="user-reservation flex flex-col gap-4 pb-5">
    <!-- 车位分布图界面 -->
    <ElCard class="flex-1" shadow="never" style="margin-top: 0">
      <!-- 车位分布图 -->
      <div v-if="parkingSpaces.length > 0" class="parking-spaces-container">
        <!-- 停车场标题、选择器和图例 -->
        <div class="parking-header">
          <div class="header-left">
            <h3 class="parking-title">{{ getParkingLotName() }} - 车位分布图</h3>
            <div class="parking-selector">
              <ElSelect 
            v-model="formData.parkingLotId" 
            placeholder="请选择停车场" 
            style="width: 200px" 
            @change="handleParkingLotChange"
          >
            <ElOption 
              v-for="lot in parkingLots" 
              :key="lot.id" 
              :label="lot.name" 
              :value="lot.id.toString()" 
            />
          </ElSelect>
            </div>
          </div>
          <div class="parking-legend">
            <div class="legend-item">
              <span class="legend-dot available"></span>
              <span>空闲</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot occupied"></span>
              <span>占用</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot reserved"></span>
              <span>预约</span>
            </div>
            <div class="legend-item">
              <span class="legend-icon charging"></span>
              <span>充电桩</span>
            </div>
          </div>
        </div>
        
        <!-- 车位网格 -->
        <div class="parking-grid">
          <div
            v-for="space in parkingSpaces"
            :key="space.id"
            class="parking-space"
            :class="{
              'available': space.status === 'available',
              'reserved': space.status === 'reserved',
              'occupied': space.status === 'occupied'
            }"
            @click="handleSpaceClick(space)"
          >
            <div v-if="space.status === 'occupied'" class="occupied-space">
              <div class="car-icon">
                <i class="el-icon-car"></i>
              </div>
              <div class="space-number">{{ space.number }}</div>
            </div>
            <div v-else class="space-number">{{ space.number }}</div>
            <div v-if="space.isCharging" class="charging-icon">
              ⚡
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-gray-400 py-10">
        请先选择停车场
      </div>
    </ElCard>

    <!-- 预约对话框 -->
    <ElDialog 
      v-model="reserveDialogVisible" 
      title="预约停车" 
      width="450px"
      :close-on-click-modal="false"
    >
      <ElForm 
        ref="formRef" 
        :model="formData" 
        :rules="formRules" 
        label-width="100px"
      >
        <ElFormItem label="车牌号" prop="plateNumber">
          <ElSelect 
            v-model="formData.plateNumber" 
            placeholder="请选择车辆" 
            style="width: 100%"
          >
            <ElOption 
              v-for="vehicle in userVehicles" 
              :key="vehicle.id" 
              :label="vehicle.plateNumber" 
              :value="vehicle.plateNumber" 
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="选择车位" prop="parkingSpaceId">
          <ElInput 
            v-model="selectedSpaceNumber" 
            disabled 
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="备注" prop="remark">
          <ElInput 
            v-model="formData.remark" 
            type="textarea" 
            placeholder="请输入备注信息" 
            :rows="3"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="reserveDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">提交预约</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { spaceApi, lotApi, reservationApi, vehicleApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'UserReservation' })

interface ParkingSpace {
  id: string
  number: string
  status: 'available' | 'reserved' | 'occupied'
  isCharging: boolean
}

const formRef = ref<any>(null)
const reserveDialogVisible = ref(false)
const parkingSpaces = ref<ParkingSpace[]>([])
const parkingLots = ref<any[]>([])
const selectedSpaceNumber = ref('')
const userStore = useUserStore()
const userVehicles = ref<any[]>([])

const formData = ref({
  plateNumber: '',
  parkingLotId: '1',
  parkingSpaceId: '',
  remark: ''
})

const formRules = {
  plateNumber: [{ required: true, message: '请选择车辆', trigger: 'change' }],
  parkingLotId: [{ required: true, message: '请选择停车场', trigger: 'change' }],
  parkingSpaceId: [{ required: true, message: '请选择车位', trigger: 'change' }]
}

// 车位相关计算属性
const availableSpaces = computed(() => {
  return parkingSpaces.value.filter(space => space.status === 'available').length
})

// 获取停车场名称
const getParkingLotName = () => {
  const lot = parkingLots.value.find((lot: any) => lot.id.toString() === formData.value.parkingLotId)
  return lot ? lot.name : '停车场'
}

const generateParkingSpaces = (count: number) => {
  const spaces: ParkingSpace[] = []
  // 计算充电桩数量（10%）
  const chargingCount = Math.floor(count * 0.1)
  for (let i = 1; i <= count; i++) {
    // 随机生成车位状态，模拟真实情况
    const statuses: Array<'available' | 'reserved' | 'occupied'> = ['available', 'reserved', 'occupied']
    const randomStatus = statuses[Math.floor(Math.random() * statuses.length)]
    // 从1号车位开始，前10%的车位作为充电桩
    const isCharging = i <= chargingCount
    spaces.push({
      id: `space-${i}`,
      number: `A${i.toString().padStart(3, '0')}`,
      status: randomStatus,
      isCharging
    })
  }
  return spaces
}

const handleParkingLotChange = async (parkingLotId: string) => {
  if (!parkingLotId) {
    ElMessage.warning('请选择有效的停车场')
    return
  }
  
  try {
    console.log('开始获取车位数据，停车场ID:', parkingLotId)
    // 调用API获取车位列表
    const response: any = await spaceApi.fetchSpaceList({ parkingLotId })
    console.log('API响应:', response)
    
    // 检查响应格式
    if (response && response.records && response.records.length > 0) {
      // 转换API返回的数据格式
      // 数据库字段：spaceNo(车位编号), status(0:空闲 1:占用 2:预约 3:维护)
      const totalSpaces = response.records.length
      const chargingCount = Math.floor(totalSpaces * 0.1)
      parkingSpaces.value = response.records.map((space: any, index: number) => {
        // 状态映射：0:空闲 1:占用 2:预约 3:维护
        let status: 'available' | 'reserved' | 'occupied' = 'available'
        if (space.status === 0) {
          status = 'available'
        } else if (space.status === 1) {
          status = 'occupied'
        } else if (space.status === 2) {
          status = 'reserved'
        } else {
          status = 'occupied' // 维护状态也显示为占用
        }
        
        return {
          id: space.id?.toString() || `space-${index}`,
          number: space.spaceNo || (index + 1).toString().padStart(3, '0'),
          status: status,
          isCharging: index < chargingCount // 前10%的车位为充电桩
        }
      })
      console.log('转换后的车位数据:', parkingSpaces.value)
    } else {
      console.warn('API返回数据为空')
      // 不使用模拟数据，清空车位数据
      parkingSpaces.value = []
    }
  } catch (error: any) {
    console.error('获取车位数据时发生错误:', error)
    ElMessage.error('获取车位数据失败: ' + (error.message || '未知错误'))
    // 不使用模拟数据，清空车位数据
    parkingSpaces.value = []
  } finally {
    formData.value.parkingSpaceId = ''
    selectedSpaceNumber.value = ''
  }
}

const handleSpaceClick = (space: ParkingSpace) => {
  if (space.status === 'available') {
    formData.value.parkingSpaceId = space.id
    selectedSpaceNumber.value = space.number
    reserveDialogVisible.value = true
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  try {
    // 计算过期时间（预约后15分钟自动取消）
    const expireTime = new Date(Date.now() + 15 * 60 * 1000) // 15分钟后过期
    
    // 构造预约数据
    const reservationData = {
      parkingLotId: parseInt(formData.value.parkingLotId),
      parkingSpaceId: parseInt(formData.value.parkingSpaceId),
      plateNumber: formData.value.plateNumber,
      expireTime: expireTime
    }
    
    // 调用后端 API 接口
    const response = await reservationApi.create(reservationData)
    // 由于后端返回的是 Result 类型，直接显示成功消息
    ElMessage.success('预约提交成功，请在15分钟内到达')
    reserveDialogVisible.value = false
    // 重置表单
    formData.value = {
      plateNumber: '',
      parkingLotId: formData.value.parkingLotId, // 保持当前停车场选择
      parkingSpaceId: '',
      remark: ''
    }
    selectedSpaceNumber.value = ''
    // 重新加载车位数据，更新车位状态
    await handleParkingLotChange(formData.value.parkingLotId)
  } catch (error: any) {
    console.error('预约提交失败:', error)
    ElMessage.error('预约提交失败: ' + (error.message || '未知错误'))
  }
}

// 获取停车场列表
const fetchParkingLots = async () => {
  try {
    const response: any = await lotApi.fetchLotList({})
    if (response && response.records) {
      parkingLots.value = response.records.filter((lot: any) => lot.status === 1)
    }
  } catch (error) {
    console.error('获取停车场列表失败:', error)
    ElMessage.error('获取停车场列表失败')
  }
}

// 获取用户车辆列表
const fetchUserVehicles = async () => {
  try {
    const userId = userStore.info?.userId
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const response: any = await vehicleApi.fetchVehicleList({ userId })
    if (response && response.records) {
      userVehicles.value = response.records
    }
  } catch (error) {
    console.error('获取车辆列表失败:', error)
    // 不使用模拟数据，保持空数组
    userVehicles.value = []
  }
}

// 组件挂载时获取默认停车场的车位数据
onMounted(async () => {
  await fetchParkingLots()
  await fetchUserVehicles()
  await handleParkingLotChange(formData.value.parkingLotId)
})
</script>

<style scoped>
.user-reservation {
  padding: 20px;
  min-height: 100vh;
  background-color: #f0f2f5;
}

/* 停车场选择器 */
.parking-selector {
  margin-bottom: 20px;
}

/* 车位选择 */
.parking-spaces-container {
  max-height: 600px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 20px;
  background-color: #ffffff;
}

/* 停车场标题和图例 */
.parking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.parking-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.parking-title::before {
  content: "🅿️";
  font-size: 20px;
}

.parking-selector {
  margin: 0;
}

.parking-legend {
  display: flex;
  gap: 20px;
  align-items: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-dot.available {
  background-color: #67c23a;
}

.legend-dot.occupied {
  background-color: #f56c6c;
}

.legend-dot.reserved {
  background-color: #e6a23c;
}

.legend-dot.charging {
  background-color: #409eff;
  position: relative;
}

.legend-dot.charging::after {
  content: "⚡";
  font-size: 8px;
  position: absolute;
  top: -2px;
  right: -2px;
}

.legend-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.legend-icon.charging::after {
  content: "⚡";
  font-size: 14px;
  color: #e6a23c;
}

.parking-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 6px;
}

.parking-space {
  width: 100%;
  aspect-ratio: 1;
  max-width: 70px;
  max-height: 70px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid #e4e7ed;
  box-sizing: border-box;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.parking-space:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.parking-space.available {
  background-color: #e6f7ee;
  color: #303133;
  border-color: #b3e6cc;
}

.parking-space.reserved {
  background-color: #fff7e6;
  color: #303133;
  border-color: #ffd993;
}

.parking-space.occupied {
  background-color: #fde2e2;
  color: #303133;
  border-color: #f8a3a3;
  cursor: not-allowed;
}

.space-number {
  font-size: 14px;
  font-weight: bold;
}

.car-icon {
  font-size: 24px;
  color: #909399;
}

.occupied-space {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.occupied-space .space-number {
  font-size: 12px;
  color: #909399;
}

.charging-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 16px;
  color: #e6a23c;
  transform: rotate(0deg);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .parking-grid {
    grid-template-columns: repeat(8, 1fr);
  }
  
  .space-number {
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  .parking-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .parking-legend {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .parking-grid {
    grid-template-columns: repeat(6, 1fr);
  }
  
  .space-number {
    font-size: 10px;
  }
  
  .car-icon {
    font-size: 20px;
  }
  
  .charging-icon {
    font-size: 12px;
  }
}
</style>
