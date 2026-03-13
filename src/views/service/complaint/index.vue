<template>
  <div class="complaint-page flex flex-col gap-4 pb-5">
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
              filename="投诉管理数据"
              :auto-index="true"
              button-text="导出"
              @export-success="handleExportSuccess"
            />
          </ElSpace>
        </template>
        <template #right>
          <ElButton type="primary" @click="handleAdd">
            <Plus />
            新建投诉
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
            <ArtButtonTable type="edit" icon="ri:chat-1-line" tooltip="回复" @click="handleReply(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <!-- 查看详情对话框 -->
    <ElDialog v-model="detailDialogVisible" title="投诉详情" width="700px">
      <ElDescriptions :column="2" border v-if="currentFeedback">
        <ElDescriptionsItem label="投诉编号">{{ currentFeedback.id }}</ElDescriptionsItem>
        <ElDescriptionsItem label="客户姓名">{{ currentFeedback.username }}</ElDescriptionsItem>
        <ElDescriptionsItem label="联系电话">{{ currentFeedback.mobile }}</ElDescriptionsItem>
        <ElDescriptionsItem label="投诉状态">{{ getStatusText(currentFeedback.status) }}</ElDescriptionsItem>
        <ElDescriptionsItem label="投诉类型">{{ getTypeText(currentFeedback.type) }}</ElDescriptionsItem>
        <ElDescriptionsItem label="投诉时间">{{ currentFeedback.gmtCreate }}</ElDescriptionsItem>
        <ElDescriptionsItem label="投诉内容" :span="2">{{ currentFeedback.content }}</ElDescriptionsItem>
        <ElDescriptionsItem label="回复内容" :span="2">{{ currentFeedback.replyContent || '未回复' }}</ElDescriptionsItem>
        <ElDescriptionsItem label="回复时间" :span="2">{{ currentFeedback.replyTime || '未回复' }}</ElDescriptionsItem>
      </ElDescriptions>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>

    <!-- 回复投诉对话框 -->
    <ElDialog v-model="editDialogVisible" title="回复投诉" width="700px">
      <ElForm :model="editForm" label-width="100px" v-if="editForm">
        <ElFormItem label="客户姓名">
          <ElInput v-model="editForm.username" disabled />
        </ElFormItem>
        <ElFormItem label="联系电话">
          <ElInput v-model="editForm.mobile" disabled />
        </ElFormItem>
        <ElFormItem label="投诉内容">
          <ElInput v-model="editForm.content" type="textarea" :rows="4" disabled />
        </ElFormItem>
        <ElFormItem label="投诉状态">
          <ElSelect v-model="editForm.status" placeholder="请选择投诉状态">
            <ElOption label="未处理" :value="0" />
            <ElOption label="已处理" :value="1" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="回复内容">
          <ElInput v-model="editForm.replyContent" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="editDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitReply" :loading="editLoading">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 新建投诉对话框 -->
    <ElDialog v-model="addDialogVisible" title="新建投诉" width="700px">
      <ElForm :model="addForm" label-width="100px">
        <ElFormItem label="客户姓名" required>
          <ElInput v-model="addForm.username" placeholder="请输入客户姓名" />
        </ElFormItem>
        <ElFormItem label="联系电话" required>
          <ElInput v-model="addForm.mobile" placeholder="请输入联系电话" />
        </ElFormItem>
        <ElFormItem label="投诉类型" required>
          <ElSelect v-model="addForm.type" placeholder="请选择投诉类型">
            <ElOption label="服务投诉" :value="0" />
            <ElOption label="设备投诉" :value="1" />
            <ElOption label="其他投诉" :value="2" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="投诉内容" required>
          <ElInput v-model="addForm.content" type="textarea" :rows="4" placeholder="请输入投诉内容" />
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
import { feedbackApi } from '@/api/business'

interface Feedback {
  id: number
  userId: number
  username: string
  mobile: string
  content: string
  replyContent: string
  replyUserId: number
  replyTime: string
  type: number
  status: number
  gmtCreate: string
}

defineOptions({ name: 'Complaint' })

// 选中的行
const selectedRows = ref<any[]>([])

// 表格实例引用
const tableRef = ref()
const searchBarRef = ref()

