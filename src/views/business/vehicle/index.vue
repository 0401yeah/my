<template>
  <div class="vehicle-page flex flex-col gap-4 pb-5">
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
            <ElButton type="primary" @click="handleAddVehicle" v-ripple>
              <ElIcon>
                <Plus />
              </ElIcon>
              新增车辆
            </ElButton>
            <ArtExcelExport
              :data="data as any"
              :columns="exportColumns as any"
              filename="车辆数据"
              :auto-index="true"
              button-text="导出"
              @export-success="handleExportSuccess"
            />
          </ElSpace>
        </template>
      </ArtTableHeader>

      <!-- 车辆对话框 -->
      <ElDialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <ElForm v-model="formData" label-position="top" :rules="rules" ref="formRef">
          <ElRow :gutter="20">
            <ElCol :span="24">
              <ElFormItem label="车牌号" prop="plateNumber">
                <ElInput v-model="formData.plateNumber" placeholder="请输入车牌号" />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="车主姓名" prop="ownerName">
                <ElInput v-model="formData.ownerName" placeholder="请输入车主姓名" />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="手机号" prop="mobile">
                <ElInput v-model="formData.mobile" placeholder="请输入手机号" />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="车辆类型" prop="type">
                <ElSelect v-model="formData.type" placeholder="请选择车辆类型">
                  <ElOption label="临时车" value="0" />
                  <ElOption label="月租车" value="1" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="状态" prop="status">
                <ElSelect v-model="formData.status" placeholder="请选择状态">
                  <ElOption label="正常" value="1" />
                  <ElOption label="禁用" value="0" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="24">
              <ElFormItem label="月租到期时间" prop="validityTime">
                <ElDatePicker
                  v-model="formData.validityTime"
                  type="datetime"
                  placeholder="请选择月租到期时间"
                  style="width: 100%"
                />
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElForm>
        <template #footer>
          <span class="dialog-footer">
            <ElButton @click="dialogVisible = false">取消</ElButton>
            <ElButton type="primary" @click="handleSubmit">确定</ElButton>
          </span>
        </template>
      </ElDialog>

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
        <!-- 车辆信息列 -->
        <template #vehicleInfo="{ row }">
          <div class="flex gap-3 items-center">
            <div class="flex-1 min-w-0">
              <div class="flex items-center">
                <p class="m-0 overflow-hidden font-medium text-ellipsis whitespace-nowrap">{{
                  row.plateNumber || '无车牌号'
                }}</p>
              </div>
            </div>
          </div>
        </template>

        <!-- 车主信息列 -->
        <template #ownerInfo="{ row }">
          <div class="flex gap-3">
            <div class="flex-1 min-w-0">
              <p class="m-0 overflow-hidden font-medium text-ellipsis whitespace-nowrap">{{
                row.ownerName || '无车主姓名'
              }}</p>
              <p
                class="m-0 mt-1 overflow-hidden text-xs text-g-700 text-ellipsis whitespace-nowrap"
                >{{ row.mobile || '无手机号' }}</p
              >
            </div>
          </div>
        </template>

        <!-- 状态列 -->
        <template #status="{ row }">
          <div class="flex items-center gap-2">
            <ElTag v-if="row.status === 1" type="success" size="small">正常</ElTag>
            <ElTag v-else type="danger" size="small">禁用</ElTag>
            <ElButton
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleStatusToggle(row, 0)"
            >
              禁用
            </ElButton>
            <ElButton
              v-else
              type="primary"
              size="small"
              @click="handleStatusToggle(row, 1)"
            >
              启用
            </ElButton>
          </div>
        </template>

        <!-- 操作列 -->
        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>
      </ArtTable>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import { Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
  import {
    ElMessageBox,
    ElMessage,
    ElIcon,
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElRow,
    ElCol,
    ElDatePicker,
    ElTag,
    ElButton
  } from 'element-plus'
  import { useTable } from '@/hooks/core/useTable'
  import ArtExcelExport from '@/components/core/forms/art-excel-export/index.vue'
  import { vehicleApi } from '@/api/business'

  defineOptions({ name: 'Vehicle' })

  // 选中的行
  const selectedRows = ref<any[]>([])

  // 表格实例引用
  const tableRef = ref()
  const searchBarRef = ref()

  // 对话框状态
  const dialogVisible = ref(false)
  const formRef = ref()

  // 对话框标题
  const dialogTitle = computed(() => {
    return formData.value.id ? '编辑车辆' : '新增车辆'
  })

  // 表单数据
  const formData = ref({
    id: '',
    plateNumber: '',
    ownerName: '',
    mobile: '',
    type: '0',
    status: '1',
    validityTime: ''
  })

  // 表单验证规则
  const rules = ref({
    plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
    ownerName: [{ required: true, message: '请输入车主姓名', trigger: 'blur' }],
    mobile: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    type: [{ required: true, message: '请选择车辆类型', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  })

  // 搜索表单状态
  const searchFormState = ref({
    plateNumber: '',
    ownerName: '',
    mobile: '',
    type: '',
    status: ''
  })

  // 搜索表单配置
  const searchItems = computed(() => [
    {
      key: 'plateNumber',
      label: '车牌号',
      type: 'input',
      props: {
        placeholder: '请输入车牌号（模糊查询）'
      }
    },
    {
      key: 'ownerName',
      label: '车主姓名',
      type: 'input',
      props: {
        placeholder: '请输入车主姓名（模糊查询）'
      }
    },
    {
      key: 'mobile',
      label: '手机号',
      type: 'input',
      props: {
        placeholder: '请输入手机号（模糊查询）',
        maxlength: '11'
      }
    },
    {
      key: 'type',
      label: '车辆类型',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '临时车', value: '0' },
        { label: '月租车', value: '1' }
      ]
    },
    {
      key: 'status',
      label: '状态',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '正常', value: '1' },
        { label: '禁用', value: '0' }
      ]
    }
  ])

  // 车辆类型映射
  const vehicleTypeMap: Record<number, string> = {
    0: '临时车',
    1: '月租车'
  }

  // 获取车辆类型文本
  const getVehicleTypeText = (type: number) => {
    return vehicleTypeMap[type] || '未知'
  }

  // 导出列配置
  const exportColumns = computed(() => ({
    plateNumber: { title: '车牌号', width: 15 },
    type: {
      title: '车辆类型',
      width: 15,
      formatter: (value: number) => getVehicleTypeText(value)
    },
    ownerName: { title: '车主姓名', width: 15 },
    mobile: { title: '手机号', width: 15 },
    gmtCreate: { title: '创建时间', width: 20 }
  }))

  // 获取车辆列表
  const fetchVehicleList = async (params: any) => {
    try {
      const response = await vehicleApi.fetchVehicleList(params)
      return response
    } catch (error) {
      throw error
    }
  }

  // 使用 useTable Hook 管理表格数据
  const {
    data, // 表格数据
    loading, // 加载中状态
    error, // 数据加载错误状态
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
      apiFn: fetchVehicleList,
      apiParams: {
        current: 1,
        size: 20,
        ...searchFormState.value
      },
      columnsFactory: () => [
        { type: 'selection', width: 40 },
        { type: 'globalIndex', width: 60, label: '序号' },
        {
          prop: 'vehicleInfo',
          label: '车辆信息',
          minWidth: 80,
          useSlot: true
        },
        {
          prop: 'ownerInfo',
          label: '车主信息',
          minWidth: 80,
          useSlot: true
        },
        {
          prop: 'type',
          label: '车辆类型',
          sortable: true,
          formatter: (row: any) => getVehicleTypeText(row.type)
        },
        {
          prop: 'status',
          label: '状态',
          sortable: true,
          width: 180,
          useSlot: true
        },
        {
          prop: 'gmtCreate',
          label: '创建时间',
          sortable: true,
          formatter: (row: any) => {
            return row.gmtCreate ? new Date(row.gmtCreate).toLocaleString() : '未知'
          }
        },
        {
          prop: 'validityTime',
          label: '月租到期时间',
          sortable: true,
          formatter: (row: any) => {
            return row.validityTime ? new Date(row.validityTime).toLocaleString() : '无'
          }
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
        ElMessage.error('数据加载失败，请重试')
      }
    }
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
    ElMessage.success(`导出 ${count} 条数据成功`)
  }

  // CRUD 操作
  const handleEdit = (row: any) => {
    // 填充表单数据
    formData.value = {
      id: row.id,
      plateNumber: row.plateNumber || '',
      ownerName: row.ownerName || '',
      mobile: row.mobile || '',
      type: row.type.toString(),
      status: row.status.toString(),
      validityTime: row.validityTime || ''
    }
    dialogVisible.value = true
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除车辆 ${row.plateNumber} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          await vehicleApi.deleteVehicle(row.id)
          ElMessage.success(`删除车辆: ${row.plateNumber} 成功`)
          refreshData()
        } catch (error) {
          ElMessage.error('删除失败，请重试')
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除')
      })
  }

  // 状态切换
  const handleStatusToggle = async (row: any, status: number) => {
    try {
      await vehicleApi.updateVehicle({
        id: row.id,
        status: status
      })
      ElMessage.success(status === 1 ? '启用成功' : '禁用成功')
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }

  // 新增车辆
  const handleAddVehicle = () => {
    // 重置表单
    formData.value = {
      id: '',
      plateNumber: '',
      ownerName: '',
      mobile: '',
      type: '0',
      status: '1',
      validityTime: ''
    }
    dialogVisible.value = true
  }

  // 提交表单
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()

      // 准备提交数据
      const submitData = {
        ...formData.value,
        type: parseInt(formData.value.type),
        status: parseInt(formData.value.status)
      }

      // 根据是否有id判断是新增还是编辑
      if (submitData.id) {
        // 调用API更新车辆
        await vehicleApi.updateVehicle(submitData)
        ElMessage.success('编辑车辆成功')
      } else {
        // 调用API添加车辆
        await vehicleApi.addVehicle(submitData)
        ElMessage.success('新增车辆成功')
      }

      // 关闭对话框
      dialogVisible.value = false

      // 刷新数据
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }


</script>

<style scoped>
  .vehicle-page {
    padding: 20px;
  }
</style>
