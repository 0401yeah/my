<template>
  <div class="user-complain flex flex-col gap-4 pb-5">
    <ElCard class="flex-1 art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #right>
          <ElButton type="primary" @click="handleAdd">
            <Plus />
            新建求助
          </ElButton>
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
        <template #type="{ row }">
          <ElTag v-if="row.type === 'vehicle'" type="danger">车辆故障</ElTag>
          <ElTag v-else-if="row.type === 'lock'" type="warning">车锁问题</ElTag>
          <ElTag v-else-if="row.type === 'phone'" type="primary">紧急联系</ElTag>
          <ElTag v-else type="info">其他问题</ElTag>
        </template>

        <template #status="{ row }">
          <ElTag v-if="row.status === 0" type="warning">处理中</ElTag>
          <ElTag v-else-if="row.status === 1" type="success">已解决</ElTag>
          <ElTag v-else type="info">未知</ElTag>
        </template>

        <template #operation="{ row }">
          <div class="flex gap-1">
            <ElButton type="primary" link size="small" @click="handleEdit(row)">
              <ElIcon><Edit /></ElIcon>
            </ElButton>
          </div>
        </template>
      </ArtTable>
    </ElCard>

    <ElDialog v-model="editDialogVisible" title="编辑求助" width="500px">
      <ElForm :model="editForm" label-width="100px">
        <ElFormItem label="求助类型" required>
          <ElSelect v-model="editForm.type" placeholder="请选择求助类型">
            <ElOption label="车辆故障" value="vehicle" />
            <ElOption label="车锁问题" value="lock" />
            <ElOption label="紧急联系" value="phone" />
            <ElOption label="其他问题" value="other" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="联系方式" required>
          <ElInput v-model="editForm.contactPhone" placeholder="请输入联系方式" />
        </ElFormItem>
        <ElFormItem label="问题描述" required>
          <ElInput
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请描述您遇到的问题"
          />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput
            v-model="editForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="editDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitEdit">保存</ElButton>
      </template>
    </ElDialog>

    <ElDialog v-model="addDialogVisible" title="新建求助" width="500px">
      <ElForm :model="addForm" label-width="100px">
        <ElFormItem label="求助类型" required>
          <ElSelect v-model="addForm.type" placeholder="请选择求助类型">
            <ElOption label="车辆故障" value="vehicle" />
            <ElOption label="车锁问题" value="lock" />
            <ElOption label="紧急联系" value="phone" />
            <ElOption label="其他问题" value="other" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="联系方式" required>
          <ElInput v-model="addForm.contactPhone" placeholder="请输入联系方式" />
        </ElFormItem>
        <ElFormItem label="问题描述" required>
          <ElInput
            v-model="addForm.description"
            type="textarea"
            :rows="3"
            placeholder="请描述您遇到的问题"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="addDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="submitAdd">提交</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElIcon, ElMessage } from 'element-plus'
import { Edit, Plus } from '@element-plus/icons-vue'
import { feedbackApi } from '@/api/business'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

defineOptions({ name: 'UserComplain' })

interface ComplainItem {
  id: number
  username: string
  type: string
  contactPhone: string
  description: string
  status: number
  remark: string
  createTime: string
}

const tableRef = ref()
const loading = ref(false)
const tableData = ref<ComplainItem[]>([])
const editDialogVisible = ref(false)
const addDialogVisible = ref(false)
const editingId = ref<number | null>(null)

const addForm = ref({
  type: '',
  contactPhone: '',
  description: ''
})

const editForm = ref({
  type: '',
  contactPhone: '',
  description: '',
  remark: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = ref([
  { type: 'globalIndex' as const, width: 60, label: '序号' },
  { prop: 'username', label: '用户名', width: 120 },
  { prop: 'type', label: '求助类型', width: 120, useSlot: true },
  { prop: 'contactPhone', label: '联系方式', width: 130 },
  { prop: 'description', label: '问题描述', minWidth: 200, showOverflowTooltip: true },
  { prop: 'status', label: '状态', width: 100, useSlot: true },
  { prop: 'createTime', label: '求助时间', minWidth: 180 },
  { prop: 'operation', label: '操作', width: 80, useSlot: true, fixed: 'right' as const }
])

const columnChecks = ref([])

const handleEdit = (row: ComplainItem) => {
  editingId.value = row.id
  editForm.value = {
    type: row.type,
    contactPhone: row.contactPhone,
    description: row.description,
    remark: row.remark
  }
  editDialogVisible.value = true
}

const submitEdit = () => {
  if (!editForm.value.type || !editForm.value.contactPhone || !editForm.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const index = tableData.value.findIndex(item => item.id === editingId.value)
  if (index !== -1) {
    tableData.value[index] = {
      ...tableData.value[index],
      type: editForm.value.type,
      contactPhone: editForm.value.contactPhone,
      description: editForm.value.description,
      remark: editForm.value.remark
    }
    ElMessage.success('修改成功')
    editDialogVisible.value = false
  }
}

const handleAdd = () => {
  addForm.value = {
    type: '',
    contactPhone: '',
    description: ''
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  if (!addForm.value.type || !addForm.value.contactPhone || !addForm.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    const userId = userStore.info?.userId
    const typeMap: Record<string, number> = {
      vehicle: 1,
      lock: 2,
      phone: 3,
      other: 4
    }
    
    const feedbackData = {
      type: typeMap[addForm.value.type],
      contactPhone: addForm.value.contactPhone,
      content: addForm.value.description,
      status: 0,
      userId: userId
    }
    
    await feedbackApi.addFeedback(feedbackData)
    ElMessage.success('求助已提交，工作人员将尽快与您联系')
    addDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交求助失败:', error)
    ElMessage.error('提交求助失败，请重试')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const userId = userStore.info?.userId
    const res: any = await feedbackApi.fetchFeedbackList({
      current: pagination.value.current,
      size: pagination.value.size,
      userId: userId
    })
    if (res && res.records) {
      tableData.value = res.records.map((item: any) => ({
        id: item.id,
        username: item.username || '未知用户',
        type: item.type === 1 ? 'vehicle' : item.type === 2 ? 'lock' : item.type === 3 ? 'phone' : 'other',
        contactPhone: item.contactPhone || item.contact_phone || item.mobile || '',
        description: item.content || item.description || '',
        status: item.status,
        remark: item.remark || '',
        createTime: item.gmtCreate || item.createTime || item.gmt_create || ''
      }))
      pagination.value.total = res.total || 0
    }
  } catch (error) {
    console.error('获取投诉列表失败:', error)
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-complain {
  padding: 20px;
}
</style>