// 搜索表单状态
const searchFormState = ref<{ username: string; status: string | number }>({
  username: '',
  status: ''
})

// 搜索表单配置
const searchItems = computed(() => [
  {
    key: 'username',
    label: '客户姓名',
    type: 'input',
    props: {
      placeholder: '请输入客户姓名'
    }
  },
  {
    key: 'status',
    label: '投诉状态',
    type: 'select',
    options: [
      { label: '全部', value: '' },
      { label: '未处理', value: 0 },
      { label: '已处理', value: 1 }
    ]
  }
])

// 状态映射
const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '未处理',
    1: '已处理'
  }
  return statusMap[status] || '未知'
}

// 类型映射
const getTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    0: '服务投诉',
    1: '设备投诉',
    2: '其他投诉'
  }
  return typeMap[type] || '未知'
}

// 导出列配置
const exportColumns = computed(() => ({
  id: { title: '投诉编号', width: 10 },
  username: { title: '客户姓名', width: 15 },
  mobile: { title: '联系电话', width: 20 },
  content: { title: '投诉内容', width: 30 },
  replyContent: { title: '回复内容', width: 30 },
  type: {
    title: '投诉类型',
    width: 15,
    formatter: (value: number) => getTypeText(value)
  },
  status: {
    title: '投诉状态',
    width: 15,
    formatter: (value: number) => getStatusText(value)
  },
  gmtCreate: { title: '投诉时间', width: 20 }
}))

// 获取投诉列表
const fetchFeedbackList = async (params: any) => {
  try {
    const response = await feedbackApi.fetchFeedbackList(params)
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
    apiFn: fetchFeedbackList,
    apiParams: {
      current: 1,
      size: 20,
      ...searchFormState.value
    },
    columnsFactory: () => [
      { type: 'selection', width: 40 },
      { type: 'globalIndex', width: 60, label: '序号' },
      { prop: 'username', label: '客户姓名', minWidth: 100, sortable: true },
      { prop: 'mobile', label: '联系电话', minWidth: 120, sortable: true },
      { prop: 'content', label: '投诉内容', minWidth: 200, showOverflowTooltip: true },
      { 
        prop: 'type', 
        label: '投诉类型', 
        minWidth: 100,
        formatter: (row: any) => getTypeText(row.type)
      },
      { 
        prop: 'status', 
        label: '投诉状态', 
        minWidth: 100,
        formatter: (row: any) => getStatusText(row.status)
      },
      { 
        prop: 'gmtCreate', 
        label: '投诉时间', 
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
const currentFeedback = ref<Feedback | null>(null)

// 编辑对话框
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editForm = ref<any>(null)

// 新建对话框
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addForm = ref({
  username: '',
  mobile: '',
  type: 0,
  content: '',
  status: 0
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
  const params = { ...searchFormState.value }
  if (params.status === '') {
    delete params.status
  }
  Object.assign(searchParams, params)
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
    const response = await feedbackApi.fetchFeedbackDetail(row.id)
    currentFeedback.value = response
    detailDialogVisible.value = true
  } catch (error) {
    // 查看详情失败
  }
}

const handleReply = (row: any) => {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

const submitReply = async () => {
  if (!editForm.value) return

  editLoading.value = true
  try {
    await feedbackApi.updateFeedback({
      id: editForm.value.id,
      status: editForm.value.status,
      replyContent: editForm.value.replyContent
    })
    ElMessage.success('回复成功')
    editDialogVisible.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('回复失败')
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '删除该投诉吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await feedbackApi.deleteFeedback(row.id)
    ElMessage.success('投诉删除成功')
    // 删除成功后刷新数据
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除投诉失败')
    }
  }
}

const handleAdd = () => {
  // 重置表单
  addForm.value = {
    username: '',
    mobile: '',
    type: 0,
    content: '',
    status: 0
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  // 表单验证
  if (!addForm.value.username || !addForm.value.mobile || !addForm.value.content) {
    ElMessage.warning('请填写必填项')
    return
  }

  addLoading.value = true
  try {
    await feedbackApi.addFeedback(addForm.value)
    ElMessage.success('投诉添加成功')
    addDialogVisible.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('添加投诉失败')
  } finally {
    addLoading.value = false
  }
}
</script>

<style scoped>
.complaint-page {
  padding: 20px;
}
</style>
