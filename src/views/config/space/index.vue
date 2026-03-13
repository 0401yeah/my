<template>
  <div class="space-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <art-table
        row-key="id"
        :show-table-header="false"
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <el-tag
            :type="row.status === 0 ? 'success' : row.status === 1 ? 'warning' : 'danger'"
            size="small"
          >
            {{ row.status === 0 ? '空闲' : row.status === 1 ? '占用' : '禁用' }}
          </el-tag>
        </template>
      </art-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import { parkingSpaceApi } from '@/api/system'

  defineOptions({ name: 'Space' })

  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange } = useTable({
    core: {
      apiFn: parkingSpaceApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        spaceNumber: '',
        status: ''
      },
      columnsFactory: () => [
        {
          type: 'globalIndex',
          width: 60,
          label: '序号'
        },
        {
          prop: 'parkingLotName',
          label: '停车场名称',
          sortable: true
        },
        {
          prop: 'type',
          label: '车位类型',
          sortable: true,
          formatter: (row: any) =>
            row.type === 1 ? '普通' : row.type === 2 ? '充电桩位' : 'VIP代泊位'
        },
        {
          prop: 'status',
          label: '状态',
          sortable: true,
          useSlot: true
        },

        {
          prop: 'gmtCreate',
          label: '创建时间',
          sortable: true
        }
      ]
    }
  })
</script>

<style scoped>
  .space-page {
    padding: 20px;
  }
</style>