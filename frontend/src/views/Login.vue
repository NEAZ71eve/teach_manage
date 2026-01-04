<template>
  <div class="login-page">
    <div class="bg-decoration" aria-hidden="true"></div>

    <div class="login-card">
      <div class="brand">
        <div class="brand-badge">PM</div>
        <div class="brand-text">
          <div class="brand-title">本科专业管理系统</div>
          <div class="brand-subtitle">Professional Management Portal</div>
        </div>
      </div>

      <el-form
          class="login-form"
          :model="loginForm"
          :rules="loginRules"
          ref="loginFormRef"
          label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
              size="large"
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button
            class="login-btn"
            type="primary"
            :loading="loading"
            @click="handleLogin"
        >
          登录
        </el-button>

        <div class="tips">
          <span></span>
          <span class="muted"> </span>
        </div>
      </el-form>
    </div>

    <div class="footer">
      <span class="muted">© {{ new Date().getFullYear() }} 本科专业管理系统</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
import { login } from "../api/user";

const router = useRouter();
const loginFormRef = ref();
const loading = ref(false);

const loginForm = reactive({
  username: "",
  password: "",
});

const loginRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const resolveLandingRoute = (user, roles) => {
  const roleNames = (roles || [])
    .map((role) => (typeof role === "string" ? role : role.roleName))
    .filter(Boolean);

  const isSystemAdmin =
    (user && user.username === "admin") || roleNames.includes("系统管理员");
  const isProgramTeacher =
    roleNames.includes("学院管理员") || roleNames.includes("专业负责教师");
  const isNormalTeacher = roleNames.includes("教师") && !isProgramTeacher;

  if (isSystemAdmin) return "/users";
  if (isProgramTeacher) return "/courses";
  if (isNormalTeacher) return "/knowledge-points";
  return "/courses";
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      const response = await login(loginForm);

      if (response.success) {
        // 清除之前的localStorage信息
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        localStorage.removeItem("roles");
        localStorage.removeItem("permissions");

        // 保存token、用户信息、角色和权限到localStorage
        localStorage.setItem("token", response.token);
        localStorage.setItem("user", JSON.stringify(response.user));
        localStorage.setItem("roles", JSON.stringify(response.roles || []));
        localStorage.setItem("permissions", JSON.stringify(response.permissions || []));

        ElMessage.success("登录成功");
        router.push(resolveLandingRoute(response.user, response.roles));
      } else {
        ElMessage.error(response.message || "登录失败");
      }
    } catch (error) {
      ElMessage.error("登录失败，请检查网络连接");
      console.error("登录失败:", error);
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped>
/* 页面整体 */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px 16px 54px;
  position: relative;
  overflow: hidden;
  background: radial-gradient(1200px 600px at 20% 10%, rgba(64, 158, 255, 0.18), transparent 60%),
  radial-gradient(900px 500px at 85% 20%, rgba(103, 194, 58, 0.14), transparent 55%),
  radial-gradient(700px 500px at 70% 90%, rgba(230, 162, 60, 0.12), transparent 55%),
  linear-gradient(180deg, #f6f8fb 0%, #eef2f7 100%);
}

/* 背景点阵装饰 */
.bg-decoration {
  position: absolute;
  inset: -40px;
  background-image: radial-gradient(rgba(0, 0, 0, 0.045) 1px, transparent 1px);
  background-size: 18px 18px;
  mask-image: radial-gradient(circle at 50% 30%, black 0%, transparent 65%);
  pointer-events: none;
}

/* 登录卡片 */
.login-card {
  width: 420px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.10);
  padding: 26px 26px 22px;
  position: relative;
  z-index: 1;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.login-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 22px 55px rgba(0, 0, 0, 0.12);
}

/* 品牌区 */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.brand-badge {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(135deg, #409eff 0%, #6aa8ff 55%, #67c23a 120%);
  box-shadow: 0 10px 18px rgba(64, 158, 255, 0.25);
  user-select: none;
}

.brand-title {
  font-size: 18px;
  font-weight: 800;
  color: rgba(0, 0, 0, 0.86);
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

/* 表单 */
.login-form {
  margin-top: 8px;
}

/* 让 el-form label 更清爽 */
.login-form :deep(.el-form-item__label) {
  font-weight: 700;
  color: rgba(0, 0, 0, 0.72);
  padding-bottom: 6px;
}

/* 输入框外观微调 */
.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 0 12px;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.08) inset;
  transition: box-shadow 0.16s ease, transform 0.16s ease;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.25) inset;
}

/* 按钮 */
.login-btn {
  width: 100%;
  height: 44px;
  margin-top: 6px;
  border-radius: 12px;
  font-weight: 800;
  letter-spacing: 1px;
  box-shadow: 0 12px 22px rgba(64, 158, 255, 0.22);
  transition: transform 0.16s ease, box-shadow 0.16s ease;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 16px 28px rgba(64, 158, 255, 0.26);
}

/* 提示 */
.tips {
  margin-top: 12px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.64);
}

.muted {
  color: #909399;
}

/* 页脚 */
.footer {
  position: absolute;
  bottom: 14px;
  left: 0;
  right: 0;
  text-align: center;
  z-index: 1;
  font-size: 12px;
}
</style>
