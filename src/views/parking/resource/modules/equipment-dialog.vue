<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加设备' : '编辑设备'"
    width="30%"
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="80px">
      <ElFormItem label="设备名称" prop="name">
        <ElInput v-model="formData.name" placeholder="请输入设备名称" />
      </ElFormItem>
      <ElFormItem label="设备编号" prop="code">
        <ElInput v-model="formData.code" placeholder="请输入设备编号" />
      </ElFormItem>
      <ElFormItem label="设备类型" prop="type">
        <ElSelect v-model="formData.type">
          <ElOption label="闸机" value="闸机" />
          <ElOption label="相机" value="相机" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElSelect v-model="formData.status">
          <ElOption label="在线" value="1" />
          <ElOption label="故障" value="0" />
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
    equipmentData?: Partial<any>
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
    name: '',
    code: '',
    type: '闸机',
    status: '1'
  })

  // 表单验证规则
  const rules: FormRules = {
    name: [
      { required: true, message: '请输入设备名称', trigger: 'blur' },
      { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
    ],
    code: [
      { required: true, message: '请输入设备编号', trigger: 'blur' },
      { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
    ],
    type: [{ required: true, message: '请选择设备类型', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'blur' }]
  }

  /**
   * 初始化表单数据
   * 根据对话框类型（新增/编辑）填充表单
   */
  const initFormData = () => {
    const isEdit = props.type === 'edit' && props.equipmentData
    const row = props.equipmentData

    Object.assign(formData, {
      name: isEdit && row ? row.name || '' : '',
      code: isEdit && row ? row.code || '' : '',
      type: isEdit && row ? row.type || '闸机' : '闸机',
      status: isEdit && row ? row.status || '1' : '1'
    })
  }

  /**
   * 监听对话框状态变化
   * 当对话框打开时初始化表单数据并清除验证状态
   */
  watch(
    () => [props.visible, props.type, props.equipmentData],
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
