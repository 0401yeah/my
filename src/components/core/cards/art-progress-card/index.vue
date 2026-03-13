<!-- 进度条卡片 -->
<template>
  <div class="art-card h-32 flex flex-col justify-center px-5">
    <div class="mb-3.5 flex items-center">
      <div v-if="icon" class="size-11 flex-cc bg-g-300 text-xl rounded-lg" :class="iconStyle">
        <ArtSvgIcon :icon="icon" class="text-2xl"></ArtSvgIcon>
      </div>
      <div v-if="props.titlePosition === 'icon-right' && icon" class="ml-4">
        <p class="text-sm text-g-500">{{ title }}</p>
        <p v-if="extraInfo" class="text-lg font-medium">{{ extraInfo }}</p>
      </div>
      <div class="flex-1 text-right">
        <ArtCountTo
          class="mb-1 block text-2xl font-semibold"
          :target="percentage"
          :duration="2000"
          suffix="%"
        />
        <p v-if="props.titlePosition === 'bottom'" class="text-sm text-g-500">{{ title }}</p>
      </div>
    </div>
    <ElProgress
      :percentage="currentPercentage"
      :stroke-width="strokeWidth"
      :show-text="false"
      :color="color"
      class="[&_.el-progress-bar__outer]:bg-[rgb(240_240_240)]"
    />
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'ArtProgressCard' })

  interface Props {
    /** 进度百分比 */
    percentage: number
    /** 标题 */
    title: string
    /** 颜色 */
    color?: string
    /** 图标 */
    icon?: string
    /** 图标样式 */
    iconStyle?: string
    /** 进度条宽度 */
    strokeWidth?: number
    /** 额外信息，如车位数量 */
    extraInfo?: string
    /** 标题位置，默认在百分比下方，设置为 'icon-right' 时显示在图标右侧 */
    titlePosition?: 'bottom' | 'icon-right'
  }

  const props = withDefaults(defineProps<Props>(), {
    strokeWidth: 5,
    color: '#67C23A',
    extraInfo: '',
    titlePosition: 'bottom'
  })

  const animationDuration = 500
  const currentPercentage = ref(0)

  const animateProgress = () => {
    const startTime = Date.now()
    const startValue = currentPercentage.value
    const endValue = props.percentage

    const animate = () => {
      const currentTime = Date.now()
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / animationDuration, 1)

      currentPercentage.value = startValue + (endValue - startValue) * progress

      if (progress < 1) {
        requestAnimationFrame(animate)
      }
    }

    requestAnimationFrame(animate)
  }

  onMounted(() => {
    animateProgress()
  })

  // 当 percentage 属性变化时重新执行动画
  watch(
    () => props.percentage,
    () => {
      animateProgress()
    }
  )
</script>
