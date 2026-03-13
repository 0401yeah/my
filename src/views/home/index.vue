<template>
  <div class="home-container">
    <!-- 内容主网格 -->
    <div class="content-grid">
      
      <!-- 左侧：实时停放状态 -->
      <div class="parking-status">
        <div class="status-card flex flex-col">
          
          <!-- 有车辆在场的情况 -->
          <div v-if="hasVehicle" class="flex flex-col h-full">
            <!-- 装饰背景 -->
            <div class="decorative-bg"></div>

            <div class="status-header">
              <h3 class="status-title">
                <span class="status-indicator"></span>
                实时停放状态
              </h3>
              <span class="status-tag">计费中</span>
            </div>

            <div class="status-content flex-1">
              <!-- 顶部车牌号 -->
              <div class="plate-number-section">
                <div class="plate-number-left">
                  <p class="plate-label">PLATE NUMBER</p>
                  <p class="plate-number">{{ activeParkingRecord?.plateNumber || '未知车辆' }}</p>
                </div>
                <div class="shield-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                    <path d="m9 12 2 2 4-4"></path>
                  </svg>
                </div>
              </div>

              <!-- 详情信息 -->
              <div class="info-list">
                <div class="info-item">
                  <div class="info-item-left">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                      <circle cx="12" cy="10" r="3"></circle>
                    </svg>
                    <span class="info-label">停车场名称</span>
                  </div>
                  <span class="info-value">{{ activeParkingRecord?.parkingLotName || '未知停车场' }}</span>
                </div>
                
                <div class="info-item">
                  <div class="info-item-left">
                    <div class="dot-indicator"></div>
                    <span class="info-label blue-label">当前停车位</span>
                  </div>
                  <span class="info-value blue-value">{{ activeParkingRecord?.spaceNo || '暂无车位' }}</span>
                </div>
                
                <div class="info-item">
                  <div class="info-item-left">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                    <span class="info-label">入场时间</span>
                  </div>
                  <span class="info-value">{{ currentEntryTime }}</span>
                </div>
              </div>

              <!-- 分隔线 -->
              <div class="divider"></div>

              <!-- 底部时长和费用 -->
              <div class="bottom-info">
                <div class="bottom-item">
                  <span class="bottom-label">已停时长</span>
                  <span class="bottom-time">{{ currentTime }}</span>
                </div>
                <div class="bottom-item">
                  <span class="bottom-label">当前费用</span>
                  <div class="bottom-amount">
                    <span class="currency">¥</span>
                    <span class="amount">{{ currentCost }}</span>
                  </div>
                </div>
              </div>

              <!-- 分隔线 -->
              <div class="divider"></div>

              <!-- 一键求助按钮 -->
              <button class="help-button">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 0 .45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 0 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                </svg>
                一键求助
              </button>

              <!-- 底部文字 -->
              <p class="powered-by">POWERED BY SMART PARKING SYSTEM</p>
            </div>
          </div>

          <!-- 无车辆在场的情况 -->
          <div v-else class="no-vehicle-state flex-1 flex flex-col items-start justify-center">
            <!-- 装饰背景 -->
            <div class="decorative-bg"></div>

            <!-- 蓝色横线 -->
            <div class="w-12 h-1 bg-blue-500 mb-8"></div>
            
            <!-- 标题 -->
            <h3 class="text-3xl font-bold text-gray-900 mb-4">车辆已离场</h3>
            
            <!-- 描述 -->
            <p class="text-gray-500 mb-10 max-w-md">
              目前所有关联车辆均在场外。点击下方按钮可以为您寻找目的地周边的最优空位。
            </p>
            
            <!-- 主要按钮 -->
            <button class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-lg font-semibold transition-all shadow-lg flex items-center gap-2" @click="handleActionClick('/user/parking/reservation')">
              去预约新停车位
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M5 12h14"></path>
                <path d="m12 5 7 7-7 7"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧：功能按钮与记录 -->
      <div class="side-content">
        
        <!-- 四个快捷入口 -->
        <div class="action-grid">
          <div 
            v-for="(item, index) in actions" 
            :key="index"
            class="action-item"
            :class="item.borderClass"
            @click="handleActionClick(item.route)"
          >
            <div class="action-icon" :class="[item.bgClass, item.colorClass]">
              <ArtSvgIcon :icon="item.icon" :size="20" />
            </div>
            <span class="action-label">{{ item.label }}</span>
          </div>
        </div>

        <!-- 消费记录列表 -->
        <div class="records-card">
          <div class="records-header">
            <h4 class="records-title">最近消费流水</h4>
            <button class="records-more" @click="handleActionClick('/user/vehicle/record')">查看全部</button>
          </div>
          
          <div class="records-list">
            <div 
              v-for="record in records" 
              :key="record.id"
              class="record-item"
            >
              <div class="record-info">
                <div class="record-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"></path>
                    <circle cx="7" cy="17" r="2"></circle>
                    <circle cx="17" cy="17" r="2"></circle>
                  </svg>
                </div>
                <div class="record-details">
                  <p class="record-name">{{ record.name }}</p>
                  <p class="record-time">{{ record.time }}</p>
                </div>
              </div>
              <span class="record-amount">{{ record.amount }}</span>
            </div>
          </div>
        </div>

        <!-- 通知列表 -->
        <div class="notifications-card">
          <div class="notifications-header">
            <h4 class="notifications-title">最新通知</h4>
            <button class="notifications-more" @click="handleActionClick('/user/notification')">查看全部</button>
          </div>
          
          <div class="notifications-list">
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              class="notification-item"
              @click="openNotificationDetail(notification)"
            >
              <div class="notification-date" :class="notification.dateClass">
                <span class="date-day">{{ notification.day }}</span>
                <span class="date-month">{{ notification.month }}</span>
              </div>
              <div class="notification-content">
                <p class="notification-title">{{ notification.title }}</p>
                <p class="notification-desc">{{ notification.description }}</p>
              </div>
            </div>
          </div>
        </div>

        <el-dialog v-model="notificationDialogVisible" title="通知详情" width="500px" destroy-on-close>
          <div class="notification-detail">
            <div class="notification-detail-header">
              <div class="notification-detail-date-title">
                <div class="notification-detail-date" :class="currentNotification?.dateClass">
                  <span class="detail-date-day">{{ currentNotification?.day }}</span>
                  <span class="detail-date-month">{{ currentNotification?.month }}</span>
                </div>
                <h3 class="notification-detail-title">{{ currentNotification?.title }}</h3>
              </div>
            </div>
            <div class="notification-detail-content">
              <p>{{ currentNotification?.description }}</p>
            </div>
          </div>
          <template #footer>
            <el-button @click="notificationDialogVisible = false">关闭</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue';
