<template>
  <div class="user-charge flex flex-col gap-4 pb-5">
    <div class="flex gap-4">
      <ElCard class="flex-1 charge-panel">
        <template #header>
          <div class="panel-header">
            <span class="panel-title">选择充电站</span>
          </div>
        </template>
        <div class="station-list">
          <div v-if="loading" class="loading-state">
            <ElIcon class="is-loading"><Loading /></ElIcon>
            <span>加载中...</span>
          </div>
          <div v-else-if="stations.length === 0" class="empty-state">
            <ElIcon><Close /></ElIcon>
            <span>暂无充电站</span>
          </div>
          <div 
            v-for="station in stations" 
            :key="station.id"
            class="station-item"
            :class="{ active: selectedStation?.id === station.id }"
            @click="selectStation(station)"
          >
            <div class="station-info">
              <div class="station-name">{{ station.name }}</div>
              <div class="station-address">{{ station.address }}</div>
              <div class="station-meta">
                <span class="distance">{{ station.distance }}km</span>
                <span class="divider">·</span>
                <span class="available">{{ station.availablePiles }}/{{ station.totalPiles }}空闲</span>
              </div>
            </div>
            <ElIcon class="arrow-icon"><ArrowRight /></ElIcon>
          </div>
        </div>
      </ElCard>

      <ElCard class="flex-1 charge-panel">
        <template #header>
          <div class="panel-header">
            <span class="panel-title">选择充电桩</span>
            <span class="station-select" v-if="selectedStation">{{ selectedStation.name }}</span>
          </div>
        </template>
        <div class="pile-grid">
          <div v-if="loadingPiles" class="loading-state">
            <ElIcon class="is-loading"><Loading /></ElIcon>
            <span>加载中...</span>
          </div>
          <div v-else-if="currentPiles.length === 0" class="empty-tip">
            <ElIcon><Close /></ElIcon>
            <span>{{ selectedStation ? '暂无充电桩' : '请先选择充电站' }}</span>
          </div>
          <div 
            v-for="pile in currentPiles" 
            :key="pile.id"
            class="pile-item"
            :class="{ 
              active: selectedPile?.id === pile.id,
              available: pile.status === 'available',
              occupied: pile.status === 'occupied',
              fault: pile.status === 'fault'
            }"
            @click="selectPile(pile)"
          >
            <div class="pile-no">{{ pile.no }}</div>
            <div class="pile-power">
              <ElTag :type="pile.powerType === 'super' ? 'danger' : 'primary'" size="small">
                {{ pile.powerType === 'super' ? '超充' : '快充' }}
              </ElTag>
            </div>
            <div class="pile-power-value">{{ pile.power }}kW</div>
            <div class="pile-status">
              <span v-if="pile.status === 'available'" class="status-text available-text">空闲</span>
              <span v-else-if="pile.status === 'occupied'" class="status-text occupied-text">使用中</span>
              <span v-else-if="pile.status === 'fault'" class="status-text fault-text">故障</span>
            </div>
          </div>
        </div>
      </ElCard>

      <ElCard class="flex-1 charge-panel">
        <template #header>
          <div class="panel-header">
            <span class="panel-title">充电信息</span>
          </div>
        </template>
        <div v-if="selectedPile" class="charge-info">
          <div class="pile-detail">
            <div class="pile-detail-header">
              <span class="pile-detail-no">{{ selectedPile.no }}</span>
              <span class="pile-detail-status">
                <ElTag :type="selectedPile.status === 'available' ? 'success' : 'warning'" size="small">
                  {{ selectedPile.status === 'available' ? '可使用' : '使用中' }}
                </ElTag>
              </span>
            </div>
            <div class="pile-detail-info">
              <div class="info-row">
                <span class="info-label">充电桩功率</span>
                <span class="info-value">{{ selectedPile.power }}kW {{ selectedPile.powerType === 'super' ? '超充' : '快充' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">充电费率</span>
                <span class="info-value">¥{{ selectedPile.rate }}/kWh</span>
              </div>
              <div class="info-row">
                <span class="info-label">服务费</span>
                <span class="info-value">¥0.50/kWh</span>
              </div>
            </div>
          </div>

          <div class="divider-line"></div>

          <div class="account-info">
            <div class="info-row">
              <span class="info-label">账户余额</span>
              <span class="info-value balance">¥{{ userBalance }}</span>
            </div>
          </div>

          <div class="divider-line"></div>

          <div class="gun-info">
            <div class="gun-check">
              <ElIcon class="check-icon" :class="{ connected: gunConnected }">
                <CircleCheck v-if="gunConnected" />
                <CircleClose v-else />
              </ElIcon>
              <span class="gun-status">{{ gunConnected ? '已插枪' : '请插枪' }}</span>
            </div>
            <ElButton 
              type="primary" 
              size="small" 
              @click="toggleGun"
              :text="gunConnected"
            >
              {{ gunConnected ? '拔枪' : '插枪' }}
            </ElButton>
          </div>

          <ElButton 
            type="primary" 
            class="start-charge-btn"
            :disabled="!gunConnected || selectedPile.status !== 'available' || startingCharge"
            @click="startCharge"
            :loading="startingCharge"
          >
            {{ startingCharge ? '启动中...' : '开始充电' }}
          </ElButton>
        </div>
        <div v-else class="empty-tip">
          <ElIcon><Close /></ElIcon>
          <span>请先选择充电桩</span>
        </div>
      </ElCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElIcon, ElLoading } from 'element-plus'
import { ArrowRight, CircleCheck, CircleClose, Loading, Close } from '@element-plus/icons-vue'
import { chargeApi, lotApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'UserCharge' })

