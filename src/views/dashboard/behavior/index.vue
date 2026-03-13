<template>
  <div class="behavior-page min-h-screen bg-white text-gray-900">
    <div class="container mx-auto px-4 py-6">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">行为数据分析</h1>
      </div>

      <ElCard class="mb-6 bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm">
        <div class="chart-container relative">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
              <ElIcon class="text-blue-500"><TrendCharts /></ElIcon>
              24小时进出流量分析
            </h2>
            <div class="flex gap-4">
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 bg-blue-500 rounded"></div>
                <span class="text-sm text-gray-600">入场</span>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 bg-orange-500 rounded"></div>
                <span class="text-sm text-gray-600">出场</span>
              </div>
            </div>
          </div>

          <div class="chart-content h-80">
            <ArtBarChart
              height="100%"
              :data="chartData"
              :xAxisData="xAxisData"
              :stack="true"
              :colors="['#3b82f6', '#f97316']"
              :showLegend="false"
              :showAxisLabel="true"
              :showAxisLine="true"
              :showSplitLine="true"
              :barWidth="'60%'"
            />
          </div>

          <div class="absolute bottom-4 right-4">
            <ElButton size="small" circle class="bg-gray-100 hover:bg-gray-200 text-gray-700">
              <ElIcon><Download /></ElIcon>
            </ElButton>
          </div>
        </div>
      </ElCard>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <ElCard class="bg-white border border-gray-200 rounded-xl shadow-sm">
          <div class="card-content">
            <h3 class="text-sm text-gray-600 mb-2">早高峰 (Entry Peak)</h3>
            <div class="text-2xl font-bold text-gray-900 mb-2">--:-- - --:--</div>
            <div class="text-sm text-gray-500">暂无数据</div>
          </div>
        </ElCard>

        <ElCard class="bg-white border border-gray-200 rounded-xl shadow-sm">
          <div class="card-content">
            <h3 class="text-sm text-gray-600 mb-2">晚高峰 (Exit Peak)</h3>
            <div class="text-2xl font-bold text-gray-900 mb-2">--:-- - --:--</div>
            <div class="text-sm text-gray-500">暂无数据</div>
          </div>
        </ElCard>

        <ElCard class="bg-white border border-gray-200 rounded-xl shadow-sm">
          <div class="card-content">
            <h3 class="text-sm text-gray-600 mb-2">平均停留 (Avg Duration)</h3>
            <div class="text-2xl font-bold text-gray-900 mb-2">-- 小时</div>
            <div class="text-sm text-gray-500">暂无数据</div>
          </div>
        </ElCard>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { TrendCharts, Download } from '@element-plus/icons-vue'
  import { ElCard, ElButton, ElIcon } from 'element-plus'
  import ArtBarChart from '@/components/core/charts/art-bar-chart/index.vue'
  import { dashboardApi } from '@/api/business'

  defineOptions({ name: 'Behavior' })

  const xAxisData = Array.from({ length: 24 }, (_, i) => i.toString())

  const entryData = ref<number[]>(Array.from({ length: 24 }, () => 0))
  const exitData = ref<number[]>(Array.from({ length: 24 }, () => 0))

  const chartData = computed(() => [
    {
      name: '入场',
      data: entryData.value
    },
    {
      name: '出场',
      data: exitData.value
    }
  ])

  const fetchData = async () => {
    try {
      const res = await dashboardApi.fetchHourlyTraffic()
      if (res.data) {
        entryData.value = res.data.entryData || Array.from({ length: 24 }, () => 0)
        exitData.value = res.data.exitData || Array.from({ length: 24 }, () => 0)
      }
    } catch (error) {
      // 获取24小时进出流量数据失败
    }
  }

  onMounted(() => {
    fetchData()
  })
</script>

<style scoped>
  .behavior-page {
    min-height: 100vh;
    background-color: #ffffff;
  }

  .chart-container {
    position: relative;
  }

  .card-content {
    padding: 16px;
  }
</style>