import { recordApi, noticeApi, dashboardApi, vehicleApi, lotApi, feeStandardApi } from '@/api/business';
import { useUserStore } from '@/store/modules/user';

const router = useRouter();
const userStore = useUserStore();

const currentTime = ref('00:00');
const currentCost = ref('0.00');
const currentEntryTime = ref('');
const loading = ref(false);
const error = ref('');
const hasVehicle = ref(false);
const activeParkingRecord = ref(null);
const feeStandards = ref([]);
const notificationDialogVisible = ref(false);
const currentNotification = ref(null);

// 快捷入口配置
const actions = [
  { 
    label: '个人中心', 
    icon: 'mdi:account', 
    colorClass: 'text-blue-500', 
    bgClass: 'bg-blue-100', 
    borderClass: 'hover-border-blue',
    route: '/user/profile'
  },
  { 
    label: '我的车辆', 
    icon: 'mdi:car', 
    colorClass: 'text-red-500', 
    bgClass: 'bg-red-100', 
    borderClass: 'hover-border-red',
    route: '/user/vehicle/my-vehicle'
  },
  { 
    label: '停车记录', 
    icon: 'mdi:history', 
    colorClass: 'text-green-500', 
    bgClass: 'bg-green-100', 
    borderClass: 'hover-border-green',
    route: '/user/vehicle/record'
  },
  { 
    label: '预约停车', 
    icon: 'mdi:calendar', 
    colorClass: 'text-orange-500', 
    bgClass: 'bg-orange-100', 
    borderClass: 'hover-border-orange',
    route: '/user/parking/reservation'
  },
];

// 处理快捷入口点击事件
const handleActionClick = (route) => {
  if (route) {
    router.push(route);
  }
};

const records = ref([]);

const notifications = ref([]);

