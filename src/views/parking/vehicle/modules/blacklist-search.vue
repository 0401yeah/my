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

  // 动态 options
  const tagTypeOptions = ref<{ label: string; value: string; disabled?: boolean }[]>([])

  // 模拟接口返回标签类型数据
  function fetchTagTypeOptions(): Promise<typeof tagTypeOptions.value> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve([
          { label: '普通', value: '0' },
          { label: '白名单', value: '1' },
          { label: '黑名单', value: '2' }
        ])
      }, 1000)
    })
  }

  onMounted(async () => {
    tagTypeOptions.value = await fetchTagTypeOptions()
  })

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
      label: '标签类型',
      key: 'tagType',
      type: 'select',
      props: {
        placeholder: '请选择标签类型',
        options: tagTypeOptions.value
      }
    }
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
