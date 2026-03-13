<template>
  <div class="min-h-screen bg-slate-50 p-6 font-sans antialiased text-slate-900">
    <div class="max-w-6xl mx-auto">
      <!-- 加载状态 -->
      <ElLoading v-if="loading" fullscreen text="加载中..." />
      
      <!-- 顶部统计栏 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-indigo-50 rounded-xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-indigo-100 flex items-center justify-center text-indigo-600">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
              </svg>
            </div>
            <div>
              <div class="text-xs text-slate-500 font-medium">当前共享成员</div>
              <div class="text-lg font-bold text-slate-800">{{ familyData.currentMembers }} / {{ familyData.maxMembers }} 席</div>
            </div>
          </div>
        </div>
        
        <div class="bg-blue-50 rounded-xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-blue-100 flex items-center justify-center text-blue-500">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
            </div>
            <div>
              <div class="text-xs text-slate-500 font-medium">月租剩余时长</div>
              <div class="text-lg font-bold text-slate-800">{{ familyData.expiryDays }} 天</div>
            </div>
          </div>
        </div>
        
        <div class="bg-emerald-50 rounded-xl p-6">
          <div class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg bg-emerald-100 flex items-center justify-center text-emerald-500">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
              </svg>
            </div>
            <div>
              <div class="text-xs text-slate-500 font-medium">共享安全状态</div>
              <div class="text-lg font-bold text-emerald-600">正常</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 主内容区 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start">
        <!-- 左侧家庭组单元 -->
        <div class="flex flex-col">
          <!-- 主车信息卡片 -->
          <div v-if="familyData.mainCar" class="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden flex flex-col transition-all duration-300">
            <div class="p-6 flex-1">
              <div class="flex justify-between items-start mb-6">
                <div>
                  <span class="bg-slate-800 text-white px-3 py-1 rounded font-bold text-sm tracking-wider">
                    {{ familyData.mainCar }}
                  </span>
                  <p class="text-[10px] text-indigo-600 font-bold mt-2 uppercase tracking-wider">
                    家庭共享组发起人
                  </p>
                </div>
                <div class="flex items-center gap-2">
                  <span class="relative flex h-2 w-2">
                    <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                    <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                  </span>
                  <span class="text-emerald-500 text-xs font-bold">{{ familyData.status }}</span>
                </div>
              </div>
              
              <!-- 最近家庭动态 -->
              <div class="bg-slate-50 rounded-lg p-4 mb-4">
                <p class="text-[10px] text-slate-400 font-bold mb-3 uppercase tracking-tight">最近家庭动态</p>
                <div class="space-y-3">
                  <div v-if="members.length > 0" class="flex items-center gap-2">
                    <span class="text-sm text-slate-700">副车 {{ members[0].plate }} 进场</span>
                    <span class="text-xs text-slate-400 ml-auto">10分钟前</span>
                  </div>
                  <div v-if="familyData.expiryDays > 0" class="flex items-center gap-2">
                    <span class="text-sm text-slate-700">权益到期提醒</span>
                    <span class="text-xs text-slate-400 ml-auto">昨天</span>
                  </div>
                </div>
              </div>
              
              <!-- 配额分布 -->
              <div class="flex items-center justify-between pt-2">
                <div class="flex -space-x-2">
                  <div class="w-8 h-8 rounded-full bg-indigo-600 border-2 border-white flex items-center justify-center text-[10px] font-bold text-white shadow-sm">主</div>
                  <div v-for="(item, index) in familyData.maxMembers - 1" :key="index" class="w-8 h-8 rounded-full" :class="{
                    'bg-slate-200 border-2 border-white flex items-center justify-center text-[10px] font-bold text-slate-500 shadow-sm': index < familyData.currentMembers - 1,
                    'bg-slate-100 border-2 border-white border-dashed flex items-center justify-center text-[10px] font-bold text-slate-300': index >= familyData.currentMembers - 1
                  }">
                    {{ index < familyData.currentMembers - 1 ? '副' : '空' }}
                  </div>
                </div>
                <p class="text-xs text-slate-500 font-medium">配额使用率 {{ familyData.maxMembers > 0 ? Math.round((familyData.currentMembers / familyData.maxMembers) * 100) : 0 }}%</p>
              </div>
            </div>
            
            <!-- 操作按钮 -->
            <div class="p-4 bg-slate-50 border-t border-slate-100 flex gap-2">
              <button 
                @click="toggleExpand"
                class="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white text-xs py-2.5 rounded-lg flex items-center justify-center gap-2 font-bold transition-all shadow-md active:scale-95"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="3"></circle>
                  <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                </svg>
                {{ isExpanded ? '收起管理界面' : '管理共享成员' }}
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ 'rotate-180': isExpanded }" class="transition-transform duration-300">
                  <polyline points="6 9 12 15 18 9"></polyline>
                </svg>
              </button>
              <button 
                @click="showSettingsDialog = true"
                class="px-4 bg-white hover:bg-slate-100 border border-slate-200 text-slate-600 text-xs py-2.5 rounded-lg font-medium transition-all"
              >
                设置
              </button>
              <button 
                @click="deleteFamilyGroup"
                class="px-4 bg-white hover:bg-red-50 border border-red-200 text-red-600 text-xs py-2.5 rounded-lg font-medium transition-all"
              >
                删除
              </button>
            </div>
          </div>
          
          <!-- 无家庭组时的提示 -->
          <div v-else class="bg-white border border-slate-200 rounded-xl p-8 text-center">
            <div class="w-16 h-16 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mx-auto mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-slate-800 mb-2">暂无家庭组</h3>
            <p class="text-sm text-slate-500 mb-6">您还没有创建家庭组，点击右侧按钮创建新的家庭组</p>
          </div>
          
          <!-- 可展开的副车列表 -->
          <div 
            v-if="familyData.mainCar"
            class="overflow-hidden transition-all duration-500 ease-[cubic-bezier(0.4,0,0.2,1)]" 
            :class="{ 'max-h-[600px] opacity-100 mt-4': isExpanded, 'max-h-0 opacity-0': !isExpanded }"
          >
            <div class="space-y-3">
              <!-- 副车项 -->
              <div v-if="members.length === 0" class="bg-white border border-slate-200 rounded-xl p-8 text-center">
                <p class="text-slate-500">暂无家庭成员车辆</p>
              </div>
              <div v-for="member in members" :key="member.id" class="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex justify-between items-center group hover:border-indigo-200 transition-all">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-lg bg-slate-100 flex items-center justify-center text-slate-500 font-bold text-xs uppercase group-hover:bg-indigo-50 group-hover:text-indigo-600 transition-all">
                    {{ member.plate.substring(0, 2) }}
                  </div>
                  <div>
                    <p class="text-sm font-bold text-slate-700">{{ member.plate }}</p>
                    <div class="flex items-center gap-2 mt-0.5">
                      <span class="w-2 h-2 bg-emerald-500 rounded-full"></span>
                      <span class="text-[10px] text-slate-400 font-medium">{{ member.status }}</span>
                    </div>
                  </div>
                </div>
                <div class="flex items-center gap-4">
                  <div class="flex flex-col items-end">
                    <p class="text-[10px] text-slate-400 font-bold uppercase mb-1">准入控制</p>
                    <ElSwitch v-model="member.active" />
                  </div>
                  <button 
                    @click="removeMember(member.id)"
                    class="text-slate-300 hover:text-red-500 transition-colors p-2"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      <line x1="10" y1="11" x2="10" y2="17"></line>
                      <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                  </button>
                </div>
              </div>
              
              <!-- 邀请新成员 -->
              <div 
                v-if="familyData.currentMembers < familyData.maxMembers"
                @click="showInviteDialog = true"
                class="border-2 border-dashed border-slate-200 rounded-xl p-4 flex items-center justify-center gap-3 bg-white/50 hover:bg-white hover:border-indigo-400 hover:shadow-md transition-all cursor-pointer group"
              >
                <div class="w-6 h-6 rounded-full border border-slate-300 flex items-center justify-center text-slate-400 group-hover:text-indigo-500 group-hover:border-indigo-500 transition-all">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="16"></line>
                    <line x1="8" y1="12" x2="16" y2="12"></line>
                  </svg>
                </div>
                <span class="text-xs text-slate-400 group-hover:text-indigo-500 font-bold">添加副车</span>
              </div>
              <div v-else class="border-2 border-dashed border-slate-200 rounded-xl p-4 flex items-center justify-center gap-3 bg-gray-50 cursor-not-allowed">
                <div class="w-6 h-6 rounded-full border border-gray-300 flex items-center justify-center text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="16"></line>
                    <line x1="8" y1="12" x2="16" y2="12"></line>
                  </svg>
                </div>
                <span class="text-xs text-gray-400 font-bold">已达到最大成员数量</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧操作面板 -->
        <div class="space-y-6">
          <!-- 添加新家庭组 -->
          <div class="bg-white border border-dashed border-indigo-300 rounded-2xl p-8 text-center shadow-sm hover:shadow-md transition-all cursor-pointer"
               @click="showCreateFamilyDialog = true">
            <div class="w-16 h-16 rounded-full bg-indigo-50 flex items-center justify-center text-indigo-500 mx-auto mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="16"></line>
                <line x1="8" y1="12" x2="16" y2="12"></line>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-indigo-700 mb-2">创建新家庭组</h3>
            <p class="text-sm text-slate-500">为不同的车辆创建独立的共享组</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 管理共享成员对话框 -->
    <ElDialog 
      v-model="showManageMembersDialog" 
      title="管理共享成员" 
      width="500px" 
      center
      class="rounded-2xl overflow-hidden"
    >
      <div class="p-6">
        <!-- 成员列表 -->
        <div class="space-y-4">
          <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-4">
              <div class="plate-base plate-blue text-sm">
                苏G · 6B608
              </div>
              <div>
                <div class="font-medium text-gray-800">苏G · 6B608</div>
                <div class="text-sm text-gray-500">主车</div>
              </div>
            </div>
            <ElTag type="primary" size="small" class="rounded-full">
              管理员
            </ElTag>
          </div>
          
          <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-4">
              <div class="plate-base plate-green text-sm">
                苏G · A1234
              </div>
              <div>
                <div class="font-medium text-gray-800">苏G · A1234</div>
                <div class="text-sm text-gray-500">副车</div>
              </div>
            </div>
            <ElButton 
              type="danger" 
              size="small" 
              :icon="Delete"
              class="rounded-full"
            >
              移除
            </ElButton>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3 px-6 pb-6">
          <ElButton @click="showManageMembersDialog = false" class="rounded-full px-6">关闭</ElButton>
        </div>
      </template>
    </ElDialog>
    
    <!-- 添加副车对话框 -->
    <ElDialog 
      v-model="showInviteDialog" 
      title="添加副车" 
      width="500px" 
      center
      class="rounded-2xl overflow-hidden"
    >
      <div class="p-6">
        <ElForm :model="inviteForm" label-width="120px">
          <ElFormItem label="副车车牌号" required>
            <ElSelect 
              v-model="inviteForm.plateNumber" 
              placeholder="请选择副车车牌号" 
              class="w-full"
            >
              <ElOption
                v-for="vehicle in temporaryVehicles"
                :key="vehicle.id"
                :label="vehicle.plateNumber"
                :value="vehicle.plateNumber"
              />
            </ElSelect>
          </ElFormItem>
        </ElForm>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3 px-6 pb-6">
          <ElButton @click="showInviteDialog = false" class="rounded-full px-6">取消</ElButton>
          <ElButton type="primary" @click="submitInvite" class="rounded-full px-6">确认添加</ElButton>
        </div>
      </template>
    </ElDialog>
    
    <!-- 设置对话框 -->
    <ElDialog 
      v-model="showSettingsDialog" 
      title="设置" 
      width="500px" 
      center
      class="rounded-2xl overflow-hidden"
    >
      <div class="p-6">
        <ElForm :model="settingsForm" label-width="120px">
          <ElFormItem label="共享组名称">
            <ElInput 
              v-model="settingsForm.groupName" 
              placeholder="请输入共享组名称" 
              class="w-full"
            />
          </ElFormItem>
          <ElFormItem label="自动授权">
            <ElSwitch v-model="settingsForm.autoAuthorize" />
            <div class="text-xs text-gray-400 mt-2">
              开启后，副车进场将自动授权
            </div>
          </ElFormItem>
          <ElFormItem label="通知设置">
            <ElSwitch v-model="settingsForm.notification" />
            <div class="text-xs text-gray-400 mt-2">
              开启后，副车进场离场将收到通知
            </div>
          </ElFormItem>
        </ElForm>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3 px-6 pb-6">
          <ElButton @click="showSettingsDialog = false" class="rounded-full px-6">取消</ElButton>
          <ElButton type="primary" @click="submitSettings" class="rounded-full px-6">保存设置</ElButton>
        </div>
      </template>
    </ElDialog>
    
    <!-- 法律条款对话框 -->
    <ElDialog 
      v-model="showTermsDialog" 
      title="详细法律条款" 
      width="800px" 
      center
      class="rounded-2xl overflow-hidden"
    >
      <div class="p-6 max-h-96 overflow-y-auto">
        <div class="text-gray-700 space-y-4">
          <p>1. 家庭组共享权益仅适用于月卡车用户，主车必须为月卡车。</p>
          <p>2. 副车入组后，将自动继承主车的月租停车免费政策，无需额外缴费。</p>
          <p>3. 当前停车场仅支持1辆副车同时在场享受免费，超过限制的副车将按临时车计费。</p>
          <p>4. 主车享有最高管理权，可随时开启或关闭副车的共享权限。</p>
          <p>5. 副车必须遵守停车场的相关规定，如有违规行为，主车将承担相应责任。</p>
          <p>6. 家庭组权益有效期与主车月卡有效期一致，月卡到期后，家庭组权益自动失效。</p>
          <p>7. 停车场管理方有权根据实际情况调整家庭组共享规则，调整前将提前通知用户。</p>
          <p>8. 用户开通家庭组权益即表示同意以上条款，如有违反，停车场管理方有权取消其家庭组权益。</p>
        </div>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3 px-6 pb-6">
          <ElButton @click="showTermsDialog = false" class="rounded-full px-6">关闭</ElButton>
        </div>
      </template>
    </ElDialog>
    
    <!-- 创建新家庭组对话框 -->
    <ElDialog 
      v-model="showCreateFamilyDialog" 
      title="创建新家庭组" 
      width="500px" 
      center
      class="rounded-2xl overflow-hidden"
    >
      <div class="p-6">
        <ElForm :model="createFamilyForm" label-width="120px">
          <ElFormItem label="主车车牌号" required>
            <ElSelect 
              v-model="createFamilyForm.mainCarPlate" 
              placeholder="请选择主车车牌号" 
              class="w-full"
            >
              <ElOption
                v-for="vehicle in monthlyVehicles"
                :key="vehicle.id"
                :label="vehicle.plateNumber"
                :value="vehicle.plateNumber"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="共享组名称">
            <ElInput 
              v-model="createFamilyForm.groupName" 
              placeholder="请输入共享组名称（选填）" 
              class="w-full"
            />
          </ElFormItem>
        </ElForm>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3 px-6 pb-6">
          <ElButton @click="showCreateFamilyDialog = false" class="rounded-full px-6">取消</ElButton>
          <ElButton type="primary" @click="submitCreateFamily" class="rounded-full px-6">创建家庭组</ElButton>
        </div>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { familyApi, vehicleApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'UserFamily' })

