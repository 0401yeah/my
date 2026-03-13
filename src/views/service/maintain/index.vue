<template>
  <div class="maintain-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <div class="card-header flex justify-between items-center mb-4">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加
        </el-button>
      </div>
      <art-table
        row-key="id"
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <el-tag
            :type="
              row.status === 0
                ? 'warning'
                : row.status === 1
                  ? 'info'
                  : row.status === 2
                    ? 'success'
                    : 'danger'
            "
            size="small"
          >
            {{ row.status === 0 ? '待派单' : row.status === 1 ? '维修中' : row.status === 2 ? '已修复' : '需返厂' }}
          </el-tag>
        </template>
        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" @click="handleDelete(row)" />
          </div>
        </template>
      </art-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="设备名称" prop="equipmentId">
          <el-select v-model="formData.equipmentId" placeholder="请选择设备" style="width: 100%">
            <el-option
              v-for="item in equipmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="issueDesc">
          <el-input v-model="formData.issueDesc" type="textarea" :rows="3" placeholder="请输入故障描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待派单" :value="0" />
            <el-option label="维修中" :value="1" />
            <el-option label="已修复" :value="2" />
            <el-option label="需返厂" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="维修费用" prop="payAmount">
          <el-input-number v-model="formData.payAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import { equipmentMaintainApi, equipmentApi } from '@/api/system'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { Plus } from '@element-plus/icons-vue'

  defineOptions({ name: 'Maintain' })

  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref()
  const equipmentList = ref<any[]>([])
  const formData = ref({
    id: undefined as number | undefined,
    equipmentId: undefined as number | undefined,
    issueDesc: '',
    status: 0,
    payAmount: 0
  })

  const dialogTitle = computed(() => (dialogType.value === 'add' ? '添加维修工单' : '编辑维修工单'))

  const formRules = {
    equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
    issueDesc: [{ required: true, message: '请输入故障描述', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }

  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange, refreshData } = useTable({
    core: {
      apiFn: equipmentMaintainApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        status: ''
      },
      columnsFactory: () => [
        {
          type: 'globalIndex',
          width: 60,
          label: '序号'
        },
        {
          prop: 'equipmentName',
          label: '设备名称',
          minWidth: 200,
          sortable: true
        },
        {
          prop: 'issueDesc',
          label: '故障描述',
          minWidth: 280
        },
        {
          prop: 'status',
          label: '状态',
          width: 100,
          sortable: true,
          useSlot: true
        },
        {
          prop: 'payAmount',
          label: '维修费用',
          sortable: true
        },
        {
          prop: 'gmtCreate',
          label: '报修时间',
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 180,
          fixed: 'right',
          useSlot: true
        }
      ]
    }
  })

  const fetchEquipmentList = async () => {
    try {
      const res = await equipmentApi.fetchList({ current: 1, size: 1000 })
      equipmentList.value = Array.isArray(res) ? res : (res as any)?.records || []
    } catch (error) {
      // 获取设备列表失败
    }
  }

  const handleAdd = () => {
    dialogType.value = 'add'
    formData.value = {
      id: undefined,
      equipmentId: undefined,
      issueDesc: '',
      status: 0,
      payAmount: 0
    }
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    dialogType.value = 'edit'
    formData.value = { ...row }
    dialogVisible.value = true
  }

  const handleDelete = async (row: any) => {
    try {
      await equipmentMaintainApi.delete(row.id)
      ElMessage.success('删除成功')
      refreshData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    await formRef.value.validate()
    try {
      if (dialogType.value === 'add') {
        await equipmentMaintainApi.add(formData.value)
        ElMessage.success('添加成功')
      } else {
        await equipmentMaintainApi.update(formData.value)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  onMounted(() => {
    fetchEquipmentList()
  })
</script>

<style scoped>
  .maintain-page {
    padding: 20px;
  }
</style>
