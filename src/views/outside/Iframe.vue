<template>
  <div class="art-iframe-container">
    <!-- 加载动画 -->
    <div v-if="loading" class="loading-container">
      <ElIcon class="is-loading"><Loading /></ElIcon>
      <span>加载中...</span>
    </div>
    
    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <ElIcon class="error-icon"><CircleClose /></ElIcon>
      <span>{{ error }}</span>
      <ElButton size="small" type="primary" @click="reloadIframe" style="margin-top: 12px">
        重新加载
      </ElButton>
    </div>
    
    <!-- iframe 容器 -->
    <div v-else class="iframe-wrapper" ref="iframeWrapperRef">
      <iframe
        ref="iframeRef"
        :src="iframeSrc"
        :style="iframeStyle"
        @load="handleIframeLoad"
        @error="handleIframeError"
        frameborder="0"
        scrolling="auto"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElButton, ElIcon } from 'element-plus'
import { Loading, CircleClose } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const props = defineProps<{
  src: string
}>()

const iframeRef = ref<HTMLIFrameElement | null>(null)
const iframeWrapperRef = ref<HTMLDivElement | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

// 计算 iframe 地址
const iframeSrc = computed(() => props.src)

// 计算 iframe 样式
const iframeStyle = computed(() => ({
  width: '100%',
  height: '100%',
  border: 'none',
  display: loading.value || error.value ? 'none' : 'block'
}))

// 处理 iframe 加载完成
const handleIframeLoad = () => {
  loading.value = false
  error.value = null
}

// 处理 iframe 加载错误
const handleIframeError = () => {
  loading.value = false
  error.value = '页面加载失败，请检查网络连接或地址是否正确'
}

// 重新加载 iframe
const reloadIframe = () => {
  loading.value = true
  error.value = null
  if (iframeRef.value) {
    iframeRef.value.src = iframeSrc.value
  }
}

// 生命周期钩子
onMounted(() => {
})

onUnmounted(() => {
})
</script>

<style scoped lang="scss">
.art-iframe-container {
  width: 100%;
  height: 100%;
  position: relative;
  background-color: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.loading-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  z-index: 10;
  
  .is-loading {
    font-size: 24px;
    color: #409eff;
    margin-bottom: 12px;
  }
  
  span {
    color: #606266;
    font-size: 14px;
  }
}

.error-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  z-index: 10;
  
  .error-icon {
    font-size: 32px;
    color: #f56c6c;
    margin-bottom: 12px;
  }
  
  span {
    color: #606266;
    font-size: 14px;
    margin-bottom: 8px;
    text-align: center;
    padding: 0 20px;
  }
}

.iframe-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

iframe {
  transition: opacity 0.3s ease;
}
</style>