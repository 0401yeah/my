<template>
  <div class="admin-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <div class="card-header flex justify-between items-center mb-4">
        <el-space wrap>
          <el-input
            v-model="searchParams.username"
            placeholder="用户名"
            clearable
            style="width: 180px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchParams.nickname"
            placeholder="昵称"
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
            <el-option label="正常" :value="1" />
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
        row-key="userId"
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
            {{ row.status === 1 ? '正常' : '禁用' }}
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
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="formData.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive, computed } from 'vue'
  import { useTable } from '@/hooks/core/useTable'
  import { sysUserApi } from '@/api/system'
  import { Plus, Search, Refresh } from '@element-plus/icons-vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'

  defineOptions({ name: 'Admin' })

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
      apiFn: sysUserApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        username: undefined,
        nickname: undefined,
        status: undefined
      },
      columnsFactory: () => [
        { type: 'globalIndex', width: 60, label: '序号' },
        {
          prop: 'username',
          label: '用户名',
          minWidth: 120
        },
        {
          prop: 'nickname',
          label: '昵称',
          minWidth: 120
        },
        {
          prop: 'mobile',
          label: '手机号',
          minWidth: 130
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
          minWidth: 200,
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
  const dialogTitle = ref('添加管理员')
  const formRef = ref()
  const submitLoading = ref(false)
  const isEdit = computed(() => !!formData.userId)

  const formData = reactive({
    userId: undefined as number | undefined,
    orgId: 1,
    username: '',
    password: '',
    nickname: '',
    mobile: '',
    status: 1
  })

  const rules = reactive({
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    nickname: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
    ],

    mobile: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择状态', trigger: 'change' }
    ]
  })

  const handleSearch = () => {
    searchData()
  }

  const handleReset = () => {
    resetSearchParams()
  }

  const handleAdd = () => {
    dialogTitle.value = '添加管理员'
    Object.assign(formData, {
      userId: undefined,
      orgId: 1,
      username: '',
      password: '',
      nickname: '',
      mobile: '',
      status: 1
    })
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    dialogTitle.value = '编辑管理员'
    Object.assign(formData, {
      userId: row.userId,
      orgId: row.orgId || 1,
      username: row.username,
      password: '',
      nickname: row.nickname,
      mobile: row.mobile,
      status: row.status
    })
    dialogVisible.value = true
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除管理员【${row.username}】吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      sysUserApi.delete(row.userId).then(() => {
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
        submitLoading.value = true
        const submitData: any = {
          orgId: formData.orgId,
          username: formData.username,
          nickname: formData.nickname,
          mobile: formData.mobile,
          status: formData.status
        }

        if (formData.userId) {
          submitData.userId = formData.userId
          sysUserApi.update(submitData).then(() => {
            ElMessage.success('编辑成功')
            dialogVisible.value = false
            refreshData()
          }).catch(() => {
            ElMessage.error('编辑失败')
          }).finally(() => {
            submitLoading.value = false
          })
        } else {
          submitData.password = formData.password
          sysUserApi.add(submitData).then(() => {
            ElMessage.success('添加成功')
            dialogVisible.value = false
            refreshData()
          }).catch(() => {
            ElMessage.error('添加失败，请检查用户名是否已存在')
          }).finally(() => {
            submitLoading.value = false
          })
        }
      }
    })
  }
</script>

<style scoped>
  .admin-page {
    padding: 20px;
  }
</style>
