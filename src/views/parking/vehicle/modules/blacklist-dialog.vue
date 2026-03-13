<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加车辆标签' : '编辑车辆标签'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="车牌号" prop="plateNumber">
        <ElInput v-model="formData.plateNumber" placeholder="请输入车牌号" />
      </ElFormItem>
      <ElFormItem label="标签名称" prop="tagName">
        <ElInput v-model="formData.tagName" placeholder="请输入标签名称" />
      </ElFormItem>
      <ElFormItem label="标签类型" prop="tagType">
        <ElSelect v-model="formData.tagType">
          <ElOption label="普通" value="0" />
          <ElOption label="白名单" value="1" />
          <ElOption label="黑名单" value="2" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="标签原因" prop="reason">
        <ElInput v-model="formData.reason" placeholder="请输入标签原因" type="textarea" />
      </ElFormItem>
      <ElFormItem label="标签颜色" prop="color">
        <ElColorPicker v-model="formData.color" />
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
    blacklistData?: Partial<any>
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
    tagName: '',
    tagType: '0',
    reason: '',
    color: '#909399'
  })

  // 表单验证规则
  const rules: FormRules = {
    plateNumber: [
      { required: true, message: '请输入车牌号', trigger: 'blur' },
      { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
    ],
    tagName: [
      { required: true, message: '请输入标签名称', trigger: 'blur' },
      { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
    ],
    tagType: [{ required: true, message: '请选择标签类型', trigger: 'blur' }],
    reason: [
      { required: true, message: '请输入标签原因', trigger: 'blur' },
      { min: 1, max: 200, message: '长度在 1 到 200 个字符', trigger: 'blur' }
    ]
  }

  /**
   * 初始化表单数据
   * 根据对话框类型（新增/编辑）填充表单
   */
  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.blacklistData
    const row = props.blacklistData

    Object.assign(formData, {
      plateNumber: isEdit && row ? row.plateNumber || '' : '',
      tagName: isEdit && row ? row.tagName || '' : '',
      tagType: isEdit && row ? row.tagType || '0' : '0',
      reason: isEdit && row ? row.reason || '' : '',
      color: isEdit && row ? row.color || '#909399' : '#909399'
    })
  }

  /**
   * 监听对话框状态变化
   * 当对话框打开时初始化表单数据并清除验证状态
   */
  watch(
    () => [props.visible, props.type, props.blacklistData],
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
