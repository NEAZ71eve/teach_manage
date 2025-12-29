<template>
  <div class="practice-project-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>实践项目管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            添加实践项目
          </el-button>
        </div>
      </template>
      <el-empty description="实践项目管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>