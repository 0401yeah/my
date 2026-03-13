<template>
  <div class="camera-player w-full aspect-video bg-black rounded-lg overflow-hidden relative">
    <!-- 视频画面 -->
    <video
      v-if="status === 'online' && selectedDeviceId"
      ref="videoRef"
      class="w-full h-full object-cover"
      autoplay
      playsinline
      muted
    />
    
    <!-- 占位符（默认关闭/黑屏状态） -->
    <div
      v-else
      class="w-full h-full bg-black flex items-center justify-center"
    >
      <div class="text-white/50 text-sm">摄像头未开启</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 定义组件属性
const props = defineProps({
  // 摄像头状态: 'online', 'offline', 'error'
  status: {
    type: String,
    default: 'offline'
  },
  // 选中的设备ID
  selectedDeviceId: {
    type: String,
    default: ''
  }
})

// 视频元素引用
const videoRef = ref<HTMLVideoElement | null>(null)

// 暴露方法给父组件
defineExpose({
  get videoElement() {
    return videoRef.value
  }
})
</script>

<style scoped>
.camera-player {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  transition: all 0.3s ease;
}

.camera-player:hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.7);
}
</style>