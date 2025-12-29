<template>
  <div class="app-container">
    <el-container>
      <el-header class="app-header">
        <h1>本科专业管理系统</h1>
      </el-header>
      <el-container>
        <el-aside width="200px" class="app-aside">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/courses">
              <el-icon><Document /></el-icon>
              <span>课程管理</span>
            </el-menu-item>
            <el-menu-item index="/knowledge-points">
              <el-icon><Collection /></el-icon>
              <span>知识点管理</span>
            </el-menu-item>
            <el-menu-item index="/question-bank">
              <el-icon><EditPen /></el-icon>
              <span>题库管理</span>
            </el-menu-item>
            <el-menu-item index="/exam-papers">
              <el-icon><Notebook /></el-icon>
              <span>试卷管理</span>
            </el-menu-item>
            <el-menu-item index="/training-programs">
              <el-icon><List /></el-icon>
              <span>培养方案管理</span>
            </el-menu-item>
            <el-menu-item index="/statistics">
              <el-icon><DataAnalysis /></el-icon>
              <span>统计分析</span>
            </el-menu-item>
            <el-menu-item index="/practice-projects">
              <el-icon><Operation /></el-icon>
              <span>实践项目</span>
            </el-menu-item>
            <el-menu-item index="/semester-management">
              <el-icon><Calendar /></el-icon>
              <span>学期管理</span>
            </el-menu-item>
            <el-menu-item index="/semester-schedule">
              <el-icon><Notebook /></el-icon>
              <span>学期课表</span>
            </el-menu-item>
            <el-menu-item index="/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/roles">
              <el-icon><Key /></el-icon>
              <span>角色管理</span>
            </el-menu-item>
            <el-menu-item index="/permissions">
              <el-icon><Lock /></el-icon>
              <span>权限管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Document, Collection, Operation, Calendar, EditPen, Notebook, List, DataAnalysis, User, Key, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const activeMenu = ref('/courses')

onMounted(() => {
  activeMenu.value = route.path
})

const handleMenuSelect = (key) => {
  router.push(key)
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.app-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}

.app-aside {
  background-color: #304156;
  color: white;
}

.el-menu-vertical-demo {
  height: 100%;
  border-right: none;
  background-color: #304156;
}

.el-menu-vertical-demo :deep(.el-menu-item) {
  color: white;
}

.el-menu-vertical-demo :deep(.el-menu-item.is-active) {
  background-color: #409eff;
}

.el-menu-vertical-demo :deep(.el-menu-item:hover) {
  background-color: #1890ff;
}

.app-main {
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