interface Station {
  id: number
  name: string
  address: string
  distance: number
  availablePiles: number
  totalPiles: number
}

interface Pile {
  id: number
  no: string
  power: number
  powerType: 'quick' | 'super'
  status: 'available' | 'occupied' | 'fault'
  rate: number
}

const userStore = useUserStore()

const loading = ref(false)
const loadingPiles = ref(false)
const startingCharge = ref(false)
const stations = ref<Station[]>([])
const piles = ref<Pile[]>([])
const selectedStation = ref<Station | null>(null)
const selectedPile = ref<Pile | null>(null)
const gunConnected = ref(false)
const userBalance = ref(128.50)

const currentPiles = computed(() => {
  return piles.value
})

const fetchStations = async () => {
  try {
    loading.value = true
    const response = await lotApi.fetchLotList({})
    if (response && response.records) {
      // 将停车场列表转换为充电站格式
      stations.value = response.records.map((lot: any) => ({
        id: lot.id,
        name: lot.name,
        address: lot.address,
        distance: 0.5, // 默认距离
        availablePiles: 5, // 默认可用充电桩数
        totalPiles: 10 // 默认总充电桩数
      }))
    }
  } catch (error) {
    console.error('获取停车场列表失败:', error)
    ElMessage.error('获取停车场列表失败，请重试')
  } finally {
    loading.value = false
  }
}

const fetchPiles = async (stationId: number) => {
  try {
    loadingPiles.value = true
    // 这里应该调用API获取停车场的车位数量
    // 为了演示，我们假设每个停车场有100个车位
    const totalSpaces = 100
    const tenPercent = Math.ceil(totalSpaces * 0.1)
    
    // 生成前10%的充电车位
    const generatedPiles = []
    for (let i = 1; i <= tenPercent; i++) {
      generatedPiles.push({
        id: i,
        no: `P${i.toString().padStart(3, '0')}`,
        power: i % 2 === 0 ? 120 : 60,
        powerType: i % 2 === 0 ? 'super' : 'quick',
        status: i % 3 === 0 ? 'occupied' : i % 4 === 0 ? 'fault' : 'available',
        rate: i % 2 === 0 ? 1.5 : 1.2
      })
    }
    
    piles.value = generatedPiles
  } catch (error) {
    console.error('获取充电桩列表失败:', error)
    ElMessage.error('获取充电桩列表失败，请重试')
  } finally {
    loadingPiles.value = false
  }
}