// 加载数据
const loadData = async () => {
  loading.value = true;
  error.value = '';
  hasVehicle.value = false;
  activeParkingRecord.value = null;
  
  try {
    const userId = userStore.info?.userId;
    if (!userId) {
      console.log('用户未登录');
      loading.value = false;
      return;
    }
    
    console.log('开始加载数据...');
    // 并行请求数据
    const [recordsData, noticesData, userRecordsData, lotsData, feeData] = await Promise.all([
      recordApi.fetchRecordList({ page: 1, pageSize: 7 }),
      noticeApi.fetchNoticeList({ page: 1, pageSize: 3 }),
      recordApi.fetchRecordList({ userId, status: 0, page: 1, pageSize: 10 }), // 查询正在停车的记录
      lotApi.fetchLotList({}), // 获取停车场列表
      feeStandardApi.fetchFeeStandardList({}) // 获取收费标准
    ]);
    
    console.log('API调用成功:', { recordsData, noticesData, userRecordsData, lotsData, feeData });
    
    // 保存收费标准
    if (feeData && feeData.records) {
      feeStandards.value = feeData.records;
    }
    
    // 更新停车记录（历史记录）
    if (recordsData && recordsData.records) {
      console.log('更新停车记录:', recordsData.records);
      records.value = recordsData.records.map((item, index) => ({
        id: item.id || index + 1,
        name: item.parkManageName || item.plateNumber || `停车场 ${index + 1}`,
        time: formatTime(item.gmtInto || item.createTime || item.entryTime),
        amount: `-${item.payAmount ? `¥${item.payAmount.toFixed(2)}` : '¥0.00'}`
      }));
    }
    
    // 更新通知
    if (noticesData && noticesData.records) {
      console.log('更新通知:', noticesData.records);
      notifications.value = noticesData.records.map((item, index) => {
        const date = new Date(item.gmtCreate || item.createTime || new Date());
        const day = date.getDate().toString().padStart(2, '0');
        const month = date.toLocaleString('en-US', { month: 'short' }).toUpperCase();
        const dateClasses = ['date-blue', 'date-green', 'date-orange'];
        
        return {
          id: item.id || index + 1,
          day,
          month,
          title: item.title || `通知 ${index + 1}`,
          description: item.content || '暂无内容',
          dateClass: dateClasses[index % dateClasses.length]
        };
      });
    }
    
    // 检查是否有正在停车的记录
    if (userRecordsData && userRecordsData.records && userRecordsData.records.length > 0) {
      console.log('发现正在停车的记录:', userRecordsData.records);
      let record = userRecordsData.records[0];
      
      // 如果有停车场列表，查找并添加停车场名称
      if (lotsData && lotsData.records && record.parkingLotId) {
        const lot = lotsData.records.find((l) => l.id === record.parkingLotId);
        if (lot) {
          record = { ...record, parkingLotName: lot.name };
        }
      }
      
      activeParkingRecord.value = record;
      hasVehicle.value = true;
      
      // 计算并显示停车时间
      updateParkingTime();
    }
  } catch (err) {
    console.error('加载数据失败:', err);
    error.value = '加载数据失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

// 更新停车时间
let timeInterval = null;

const updateParkingTime = () => {
  if (!activeParkingRecord.value) return;
  
  const entryTime = new Date(activeParkingRecord.value.gmtInto || activeParkingRecord.value.entryTime);
  
  const updateTime = () => {
    const now = new Date();
    const diffMs = now.getTime() - entryTime.getTime();
    const hours = Math.floor(diffMs / (1000 * 60 * 60));
    const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diffMs % (1000 * 60)) / 1000);
    currentTime.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    
    currentEntryTime.value = `${entryTime.getHours().toString().padStart(2, '0')}:${entryTime.getMinutes().toString().padStart(2, '0')}:${entryTime.getSeconds().toString().padStart(2, '0')}`;
    
    const totalMinutes = Math.floor(diffMs / (1000 * 60));
    const billableHours = Math.max(1, Math.ceil(totalMinutes / 60));
    const cost = billableHours * 5;
    currentCost.value = cost.toFixed(2);
  };
  
  updateTime();
  timeInterval = setInterval(updateTime, 1000);
};

// 格式化入场时间
const formatEntryTime = (time) => {
  if (!time) return '未知时间';
  const date = new Date(time);
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未知时间';
  
  const date = new Date(time);
  const now = new Date();
  const diffTime = now.getTime() - date.getTime();
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) {
    return `今天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  } else if (diffDays === 1) {
    return `昨天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  } else {
    return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }
};

