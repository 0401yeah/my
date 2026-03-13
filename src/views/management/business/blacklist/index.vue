<template>
  <div class="blacklist-page">
    <el-card shadow="never">
      <div class="page-title">黑名单管理</div>
      <div class="empty-content">
        <el-empty description="暂无数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
  import { ElCard, ElEmpty } from 'element-plus'
  
  defineOptions({ name: 'Blacklist' })
</script>

<style scoped>
  .blacklist-page {
    padding: 16px;
    height: 100%;
    box-sizing: border-box;
  }
  
  .page-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 20px;
  }
  
  .empty-content {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px 0;
  }
</style>