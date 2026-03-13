<template>
  <div class="report-page p-6">
    <ElRow :gutter="20" class="mb-6">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in kpiCards" :key="card.title">
        <ArtStatsCard
          :boxStyle="'h-32'"
          :icon="card.icon"
          :iconStyle="card.iconStyle"
          :title="card.title"
          :count="card.count"
          :description="card.description"
          :showArrow="true"
        />
      </ElCol>
    </ElRow>

    <ElRow :gutter="20" class="mb-6">
      <ElCol :xs="24" :lg="12">
        <ElCard shadow="hover" class="h-full">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">收入构成分析</h3>
            <ElTag size="small" type="info">本月数据</ElTag>
          </div>
          <ArtRingChart
            :height="'320px'"
            :data="incomeCompositionData"
            :showLegend="true"
            :legendPosition="'right'"
          />
        </ElCard>
      </ElCol>

      <ElCol :xs="24" :lg="12">
        <ElCard shadow="hover" class="h-full">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">支出构成分析</h3>
            <ElTag size="small" type="warning">本月数据</ElTag>
          </div>
          <ArtRingChart
            :height="'320px'"
            :data="expenseCompositionData"
            :showLegend="true"
            :legendPosition="'right'"
          />
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="20" class="mb-6">
      <ElCol :span="24">
        <ElCard shadow="hover">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">收支趋势分析</h3>
            <ElRadioGroup v-model="trendPeriod" size="small" @change="handlePeriodChange">
              <ElRadioButton label="week">近7天</ElRadioButton>
              <ElRadioButton label="month">近30天</ElRadioButton>
            </ElRadioGroup>
          </div>
          <ArtLineChart
            :height="'350px'"
            :data="trendLineData"
            :xAxisData="trendData.dates"
            :showAreaColor="true"
            :smooth="true"
            :showLegend="true"
            :legendPosition="'top'"
          />
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="20" class="mb-6">
      <ElCol :xs="24" :lg="12">
        <ElCard shadow="hover">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">收入分类汇总</h3>
            <ElTag size="small" type="success">本月</ElTag>
          </div>
          <ElTable :data="incomeTableData" stripe border size="small">
            <ElTableColumn prop="category" label="收入类别" min-width="120" />
            <ElTableColumn prop="amount" label="金额(元)" min-width="100">
              <template #default="{ row }">
                <span class="text-green-600 font-medium">¥{{ row.amount.toFixed(2) }}</span>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="count" label="笔数" min-width="80" />
            <ElTableColumn prop="percentage" label="占比" min-width="80">
              <template #default="{ row }">
                <ElProgress :percentage="row.percentage" :stroke-width="6" :show-text="false" />
                <span class="text-xs text-gray-500">{{ row.percentage.toFixed(1) }}%</span>
              </template>
            </ElTableColumn>
          </ElTable>
        </ElCard>
      </ElCol>

      <ElCol :xs="24" :lg="12">
        <ElCard shadow="hover">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">支出分类汇总</h3>
            <ElTag size="small" type="danger">本月</ElTag>
          </div>
          <ElTable :data="expenseTableData" stripe border size="small">
            <ElTableColumn prop="category" label="支出类别" min-width="120" />
            <ElTableColumn prop="amount" label="金额(元)" min-width="100">
              <template #default="{ row }">
                <span class="text-red-600 font-medium">¥{{ row.amount.toFixed(2) }}</span>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="count" label="笔数" min-width="80" />
            <ElTableColumn prop="percentage" label="占比" min-width="80">
              <template #default="{ row }">
                <ElProgress :percentage="row.percentage" :stroke-width="6" :show-text="false" color="#f56c6c" />
                <span class="text-xs text-gray-500">{{ row.percentage.toFixed(1) }}%</span>
              </template>
            </ElTableColumn>
          </ElTable>
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="20">
      <ElCol :span="24">
        <ElCard shadow="hover">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">各停车场收入对比</h3>
            <ElTag size="small" type="info">本月数据</ElTag>
          </div>
          <ArtHBarChart
            :height="'300px'"
            :data="parkingLotRevenue.data"
            :xAxisData="parkingLotRevenue.labels"
            :barWidth="'60%'"
            :showLegend="false"
          />
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import ArtStatsCard from '@/components/core/cards/art-stats-card/index.vue'
  import ArtLineChart from '@/components/core/charts/art-line-chart/index.vue'
  import ArtRingChart from '@/components/core/charts/art-ring-chart/index.vue'
  import ArtHBarChart from '@/components/core/charts/art-h-bar-chart/index.vue'
  import { financeApi } from '@/api/finance'
  import { ElMessage } from 'element-plus'

  defineOptions({ name: 'Report' })

  const trendPeriod = ref('week')

  const kpiCards = ref([
    {
      title: '总收入',
      count: 0,
      icon: 'ri:money-cny-circle-line',
      iconStyle: 'bg-green-500',
      description: ''
    },
    {
      title: '总支出',
      count: 0,
      icon: 'ri:wallet-3-line',
      iconStyle: 'bg-red-500',
      description: ''
    },
    {
      title: '净利润',
      count: 0,
      icon: 'ri:line-chart-line',
      iconStyle: 'bg-blue-500',
      description: ''
    },
    {
      title: '环比增长',
      count: 0,
      icon: 'ri:arrow-up-circle-line',
      iconStyle: 'bg-purple-500',
      description: ''
    }
  ])

  const incomeCompositionData = ref<{ value: number; name: string }[]>([])
  const expenseCompositionData = ref<{ value: number; name: string }[]>([])

  const trendData = ref<{
    dates: string[];
    income: number[];
    expense: number[];
  }>({
    dates: [],
    income: [],
    expense: []
  })

  const trendLineData = computed(() => [
    {
      name: '收入',
      data: trendData.value.income,
      showAreaColor: true
    },
    {
      name: '支出',
      data: trendData.value.expense,
      showAreaColor: true
    }
  ])

  const incomeTableData = ref([
    { category: '临时停车费', amount: 0, count: 0, percentage: 0 },
    { category: '月租费', amount: 0, count: 0, percentage: 0 },
    { category: '代泊服务', amount: 0, count: 0, percentage: 0 },
    { category: '洗车服务', amount: 0, count: 0, percentage: 0 },
    { category: '其他收入', amount: 0, count: 0, percentage: 0 }
  ])

  const expenseTableData = ref([
    { category: '设备维护费', amount: 0, count: 0, percentage: 0 },
    { category: '电费', amount: 0, count: 0, percentage: 0 },
    { category: '人工成本', amount: 0, count: 0, percentage: 0 },
    { category: '退款金额', amount: 0, count: 0, percentage: 0 },
    { category: '其他支出', amount: 0, count: 0, percentage: 0 }
  ])

  const parkingLotRevenue = ref<{
    labels: string[];
    data: number[];
  }>({
    labels: [],
    data: []
  })

  const handlePeriodChange = () => {
    fetchTrendData()
  }

  const fetchKPIData = async () => {
    try {
      const data = await financeApi.fetchFinanceKPI()
      kpiCards.value[0].count = data.totalIncome || 0
      kpiCards.value[1].count = data.totalExpense || 0
      kpiCards.value[2].count = data.netProfit || 0
      kpiCards.value[3].count = data.growthRate || 0
      kpiCards.value[3].description = data.growthRate >= 0 ? '较上月增长' : '较上月下降'
    } catch (error) {
      // 获取KPI数据失败
    }
  }

  const fetchCompositionData = async () => {
    try {
      const data = await financeApi.fetchFinanceComposition()
      if (data.income?.labels && data.income?.data) {
        incomeCompositionData.value = data.income.labels.map((label: string, index: number) => ({
          name: label,
          value: data.income.data[index] || 0
        }))
      }
      if (data.expense?.labels && data.expense?.data) {
        expenseCompositionData.value = data.expense.labels.map((label: string, index: number) => ({
          name: label,
          value: data.expense.data[index] || 0
        }))
      }
    } catch (error) {
      // 获取构成数据失败
    }
  }

  const fetchTrendData = async () => {
    try {
      const data = await financeApi.fetchFinanceTrend({ period: trendPeriod.value })
      trendData.value = {
        dates: data.dates || [],
        income: data.income || [],
        expense: data.expense || []
      }
    } catch (error) {
      // 获取趋势数据失败
    }
  }

  const fetchTableData = async () => {
    try {
      const data = await financeApi.fetchFinanceTable()
      if (data.income) {
        const totalIncome = data.income.reduce((sum: number, item: any) => sum + item.amount, 0)
        incomeTableData.value = data.income.map((item: any) => ({
          ...item,
          percentage: totalIncome > 0 ? (item.amount / totalIncome) * 100 : 0
        }))
      }
      if (data.expense) {
        const totalExpense = data.expense.reduce((sum: number, item: any) => sum + item.amount, 0)
        expenseTableData.value = data.expense.map((item: any) => ({
          ...item,
          percentage: totalExpense > 0 ? (item.amount / totalExpense) * 100 : 0
        }))
      }
    } catch (error) {
      // 获取表格数据失败
    }
  }

  const fetchParkingLotRevenue = async () => {
    try {
      const data = await financeApi.fetchParkingLotRevenue()
      parkingLotRevenue.value = {
        labels: data.labels || [],
        data: data.data || []
      }
    } catch (error) {
      // 获取停车场收入数据失败
    }
  }

  const initData = async () => {
    try {
      await Promise.all([
        fetchKPIData(),
        fetchCompositionData(),
        fetchTrendData(),
        fetchTableData(),
        fetchParkingLotRevenue()
      ])
    } catch (error) {
      ElMessage.error('获取财务报表数据失败')
    }
  }

  onMounted(() => {
    initData()
  })
</script>

<style scoped>
  .report-page {
    min-height: 100%;
    background: #f5f7fa;
  }

  .el-card {
    border-radius: 8px;
    border: 1px solid #e5e7eb;
  }

  .el-card:hover {
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }
</style>
