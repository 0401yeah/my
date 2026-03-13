<!-- 系统logo -->
<template>
  <div class="flex-cc" :style="logoStyle">
    <ArtSvgIcon
      icon="material-symbols:multiline-chart"
      class="text-blue-500"
      style="font-size: 1.5em"
    />
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import ArtSvgIcon from '../art-svg-icon/index.vue'

  defineOptions({ name: 'ArtLogo' })

  interface Props {
    /** logo 大小 */
    size?: number | string
  }

  const props = withDefaults(defineProps<Props>(), {
    size: 36
  })

  const logoStyle = computed(() => ({
    width: `${props.size}px`,
    height: `${props.size}px`
  }))
</script>