const selectStation = async (station: Station) => {
  selectedStation.value = station
  selectedPile.value = null
  await fetchPiles(station.id)
}

const selectPile = (pile: Pile) => {
  if (pile.status === 'fault') {
    ElMessage.warning('该充电桩故障，无法选择')
    return
  }
  selectedPile.value = pile
  gunConnected.value = false
}

const toggleGun = () => {
  gunConnected.value = !gunConnected.value
  if (gunConnected.value) {
    ElMessage.success('已插枪')
  }
}

const startCharge = async () => {
  if (!selectedPile.value) {
    ElMessage.warning('请先选择充电桩')
    return
  }
  if (!gunConnected.value) {
    ElMessage.warning('请先插枪')
    return
  }
  
  try {
    startingCharge.value = true
    const response = await chargeApi.startCharge({
      pileId: selectedPile.value.id,
      stationId: selectedStation.value?.id
    })
    ElMessage.success('开始充电！')
    
    // 重置状态
    gunConnected.value = false
    selectedPile.value = null
  } catch (error) {
    console.error('开始充电失败:', error)
    ElMessage.error('开始充电失败，请重试')
  } finally {
    startingCharge.value = false
  }
}

onMounted(() => {
  fetchStations()
})
</script>

<style scoped>
.user-charge {
  padding: 20px;
  height: calc(100vh - 120px);
}

.charge-panel {
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
}

.station-select {
  font-size: 14px;
  color: #909399;
}

.station-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 450px;
  overflow-y: auto;
  padding: 4px;
}

.station-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.station-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.station-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.station-info {
  flex: 1;
}

.station-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.station-address {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.station-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.distance {
  color: #409eff;
}

.divider {
  color: #dcdfe6;
}

.available {
  color: #67c23a;
}

.arrow-icon {
  color: #c0c4cc;
}

.pile-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-height: 450px;
  overflow-y: auto;
  padding: 4px;
}

.pile-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 8px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.pile-item:hover {
  border-color: #409eff;
}

.pile-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.pile-item.available {
  border-color: #67c23a;
}

.pile-item.available:hover {
  background-color: #f0f9ff;
}

.pile-item.available.active {
  background-color: #ecf5ff;
}

.pile-item.occupied {
  opacity: 0.6;
  cursor: not-allowed;
}

.pile-item.fault {
  opacity: 0.4;
  cursor: not-allowed;
}

.pile-no {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.pile-power {
  margin-bottom: 4px;
}

.pile-power-value {
  font-size: 14px;
  color: #606266;
  margin-bottom: 6px;
}

.pile-status {
  font-size: 12px;
}

.status-text.available-text {
  color: #67c23a;
}

.status-text.occupied-text {
  color: #e6a23c;
}

.status-text.fault-text {
  color: #f56c6c;
}

.charge-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pile-detail {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.pile-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pile-detail-no {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.pile-detail-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 14px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.info-value.balance {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 700;
}

.divider-line {
  height: 1px;
  background-color: #e4e7ed;
}

.account-info {
  padding: 12px;
}

.gun-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
}

.gun-check {
  display: flex;
  align-items: center;
  gap: 8px;
}

.check-icon {
  font-size: 24px;
  color: #909399;
}

.check-icon.connected {
  color: #67c23a;
}

.gun-status {
  font-size: 14px;
  color: #606266;
}

.start-charge-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.loading-state {
  text-align: center;
  color: #409eff;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.empty-state {
  text-align: center;
  color: #909399;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.empty-state .el-icon {
  font-size: 32px;
  color: #c0c4cc;
}
</style>