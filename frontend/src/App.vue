<template>
  <div class="app-container">
    <!-- 只有非登录页面才显示完整布局 -->
    <template v-if="$route.path !== '/login'">
      <el-container>
        <el-header class="app-header">
          <h1>本科专业管理系统</h1>
          <div class="user-info" v-if="isLoggedIn">
            <span class="user-name">{{
              userInfo.realName || userInfo.username
            }}</span>
            <el-button type="danger" size="small" @click="handleLogout"
              >登出</el-button
            >
          </div>
        </el-header>
        <el-container>
          <el-aside width="200px" class="app-aside">
            <el-menu
              :default-active="activeMenu"
              class="el-menu-vertical-demo"
              @select="handleMenuSelect"
            >
              <el-menu-item index="/courses" v-if="canAccessMenu('/courses')">
                <el-icon><Document /></el-icon>
                <span>课程管理</span>
              </el-menu-item>
              <el-menu-item
                index="/knowledge-points"
                v-if="canAccessMenu('/knowledge-points')"
              >
                <el-icon><Collection /></el-icon>
                <span>知识点管理</span>
              </el-menu-item>
              <el-menu-item
                index="/question-bank"
                v-if="canAccessMenu('/question-bank')"
              >
                <el-icon><EditPen /></el-icon>
                <span>题库管理</span>
              </el-menu-item>
              <el-menu-item
                index="/exam-papers"
                v-if="canAccessMenu('/exam-papers')"
              >
                <el-icon><Notebook /></el-icon>
                <span>试卷管理</span>
              </el-menu-item>
              <el-menu-item
                index="/training-programs"
                v-if="canAccessMenu('/training-programs')"
              >
                <el-icon><List /></el-icon>
                <span>培养方案管理</span>
              </el-menu-item>
              <el-menu-item
                index="/statistics"
                v-if="canAccessMenu('/statistics')"
              >
                <el-icon><DataAnalysis /></el-icon>
                <span>统计分析</span>
              </el-menu-item>
              <el-menu-item
                index="/practice-projects"
                v-if="canAccessMenu('/practice-projects')"
              >
                <el-icon><Operation /></el-icon>
                <span>实践项目</span>
              </el-menu-item>
              <el-menu-item
                index="/semester-management"
                v-if="canAccessMenu('/semester-management')"
              >
                <el-icon><Calendar /></el-icon>
                <span>学期管理</span>
              </el-menu-item>
              <el-menu-item
                index="/semester-schedule"
                v-if="canAccessMenu('/semester-schedule')"
              >
                <el-icon><Notebook /></el-icon>
                <span>学期课表</span>
              </el-menu-item>
              <el-menu-item index="/users" v-if="canAccessMenu('/users')">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
              <el-menu-item index="/roles" v-if="canAccessMenu('/roles')">
                <el-icon><Key /></el-icon>
                <span>角色管理</span>
              </el-menu-item>
              <el-menu-item
                index="/permissions"
                v-if="canAccessMenu('/permissions')"
              >
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
    </template>
    <!-- 登录页面直接显示路由视图 -->
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import {
  Document,
  Collection,
  Operation,
  Calendar,
  EditPen,
  Notebook,
  List,
  DataAnalysis,
  User,
  Key,
  Lock,
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const activeMenu = ref("/courses");

// 初始化响应式变量
const userInfo = ref({ username: "", realName: "" });
const permissions = ref([]);

// 计算属性：判断是否已登录
const isLoggedIn = ref(false);

// 权限配置：每个菜单需要的权限
const menuPermissions = {
  "/courses": ["course:list"],
  "/knowledge-points": ["knowledge-point:list"],
  "/question-bank": ["question:list"],
  "/exam-papers": ["exam-paper:list"],
  "/training-programs": ["training-program:list"],
  "/statistics": ["statistics:view"],
  "/practice-projects": ["practice-project:list"],
  "/semester-management": ["semester:list"],
  "/semester-schedule": ["semester-schedule:view"],
  "/users": ["user:list"],
  "/roles": ["role:list"],
  "/permissions": ["permission:list"],
};

// 从localStorage获取用户信息和权限的函数
const getUserInfoAndPermissions = () => {
  const token = localStorage.getItem("token");
  const userStr = localStorage.getItem("user");
  const permissionsStr = localStorage.getItem("permissions");

  // 判断是否已登录
  isLoggedIn.value = !!(token && userStr);

  if (userStr && isLoggedIn.value) {
    userInfo.value = JSON.parse(userStr);
  } else {
    userInfo.value = { username: "", realName: "" };
  }

  if (permissionsStr && isLoggedIn.value) {
    try {
      permissions.value = JSON.parse(permissionsStr) || [];
    } catch (e) {
      permissions.value = [];
    }
  } else {
    permissions.value = [];
  }
};

// 检查用户是否有访问菜单的权限
const canAccessMenu = (menuPath) => {
  // 获取菜单需要的权限
  const requiredPermissions = menuPermissions[menuPath] || [];

  // 如果不需要权限，直接返回true
  if (requiredPermissions.length === 0) {
    return true;
  }

  // 检查用户是否有任一所需权限
  // 处理权限数据可能的不同结构
  const permissionCodes = permissions.value
    .map((p) => {
      // 如果是字符串，直接返回；如果是对象，返回permissionCode属性
      return typeof p === "string" ? p : p.permissionCode;
    })
    .filter(Boolean); // 过滤掉undefined和null

  // 管理员用户直接返回true
  if (userInfo.value.username === "admin") {
    return true;
  }

  return requiredPermissions.some((perm) => permissionCodes.includes(perm));
};

// 登出功能
const handleLogout = () => {
  // 清除localStorage中的信息
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("roles");
  localStorage.removeItem("permissions");

  // 同时更新响应式变量的值，确保用户界面立即更新
  userInfo.value = { username: "", realName: "" };
  permissions.value = [];

  // 跳转到登录页
  ElMessage.success("登出成功");
  router.push("/login");
};

// 初始化用户信息和权限
onMounted(() => {
  activeMenu.value = route.path;
  getUserInfoAndPermissions();
});

// 监听路由变化，当路由变化时重新获取用户信息和权限
watch(
  () => route.path,
  () => {
    activeMenu.value = route.path;
    getUserInfoAndPermissions();
  }
);

const handleMenuSelect = (key) => {
  router.push(key);
};
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
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-weight: 500;
  margin-right: 10px;
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
