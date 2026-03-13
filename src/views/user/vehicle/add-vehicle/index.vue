<template>
  <div class="p-6 max-w-[800px] mx-auto">
    <!-- 蓝色横线 -->
    <div class="w-12 h-1 bg-blue-500 mb-8"></div>
    
    <!-- 标题 -->
    <h3 class="text-3xl font-bold text-gray-900 mb-6">添加车辆</h3>
    
    <!-- 百度AI智能提取 -->
    <div class="mb-8">
      <h4 class="text-lg font-semibold text-center mb-4">Baidu AI 智能提取</h4>
      <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex justify-center items-center">
        <div class="flex gap-8">
          <!-- 拍车牌/证件 -->
          <div class="flex flex-col items-center cursor-pointer hover:opacity-80 transition-opacity">
            <div class="w-16 h-16 rounded-full bg-blue-50 flex items-center justify-center mb-2">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-blue-500">
                <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"></path>
                <circle cx="12" cy="13" r="3"></circle>
              </svg>
            </div>
            <span class="text-sm text-gray-600">拍车牌/证件</span>
          </div>
          
          <!-- 相册上传 -->
          <div class="flex flex-col items-center cursor-pointer hover:opacity-80 transition-opacity">
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
    <ElCard shadow="never" class="mb-6">
      <ElForm :model="form" label-width="100px">
        <ElFormItem label="车牌号码">
          <ElInput v-model="form.plateNumber" placeholder="请输入车牌号" />
        </ElFormItem>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ElFormItem label="车主姓名">
            <ElInput v-model="form.ownerName" placeholder="填写真实姓名" />
          </ElFormItem>
          
          <ElFormItem label="性别">
            <ElSelect v-model="form.gender" placeholder="请选择性别">
              <ElOption label="保密" value="0" />
              <ElOption label="男" value="1" />
              <ElOption label="女" value="2" />
            </ElSelect>
          </ElFormItem>
        </div>
        
        <ElFormItem label="手机号码">
          <ElInput v-model="form.mobile" placeholder="接收挪车等通知" />
        </ElFormItem>
        
        <ElFormItem label="车辆类型">
          <ElSelect v-model="form.type" placeholder="请选择车辆类型">
            <ElOption label="临时车辆" value="0" />
            <ElOption label="大型车" value="1" />
            <ElOption label="新能源" value="2" />
            <ElOption label="特种车" value="3" />
          </ElSelect>
        </ElFormItem>
        
        <ElFormItem label="有效期">
          <ElDatePicker
            v-model="form.expireTime"
            type="datetime"
            placeholder="选择有效期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </ElFormItem>
        
        <ElFormItem label="是否自动缴费">
          <ElSwitch v-model="form.isAutoPay" />
        </ElFormItem>
        
        <ElFormItem>
          <ElButton type="primary" class="w-full py-3 text-base" @click="submitForm">确认无误并绑定</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { familyApi } from '@/api/business'
import { useRouter } from 'vue-router'

defineOptions({ name: 'AddVehicle' })

const router = useRouter()

const form = ref({
  plateNumber: '',
  ownerName: '',
  gender: 0,
  mobile: '',
  type: 0,
  expireTime: '',
  isAutoPay: false
})

const submitForm = async () => {
  try {
    // 假设当前用户ID为1，实际应该从登录状态获取
    const userId = 1
    
    const res: any = await familyApi.addFamily({
      user_id: userId,
      plate_number: form.value.plateNumber,
      bind_type: 1, // 1表示主账号
      is_auto_pay: form.value.isAutoPay ? 1 : 0,
      expire_time: form.value.expireTime
    })
    
    if (res?.code === 200) {
      ElMessage.success('添加车辆成功')
      // 跳回我的车辆页面
      router.push('/user/vehicle/my-vehicle')
    } else {
      ElMessage.error(res?.message || '添加车辆失败')
    }
  } catch (error) {
    console.error('添加车辆失败:', error)
    ElMessage.error('添加车辆失败')
  }
}

const resetForm = () => {
  form.value = {
    plateNumber: '',
    ownerName: '',
    gender: 0,
    mobile: '',
    type: 0,
    expireTime: '',
    isAutoPay: false
  }
}
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>