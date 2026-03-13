<template>
  <div class="zombie-page flex flex-col gap-4 pb-5">
    <ArtSearchBar
      ref="searchBarRef"
      v-model="searchFormState"
      :items="searchItems"
      :is-expand="false"
      :show-expand="true"
      :show-reset-button="true"
      :show-search-button="true"
      @search="handleSearch"
      @reset="handleReset"
    />

    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="handleSendReminder" v-ripple>
              <ElIcon>
                <Message />
              </ElIcon>
              一键发送提醒
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="data"
        :columns="columns"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
        @sort-change="handleSortChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #vehicleInfo="{ row }">
          <div class="flex gap-3">
            <div class="flex-1 min-w-0">
              <p class="m-0 overflow-hidden font-medium text-ellipsis whitespace-nowrap">{{
                row.licensePlate
              }}</p>
              <p
                class="m-0 mt-1 overflow-hidden text-xs text-g-700 text-ellipsis whitespace-nowrap"
                >{{ row.vehicleType }}</p
              >
            </div>
          </div>
        </template>

        <template #stayDuration="{ row }">
          <ElTag :type="getDurationLevel(row.stayDuration).type" effect="light">
            {{ row.stayDuration }} 天
          </ElTag>
        </template>

        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>

      <!-- 编辑对话框 -->
      <ElDialog v-model="editDialogVisible" title="编辑僵尸车信息" width="500px">
        <ElForm :model="editForm" label-width="100px">
          <ElFormItem label="车牌号">
            <ElInput v-model="editForm.licensePlate" />
          </ElFormItem>
          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="停留时长(天)">
                <ElInput v-model.number="editForm.stayDuration" type="number" :min="1" placeholder="请输入停留时长" />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="预警级别">
                <ElSelect v-model="editForm.level" placeholder="请选择预警级别">
                  <ElOption label="轻微" :value="1" />
                  <ElOption label="中等" :value="2" />
                  <ElOption label="严重" :value="3" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
          </ElRow>
          <ElFormItem label="停车场">
            <ElSelect v-model="editForm.parkingLot" placeholder="请选择停车场">
              <ElOption 
                v-for="lot in parkingLots" 
                :key="lot.id" 
                :label="lot.name" 
                :value="lot.id" 
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="最后移动时间">
            <ElInput v-model="editForm.lastMoveTime" placeholder="请输入最后移动时间" />
          </ElFormItem>
          <ElFormItem label="停车位">
            <ElInput v-model="editForm.position" disabled />
          </ElFormItem>
        </ElForm>
        <template #footer>
          <ElButton @click="editDialogVisible = false">取消</ElButton>
          <ElButton type="primary" @click="submitEdit" :loading="editLoading">确定</ElButton>
        </template>
      </ElDialog>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { Message, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import { zombieApi, lotApi } from '@/api/business'

  defineOptions({ name: 'Zombie' })

  const selectedRows = ref<any[]>([])

  const tableRef = ref()
  const searchBarRef = ref()

  // 停车场列表和映射
  const parkingLots = ref<any[]>([])
  const parkingLotMap = ref<Record<string, string>>({})

  const searchFormState = ref({
    licensePlate: '',
    stayDurationMin: '',
    stayDurationMax: '',
    level: '',
    parkingLot: ''
  })

  const DURATION_LEVEL_CONFIG = {
    '1': { type: 'info' as const, text: '轻微' },
    '2': { type: 'warning' as const, text: '中等' },
    '3': { type: 'danger' as const, text: '严重' }
  } as const

  const searchItems = computed(() => [
    {
      key: 'licensePlate',
      label: '车牌号',
      type: 'input',
      props: {
        placeholder: '请输入车牌号'
      }
    },
    {
      key: 'stayDurationMin',
      label: '最少天数',
      type: 'input',
      props: {
        placeholder: '最少停留天数',
        type: 'number'
      }
    },
    {
      key: 'stayDurationMax',
      label: '最大天数',
      type: 'input',
      props: {
        placeholder: '最大停留天数',
        type: 'number'
      }
    },
    {
      key: 'level',
      label: '预警级别',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '轻微', value: '1' },
        { label: '中等', value: '2' },
        { label: '严重', value: '3' }
      ]
    },
    {
      key: 'parkingLot',
      label: '停车场',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '停车场 A', value: 'A' },
        { label: '停车场 B', value: 'B' },
        { label: '停车场 C', value: 'C' }
      ]
    }
  ])

  const getDurationLevel = (duration: number) => {
    if (duration < 7) {
      return DURATION_LEVEL_CONFIG['1']
    } else if (duration < 30) {
      return DURATION_LEVEL_CONFIG['2']
    } else {
      return DURATION_LEVEL_CONFIG['3']
    }
  }

  // 获取停车场列表并创建映射
  const fetchParkingLots = async () => {
    try {
      const response: any = await lotApi.fetchLotList({})
      if (response && response.records) {
        parkingLots.value = response.records
        // 创建停车场ID到名称的映射
        parkingLotMap.value = {}
        response.records.forEach((lot: any) => {
          parkingLotMap.value[lot.id] = lot.name
        })
      }
    } catch (error) {
      console.error('获取停车场列表失败:', error)
    }
  }

  // 获取僵尸车列表
  const fetchZombieList = async (params: any) => {
    try {
      const response: any = await zombieApi.fetchZombieList(params)
      return response
    } catch (error) {
      throw error
    }
  }

  const {
    data,
    loading,
    pagination,
    handleSizeChange,
    handleCurrentChange,
    searchParams,
    resetSearchParams,
    getData,
    refreshData,
    columns,
    columnChecks
  }: any = useTable({
    core: {
      apiFn: fetchZombieList,
      apiParams: {
        current: 1,
        size: 20,
        plate_number: '',
        stayDurationMin: undefined,
        stayDurationMax: undefined
      },
      columnsFactory: () => [
        { type: 'selection', width: 50 },
        { type: 'globalIndex', width: 60, label: '序号' },
        {
          prop: 'vehicleInfo',
          label: '车辆信息',
          minWidth: 120,
          useSlot: true
        },
        {
          prop: 'stayDuration',
          label: '停留时长',
          sortable: true,
          useSlot: true
        },
        {
          prop: 'level',
          label: '预警级别',
          sortable: true,
          formatter: (row: any) => {
            const levelKey = String(row.level)
            const levelConfig =
              DURATION_LEVEL_CONFIG[levelKey as keyof typeof DURATION_LEVEL_CONFIG]
            return levelConfig ? levelConfig.text : '未知'
          }
        },
        {
          prop: 'parkingLot',
          label: '停车场',
          minWidth: 150,
          sortable: true,
          formatter: (row: any) => {
            return parkingLotMap.value[row.parkingLot] || row.parkingLot || '未知'
          }
        },
        {
          prop: 'lastMoveTime',
          label: '最后移动时间',
          minWidth: 160,
          sortable: true,
          formatter: (row: any) => {
            return row.lastMoveTime ? new Date(row.lastMoveTime).toLocaleString() : '未知'
          }
        },
        {
          prop: 'position',
          label: '停车位',
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 150,
          useSlot: true,
          fixed: 'right'
        }
      ]
    },

    hooks: {
      onSuccess: (data, response) => {
        // 数据加载成功
      },
      onError: (error) => {
        ElMessage.error('数据加载失败，请重试')
      }
    }
  })

  const handleSelectionChange = (selection: any[]) => {
    selectedRows.value = selection
  }

  const handleRowClick = (row: any) => {
  }

  const handleSortChange = (sortInfo: any) => {
  }

  const handleSearch = async () => {
    // 转换搜索参数格式
    searchParams.plate_number = searchFormState.value.licensePlate
    searchParams.stayDurationMin = searchFormState.value.stayDurationMin
      ? parseInt(searchFormState.value.stayDurationMin)
      : undefined
    searchParams.stayDurationMax = searchFormState.value.stayDurationMax
      ? parseInt(searchFormState.value.stayDurationMax)
      : undefined
    getData()
  }

  const handleReset = () => {
    resetSearchParams()
    searchFormState.value = {
      licensePlate: '',
      stayDurationMin: '',
      stayDurationMax: '',
      level: '',
      parkingLot: ''
    }
  }

  const handleRefresh = () => {
    refreshData()
  }

  const handleSendReminder = () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要发送提醒的车辆')
      return
    }

    ElMessageBox.confirm(`确定要向选中的 ${selectedRows.value.length} 辆车发送提醒吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        ElMessage.success(`已成功发送 ${selectedRows.value.length} 条提醒`)
      })
      .catch(() => {
        ElMessage.info('已取消发送')
      })
  }

  const handleView = (row: any) => {
    ElMessage.info(`查看车辆: ${row.licensePlate}`)
  }

  // 编辑对话框
  const editDialogVisible = ref(false)
  const editLoading = ref(false)
  const editForm = ref<any>({
    id: '',
    licensePlate: '',
    stayDuration: '',
    level: '',
    parkingLot: '',
    lastMoveTime: '',
    position: ''
  })

  const handleEdit = (row: any) => {
    // 填充表单数据
    editForm.value = {
      id: row.id,
      licensePlate: row.licensePlate,
      stayDuration: row.stayDuration,
      level: row.level,
      parkingLot: row.parkingLot,
      lastMoveTime: row.lastMoveTime,
      position: row.position
    }
    editDialogVisible.value = true
  }

  const submitEdit = async () => {
    editLoading.value = true
    try {
      // 调用后端API更新数据
      await zombieApi.updateZombie(editForm.value)
      ElMessage.success(`编辑车辆: ${editForm.value.licensePlate} 成功`)
      editDialogVisible.value = false
      refreshData() // 刷新数据
    } catch (error) {
      ElMessage.error('编辑失败，请重试')
    } finally {
      editLoading.value = false
    }
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除车辆 ${row.licensePlate} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        ElMessage.success(`删除车辆: ${row.licensePlate} 成功`)
      })
      .catch(() => {
        ElMessage.info('已取消删除')
      })
  }

  // 组件挂载时获取停车场列表
  onMounted(async () => {
    await fetchParkingLots()
  })
</script>

<style scoped>
  .zombie-page {
    padding: 20px;
  }
</style>
