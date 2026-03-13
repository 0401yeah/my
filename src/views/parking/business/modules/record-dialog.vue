<template>
  <ElDialog
    v-model="visible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <ElFormItem label="车牌号" prop="plateNumber">
        <ElInput v-model="formData.plateNumber" placeholder="请输入车牌号" />
      </ElFormItem>
      <ElFormItem label="停车场" prop="parkingLotName">
        <ElInput v-model="formData.parkingLotName" placeholder="请输入停车场名称" />
      </ElFormItem>
      <ElFormItem label="入场时间" prop="gmtInto">
        <ElDatePicker
          v-model="formData.gmtInto"
          type="datetime"
          placeholder="请选择入场时间"
          style="width: 100%"
        />
      </ElFormItem>
      <ElFormItem label="出场时间" prop="gmtOut">
        <ElDatePicker
          v-model="formData.gmtOut"
          type="datetime"
          placeholder="请选择出场时间"
          style="width: 100%"
        />
      </ElFormItem>
      <ElFormItem label="停留时长" prop="stayMinutes">
        <ElInput v-model="formData.stayMinutes" placeholder="请输入停留时长(分钟)" />
      </ElFormItem>
      <ElFormItem label="费用" prop="payAmount">
        <ElInput v-model="formData.payAmount" placeholder="请输入费用" />
      </ElFormItem>
      <ElFormItem label="停车位" prop="spaceNo">
        <ElInput v-model="formData.spaceNo" placeholder="请输入停车位号" />
      </ElFormItem>
      <ElFormItem label="订单号" prop="orderNo">
        <ElInput v-model="formData.orderNo" placeholder="请输入订单号" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue'
  import type { FormInstance, FormRules } from 'element-plus'

  interface Props {
    modelValue: boolean
    data?: Record<string, any>
  }
  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'submit', data: Record<string, any>): void
  }
  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  const formRef = ref<FormInstance>()
  const visible = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const dialogTitle = computed(() => (props.data ? '编辑停车记录' : '新增停车记录'))

  const formData = ref<Record<string, any>>({
    plateNumber: '',
    parkingLotName: '',
    gmtInto: '',
    gmtOut: '',
    stayMinutes: '',
    payAmount: '',
    spaceNo: '',
    orderNo: ''
  })

  const rules: FormRules = {
    plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
    parkingLotName: [{ required: true, message: '请输入停车场名称', trigger: 'blur' }]
  }

  watch(
    () => props.data,
    (val) => {
      if (val) {
        formData.value = { ...val }
      } else {
        formData.value = {
          plateNumber: '',
          parkingLotName: '',
          gmtInto: '',
          gmtOut: '',
          stayMinutes: '',
          payAmount: '',
          spaceNo: '',
          orderNo: ''
        }
      }
    },
    { immediate: true }
  )

  const handleClose = () => {
    formRef.value?.resetFields()
    visible.value = false
  }

  const handleSubmit = async () => {
    await formRef.value?.validate()
    emit('submit', formData.value)
    handleClose()
  }
</script>
