<template>
  <div id="app" class="h-full flex flex-col bg-slate-50 font-sans">
    <!-- 顶部导航 --> 
    <div class="bg-white border-b border-gray-200 sticky top-0 z-30 shadow-sm"> 
      <div class="w-full px-8 h-16 flex items-center justify-between"> 
        <div class="flex items-center gap-4"> 
          <button class="w-10 h-10 flex items-center justify-center rounded-xl hover:bg-gray-100 text-gray-500 transition-all active:scale-95" @click="goBack"> 
            <ArtSvgIcon icon="ri:arrow-left" /> 
          </button> 
        </div> 
          
        <!-- 筛选器 --> 
        <div class="flex gap-3"> 
          <button v-if="entity.plateNumber" @click="clearPlateFilter" class="bg-blue-50 text-blue-600 border border-blue-100 px-4 py-2 rounded-xl text-sm hover:bg-blue-100 transition shadow-sm">
            <ArtSvgIcon icon="ri:close-circle-line" class="mr-1" /> {{ entity.plateNumber }} ×
          </button>
          <button v-if="entity.onlyUnpaid" @click="entity.onlyUnpaid = false; reload()" class="bg-red-50 text-red-600 border border-red-100 px-4 py-2 rounded-xl text-sm hover:bg-red-100 transition shadow-sm">
            <ArtSvgIcon icon="ri:close-circle-line" class="mr-1" /> 取消待支付筛选
          </button>
          <div class="relative">
               <input type="text" id="monthPicker" class="bg-gray-50 border border-gray-200 text-gray-600 text-sm rounded-xl px-4 py-2 outline-none hover:border-blue-400 focus:border-blue-500 transition cursor-pointer w-40" placeholder="按月份筛选" readonly>
               <ArtSvgIcon icon="ri:calendar-line" class="absolute right-3 top-3 text-gray-400 pointer-events-none" />
          </div>

          <button class="bg-emerald-50 text-emerald-600 border border-emerald-100 px-4 py-2 rounded-xl text-sm hover:bg-emerald-100 transition-all shadow-sm flex items-center gap-2" @click="exportTable">
            <ArtSvgIcon icon="ri:file-excel-2-line" /> 导出
          </button>
          <button class="bg-blue-600 text-white px-5 py-2 rounded-xl text-sm hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 active:scale-95 flex items-center gap-2" @click="add">
            <ArtSvgIcon icon="ri:add-line" /> 新增
          </button>

        </div> 
      </div> 
    </div> 

    <!-- 主内容区域 -->
    <div class="w-full px-8 pt-6 pb-0 flex-1 flex flex-row gap-8 min-h-0">
        <!-- 左侧列表 -->
        <div class="list-wrapper">
            <div class="list-scroll-area">
                <div v-if="loading" class="text-center py-20 text-gray-400">
                    <i class="fas fa-circle-notch fa-spin text-3xl text-blue-500"></i>
                    <p class="mt-4 text-sm font-medium">正在检索停车明细...</p>
                </div>

                <div v-else-if="tableData.length > 0" class="space-y-2">
                    <div v-for="(item, index) in tableData" :key="index" 
                         class="g1-record-card group" 
                         :class="{'is-expanded': expandedId === item.id, 'is-unpaid': isUnpaid(item)}"
                         @click="toggleExpand(item.id)"> 
                        
                        <!-- 头部区域 - 紧凑布局 -->
                        <div class="px-4 py-3 flex items-center justify-between">
                            <!-- 左侧：图标 + 车辆信息 -->
                            <div class="flex items-center gap-3">
                                <div class="car-icon-container w-9 h-9 rounded-lg shrink-0 flex items-center justify-center transition-all duration-300" 
                                     :class="[
                                       (!item.gmtOut || (item.payStatus != 1 && parseFloat(item.cost) > 0)) ? 'text-red-500' : 'text-blue-500',
                                       expandedId === item.id ? 'car-icon-expanded' : ''
                                     ]">
                                    <ArtSvgIcon icon="ri:car-fill" class="text-lg" />
                                </div>
                                
                                <div class="min-w-0">
                                    <div class="flex items-center gap-2">
                                        <span class="font-bold text-lg text-slate-800 font-mono">{{item.plateNumber}}</span>
                                        <span class="mini-tag" 
                                              :class="{
                                                  'text-blue-500': item.type == 0,
                                                  'text-slate-400': item.type !== 0
                                              }">
                                            {{item.type == 0 ? '包月' : '临时'}}
                                        </span>
                                        <button 
                                          v-if="item.gmtOut && (item.payStatus == 1 || parseFloat(item.cost) <= 0)" 
                                          @click.stop="viewInvoice(item)" 
                                          class="invoice-btn mini-tag"
                                          style="color: #f97316; border-color: #fed7aa; background-color: #fff7ed;"
                                        >
                                          发票
                                        </button>
                                    </div>
                                    <div class="text-[11px] mt-0.5 flex items-center gap-1.5">
                                        <span class="text-slate-700">{{item.parkManageName}}</span>
                                        <span class="text-emerald-600 font-medium">{{item.parkingLot || '未分配'}}</span>
                                        <span class="text-slate-300">|</span>
                                        <span class="text-slate-500">{{item.gmtInto ? item.gmtInto.split(' ')[0] : '-'}}</span>
                                    </div>
                                </div>
                            </div>

                            <!-- 右侧：金额 + 状态 -->
                            <div class="flex items-center gap-4 shrink-0">
                                <div class="text-right">
                                    <div class="flex items-baseline justify-end gap-0.5">
                                        <span class="text-xs text-slate-400">¥</span>
                                        <span class="font-bold text-xl text-slate-900">{{item.cost || '0.00'}}</span>
                                    </div>
                                    <div class="mt-0.5">
                                        <span v-if="item.gmtOut && (item.payStatus == 0 || !item.payStatus) && (parseFloat(item.cost) > 0)" 
                                              class="status-tag text-red-500">
                                            待支付
                                        </span>
                                        <span v-else-if="!item.gmtOut" class="status-tag text-blue-500">
                                            停车中
                                        </span>
                                        <span v-else-if="item.payStatus == 1 || (parseFloat(item.cost) <= 0 && item.gmtOut)" 
                                              class="status-tag text-emerald-500">
                                            {{item.type == 0 ? '包月' : '已付'}}
                                        </span>
                                    </div>
                                </div>
                                <ArtSvgIcon 
                                    class="text-slate-300 group-hover:text-blue-400 transition-all duration-300" 
                                    :icon="expandedId === item.id ? 'ri:chevron-up' : 'ri:chevron-down'" 
                                    :class="[expandedId === item.id ? 'text-blue-500 rotate-180' : '']"
                                />
                            </div>
                        </div>

                        <!-- 展开详情区域 -->
                        <transition name="expand-fade">
                            <div v-if="expandedId === item.id" class="expand-content">
                                <div class="px-4 py-2.5 flex items-center gap-6 text-xs">
                                    <div class="flex items-center gap-1.5">
                                        <span class="text-slate-400">入场</span>
                                        <span class="text-slate-600 font-medium">{{item.gmtInto || '-'}}</span>
                                    </div>
                                    <div class="flex items-center gap-1.5">
                                        <span class="text-slate-400">出场</span>
                                        <span class="text-slate-600 font-medium">{{item.gmtOut || '尚未离场'}}</span>
                                    </div>
                                    <div class="flex items-center gap-1.5">
                                        <span class="text-slate-400">时长</span>
                                        <span class="text-blue-500 font-bold">{{calculateDuration(item.gmtInto, item.gmtOut)}}</span>
                                    </div>
                                    <div class="flex-1"></div>
                                    <button @click.stop="openModal('appeal')" class="text-slate-400 hover:text-slate-600 flex items-center gap-1 transition-colors">
                                        <ArtSvgIcon icon="ri:question-circle-line" /> 申诉
                                    </button>
                                    <button v-if="item.gmtOut && (item.payStatus == 0 || !item.payStatus) && (parseFloat(item.cost) > 0)"
                                            @click.stop="pay(item)"
                                            class="text-red-500 border border-red-300 px-3 py-1 rounded hover:bg-red-50 transition-all font-medium">
                                        支付 ¥{{item.cost}}
                                    </button>
                                </div>
                            </div>
                        </transition>
                    </div>
                </div>

                <div v-else class="text-center py-20 mt-10">
                    <div class="w-24 h-24 bg-white rounded-3xl shadow-inner flex items-center justify-center mx-auto mb-6 text-slate-200">
                        <i class="far fa-folder-open text-4xl"></i>
                    </div>
                    <p class="text-slate-400 font-medium">暂无相关的停车流水记录</p>
                </div>
            </div>

            <!-- 底部翻页 -->
            <div class="flex-shrink-0 px-8 py-4 flex justify-between items-center bg-white/50 border-t border-slate-100 backdrop-blur-sm">
                 <div class="text-xs text-slate-400 font-medium">第 <span class="text-slate-900 font-bold">{{entity.pageNo}}</span> / {{totalPage}} 页</div>
                 <div class="flex gap-3">
                     <button @click="prevPage" :disabled="entity.pageNo <= 1" class="px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs font-bold text-slate-600 hover:bg-slate-50 disabled:opacity-50 transition-all shadow-sm flex items-center gap-2">
                        <i class="fas fa-chevron-left text-[10px]"></i> 上一页
                     </button>
                     <button @click="nextPage" :disabled="entity.pageNo >= totalPage" class="px-4 py-2 bg-white border border-slate-200 rounded-xl text-xs font-bold text-slate-600 hover:bg-slate-50 disabled:opacity-50 transition-all shadow-sm flex items-center gap-2">
                        下一页 <i class="fas fa-chevron-right text-[10px]"></i>
                     </button>
                 </div>
            </div>
        </div>

        <!-- 右侧侧边栏 -->
        <div class="sidebar-wrapper list-scroll-area">
            <div class="dashboard-card group"> 
                <div class="flex justify-between items-start"> 
                    <div class="text-slate-400 text-[11px] font-black uppercase tracking-widest">累计停车消费</div> 
                    <div class="bg-blue-50 text-blue-600 w-10 h-10 flex items-center justify-center rounded-2xl text-sm group-hover:scale-110 transition-transform duration-500"><ArtSvgIcon icon="ri:wallet-2-line" /></div> 
                </div> 
                <div class="mt-6"> 
                    <div class="flex items-baseline gap-1">
                         <span class="text-3xl font-black text-slate-900 tracking-tighter">¥ {{totalCostAll}}</span> 
                    </div>
                    <div class="mt-2 text-xs text-slate-400 font-bold">共 {{tableSize}} 笔流水记录</div> 
                </div> 
            </div> 
              
            <div class="dashboard-card group"> 
                <div class="flex justify-between items-start"> 
                    <div class="text-slate-400 text-[11px] font-black uppercase tracking-widest">总计入场次数</div> 
                    <div class="bg-emerald-50 text-emerald-600 w-10 h-10 flex items-center justify-center rounded-2xl text-sm group-hover:rotate-12 transition-transform duration-500"><ArtSvgIcon icon="ri:time-line" /></div> 
                </div> 
                <div class="mt-6"> 
                     <div class="flex items-baseline gap-1">
                        <span class="text-3xl font-black text-slate-900 tracking-tighter">{{tableSize}}</span>
                        <span class="text-xs text-slate-400 font-bold uppercase">times</span>
                     </div>
                     <div class="mt-2 text-xs text-emerald-600 flex items-center font-bold">
                        <ArtSvgIcon icon="ri:check-circle-line" class="mr-1.5" /> 数据同步至最新
                     </div>
                </div> 
            </div> 

            <div class="bg-gradient-to-br from-blue-600 to-indigo-700 rounded-3xl p-6 text-white shadow-xl shadow-blue-200 relative overflow-hidden group">
                <ArtSvgIcon icon="ri:double-quotes-l" class="absolute -top-2 -left-2 text-white/10 text-6xl" />
                <div class="relative z-10">
                    <h4 class="font-bold text-sm mb-2">温馨提示</h4>
                    <p class="text-[11px] leading-relaxed opacity-90 font-medium">
                        为了您的车辆安全，建议开启临停支付后的自动抬杆功能。连续停放超过 7 天建议联系物业报备。
                    </p>
                </div>
            </div>
        </div>
    </div> 

    <!-- 停车缴费弹窗 -->
    <ElDialog
      v-model="paymentDialogVisible"
      title="停车缴费"
      width="360px"
      center
      :show-close="false"
    >
      <div class="py-4 px-4">
        <!-- 标题 -->
        <div class="text-center mb-4">
          <h3 class="text-xl font-bold text-gray-800 mb-1">确认支付</h3>
          <p class="text-sm text-gray-500">请核对您的停车费用并扫描二维码</p>
        </div>
        
        <!-- 订单信息 -->
        <div class="space-y-3 mb-4">
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">车牌号</span>
            <span class="font-medium">{{ currentPayItem?.plateNumber }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">停车场</span>
            <span class="font-medium">{{ currentPayItem?.parkingLot || '未分配' }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId || generateOrderId() }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-500">支付总额</span>
            <span class="text-2xl font-bold">¥{{ currentPayItem?.cost || '0.00' }}</span>
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
          您的停车费用已支付成功，祝您一路平安！
        </p>
        
        <!-- 订单信息 -->
        <div class="w-full bg-gray-50 rounded-lg p-4 mb-6">
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">车牌号</span>
            <span class="font-medium">{{ currentPayItem?.plateNumber }}</span>
          </div>
          <div class="flex justify-between text-sm mb-2">
            <span class="text-gray-500">订单编号</span>
            <span class="font-medium">{{ currentOrderId }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-500">支付金额</span>
            <span class="font-bold text-green-600">¥{{ currentPayItem?.cost || '0.00' }}</span>
          </div>
        </div>
        
        <!-- 确认按钮 -->
        <button 
          class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-medium transition-colors"
          @click="paymentSuccessVisible = false; reload()"
        >
          确定
        </button>
      </div>
    </ElDialog>

    <!-- 通用弹窗 -->
    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm" @click="closeModal"></div>
        <div class="relative bg-white rounded-2xl shadow-2xl z-10 w-full max-w-md overflow-hidden">
            <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
                <h3 class="font-bold text-lg text-slate-800">{{ modalTitle }}</h3>
                <button @click="closeModal" class="w-8 h-8 rounded-lg hover:bg-slate-100 flex items-center justify-center text-slate-400 hover:text-slate-600 transition-colors">
                    <ArtSvgIcon icon="ri:close-line" class="text-xl" />
                </button>
            </div>
            <div class="p-8">
                <div v-if="modalType === 'invoice'" class="text-center space-y-8">
                    <!-- 初始状态 - 发票开具申请 -->
                    <div v-if="invoiceStatus === 'initial'" class="space-y-6">
                        <div class="w-20 h-20 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center mx-auto shadow-lg shadow-blue-200">
                            <ArtSvgIcon icon="ri:file-text-line" class="text-3xl text-white" />
                        </div>
                        <h3 class="text-2xl font-bold text-gray-800">发票开具申请</h3>
                        <div class="bg-white rounded-2xl p-6 shadow-md border border-gray-100 hover:shadow-lg transition-shadow">
                            <div class="space-y-4 text-left">
                                <div class="flex justify-between items-center">
                                    <span class="text-sm text-gray-500">抬头名称</span>
                                    <input 
                                        v-model="invoiceForm.invoiceTitle" 
                                        type="text" 
                                        class="px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors w-48 text-right"
                                        placeholder="请输入抬头"
                                    />
                                </div>
                                <div class="flex justify-between items-center py-2 border-t border-gray-100">
                                    <span class="text-sm text-gray-500">开票金额</span>
                                    <span class="text-2xl font-bold text-blue-600">¥{{ currentInvoiceItem?.cost || '0.00' }}</span>
                                </div>
                            </div>
                        </div>
                        <button @click="submitInvoice" class="w-full bg-gradient-to-r from-blue-600 to-indigo-700 text-white py-3 rounded-2xl font-bold hover:from-blue-700 hover:to-indigo-800 transition-all shadow-lg shadow-blue-200 transform hover:-translate-y-1">
                            提交审核申请
                        </button>
                        <p class="text-xs text-gray-400 max-w-md mx-auto">根据财务要求，发票申请将提交至后台管理端进行人工审核，审核通过后将为您开具。</p>
                    </div>
                    
                    <!-- 审批中状态 - 正在生成电子发票 -->
                    <div v-else-if="invoiceStatus === 'pending'" class="space-y-6">
                        <div class="w-20 h-20 bg-gradient-to-br from-yellow-500 to-amber-600 rounded-full flex items-center justify-center mx-auto shadow-lg shadow-yellow-200">
                            <svg class="w-10 h-10 text-white animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                        </div>
                        <h3 class="text-2xl font-bold text-gray-800">正在生成电子发票...</h3>
                        <p class="text-sm text-gray-500">您的申请已提交，请耐心等待</p>
                        <div class="bg-white rounded-2xl p-6 shadow-md border border-gray-100 hover:shadow-lg transition-shadow">
                            <div class="space-y-4 text-left">
                                <div class="flex justify-between items-center">
                                    <span class="text-sm text-gray-500">流水单号</span>
                                    <span class="font-medium text-gray-800">{{ currentInvoiceId || '生成中' }}</span>
                                </div>
                                <div class="flex justify-between items-center py-2 border-t border-gray-100">
                                    <span class="text-sm text-gray-500">当前状态</span>
                                    <span class="px-4 py-1.5 bg-blue-100 text-blue-700 rounded-full text-sm font-medium">等待人工审核</span>
                                </div>
                            </div>
                        </div>
                        <button @click="closeModal" class="w-full bg-gray-100 text-gray-700 py-3 rounded-2xl font-bold hover:bg-gray-200 transition-colors transform hover:-translate-y-1">
                            我知道了
                        </button>
                        <p class="text-xs text-gray-400 max-w-md mx-auto">系统预计在5分钟内完成处理。您可以在"停车记录"中查看实时进度。</p>
                    </div>
                    
                    <!-- 已通过状态 - 发票已开具 -->
                    <div v-else-if="invoiceStatus === 'approved'" class="space-y-6">
                        <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto">
                            <ArtSvgIcon icon="ri:check-line" class="text-2xl text-blue-500" />
                        </div>
                        <h3 class="text-xl font-bold text-gray-800">发票已开具</h3>
                        <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                            <div class="space-y-4 text-left">
                                <div class="flex justify-between items-center py-2">
                                    <span class="text-sm text-gray-500">流水单号</span>
                                    <span class="font-medium text-gray-800">{{ currentInvoiceItem?.orderNo || '生成中' }}</span>
                                </div>
                                <div class="flex justify-between items-center py-2">
                                    <span class="text-sm text-gray-500">开票金额</span>
                                    <span class="text-2xl font-bold text-blue-600">¥{{ currentInvoiceItem?.cost || '0.00' }}</span>
                                </div>
                            </div>
                        </div>
                        <button @click="downloadInvoice" class="w-full bg-blue-600 text-white py-3 rounded-xl font-bold hover:bg-blue-700 transition-colors">
                            下载电子发票 (PDF)
                        </button>
                        <p class="text-xs text-gray-400 max-w-md mx-auto">依据国家税务总局令，电子发票与纸质发票具有同等法律效力。</p>
                    </div>
                </div>
                <div v-else-if="modalType === 'appeal'" class="text-center">
                    <div class="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-4">
                        <ArtSvgIcon icon="ri:question-circle-line" class="text-3xl text-blue-500" />
                    </div>
                    <p class="text-slate-600 mb-4">如有订单疑问，请联系客服处理</p>
                    <p class="text-sm text-slate-400 mb-6">客服电话：400-123-4567</p>
                    <button @click="closeModal" class="w-full bg-slate-900 text-white py-3 rounded-xl font-bold hover:bg-slate-800 transition-colors">
                        我知道了
                    </button>
                </div>
                <div v-else-if="modalType === 'export'" class="text-center">
                    <div class="w-16 h-16 bg-emerald-50 rounded-full flex items-center justify-center mx-auto mb-4">
                        <ArtSvgIcon icon="ri:file-excel-2-line" class="text-3xl text-emerald-500" />
                    </div>
                    <p class="text-slate-600 mb-4">数据导出成功！</p>
                    <button @click="closeModal" class="w-full bg-emerald-500 text-white py-3 rounded-xl font-bold hover:bg-emerald-600 transition-colors">
                        完成
                    </button>
                </div>
                <div v-else-if="modalType === 'add'" class="text-center">
                    <div class="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-4">
                        <ArtSvgIcon icon="ri:add-line" class="text-3xl text-blue-500" />
                    </div>
                    <p class="text-slate-600 mb-4">新增停车记录功能开发中...</p>
                    <button @click="closeModal" class="w-full bg-blue-500 text-white py-3 rounded-xl font-bold hover:bg-blue-600 transition-colors">
                        我知道了
                    </button>
                </div>
                <div v-else-if="modalType === 'back'" class="text-center">
                    <div class="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <ArtSvgIcon icon="ri:arrow-left" class="text-3xl text-slate-500" />
                    </div>
                    <p class="text-slate-600 mb-4">确定返回上一页吗？</p>
                    <div class="flex gap-3">
                        <button @click="closeModal" class="flex-1 bg-slate-100 text-slate-600 py-3 rounded-xl font-bold hover:bg-slate-200 transition-colors">
                            取消
                        </button>
                        <button @click="confirmBack" class="flex-1 bg-slate-900 text-white py-3 rounded-xl font-bold hover:bg-slate-800 transition-colors">
                            确定
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
import { invoiceApi, recordApi, vasOrderApi, sysConfigApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'
import { useRoute } from 'vue-router'
import QrcodeVue from 'qrcode.vue'

defineOptions({ name: 'UserParkingRecord' })

interface ParkingRecord {
  id: number
  plateNumber: string
  type: number
  method: string
  payStatus: number
  cost: string
  gmtInto: string
  gmtOut: string
  parkManageName: string
  parkingLot: string
  orderNo?: string
}

interface InvoiceConfig {
  invoiceType: string
  invoiceTitle: string
  invoiceContent: string
}

const tableData = ref<ParkingRecord[]>([])
const entity = ref({ pageSize: 5, pageNo: 1, plateNumber: '', searchMonth: '', onlyUnpaid: false })
const tableSize = ref(0)
const loading = ref(false)
const expandedId = ref<number | null>(null)
const totalCostAll = ref('0.00')
const userStore = useUserStore()
const route = useRoute()

const showModal = ref(false)
const modalType = ref('')
const modalTitle = ref('')
const currentPayItem = ref<ParkingRecord | null>(null)
const currentInvoiceItem = ref<ParkingRecord | null>(null)
const invoiceStatus = ref<'initial' | 'pending' | 'approved'>('initial')
const currentInvoiceId = ref<number | null>(null)
const invoiceConfig = ref<InvoiceConfig>({
  invoiceType: '增值税电子普通发票',
  invoiceTitle: '个人',
  invoiceContent: '*停车服务*车辆停放费'
})

const invoiceForm = ref<InvoiceConfig>({
  invoiceType: '增值税电子普通发票',
  invoiceTitle: '个人',
  invoiceContent: '*停车服务*车辆停放费'
})

const fetchInvoiceConfig = async (): Promise<void> => {
  try {
    const response: any = await sysConfigApi.fetchInvoiceConfig()
    if (response.data) {
      invoiceConfig.value = response.data
    }
  } catch (error) {
    console.error('获取发票配置失败:', error)
  }
}

// 支付弹窗相关状态
const paymentDialogVisible = ref(false)
const paymentSuccessVisible = ref(false)
const currentOrderId = ref('')

const modalTitles: Record<string, string> = {
  pay: '支付确认',
  invoice: '电子发票',
  appeal: '订单申诉',
  export: '导出数据',
  add: '新增记录',
  back: '返回确认'
}

const openModal = async (type: string, item?: ParkingRecord): Promise<void> => {
  modalType.value = type
  modalTitle.value = modalTitles[type] || '提示'
  if (type === 'invoice' && item) {
    currentInvoiceItem.value = item
    await fetchInvoiceConfig()
    invoiceForm.value = { ...invoiceConfig.value }
    
    const orderNo = item.orderNo || item.id?.toString()
    if (!orderNo) {
      ElMessage.error('订单信息不完整，无法申请发票')
      return
    }
    
    await fetchInvoiceStatus(orderNo)
  }
  showModal.value = true
}

const fetchInvoiceStatus = async (orderNo: string): Promise<void> => {
  try {
    const data: any = await invoiceApi.fetchInvoiceStatus(orderNo)
    
    if (!data || data.status === undefined || data.status === null) {
      invoiceStatus.value = 'initial'
      currentInvoiceId.value = null
      return
    }
    
    currentInvoiceId.value = data.id || null
    
    if (data.status === 0) {
      invoiceStatus.value = 'pending'
    } else if (data.status === 1) {
      invoiceStatus.value = 'approved'
    } else {
      invoiceStatus.value = 'initial'
    }
  } catch (error) {
    console.error('获取发票状态失败:', error)
    invoiceStatus.value = 'initial'
    currentInvoiceId.value = null
  }
}

const closeModal = (): void => {
  showModal.value = false
  modalType.value = ''
  currentPayItem.value = null
  currentInvoiceItem.value = null
  // 不重置invoiceStatus，保持当前状态
  // 这样用户关闭弹窗后重新打开时，状态会保持不变
}

const totalPage = computed(() => Math.ceil(tableSize.value / entity.value.pageSize) || 1)

const getTypeText = (type: number): string => {
  if(type === 0) return '包月车辆'
  return '临时车辆'
}

const calculateDuration = (gmtInto: string, gmtOut: string): string => {
  if (!gmtInto) return '0分钟'
  const start = new Date(gmtInto).getTime()
  const end = gmtOut ? new Date(gmtOut).getTime() : Date.now()
  const diff = Math.floor((end - start) / 1000 / 60)
  if (diff < 60) return `${diff}min`
  return `${Math.floor(diff/60)}h ${diff%60}m`
}

const isUnpaid = (item: ParkingRecord): boolean => {
  return !!item.gmtOut && (item.payStatus == 0 || !item.payStatus) && (parseFloat(item.cost) > 0)
}

const toggleExpand = (id: number): void => {
  expandedId.value = (expandedId.value === id) ? null : id
}

const load = async (): Promise<void> => {
    loading.value = true
    try {
      // 从用户状态中获取当前登录用户的ID
      const userId = userStore.info?.userId
      
      const res: any = await recordApi.fetchRecordList({
        current: entity.value.pageNo,
        size: entity.value.pageSize,
        plateNumber: entity.value.plateNumber || undefined,
        status: entity.value.onlyUnpaid ? 0 : undefined,
        userId: userId
      })
      
      if (res?.records) {
        tableData.value = res.records
        tableSize.value = res.total || 0
        
        // 计算总消费
        totalCostAll.value = tableData.value.reduce((total, item) => {
          return (parseFloat(total) + parseFloat(item.cost || '0')).toFixed(2)
        }, '0.00')
      }
    } catch (error) {
      console.error('获取停车记录失败:', error)
      ElMessage.error('获取停车记录失败')
    } finally {
      loading.value = false
    }
  }

const reload = async (): Promise<void> => {
  entity.value.pageNo = 1
  await load()
}

const clearPlateFilter = async (): Promise<void> => {
  entity.value.plateNumber = ''
  await reload()
}

const prevPage = async (): Promise<void> => {
  if (entity.value.pageNo > 1) {
    entity.value.pageNo--
    await load()
  }
}

const nextPage = async (): Promise<void> => {
  if (entity.value.pageNo < totalPage.value) {
    entity.value.pageNo++
    await load()
  }
}

const goBack = (): void => {
  openModal('back')
}

const confirmBack = (): void => {
  closeModal()
  window.history.back()
}

const exportTable = (): void => {
  openModal('export')
}

const add = (): void => {
  openModal('add')
}

const remove = (id: number): void => {
  ElMessage.error('删除成功')
}

const pay = (item: ParkingRecord): void => {
  currentPayItem.value = item
  paymentDialogVisible.value = true
}

const generateOrderId = () => {
  const prefix = 'PK'
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
      orderType: 0,
      body: '临停缴费',
      payAmount: parseFloat(currentPayItem.value.cost || '0'),
      originalAmount: parseFloat(currentPayItem.value.cost || '0'),
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

const viewInvoice = (item: any): void => {
  openModal('invoice', item)
}

const submitInvoice = async (): Promise<void> => {
  if (!currentInvoiceItem.value) return
  
  try {
    const userId = userStore.info?.userId || 5
    
    let invoiceTypeValue = 3
    if (invoiceForm.value.invoiceType === '增值税电子普通发票') {
      invoiceTypeValue = 1
    } else if (invoiceForm.value.invoiceType === '增值税电子专用发票') {
      invoiceTypeValue = 2
    }
    
    const orderNo = currentInvoiceItem.value.orderNo || currentInvoiceItem.value.id?.toString()
    if (!orderNo) {
      ElMessage.error('订单信息不完整，无法提交申请')
      return
    }
    
    const invoiceData = {
      orderNo: orderNo,
      userId: userId,
      amount: parseFloat(currentInvoiceItem.value.cost),
      invoiceType: invoiceTypeValue,
      invoiceTitle: invoiceForm.value.invoiceTitle,
      invoiceContent: invoiceForm.value.invoiceContent,
      email: 'user@example.com',
      status: 0
    }
    
    const response: any = await invoiceApi.submitInvoice(invoiceData)
    
    if (response?.id) {
      currentInvoiceId.value = response.id
    }
    
    invoiceStatus.value = 'pending'
    ElMessage.success('申请已提交，正在等待管理员审核')
  } catch (error) {
    console.error('提交发票申请失败:', error)
    ElMessage.error('提交申请失败，请重试')
  }
}

const downloadInvoice = async (): Promise<void> => {
  if (!currentInvoiceId.value) {
    ElMessage.error('发票信息不完整，请重新打开')
    return
  }
  
  try {
    const response = await invoiceApi.downloadInvoice(currentInvoiceId.value)
    
    const blob = response instanceof Blob ? response : new Blob([response as any], { type: 'application/pdf' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `发票_${currentInvoiceItem.value?.plateNumber || 'download'}_${Date.now()}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    URL.revokeObjectURL(url)
    
    ElMessage.success('电子发票已开始下载')
  } catch (error) {
    console.error('下载发票失败:', error)
    ElMessage.error('下载发票失败，请重试')
  }
}

onMounted(async () => {
  const plateNumber = route.query.plateNumber as string
  if (plateNumber) {
    entity.value.plateNumber = plateNumber
  }
  
  await load()
  await fetchInvoiceConfig()
})
</script>

<style scoped>
/* 核心自定义样式 */
.list-wrapper {
  flex: 7;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-wrapper {
  flex: 3;
  min-width: 320px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.list-scroll-area {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 24px;
  scroll-behavior: smooth;
}

/* 自定义滚动条 */
.list-scroll-area::-webkit-scrollbar {
  width: 4px;
}

.list-scroll-area::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}

/* 卡片样式 - 紧凑型 */
.g1-record-card {
  background: white;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  cursor: pointer;
}

.g1-record-card:hover {
  border-color: #d1d5db;
  background: #fafafa;
}

.g1-record-card.is-expanded {
  border-color: #3b82f6;
  background: white;
}

.g1-record-card.is-unpaid {
  border-color: #fca5a5;
  background: #fef2f2;
}

.g1-record-card.is-unpaid:hover {
  border-color: #f87171;
  background: #fee2e2;
}

.car-icon-expanded {
  transform: scale(1.1);
}

.car-icon-container {
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

/* 标签样式 */
.mini-tag {
  font-size: 10px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid;
}

.mini-tag.text-blue-500 {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.mini-tag.text-slate-400 {
  border-color: #94a3b8;
  background-color: #f1f5f9;
}

.status-tag {
  font-size: 10px;
  font-weight: 500;
}

.invoice-btn {
  font-size: 10px;
  font-weight: 500;
  color: #f97316;
  padding: 2px 8px;
  border: 1px solid #fed7aa;
  border-radius: 4px;
  background: #fff7ed;
  cursor: pointer;
  transition: all 0.2s;
}

.invoice-btn:hover {
  background: #ffedd5;
  border-color: #fdba74;
}

/* 展开内容 */
.expand-content {
  background: linear-gradient(to right, #eff6ff, #f8fafc);
  border-top: 1px solid #dbeafe;
}

/* 侧边栏卡片 */
.dashboard-card {
  background: white;
  border-radius: 14px;
  padding: 18px;
  border: 1px solid #e5e7eb;
}

/* 展开动画 */
.expand-fade-enter-active {
  transition: all 0.2s ease-out;
}

.expand-fade-leave-active {
  transition: all 0.15s ease-in;
}

.expand-fade-enter-from,
.expand-fade-leave-to {
  opacity: 0;
}
</style>