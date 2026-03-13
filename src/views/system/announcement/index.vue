<!-- 系统公告页面 -->
<!-- art-full-height 自动计算出页面剩余高度 -->
<!-- art-table-card 一个符合系统样式的 class，同时自动撑满剩余高度 -->
<!-- 更多 useTable 使用示例请移步至 功能示例 下面的高级表格示例或者查看官方文档 -->
<!-- useTable 文档：https://www.artd.pro/docs/zh/guide/hooks/use-table.html -->
<template>
  <div class="announcement-page art-full-height">
    <!-- 搜索栏 -->
    <AnnouncementSearch
      v-model="searchForm"
      @search="handleSearch"
      @reset="resetSearchParams"
    ></AnnouncementSearch>

    <ElCard class="art-table-card" shadow="never">
      <!-- 表格头部 -->
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton @click="showDialog('add')" v-ripple>新增公告</ElButton>
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

      <!-- 系统公告弹窗 -->
      <AnnouncementDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :announcement-data="currentAnnouncementData"
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
import AnnouncementSearch from './modules/announcement-search.vue'
import AnnouncementDialog from './modules/announcement-dialog.vue'
import { ElTag } from 'element-plus'
import { DialogType } from '@/types'

  defineOptions({ name: 'SystemAnnouncementPage' })

  type AnnouncementListItem = any

  // 弹窗相关
  const dialogType = ref<DialogType>('add')
  const dialogVisible = ref(false)
  const currentAnnouncementData = ref<Partial<AnnouncementListItem>>({})

  // 选中行
  const selectedRows = ref<AnnouncementListItem[]>([])

  // 搜索表单
  const searchForm = ref({
    title: undefined,
    status: undefined,
    date: undefined
  })

  // 公告状态配置
  const ANNOUNCEMENT_STATUS_CONFIG = {
    '1': { type: 'success' as const, text: '发布' },
    '0': { type: 'danger' as const, text: '草稿' }
  } as const

  /**
   * 获取公告状态配置
   */
  const getAnnouncementStatusConfig = (status: string | number) => {
    const statusStr = String(status)
    return (
      ANNOUNCEMENT_STATUS_CONFIG[statusStr as keyof typeof ANNOUNCEMENT_STATUS_CONFIG] || {
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
        // 这里应该调用真实的系统公告API
        // 暂时返回空数组，确保没有模拟数据
        return { records: [], total: 0 }
      },
      apiParams: {
        current: 1,
        size: 20,
        title: undefined,
        status: undefined,
        date: undefined
      },
      columnsFactory: () => [
        { type: 'selection' }, // 勾选列
        { type: 'index', width: 60, label: '序号' }, // 序号
        {
          prop: 'announcementInfo',
          label: '公告信息',
          width: 280,
          formatter: (row) => {
            return h('div', { class: 'announcement flex-c' }, [
              h('div', { class: 'ml-2' }, [
                h('p', { class: 'title' }, row.title || '无标题'),
                h('p', { class: 'summary' }, row.summary || '无内容')
              ])
            ])
          }
        },
        {
          prop: 'status',
          label: '状态',
          formatter: (row) => {
            const statusConfig = getAnnouncementStatusConfig(row.status)
            return h(ElTag, { type: statusConfig.type }, () => statusConfig.text)
          }
        },
        { prop: 'author', label: '发布人' },
        {
          prop: 'gmt_create',
          label: '发布时间',
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 120,
          fixed: 'right', // 固定列
          formatter: (row) =>
            h('div', [
              h(ArtButtonTable, {
                type: 'edit',
                onClick: () => showDialog('edit', row)
              }),
              h(ArtButtonTable, {
                type: 'delete',
                onClick: () => deleteAnnouncement(row)
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
   * 显示系统公告弹窗
   */
  const showDialog = (type: DialogType, row?: AnnouncementListItem): void => {
    console.log('打开弹窗:', { type, row })
    dialogType.value = type
    currentAnnouncementData.value = row || {}
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  /**
   * 删除系统公告
   */
  const deleteAnnouncement = (row: AnnouncementListItem): void => {
    console.log('删除系统公告:', row)
    ElMessageBox.confirm(`确定要删除该公告吗？`, '删除公告', {
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
      currentAnnouncementData.value = {}
    } catch (error) {
      console.error('提交失败:', error)
    }
  }

  /**
   * 处理表格行选择变化
   */
  const handleSelectionChange = (selection: AnnouncementListItem[]): void => {
    selectedRows.value = selection
    console.log('选中行数据:', selectedRows.value)
  }

  // 页面加载时获取数据
  onMounted(() => {
    getData()
  })
</script>
