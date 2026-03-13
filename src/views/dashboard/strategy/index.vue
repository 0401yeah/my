<template>
  <div class="strategy-page">
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
        :data="data as Record<string, any>[]"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <div class="flex items-center gap-2">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'" 
              size="small" 
              @click="handleStatusChange(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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

    <!-- 查看/编辑对话框 -->
    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="600px" center>
      <ElForm :model="formData" :rules="rules" ref="formRef" label-position="top">
        <ElRow :gutter="20">
          <ElCol :span="24">
            <ElFormItem label="策略名称">
              <ElInput v-model="formData.strategyName" :disabled="dialogType === 'view'" />
            </ElFormItem>
          </ElCol>

          <ElCol :span="24">
            <ElFormItem label="适用停车场">
              <ElSelect v-model="formData.parkingLotId" class="w-full" :disabled="dialogType === 'view'" placeholder="选择停车场">
                <ElOption label="全部" :value="0" />
                <ElOption
                  v-for="lot in parkingLotOptions"
                  :key="lot.id"
                  :label="lot.name"
                  :value="lot.id"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :span="12">
            <ElFormItem label="是否允许叠加">
              <ElSelect v-model="formData.isStackable" class="w-full" :disabled="dialogType === 'view'">
                <ElOption label="是" :value="1" />
                <ElOption label="否" :value="0" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="状态">
              <ElSelect v-model="formData.status" class="w-full" :disabled="dialogType === 'view'">
                <ElOption label="启用" :value="1" />
                <ElOption label="禁用" :value="0" />
              </ElSelect>
            </ElFormItem>
          </ElCol>

          <ElCol :span="24">
            <ElFormItem label="策略描述">
              <ElInput v-model="formData.description" type="textarea" :rows="3" :disabled="dialogType === 'view'" />
            </ElFormItem>
          </ElCol>
        </ElRow>
      </ElForm>
      <template #footer v-if="dialogType !== 'view'">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useTable } from '@/hooks/core/useTable'
