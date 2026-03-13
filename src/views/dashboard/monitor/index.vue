<template>
  <div class="h-screen bg-gray-50 text-gray-800 p-6 font-sans overflow-hidden">
    <!-- 主体内容区域 -->
    <div class="max-w-7xl mx-auto h-full">

      
      <!-- 沉浸式车位地图布局 -->
      <div class="flex gap-6 h-full animate-in fade-in duration-500">
        <div class="w-80 flex flex-col gap-6 shrink-0">
          <!-- 统计数据卡片 (紧凑模式) -->
          <div class="grid grid-cols-2 gap-2">
            <ElCard shadow="hover" class="border-none rounded-xl overflow-hidden">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">总车位</p>
                  <p class="text-2xl font-bold text-gray-900">{{ totalSpaces }}</p>
                </div>
                <div class="bg-blue-50 p-2 rounded-lg text-blue-600">
                  <Icon icon="icon-park:car" />
                </div>
              </div>
            </ElCard>
            <ElCard shadow="hover" class="border-none rounded-xl overflow-hidden">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">剩余空位</p>
                  <p class="text-2xl font-bold text-emerald-600">{{ availableSpaces }}</p>
                </div>
                <div class="bg-emerald-50 p-2 rounded-lg text-emerald-600">
                  <Icon icon="icon-park:check" />
                </div>
              </div>
            </ElCard>
            <ElCard shadow="hover" class="border-none rounded-xl overflow-hidden">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">已占用</p>
                  <p class="text-2xl font-bold text-rose-600">{{ occupiedSpaces }}</p>
                </div>
                <div class="bg-rose-50 p-2 rounded-lg text-rose-600">
                  <Icon icon="icon-park:close" />
                </div>
              </div>
            </ElCard>
            <ElCard shadow="hover" class="border-none rounded-xl overflow-hidden">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-gray-500 text-sm">已预约</p>
                  <p class="text-2xl font-bold text-amber-600">{{ reservedSpaces }}</p>
                </div>
                <div class="bg-amber-50 p-2 rounded-lg text-amber-600">
                  <Icon icon="icon-park:timer" />
                </div>
              </div>
            </ElCard>
          </div>

          <!-- 摄像头 -->
          <div class="grid gap-4">
            <div
              v-for="camera in cameras"
              :key="camera.id"
              class="rounded-xl overflow-hidden shadow-sm"
            >
              <!-- 摄像头画面 -->
              <div class="w-full aspect-video bg-black rounded-lg overflow-hidden relative">
                <video 
                  :ref="el => el && setCameraVideo(el, camera.id)" 
                  class="w-full h-full object-cover"
                  autoplay 
                  muted 
                  playsinline
                ></video>
                
                <!-- 右上角控制元素 -->
                <div class="absolute top-2 right-2 flex items-center gap-1.5">
                  <!-- 下拉菜单：选择摄像头 -->
                  <el-select 
                    v-model="camera.selectedDeviceId" 
                    @change="(deviceId) => switchCamera(camera.id, deviceId)" 
                    size="small" 
                    placeholder="选择摄像头"
                    class="bg-transparent text-white text-xs"
                    style="width: 100px;"
                  >
                    <el-option label="不开启" value="" />
                    <el-option 
                      v-for="device in availableCameras" 
                      :key="device.deviceId" 
                      :label="device.label" 
                      :value="device.deviceId"
                    />
                  </el-select>

                  <!-- 上传按钮 -->
                  <button
                    class="bg-transparent w-6 h-6 flex items-center justify-center text-white/80 hover:text-white hover:bg-white/10 rounded transition-all duration-200 ease-in-out transform hover:scale-110 active:scale-95"
                    @click="uploadPlate(camera.id)"
                    title="上传车牌照"
                  >
                    <Icon icon="icon-park:upload" size="14" class="transition-transform duration-300 hover:rotate-12" />
                  </button>
                </div>
              </div>
            </div>
          </div>


        </div>
        <div class="flex-1">
          <ElCard shadow="hover" class="border-none rounded-xl overflow-hidden h-full">
            <div class="p-4 border-b border-gray-100 flex flex-wrap justify-between items-center bg-gray-50/50 rounded-t-xl gap-4">
              <h3 class="text-gray-900 font-semibold flex items-center gap-2">
                  <Icon icon="icon-park:map" class="text-blue-600" /> {{ parkingLots.find(lot => lot.id === currentParkingLot)?.name || '停车场' }} - 车位分布图
              </h3>
              <div class="flex items-center gap-4">
                <ElSelect v-model="currentParkingLot" @change="handleParkingLotChange" size="small" style="width: 150px">
                  <ElOption v-for="lot in parkingLots" :key="lot.id" :label="lot.name" :value="lot.id" />
                </ElSelect>
                <div class="flex gap-4 text-sm">
                  <span class="flex items-center gap-1 text-gray-600">
                    <div class="w-3 h-3 rounded-full bg-emerald-400"></div> 空闲
                  </span>
                  <span class="flex items-center gap-1 text-gray-600">
                    <div class="w-3 h-3 rounded-full bg-gray-300"></div> 占用
                  </span>
                  <span class="flex items-center gap-1 text-gray-600">
                    <div class="w-3 h-3 rounded-full bg-amber-400"></div> 预约
                  </span>
                  <span class="flex items-center gap-1 text-gray-600">
                    <Icon icon="icon-park:lightning" class="text-yellow-500" /> 充电桩
                  </span>
                </div>
              </div>
            </div>
            <div class="p-4 flex-1 overflow-auto" style="max-height: calc(100vh - 200px);">
              <div class="grid grid-cols-10 gap-2 min-w-[600px]">
                <div
                  v-for="spot in parkingSpots"
                  :key="spot.id"
                  class="relative aspect-square rounded-md flex flex-col items-center justify-center text-xs font-mono font-medium border cursor-pointer transition-all hover:scale-105"
                  :class="{
                    'bg-emerald-50 border-emerald-200 text-emerald-600': spot.status === 'free',
                    'bg-gray-100 border-gray-200 text-gray-500': spot.status === 'occupied',
                    'bg-amber-50 border-amber-200 text-amber-600': spot.status === 'reserved'
                  }"
                  :title="`${spot.spaceNo || spot.id} - ${spot.status === 'free' ? '空闲' : spot.status === 'occupied' ? '占用' : '预约'}${spot.plateNumber ? ' (' + spot.plateNumber + ')' : ''}${spot.isCharging ? ' (充电桩)' : ''}`"
                >
                  <span class="font-bold">{{ spot.spaceNo || spot.id }}</span>
                  <span v-if="spot.status === 'occupied' && spot.plateNumber" class="text-[10px] text-gray-400 mt-0.5 truncate w-full text-center px-1">
                    {{ spot.plateNumber }}
                  </span>
                  
                  <!-- 渲染充电桩闪电图标 -->
                  <div v-if="spot.isCharging" class="absolute top-1 right-1">
                    <Icon icon="icon-park:lightning" size="12" class="text-yellow-500" />
                  </div>
                </div>
              </div>
            </div>
          </ElCard>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onUnmounted, computed } from 'vue'
  import { Icon } from '@iconify/vue'
  import {
    ElCard,
    ElButton,
    ElTable,
    ElTableColumn,
    ElSelect,
    ElOption,
    ElMessage
  } from 'element-plus'
  import { dashboardApi, lotApi, plateApi } from '@/api/business'

  defineOptions({ name: 'ParkingMonitorPage' })

  // 当前时间
  const currentTime = ref('')
  
  // 加载状态
  const loading = ref(false)
  
  // 停车场数据
  interface ParkingLot {
    id: number
    name: string
  }
  
  const parkingLots = ref<ParkingLot[]>([])
  
  // 当前选中的停车场
  const currentParkingLot = ref<number | null>(null)
  
  // 统计数据
  const totalSpaces = ref(0)
  const availableSpaces = ref(0)
  const occupiedSpaces = computed(() => totalSpaces.value - availableSpaces.value)
  const reservedSpaces = ref(0)

  // 车位数据
  interface ParkingSpot {
    id: string
    spaceNo: string
    status: 'free' | 'occupied' | 'reserved'
    isCharging?: boolean
    plateNumber?: string
  }

  const parkingSpots = ref<ParkingSpot[]>([])



  // 摄像头数据
  interface Camera {
    id: number
    name: string
    type: 'entry' | 'exit'
    selectedDeviceId?: string
  }

  const cameras = ref<Camera[]>([])

  // 可用摄像头设备
  const availableCameras = ref<{ deviceId: string; label: string }[]>([])

  // 获取停车场列表
  const fetchParkingLots = async () => {
    try {
      const data: any = await lotApi.fetchLotList({})
      if (data && data.records) {
        parkingLots.value = data.records.map((item: any) => ({
          id: item.id,
          name: item.name
        }))
        if (parkingLots.value.length > 0 && !currentParkingLot.value) {
          currentParkingLot.value = parkingLots.value[0].id
          fetchMonitorData()
        }
      } else {
        // 如果没有停车场数据，使用默认数据
        parkingLots.value = [
          { id: 1, name: '泰州学院南门地下车库' },
          { id: 2, name: '泰州学院行政楼地面车场' },
          { id: 3, name: '泰州万达广场地下停车场' }
        ]
        currentParkingLot.value = 1
        fetchMonitorData()
      }
    } catch (error) {
      // 发生错误时使用默认数据
      parkingLots.value = [
        { id: 1, name: '泰州学院南门地下车库' },
        { id: 2, name: '泰州学院行政楼地面车场' },
        { id: 3, name: '泰州万达广场地下停车场' }
      ]
      currentParkingLot.value = 1
      fetchMonitorData()
    }
  }

  // 获取监控数据
  const fetchMonitorData = async () => {
    if (!currentParkingLot.value) return
    
    loading.value = true
    try {
      const data: any = await dashboardApi.fetchMonitorData(currentParkingLot.value)
      if (data && typeof data === 'object') {
        totalSpaces.value = data.totalSpaces || 0
        availableSpaces.value = data.availableSpaces || 0
        reservedSpaces.value = 0
        
        // 转换车位数据
        parkingSpots.value = (data.parkingSpots || []).map((spot: any) => ({
          id: spot.id || spot.spaceNo,
          spaceNo: spot.spaceNo || spot.id,
          status: spot.status === 'occupied' ? 'occupied' : 'free',
          isCharging: spot.isCharging,
          plateNumber: spot.plateNumber
        }))
      } else {
        // 数据格式错误，不使用模拟数据
        totalSpaces.value = 0
        availableSpaces.value = 0
        parkingSpots.value = []
      }
    } catch (error) {
      console.error('获取监控数据失败:', error)
      // 发生错误时不使用模拟数据
      totalSpaces.value = 0
      availableSpaces.value = 0
      parkingSpots.value = []
    } finally {
      loading.value = false
    }
  }







  // 处理停车场切换
  const handleParkingLotChange = (lotId: number) => {
    currentParkingLot.value = lotId
    fetchMonitorData()
  }

  // 摄像头视频元素引用
  const cameraVideos = ref<Map<number, HTMLVideoElement>>(new Map())

  // 设置摄像头视频
  const setCameraVideo = (el: Element | ComponentPublicInstance | null, cameraId: number) => {
    if (el && el instanceof HTMLVideoElement && !cameraVideos.value.has(cameraId)) {
      cameraVideos.value.set(cameraId, el)
      startCamera(cameraId, el)
    }
  }

  // 启动摄像头
  const startCamera = async (cameraId: number, videoElement: HTMLVideoElement) => {
    try {
      // 检查浏览器是否支持
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        console.warn('浏览器不支持摄像头')
        return
      }
      
      // 请求摄像头权限
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: true,
        audio: false 
      })
      
      // 将视频流设置到video元素
      videoElement.srcObject = stream
    } catch (error) {
      // 失败时显示错误信息
      console.error('无法访问摄像头:', error)
    }
  }

  // 停止所有摄像头
  const stopAllCameras = () => {
    cameraVideos.value.forEach((videoElement) => {
      if (videoElement.srcObject) {
        const stream = videoElement.srcObject as MediaStream
        stream.getTracks().forEach(track => track.stop())
      }
    })
    cameraVideos.value.clear()
  }

  // 枚举可用摄像头设备
  const enumerateCameras = async () => {
    try {
      const devices = await navigator.mediaDevices.enumerateDevices()
      const videoDevices = devices.filter(device => device.kind === 'videoinput')
      
      availableCameras.value = videoDevices.map(device => ({
        deviceId: device.deviceId,
        label: device.label || `摄像头 ${videoDevices.indexOf(device) + 1}`
      }))
    } catch (error) {
      // 忽略错误
    }
  }

  // 切换摄像头
  const switchCamera = async (cameraId: number, deviceId: string) => {
    const videoElement = cameraVideos.value.get(cameraId)
    if (!videoElement) return
    
    // 停止当前视频流
    if (videoElement.srcObject) {
      const stream = videoElement.srcObject as MediaStream
      stream.getTracks().forEach(track => track.stop())
    }
    
    // 启动新的摄像头
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { deviceId: { exact: deviceId } },
        audio: false
      })
      videoElement.srcObject = stream
    } catch (error) {
      // 忽略错误
    }
  }

  // 停止摄像头
  const stopCamera = (cameraId: number) => {
    const videoElement = cameraVideos.value.get(cameraId)
    if (!videoElement) return
    
    // 停止当前视频流
    if (videoElement.srcObject) {
      const stream = videoElement.srcObject as MediaStream
      stream.getTracks().forEach(track => track.stop())
      videoElement.srcObject = null
    }
  }

  // 刷新摄像头
  const refreshCamera = async (cameraId: number) => {
    const videoElement = cameraVideos.value.get(cameraId)
    if (!videoElement) return
    
    // 停止当前视频流
    if (videoElement.srcObject) {
      const stream = videoElement.srcObject as MediaStream
      stream.getTracks().forEach(track => track.stop())
    }
    
    // 重新启动摄像头
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: false
      })
      videoElement.srcObject = stream
    } catch (error) {
      // 忽略错误
    }
  }

  // 上传车牌照
  const uploadPlate = async (cameraId: number) => {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/*'
    input.capture = 'camera'
    
    input.onchange = async (e) => {
      const target = e.target as HTMLInputElement
      if (target.files && target.files[0]) {
        const file = target.files[0]
        
        // 检查文件大小（4MB限制）
        const maxSize = 4 * 1024 * 1024 // 4MB
        if (file.size > maxSize) {
          ElMessage.error(`图片大小不能超过4MB，当前大小：${(file.size / 1024 / 1024).toFixed(2)}MB`)
          return
        }
        
        try {
          const result: any = await plateApi.uploadPlate(file, currentParkingLot.value || 1)
          
          if (result && result.plateNumber) {
            const plateNumber = result.plateNumber
            
            try {
              // 检查是否已在场内（有停车中的记录）
              // 暂时简化逻辑，直接调用入场API，由后端处理重复入场的情况
              const entryResult: any = await plateApi.vehicleEntry(plateNumber, currentParkingLot.value || 1)
              
              if (entryResult) {
                if (entryResult.releasedSpace) {
                  // 车辆已在场内，自动执行了出场操作
                  ElMessage.success(`车牌识别成功：${plateNumber}，车辆出场成功，停留时间：${entryResult.stayMinutes || 0}分钟`)
                } else {
                  // 车辆入场成功
                  const msg = entryResult.spaceNo 
                    ? `车牌识别成功：${plateNumber}，分配车位：${entryResult.spaceNo}`
                    : `车牌识别成功：${plateNumber}，车辆入场成功`
                  ElMessage.success(msg)
                }
                fetchMonitorData()
              }
            } catch (entryError: any) {
              ElMessage.error('车辆操作失败：' + (entryError.message || '未知错误'))
            }
          } else {
            ElMessage.error('未识别到车牌号')
          }
        } catch (error: any) {
          ElMessage.error('上传失败：' + (error.message || '未知错误'))
        }
      }
    }
    
    input.click()
  }

  // 组件卸载时停止摄像头
  onUnmounted(() => {
    if (timeInterval) {
      clearInterval(timeInterval)
    }
    if (refreshInterval) {
      clearInterval(refreshInterval)
    }
    stopAllCameras()
  })

  // 更新当前时间
  const updateCurrentTime = () => {
    const now = new Date()
    currentTime.value = now.toTimeString().split(' ')[0]
  }

  // 定时刷新数据
  let refreshInterval: ReturnType<typeof setInterval> | null = null
  let timeInterval: ReturnType<typeof setInterval> | null = null

  // 组件挂载时初始化
  onMounted(async () => {
    // 初始化时间
    updateCurrentTime()
    
    // 初始化摄像头数据
    cameras.value = [
      { id: 1, name: '入口1', type: 'entry' },
      { id: 2, name: '出口1', type: 'exit' }
    ]
    
    // 枚举可用摄像头
    await enumerateCameras()
    
    // 获取停车场列表
    fetchParkingLots()
    
    // 每秒更新时间
    timeInterval = setInterval(updateCurrentTime, 1000)
    
    // 每30秒刷新数据
    refreshInterval = setInterval(() => {
      fetchMonitorData()
    }, 30000)
  })
</script>

<style scoped>
  /* 扫描线动画 */
  @keyframes scan {
    0% { top: 0%; opacity: 0; }
    10% { opacity: 1; }
    90% { opacity: 1; }
    100% { top: 100%; opacity: 0; }
  }

  /* 淡入动画 */
  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
  }

  .animate-in {
    animation: fadeIn 0.5s ease-in-out;
  }

  .fade-in {
    animation-name: fadeIn;
  }

  .duration-500 {
    animation-duration: 0.5s;
  }

  /* 滚动条样式 */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }

  ::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  ::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }

  ::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }

  /* 响应式设计 */
  @media (max-width: 1200px) {
    .grid-cols-10 {
      grid-template-columns: repeat(8, minmax(0, 1fr));
    }
    
    .w-80 {
      width: 200px;
    }
  }

  @media (max-width: 768px) {
    .flex {
      flex-direction: column;
    }
    
    .w-80 {
      width: 100%;
      margin-bottom: 20px;
    }
    
    .h-\[calc\(100vh-120px\)\] {
      height: auto;
      min-height: 600px;
    }
    
    .grid-cols-10 {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }
    
    .grid-cols-2 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
  }
  
  @media (max-width: 480px) {
    .grid-cols-10 {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    
    .grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
</style>