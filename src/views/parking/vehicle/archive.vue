<!-- 车辆档案页面 -->
<!-- art-full-height 自动计算出页面剩余高度 -->
<!-- art-table-card 一个符合系统样式的 class，同时自动撑满剩余高度 -->
<!-- 更多 useTable 使用示例请移步至 功能示例 下面的高级表格示例或者查看官方文档 -->
<!-- useTable 文档：https://www.artd.pro/docs/zh/guide/hooks/use-table.html -->
<template>
  <div class="vehicle-page art-full-height">
    <!-- 搜索栏 -->
    <VehicleSearch
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></VehicleSearch>

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple>新增车辆</ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 表格 -->
      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @selection-change="handleSelectionChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
      </ArtTable>

      <!-- 车辆弹窗 -->
      <VehicleDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :vehicle-data="currentVehicleData"
        @submit="handleDialogSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
import ArtTable from '@/components/core/tables/art-table/index.vue'
import { useTable } from '@/hooks/core/useTable'
import VehicleSearch from './modules/vehicle-search.vue'
import VehicleDialog from './modules/vehicle-dialog.vue'
import { ElTag } from 'element-plus'
import { DialogType } from '@/types'

  defineOptions({ name: 'VehicleArchivePage' })

  type VehicleListItem = any

  // 车辆类型配置
  const VEHICLE_TYPE_CONFIG = {
    '0': { type: 'info' as const, text: '小型车' },
    '1': { type: 'success' as const, text: '大型车' },
    '2': { type: 'warning' as const, text: '新能源' },
    '3': { type: 'danger' as const, text: '特种车' }
  }

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentVehicleData = ref<Partial<VehicleListItem>>({})

  // 选中行
  const selectedRows = ref<VehicleListItem[]>([])

  // 搜索表单
  const searchForm = ref({
    plateNumber: undefined,
    ownerName: undefined,
    status: '1'
  })

  // 车辆状态配置
  const VEHICLE_STATUS_CONFIG = {
    '1': { type: 'success' as const, text: '正常' },
    '0': { type: 'danger' as const, text: '禁用' }
  } as const

  // 车辆类型配置
  



  /**
   * 获取车辆类型配置
   */
  const getVehicleTypeConfig = (type: string | number | undefined) => {
    const typeStr = String(type || '0')
    return (
      VEHICLE_TYPE_CONFIG[typeStr as keyof typeof VEHICLE_TYPE_CONFIG] || {
        type: 'info' as const,
        text: '未知'
      }
    )
  }

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
      apiFn: async (params: any) => {
        // 这里应该调用真实的车辆档案API
        // 暂时返回空数组，确保没有模拟数据
        return { records: [], total: 0 }
      },
      apiParams: {
        current: 1,
        size: 20,
        plateNumber: undefined,
        ownerName: undefined
      },
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        {
          prop: 'vehicleInfo',
          label: '车辆信息',
          width: 280,
          formatter: (row) => {
            return h('div', { class: 'vehicle flex-c' }, [
              h('div', { class: 'ml-2' }, [
                h(
                  'p',
                  { class: 'plate-number' },
                  row.plateNumber || '未知车辆'
                ),
                h('p', { class: 'owner' }, row.ownerName || '未知车主')
              ])
            ])
          }
        },
        {
          prop: 'type',
          label: '车辆类型',
          formatter: (row) => {
            const typeConfig = getVehicleTypeConfig(row.type)
            return h(ElTag, { type: typeConfig.type }, () => typeConfig.text)
          }
        },

        { prop: 'mobile', label: '联系电话' },
        {
          prop: 'gmt_create',
          label: '创建日期',
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 150,
          fixed: 'right',
          formatter: (row) =>
            h('div', [
              h(ArtButtonTable, {
                type: 'view',
                onClick: () => showDialog('view', row)
              }),
              h(ArtButtonTable, {
                type: 'edit',
                onClick: () => showDialog('edit', row)
              }),
              h(ArtButtonTable, {
                type: 'delete',
                onClick: () => deleteVehicle(row)
              })
            ])
        }
      ]
    }
  })

  /**
   * 搜索处理
   * @param params 参数
   */
  const handleSearch = (params: Record<string, any>) => {
    console.log('搜索参数:', params)
    // 搜索参数赋值
    Object.assign(searchParams, params)
    getData()
  }

  /**
   * 显示车辆弹窗
   */
  const showDialog = (type: DialogType, row?: VehicleListItem): void => {
    console.log('打开弹窗:', { type, row })
    dialogType.value = type
    currentVehicleData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除车辆
   */
  const deleteVehicle = (row: VehicleListItem): void => {
    console.log('删除车辆:', row)
    ElMessageBox.confirm(`确定要删除该车辆吗？`, '删除车辆', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }).then(() => {
      ElMessage.success('删除成功')
    })
  }

  /**
   * 处理弹窗提交事件
   */
  const handleDialogSubmit = async () => {
    try {
      dialogVisible.value = false
      currentVehicleData.value = {}
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: VehicleListItem[]): void => {
    selectedRows.value = selection
    console.log('选中行数据:', selectedRows.value)
  }

  // 页面加载时获取数据
  onMounted(() => {
    getData()
  })
</script>
