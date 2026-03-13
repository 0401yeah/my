<template>
  <div class="user-notification flex flex-col gap-4 pb-5">
    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <div class="card-title">通知中心</div>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="tableData"
        :columns="columns"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #title="{ row }">
          <span>{{ row.title }}</span>
        </template>

        <template #type="{ row }">
          <ElTag v-if="row.type === 'system'" type="primary">系统通知</ElTag>
          <ElTag v-else-if="row.type === 'parking'" type="success">停车通知</ElTag>
          <ElTag v-else-if="row.type === 'payment'" type="warning">缴费通知</ElTag>
          <ElTag v-else type="info">其他</ElTag>
        </template>

        <template #operation="{ row }">
          <ArtButtonTable type="view" @click="handleView(row)" />
        </template>
      </ArtTable>
    </ElCard>

    <ElDialog v-model="dialogVisible" title="通知详情" width="500px">
      <div class="notification-detail">
        <h3 class="text-lg font-bold mb-4">{{ currentNotification?.title }}</h3>
        <p class="text-gray-600 mb-4">{{ currentNotification?.content }}</p>
        <p class="text-sm text-gray-400">{{ currentNotification?.time }}</p>
      </div>
      <template #footer>
        <ElButton @click="dialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { noticeApi } from '@/api/business'
import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'

defineOptions({ name: 'UserNotification' })

interface NotificationItem {
  id: number
  title: string
  content: string
  type: string
  time: string
  isRead: boolean
}

const tableRef = ref()
const loading = ref(false)
const tableData = ref<NotificationItem[]>([])
const dialogVisible = ref(false)
const currentNotification = ref<NotificationItem | null>(null)

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = ref([
  { type: 'globalIndex' as const, width: 60, label: '序号' },
  { prop: 'title', label: '标题', minWidth: 200, useSlot: true },
  { prop: 'type', label: '类型', width: 120, useSlot: true },
  { prop: 'time', label: '时间', width: 180 },
  { prop: 'operation', label: '操作', width: 120, useSlot: true, fixed: 'right' as const }
])

const columnChecks = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await noticeApi.fetchNoticeList({
      current: pagination.value.current,
      size: pagination.value.size
    })
    
    if (res && res.records) {
      tableData.value = res.records.map((item: any) => ({
        id: item.id,
        title: item.title,
        content: item.content,
        type: item.type || 'system',
        time: item.gmtCreate || item.createTime,
        isRead: item.isRead || false
      }))
      pagination.value.total = res.total || 0
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pagination.value.size = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  pagination.value.current = val
  fetchData()
}

const handleRefresh = () => {
  fetchData()
}

const handleView = (row: NotificationItem) => {
  currentNotification.value = row
  dialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-notification {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.notification-detail {
  padding: 10px 0;
}
</style>
