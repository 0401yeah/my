import request from '@/utils/http'

/**
 * 车辆管理相关 API
 */
export const vehicleApi = {
  /**
   * 获取车辆列表
   * @param params 查询参数
   * @returns 车辆列表
   */
  fetchVehicleList: (params: any) => {
    return request.get({
      url: '/api/vehicle/list',
      params
    })
  },

  /**
   * 获取车辆详情
   * @param id 车辆 ID
   * @returns 车辆详情
   */
  fetchVehicleDetail: (id: number) => {
    return request.get({
      url: `/api/vehicle/detail/${id}`
    })
  },

  /**
   * 添加车辆
   * @param data 车辆数据
   * @returns 添加结果
   */
  addVehicle: (data: any) => {
    return request.post({
      url: '/api/vehicle/add',
      data: data,
      showSuccessMessage: false
    })
  },

  /**
   * 更新车辆
   * @param data 车辆数据
   * @returns 更新结果
   */
  updateVehicle: (data: any) => {
    return request.put({
      url: '/api/vehicle/update',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除车辆
   * @param id 车辆 ID
   * @returns 删除结果
   */
  deleteVehicle: (id: number) => {
    return request.del({
      url: `/api/vehicle/delete/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 停车记录相关 API
 */
export const recordApi = {
  /**
   * 获取停车记录列表
   * @param params 查询参数
   * @returns 停车记录列表
   */
  fetchRecordList: (params: any) => {
    return request.get({
      url: '/api/record/list',
      params
    })
  },

  /**
   * 获取停车记录详情
   * @param id 记录 ID
   * @returns 记录详情
   */
  fetchRecordDetail: (id: number) => {
    return request.get({
      url: `/api/record/detail/${id}`
    })
  },

  /**
   * 更新停车记录
   * @param data 记录数据
   * @returns 更新结果
   */
  updateRecord: (data: any) => {
    return request.put({
      url: '/api/record/update',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除停车记录
   * @param id 记录 ID
   * @returns 删除结果
   */
  deleteRecord: (id: number) => {
    return request.del({
      url: `/api/record/delete/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 更新停车记录支付状态
   * @param id 记录 ID
   * @param status 支付状态
   * @returns 更新结果
   */
  updateRecordPaymentStatus: (id: number, status: number) => {
    return request.get({
      url: '/api/record/updatePaymentStatus',
      params: { id, status },
      showSuccessMessage: true
    })
  }
}

/**
 * 车位管理相关 API
 */
export const spaceApi = {
  /**
   * 获取车位列表
   * @param params 查询参数
   * @returns 车位列表
   */
  fetchSpaceList: (params: any) => {
    return request.get({
      url: '/api/parking-space/list',
      params
    })
  },

  /**
   * 获取车位详情
   * @param id 车位 ID
   * @returns 车位详情
   */
  fetchSpaceDetail: (id: number) => {
    return request.get({
      url: `/api/parking-space/${id}`
    })
  },

  /**
   * 更新车位状态
   * @param data 车位数据
   * @returns 更新结果
   */
  updateSpaceStatus: (data: any) => {
    return request.put({
      url: '/api/parking-space',
      data: data,
      showSuccessMessage: true
    })
  }
}

/**
 * 设施管理相关 API
 */
export const facilityApi = {
  /**
   * 获取设施列表
   * @param params 查询参数
   * @returns 设施列表
   */
  fetchFacilityList: (params: any) => {
    return request.get({
      url: '/api/equipment/list',
      params
    })
  },

  /**
   * 获取设施详情
   * @param id 设施 ID
   * @returns 设施详情
   */
  fetchFacilityDetail: (id: number) => {
    return request.get({
      url: `/api/equipment/${id}`
    })
  },

  /**
   * 添加设施
   * @param data 设施数据
   * @returns 添加结果
   */
  addFacility: (data: any) => {
    return request.post({
      url: '/api/equipment',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新设施
   * @param data 设施数据
   * @returns 更新结果
   */
  updateFacility: (data: any) => {
    return request.put({
      url: '/api/equipment',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除设施
   * @param id 设施 ID
   * @returns 删除结果
   */
  deleteFacility: (id: number) => {
    return request.del({
      url: `/api/equipment/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 黑白名单相关 API
 */
export const blacklistApi = {
  /**
   * 获取黑白名单列表
   * @param params 查询参数
   * @returns 黑白名单列表
   */
  fetchBlacklistList: (params: any) => {
    return request.get({
      url: '/api/blacklist/list',
      params
    })
  },

  /**
   * 添加黑白名单
   * @param data 黑白名单数据
   * @returns 添加结果
   */
  addBlacklist: (data: any) => {
    return request.post({
      url: '/api/blacklist/add',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除黑白名单
   * @param id 黑白名单 ID
   * @returns 删除结果
   */
  deleteBlacklist: (id: number) => {
    return request.del({
      url: `/api/blacklist/delete/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 更新黑白名单
   * @param data 黑白名单数据
   * @returns 更新结果
   */
  updateBlacklist: (data: any) => {
    return request.put({
      url: '/api/blacklist/update',
      data: data,
      showSuccessMessage: true
    })
  }
}

/**
 * 亲情组相关 API
 */
export const familyApi = {
  fetchFamilyList: (params: any) => {
    return request.get({
      url: '/api/family/list',
      params
    })
  },

  addFamily: (data: any) => {
    return request.post({
      url: '/api/family/add',
      data: data,
      showSuccessMessage: false
    })
  },

  deleteFamily: (id: number) => {
    return request.del({
      url: `/api/family/delete/${id}`,
      showSuccessMessage: false
    })
  },

  updateFamily: (data: any) => {
    return request.put({
      url: '/api/family/update',
      data: data,
      showSuccessMessage: true
    })
  },

  toggleStatus: (id: number) => {
    return request.put({
      url: `/api/family/toggle-status/${id}`,
      showSuccessMessage: true
    })
  },

  checkVehicle: (plateNumber: string) => {
    return request.get({
      url: '/api/family/check-vehicle',
      params: { plate_number: plateNumber }
    })
  },

  getUserVehicles: (userId: number) => {
    return request.get({
      url: '/api/family/user-vehicles',
      params: { user_id: userId }
    })
  }
}

/**
 * 订单管理相关 API
 */
export const orderApi = {
  /**
   * 获取订单明细列表（合并订单和停车记录）
   * @param params 查询参数
   * @returns 订单明细列表
   */
  fetchOrderDetailList: (params: any) => {
    return request.get({
      url: '/api/order/detail-list',
      params
    })
  },

  /**
   * 获取订单列表
   * @param params 查询参数
   * @returns 订单列表
   */
  fetchOrderList: (params: any) => {
    return request.get({
      url: '/api/order/list',
      params
    })
  },

  /**
   * 获取订单详情
   * @param id 订单 ID
   * @returns 订单详情
   */
  fetchOrderDetail: (id: number) => {
    return request.get({
      url: `/api/order/detail/${id}`
    })
  },

  /**
   * 新增订单
   * @param data 订单数据
   * @returns 新增结果
   */
  addOrder: (data: any) => {
    return request.post({
      url: '/api/order/add',
      data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新订单
   * @param data 订单数据
   * @returns 更新结果
   */
  updateOrder: (data: any) => {
    return request.put({
      url: '/api/order/update',
      data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除订单
   * @param id 订单 ID
   * @returns 删除结果
   */
  deleteOrder: (id: number) => {
    return request.del({
      url: `/api/order/delete/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 更新订单状态
   * @param id 订单 ID
   * @param status 订单状态
   * @returns 更新结果
   */
  updateOrderStatus: (id: number, status: number) => {
    return request.get({
      url: '/api/order/updateStatus',
      params: { id, status },
      showSuccessMessage: true
    })
  }
}

/**
 * 发票管理相关 API
 */
export const invoiceApi = {
  /**
   * 获取发票列表
   * @param params 查询参数
   * @returns 发票列表
   */
  fetchInvoiceList: (params: any) => {
    return request.get({
      url: '/api/invoice/list',
      params
    })
  },
  
  /**
   * 获取发票状态
   * @param orderNo 订单号
   * @returns 发票状态
   */
  fetchInvoiceStatus: (orderNo: string) => {
    return request.get({
      url: '/api/invoice/status',
      params: { orderNo }
    })
  },
  
  /**
   * 提交发票申请
   * @param data 发票申请数据
   * @returns 申请结果
   */
  submitInvoice: (data: any) => {
    return request.post({
      url: '/api/invoice/apply',
      data,
      showSuccessMessage: true
    })
  },
  
  /**
   * 下载发票PDF
   * @param id 发票ID
   * @returns PDF文件Blob
   */
  downloadInvoice: (id: number) => {
    return request.get({
      url: `/api/invoice/download/${id}`,
      responseType: 'blob'
    })
  },
  
  /**
   * 审批发票（管理员）
   * @param id 发票ID
   * @returns 审批结果
   */
  approveInvoice: (id: number) => {
    return request.post({
      url: `/api/invoice/approve/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 投诉管理相关 API
 */
export const complaintApi = {
  /**
   * 获取投诉列表
   * @param params 查询参数
   * @returns 投诉列表
   */
  fetchComplaintList: (params: any) => {
    return request.get({
      url: '/api/complaint/list',
      params
    })
  }
}

/**
 * 维保管理相关 API
 */
export const maintainApi = {
  /**
   * 获取维保列表
   * @param params 查询参数
   * @returns 维保列表
   */
  fetchMaintainList: (params: any) => {
    return request.get({
      url: '/api/maintain/list',
      params
    })
  }
}

/**
 * 公告管理相关 API
 */
export const noticeApi = {
  /**
   * 获取公告列表
   * @param params 查询参数
   * @returns 公告列表
   */
  fetchNoticeList: (params: any) => {
    return request.get({
      url: '/api/notice/list',
      params
    })
  },

  /**
   * 获取公告详情
   * @param id 公告 ID
   * @returns 公告详情
   */
  fetchNoticeDetail: (id: number) => {
    return request.get({
      url: `/api/notice/detail/${id}`
    })
  },

  /**
   * 添加公告
   * @param data 公告数据
   * @returns 添加结果
   */
  addNotice: (data: any) => {
    return request.post({
      url: '/api/notice/add',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新公告
   * @param data 公告数据
   * @returns 更新结果
   */
  updateNotice: (data: any) => {
    return request.put({
      url: '/api/notice/update',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除公告
   * @param id 公告 ID
   * @returns 删除结果
   */
  deleteNotice: (id: number) => {
    return request.del({
      url: `/api/notice/delete/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 停车场管理相关 API
 */
export const lotApi = {
  /**
   * 获取停车场列表
   * @param params 查询参数
   * @returns 停车场列表
   */
  fetchLotList: (params: any) => {
    return request.get({
      url: '/api/lot/list',
      params
    })
  },

  /**
   * 获取停车场详情
   * @param id 停车场 ID
   * @returns 停车场详情
   */
  fetchLotDetail: (id: number) => {
    return request.get({
      url: `/api/lot/detail/${id}`
    })
  },

  /**
   * 添加停车场
   * @param data 停车场数据
   * @returns 添加结果
   */
  addLot: (data: any) => {
    return request.post({
      url: '/api/lot/add',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新停车场
   * @param data 停车场数据
   * @returns 更新结果
   */
  updateLot: (data: any) => {
    return request.put({
      url: '/api/lot/update',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除停车场
   * @param id 停车场 ID
   * @returns 删除结果
   */
  deleteLot: (id: number) => {
    return request.del({
      url: `/api/lot/delete/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 生成停车位
   * @param parkingLotId 停车场 ID
   * @param count 车位数量
   * @returns 生成结果
   */
  generateSpaces: (parkingLotId: number, count: number) => {
    return request.get({
      url: '/api/lot/generate-spaces',
      params: { parkingLotId, count },
      showSuccessMessage: true
    })
  }
}

/**
 * 僵尸车管理相关 API
 */
export const zombieApi = {
  /**
   * 获取僵尸车列表
   * @param params 查询参数
   * @returns 僵尸车列表
   */
  fetchZombieList: (params: any) => {
    return request.get({
      url: '/api/zombie/list',
      params
    })
  },

  /**
   * 更新僵尸车信息
   * @param data 僵尸车数据
   * @returns 更新结果
   */
  updateZombie: (data: any) => {
    return request.put({
      url: '/api/zombie/update',
      data,
      showSuccessMessage: true
    })
  }
}

/**
 * 数据看板相关 API
 */
export const dashboardApi = {
  /**
   * 获取顶部统计卡片数据
   * @returns 统计数据
   */
  fetchStats: () => {
    return request.get({
      url: '/api/dashboard/stats'
    })
  },

  /**
   * 获取营收与车流量分析数据
   * @returns 营收与车流量数据
   */
  fetchRevenueTraffic: () => {
    return request.get({
      url: '/api/dashboard/revenue-traffic'
    })
  },

  /**
   * 获取停车场停车热度分析数据
   * @returns 停车场热度数据
   */
  fetchParkingHeat: () => {
    return request.get({
      url: '/api/dashboard/parking-heat'
    })
  },

  /**
   * 获取车场实况监控数据
   * @param lotId 停车场ID
   * @returns 监控数据
   */
  fetchMonitorData: (lotId?: number) => {
    return request.get({
      url: '/api/dashboard/monitor',
      params: lotId ? { lotId } : {}
    })
  },

  /**
   * 获取通行记录
   * @param lotId 停车场ID
   * @param limit 限制数量
   * @returns 通行记录
   */
  fetchPassRecords: (lotId?: number, limit: number = 10) => {
    return request.get({
      url: '/api/dashboard/pass-records',
      params: { ...(lotId ? { lotId } : {}), limit }
    })
  }
}

/**
 * 收费标准相关 API
 */
export const feeStandardApi = {
  /**
   * 获取收费标准列表
   * @param params 查询参数
   * @returns 收费标准列表
   */
  fetchFeeStandardList: (params: any) => {
    return request.get({
      url: '/api/fee-standard/list',
      params
    })
  },

  /**
   * 获取收费标准详情
   * @param id 收费标准 ID
   * @returns 收费标准详情
   */
  fetchFeeStandardDetail: (id: number) => {
    return request.get({
      url: `/api/fee-standard/detail/${id}`
    })
  },

  /**
   * 添加收费标准
   * @param data 收费标准数据
   * @returns 添加结果
   */
  addFeeStandard: (data: any) => {
    return request.post({
      url: '/api/fee-standard/add',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新收费标准
   * @param data 收费标准数据
   * @returns 更新结果
   */
  updateFeeStandard: (data: any) => {
    return request.put({
      url: '/api/fee-standard/update',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除收费标准
   * @param id 收费标准 ID
   * @returns 删除结果
   */
  deleteFeeStandard: (id: number) => {
    return request.del({
      url: `/api/fee-standard/delete/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 增值服务订单相关 API
 */
export const vasOrderApi = {
  /**
   * 获取增值服务订单列表
   * @param params 查询参数
   * @returns 增值服务订单列表
   */
  fetchVasOrderList: (params: any) => {
    return request.get({
      url: '/api/vas-order/list',
      params
    })
  },

  /**
   * 获取增值服务订单详情
   * @param id 订单 ID
   * @returns 订单详情
   */
  fetchVasOrderDetail: (id: number) => {
    return request.get({
      url: `/api/vas-order/detail/${id}`
    })
  },

  /**
   * 添加增值服务订单
   * @param data 订单数据
   * @returns 添加结果
   */
  addVasOrder: (data: any) => {
    return request.post({
      url: '/api/vas-order/add',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新增值服务订单
   * @param data 订单数据
   * @returns 更新结果
   */
  updateVasOrder: (data: any) => {
    return request.put({
      url: '/api/vas-order/update',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除增值服务订单
   * @param id 订单 ID
   * @returns 删除结果
   */
  deleteVasOrder: (id: number) => {
    return request.del({
      url: `/api/vas-order/delete/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 代泊订单相关 API
 */
export const valetOrderApi = {
  /**
   * 获取代泊订单列表
   * @param params 查询参数
   * @returns 代泊订单列表
   */
  fetchValetOrderList: (params: any) => {
    return request.get({
      url: '/api/valet-order/list',
      params
    })
  },

  /**
   * 获取代泊订单详情
   * @param id 订单 ID
   * @returns 订单详情
   */
  fetchValetOrderDetail: (id: number) => {
    return request.get({
      url: `/api/valet-order/${id}`
    })
  },

  /**
   * 添加代泊订单
   * @param data 订单数据
   * @returns 添加结果
   */
  addValetOrder: (data: any) => {
    return request.post({
      url: '/api/valet-order',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新代泊订单
   * @param data 订单数据
   * @returns 更新结果
   */
  updateValetOrder: (data: any) => {
    return request.put({
      url: '/api/valet-order',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除代泊订单
   * @param id 订单 ID
   * @returns 删除结果
   */
  deleteValetOrder: (id: number) => {
    return request.del({
      url: `/api/valet-order/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 洗车订单相关 API
 */
export const carwashOrderApi = {
  /**
   * 获取洗车订单列表
   * @param params 查询参数
   * @returns 洗车订单列表
   */
  fetchCarwashOrderList: (params: any) => {
    return request.get({
      url: '/api/carwash-order/list',
      params
    })
  },

  /**
   * 获取洗车订单详情
   * @param id 订单 ID
   * @returns 订单详情
   */
  fetchCarwashOrderDetail: (id: number) => {
    return request.get({
      url: `/api/carwash-order/${id}`
    })
  },

  /**
   * 添加洗车订单
   * @param data 订单数据
   * @returns 添加结果
   */
  addCarwashOrder: (data: any) => {
    return request.post({
      url: '/api/carwash-order',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新洗车订单
   * @param data 订单数据
   * @returns 更新结果
   */
  updateCarwashOrder: (data: any) => {
    return request.put({
      url: '/api/carwash-order',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除洗车订单
   * @param id 订单 ID
   * @returns 删除结果
   */
  deleteCarwashOrder: (id: number) => {
    return request.del({
      url: `/api/carwash-order/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 投诉管理相关 API
 */
export const feedbackApi = {
  /**
   * 获取投诉列表
   * @param params 查询参数
   * @returns 投诉列表
   */
  fetchFeedbackList: (params: any) => {
    return request.get({
      url: '/api/feedback/list',
      params
    })
  },

  /**
   * 获取投诉详情
   * @param id 投诉 ID
   * @returns 投诉详情
   */
  fetchFeedbackDetail: (id: number) => {
    return request.get({
      url: `/api/feedback/${id}`
    })
  },

  /**
   * 添加投诉
   * @param data 投诉数据
   * @returns 添加结果
   */
  addFeedback: (data: any) => {
    return request.post({
      url: '/api/feedback',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新投诉
   * @param data 投诉数据
   * @returns 更新结果
   */
  updateFeedback: (data: any) => {
    return request.put({
      url: '/api/feedback',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除投诉
   * @param id 投诉 ID
   * @returns 删除结果
   */
  deleteFeedback: (id: number) => {
    return request.del({
      url: `/api/feedback/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 车牌识别相关 API
 */
export const plateApi = {
  /**
   * 上传车牌图片进行识别
   * @param file 图片文件
   * @param parkId 停车场ID
   * @returns 识别结果
   */
  uploadPlate: (file: File, parkId: number) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('id', parkId.toString())
    return request.post({
      url: '/api/distinguish',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      showSuccessMessage: false
    })
  },

  /**
   * 车辆入场
   * @param plateNumber 车牌号
   * @param parkingLotId 停车场ID
   * @returns 入场结果
   */
  vehicleEntry: (plateNumber: string, parkingLotId: number) => {
    return request.post({
      url: '/api/record/entry',
      data: {
        plateNumber,
        parkingLotId
      },
      showSuccessMessage: true
    })
  }
}

/**
 * 预约管理相关 API
 */
export const reservationApi = {
  /**
   * 获取预约列表
   * @param params 查询参数
   * @returns 预约列表
   */
  fetchReservationList: (params: any) => {
    return request.get({
      url: '/api/reservation/list',
      params
    })
  },

  /**
   * 获取预约详情
   * @param id 预约 ID
   * @returns 预约详情
   */
  fetchReservationDetail: (id: number) => {
    return request.get({
      url: `/api/reservation/${id}`
    })
  },

  /**
   * 创建预约
   * @param data 预约数据
   * @returns 创建结果
   */
  create: (data: any) => {
    return request.post({
      url: '/api/reservation',
      data,
      showSuccessMessage: true
    })
  },

  /**
   * 取消预约
   * @param id 预约 ID
   * @returns 取消结果
   */
  cancel: (id: number) => {
    return request.put({
      url: `/api/reservation/cancel/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 确认预约
   * @param id 预约 ID
   * @returns 确认结果
   */
  confirm: (id: number) => {
    return request.put({
      url: `/api/reservation/confirm/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 完成预约
   * @param id 预约 ID
   * @returns 完成结果
   */
  complete: (id: number) => {
    return request.put({
      url: `/api/reservation/complete/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 系统配置相关 API
 */
export const sysConfigApi = {
  /**
   * 获取配置列表
   * @param params 查询参数
   * @returns 配置列表
   */
  fetchConfigList: (params: any) => {
    return request.get({
      url: '/api/sys-config/list',
      params
    })
  },

  /**
   * 获取配置详情
   * @param id 配置 ID
   * @returns 配置详情
   */
  fetchConfigDetail: (id: number) => {
    return request.get({
      url: `/api/sys-config/${id}`
    })
  },

  /**
   * 获取发票配置
   * @returns 发票配置
   */
  fetchInvoiceConfig: () => {
    return request.get({
      url: '/api/sys-config/invoice-config'
    })
  }
}

/**
 * 运营策略相关 API
 */
export const operationStrategyApi = {
  /**
   * 获取运营策略列表
   * @param params 查询参数
   * @returns 运营策略列表
   */
  fetchOperationStrategyList: (params: any) => {
    return request.get({
      url: '/api/operation-strategy/list',
      params
    })
  },

  /**
   * 获取运营策略详情
   * @param id 策略 ID
   * @returns 运营策略详情
   */
  fetchOperationStrategyDetail: (id: number) => {
    return request.get({
      url: `/api/operation-strategy/${id}`
    })
  },

  /**
   * 添加运营策略
   * @param data 策略数据
   * @returns 添加结果
   */
  addOperationStrategy: (data: any) => {
    return request.post({
      url: '/api/operation-strategy',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 更新运营策略
   * @param data 策略数据
   * @returns 更新结果
   */
  updateOperationStrategy: (data: any) => {
    return request.put({
      url: '/api/operation-strategy',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 删除运营策略
   * @param id 策略 ID
   * @returns 删除结果
   */
  deleteOperationStrategy: (id: number) => {
    return request.del({
      url: `/api/operation-strategy/${id}`,
      showSuccessMessage: true
    })
  }
}

/**
 * 充电服务相关 API
 */
export const chargeApi = {
  /**
   * 获取充电站列表
   * @param params 查询参数
   * @returns 充电站列表
   */
  fetchStationList: (params: any) => {
    return request.get({
      url: '/api/charge/station/list',
      params
    })
  },

  /**
   * 获取充电桩列表
   * @param stationId 充电站ID
   * @returns 充电桩列表
   */
  fetchPileList: (stationId: number) => {
    return request.get({
      url: `/api/charge/pile/list`,
      params: { stationId }
    })
  },

  /**
   * 开始充电
   * @param data 充电数据
   * @returns 充电结果
   */
  startCharge: (data: any) => {
    return request.post({
      url: '/api/charge/start',
      data: data,
      showSuccessMessage: true
    })
  },

  /**
   * 结束充电
   * @param id 充电ID
   * @returns 结束结果
   */
  stopCharge: (id: number) => {
    return request.post({
      url: `/api/charge/stop/${id}`,
      showSuccessMessage: true
    })
  },

  /**
   * 获取充电记录
   * @param params 查询参数
   * @returns 充电记录
   */
  fetchChargeRecords: (params: any) => {
    return request.get({
      url: '/api/charge/record/list',
      params
    })
  }
}
