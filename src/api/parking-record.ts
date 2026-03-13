import api from '@/utils/http'

/**
 * 获取停车记录列表
 * @param params 查询参数
 * @returns 停车记录列表
 */
export function fetchParkingRecordList(params: {
  current?: number
  size?: number
  plateNumber?: string
  status?: number
}) {
  return api.get({
    url: '/api/record/list',
    params
  })
}

/**
 * 获取停车记录详情
 * @param id 停车记录ID
 * @returns 停车记录详情
 */
export function fetchParkingRecordDetail(id: number) {
  return api.get({
    url: `/api/record/detail/${id}`
  })
}

/**
 * 删除停车记录
 * @param id 停车记录ID
 * @returns 删除结果
 */
export function deleteParkingRecord(id: number) {
  return api.get({
    url: `/api/record/delete/${id}`
  })
}
