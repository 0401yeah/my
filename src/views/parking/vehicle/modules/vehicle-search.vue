<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    :rules="rules"
    @reset="handleReset"
    @search="handleSearch"
  >
  </ArtSearchBar>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import ArtSearchBar from '@/components/core/forms/art-search-bar/index.vue'

  interface Props {
    modelValue: Record<string, any>
  }
  interface Emits {
    (e: 'update:modelValue', value: Record<string, any>): void
    (e: 'search', params: Record<string, any>): void
    (e: 'reset'): void
  }
  const props = defineProps<Props>()
  const emit = defineEmits<Emits>()

  // 表单数据双向绑定
  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  // 校验规则
  const rules = {
    // plateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }]
  }



  // 表单配置
  const formItems = computed(() => [
    {
      label: '车牌号',
      key: 'plateNumber',
      type: 'input',
      props: {
        placeholder: '请输入车牌号',
        clearable: true
      }
    },
    {
      label: '车主姓名',
      key: 'ownerName',
      type: 'input',
      props: {
        placeholder: '请输入车主姓名',
        clearable: true
      }
    },

  ])

  // 事件
  function handleReset() {
    console.log('重置表单')
    emit('reset')
  }

  async function handleSearch() {
    await searchBarRef.value.validate()
    emit('search', formData.value)
    console.log('表单数据', formData.value)
  }
</script>
