import request from '@/utils/http'

export const expenseApi = {
  getList: (params: any) => {
    return request.get({
      url: '/api/expense/list',
      params
    })
  },

  getDetail: (id: number) => {
    return request.get({
      url: `/api/expense/${id}`
    })
  },

  add: (data: any) => {
    return request.post({
      url: '/api/expense',
      data,
      showSuccessMessage: true
    })
  },

  update: (data: any) => {
    return request.put({
      url: `/api/expense/${data.id}`,
      data,
      showSuccessMessage: true
    })
  },

  delete: (id: number) => {
    return request.del({
      url: `/api/expense/${id}`,
      showSuccessMessage: true
    })
  },

  approve: (id: number, status: number) => {
    return request.put({
      url: `/api/expense/${id}/approve`,
      data: { status },
      showSuccessMessage: false
    })
  },

  getParkingLots: () => {
    return request.get({
      url: '/api/lot/list',
      params: { size: 1000 }
    })
  }
}
