<template>
  <div class="p-6 max-w-[1600px] mx-auto">
    <!-- 有车辆时的UI -->
    <template v-if="cars.length > 0">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <!-- 拥有车辆 -->
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex items-center">
          <div class="w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center mr-4">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-500">
              <path d="M14 16H9m10 0h3v-3.15a1 1 0 0 0-.84-.99L13 11V7a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1v5a1 1 0 0 0 1 1h2"></path>
              <path d="M6 18a3 3 0 0 0 3 3h6a3 3 0 0 0 3-3v-1"></path>
            </svg>
          </div>
          <div>
            <div class="text-sm text-gray-500">拥有车辆</div>
            <div class="text-2xl font-bold text-gray-800">{{ cars.length }} 辆</div>
          </div>
        </div>
        
        <!-- 本月停车 -->
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex items-center">
          <div class="w-12 h-12 rounded-full bg-orange-50 flex items-center justify-center mr-4">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-orange-500">
              <path d="M18 8h1a4 4 0 0 1 0 8h-1"></path>
              <path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path>
              <line x1="6" y1="1" x2="6" y2="4"></line>
              <line x1="10" y1="1" x2="10" y2="4"></line>
              <line x1="14" y1="1" x2="14" y2="4"></line>
            </svg>
          </div>
          <div>
            <div class="text-sm text-gray-500">本月停车</div>
            <div class="text-2xl font-bold text-gray-800">{{ totalMonthlyCount }} 次</div>
          </div>
        </div>
        
        <!-- 本月消费 -->
        <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex items-center">
          <div class="w-12 h-12 rounded-full bg-green-50 flex items-center justify-center mr-4">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-green-500">
              <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
          </div>
          <div>
            <div class="text-sm text-gray-500">本月消费</div>
            <div class="text-2xl font-bold text-gray-800">{{ totalMonthlySpent }} 元</div>
          </div>
        </div>
      </div>
      
      <!-- 车辆列表 -->
      <div class="mb-8">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <!-- 车辆卡片 -->
          <div v-for="car in cars" :key="car.id" class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow flex flex-col h-full">
            <div class="p-4 pb-3 flex-1">
              <!-- 卡片头部：车牌 -->
              <div class="flex justify-between items-center mb-4">
                <div :class="['plate-base', car.isNewEnergy ? 'plate-green' : 'plate-blue']">
                  {{ car.plate }}
                </div>
              </div>

              <!-- 信息详情 -->
              <div class="space-y-2.5 text-xs border-b border-gray-50 pb-4">
                  <div class="flex justify-between items-center">
                    <span class="text-gray-400">会员类型</span>
                    <span class="font-bold text-gray-700">{{ car.type }}</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-gray-400">有效期至</span>
                    <span :class="['font-bold', car.expiry === '请续费' ? 'text-red-500' : 'text-gray-600']">{{ car.expiry }}</span>
                  </div>
                <!-- 停车和消费的数字放在同一排 -->
                <div class="flex justify-between items-center mt-3 pt-3 border-t border-gray-50">
                  <div class="flex-1">
                    <span class="text-gray-400">本月停车</span>
                    <span class="text-blue-600 font-bold ml-2">{{ car.monthlyCount }} <span class="text-[10px] font-normal opacity-70">次</span></span>
                  </div>
                  <div class="flex-1 text-right">
                    <span class="text-gray-400">累计消费</span>
                    <span class="text-blue-600 font-bold ml-2">¥{{ car.totalSpent }}</span>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="flex gap-2 mt-3">
                <button class="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-1.5 rounded-lg text-xs flex items-center justify-center transition-colors" @click="handleMonthlyCard(car)">
                  <svg class="w-3.5 h-3.5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path></svg>
                  月卡
                </button>
                <button class="flex-1 border border-gray-200 hover:bg-gray-50 text-gray-600 py-1.5 rounded-lg text-xs flex items-center justify-center transition-colors" @click="handleRecord(car)">
                  <svg class="w-3.5 h-3.5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                  记录
                </button>
                <button class="w-8 border border-gray-200 hover:bg-red-50 hover:text-red-500 text-gray-400 flex items-center justify-center rounded-lg transition-colors" @click="handleDelete(car)">
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                </button>
              </div>
            </div>
          </div>

          <!-- 绑定车辆入口 -->
          <div class="border-2 border-dashed border-gray-200 rounded-xl flex flex-col items-center justify-center p-6 bg-white hover:border-blue-300 hover:bg-blue-50 transition-all cursor-pointer group min-h-[220px]" @click="addVehicle">
            <div class="w-10 h-10 rounded-full bg-gray-50 flex items-center justify-center mb-3 group-hover:bg-blue-100 transition-colors">
              <svg class="w-5 h-5 text-gray-400 group-hover:text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
              </svg>
            </div>
            <div class="text-gray-700 font-medium text-sm mb-1 text-center">绑定新车辆</div>
            <div class="text-gray-400 text-[10px] text-center">支持绑定燃油车/新能源车</div>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 无车辆时的UI -->
    <template v-else>
      <div class="flex flex-col items-start justify-center h-full py-20 px-8">
        <!-- 蓝色横线 -->
        <div class="w-12 h-1 bg-blue-500 mb-8"></div>
        
        <!-- 标题 -->
        <h3 class="text-3xl font-bold text-gray-900 mb-4">暂无车辆</h3>
        
        <!-- 描述 -->
        <p class="text-gray-500 mb-12 max-w-md">
          您还没有绑定任何车辆。绑定车辆后可以享受便捷的停车服务，包括自动推荐离目的地最近的空余车位。
        </p>
        
        <!-- 主要按钮 -->
        <button class="bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-lg font-semibold transition-all shadow-lg flex items-center gap-2" @click="addVehicle">
          快速添加车辆
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M5 12h14"></path>
            <path d="m12 5 7 7-7 7"></path>
          </svg>
        </button>
      </div>
    </template>
    
    <!-- 添加车辆弹窗 -->
    <ElDialog
      v-model="dialogVisible"
      title="添加车辆"
      width="500px"
      center
    >
      <!-- 百度AI智能提取 -->
      <div class="mb-8">
        <h4 class="text-lg font-semibold text-center mb-4">Baidu AI 智能提取</h4>
        <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex justify-center items-center">
          <div class="flex gap-8">
            <!-- 拍车牌/证件 -->
            <div class="flex flex-col items-center cursor-pointer hover:opacity-80 transition-opacity" @click="handleCamera">
              <div class="w-16 h-16 rounded-full bg-blue-50 flex items-center justify-center mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-500">
                  <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"></path>
                  <circle cx="12" cy="13" r="3"></circle>
                </svg>
              </div>
              <span class="text-sm text-gray-600">拍车牌/证件</span>
            </div>
            
            <!-- 相册上传 -->
            <div class="flex flex-col items-center cursor-pointer hover:opacity-80 transition-opacity" @click="handleAlbumUpload">
              <div class="w-16 h-16 rounded-full bg-blue-50 flex items-center justify-center mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-500">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
              </div>
              <span class="text-sm text-gray-600">相册上传</span>
            </div>
          </div>
        </div>
        <p class="text-xs text-gray-500 text-center mt-2">支持识别车牌实体及行驶证正页</p>
      </div>
      
      <!-- 或手动录入信息 -->
      <div class="mb-6">
        <p class="text-center text-gray-500 mb-4">或手动录入信息</p>
      </div>
      
      <!-- 表单 -->
      <ElForm :model="form" label-width="100px">
        <ElFormItem label="车牌号码">
          <ElInput v-model="form.plateNumber" placeholder="请输入车牌号" style="width: 300px" />
        </ElFormItem>
        
        <ElFormItem label="车主姓名">
          <ElInput v-model="form.ownerName" placeholder="填写真实姓名" style="width: 300px" />
        </ElFormItem>
        
        <ElFormItem label="手机号码">
          <ElInput v-model="form.mobile" placeholder="接受通知" style="width: 300px" />
        </ElFormItem>
      </ElForm>
      
      <template #footer>
        <span class="dialog-footer">
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton type="primary" @click="submitForm">确认无误并绑定</ElButton>
        </span>
      </template>
    </ElDialog>
    
    <!-- 摄像头弹窗 -->
    <ElDialog
      v-model="cameraDialogVisible"
      title="拍摄车牌"
      width="800px"
      center
    >
      <div class="flex flex-col items-center">
        <!-- 摄像头预览 -->
        <div class="relative w-full max-w-md mb-4">
          <video ref="videoRef" class="w-full h-auto border rounded-lg" autoplay></video>
          <!-- 拍照按钮 -->
          <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2">
            <button 
              class="w-16 h-16 rounded-full bg-blue-600 text-white flex items-center justify-center shadow-lg hover:bg-blue-700 transition-colors"
              @click="capturePhoto"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M12 8v4l3 3"></path>
              </svg>
            </button>
          </div>
        </div>
        
        <!-- 拍摄的照片预览 -->
        <div v-if="capturedImage" class="w-full max-w-md mb-4">
          <img :src="capturedImage" class="w-full h-auto border rounded-lg" alt="拍摄的照片">
        </div>
        
        <!-- 提示信息 -->
        <p class="text-sm text-gray-500 text-center mb-4">请将车牌对准摄像头，确保车牌清晰可见</p>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <ElButton @click="closeCamera">取消</ElButton>
          <ElButton type="primary" @click="confirmCapture" :disabled="!capturedImage">确认识别</ElButton>
        </span>
      </template>
    </ElDialog>
    
    <!-- 月卡续费弹窗 -->
    <ElDialog
      v-model="monthlyCardDialogVisible"
      title="月卡续费"
      width="360px"
      center
    >
      <div class="py-4">
        <!-- 套餐选项 -->
        <div class="space-y-4">
          <div 
            v-for="plan in plans" 
            :key="plan.id"
            class="border rounded-lg p-4 cursor-pointer transition-all hover:border-blue-300 relative"
            :class="{ 'border-blue-500 bg-blue-50': selectedPlan.id === plan.id }"
            @click="selectPlan(plan)"
          >
            <div v-if="plan.isHot" class="absolute -top-2 -right-2 bg-blue-600 text-white text-xs px-2 py-1 rounded">
              <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
            <div class="flex justify-between items-center">
              <div>
                <div class="font-medium">{{ plan.name }}</div>
                <div class="text-sm text-gray-500">{{ plan.duration }}</div>
              </div>
              <div class="text-right">
                <div class="text-xl font-bold text-blue-600">¥{{ plan.price }}</div>
                <div v-if="plan.discount" class="text-xs text-green-500">{{ plan.discount }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 服务协议 -->
        <div class="mt-6 text-sm text-gray-500">
          <el-checkbox v-model="agreeTerms" disabled>
            办理即代表同意《停车场月卡服务协议》
          </el-checkbox>
        </div>
      </div>
      
      <template #footer>
        <div class="w-full">
          <button 
            class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition-colors"
            @click="confirmPayment"
          >
            确认支付 ¥{{ selectedPlan.price }}
          </button>
        </div>
      </template>
    </ElDialog>
    
    <!-- 支付二维码弹窗 -->
    <ElDialog
      v-model="paymentDialogVisible"
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
            <span class="font-medium">{{ selectedPlan.name }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId || generateOrderId() }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-500">支付总额</span>
            <span class="text-2xl font-bold">¥{{ selectedPlan.price.toLocaleString() }}</span>
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
          您的月卡已成功续费，祝您使用愉快！
        </p>
        
        <!-- 订单信息 -->
        <div class="w-full bg-gray-50 rounded-lg p-4 mb-6">
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">套餐名称</span>
            <span class="font-medium">{{ selectedPlan.name }}</span>
          </div>
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">支付金额</span>
            <span class="font-bold text-green-600">¥{{ selectedPlan.price }}</span>
          </div>
        </div>
        
        <!-- 确认按钮 -->
        <button 
          class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition-colors"
          @click="paymentSuccessVisible = false"
        >
          确定
        </button>
      </div>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import request from '@/utils/http'
import { vehicleApi, plateApi, vasOrderApi } from '@/api/business'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import QrcodeVue from 'qrcode.vue'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'UserMyVehicle' })