import { operationStrategyApi } from '@/api/system'
import { lotApi } from '@/api/business'
import { formatTime } from '@/utils/time'

  defineOptions({ name: 'Strategy' })

  const dialogVisible = ref(false)
  const dialogType = ref<'view' | 'edit' | 'add'>('view')
  const formRef = ref()
  const parkingLotOptions = ref<{ id: number; name: string }[]>([])

  const formData = ref<any>({
    id: undefined,
    strategyName: '',
    description: '',
    parkingLotId: undefined,
    isStackable: 0,
    status: 1
  })

  const dialogTitle = computed(() => {
    switch (dialogType.value) {
      case 'add': return '添加运营策略'
      case 'view': return '策略详情'
      case 'edit': return '编辑策略'
      default: return '运营策略'
    }
  })

  const rules = {
    strategyName: [{ required: true, message: '请输入策略名称', trigger: 'blur' }]
  }

  // 停车场映射表，用于显示停车场名称
  const parkingLotsMap: Record<number, string> = {
    1: '泰州学院南门地下车库',
    2: '泰州学院行政楼地面车场',
    3: '泰州万达广场地下停车场',
    4: '凤城河老街游客车场',
    5: '医药城国际会议中心车场'
  }

  // 自定义的策略列表获取函数，添加错误处理
  const fetchStrategyList = async (params: any) => {
    try {
      const res: any = await operationStrategyApi.fetchList(params)
      
      // 检查响应格式
      if (res && (res.data || res.records)) {
        if (res.data && res.data.records) {
          return {
            records: res.data.records,
            total: res.data.total || res.data.records.length,
            size: res.data.size || 20,
            current: res.data.current || 1
          }
        } else if (res.records) {
          return {
            records: res.records,
            total: res.total || res.records.length,
            size: res.size || 20,
            current: res.current || 1
          }
        } else {
          return {
            records: Array.isArray(res.data) ? res.data : [],
            total: Array.isArray(res.data) ? res.data.length : 0,
            size: 20,
            current: 1
          }
        }
      } else {
        // 返回空数据，避免表格崩溃
        return {
          records: [],
          total: 0,
          size: 20,
          current: 1
        }
      }
    } catch (error) {
      // 捕获错误时返回空数据
      return {
        records: [],
        total: 0,
        size: 20,
        current: 1
      }
    }
  }

  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange, refreshData } = useTable({
    core: {
      apiFn: fetchStrategyList,
      apiParams: {
        current: 1,
        size: 20,
        strategyName: '',
        status: ''
      },
      columnsFactory: () => [
        {
          type: 'globalIndex',
          width: 60,
          label: '序号'
        },
        {
          prop: 'strategyName',
          label: '策略名称',
          minWidth: 150,
          sortable: true
        },
        {
          prop: 'description',
          label: '策略描述',
          minWidth: 200
        },
        {
          prop: 'parkingLotId',
          label: '适用停车场',
          minWidth: 150,
          formatter: (row: any) => {
            if (row.parkingLotId === 0) {
              return '全部'
            }
            
            // 优先从 parkingLotOptions 中查找
            const parkingLot = parkingLotOptions.value.find((lot: any) => lot.id === row.parkingLotId)
            if (parkingLot) {
              return parkingLot.name
            }
            
            // 其次从映射表中查找
            return parkingLotsMap[row.parkingLotId] || '全部'
          }
        },
        {
          prop: 'isStackable',
          label: '允许叠加',
          minWidth: 80,
          formatter: (row: any) => row.isStackable === 1 ? '是' : '否'
        },
        {
          prop: 'status',
          label: '状态',
          minWidth: 160,
          sortable: true,
          useSlot: true
        },
        {
          prop: 'creatorName',
          label: '创建人',
          minWidth: 100
        },
        {
          prop: 'gmtCreate',
          label: '创建时间',
          minWidth: 160,
          sortable: true,
          formatter: (row: any) => formatTime(row.gmtCreate)
        },
        {
          prop: 'operation',
          label: '操作',
          width: 150,
          useSlot: true,
          fixed: 'right'
        }
      ]
    }
  })

  const handleView = (row: any) => {
    dialogType.value = 'view'
    formData.value = { ...row }
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    dialogType.value = 'edit'
    formData.value = { ...row }
    dialogVisible.value = true
  }

  const handleSubmit = async () => {
    await formRef.value.validate()
    try {
      if (dialogType.value === 'add') {
        await operationStrategyApi.add(formData.value)
        ElMessage.success('添加成功')
      } else if (dialogType.value === 'edit') {
        await operationStrategyApi.update(formData.value)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm('确定要删除该策略吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await operationStrategyApi.delete(row.id)
      ElMessage.success('删除成功')
      refreshData()
    })
  }

  const handleStatusChange = async (row: any) => {
    try {
      // 切换状态
      const newStatus = row.status === 1 ? 0 : 1
      // 调用API更新状态
      await operationStrategyApi.update({ id: row.id, status: newStatus })
      // 显示成功消息
      ElMessage.success(`${row.status === 1 ? '禁用' : '启用'}成功`)
      // 刷新数据
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  const fetchParkingLots = async () => {
    try {
      const res: any = await lotApi.fetchLotList({ current: 1, size: 100 })
      // 检查响应格式，适配不同的 API 响应结构
      if (res && (res.data || res.records)) {
        if (res.data && res.data.records) {
          // 标准格式: { data: { records: [...] } }
          parkingLotOptions.value = res.data.records || []
        } else if (res.records) {
          // 简化格式: { records: [...] }
          parkingLotOptions.value = res.records || []
        } else {
          // 直接返回数组
          parkingLotOptions.value = Array.isArray(res.data) ? res.data : []
        }
      } else {
        // 如果 API 调用失败，使用硬编码的停车场数据作为备份
        parkingLotOptions.value = [
          { id: 1, name: '泰州学院南门地下车库' },
          { id: 2, name: '泰州学院行政楼地面车场' },
          { id: 3, name: '泰州万达广场地下停车场' },
          { id: 4, name: '凤城河老街游客车场' },
          { id: 5, name: '医药城国际会议中心车场' }
        ]
      }
    } catch (error) {
      // 捕获错误时使用硬编码数据
      parkingLotOptions.value = [
        { id: 1, name: '泰州学院南门地下车库' },
        { id: 2, name: '泰州学院行政楼地面车场' },
        { id: 3, name: '泰州万达广场地下停车场' },
        { id: 4, name: '凤城河老街游客车场' },
        { id: 5, name: '医药城国际会议中心车场' }
      ]
    }
  }

  const handleAdd = () => {
    dialogType.value = 'add'
    formData.value = {
      strategyName: '',
      description: '',
      parkingLotId: 0,
      isStackable: 0,
      status: 1
    }
    dialogVisible.value = true
  }

  onMounted(async () => {
    await fetchParkingLots()
    await refreshData()
  })
</script>

<style scoped>
  .strategy-page {
    padding: 20px;
  }
</style>
