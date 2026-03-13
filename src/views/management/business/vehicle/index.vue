<template>
  <div class="vehicle-page">
    <el-card shadow="never">
      <div class="page-title">车辆管理</div>
      <div class="empty-content">
        <el-empty description="暂无数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
  import { ElCard, ElEmpty } from 'element-plus'
  
  defineOptions({ name: 'Vehicle' })
</script>

<style scoped>
  .vehicle-page {
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