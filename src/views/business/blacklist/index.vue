<template>
  <div class="blacklist-page">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" ref="searchFormRef" class="search-form">
        <el-form-item label="车牌号码" prop="plate_number">
          <el-input 
            v-model="searchForm.plate_number" 
            placeholder="请输入车牌号码" 
            clearable 
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="名单类型" prop="tag_type">
          <el-select v-model="searchForm.tag_type" placeholder="全部类型" clearable style="width: 160px">
            <el-option label="白名单 (特种/固定)" :value="1" />
            <el-option label="黑名单 (逃避漏费)" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="到期时间" prop="expire_time">
          <el-date-picker
            v-model="searchForm.expire_time"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            clearable
            style="width: 300px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="art-table-card" shadow="never">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd">新增车辆标签</el-button>
        </div>
        <div class="toolbar-right">
          <ArtTableHeader
            :loading="loading"
            layout="refresh,size,fullscreen,columns,settings"
            fullClass="art-table-card"
          />
        </div>
      </div>

      <div class="table-container">
        <art-table
          row-key="id"
          :loading="loading"
          :data="data"
          :columns="columns"
          :pagination="pagination"
          @pagination:size-change="handleSizeChange"
          @pagination:current-change="handleCurrentChange"
        >
          <template #operation="{ row }">
            <div class="flex items-center gap-2">
              <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
              <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
            </div>
          </template>
        </art-table>
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-position="top" :rules="rules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="车牌号码" prop="plateNumber">
              <el-input v-model="formData.plateNumber" placeholder="请输入车牌号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="名单类型" prop="tagType">
              <el-select v-model="formData.tagType" placeholder="请选择名单类型">
                <el-option label="白名单 (特种/固定)" value="1" />
                <el-option label="黑名单 (逃避漏费)" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期时间" prop="expireTime">
              <el-date-picker
                v-model="formData.expireTime"
                type="datetime"
                placeholder="请选择到期时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="原因" prop="reason">
              <el-input v-model="formData.reason" placeholder="请输入原因" type="textarea" :rows="4" />
            </el-form-item>
          </el-col>
        </el-row>
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
  import { reactive, ref, computed } from 'vue'
  import { useTable } from '@/hooks/core/useTable'
  import ArtTableHeader from '@/components/core/tables/art-table-header/index.vue'
  import ArtButtonTable from '@/components/core/forms/art-button-table/index.vue'
  import { ElButton, ElMessage, ElMessageBox, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElRow, ElCol, ElDatePicker } from 'element-plus'
  import type { FormInstance } from 'element-plus'
  import { blacklistApi } from '@/api/business'

  defineOptions({ name: 'Blacklist' })

  // --- 搜索表单相关 ---
  const searchFormRef = ref<FormInstance>()
  const searchForm = reactive({
    plate_number: '',
    tag_type: '',
    expire_time: []
  })

  // --- 对话框相关 ---
  const dialogVisible = ref(false)
  const formRef = ref<FormInstance>()
  const formData = reactive({
    id: '',
    plateNumber: '',
    tagType: '',
    reason: '',
    expireTime: ''
  })

  // 对话框标题
  const dialogTitle = computed(() => {
    return formData.id ? '编辑车辆标签' : '新增车辆标签'
  })

  // 表单验证规则
  const rules = reactive({
    plateNumber: [{ required: true, message: '请输入车牌号码', trigger: 'blur' }],
    tagType: [{ required: true, message: '请选择名单类型', trigger: 'change' }],
    reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
  })

  // --- 表格数据相关 ---
  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange, searchParams, getData, refreshData } = useTable({
    core: {
      apiFn: blacklistApi.fetchBlacklistList,
      // 动态绑定搜索参数
      apiParams: Object.assign({ current: 1, size: 20 }, searchForm), 
      columnsFactory: () => [
        { type: 'globalIndex', width: 80, label: '序号', align: 'center' },
        
        // 1. 修改 plate_number 为 plateNumber
        {
          prop: 'plateNumber', 
          label: '车牌', 
          minWidth: 120, 
          sortable: true, 
          align: 'left',
          formatter: (row: any) => row.plateNumber || '-'
        },
        
        // 2. 修改 tag_type 为 tagType
        { 
          prop: 'tagType', 
          label: '类型', 
          minWidth: 120, 
          sortable: true,
          align: 'left',
          formatter: (row: any) => (row.tagType === 1 ? '白名单' : row.tagType === 2 ? '黑名单' : '普通')
        },
        
        // reason 没下划线，不用动，所以它本来就能显示
        { prop: 'reason', label: '原因', minWidth: 280, showOverflowTooltip: true, align: 'left' },
        
        // 3. 修改 gmt_create 为 gmtCreate
        { prop: 'gmtCreate', label: '创建时间', minWidth: 180, sortable: true, align: 'left' },
        
        // 4. 修改 expire_time 为 expireTime
        {
          prop: 'expireTime',
          label: '到期时间',
          minWidth: 180,
          sortable: true,
          align: 'left',
          formatter: (row: any) => {
            return row.expireTime ? row.expireTime : '永久有效'
          }
        },
        {
          label: '操作',
          width: 120,
          fixed: 'right',
          slot: 'operation',
          useSlot: true,
          prop: 'operation',
          align: 'left'
        }
      ]
    }
  })

  // --- 事件处理 ---
  const handleSearch = () => {
    // 重置搜索参数
    Object.assign(searchParams, {
      plate_number: searchForm.plate_number,
      tag_type: searchForm.tag_type,
      current: 1
    })
    
    // 处理时间范围
    if (searchForm.expire_time && searchForm.expire_time.length === 2) {
      searchParams.expire_time_start = searchForm.expire_time[0]
      searchParams.expire_time_end = searchForm.expire_time[1]
    } else {
      delete searchParams.expire_time_start
      delete searchParams.expire_time_end
    }
    
    getData()
  }

  const handleReset = () => {
    if (!searchFormRef.value) return
    searchFormRef.value.resetFields()
    handleSearch()
  }

  const handleAdd = () => {
    // 重置表单
    formData.id = ''
    formData.plateNumber = ''
    formData.tagType = ''
    formData.reason = ''
    formData.expireTime = ''
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    // 填充表单数据
    formData.id = row.id
    formData.plateNumber = row.plateNumber || ''
    formData.tagType = row.tagType.toString() || ''
    formData.reason = row.reason || ''
    formData.expireTime = row.expireTime || ''
    dialogVisible.value = true
  }

  // 提交表单
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()

      // 准备提交数据
      const submitData = {
        ...formData,
        tagType: parseInt(formData.tagType)
      }

      // 根据是否有id判断是新增还是编辑
      if (submitData.id) {
        // 调用API更新
        await blacklistApi.updateBlacklist(submitData)
        ElMessage.success('编辑成功')
      } else {
        // 调用API添加
        await blacklistApi.addBlacklist(submitData)
        ElMessage.success('新增成功')
      }

      // 关闭对话框
      dialogVisible.value = false

      // 刷新数据
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除 ${row.plateNumber} 的记录吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        try {
          await blacklistApi.deleteBlacklist(row.id)
          ElMessage.success(`删除车牌: ${row.plateNumber} 成功`)
          refreshData()
        } catch (error) {
          ElMessage.error('删除失败，请重试')
        }
      })
      .catch(() => {
        ElMessage.info('已取消删除')
      })
  }
</script>

<style scoped>
  .blacklist-page {
    padding: 16px;
    height: 100%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
  }

  /* 搜索卡片样式 */
  .search-card {
    margin-bottom: 16px;
    border: none;
  }
  
  /* 修复 Element Plus 表单在卡片中底部多余的 margin */
  .search-form .el-form-item {
    margin-bottom: 0;
  }

  .art-table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    border: none;
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 16px;
    overflow: hidden;
  }

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .table-container {
    flex: 1;
    overflow: hidden;
  }
  
  .justify-center {
    justify-content: center;
  }
</style>