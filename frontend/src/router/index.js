import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/courses",
      name: "courses",
      component: () => import("../views/CourseManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "course:list",
          "course:add",
          "course:edit",
          "course:delete",
        ],
      },
    },
    {
      path: "/training-programs",
      name: "training-programs",
      component: () => import("../views/TrainingProgramManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "training-program:list",
          "training-program:add",
          "training-program:edit",
          "training-program:delete",
        ],
      },
    },
    {
      path: "/knowledge-points",
      name: "knowledge-points",
      component: () => import("../views/KnowledgePointManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "knowledge-point:list",
          "knowledge-point:add",
          "knowledge-point:edit",
          "knowledge-point:delete",
        ],
      },
    },
    {
      path: "/question-bank",
      name: "question-bank",
      component: () => import("../views/QuestionBankManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "question:list",
          "question:add",
          "question:edit",
          "question:delete",
        ],
      },
    },
    {
      path: "/exam-papers",
      name: "exam-papers",
      component: () => import("../views/ExamPaperManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "exam-paper:list",
          "exam-paper:add",
          "exam-paper:edit",
          "exam-paper:delete",
        ],
      },
    },
    {
      path: "/statistics",
      name: "statistics",
      component: () => import("../views/Statistics.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["statistics:view"],
      },
    },
    {
      path: "/practice-projects",
      name: "practice-projects",
      component: () => import("../views/PracticeProjectManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "practice-project:list",
          "practice-project:add",
          "practice-project:edit",
          "practice-project:delete",
        ],
      },
    },
    {
      path: "/semester-management",
      name: "semester-management",
      component: () => import("../views/SemesterManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "semester:list",
          "semester:add",
          "semester:edit",
          "semester:delete",
        ],
      },
    },
    {
      path: "/semester-schedule",
      name: "semester-schedule",
      component: () => import("../views/SemesterSchedule.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["semester-schedule:view"],
      },
    },
    {
      path: "/users",
      name: "users",
      component: () => import("../views/UserManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["user:list", "user:add", "user:edit", "user:delete"],
      },
    },
    {
      path: "/roles",
      name: "roles",
      component: () => import("../views/RoleManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["role:list", "role:add", "role:edit", "role:delete"],
      },
    },
    {
      path: "/permissions",
      name: "permissions",
      component: () => import("../views/PermissionManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "permission:list",
          "permission:add",
          "permission:edit",
          "permission:delete",
        ],
      },
    },
  ],
});

const getRoleFlags = () => {
  const userStr = localStorage.getItem("user");
  const user = userStr ? JSON.parse(userStr) : null;

  const rolesStr = localStorage.getItem("roles");
  const roles = rolesStr ? JSON.parse(rolesStr) : [];
  const roleNames = roles
    .map((role) => (typeof role === "string" ? role : role.roleName))
    .filter(Boolean);

  const isSystemAdmin =
    (user && user.username === "admin") || roleNames.includes("系统管理员");
  const isProgramTeacher =
    roleNames.includes("学院管理员") || roleNames.includes("专业负责教师");
  const isNormalTeacher = roleNames.includes("教师") && !isProgramTeacher;

  return { isSystemAdmin, isProgramTeacher, isNormalTeacher };
};

// 检查用户是否有访问该路由的权限
const hasPermission = (to, permissions) => {
  // 如果路由没有配置权限要求，则允许访问
  if (!to.meta.permissions || to.meta.permissions.length === 0) {
    return true;
  }

  const { isSystemAdmin, isProgramTeacher, isNormalTeacher } = getRoleFlags();

  if (isSystemAdmin) {
    return to.path === "/users";
  }

  if (isProgramTeacher) {
    return [
      "/courses",
      "/training-programs",
      "/semester-schedule",
      "/knowledge-points",
      "/question-bank",
      "/practice-projects",
    ].includes(to.path);
  }

  if (isNormalTeacher) {
    return ["/knowledge-points", "/question-bank", "/exam-papers"].includes(
      to.path
    );
  }

  // 获取权限码列表
  const permissionCodes = permissions
    .map((p) => (typeof p === "string" ? p : p.permissionCode))
    .filter(Boolean);

  // 检查用户是否有路由要求的任一权限
  return to.meta.permissions.some((requiredPermission) => {
    return permissionCodes.includes(requiredPermission);
  });
};

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // 检查是否登录 - 必须同时有token和user信息才认为是已登录
    const token = localStorage.getItem("token");
    const userStr = localStorage.getItem("user");
    
    // 验证token和user的有效性
    if (!token || !userStr) {
      // 未登录，跳转到登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath },
      });
      return;
    }
    
    try {
      // 尝试解析用户信息，确保userStr是有效的JSON
      const user = JSON.parse(userStr);
      if (!user || !user.username) {
        // 用户信息无效，跳转到登录页
        next({
          path: "/login",
          query: { redirect: to.fullPath },
        });
        return;
      }
    } catch (e) {
      // 解析失败，跳转到登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath },
      });
      return;
    }

    // 已登录，检查权限
    const permissionsStr = localStorage.getItem("permissions");
    const permissions = permissionsStr ? JSON.parse(permissionsStr) : [];

    if (hasPermission(to, permissions)) {
      // 有权限，继续访问
      next();
    } else {
      // 无权限，跳转到登录页或显示错误信息
      ElMessage.error("您没有访问该页面的权限");
      // 避免无限重定向，直接放行但显示错误信息
      next(false);
    }
  } else {
    // 不需要认证的路由，直接访问
    next();
  }
});

export default router;
