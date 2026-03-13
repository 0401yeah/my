<!-- 表格按钮 -->
<template>
  <el-tooltip :content="tooltipText" placement="top">
    <div
      :class="[
        'inline-flex items-center justify-center min-w-8 h-8 px-2.5 mr-2.5 text-sm c-p rounded-md align-middle',
        buttonClass,
        { 'opacity-50 cursor-not-allowed': props.disabled || props.loading }
      ]"
      :style="{ backgroundColor: buttonBgColor || '', color: iconColor || '' }"
      @click="handleClick"
    >
      <ArtSvgIcon :icon="iconContent" v-if="!props.loading" />
      <el-icon v-else class="is-loading"><Loading /></el-icon>
    </div>
  </el-tooltip>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import { Loading } from '@element-plus/icons-vue'
  defineOptions({ name: 'ArtButtonTable' })

  interface Props {
    /** 按钮类型 */
    type?: 'add' | 'edit' | 'delete' | 'refund' | 'more' | 'view' | 'pay'
    /** 按钮图标 */
    icon?: string
    /** 按钮样式类 */
    iconClass?: string
    /** icon 颜色 */
    iconColor?: string
    /** 按钮背景色 */
    buttonBgColor?: string
    /** 是否禁用 */
    disabled?: boolean
    /** 是否加载中 */
    loading?: boolean
    /** 行数据 */
    row?: any
    /** tooltip文本 */
    tooltip?: string
  }

  const props = withDefaults(defineProps<Props>(), {
    disabled: false,
    loading: false,
    row: undefined,
    tooltip: ''
  })

  const emit = defineEmits<{
    (e: 'click'): void
  }>()

  // 默认按钮配置
  const defaultButtons = {
    add: { icon: 'ri:add-fill', class: 'bg-theme/12 text-theme', tooltip: '新增' },
    edit: { icon: 'ri:pencil-line', class: 'bg-secondary/12 text-secondary', tooltip: '编辑' },
    delete: { icon: 'ri:delete-bin-5-line', class: 'bg-error/12 text-error', tooltip: '删除' },
    refund: { icon: 'ri:exchange-dollar-line', class: 'bg-warning/12 text-warning', tooltip: '退款' },
    view: { icon: 'ri:eye-line', class: 'bg-info/12 text-info', tooltip: '查看' },
    pay: { icon: 'ri:wallet-2-line', class: 'bg-success/12 text-success', tooltip: '支付' },
    more: { icon: 'ri:more-2-fill', class: '', tooltip: '更多' }
  } as const

  // 获取图标内容
  const iconContent = computed(() => {
    return props.icon || (props.type ? defaultButtons[props.type]?.icon : '') || ''
  })

  // 获取按钮样式类
  const buttonClass = computed(() => {
    return props.iconClass || (props.type ? defaultButtons[props.type]?.class : '') || ''
  })

  // 获取tooltip文本
  const tooltipText = computed(() => {
    if (props.tooltip) {
      return props.tooltip
    }
    return props.type ? defaultButtons[props.type]?.tooltip : ''
  })

  const handleClick = () => {
    if (!props.disabled && !props.loading) {
      emit('click')
    }
  }
</script>
