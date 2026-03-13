<!-- 黑白名单页面 -->
<!-- art-full-height 自动计算出页面剩余高度 -->
<!-- art-table-card 一个符合系统样式的 class，同时自动撑满剩余高度 -->
<!-- 更多 useTable 使用示例请移步至 功能示例 下面的高级表格示例或者查看官方文档 -->
<!-- useTable 文档：https://www.artd.pro/docs/zh/guide/hooks/use-table.html -->
<template>
  <div class="blacklist-page art-full-height">
    <!-- 搜索栏 -->
    <BlacklistSearch
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></BlacklistSearch>

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

      <!-- 黑白名单弹窗 -->
      <BlacklistDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :blacklist-data="currentBlacklistData"
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
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
  import { ACCOUNT_TABLE_DATA } from '@/mock/temp/formData'
  import { useTable } from '@/hooks/core/useTable'
  import { fetchGetUserList } from '@/api/system-manage'
  import BlacklistSearch from './modules/blacklist-search.vue'
  import BlacklistDialog from './modules/blacklist-dialog.vue'
  import { ElTag, ElImage } from 'element-plus'
  import { DialogType } from '@/types'

  defineOptions({ name: 'VehicleBlacklistPage' })

  type BlacklistListItem = any

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentBlacklistData = ref<Partial<BlacklistListItem>>({})

  // 选中行
  const selectedRows = ref<BlacklistListItem[]>([])

  // 搜索表单
  const searchForm = ref({
    plateNumber: undefined,
    tagType: undefined
  })

  // 标签类型配置
  const TAG_TYPE_CONFIG = {
    '0': { type: 'info' as const, text: '普通' },
    '1': { type: 'success' as const, text: '白名单' },
    '2': { type: 'danger' as const, text: '黑名单' }
  } as const

  /**
   * 获取标签类型配置
   */
  const getTagTypeConfig = (type: string | number | undefined) => {
    const typeStr = String(type || '0')
    return (
      TAG_TYPE_CONFIG[typeStr as keyof typeof TAG_TYPE_CONFIG] || {
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
      apiFn: fetchGetUserList,
      apiParams: {
        current: 1,
        size: 20,
        plateNumber: undefined,
        tagType: undefined
      },
      // 自定义分页字段映射，未设置时将使用全局配置 tableConfig.ts 中的 paginationKey
      // paginationKey: {
      //   current: 'pageNum',
      //   size: 'pageSize'
      // },
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        {
          prop: 'vehicleInfo',
          label: '车辆信息',
          width: 280,
          // visible: false, // 默认是否显示列
          formatter: (row) => {
            return h('div', { class: 'vehicle flex-c' }, [
              h('div', { class: 'ml-2' }, [
                h(
                  'p',
                  { class: 'plate-number' },
                  row.plateNumber || `车辆${Math.floor(Math.random() * 1000)}`
                ),
                h('p', { class: 'tag-name' }, row.tagName || '未命名')
              ])
            ])
          }
        },
        {
          prop: 'tagType',
          label: '标签类型',
          formatter: (row) => {
            const typeConfig = getTagTypeConfig(row.tagType)
            return h(ElTag, { type: typeConfig.type }, () => typeConfig.text)
          }
        },
        { prop: 'reason', label: '标签原因' },
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
                onClick: () => deleteBlacklist(row)
              })
            ])
        }
      ]
    },
    // 数据处理
    transform: {
      // 数据转换器
      dataTransformer: (records: any[]) => {
        // 类型守卫检查
        if (!Array.isArray(records)) {
          console.warn('数据转换器: 期望数组类型，实际收到:', typeof records)
          return []
        }

        // 转换数据为黑白名单格式
        return records.map((item, index: number) => {
          const platePrefix = ['苏', '沪', '浙', '皖', '鲁'][index % 5]
          const tagTypes = [0, 1, 2]
          const tagType = tagTypes[index % 3]
          return {
            ...item,
            plateNumber: `${platePrefix}A${1000 + index}`,
            tagName: tagType === 0 ? '普通车辆' : tagType === 1 ? '白名单车辆' : '黑名单车辆',
            tagType: tagType,
            reason: tagType === 0 ? '普通车辆' : tagType === 1 ? '长期合作' : '违规记录',
            color: tagType === 0 ? '#909399' : tagType === 1 ? '#67C23A' : '#F56C6C'
          }
        })
      }
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
   * 显示黑白名单弹窗
   */
  const showDialog = (type: DialogType, row?: BlacklistListItem): void => {
    console.log('打开弹窗:', { type, row })
    dialogType.value = type
    currentBlacklistData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除黑白名单
   */
  const deleteBlacklist = (row: BlacklistListItem): void => {
    console.log('删除黑白名单:', row)
    ElMessageBox.confirm(`确定要删除该车辆标签吗？`, '删除车辆标签', {
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
      currentBlacklistData.value = {}
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: BlacklistListItem[]): void => {
    selectedRows.value = selection
    console.log('选中行数据:', selectedRows.value)
  }

  // 页面加载时获取数据
  onMounted(() => {
    getData()
  })
</script>
