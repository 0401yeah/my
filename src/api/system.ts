import request from '@/utils/http'

export const announcementApi = {
  fetchList: (params: any) => request.get({ url: '/api/announcement/list', params }),
  getById: (id: number) => request.get({ url: `/api/announcement/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/announcement', data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/announcement', data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/announcement/${id}`, showSuccessMessage: true })
}

export const equipmentApi = {
  fetchList: (params: any) => request.get({ url: '/api/equipment/list', params }),
  getById: (id: number) => request.get({ url: `/api/equipment/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/equipment', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/equipment', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/equipment/${id}`, showSuccessMessage: true })
}

export const equipmentMaintainApi = {
  fetchList: (params: any) => request.get({ url: '/api/equipment-maintain/list', params }),
  getById: (id: number) => request.get({ url: `/api/equipment-maintain/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/equipment-maintain', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/equipment-maintain', params: data, showSuccessMessage: true }),
  delete: (id: number) =>
    request.del({ url: `/api/equipment-maintain/${id}`, showSuccessMessage: true })
}

export const feedbackApi = {
  fetchList: (params: any) => request.get({ url: '/api/feedback/list', params }),
  getById: (id: number) => request.get({ url: `/api/feedback/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/feedback', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/feedback', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/feedback/${id}`, showSuccessMessage: true })
}

export const invoiceApi = {
  fetchList: (params: any) => request.get({ url: '/api/invoice/list', params }),
  getById: (id: number) => request.get({ url: `/api/invoice/${id}` }),
  add: (data: any) => request.post({ url: '/api/invoice', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/invoice', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/invoice/${id}`, showSuccessMessage: true }),
  approve: (id: number) =>
    request.post({ url: `/api/invoice/approve/${id}`, showSuccessMessage: true }),
  download: (id: number) =>
    request.get({ url: `/api/invoice/download/${id}`, responseType: 'blob' })
}

export const feeRuleApi = {
  fetchList: (params: any) => request.get({ url: '/api/fee-rule/list', params }),
  getById: (id: number) => request.get({ url: `/api/fee-rule/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/fee-rule', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/fee-rule', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/fee-rule/${id}`, showSuccessMessage: true })
}

export const parkingSpaceApi = {
  fetchList: (params: any) => request.get({ url: '/api/parking-space/list', params }),
  getById: (id: number) => request.get({ url: `/api/parking-space/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/parking-space', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/parking-space', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/parking-space/${id}`, showSuccessMessage: true })
}

export const operationStrategyApi = {
  fetchList: (params: any) => request.get({ url: '/api/operation-strategy/list', params }),
  getById: (id: number) => request.get({ url: `/api/operation-strategy/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/operation-strategy', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/operation-strategy', params: data, showSuccessMessage: true }),
  delete: (id: number) =>
    request.del({ url: `/api/operation-strategy/${id}`, showSuccessMessage: true })
}

export const sysConfigApi = {
  fetchList: (params: any) => request.get({ url: '/api/sys-config/list', params }),
  getById: (id: number) => request.get({ url: `/api/sys-config/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/sys-config', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/sys-config', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/sys-config/${id}`, showSuccessMessage: true })
}

export const sysInterfaceApi = {
  fetchList: (params: any) => request.get({ url: '/api/sys-interface/list', params }),
  getById: (id: number) => request.get({ url: `/api/sys-interface/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/sys-interface', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/sys-interface', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/sys-interface/${id}`, showSuccessMessage: true })
}

export const sysLogApi = {
  fetchList: (params: any) => request.get({ url: '/api/sys-log/list', params }),
  getById: (id: number) => request.get({ url: `/api/sys-log/${id}` }),
  delete: (id: number) => request.del({ url: `/api/sys-log/${id}`, showSuccessMessage: true })
}

export const sysUserApi = {
  fetchList: (params: any) => request.get({ url: '/api/sys-user/list', params }),
  getById: (id: number) => request.get({ url: `/api/sys-user/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/sys-user', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/sys-user', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/sys-user/${id}`, showSuccessMessage: true })
}

export const sysOrgApi = {
  fetchList: (params: any) => request.get({ url: '/api/sys-org/list', params }),
  getById: (id: number) => request.get({ url: `/api/sys-org/${id}` }),
  add: (data: any) =>
    request.post({ url: '/api/sys-org', params: data, showSuccessMessage: true }),
  update: (data: any) =>
    request.put({ url: '/api/sys-org', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/sys-org/${id}`, showSuccessMessage: true })
}

// 由于已删除 app_customer 表，使用 sys_user 表代替
export const customerApi = {
  fetchList: (params: any) => request.get({ url: '/api/sys-user/list', params }),
  getById: (id: number) => request.get({ url: `/api/sys-user/${id}` }),
  update: (data: any) =>
    request.put({ url: '/api/sys-user', params: data, showSuccessMessage: true }),
  delete: (id: number) => request.del({ url: `/api/sys-user/${id}`, showSuccessMessage: true }),
  updateStatus: (id: number, status: number) =>
    request.put({ url: `/api/sys-user/status/${id}`, params: { status }, showSuccessMessage: true })
}