// 状态变量
const showManageMembersDialog = ref(false)
const showInviteDialog = ref(false)
const showSettingsDialog = ref(false)
const showTermsDialog = ref(false)
const showCreateFamilyDialog = ref(false)
const isExpanded = ref(false)
const loading = ref(false)
const userStore = useUserStore()
const familyData = ref({
  mainCar: '',
  expiryDays: 0,
  maxMembers: 0,
  currentMembers: 0,
  status: '',
  groupName: ''
})

// 成员数据
const members = ref<any[]>([])

// 当前用户绑定的车辆列表
const userVehiclesList = ref<any[]>([])

// 计算属性：月租车列表（用于主车选择）
const monthlyVehicles = computed(() => {
  // 尝试不同的车辆类型字段名称和值
  return userVehiclesList.value.filter(vehicle => {
    // 检查各种可能的字段名称和值
    return (vehicle.type === 'monthly' || vehicle.type === 1 || vehicle.vehicleType === 'monthly' || vehicle.vehicleType === 1)
  })
})

// 计算属性：临时车列表（用于副车选择）
const temporaryVehicles = computed(() => {
  // 尝试不同的车辆类型字段名称和值
  return userVehiclesList.value.filter(vehicle => {
    // 检查各种可能的字段名称和值
    return (vehicle.type === 'temporary' || vehicle.type === 0 || vehicle.vehicleType === 'temporary' || vehicle.vehicleType === 0)
  })
})