const openNotificationDetail = (notification) => {
  currentNotification.value = notification;
  notificationDialogVisible.value = true;
};

// 组件挂载时加载数据
onMounted(() => {
  loadData();
});

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval);
  }
});
</script>

<style scoped>
.home-container {
  width: 100%;
  height: calc(100vh - 120px);
  height: calc(100dvh - 120px);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
  box-sizing: border-box;
}

/* 内容主网格 */
.content-grid {
  flex: 1;
  display: flex;
  gap: 16px;
  min-height: 0;
  overflow: hidden;
}

/* 左侧：实时停放状态 */
.parking-status {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.status-card {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 40px;
  padding: 40px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.decorative-bg {
  position: absolute;
  right: -64px;
  top: -64px;
  width: 256px;
  height: 256px;
  background: #dbeafe;
  border-radius: 50%;
  opacity: 0.6;
  z-index: 0;
}

.status-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}

.status-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.status-indicator {
  width: 10px;
  height: 10px;
  background: #22c55e;
  border-radius: 50%;
  margin-right: 12px;
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.status-tag {
  padding: 4px 12px;
  background: #dbeafe;
  color: #2563eb;
  border-radius: 9999px;
  font-size: 10px;
  font-weight: 900;
  text-transform: uppercase;
}

.status-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  min-height: 0;
}

/* 车牌号部分 */
.plate-number-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
}

.plate-number-left {
  flex: 1;
}

.plate-label {
  font-size: 14px;
  font-weight: 700;
  color: #3b82f6;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin: 0 0 8px 0;
}

.plate-number {
  font-size: 48px;
  font-weight: 900;
  color: #0f172a;
  letter-spacing: -0.025em;
  margin: 0;
}

.shield-icon {
  width: 80px;
  height: 80px;
  background: #eff6ff;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 信息列表 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.info-item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-label {
  font-size: 14px;
  font-weight: 600;
  color: #94a3b8;
  margin: 0;
}

.blue-label {
  color: #3b82f6;
}

.info-value {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.blue-value {
  color: #3b82f6;
}

.dot-indicator {
  width: 8px;
  height: 8px;
  background: #3b82f6;
  border-radius: 50%;
}

/* 分隔线 */
.divider {
  height: 1px;
  background: #f1f5f9;
  margin: 24px 0;
}

/* 底部信息 */
.bottom-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0;
}

