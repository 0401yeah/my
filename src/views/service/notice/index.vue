<template>
  <div class="notice-page flex flex-col gap-4 pb-5">
    <!-- 搜索区域 -->
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

    <!-- 表格区域 -->
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
            <ArtExcelExport
              :data="data as any"
              :columns="exportColumns as any"
              filename="通知与公告数据"
              :auto-index="true"
              button-text="导出"
              @export-success="handleExportSuccess"
            />
          </ElSpace>
        </template>
        <template #right>
          <ElButton type="primary" @click="handleAdd">
            <Plus />
            新建公告
          </ElButton>
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
        <!-- 操作列 -->
        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="view" :row="row" @click="handleView(row)" />
            <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <!-- 查看详情对话框 -->
    <ElDialog v-model="detailDialogVisible" title="公告详情" width="700px">
      <ElDescriptions :column="2" border v-if="currentNotice">
        <ElDescriptionsItem label="公告标题">{{ currentNotice.title }}</ElDescriptionsItem>
        <ElDescriptionsItem label="公告类型">{{ getTypeText(currentNotice.type) }}</ElDescriptionsItem>
        <ElDescriptionsItem label="公告状态">{{ getStatusText(currentNotice.status) }}</ElDescriptionsItem>
        <ElDescriptionsItem label="发布人">{{ currentNotice.creatorName || currentNotice.creatorId || '系统' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="发布时间">{{ currentNotice.gmtCreate }}</ElDescriptionsItem>
        <ElDescriptionsItem label="组织ID">{{ currentNotice.orgId || '-' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="公告内容" :span="2">{{ currentNotice.content }}</ElDescriptionsItem>
      </ElDescriptions>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>

    <!-- 编辑公告对话框 -->
    <ElDialog v-model="editDialogVisible" title="编辑公告" width="700px">
      <ElForm :model="editForm" label-width="100px" v-if="editForm">
        <ElFormItem label="公告标题" required>
          <ElInput v-model="editForm.title" placeholder="请输入公告标题" />
        </ElFormItem>
        <ElFormItem label="公告类型" required>
          <ElSelect v-model="editForm.type" placeholder="请选择公告类型">
            <ElOption label="系统通知" :value="0" />
            <ElOption label="活动公告" :value="1" />
            <ElOption label="其他" :value="2" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="公告状态" required>
          <ElSelect v-model="editForm.status" placeholder="请选择公告状态">
            <ElOption label="草稿" :value="0" />
            <ElOption label="已发布" :value="1" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="组织ID">
          <ElInput v-model="editForm.orgId" placeholder="请输入组织ID" />
        </ElFormItem>
        <ElFormItem label="发布人ID">
          <ElInput v-model="editForm.creatorId" placeholder="请输入发布人ID" />
        </ElFormItem>
        <ElFormItem label="公告内容" required>
          <ElInput v-model="editForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="editDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitEdit" :loading="editLoading">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 新建公告对话框 -->
    <ElDialog v-model="addDialogVisible" title="新建公告" width="700px">
      <ElForm :model="addForm" label-width="100px">
        <ElFormItem label="公告标题" required>
          <ElInput v-model="addForm.title" placeholder="请输入公告标题" />
        </ElFormItem>
        <ElFormItem label="公告类型" required>
          <ElSelect v-model="addForm.type" placeholder="请选择公告类型">
            <ElOption label="系统通知" :value="0" />
            <ElOption label="活动公告" :value="1" />
            <ElOption label="其他" :value="2" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="公告状态" required>
          <ElSelect v-model="addForm.status" placeholder="请选择公告状态">
            <ElOption label="草稿" :value="0" />
            <ElOption label="已发布" :value="1" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="组织ID">
          <ElInput v-model="addForm.orgId" placeholder="请输入组织ID" />
        </ElFormItem>
        <ElFormItem label="发布人ID">
          <ElInput v-model="addForm.creatorId" placeholder="请输入发布人ID" />
        </ElFormItem>
        <ElFormItem label="公告内容" required>
          <ElInput v-model="addForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="addDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitAdd" :loading="addLoading">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTable } from '@/hooks/core/useTable'
import ArtExcelExport from '@/components/core/forms/art-excel-export/index.vue'
import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'
import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
import ArtTable from '@/components/core/tables/art-table/index.vue'
import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
import { announcementApi } from '@/api/system'

interface Notice {
  id: number
  title: string
  content: string
  type: number
  status: number
  orgId: number
  creatorId: number
  creatorName?: string
  gmtCreate: string
}

defineOptions({ name: 'Notice' })

// 选中的行
const selectedRows = ref<any[]>([])

// 表格实例引用
const tableRef = ref()
const searchBarRef = ref()

// 搜索表单状态
const searchFormState = ref({
  title: '',
  status: ''
})

// 搜索表单配置
const searchItems = computed(() => [
  {
    key: 'title',
    label: '公告标题',
    type: 'input',
    props: {
      placeholder: '请输入公告标题'
    }
  },
  {
    key: 'status',
    label: '公告状态',
    type: 'select',
    options: [
      { label: '全部', value: '' },
      { label: '草稿', value: '0' },
      { label: '已发布', value: '1' }
    ]
  }
])

// 状态映射
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '草稿',
    1: '已发布'
  }
  return statusMap[status] || '未知'
}

// 类型映射
const getTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    0: '系统通知',
    1: '活动公告',
    2: '其他'
  }
  return typeMap[type] || '未知'
}