interface CarItem {
  id: number
  plate: string
  isNewEnergy: boolean
  type: string
  expiry: string
  monthlyCount: number
  totalSpent: number
}

const router = useRouter()
const userStore = useUserStore()
const cars = ref<CarItem[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const cameraDialogVisible = ref(false);
const monthlyCardDialogVisible = ref(false);
const videoRef = ref<HTMLVideoElement | null>(null);
const capturedImage = ref<string>('');
let stream: MediaStream | null = null;

// 当前操作的车辆
const currentCar = ref<CarItem | null>(null);

// 支付弹窗
const paymentDialogVisible = ref(false);
const paymentSuccessVisible = ref(false);
const currentOrderId = ref('');

// 表单数据
const form = ref({
  plateNumber: '',
  ownerName: '',
  mobile: ''
});

// 月卡套餐数据
interface Plan {
  id: number;
  name: string;
  duration: string;
  price: number;
  discount?: string;
  isHot?: boolean;
}

const plans = ref<Plan[]>([
  {
    id: 1,
    name: '基础版',
    duration: '1个月',
    price: 200
  },
  {
    id: 2,
    name: '热门推荐',
    duration: '3个月',
    price: 550,
    discount: '比单买3个月省50元',
    isHot: true
  },
  {
    id: 3,
    name: '年度会员',
    duration: '12个月',
    price: 2000,
    discount: '比单买12个月省400元'
  }
]);

const selectedPlan = ref<Plan>(plans.value[1]); // 默认选择热门推荐
const agreeTerms = ref(true); // 默认同意服务协议

// 计算本月停车总次数
const totalMonthlyCount = computed(() => {
  return cars.value.reduce((total, car) => total + car.monthlyCount, 0);
});

// 计算本月总消费
const totalMonthlySpent = computed(() => {
  return Math.round(cars.value.reduce((total, car) => total + (car.totalSpent || 0), 0));
});

const formatValidityDate = (dateStr: string | null): string => {
  if (!dateStr) return '';
  try {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  } catch (e) {
    return dateStr.split('T')[0];
  }
};

const fetchCars = async () => {
  loading.value = true;
  try {
    const res: any = await vehicleApi.fetchVehicleList({});
    if (res && res.records) {
      cars.value = res.records.map((item: any) => {
        let expiryText = '';
        if (item.type === 1) {
          // 月租车
          if (item.validityTime) {
            expiryText = formatValidityDate(item.validityTime);
          } else {
            expiryText = '请续费';
          }
        } else {
          // 其他类型车辆
          expiryText = item.validityTime ? formatValidityDate(item.validityTime) : '长期有效';
        }
        
        // 判断是否为新能源车辆（根据车牌号总长度）
        // 总长度为8位的是新能源牌照（如"苏M000000"）
        // 总长度为7位的是普通汽车牌照（如"苏GPR867"）
        const isNewEnergy = item.plateNumber.length === 8;
        
        return {
          id: item.id,
          plate: item.plateNumber,
          isNewEnergy: isNewEnergy,
          type: item.type === 0 ? '临时车辆' : item.type === 1 ? '月租车' : item.type === 2 ? 'VIP' : '特种车',
          expiry: expiryText,
          monthlyCount: item.monthlyCount || 0,
          totalSpent: item.totalSpent || 0
        };
      });
    }
  } catch (error) {
    console.error('获取车辆列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleMonthlyCard = (car: CarItem) => {
  // 保存当前操作的车辆
  currentCar.value = car;
  // 显示月卡续费弹窗
  console.log('打开月卡续费弹窗:', car.plate);
  monthlyCardDialogVisible.value = true;
};

const selectPlan = (plan: Plan) => {
  selectedPlan.value = plan;
  console.log('选择套餐:', plan.name);
};

const confirmPayment = () => {
  if (!currentCar.value) {
    ElMessage.error('请选择车辆');
    return;
  }
  
  console.log('确认支付:', selectedPlan.value);
  // 关闭月卡续费弹窗
  monthlyCardDialogVisible.value = false;
  // 显示支付二维码弹窗
  paymentDialogVisible.value = true;
};

const generateOrderId = () => {
  // 生成订单编号（与后端逻辑一致）
  // 1. 前缀：VS（增值服务）
  const prefix = 'VS';
  
  // 2. 生成10位时间戳 (YYMMDDHHmm)
  const now = new Date();
  const year = String(now.getFullYear()).slice(-2);
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hour = String(now.getHours()).padStart(2, '0');
  const minute = String(now.getMinutes()).padStart(2, '0');
  const timestamp = `${year}${month}${day}${hour}${minute}`;
  
  // 3. 生成4位随机数
  const randomSuffix = String(Math.floor(Math.random() * 10000)).padStart(4, '0');
  
  // 4. 拼接成16位订单号
  return `${prefix}${timestamp}${randomSuffix}`;
};

const simulatePayment = async () => {
  if (!currentCar.value) {
    ElMessage.error('请选择车辆');
    return;
  }
  
  try {
    console.log('模拟支付:', selectedPlan.value);
    
    // 获取当前登录用户ID
    const currentUserId = userStore.info?.userId || 5;
    
    // 计算有效期：根据套餐名称确定月数
    let monthsToAdd = 1;
    if (selectedPlan.value.name.includes('3')) {
      monthsToAdd = 3;
    } else if (selectedPlan.value.name.includes('年度') || selectedPlan.value.name.includes('12')) {
      monthsToAdd = 12;
    }
    
    // 计算新的有效期（只到日）
    const now = new Date();
    let newValidityTime = new Date(now);
    newValidityTime.setMonth(newValidityTime.getMonth() + monthsToAdd);
    
    // 格式化为只包含日期的字符串
    const year = newValidityTime.getFullYear();
    const month = String(newValidityTime.getMonth() + 1).padStart(2, '0');
    const day = String(newValidityTime.getDate()).padStart(2, '0');
    const validityDateStr = `${year}-${month}-${day}`;
    
    // 创建增值服务订单
    const orderData = {
      vehicleId: currentCar.value.id,
      plateNumber: currentCar.value.plate,
      orderType: 3, // 3表示月卡服务
      body: '月卡服务',
      payAmount: selectedPlan.value.price,
      originalAmount: selectedPlan.value.price,
      discountAmount: 0,
      status: 1, // 1表示已支付
      payWay: Math.floor(Math.random() * 2),
      orderNo: generateOrderId(),
      parkingLotId: 0,
      userId: currentUserId
    };
    
    console.log('创建增值服务订单，参数:', orderData);
    const res = await vasOrderApi.addVasOrder(orderData);
    
    if (res) {
      // 更新车辆信息：设置为月租车，更新有效期（只到日）
      await vehicleApi.updateVehicle({
        id: currentCar.value.id,
        plateNumber: currentCar.value.plate,
        type: 1, // 1表示月租车
        validityTime: validityDateStr
      });
      
      // 保存实际的订单编号
      currentOrderId.value = (res as any)?.orderId || (res as any)?.id || generateOrderId();
      // 关闭支付弹窗
      paymentDialogVisible.value = false;
      // 显示支付成功弹窗
      paymentSuccessVisible.value = true;
      // 重新获取车辆列表，更新车辆类型
      setTimeout(() => {
        fetchCars();
      }, 1000);
    } else {
      ElMessage.error('创建订单失败');
    }
  } catch (error) {
    console.error('创建订单失败:', error);
    ElMessage.error('创建订单失败');
  }
};

const handleRecord = (car: CarItem) => {
  // 跳转到该车辆的停车记录页面，并传递车牌号码作为筛选条件
  console.log('跳转到停车记录页面:', car.plate);
  router.push({
    path: '/user/vehicle/record',
    query: {
      plateNumber: car.plate
    }
  });
};

const handleDelete = (car: CarItem) => {
  // 弹出确认对话框
  ElMessageBox.confirm(
    `确定要删除车辆 ${car.plate} 吗？`,
    '删除车辆',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 调用删除车辆的API
      console.log('删除车辆:', car.plate);
      await vehicleApi.deleteVehicle(car.id);
      ElMessage.success('删除车辆成功');
      // 重新获取车辆列表
      fetchCars();
    } catch (error) {
      console.error('删除车辆失败:', error);
      ElMessage.error('删除车辆失败');
    }
  }).catch(() => {
    // 取消删除
  });
};

const addVehicle = () => {
  // 显示添加车辆弹窗
  dialogVisible.value = true;
};

const handleCamera = () => {
  // 显示摄像头弹窗
  cameraDialogVisible.value = true;
  // 启动摄像头
  startCamera();
};

const startCamera = async () => {
  try {
    // 停止之前的流
    if (stream) {
      stream.getTracks().forEach(track => track.stop());
    }
    
    // 请求摄像头权限
    stream = await navigator.mediaDevices.getUserMedia({ video: true });
    
    // 显示摄像头预览
    if (videoRef.value && stream) {
      videoRef.value.srcObject = stream;
    }
  } catch (error) {
    console.error('摄像头调用失败:', error);
    ElMessage.error('摄像头调用失败，请检查权限设置');
    cameraDialogVisible.value = false;
  }
};

const capturePhoto = () => {
  if (!videoRef.value) return;
  
  // 创建Canvas元素
  const canvas = document.createElement('canvas');
  canvas.width = videoRef.value.videoWidth || 640;
  canvas.height = videoRef.value.videoHeight || 480;
  
  // 绘制视频帧到Canvas
  const ctx = canvas.getContext('2d');
  if (ctx) {
    ctx.drawImage(videoRef.value, 0, 0, canvas.width, canvas.height);
    // 将Canvas转换为图片URL
    capturedImage.value = canvas.toDataURL('image/jpeg');
  }
};

const confirmCapture = async () => {
  if (!capturedImage.value) return;
  
  try {
    // 将base64图片转换为Blob
    const response = await fetch(capturedImage.value);
    const blob = await response.blob();
    const file = new File([blob], 'license-plate.jpg', { type: 'image/jpeg' });
    
    ElMessage.info('正在识别车牌...');
    
    // 调用真实的车牌识别 API
    const res: any = await plateApi.uploadPlate(file, 1);
    if (res && res.plateNumber) {
      const recognizedPlate = res.plateNumber;
      // 将识别结果填充到车牌号输入框
      form.value.plateNumber = recognizedPlate;
      ElMessage.success(`车牌识别成功: ${recognizedPlate}`);
      // 关闭摄像头弹窗
      closeCamera();
    } else {
      ElMessage.error('车牌识别失败');
    }
  } catch (error) {
    console.error('车牌识别失败:', error);
    ElMessage.error('车牌识别失败，请重试');
  }
};

const closeCamera = () => {
  // 停止摄像头流
  if (stream) {
    stream.getTracks().forEach(track => track.stop());
    stream = null;
  }
  // 清除捕获的图片
  capturedImage.value = '';
  // 关闭弹窗
  cameraDialogVisible.value = false;
};

const handleAlbumUpload = () => {
  // 调用相册上传功能
  console.log('调用相册上传功能');
  // 创建一个隐藏的文件输入元素
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.onchange = async (e) => {
    const target = e.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file = target.files[0];
      console.log('选择的文件:', file);
      ElMessage.info('文件选择成功，正在识别车牌...');
      
      try {
        // 调用真实的车牌识别 API
        // 这里使用默认的停车场 ID 1，实际应该根据当前选择的停车场动态获取
        const res: any = await plateApi.uploadPlate(file, 1);
        if (res && res.plateNumber) {
          const recognizedPlate = res.plateNumber;
          // 将识别结果填充到车牌号输入框
          form.value.plateNumber = recognizedPlate;
          ElMessage.success(`车牌识别成功: ${recognizedPlate}`);
        } else {
          ElMessage.error('车牌识别失败');
        }
      } catch (error) {
        console.error('车牌识别失败:', error);
        ElMessage.error('车牌识别失败，请重试');
      }
    }
  };
  input.click();
};

