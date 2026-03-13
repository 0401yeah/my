<template>
  <div class="log-page">
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
        <template #action="{ row }">
          <div class="flex">
            <ArtButtonTable type="view" @click="handleView(row)" />
          </div>
        </template>
      </art-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="日志详情" width="500px" destroy-on-close>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ logDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ logDetail.operation }}</el-descriptions-item>
        <el-descriptions-item label="方法">{{ logDetail.method }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ logDetail.ip }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ logDetail.time }}ms</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ logDetail.gmtCreate }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import { useTable } from '@/hooks/core/useTable'
  import { sysLogApi } from '@/api/system'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'

  defineOptions({ name: 'SystemLogPage' })

  const dialogVisible = ref(false)
  const logDetail = ref<any>({})

  const handleView = (row: any) => {
    logDetail.value = row
    dialogVisible.value = true
  }

  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange } = useTable({
    core: {
      apiFn: sysLogApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        username: '',
        operation: ''
      },
      columnsFactory: () => [
        {
          type: 'globalIndex',
          width: 60,
          label: '序号'
        },
        {
          prop: 'username',
          label: '用户名',
          minWidth: 120, // 改用 minWidth，提供基础自适应空间
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          minWidth: 150,
          sortable: true
        },
        {
          prop: 'method',
          label: '方法',
          minWidth: 220,
          showOverflowTooltip: true
        },
        {
          prop: 'ip',
          label: 'IP地址',
          minWidth: 130
        },
        {
          prop: 'time',
          label: '耗时(ms)',
          minWidth: 120, // 增加宽度，避免文字与排序箭头折行
          sortable: true
        },
        {
          prop: 'gmtCreate',
          label: '创建时间',
          minWidth: 180, // 增加宽度
          sortable: true
        },
        {
          prop: 'action',
          label: '操作',
          width: 90,
          fixed: 'right',
          useSlot: true
        }
      ]
    }
  })
</script>

<style scoped>
  .log-page {
    padding: 20px;
  }
  
  /* 强制表头内容不换行，保证文字和排序箭头永远排成一排 */
  :deep(.el-table th.el-table__cell > .cell) {
    white-space: nowrap !important;
    word-break: keep-all !important;
  }
</style>