// 导出列配置
const exportColumns = computed(() => ({
  title: { title: '公告标题', width: 20 },
  type: {
    title: '公告类型',
    width: 10,
    formatter: (value: number) => getTypeText(value)
  },
  status: {
    title: '公告状态',
    width: 10,
    formatter: (value: number) => getStatusText(value)
  },
  creatorId: { title: '发布人ID', width: 15 },
  content: { title: '公告内容', width: 30 },
  gmtCreate: { title: '发布时间', width: 20 }
}))

// 获取公告列表
const fetchNoticeList = async (params: any) => {
  try {
    const response = await announcementApi.fetchList(params)
    return response
  } catch (error) {
    throw error
  }
}

// 使用 useTable Hook 管理表格数据
const {
  data, // 表格数据
  loading, // 加载中状态
  pagination, // 分页信息
  handleSizeChange, // 分页大小变化处理
  handleCurrentChange, // 当前页变化处理
  searchParams, // 搜索参数
  resetSearchParams, // 重置搜索参数
  getData, // 获取数据
  refreshData, // 全量刷新
  columns, // 表格列配置
  columnChecks // 列显示、拖拽配置
} = useTable({
  // 核心配置
  core: {
    apiFn: fetchNoticeList,
    apiParams: {
      current: 1,
      size: 20,
      ...searchFormState.value
    },
    columnsFactory: () => [
      { type: 'selection', width: 40 },
      { type: 'globalIndex', width: 60, label: '序号' },
      { prop: 'title', label: '公告标题', minWidth: 200, showOverflowTooltip: true, sortable: true },
      { 
        prop: 'type', 
        label: '公告类型', 
        minWidth: 100,
        formatter: (row: any) => getTypeText(row.type)
      },
      { 
        prop: 'status', 
        label: '公告状态', 
        minWidth: 100,
        formatter: (row: any) => getStatusText(row.status)
      },
      { 
        prop: 'creatorName', 
        label: '发布人', 
        minWidth: 120, 
        formatter: (row: any) => row.creatorName || row.creatorId || '系统'
      },
      { 
        prop: 'gmtCreate', 
        label: '发布时间', 
        minWidth: 160,
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

  // 生命周期钩子
  hooks: {
    onSuccess: (data, response) => {
      // 数据加载成功
    },
    onError: (error) => {
      // 数据加载失败
    }
  }
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentNotice = ref<Notice | null>(null)

// 编辑对话框
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editForm = ref<any>(null)

// 新建对话框
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addForm = ref({
  title: '',
  type: 0,
  status: 0,
  orgId: 0,
  creatorId: 0,
  content: ''
})

// 事件处理函数
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

const handleRowClick = (row: any) => {
}

const handleSortChange = (sortInfo: any) => {
}

const handleSearch = async () => {
  Object.assign(searchParams, searchFormState.value)
  getData()
}

const handleReset = () => {
  resetSearchParams()
}

const handleRefresh = () => {
  refreshData()
}

// 导出成功处理
const handleExportSuccess = (filename: string, count: number) => {
  // 导出成功
}

// CRUD 操作
const handleView = async (row: any) => {
  try {
    const response = await announcementApi.getById(row.id)
    currentNotice.value = response as Notice
    detailDialogVisible.value = true
  } catch (error) {
    // 查看详情失败
  }
}

const handleEdit = (row: any) => {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  if (!editForm.value) return

  // 表单验证
  if (!editForm.value.title || !editForm.value.content) {
    ElMessage.warning('请填写公告标题和内容')
    return
  }

  editLoading.value = true
  try {
    await announcementApi.update({
      id: editForm.value.id,
      title: editForm.value.title,
      type: editForm.value.type,
      status: editForm.value.status,
      orgId: editForm.value.orgId,
      creatorId: editForm.value.creatorId,
      content: editForm.value.content
    })
    ElMessage.success('公告更新成功')
    editDialogVisible.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('更新公告失败')
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '删除该公告吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await announcementApi.delete(row.id)
    ElMessage.success('公告删除成功')
    // 删除成功后刷新数据
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除公告失败')
    }
  }
}

const handleAdd = () => {
  // 重置表单
  addForm.value = {
    title: '',
    type: 0,
    status: 0,
    orgId: 0,
    creatorId: 0,
    content: ''
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  // 表单验证
  if (!addForm.value.title || !addForm.value.content) {
    ElMessage.warning('请填写公告标题和内容')
    return
  }

  addLoading.value = true
  try {
    await announcementApi.add(addForm.value)
    ElMessage.success('公告添加成功')
    addDialogVisible.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('添加公告失败')
  } finally {
    addLoading.value = false
  }
}
</script>

<style scoped>
.notice-page {
  padding: 20px;
}
</style>