const submitForm = async () => {
  try {
    // 1. 先检查车辆是否存在，如果不存在则添加
    console.log('检查车辆是否存在，车牌号:', form.value.plateNumber);
    try {
      const checkRes: any = await vehicleApi.fetchVehicleList({ plateNumber: form.value.plateNumber });
      console.log('检查车辆是否存在结果:', checkRes);
      let vehicleExists = false;
      
      if (checkRes && checkRes.records && checkRes.records.length > 0) {
        vehicleExists = true;
        console.log('车辆已存在');
        ElMessage.error('车辆已存在');
        return;
      } else {
        // 判断是否为新能源车辆（根据车牌号总长度）
        // 总长度为8位的是新能源牌照（如"苏M000000"）
        // 总长度为7位的是普通汽车牌照（如"苏GPR867"）
        const isNewEnergy = form.value.plateNumber.length === 8;
        
        const currentUserId = userStore.info?.userId;
        
        console.log('车辆不存在，添加车辆');
        console.log('添加车辆，参数:', {
          plateNumber: form.value.plateNumber,
          ownerName: form.value.ownerName,
          mobile: form.value.mobile,
          type: 0,
          status: 1,
          parkingLotId: 0,
          userId: currentUserId
        });
        const addVehicleRes: any = await vehicleApi.addVehicle({
          plateNumber: form.value.plateNumber,
          ownerName: form.value.ownerName,
          mobile: form.value.mobile,
          type: 0,
          status: 1,
          parkingLotId: 0,
          userId: currentUserId
        });
        
        console.log('添加车辆结果:', addVehicleRes);
        if (addVehicleRes) {
          ElMessage.success('添加车辆成功');
          // 关闭弹窗
          dialogVisible.value = false;
          // 重新获取车辆列表
          fetchCars();
        } else {
          ElMessage.error('添加车辆失败');
        }
      }
    } catch (error) {
      console.error('添加车辆失败:', error);
      ElMessage.error('添加车辆失败');
    }
  } catch (error) {
    console.error('添加车辆失败:', error);
    ElMessage.error('添加车辆失败');
  }
};

// 组件卸载时停止摄像头
onUnmounted(() => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop());
  }
});

onMounted(() => {
  fetchCars();
});
</script>

<style scoped>
/* 车牌基础样式 */
.plate-base {
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 800;
  font-size: 0.95rem;
  letter-spacing: 0.5px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
  white-space: nowrap;
}
/* 蓝牌渐变 */
.plate-blue {
  background: linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
  border: 1px solid #1e40af;
}
/* 绿牌渐变 (渐变色模仿新能源车牌) */
.plate-green {
  background: linear-gradient(180deg, #99f6e4 0%, #4ade80 50%, #22c55e 100%);
  color: #1a1a1a;
  border: 1px solid #86efac;
}
/* 车牌高光效果 */
.plate-base::after {
  content: "";
  position: absolute;
  top: 0; left: 0; right: 0; height: 50%;
  background: rgba(255,255,255,0.1);
  pointer-events: none;
}


</style>