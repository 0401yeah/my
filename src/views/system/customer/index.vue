<template>
  <div class="customer-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="refreshData"
      >
        <template #left>
          <el-space wrap>
            <el-input
              v-model="searchParams.mobile"
              placeholder="手机号"
              clearable
              style="width: 150px"
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
        </template>
      </ArtTableHeader>

      <art-table
        row-key="id"
        :show-table-header="false"
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        :width="'100%'"
        :fit="true"
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

    <!-- 编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      custom-class="customer-dialog"
      :show-close="false"
      destroy-on-close
    >
      <!-- 自定义头部 -->
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="dialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <User class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">编辑车主信息</h2>
              <p class="header-subtitle">修改车主账号信息</p>
            </div>
          </div>
        </div>
      </template>

      <!-- 表单内容区 -->
      <div class="form-body">
        <div class="form-grid">
          <div class="form-item full-width">
            <label class="form-label">手机号 <span class="required">*</span></label>
            <el-input 
              v-model="formData.mobile"
              placeholder="请输入手机号"
              class="form-input"
            />
          </div>
          
          <div class="form-item full-width">
            <label class="form-label">昵称 <span class="required">*</span></label>
            <el-input 
              v-model="formData.nickname"
              placeholder="请输入昵称"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <label class="form-label">状态 <span class="required">*</span></label>
            <el-select 
              v-model="formData.status"
              placeholder="请选择状态"
              class="form-input"
            >
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </div>

          <div class="form-item">
            <label class="form-label">注册时间</label>
            <el-input 
              :model-value="formData.gmtCreate ? new Date(formData.gmtCreate).toLocaleString() : '-'"
              disabled
              class="form-input"
              size="small"
            />
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading" class="submit-btn">
            保存修改
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, reactive } from 'vue'
  import { useTable } from '@/hooks/core/useTable'
  import { customerApi } from '@/api/system'
  import { Search, Refresh } from '@element-plus/icons-vue'
  import { X, User } from 'lucide-vue-next'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'

  defineOptions({ name: 'CustomerPage' })

  const {
    data,
    columns,
    columnChecks,
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
      apiFn: customerApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        mobile: undefined,
        nickname: undefined,
        status: undefined
      },
      // 这里是优化后的列宽配置
      columnsFactory: () => [
        { type: 'globalIndex', width: 80, label: '序号' },
        {
          prop: 'mobile',
          label: '手机号',
          minWidth: 120
        },
        {
          prop: 'nickname',
          label: '昵称',
          minWidth: 150
        },
        {
          prop: 'status',
          label: '状态',
          width: 100,
          useSlot: true
        },
        {
          prop: 'gmtCreate',
          label: '注册时间',
          minWidth: 180,
          sortable: true
        },
        {
          prop: 'operation',
          label: '操作',
          width: 150,
          fixed: 'right',
          useSlot: true
        }
      ]
    }
  })

  const dialogVisible = ref(false)
  const formRef = ref()
  const submitLoading = ref(false)

  const formData = reactive({
    id: undefined as number | undefined,
    mobile: '',
    nickname: '',
    status: 1,
    gmtCreate: null as string | null
  })

  const rules = reactive({
    mobile: [
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    nickname: [
      { required: true, message: '请输入昵称', trigger: 'blur' }
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

  const handleEdit = (row: any) => {
    Object.assign(formData, {
      id: row.id,
      mobile: row.mobile,
      nickname: row.nickname,
      status: row.status,
      gmtCreate: row.gmtCreate || null
    })
    dialogVisible.value = true
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除车主【${row.nickname || row.mobile}】吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      customerApi.delete(row.id).then(() => {
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
        customerApi.update(formData).then(() => {
          ElMessage.success('编辑成功')
          dialogVisible.value = false
          refreshData()
        }).catch(() => {
          ElMessage.error('编辑失败')
        }).finally(() => {
          submitLoading.value = false
        })
      }
    })
  }
</script>

<style scoped>
  .customer-page {
    padding: 20px;
  }

  /* 弹窗样式 */
  :deep(.customer-dialog) {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.customer-dialog .el-dialog__header) {
    padding: 0;
  }
  
  :deep(.customer-dialog .el-dialog__body) {
    padding: 16px 20px;
  }
  
  :deep(.customer-dialog .el-dialog__footer) {
    padding: 0 20px 16px;
  }

  /* 对话框头部 */
  .dialog-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background-color: #ffffff;
    border-bottom: 1px solid #f1f5f9;
    position: relative;
  }
  
  .close-btn {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    background-color: #f1f5f9;
    color: #64748b;
    padding: 6px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .close-btn:hover {
    background-color: #e2e8f0;
    color: #475569;
  }
  
  .header-content {
    display: flex;
    align-items: center;
    gap: 12px;
    padding-right: 40px;
  }
  
  .header-text {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
  
  .header-title {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    margin: 0;
  }
  
  .header-subtitle {
    font-size: 12px;
    color: #94a3b8;
    margin: 0;
  }

  /* 表单内容 */
  .form-body {
    padding: 0;
  }
  
  .form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .form-item {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }
  
  .form-item.full-width {
    grid-column: span 2;
  }
  
  .form-label {
    font-size: 13px;
    font-weight: 500;
    color: #475569;
  }
  
  .required {
    color: #ef4444;
    font-size: 13px;
  }
  
  .form-input {
    border-radius: 8px;
  }
  
  :deep(.form-input .el-input__wrapper) {
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    border-radius: 8px;
    padding: 6px 12px;
  }
  
  :deep(.form-input .el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px #6366f1 inset;
  }
  
  :deep(.form-input .el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 2px #6366f1 inset;
  }
  
  :deep(.form-input .el-select__wrapper) {
    box-shadow: 0 0 0 1px #e2e8f0 inset;
    border-radius: 8px;
    padding: 6px 12px;
  }
  
  :deep(.form-input .el-select__wrapper:hover) {
    box-shadow: 0 0 0 1px #6366f1 inset;
  }
  
  :deep(.form-input .el-select__wrapper.is-focus) {
    box-shadow: 0 0 0 2px #6366f1 inset;
  }

  /* 对话框底部 */
  .dialog-footer {
    display: flex;
    gap: 12px;
  }
  
  .cancel-btn {
    flex: 1;
    padding: 8px 16px;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    background-color: #ffffff;
    color: #64748b;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s ease;
  }
  
  .cancel-btn:hover {
    background-color: #f8fafc;
    border-color: #cbd5e1;
  }
  
  .submit-btn {
    flex: 1;
    padding: 8px 16px;
    border-radius: 8px;
    border: none;
    background-color: #6366f1;
    color: #ffffff;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s ease;
  }
  
  .submit-btn:hover {
    background-color: #4f46e5;
  }
</style>