<!-- 系统公告弹窗组件 -->
<template>
  <ElDialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="100px">
      <ElFormItem label="公告标题" prop="title">
        <ElInput v-model="form.title" placeholder="请输入公告标题" />
      </ElFormItem>

      <ElFormItem label="公告摘要" prop="summary">
        <ElInput v-model="form.summary" placeholder="请输入公告摘要" />
      </ElFormItem>

      <ElFormItem label="公告内容" prop="content">
        <ElInput v-model="form.content" placeholder="请输入公告内容" type="textarea" />
      </ElFormItem>

      <ElFormItem label="发布人" prop="author">
        <ElInput v-model="form.author" placeholder="请输入发布人" />
      </ElFormItem>

      <ElFormItem label="公告状态" prop="status">
        <ElSelect v-model="form.status" placeholder="请选择状态">
          <ElOption label="发布" value="1" />
          <ElOption label="草稿" value="0" />
        </ElSelect>
      </ElFormItem>

      <ElFormItem label="发布时间" prop="gmt_create">
        <ElInput v-model="form.gmt_create" placeholder="请输入发布时间" />
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
    announcementData: {
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
    return props.type === 'add' ? '新增公告' : '编辑公告'
  })

  const form = ref({ ...props.announcementData })

  const formRef = ref()

  const rules = {
    title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
    content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
  }

  watch(
    () => props.announcementData,
    (val) => {
      form.value = { ...val }
    },
    { deep: true }
  )

  watch(
    () => props.visible,
    (val) => {
      if (val) {
        form.value = { ...props.announcementData }
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
      // 表单验证失败
    }
  }
</script>