// 表单数据
const inviteForm = ref({
  plateNumber: '',
  remark: ''
})

const settingsForm = ref({
  groupName: '我的家庭组',
  autoAuthorize: true,
  notification: true
})

const createFamilyForm = ref({
  mainCarPlate: '',
  groupName: ''
})

// 方法
const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}

const removeMember = (id: number) => {
  ElMessageBox.confirm('确定要移除该成员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await familyApi.deleteFamily(id)
      members.value = members.value.filter(member => member.id !== id)
      familyData.value.currentMembers = members.value.length
      ElMessage.success('成员移除成功')
    } catch (error) {
      ElMessage.error('移除成员失败，请重试')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消操作
  })
}

const submitInvite = async () => {
  if (!inviteForm.value.plateNumber) {
    ElMessage.warning('请输入副车车牌号')
    return
  }
  
  // 检查是否达到最大成员数量
  if (members.value.length >= familyData.value.maxMembers) {
    ElMessage.warning(`已达到最大副车数量 ${familyData.value.maxMembers} 辆`)
    return
  }
  
  loading.value = true
  try {
    // 检查车辆是否存在
    const checkResult: any = await familyApi.checkVehicle(inviteForm.value.plateNumber)
    console.log('车辆校验结果:', checkResult)
    if (!checkResult || !(checkResult as any).exists) {
      ElMessage.warning('车辆不存在，请确认车牌号')
      return
    }
    
    // 检查车辆是否为月租车
    if ((checkResult as any).isMonthly) {
      ElMessage.warning('该车辆是月租车，副车只能是临时车')
      return
    }
    
    const currentUserId = userStore.info?.userId || 1
    
    // 添加家庭成员
    const addFamilyData = {
      user_id: currentUserId,
      plate_number: inviteForm.value.plateNumber,
      bind_type: 2, // 2 表示副车
      parent_id: null // 先不设置 parentId，后端会自动处理
    }
    console.log('发送的参数:', addFamilyData)
    const result: any = await familyApi.addFamily(addFamilyData)
    console.log('添加结果:', result)
    
    // 重新加载数据
    await fetchData()
    
    ElMessage.success('邀请发送成功')
    showInviteDialog.value = false
    inviteForm.value = {
      plateNumber: '',
      remark: ''
    }
  } catch (error: any) {
    console.error('添加副车失败:', error)
    ElMessage.error('邀请失败，请重试：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const submitSettings = async () => {
  loading.value = true
  try {
    await familyApi.updateFamily({
      groupName: settingsForm.value.groupName,
      autoAuthorize: settingsForm.value.autoAuthorize,
      notification: settingsForm.value.notification
    })
    familyData.value.groupName = settingsForm.value.groupName
    ElMessage.success('设置保存成功')
    showSettingsDialog.value = false
  } catch (error) {
    ElMessage.error('保存设置失败，请重试')
  } finally {
    loading.value = false
  }
}

const submitCreateFamily = async () => {
  if (!createFamilyForm.value.mainCarPlate) {
    ElMessage.warning('请选择主车车牌号')
    return
  }
  
  loading.value = true
  try {
    // 检查车辆是否为月租车
    const checkResult: any = await familyApi.checkVehicle(createFamilyForm.value.mainCarPlate)
    console.log('车辆校验结果:', checkResult)
    if (!checkResult || !(checkResult as any).exists) {
      ElMessage.warning('车辆不存在，请确认车牌号')
      return
    }
    
    if (!(checkResult as any).isMonthly) {
      ElMessage.warning('该车辆不是月租车，无法作为主车')
      return
    }
    
    const currentUserId = userStore.info?.userId || 1
    
    // 创建新家庭组
    const addFamilyData = {
      user_id: currentUserId,
      plate_number: createFamilyForm.value.mainCarPlate,
      bind_type: 1 // 1表示主车
    }
    console.log('发送的参数:', addFamilyData)
    const result = await familyApi.addFamily(addFamilyData)
    
    ElMessage.success('家庭组创建成功')
    showCreateFamilyDialog.value = false
    
    // 重置表单
    createFamilyForm.value = {
      mainCarPlate: '',
      groupName: ''
    }
    
    // 重新加载数据
    await fetchData()
  } catch (error) {
    ElMessage.error('创建家庭组失败，请重试')
  } finally {
    loading.value = false
  }
}

const deleteFamilyGroup = async () => {
  ElMessageBox.confirm('确定要删除当前家庭组吗？此操作不可恢复。', '删除家庭组', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  } as any).then(async () => {
    loading.value = true
    try {
      const currentUserId = userStore.info?.userId || 1
      const familyList: any = await familyApi.fetchFamilyList({ user_id: currentUserId })
      if (familyList.records && familyList.records.length > 0) {
        // 假设当前显示的是第一个家庭组
        const currentFamily = familyList.records[0]
        if (currentFamily.id) {
          await familyApi.deleteFamily(currentFamily.id)
          ElMessage.success('家庭组删除成功')
          
          // 重新加载数据
          await fetchData()
        } else {
          ElMessage.error('无法获取家庭组 ID')
        }
      } else {
        ElMessage.error('没有找到家庭组')
      }
    } catch (error) {
      ElMessage.error('删除家庭组失败，请重试')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消操作
  })
}

// 加载家庭组数据
const fetchData = async () => {
  loading.value = true
  console.log('=== 开始加载家庭组数据 ===')
  try {
    // 重置数据
    familyData.value = {
      mainCar: '',
      expiryDays: 0,
      maxMembers: 0,
      currentMembers: 0,
      status: '',
      groupName: ''
    }
    members.value = []
    
    const currentUserId = userStore.info?.userId || 1
    console.log('当前用户 ID:', currentUserId)
    
    // 获取当前用户绑定的所有车辆列表（用于下拉选择）
    try {
      const vehicleListResponse: any = await vehicleApi.fetchVehicleList({})
      console.log('车辆列表响应:', vehicleListResponse)
      console.log('当前用户 ID:', currentUserId)
      
      if (vehicleListResponse.records) {
        console.log('所有车辆:', vehicleListResponse.records)
        // 打印车辆的所有字段，以便了解车辆数据结构
        if (vehicleListResponse.records.length > 0) {
          console.log('车辆数据结构:', Object.keys(vehicleListResponse.records[0]))
        }
        // 过滤出当前用户的车辆（使用类型转换确保比较正确，支持不同的字段名称）
        userVehiclesList.value = vehicleListResponse.records.filter((vehicle: any) => {
          // 尝试不同的用户ID字段名称
          const vehicleUserId = Number(vehicle.userId || vehicle.user_id || vehicle.UserId || vehicle.User_ID)
          const isCurrentUser = vehicleUserId === Number(currentUserId)
          console.log('车辆', vehicle.plateNumber, 'userId:', vehicle.userId, 'user_id:', vehicle.user_id, 'type:', vehicle.type, 'vehicleType:', vehicle.vehicleType, 'currentUserId:', currentUserId, 'isCurrentUser:', isCurrentUser)
          return isCurrentUser
        })
        console.log('过滤后的用户车辆:', userVehiclesList.value)
        console.log('月租车数量:', monthlyVehicles.value.length)
        console.log('临时车数量:', temporaryVehicles.value.length)
      }
    } catch (error) {
      console.error('获取用户车辆列表失败:', error)
    }
    
    // 获取家庭组列表
    try {
      console.log('准备调用 fetchFamilyList API')
      const familyList: any = await familyApi.fetchFamilyList({ user_id: currentUserId })
      console.log('fetchFamilyList API 调用完成')
      console.log('家庭组列表:', familyList)
      
      // 修复：familyList 本身就是 data，不需要再访问 familyList.data
      const familyDataList = familyList
      console.log('家庭组 records:', familyDataList?.records)
      console.log('家庭组 records 长度:', familyDataList?.records?.length)
      
      // 处理家庭组数据
      if (familyDataList && familyDataList.records && familyDataList.records.length > 0) {
        console.log('开始处理家庭组数据，records 数量:', familyDataList.records.length)
        // 打印所有记录看看
        familyDataList.records.forEach((record: any, index: number) => {
          console.log(`记录 ${index}:`, {
            id: record.id,
            plateNumber: record.plateNumber,
            bindType: record.bindType,
            userId: record.userId,
            subAccounts: record.subAccounts?.length
          })
        })
        
        // 获取第一个家庭组（主车）
        const mainCarRecord = familyDataList.records.find((record: any) => record.bindType === 1)
        console.log('找到的主车记录:', mainCarRecord)
        
        if (mainCarRecord) {
          console.log('准备设置 familyData.mainCar =', mainCarRecord.plateNumber)
          familyData.value.mainCar = mainCarRecord.plateNumber
          familyData.value.status = '正常'
          familyData.value.groupName = mainCarRecord.groupName || '我的家庭组'
          
          // 计算到期天数
          if (mainCarRecord.expireTime) {
            const expireDate = new Date(mainCarRecord.expireTime)
            const now = new Date()
            const diffTime = expireDate.getTime() - now.getTime()
            familyData.value.expiryDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
          }
          
          // 设置最大成员数（假设最大 3 个）
          familyData.value.maxMembers = 3
          
          // 统计当前成员数（1 个主车 + 副车数量）
          const subAccounts = mainCarRecord.subAccounts || []
          console.log('副车数量:', subAccounts.length)
          familyData.value.currentMembers = 1 + subAccounts.length
          
          // 填充成员列表
          members.value = subAccounts.map((sub: any) => ({
            id: sub.id,
            plate: sub.plateNumber,
            status: '未进场',
            active: true
          }))
          
          console.log('familyData 最终值:', familyData.value)
          console.log('members 最终值:', members.value)
        } else {
          console.log('未找到 bindType=1 的主车记录')
        }
      } else {
        console.log('家庭组列表为空或没有 records')
      }
    } catch (error) {
      console.error('获取家庭组列表失败:', error)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* 动画效果 */
.animate-ping {
  animation: ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
}

@keyframes ping {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 按钮样式 */
.el-button {
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
}

/* 输入框样式 */
.el-input__wrapper {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 2px rgba(45, 140, 240, 0.1);
}

/* 选择器样式 */
.el-select .el-input__wrapper {
  border-radius: 8px;
}

/* 对话框样式 */
.el-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.el-dialog__header {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.el-dialog__title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 标签样式 */
.el-tag {
  border-radius: 16px;
  padding: 0 12px;
  height: 24px;
  line-height: 22px;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
  transition: all 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .grid-cols-3 {
    grid-template-columns: 1fr;
  }
  
  .lg\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
  
  .p-6 {
    padding: 4px;
  }
  
  .text-xl {
    font-size: 1.25rem;
  }
  
  .text-lg {
    font-size: 1.125rem;
  }
}
</style>
