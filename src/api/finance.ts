import request from '@/utils/http'

interface FinanceKPI {
  totalIncome: number
  totalExpense: number
  netProfit: number
  growthRate: number
}

interface FinanceComposition {
  income: {
    labels: string[]
    data: number[]
  }
  expense: {
    labels: string[]
    data: number[]
  }
}

interface FinanceTrend {
  dates: string[]
  income: number[]
  expense: number[]
}

interface FinanceTable {
  income: Array<{ category: string; amount: number; count: number }>
  expense: Array<{ category: string; amount: number; count: number }>
}

interface ParkingLotRevenue {
  labels: string[]
  data: number[]
}

export const financeApi = {
  fetchFinanceKPI: (): Promise<FinanceKPI> => {
    return request.get({
      url: '/api/finance/kpi'
    })
  },

  fetchFinanceComposition: (): Promise<FinanceComposition> => {
    return request.get({
      url: '/api/finance/composition'
    })
  },

  fetchFinanceTrend: (params: any): Promise<FinanceTrend> => {
    return request.get({
      url: '/api/finance/trend',
      params
    })
  },

  fetchFinanceTable: (): Promise<FinanceTable> => {
    return request.get({
      url: '/api/finance/table'
    })
  },

  fetchParkingLotRevenue: (): Promise<ParkingLotRevenue> => {
    return request.get({
      url: '/api/finance/parking-lot-revenue'
    })
  },

  fetchReportList: (params: any) => {
    return request.get({
      url: '/api/report/list',
      params
    })
  },

  getReportDetail: (id: number) => {
    return request.get({
      url: `/api/report/${id}`
    })
  },

  generateReport: (data: any) => {
    return request.post({
      url: '/api/report/generate',
      data,
      showSuccessMessage: true
    })
  },

  deleteReport: (id: number) => {
    return request.del({
      url: `/api/report/${id}`,
      showSuccessMessage: true
    })
  }
}
