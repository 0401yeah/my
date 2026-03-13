<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加车辆' : '编辑车辆'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="车牌号" prop="plateNumber">
        <ElInput v-model="formData.plateNumber" placeholder="请输入车牌号" />
      </ElFormItem>
      <ElFormItem label="车主姓名" prop="ownerName">
        <ElInput v-model="formData.ownerName" placeholder="请输入车主姓名" />
      </ElFormItem>
      <ElFormItem label="联系电话" prop="mobile">
        <ElInput v-model="formData.mobile" placeholder="请输入联系电话" />
      </ElFormItem>
      <ElFormItem label="车辆类型" prop="type">
        <ElSelect v-model="formData.type">
          <ElOption label="临时车" value="0" />
          <ElOption label="月租车" value="1" />
          <ElOption label="VIP" value="2" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElSelect v-model="formData.status">
          <ElOption label="正常" value="1" />
          <ElOption label="禁用" value="0" />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">提交</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ref, computed, watch, nextTick } from 'vue'
  import { ElMessage } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'

  interface Props {
    visible: boolean
    type: string
    vehicleData?: Partial<any>
  }

  interface Emits {
    (e: 'update:visible', value: boolean): void
    (e: 'submit'): void
  }

  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 对话框显示控制
  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const dialogType = computed(() => props.type)

  // 表单实例
  const formRef = ref<FormInstance>()

  // 表单数据
  const formData = reactive({
    plateNumber: '',
    ownerName: '',
    mobile: '',
    type: '0',
    status: '1'
  })

  // 表单验证规则
  const rules: FormRules = {
    plateNumber: [
      { required: true, message: '请输入车牌号', trigger: 'blur' },
      { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
    ],
    ownerName: [
      { required: true, message: '请输入车主姓名', trigger: 'blur' },
      { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
    ],
    mobile: [
      { required: true, message: '请输入联系电话', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
    ],
    type: [{ required: true, message: '请选择车辆类型', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'blur' }]
  }

  /**
   * 初始化表单数据
   * 根据对话框类型（新增/编辑）填充表单
   */
  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.vehicleData
    const row = props.vehicleData

    Object.assign(formData, {
      plateNumber: isEdit && row ? row.plateNumber || '' : '',
      ownerName: isEdit && row ? row.ownerName || '' : '',
      mobile: isEdit && row ? row.mobile || '' : '',
      type: isEdit && row ? row.type || '0' : '0',
      status: isEdit && row ? row.status || '1' : '1'
    })
  }

  /**
   * 监听对话框状态变化
   * 当对话框打开时初始化表单数据并清除验证状态
   */
  watch(
    () => [props.visible, props.type, props.vehicleData],
    ([visible]) => {
      if (visible) {
        initFormData()
        nextTick(() => {
          formRef.value?.clearValidate()
        })
      }
    },
    { immediate: true }
  )

  /**
   * 提交表单
   * 验证通过后触发提交事件
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate((valid) => {
      if (valid) {
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
        dialogVisible.value = false
        emit('submit')
      }
    })
  }
</script>
