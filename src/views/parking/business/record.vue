<template>
  <div class="record-page art-full-height">
    <RecordSearch
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></RecordSearch>

    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple>新增记录</ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #vehicleInfo="{ row }">
          <div class="flex flex-col items-center justify-center">
            <span class="font-medium">{{ row.plateNumber || '-' }}</span>
          </div>
        </template>

        <template #status="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
            {{ row.status === 0 ? '停车中' : '已出场' }}
          </el-tag>
        </template>

        <template #operation="{ row }">
          <div class="flex items-center justify-center gap-2">
            <ArtButtonTable
              type="edit"
              @click="() => showDialog('edit', row)"
            />
            <ArtButtonTable
              type="delete"
              @click="() => deleteRecord(row)"
            />
          </div>
        </template>
      </ArtTable>

      <RecordDialog
        v-model="dialogVisible"
        :data="currentRecordData"
        :type="dialogType"
        @submit="handleDialogSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ref, nextTick, onMounted } from 'vue'
  import { ElMessage, ElMessageBox, ElTag, ElCard, ElSpace, ElButton } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import RecordSearch from './modules/record-search.vue'
  import RecordDialog from './modules/record-dialog.vue'
  import { DialogType } from '@/types'

  // 导入停车记录API
  import { fetchParkingRecordList } from '@/api/parking-record'

  defineOptions({ name: 'BusinessRecordPage' })

  type RecordListItem = any

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentRecordData = ref<Partial<RecordListItem>>({})

  // 选中行
  const selectedRows = ref<RecordListItem[]>([])

  // 搜索表单
  const searchForm = ref({
    plateNumber: undefined,
    status: undefined
  })

  // 停车记录状态配置
  const RECORD_STATUS_CONFIG = {
    '0': { type: 'success' as const, text: '停车中' },
    '1': { type: 'info' as const, text: '已出场' }
  } as const

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    getData,
    searchParams,
    resetSearchParams,
    handleSizeChange,
    handleCurrentChange,
    refreshData
  } = useTable({
    // 核心配置
    core: {
      apiFn: fetchParkingRecordList,
      apiParams: {
        current: 1,
        size: 20,
        plateNumber: undefined,
        status: undefined
      },
      // 🚨 已经将 columnsFactory 里的 prop 全部修正为驼峰命名
      columnsFactory: () => [
        { type: 'selection', width: 50, align: 'center' },
        { type: 'index', width: 60, label: '序号', align: 'center' },
        { prop: 'vehicleInfo', label: '车辆信息', minWidth: 150, useSlot: true, align: 'center' },
        { prop: 'gmtInto', label: '入场时间', minWidth: 160, sortable: true, align: 'center' },
        {
          prop: 'gmtOut',
          label: '出场时间',
          minWidth: 160,
          sortable: true,
          align: 'center' },
        {
          prop: 'stayMinutes',
          label: '停车时长(分钟)',
          minWidth: 120,
          sortable: true,
          align: 'center'
        },
        { prop: 'payAmount', label: '停车费用(元)', minWidth: 120, sortable: true, align: 'center' },
        { prop: 'parkingLotName',
          label: '停车场',
          minWidth: 150,
          sortable: true },
        { prop: 'spaceNo',
          label: '停车位',
          minWidth: 120,
          sortable: true,
          visible: true },
        { prop: 'status', label: '状态', width: 100, useSlot: true, align: 'center' },
        { label: '操作', width: 150, fixed: 'right', slot: 'operation', useSlot: true, align: 'center' }
      ]
    }
    // 🚨 已经彻底删除了那个伪造假数据的 transform 拦截器！
  })

  /**
   * 搜索处理
   * @param params 参数
   */
  const handleSearch = (params: Record<string, any>) => {
    console.log('搜索参数:', params)
    // 搜索参数赋值，重置回第一页
    Object.assign(searchParams, { ...params, current: 1 })
    getData()
  }

  /**
   * 显示停车记录弹窗
   */
  const showDialog = (type: DialogType, row?: RecordListItem): void => {
    console.log('打开弹窗:', { type, row })
    dialogType.value = type
    currentRecordData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除停车记录
   */
  const deleteRecord = (row: RecordListItem): void => {
    console.log('删除停车记录:', row)
    ElMessageBox.confirm(`确定要删除该停车记录吗？`, '删除停车记录', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }).then(() => {
      ElMessage.success('删除成功')
      refreshData()
    })
  }

  /**
   * 处理弹窗提交事件
   */
  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentRecordData.value = {}
      refreshData()
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: RecordListItem[]): void => {
    selectedRows.value = selection
    console.log('选中行数据:', selectedRows.value)
  }

  // 页面加载时获取数据
  onMounted(() => {
    getData().then(() => {
      console.log('Data received:', data.value)
      console.log('Columns:', columns.value)
      console.log('Column checks:', columnChecks.value)
    })
  })
</script>