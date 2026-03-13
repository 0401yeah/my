<template>
  <div class="partner-page art-full-height">
    <ElCard class="art-table-card" shadow="never">
      <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="showDialog('add')" v-ripple>新增单位</ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <ElTag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </ElTag>
        </template>
        <template #action="{ row }">
          <div class="flex">
            <ArtButtonTable type="edit" @click="showDialog('edit', row)" />
            <ArtButtonTable type="delete" @click="deleteOrg(row)" />
          </div>
        </template>
      </ArtTable>

      <ElDialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
        <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
          <ElFormItem label="机构编码" prop="code">
            <ElInput v-model="formData.code" placeholder="请输入机构编码" />
          </ElFormItem>
          <ElFormItem label="机构名称" prop="name">
            <ElInput v-model="formData.name" placeholder="请输入机构名称" />
          </ElFormItem>
          <ElFormItem label="机构全称" prop="fullName">
            <ElInput v-model="formData.fullName" placeholder="请输入机构全称" />
          </ElFormItem>
          <ElFormItem label="负责人" prop="director">
            <ElInput v-model="formData.director" placeholder="请输入负责人" />
          </ElFormItem>
          <ElFormItem label="联系电话" prop="phone">
            <ElInput v-model="formData.phone" placeholder="请输入联系电话" />
          </ElFormItem>
          <ElFormItem label="邮箱" prop="email">
            <ElInput v-model="formData.email" placeholder="请输入邮箱" />
          </ElFormItem>
          <ElFormItem label="地址" prop="address">
            <ElInput v-model="formData.address" placeholder="请输入地址" />
          </ElFormItem>
          <ElFormItem label="排序号" prop="orderNum">
            <ElInputNumber v-model="formData.orderNum" :min="0" style="width: 100%" />
          </ElFormItem>
          <ElFormItem label="状态" prop="status">
            <ElSelect v-model="formData.status" placeholder="请选择状态" style="width: 100%">
              <ElOption label="启用" :value="1" />
              <ElOption label="禁用" :value="0" />
            </ElSelect>
          </ElFormItem>
        </ElForm>
        <template #footer>
          <ElButton @click="dialogVisible = false">取消</ElButton>
          <ElButton type="primary" @click="handleSubmit">确定</ElButton>
        </template>
      </ElDialog>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { sysOrgApi } from '@/api/system'
  import { DialogType } from '@/types'

  const { h } = await import('vue')

  defineOptions({ name: 'PartnerPage' })

  const dialogVisible = ref(false)
  const dialogType = ref<DialogType>('add')
  const formRef = ref()
  const currentOrgData = ref<any>({})

  const dialogTitle = computed(() => {
    switch (dialogType.value) {
      case 'add':
        return '新增机构'
      case 'edit':
        return '编辑机构'
      case 'view':
        return '查看机构'
      default:
        return '机构信息'
    }
  })

  const formData = reactive({
    orgId: undefined as number | undefined,
    parentId: 0,
    code: '',
    name: '',
    fullName: '',
    director: '',
    phone: '',
    email: '',
    address: '',
    orderNum: 0,
    status: 1
  })

  const rules = reactive({
    code: [{ required: true, message: '请输入机构编码', trigger: 'blur' }],
    name: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
    fullName: [{ required: true, message: '请输入机构全称', trigger: 'blur' }]
  })

  const {
    columns,
    columnChecks,
    data,
    loading,
    pagination,
    refreshData,
    handleSizeChange,
    handleCurrentChange
  } = useTable({
    core: {
      apiFn: sysOrgApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        name: undefined,
        code: undefined,
        status: undefined
      },
      columnsFactory: () => [
        { type: 'globalIndex', width: 60, label: '序号' },
        {
          prop: 'code',
          label: '机构编码',
          width: 120
        },
        {
          prop: 'name',
          label: '机构名称',
          width: 150
        },
        {
          prop: 'fullName',
          label: '机构全称'
        },
        {
          prop: 'director',
          label: '负责人',
          width: 100
        },
        {
          prop: 'phone',
          label: '联系电话',
          width: 130
        },
        {
          prop: 'email',
          label: '邮箱',
          width: 180
        },
        {
          prop: 'address',
          label: '地址',
          width: 200
        },
        {
          prop: 'orderNum',
          label: '排序',
          width: 80
        },
        {
          prop: 'status',
          label: '状态',
          width: 80,
          useSlot: true
        },
        {
          prop: 'gmtCreate',
          label: '创建时间',
          width: 160,
          sortable: true
        },
        {
          prop: 'action',
          label: '操作',
          width: 100,
          fixed: 'right',
          useSlot: true
        }
      ]
    }
  })

  const showDialog = (type: DialogType, row?: any): void => {
    dialogType.value = type
    if (row) {
      Object.assign(formData, {
        orgId: row.orgId,
        parentId: row.parentId || 0,
        code: row.code,
        name: row.name,
        fullName: row.fullName,
        director: row.director,
        phone: row.phone,
        email: row.email,
        address: row.address,
        orderNum: row.orderNum,
        status: row.status
      })
    } else {
      Object.assign(formData, {
        orgId: undefined,
        parentId: 0,
        code: '',
        name: '',
        fullName: '',
        director: '',
        phone: '',
        email: '',
        address: '',
        orderNum: 0,
        status: 1
      })
    }
    nextTick(() => {
      dialogVisible.value = true
    })
  }

  const deleteOrg = (row: any): void => {
    ElMessageBox.confirm(`确定要删除机构【${row.name}】吗？`, '删除机构', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await sysOrgApi.delete(row.orgId)
        ElMessage.success('删除成功')
        refreshData()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
  }

  const handleSubmit = () => {
    formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        try {
          const apiFn = formData.orgId ? sysOrgApi.update : sysOrgApi.add
          await apiFn(formData)
          ElMessage.success(formData.orgId ? '修改成功' : '新增成功')
          dialogVisible.value = false
          refreshData()
        } catch (error) {
          ElMessage.error(formData.orgId ? '修改失败' : '新增失败')
        }
      }
    })
  }
</script>

<style scoped>
  .partner-page {
    padding: 20px;
  }
</style>