.bottom-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bottom-label {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.bottom-time {
  font-size: 48px;
  font-weight: 900;
  color: #0f172a;
  letter-spacing: -0.025em;
}

.bottom-amount {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.currency {
  font-size: 24px;
  font-weight: 700;
  color: #3b82f6;
}

.amount {
  font-size: 48px;
  font-weight: 900;
  color: #3b82f6;
}

/* 一键求助按钮 */
.help-button {
  margin-top: 24px;
  padding: 24px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 16px 32px -8px rgba(79, 70, 229, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}

.help-button:hover {
  background: #4338ca;
  transform: translateY(-2px);
  box-shadow: 0 20px 40px -8px rgba(79, 70, 229, 0.5);
}

.help-button:active {
  transform: scale(0.98);
}

/* 底部文字 */
.powered-by {
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: #cbd5e1;
  margin: 24px 0 0 0;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* 右侧：功能按钮与记录 */
.side-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 0;
  overflow: hidden;
}

/* 四个快捷入口 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
  flex-shrink: 0;
}

.action-item {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
}

.hover-border-blue:hover {
  border-color: #bfdbfe;
}

.hover-border-red:hover {
  border-color: #fecaca;
}

.hover-border-green:hover {
  border-color: #dcfce7;
}

.hover-border-orange:hover {
  border-color: #ffedd5;
}

.action-icon {
  width: 32px;
  height: 32px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.text-blue-500 {
  color: #3b82f6;
}

.text-red-500 {
  color: #ef4444;
}

.text-green-500 {
  color: #22c55e;
}

.text-orange-500 {
  color: #f59e0b;
}

.bg-blue-100 {
  background: #dbeafe;
}

.bg-red-100 {
  background: #fecaca;
}

.bg-green-100 {
  background: #dcfce7;
}

.bg-orange-100 {
  background: #ffedd5;
}

.action-item:hover .action-icon {
  color: white;
}

.action-item:hover .bg-blue-100 {
  background: #3b82f6;
}

.action-item:hover .bg-red-100 {
  background: #ef4444;
}

.action-item:hover .bg-green-100 {
  background: #22c55e;
}

.action-item:hover .bg-orange-100 {
  background: #f59e0b;
}

.action-label {
  font-size: 8px;
  font-weight: 700;
  color: #1e293b;
}

/* 通知列表 */
.notifications-card {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.notifications-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-shrink: 0;
  padding: 0 6px;
}

.notifications-title {
  font-size: 12px;
  font-weight: 700;
  color: #1e293b;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin: 0;
}

.notifications-more {
  font-size: 8px;
  font-weight: 700;
  color: #3b82f6;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: all 0.2s ease;
}

.notifications-more:hover {
  text-decoration: underline;
}

.notifications-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
  gap: 12px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.notifications-list::-webkit-scrollbar {
  width: 4px;
}

.notifications-list::-webkit-scrollbar-track {
  background: transparent;
}

.notifications-list::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 8px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 14px;
  transition: all 0.2s ease;
  cursor: pointer;
  flex-shrink: 0;
}

.notification-item:hover {
  border-color: #e2e8f0;
  background: #f1f5f9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.notification-date {
  width: 36px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.date-blue {
  background: #dbeafe;
  color: #2563eb;
}

.date-green {
  background: #dcfce7;
  color: #16a34a;
}

.date-orange {
  background: #ffedd5;
  color: #d97706;
}

.date-day {
  font-size: 14px;
  font-weight: 900;
  line-height: 1;
}

.date-month {
  font-size: 8px;
  font-weight: 700;
  text-transform: uppercase;
  line-height: 1;
  margin-top: 2px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 12px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 2px 0;
  line-height: 1.2;
}

.notification-desc {
  font-size: 10px;
  color: #94a3b8;
  font-weight: 500;
  margin: 0;
  line-height: 1.3;
}

/* 消费记录列表 */
.records-card {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.records-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-shrink: 0;
  padding: 0 6px;
}

.records-title {
  font-size: 12px;
  font-weight: 700;
  color: #1e293b;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin: 0;
}

.records-more {
  font-size: 8px;
  font-weight: 700;
  color: #3b82f6;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: all 0.2s ease;
}

.records-more:hover {
  text-decoration: underline;
}

.records-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
  gap: 10px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.records-list::-webkit-scrollbar {
  width: 4px;
}

.records-list::-webkit-scrollbar-track {
  background: transparent;
}

.records-list::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 8px;
}

.record-item {
  padding: 12px;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.2s ease;
  cursor: pointer;
  flex-shrink: 0;
}

.record-item:hover {
  border-color: #e2e8f0;
}

.record-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.record-icon {
  width: 36px;
  height: 36px;
  background: #eff6ff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.record-details {
  display: flex;
  flex-direction: column;
}

.record-name {
  font-size: 10px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.record-time {
  font-size: 8px;
  color: #94a3b8;
  font-weight: 500;
  text-transform: uppercase;
  margin: 2px 0 0 0;
}

.record-amount {
  font-size: 10px;
  font-weight: 900;
  color: #1e293b;
}

/* 脉冲动画 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 响应式设计 */
@media (max-width: 1280px) {
  .content-grid {
    flex-direction: column;
  }
  
  .parking-status,
  .side-content {
    flex: none;
  }
  
  .action-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .side-content {
    flex-direction: row;
  }
  
  .records-card {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 12px;
    gap: 12px;
  }
  
  .content-grid {
    gap: 12px;
  }
  
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .side-content {
    flex-direction: column;
  }
  
  .parking-timer {
    width: 140px;
    height: 140px;
  }
  
  .timer-time {
    font-size: 28px;
  }
  
  .parking-details {
    width: 160px;
  }
  
  .amount-value {
    font-size: 20px;
  }
}

.notification-detail {
  padding: 0 0 20px 0;
}

.notification-detail-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.notification-detail-date-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-detail-date {
  width: 48px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.detail-date-day {
  font-size: 18px;
  font-weight: 900;
  line-height: 1;
}

.detail-date-month {
  font-size: 8px;
  font-weight: 700;
  text-transform: uppercase;
  line-height: 1;
  margin-top: 2px;
}

.notification-detail-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  flex: 1;
  line-height: 1.2;
}

.notification-detail-content {
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.notification-detail-content p {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  margin: 0;
}
</style>