<template>
  <div class="partner-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <div class="card-header flex justify-between items-center mb-4">
        <el-space wrap>
          <el-input
            v-model="searchParams.name"
            placeholder="机构名称"
            clearable
            style="width: 200px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchParams.code"
            placeholder="机构编码"
            clearable
            style="width: 150px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-select
            v-model="searchParams.status"
            placeholder="状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-space>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加
        </el-button>
      </div>
      <art-table
        row-key="orgId"
        :show-table-header="false"
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="机构编码" prop="code">
              <el-input v-model="formData.code" placeholder="请输入机构编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入机构名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="机构全称" prop="fullName">
          <el-input v-model="formData.fullName" placeholder="请输入机构全称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="director">
              <el-input v-model="formData.director" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
  import { ref, reactive } from 'vue'
  import { useTable } from '@/hooks/core/useTable'
  import { sysOrgApi } from '@/api/system'
  import { Plus, Search, Refresh } from '@element-plus/icons-vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'

  defineOptions({ name: 'PartnerPage' })

  const {
    data,
    columns,
    loading,
    pagination,
    handleSizeChange,
    handleCurrentChange,
    searchParams,
    resetSearchParams,
    getData: searchData,
    refreshData
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
          label: '机构全称',
          minWidth: 200
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
          prop: 'operation',
          label: '操作',
          width: 120,
          fixed: 'right',
          useSlot: true
        }
      ]
    }
  })

  const dialogVisible = ref(false)
  const dialogTitle = ref('添加机构')
  const formRef = ref()
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
    code: [
      { required: true, message: '请输入机构编码', trigger: 'blur' },
      { min: 2, max: 50, message: '机构编码长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    name: [
      { required: true, message: '请输入机构名称', trigger: 'blur' },
      { min: 2, max: 50, message: '机构名称长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    fullName: [
      { required: true, message: '请输入机构全称', trigger: 'blur' },
      { min: 2, max: 100, message: '机构全称长度在 2 到 100 个字符', trigger: 'blur' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
    ],
    email: [
      { type: 'email' as const, message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
  })

  const handleSearch = () => {
    searchData()
  }

  const handleReset = () => {
    resetSearchParams()
  }

  const handleAdd = () => {
    dialogTitle.value = '添加机构'
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
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    dialogTitle.value = '编辑机构'
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
    dialogVisible.value = true
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除机构【${row.name}】吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      sysOrgApi.delete(row.orgId).then(() => {
        ElMessage.success('删除成功')
        refreshData()
      }).catch(() => {
        ElMessage.error('删除失败')
      })
    }).catch(() => {})
  }

  const handleSubmit = () => {
    formRef.value.validate((valid: boolean) => {
      if (valid) {
        const apiFn = formData.orgId ? sysOrgApi.update : sysOrgApi.add
        apiFn(formData).then(() => {
          ElMessage.success(formData.orgId ? '编辑成功' : '添加成功')
          dialogVisible.value = false
          refreshData()
        }).catch(() => {
          ElMessage.error(formData.orgId ? '编辑失败' : '添加失败')
        })
      }
    })
  }
</script>

<style scoped>
  .partner-page {
    padding: 20px;
  }
</style>
