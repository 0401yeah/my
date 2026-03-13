<template>
  <div class="facility-page">
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
          <div class="status-container">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '在线' : '离线' }}
            </el-tag>
            <el-button 
              size="small" 
              @click="handleStatusChange(row)"
              :type="row.status === 1 ? 'danger' : 'primary'"
              style="padding: 0 10px; height: 22px; font-size: 12px;"
            >
              {{ row.status === 1 ? '离线' : '在线' }}
            </el-button>
          </div>
        </template>
        
        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" @click="handleDelete(row)" />
          </div>
        </template>
      </art-table>
    </el-card>

    <!-- 添加/编辑设备对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="formData.name"></el-input>
        </el-form-item>
        <el-form-item label="设备编码" prop="code">
          <el-input v-model="formData.code"></el-input>
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="formData.type" class="w-full">
            <el-option label="道闸" :value="'barrier'"></el-option>
            <el-option label="摄像头" :value="'camera'"></el-option>
            <el-option label="传感器" :value="'sensor'"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属停车场" prop="parkingLotId">
          <el-select v-model="formData.parkingLotId" class="w-full">
            <el-option 
              v-for="lot in parkingLots" 
              :key="lot.id" 
              :label="lot.name" 
              :value="lot.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="formData.status" class="w-full">
            <el-option label="在线" :value="1"></el-option>
            <el-option label="离线" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import { Plus } from '@element-plus/icons-vue'
  import { useTable } from '@/hooks/core/useTable'
  import { equipmentApi } from '@/api/system'
  import { lotApi } from '@/api/business'

  defineOptions({ name: 'Facility' })

  const parkingLots = ref<any[]>([])
  
  const dialogVisible = ref(false)
  const dialogType = ref<'view' | 'add' | 'edit'>('view')
  const formRef = ref()
  const formData = ref<any>({
    id: undefined,
    name: '',
    code: '',
    type: '',
    status: 1,
    parkingLotId: null
  })

  const dialogTitle = computed(() => {
    switch (dialogType.value) {
      case 'add': return '添加设备'
      case 'edit': return '编辑设备'
      case 'view': return '查看设备'
      default: return '设备'
    }
  })

  const rules = {
    name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
    code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
    type: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
    parkingLotId: [{ required: true, message: '请选择所属停车场', trigger: 'change' }],
    status: [{ required: true, message: '请选择设备状态', trigger: 'change' }]
  }

  // 获取停车场列表
  const fetchParkingLots = async () => {
    try {
      const response: any = await lotApi.fetchLotList({})
      parkingLots.value = response.records || []
      
      if (parkingLots.value.length === 0) {
        parkingLots.value = [
          { id: 1, name: '泰州学院南门地下车库' },
          { id: 2, name: '泰州学院行政楼地面车场' }
        ]
      }
    } catch (error) {
      parkingLots.value = [
        { id: 1, name: '泰州学院南门地下车库' },
        { id: 2, name: '泰州学院行政楼地面车场' }
      ]
    }
  }

  const handleView = (row: any) => {
    formData.value = { ...row }
    dialogType.value = 'view'
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    formData.value = { ...row }
    dialogType.value = 'edit'
    dialogVisible.value = true
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm('确定删除该设备吗？', '提示', { type: 'warning' }).then(async () => {
      await equipmentApi.delete(row.id)
      ElMessage.success('删除成功')
      refreshData()
    })
  }

  const handleAdd = () => {
    dialogType.value = 'add'
    formData.value = {
      name: '',
      code: '',
      type: '',
      parkingLotId: parkingLots.value[0]?.id || 1,
      status: 1
    }
    dialogVisible.value = true
  }

  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange, refreshData } = useTable({
    core: {
      apiFn: async (params) => {
        if (parkingLots.value.length === 0) {
          await fetchParkingLots()
        }
        return await equipmentApi.fetchList(params)
      },
      apiParams: { current: 1, size: 20 },
      columnsFactory: () => [
        { type: 'globalIndex', width: 50, label: '序号' },
        {
          prop: 'parkingLotId',
          label: '停车场名称',
          minWidth: 150,
          formatter: (row: any) => {
            const lotId = row.parkingLotId; 
            if (!lotId) return '未设置';
            const lot = parkingLots.value.find(item => String(item.id) === String(lotId));
            return lot ? lot.name : `未知项目(ID:${lotId})`;
          }
        },
        { prop: 'name', label: '设备名称', minWidth: 120 },
        { prop: 'code', label: '设备编码', minWidth: 120 },
        {
          prop: 'type',
          label: '设备类型',
          formatter: (row: any) => {
            const typeMap: any = { barrier: '道闸', camera: '摄像头', sensor: '传感器' };
            return typeMap[row.type] || row.type;
          }
        },
        { prop: 'status', label: '状态', width: 180, useSlot: true },
        { prop: 'gmtCreate', label: '创建时间', width: 180 }, // 对应后端驼峰字段
        { prop: 'operation', label: '操作', width: 100, fixed: 'right', useSlot: true }
      ]
    }
  })

  onMounted(async () => {
    await fetchParkingLots()
    await refreshData()
  })

  const handleSubmit = async () => {
    await formRef.value.validate()
    try {
      if (dialogType.value === 'add') {
        await equipmentApi.add(formData.value)
        ElMessage.success('添加成功')
      } else if (dialogType.value === 'edit') {
        await equipmentApi.update(formData.value)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  const handleStatusChange = async (row: any) => {
    const newStatus = row.status === 1 ? 0 : 1
    const statusText = newStatus === 1 ? '在线' : '离线'
    
    ElMessageBox.confirm(`确定要将设备设置为${statusText}吗？`, '状态变更', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await equipmentApi.update({ ...row, status: newStatus })
        ElMessage.success(`设备已${statusText}`)
        refreshData()
      } catch (error) {
        ElMessage.error('操作失败，请重试')
      }
    })
  }
</script>

<style scoped>
  .facility-page {
    padding: 20px;
  }

  /* 让内容整体向左靠一点，减小单元格左侧内边距 */
  :deep(.el-table .cell) {
    text-align: left !important;
    padding-left: 10px !important; /* 原为16px，改为10px内容会向左移 */
  }

  /* 序号、状态、操作列保持居中，不设左边距 */
  :deep(.el-table__header th:first-child .cell),
  :deep(.el-table__row td:first-child .cell),
  :deep(.el-table__header th:nth-child(6) .cell),
  :deep(.el-table__row td:nth-child(6) .cell),
  :deep(.el-table__header th:last-child .cell),
  :deep(.el-table__row td:last-child .cell) {
    text-align: center !important;
    padding-left: 0 !important;
  }

  /* 状态容器样式 */
  .status-container {
    display: flex;
    align-items: center;
    gap: 8px;
    justify-content: center;
  }
</style>