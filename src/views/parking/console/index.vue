<template>
  <div class="p-6">
    <!-- 顶部统计卡片 -->
    <ElRow :gutter="20" class="mb-6">
      <ElCol :xs="24" :sm="12" :md="6" v-for="card in statsCards" :key="card.title">
        <ArtStatsCard
          :boxStyle="'h-32'"
          :icon="card.icon"
          :iconStyle="card.iconStyle"
          :title="card.title"
          :count="card.count"
          :description="''"
          :showArrow="true"
        />
      </ElCol>
    </ElRow>

    <!-- 图表区域 -->
    <ElRow :gutter="20">
      <!-- 多组数据折线图 -->
      <ElCol :xs="24" :lg="16">
        <ElCard shadow="hover" class="mb-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">营收与车流量分析</h3>
            <ElTag size="small" type="info">近7天数据</ElTag>
          </div>
          <ArtLineChart
            :height="'400px'"
            :data="multiLineData"
            :xAxisData="revenueData.dates"
            :showAreaColor="true"
            :smooth="true"
            :showLegend="true"
            :legendPosition="'top'"
          />
        </ElCard>
      </ElCol>

      <!-- 右侧区域 -->
      <ElCol :xs="24" :lg="8">
        <!-- 总用户 -->
        <div class="mb-6">
          <ArtLineChartCard
            :value="userData.total"
            :label="userData.label"
            :percentage="userData.percentage"
            :chartData="userData.chartData"
            :showAreaColor="true"
          />
        </div>

        <!-- 停车场停车热度分析 -->
        <ElCard shadow="hover" class="mb-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-sm font-semibold">停车场停车热度分析</h3>
            <ElTag size="small" type="info" effect="plain">对比</ElTag>
          </div>
          <ArtHBarChart
            :height="'200px'"
            :data="parkingHeatData.heatData"
            :xAxisData="parkingHeatData.parkingLots"
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
  import * as echarts from 'echarts'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import ArtStatsCard from '@/components/core/cards/art-stats-card/index.vue'
  import ArtProgressCard from '@/components/core/cards/art-progress-card/index.vue'
  import ArtDonutChartCard from '@/components/core/cards/art-donut-chart-card/index.vue'
  import ArtLineChartCard from '@/components/core/cards/art-line-chart-card/index.vue'
  import ArtBarChart from '@/components/core/charts/art-bar-chart/index.vue'
  import ArtHBarChart from '@/components/core/charts/art-h-bar-chart/index.vue'
  import ArtLineChart from '@/components/core/charts/art-line-chart/index.vue'
  import ArtRingChart from '@/components/core/charts/art-ring-chart/index.vue'
  import { dashboardApi } from '@/api/business'
  import { ElMessage } from 'element-plus'

  defineOptions({ name: 'DashboardBoardPage' })

  // 总用户数据 (保持不变，用户要求不去掉)
  const userData = {
    total: 2545,
    percentage: 1.2,
    label: '总用户',
    chartData: [1800, 1950, 2100, 2050, 2200, 2350, 2450, 2545]
  }

  // 统计卡片数据
  const statsCards = ref([
    {
      title: '合作单位数量',
      count: 0,
      icon: 'ri:building-2-line',
      iconStyle: 'bg-blue-500'
    },
    {
      title: '停车场数量',
      count: 0,
      icon: 'ri:parking-box-line',
      iconStyle: 'bg-purple-500'
    },
    {
      title: '车辆数量',
      count: 0,
      icon: 'ri:car-line',
      iconStyle: 'bg-green-500'
    },
    {
      title: '收益总额',
      count: 0,
      icon: 'ri:wallet-2-line',
      iconStyle: 'bg-orange-500',
      prefix: '¥'
    }
  ])

  // 营收与车流量数据
  const revenueData = ref({
    dates: [],
    revenue: [],
    traffic: []
  })

  // 多组数据折线图数据
  const multiLineData = computed(() => [
    {
      name: '营收数据',
      data: revenueData.value.revenue,
      showAreaColor: true
    },
    {
      name: '车流量',
      data: revenueData.value.traffic,
      showAreaColor: true
    }
  ])

  // 停车场停车热度数据
  const parkingHeatData = ref({
    parkingLots: [],
    heatData: []
  })

  // 加载状态
  const loading = ref(false)

  // 获取顶部统计卡片数据
  const fetchStats = async () => {
    try {
      const data: any = await dashboardApi.fetchStats()

      // 更新统计卡片数据
      statsCards.value[0].count = data.orgCount || 0
      statsCards.value[1].count = data.lotCount || 0
      statsCards.value[2].count = data.vehicleCount || 0
      statsCards.value[3].count = data.totalRevenue || 0
    } catch (error) {
      ElMessage.error('获取统计数据失败')
    }
  }

  // 获取营收与车流量分析数据
  const fetchRevenueTraffic = async () => {
    try {
      const data: any = await dashboardApi.fetchRevenueTraffic()

      // 更新营收与车流量数据
      revenueData.value.dates = data.dates || []
      revenueData.value.revenue = data.revenue || []
      revenueData.value.traffic = data.traffic || []
    } catch (error) {
      ElMessage.error('获取营收与车流量数据失败')
    }
  }

  // 获取停车场停车热度分析数据
  const fetchParkingHeat = async () => {
    try {
      const data: any = await dashboardApi.fetchParkingHeat()

      // 更新停车场热度数据
      if (data) {
        parkingHeatData.value.parkingLots = data.parkingLots || []
        parkingHeatData.value.heatData = data.heatData || []
      }
    } catch (error) {
      ElMessage.error('获取停车场热度数据失败')
      // 出错时清空数据，避免显示旧的模拟数据
      parkingHeatData.value.parkingLots = []
      parkingHeatData.value.heatData = []
    }
  }

  // 初始化数据
  const initData = async () => {
    loading.value = true
    try {
      await Promise.all([fetchStats(), fetchRevenueTraffic(), fetchParkingHeat()])
    } finally {
      loading.value = false
    }
  }

  // 组件挂载时获取数据
  onMounted(() => {
    initData()
  })
</script>

<style scoped>
  .el-card {
    border-radius: 8px;
    border: 1px solid #e5e7eb;
  }

  .el-card:hover {
    box-shadow:
      0 4px 6px -1px rgba(0, 0, 0, 0.1),
      0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }
</style>
