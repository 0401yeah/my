<template>
  <ArtSearchBar
    ref="searchBarRef"
    v-model="formData"
    :items="formItems"
    @reset="handleReset"
    @search="handleSearch"
  >
  </ArtSearchBar>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
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

  const searchBarRef = ref()
  const formData = computed({
    get: () => props.modelValue,
    set: (val) => emit('update:modelValue', val)
  })

  const formItems = computed(() => [
    {
      label: '公告标题',
      key: 'title',
      type: 'input',
      placeholder: '请输入公告标题',
      clearable: true
    },
    {
      label: '公告状态',
      key: 'status',
      type: 'select',
      props: {
        placeholder: '请选择状态',
        clearable: true,
        options: [
          { label: '发布', value: '1' },
          { label: '草稿', value: '0' }
        ]
      }
    }
  ])

  function handleReset() {
    emit('reset')
  }

  async function handleSearch() {
    emit('search', formData.value)
  }
</script>
