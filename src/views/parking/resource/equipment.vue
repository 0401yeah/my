<!-- 设备管理页面 -->
<!-- art-full-height 自动计算出页面剩余高度 -->
<!-- art-table-card 一个符合系统样式的 class，同时自动撑满剩余高度 -->
<!-- 更多 useTable 使用示例请移步至 功能示例 下面的高级表格示例或者查看官方文档 -->
<!-- useTable 文档：https://www.artd.pro/docs/zh/guide/hooks/use-table.html -->
<template>
  <div class="equipment-page art-full-height">
    <!-- 搜索栏 -->
    <EquipmentSearch
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></EquipmentSearch>

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple>新增设备</ElButton>
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

      <!-- 设备弹窗 -->
      <EquipmentDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :equipment-data="currentEquipmentData"
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
import EquipmentSearch from './modules/equipment-search.vue'
import EquipmentDialog from './modules/equipment-dialog.vue'
import { ElTag } from 'element-plus'
import { DialogType } from '@/types'

  defineOptions({ name: 'ResourceEquipmentPage' })

  interface EquipmentListItem {
    id: number
    name: string
    code: string
    type: string
    status: number
    gmt_create: string
  }

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentEquipmentData = ref<Partial<EquipmentListItem>>({})

  // 选中行
  const selectedRows = ref<EquipmentListItem[]>([])

  // 搜索表单
  const searchForm = ref({
    name: undefined,
    code: undefined,
    status: '1'
  })

  // 设备状态配置
  const EQUIPMENT_STATUS_CONFIG = {
    '1': { type: 'success' as const, text: '在线' },
    '0': { type: 'danger' as const, text: '离线' }
  } as const

  /**
   * 获取设备状态配置
   */
  const getEquipmentStatusConfig = (status: string | number) => {
    const statusStr = String(status)
    return (
      EQUIPMENT_STATUS_CONFIG[statusStr as keyof typeof EQUIPMENT_STATUS_CONFIG] || {
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
        // 这里应该调用真实的设备API
        // 暂时返回空数组，确保没有模拟数据
        return { records: [], total: 0 }
      },
      apiParams: {
        current: 1,
        size: 20,
        name: undefined,
        code: undefined,
        status: undefined
      },
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        { 
          prop: 'equipmentInfo',
          label: '设备信息',
          width: 280,
          formatter: (row: EquipmentListItem) => {
            return h('div', { class: 'equipment flex-c' }, [
              h('div', { class: 'ml-2' }, [
                h('p', { class: 'equipment-name' }, row.name || row.code),
                h('p', { class: 'code' }, row.code)
              ])
            ])
          }
        },
        {
          prop: 'type',
          label: '设备类型',
          sortable: true
        },
        {
          prop: 'status',
          label: '状态',
          width: 180,
          formatter: (row: EquipmentListItem) => {
            const statusConfig = getEquipmentStatusConfig(row.status)
            return h('div', { class: 'status-container' }, [
              h(ElTag, { type: statusConfig.type }, () => statusConfig.text),
              h('el-button', {
                size: 'small',
                type: row.status === 1 ? 'danger' : 'primary',
                style: 'margin-left: 8px; padding: 0 10px; height: 22px; font-size: 12px;',
                onClick: () => handleStatusChange(row)
              }, () => row.status === 1 ? '离线' : '在线')
            ])
          }
        },
        {
          prop: 'gmt_create',
          label: '创建日期',
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 100,
          fixed: 'right',
          formatter: (row: EquipmentListItem) =>
            h('div', [
              h(ArtButtonTable, {
                type: 'edit',
                onClick: () => showDialog('edit', row)
              }),
              h(ArtButtonTable, {
                type: 'delete',
                onClick: () => deleteEquipment(row)
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
    // 搜索参数赋值
    Object.assign(searchParams, params)
    getData()
  }

  /**
   * 显示设备弹窗
   */
  const showDialog = (type: DialogType, row?: EquipmentListItem): void => {
    dialogType.value = type
    currentEquipmentData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除设备
   */
  const deleteEquipment = (row: EquipmentListItem): void => {
    ElMessageBox.confirm(`确定要删除该设备吗？`, '删除设备', {
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
      currentEquipmentData.value = {}
    } catch (error) {
      // 提交失败
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: EquipmentListItem[]): void => {
    selectedRows.value = selection
  }

  /**
   * 处理设备状态切换
   */
  const handleStatusChange = (row: EquipmentListItem): void => {
    const newStatus = row.status === 1 ? 0 : 1
    const statusText = newStatus === 1 ? '在线' : '离线'
    
    ElMessageBox.confirm(`确定要将设备设置为${statusText}吗？`, '状态变更', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 模拟状态更新
      row.status = newStatus
      ElMessage.success(`设备已${statusText}`)
    })
  }

  // 页面加载时获取数据
  onMounted(() => {
    getData()
  })
</script>

<style scoped>
/* 状态容器样式 */
.status-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 确保状态列内容左对齐 */
:deep(.el-table__header th:nth-child(5) .cell),
:deep(.el-table__row td:nth-child(5) .cell) {
  text-align: left !important;
  padding-left: 20px !important;
}

/* 确保操作列内容左对齐 */
:deep(.el-table__header th:last-child .cell),
:deep(.el-table__row td:last-child .cell) {
  text-align: left !important;
  padding-left: 20px !important;
}
</style>
