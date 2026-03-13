<template>
  <div class="family-page">
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
      label-width="90px"
    />

    <ElCard class="flex-1 art-table-card mt-4" shadow="never">
      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="handleAddFamily" v-ripple class="create-button">
              <ArtSvgIcon icon="ri:add-fill" />
              新增家庭组
            </ElButton>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <div class="table-container">
        <ArtTable
          ref="tableRef"
          :loading="loading"
          :data="data"
          :columns="columns"
          :pagination="pagination"
          @selection-change="handleSelectionChange"
          @row-click="handleRowClick"
          @sort-change="handleSortChange"
          @pagination:size-change="handleSizeChange"
          @pagination:current-change="handleCurrentChange"
        >
          <template #familyInfo="{ row }">
            <div class="flex gap-3">
              <div class="flex-1 min-w-0">
                <p class="m-0 overflow-hidden font-medium text-ellipsis whitespace-nowrap" :title="row.familyName">
                  {{ row.familyName || `${row.plateNumber}的家庭组` }}
                </p>
              </div>
            </div>
          </template>

          <template #mainAccount="{ row }">
            <div class="flex gap-3 justify-center">
              <div class="flex-1 min-w-0 text-center">
                <p class="m-0 overflow-hidden font-medium text-ellipsis whitespace-nowrap">
                  {{ row.plateNumber || '-' }}
                </p>
              </div>
            </div>
          </template>

          <template #subAccounts="{ row }">
            <template v-if="row.bindType === 1">
              <div class="sub-accounts-container">
                <div 
                  v-for="(sub, index) in row.subAccounts || []" 
                  :key="index"
                  class="sub-account-card"
                >
                  <div class="sub-account-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                      <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                  </div>
                  <span class="sub-account-text">{{ sub.plateNumber || '-' }}</span>
                </div>
                <div v-if="(row.subAccounts?.length || 0) < 2" class="sub-account-empty">
                  <div class="empty-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <line x1="12" y1="5" x2="12" y2="19"></line>
                      <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                  </div>
                  <span class="empty-text">空位</span>
                </div>
              </div>
            </template>
            <template v-else-if="row.bindType === 2">
              <div class="main-account-badge">
                <div class="badge-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </svg>
                </div>
                <span class="badge-text">{{ row.mainAccount?.plateNumber || '-' }}</span>
                <span class="badge-label">主账号</span>
              </div>
            </template>
            <template v-else>
              <div class="no-binding">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"></circle>
                  <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line>
                </svg>
                <span>无绑定</span>
              </div>
            </template>
          </template>

          <template #status="{ row }">
            <div class="flex items-center gap-2 justify-center" style="min-width: 180px;">
              <ElTag :type="getStatusType(row)" size="small">
                {{ getStatusText(row) }}
              </ElTag>
              <el-button 
                :type="row.status === 1 ? 'danger' : 'success'" 
                size="small" 
                @click="handleStatusChange(row)"
                :loading="statusLoading === row.id"
                :disabled="statusLoading === row.id"
                style="padding: 0 8px; font-size: 12px;"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </div>
          </template>

          <template #operation="{ row }">
            <div class="flex items-center">
              <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" :disabled="deleteLoading === row.id" />
              <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" :loading="deleteLoading === row.id" :disabled="deleteLoading === row.id" />
            </div>
          </template>
        </ArtTable>
      </div>
    </ElCard>

    <el-dialog 
      v-model="dialogVisible" 
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      custom-class="family-dialog"
      :show-close="false"
    >
      <template #header>
        <div class="dialog-header">
          <button class="close-btn" @click="dialogVisible = false">
            <X class="w-4 h-4" />
          </button>
          <div class="header-content">
            <User class="w-6 h-6 text-indigo-500" />
            <div class="header-text">
              <h2 class="header-title">{{ dialogType === 'add' ? '创建家庭组' : '编辑家庭组' }}</h2>
              <p class="header-subtitle">{{ dialogType === 'add' ? '邀请家人一起共享权益' : '修改家庭组信息' }}</p>
            </div>
          </div>
        </div>
      </template>

      <div class="form-body">
        <div class="form-grid">
          <div class="form-item">
            <label class="form-label">家庭组名称</label>
            <el-input 
              v-model="formData.familyName"
              placeholder="请输入家庭组名称"
              :disabled="formLoading"
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <label class="form-label">主账号 (管理者) <span class="required">*</span></label>
            <el-input 
              v-model="formData.plateNumber"
              @input="formData.plateNumber = formData.plateNumber.toUpperCase()"
              placeholder="输入车牌号"
              :disabled="formLoading"
              class="form-input"
            />
          </div>

          <div class="form-item full-width">
            <div class="sub-account-header">
              <label class="form-label">关联副账号</label>
              <span class="account-count">{{ formData.subAccounts.length }}/2</span>
            </div>
            
            <div v-for="(account, index) in formData.subAccounts" :key="index" class="sub-account-item">
              <el-input 
                v-model="formData.subAccounts[index]"
                @input="formData.subAccounts[index] = formData.subAccounts[index].toUpperCase()"
                :placeholder="`输入副账号 ${Number(index) + 1} 车牌`"
                :disabled="formLoading"
                class="form-input sub-account-input"
              />
              <button 
                @click="removeSubAccount(index as number)"
                class="delete-btn"
                :disabled="formLoading"
              >
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
             
            <button 
              v-if="formData.subAccounts.length < 2"
              @click="addSubAccount"
              class="add-sub-btn"
              :disabled="formLoading"
            >
              + 添加副账号
            </button>
          </div>

          <div class="form-item full-width">
            <label class="form-label">到期时间</label>
            <el-date-picker
              v-model="formData.expireTime"
              type="datetime"
              placeholder="请选择到期时间"
              style="width: 100%"
              :disabled="formLoading"
              class="form-input"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" :disabled="formLoading" class="cancel-btn">
            取消
          </el-button>
          <el-button type="primary" @click="handleSubmit" :loading="formLoading" :disabled="formLoading" class="submit-btn">
            {{ dialogType === 'add' ? '创建完成' : '保存修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue'
  import { ElMessageBox, ElMessage, ElTag } from 'element-plus'
  import { X, User, Trash2 } from 'lucide-vue-next'
  import ArtSvgIcon from '@/components/core/base/art-svg-icon/index.vue'
  import { useTable } from '@/hooks/core/useTable'
  import { familyApi } from '@/api/business'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtTable from '@/components/core/tables/art-table/index.vue'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'

  defineOptions({ name: 'Family' })

  const selectedRows = ref<any[]>([])
  const tableRef = ref()
  const searchBarRef = ref()
  
  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'view' | 'edit'>('view')
  const formLoading = ref(false)
  const formData = ref<any>({
    id: undefined,
    familyName: '',
    plateNumber: '',
    subAccounts: [''],
    status: 1,
    expireTime: null
  })

  watch(() => formData.value.plateNumber, (newPlateNumber) => {
    if (newPlateNumber && !formData.value.familyName) {
      formData.value.familyName = `${newPlateNumber}的家庭组`
    }
  })

  const addSubAccount = () => {
    if (formData.value.subAccounts.length < 2) {
      formData.value.subAccounts.push('')
    }
  }

  const removeSubAccount = (index: number) => {
    if (formData.value.subAccounts.length > 1) {
      formData.value.subAccounts.splice(index, 1)
    } else {
      formData.value.subAccounts[0] = ''
    }
  }

  const searchFormState = ref({
    plateNumber: '',
    status: '',
    expireTime: []
  })

  const searchItems = computed(() => [
    {
      key: 'plateNumber',
      label: '绑定车牌',
      type: 'input',
      props: { placeholder: '请输入车牌号' }
    },
    {
      key: 'status',
      label: '状态',
      type: 'select',
      options: [
        { label: '全部', value: '' },
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 }
      ]
    },
    {
      key: 'expireTime',
      label: '到期时间',
      type: 'daterange',
      props: {
        type: 'datetimerange',
        rangeSeparator: '至',
        startPlaceholder: '开始日期',
        endPlaceholder: '结束日期',
        clearable: true
      }
    }
  ])

  const getStatusType = (row: any) => {
    if (row.status === 0) return 'danger'
    if (row.expireTime && new Date(row.expireTime).getTime() < new Date().getTime()) return 'warning'
    return 'success'
  }

  const getStatusText = (row: any) => {
    if (row.status === 0) return '已禁用'
    if (row.expireTime && new Date(row.expireTime).getTime() < new Date().getTime()) return '已过期'
    return '启用中'
  }

  const {
    data, loading, pagination, handleSizeChange, handleCurrentChange,
    searchParams, resetSearchParams, getData, refreshData, columns, columnChecks
  } = useTable({
    core: {
      apiFn: familyApi.fetchFamilyList,
      apiParams: {
        current: 1,
        size: 20,
        ...searchFormState.value
      },
      columnsFactory: () => [
        { type: 'selection', width: 50, align: 'center' },
        { type: 'globalIndex', width: 60, label: '序号', align: 'center' },
        { prop: 'familyInfo', label: '组名称', minWidth: 120, useSlot: true },
        { prop: 'mainAccount', label: '主账号', minWidth: 150, useSlot: true, align: 'center' },
        { prop: 'subAccounts', label: '副账号', minWidth: 120, useSlot: true, align: 'center' },
        { prop: 'status', label: '状态', width: 180, sortable: true, useSlot: true, align: 'center' },
        { prop: 'gmtCreate', label: '创建时间', minWidth: 160, sortable: true, align: 'center' },
        {
          prop: 'expireTime',
          label: '到期时间',
          minWidth: 160,
          sortable: true,
          align: 'center',
          formatter: (row: any) => row.expireTime || '永久有效'
        },
        { prop: 'operation', label: '操作', width: 160, useSlot: true, fixed: 'right', align: 'left' }
      ]
    }
  })

  const handleSelectionChange = (selection: any[]) => selectedRows.value = selection
  const handleRowClick = (row: any) => {}
  const handleSortChange = (sortInfo: any) => {}
  
  const handleSearch = async () => {
    Object.assign(searchParams, {
      plateNumber: searchFormState.value.plateNumber,
      status: searchFormState.value.status === '' ? undefined : searchFormState.value.status,
      current: 1
    })
    
    if (searchFormState.value.expireTime && searchFormState.value.expireTime.length === 2) {
      searchParams.expire_time_start = searchFormState.value.expireTime[0]
      searchParams.expire_time_end = searchFormState.value.expireTime[1]
    } else {
      delete searchParams.expire_time_start
      delete searchParams.expire_time_end
    }
    
    getData()
  }
  
  const handleReset = () => {
    resetSearchParams()
    searchFormState.value = { plateNumber: '', status: '', expireTime: [] }
    getData()
  }
  
  const handleRefresh = () => refreshData()
  const handleAddFamily = () => {
    formData.value = {
      id: undefined,
      familyName: '',
      plateNumber: '',
      subAccounts: [''],
      status: 1,
      expireTime: null
    }
    dialogType.value = 'add'
    dialogVisible.value = true
  }
  
  const handleEdit = (row: any) => {
    const subAccounts = row.subAccounts || []
    const subAccountPlateNumbers = subAccounts.map((sub: any) => sub.plateNumber || '').filter((p: string) => p)
    
    formData.value = {
      id: row.id,
      familyName: row.familyName || '',
      plateNumber: row.plateNumber || '',
      subAccounts: subAccountPlateNumbers.length > 0 ? subAccountPlateNumbers : [''],
      status: row.status || 1,
      expireTime: row.expireTime || null
    }
    dialogType.value = 'edit'
    dialogVisible.value = true
  }
  
  const deleteLoading = ref<number | null>(null)

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要解绑该关系吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        deleteLoading.value = row.id
        await familyApi.deleteFamily(row.id)
        ElMessage.success(`解绑成功`)
        refreshData()
      } catch (error: any) {
        console.error('操作失败:', error)
        ElMessage.error(error.message || '操作失败，请重试')
      } finally {
        deleteLoading.value = null
      }
    }).catch(() => {
      ElMessage.info('已取消')
    })
  }

  const statusLoading = ref<number | null>(null)

  const handleStatusChange = (row: any) => {
    const action = row.status === 1 ? '禁用' : '启用'
    ElMessageBox.confirm(`确定要${action}该亲情组吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        statusLoading.value = row.id
        await familyApi.toggleStatus(row.id)
        ElMessage.success(`${action}成功`)
        refreshData()
      } catch (error: any) {
        console.error('操作失败:', error)
        ElMessage.error(error.message || '操作失败，请重试')
      } finally {
        statusLoading.value = null
      }
    }).catch(() => {
      ElMessage.info('已取消')
    })
  }

  const handleSubmit = async () => {
    try {
      formLoading.value = true

      if (!formData.value.familyName) {
        ElMessage.error('请输入组名称')
        formLoading.value = false
        return
      }
      if (!formData.value.plateNumber) {
        ElMessage.error('请输入主账号车牌')
        formLoading.value = false
        return
      }

      const plateNumber = formData.value.plateNumber.toUpperCase()
      const checkResult = await familyApi.checkVehicle(plateNumber) as any
      if (!checkResult.exists) {
        ElMessage.error('该车牌不存在于车辆档案中，请先添加车辆')
        formLoading.value = false
        return
      }
      if (!checkResult.isMonthly) {
        ElMessage.error('主车必须是月租车，请更换车牌')
        formLoading.value = false
        return
      }

      const expireTime = formData.value.expireTime
      const expireTimeStr = expireTime 
        ? (typeof expireTime === 'string' ? expireTime : new Date(expireTime).toISOString().replace('T', ' ').substring(0, 19))
        : null

      const submitData: any = {
        family_name: formData.value.familyName,
        plate_number: plateNumber,
        bind_type: 1,
        parent_id: null,
        is_auto_pay: 0,
        expire_time: expireTimeStr
      }

      let mainAccountId: number | undefined

      if (dialogType.value === 'add') {
        const result = await familyApi.addFamily(submitData) as any
        mainAccountId = result?.id
        ElMessage.success('添加成功')
      } else if (dialogType.value === 'edit') {
        submitData.id = formData.value.id
        await familyApi.updateFamily(submitData)
        mainAccountId = formData.value.id
        ElMessage.success('更新成功')
      }

      const validSubAccounts = formData.value.subAccounts.filter((sub: string) => sub.trim() !== '')
      if (mainAccountId) {
        const currentData = data.value.find((item: any) => item.id === mainAccountId)
        const existingSubAccounts: any[] = (currentData as any)?.subAccounts || []
        
        const existingPlateNumbers = new Set(existingSubAccounts.map((sub: any) => sub.plateNumber))
        const newPlateNumbers = new Set(validSubAccounts.map((sub: string) => sub.toUpperCase()))
        
        for (const subAccount of existingSubAccounts) {
          if (!newPlateNumbers.has(subAccount.plateNumber)) {
            await familyApi.deleteFamily(subAccount.id)
          }
        }
        
        for (const subPlate of validSubAccounts) {
          const upperPlateNumber = subPlate.toUpperCase()
          
          const subCheckResult = await familyApi.checkVehicle(upperPlateNumber) as any
          if (!subCheckResult.exists) {
            ElMessage.error(`副车牌【${upperPlateNumber}】不存在于车辆档案中`)
            continue
          }
          if (subCheckResult.isMonthly) {
            ElMessage.error(`副车牌【${upperPlateNumber}】是月租车，副车只能是临时车`)
            continue
          }
          
          if (!existingPlateNumbers.has(upperPlateNumber)) {
            await familyApi.addFamily({
              family_name: `${upperPlateNumber}的副账号`,
              plate_number: upperPlateNumber,
              bind_type: 2,
              parent_id: mainAccountId,
              is_auto_pay: 0,
              expire_time: expireTimeStr
            })
          } else {
            const existingSubAccount = existingSubAccounts.find((sub: any) => sub.plateNumber === upperPlateNumber)
            if (existingSubAccount) {
              await familyApi.updateFamily({
                id: existingSubAccount.id,
                family_name: `${upperPlateNumber}的副账号`,
                plate_number: upperPlateNumber,
                bind_type: 2,
                parent_id: mainAccountId,
                is_auto_pay: 0,
                expire_time: expireTimeStr
              })
            }
          }
        }
      }

      dialogVisible.value = false
      refreshData()
    } catch (error: any) {
      console.error('操作失败:', error)
      ElMessage.error(error.message || '操作失败，请重试')
    } finally {
      formLoading.value = false
    }
  }
</script>

<style scoped>
  .family-page {
    padding: 24px;
    height: 100%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    background-color: #f8fafc;
  }

  .mt-4 {
    margin-top: 24px;
  }

  .art-table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: none;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow: hidden;
  }

  .table-container {
    flex: 1;
    overflow: hidden;
    margin-top: 16px;
  }
  
  .justify-center {
    justify-content: center;
  }

  :deep(.el-table__row) {
    transition: all 0.3s ease;
  }
  
  :deep(.el-table__row:hover) {
    background-color: #f8fafc !important;
  }
  
  @keyframes zoomIn {
    from { opacity: 0; transform: scale(0.95); }
    to { opacity: 1; transform: scale(1); }
  }
  
  .animate-zoom-in {
    animation: zoomIn 0.3s ease-out forwards;
  }

  :deep(.overflow-y-auto) {
    scrollbar-width: thin;
    scrollbar-color: #e2e8f0 #f8fafc;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar) {
    width: 6px;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar-track) {
    background: #f8fafc;
    border-radius: 3px;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar-thumb) {
    background: #e2e8f0;
    border-radius: 3px;
  }
  
  :deep(.overflow-y-auto::-webkit-scrollbar-thumb:hover) {
    background: #cbd5e1;
  }

  :deep(.family-dialog) {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.family-dialog .el-dialog__header) {
    padding: 0;
  }
  
  :deep(.family-dialog .el-dialog__body) {
    padding: 16px 20px;
  }
  
  :deep(.family-dialog .el-dialog__footer) {
    padding: 0 20px 16px;
  }

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

  .sub-account-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .account-count {
    font-size: 12px;
    font-weight: 500;
    color: #94a3b8;
    background-color: #f1f5f9;
    padding: 2px 8px;
    border-radius: 12px;
  }
  
  .sub-account-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 8px;
  }
  
  .sub-account-input {
    flex: 1;
  }
  
  .delete-btn {
    padding: 8px 12px;
    border-radius: 8px;
    background-color: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    color: #64748b;
    border: none;
  }
  
  .delete-btn:hover {
    background-color: #fef2f2;
    color: #ef4444;
  }
  
  .add-sub-btn {
    width: 100%;
    padding: 8px;
    margin-top: 8px;
    border: 1px dashed #e2e8f0;
    border-radius: 8px;
    background-color: #ffffff;
    color: #6366f1;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .add-sub-btn:hover {
    border-color: #6366f1;
    background-color: #f5f3ff;
  }

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

  .create-button {
    border-radius: 12px;
    padding: 8px 16px;
    font-weight: 500;
  }

  .sub-accounts-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }

  .sub-account-card {
    display: flex;
    align-items: center;
    gap: 6px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    transition: all 0.3s ease;
  }

  .sub-account-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }

  .sub-account-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
  }

  .sub-account-text {
    letter-spacing: 0.5px;
  }

  .sub-account-empty {
    display: flex;
    align-items: center;
    gap: 6px;
    background-color: #f8fafc;
    border: 2px dashed #e2e8f0;
    color: #94a3b8;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 13px;
    transition: all 0.3s ease;
  }

  .sub-account-empty:hover {
    border-color: #cbd5e1;
    background-color: #f1f5f9;
  }

  .empty-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    background-color: #e2e8f0;
    border-radius: 50%;
  }

  .empty-text {
    font-weight: 500;
  }

  .main-account-badge {
    display: flex;
    align-items: center;
    gap: 8px;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 8px 16px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(240, 147, 251, 0.3);
    transition: all 0.3s ease;
  }

  .main-account-badge:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(240, 147, 251, 0.4);
  }

  .badge-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 22px;
    height: 22px;
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
  }

  .badge-text {
    letter-spacing: 0.5px;
  }

  .badge-label {
    background-color: rgba(255, 255, 255, 0.2);
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 11px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .no-binding {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #94a3b8;
    font-size: 13px;
    padding: 6px 12px;
    background-color: #f8fafc;
    border-radius: 20px;
    border: 1px solid #e2e8f0;
  }

  @media (max-width: 768px) {
    .family-page {
      padding: 16px;
    }
    
    :deep(.family-dialog) {
      width: 95% !important;
    }
    
    :deep(.family-dialog .el-dialog__body) {
      padding: 0 16px 16px;
    }
    
    :deep(.family-dialog .el-dialog__footer) {
      padding: 0 16px 16px;
    }
    
    .dialog-header {
      padding: 20px 16px;
    }
    
    .header-title {
      font-size: 16px;
    }
    
    .dialog-footer {
      gap: 12px;
    }
  }
</style>
