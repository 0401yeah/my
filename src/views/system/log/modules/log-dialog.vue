<!-- 系统日志弹窗组件 -->
<template>
  <ElDialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
      <ElFormItem label="用户名称" prop="username">
        <ElInput v-model="form.username" placeholder="请输入用户名称" />
      </ElFormItem>

      <ElFormItem label="操作类型" prop="operation">
        <ElInput v-model="form.operation" placeholder="请输入操作类型" />
      </ElFormItem>

      <ElFormItem label="操作状态" prop="status">
        <ElSelect v-model="form.status" placeholder="请选择状态">
          <ElOption label="成功" value="1" />
          <ElOption label="失败" value="0" />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="IP地址" prop="ip">
        <ElInput v-model="form.ip" placeholder="请输入IP地址" />
      </ElFormItem>

      <ElFormItem label="用户代理" prop="userAgent">
        <ElInput v-model="form.userAgent" placeholder="请输入用户代理" />
      </ElFormItem>

      <ElFormItem label="操作时间" prop="gmt_create">
        <ElInput v-model="form.gmt_create" placeholder="请输入操作时间" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElSpace>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </ElSpace>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ref, watch, computed } from 'vue'
  import { ElMessage } from 'element-plus'

  const props = defineProps({
    visible: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: 'add'
    },
    logData: {
      type: Object,
      default: () => ({})
    }
  })

  const emit = defineEmits(['update:visible', 'submit'])

  const dialogVisible = computed({
    get: () => props.visible,
    set: (val) => emit('update:visible', val)
  })

  const dialogTitle = computed(() => {
    return props.type === 'add' ? '新增日志' : '编辑日志'
  })

  const form = ref({ ...props.logData })

  const formRef = ref()

  const rules = {
    username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
    operation: [{ required: true, message: '请输入操作类型', trigger: 'blur' }]
  }

  watch(
    () => props.logData,
    (val) => {
      form.value = { ...val }
    },
    { deep: true }
  )

  watch(
    () => props.visible,
    (val) => {
      if (val) {
        form.value = { ...props.logData }
      }
    }
  )

  const handleSubmit = async () => {
    if (!formRef.value) return
    try {
      await formRef.value.validate()
      emit('submit', form.value)
      dialogVisible.value = false
    } catch (error) {
      console.error('表单验证失败:', error)
    }
  }
</script>
