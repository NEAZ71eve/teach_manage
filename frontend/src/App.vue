<template>
  <div class="app-page">
    <div class="bg-decoration" aria-hidden="true"></div>

    <!-- 只有非登录页面才显示完整布局 -->
    <template v-if="$route.path !== '/login'">
      <el-container class="shell">
        <!-- 顶部栏 -->
        <el-header class="app-header">
          <div class="brand">
            <div class="brand-badge">PM</div>
            <div class="brand-text">
              <div class="brand-title">本科专业管理系统</div>
              <div class="brand-subtitle">Professional Management Portal</div>
            </div>
          </div>

          <div class="header-right">
            <div class="user-info" v-if="isLoggedIn">
              <div class="user-chip">
                <span class="user-name">{{
                  userInfo.realName || userInfo.username
                }}</span>
                <span class="user-role" v-if="userInfo.username === 'admin'"
                  >管理员</span
                >
              </div>

              <el-button
                class="logout-btn"
                type="danger"
                size="small"
                @click="handleLogout"
              >
                登出
              </el-button>
            </div>
          </div>
        </el-header>

        <el-container class="body">
          <!-- 侧边栏 -->
          <el-aside width="220px" class="app-aside">
            <div class="aside-inner">
              <el-menu
                :default-active="activeMenu"
                class="app-menu"
                @select="handleMenuSelect"
                router
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

              <div class="aside-footer muted">
                <span>© {{ new Date().getFullYear() }}</span>
              </div>
            </div>
          </el-aside>

          <!-- 主体 -->
          <el-main class="app-main">
            <div class="main-inner">
              <router-view />
            </div>
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
const roles = ref([]);

// 是否已登录
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

// 从localStorage获取用户信息和权限
const getUserInfoAndPermissions = () => {
  const token = localStorage.getItem("token");
  const userStr = localStorage.getItem("user");
  const permissionsStr = localStorage.getItem("permissions");
  const rolesStr = localStorage.getItem("roles");

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

  if (rolesStr && isLoggedIn.value) {
    try {
      roles.value = JSON.parse(rolesStr) || [];
    } catch (e) {
      roles.value = [];
    }
  } else {
    roles.value = [];
  }
};

// 检查用户是否有访问菜单的权限
const canAccessMenu = (menuPath) => {
  // 管理员直接放行
  if (userInfo.value.username === "admin") return true;

  const roleNames = roles.value
    .map((role) => (typeof role === "string" ? role : role.roleName))
    .filter(Boolean);

  const isSystemAdmin =
    userInfo.value.username === "admin" || roleNames.includes("系统管理员");
  const isProgramTeacher =
    roleNames.includes("学院管理员") || roleNames.includes("专业负责教师");
  const isNormalTeacher = roleNames.includes("教师") && !isProgramTeacher;

  if (isSystemAdmin) {
    return menuPath === "/users";
  }

  if (isProgramTeacher) {
    return ["/courses", "/training-programs", "/semester-schedule"].includes(
      menuPath
    );
  }

  if (isNormalTeacher) {
    return ["/knowledge-points", "/question-bank", "/exam-papers"].includes(
      menuPath
    );
  }

  const permissionCodes = permissions.value
    .map((p) => (typeof p === "string" ? p : p.permissionCode))
    .filter(Boolean);

  const requiredPermissions = menuPermissions[menuPath] || [];
  if (requiredPermissions.length === 0) return true;

  return requiredPermissions.some((perm) => permissionCodes.includes(perm));
};

// 登出
const handleLogout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("roles");
  localStorage.removeItem("permissions");

  userInfo.value = { username: "", realName: "" };
  permissions.value = [];

  ElMessage.success("登出成功");
  router.push("/login");
};

onMounted(() => {
  activeMenu.value = route.path;
  getUserInfoAndPermissions();
});

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
/* ====== 背景与整体 ====== */
.app-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: radial-gradient(
      1200px 600px at 20% 10%,
      rgba(64, 158, 255, 0.16),
      transparent 60%
    ),
    radial-gradient(
      900px 500px at 85% 20%,
      rgba(103, 194, 58, 0.12),
      transparent 55%
    ),
    radial-gradient(
      700px 500px at 70% 90%,
      rgba(230, 162, 60, 0.1),
      transparent 55%
    ),
    linear-gradient(180deg, #f6f8fb 0%, #eef2f7 100%);
}

.bg-decoration {
  position: absolute;
  inset: -40px;
  background-image: radial-gradient(rgba(0, 0, 0, 0.045) 1px, transparent 1px);
  background-size: 18px 18px;
  mask-image: radial-gradient(circle at 50% 30%, black 0%, transparent 65%);
  pointer-events: none;
}

/* ====== 外壳 ====== */
.shell {
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

/* ====== Header ====== */
.app-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px;
  margin: 14px 14px 10px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-badge {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(135deg, #409eff 0%, #6aa8ff 55%, #67c23a 120%);
  box-shadow: 0 10px 18px rgba(64, 158, 255, 0.22);
  user-select: none;
}

.brand-title {
  font-size: 16px;
  font-weight: 800;
  color: rgba(0, 0, 0, 0.86);
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.user-name {
  font-weight: 800;
  color: rgba(0, 0, 0, 0.75);
}

.user-role {
  font-size: 12px;
  color: #409eff;
  background: rgba(64, 158, 255, 0.12);
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid rgba(64, 158, 255, 0.18);
}

.logout-btn {
  border-radius: 10px;
}

/* ====== Body ====== */
.body {
  margin: 0 14px 14px;
  gap: 12px;
}

/* ====== Aside ====== */
.app-aside {
  border-radius: 16px;
  overflow: hidden;
  background: rgba(16, 24, 40, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
}

.aside-inner {
  height: calc(100vh - 64px - 14px - 14px - 10px); /* 适配 header + 外边距 */
  display: flex;
  flex-direction: column;
  padding: 10px 8px;
}

.app-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

/* 菜单项基础 */
.app-menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  border-radius: 12px;
  margin: 4px 6px;
  color: rgba(255, 255, 255, 0.86);
  transition: background 0.16s ease, color 0.16s ease, transform 0.16s ease;
}

/* 图标对齐 */
.app-menu :deep(.el-menu-item .el-icon) {
  margin-right: 10px;
  font-size: 16px;
}

/* hover */
.app-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(1px);
}

/* active */
.app-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(
    135deg,
    rgba(64, 158, 255, 0.95),
    rgba(106, 168, 255, 0.92)
  );
  color: #fff;
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.22);
}

/* menu 自身背景透明 */
.app-menu :deep(.el-menu) {
  background: transparent;
}

/* aside footer */
.aside-footer {
  padding: 8px 12px;
  margin: 6px 6px 2px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.06);
  text-align: center;
  font-size: 12px;
}

.muted {
  color: rgba(255, 255, 255, 0.65);
}

/* ====== Main ====== */
.app-main {
  padding: 0;
  background: transparent;
}

.main-inner {
  height: calc(100vh - 64px - 14px - 14px - 10px);
  overflow: auto;
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.74);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
}

/* 小屏优化 */
@media (max-width: 980px) {
  .app-aside {
    width: 200px !important;
  }
  .brand-subtitle {
    display: none;
  }
}
</style>
