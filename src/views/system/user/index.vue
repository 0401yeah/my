<template>
  <div class="user-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <div class="card-header flex justify-between items-center mb-4">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" width="60" label="序号" />
        <el-table-column prop="nickname" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="mobile" label="手机号" width="150" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === '1' ? '男' : row.gender === '2' ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gmtCreate" label="创建时间" width="180" />
        <el-table-column prop="gmtModified" label="修改时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="formData.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="1" />
            <el-option label="女" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Plus } from '@element-plus/icons-vue'
  import request from '@/utils/http'

  defineOptions({ name: 'User' })

  interface UserItem {
    userId: number
    username: string
    nickname: string
    email: string
    mobile: string
    gender: string
    status: number
    gmtCreate: string
    gmtModified: string
  }

  const loading = ref(false)
  const tableData = ref<UserItem[]>([])
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const formRef = ref()

  const pagination = ref({
    current: 1,
    size: 20,
    total: 0
  })

  const formData = ref({
    userId: undefined as number | undefined,
    username: '',
    nickname: '',
    email: '',
    mobile: '',
    gender: '1',
    status: 1,
    orgId: 1
  })

  const dialogTitle = computed(() => (dialogType.value === 'add' ? '新增用户' : '编辑用户'))

  const formRules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
  }

  const fetchData = async () => {
    loading.value = true
    try {
      const res = await request.get({
        url: '/api/sys-user/list',
        params: {
          current: pagination.value.current,
          size: pagination.value.size
        }
      })
      tableData.value = res?.records || []
      pagination.value.total = res?.total || 0
    } catch (error) {
      console.error('获取用户列表失败', error)
      ElMessage.error('获取用户列表失败')
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

  const handleAdd = () => {
    dialogType.value = 'add'
    formData.value = {
      userId: undefined,
      username: '',
      nickname: '',
      email: '',
      mobile: '',
      gender: '1',
      status: 1,
      orgId: 1
    }
    dialogVisible.value = true
  }

  const handleEdit = (row: UserItem) => {
    dialogType.value = 'edit'
    formData.value = {
      userId: row.userId,
      username: row.username,
      nickname: row.nickname,
      email: row.email,
      mobile: row.mobile,
      gender: row.gender,
      status: row.status,
      orgId: 1
    }
    dialogVisible.value = true
  }

  const handleDelete = async (row: UserItem) => {
    try {
      await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await request.del({ url: `/api/sys-user/${row.userId}` })
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }

  const handleSubmit = async () => {
    await formRef.value.validate()
    try {
      if (dialogType.value === 'add') {
        await request.post({ url: '/api/sys-user', params: formData.value })
        ElMessage.success('添加成功')
      } else {
        await request.put({ url: '/api/sys-user', params: formData.value })
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  onMounted(() => {
    fetchData()
  })
</script>

<style scoped>
  .user-page {
    padding: 20px;
  }
</style>
