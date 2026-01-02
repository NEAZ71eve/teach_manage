<template>
  <div class="role-management">
    <h2>角色管理</h2>
    <div class="operation-bar">
      <!-- 隐藏新增角色按钮，角色是固定的 -->
    </div>
    <el-table :data="roles" style="width: 100%">
      <el-table-column prop="roleId" label="角色ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="120" />
      <el-table-column prop="roleDesc" label="角色描述" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <!-- 只允许编辑教师角色 -->
          <el-button
            v-if="scope.row.roleName === '教师'"
            type="primary"
            size="small"
            @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <!-- 隐藏删除角色按钮，角色是固定的 -->
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <!-- 角色名称只读，不允许修改 -->
        <el-form-item label="角色名称" required>
          <el-input v-model="formData.roleName" readonly disabled />
        </el-form-item>
        <!-- 角色描述只读，不允许修改 -->
        <el-form-item label="角色描述">
          <el-input
            v-model="formData.roleDesc"
            readonly
            disabled
            type="textarea"
            rows="3"
          />
        </el-form-item>
        <!-- 只有教师角色才显示专业选择 -->
        <el-form-item label="负责专业" v-if="isTeacherRole">
          <div class="help-text" style="margin-bottom: 10px; color: #606266">
            提示：可多选，为该角色分配可管理的专业。已绑定的专业会显示绑定角色名称。
          </div>
          <el-select
            v-model="formData.programIds"
            multiple
            placeholder="请选择该角色可管理的专业（培养方案）"
            style="width: 100%"
          >
            <el-option
              v-for="program in programs"
              :key="program.programId"
              :label="getProgramLabel(program)"
              :value="program.programId"
              :disabled="isProgramDisabled(program.programId)"
            >
              <div
                style="
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                "
              >
                <span>{{ program.majorName }}</span>
                <span
                  v-if="getProgramBoundStatus(program.programId)"
                  style="color: #909399; font-size: 12px"
                >
                  (已绑定: {{ getProgramBoundStatus(program.programId) }})
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRole">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import {
  getRoles,
  updateRole,
  assignPrograms,
  getRolePrograms,
} from "../api/role";
import { getAllTrainingPrograms } from "../api/trainingProgram";

const roles = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref("编辑角色");
const formData = ref({
  roleId: null,
  roleName: "",
  roleDesc: "",
  programIds: [],
});

const programs = ref([]);
const rolePrograms = ref([]);

// 判断是否为教师角色
const isTeacherRole = computed(() => {
  return formData.value.roleName === "教师";
});

// 获取角色列表
const loadRoles = async () => {
  try {
    const response = await getRoles();
    roles.value = response;
  } catch (error) {
    ElMessage.error("获取角色列表失败");
    console.error("获取角色列表失败:", error);
  }
};

// 获取培养方案列表
const loadPrograms = async () => {
  try {
    const response = await getAllTrainingPrograms();
    programs.value = response;
  } catch (error) {
    ElMessage.error("获取专业列表失败");
    console.error("获取专业列表失败:", error);
  }
};

// 获取角色关联的专业
const loadRolePrograms = async (roleId) => {
  try {
    const response = await getRolePrograms(roleId);
    rolePrograms.value = response;
  } catch (error) {
    console.error("获取角色关联的专业失败:", error);
  }
};

// 获取专业的绑定状态
const getProgramBoundStatus = (programId) => {
  const bound = rolePrograms.value.find((rp) => rp.programId === programId);
  if (bound) {
    const role = roles.value.find((r) => r.roleId === bound.roleId);
    return role ? role.roleName : "未知角色";
  }
  return null;
};

// 获取专业的显示标签
const getProgramLabel = (program) => {
  const boundTo = getProgramBoundStatus(program.programId);
  if (boundTo) {
    return `${program.majorName} (已绑定: ${boundTo})`;
  }
  return program.majorName;
};

// 判断专业是否应该被禁用
const isProgramDisabled = (programId) => {
  // 如果专业已经被绑定到其他角色，并且当前角色不是绑定角色，则禁用
  const bound = rolePrograms.value.find((rp) => rp.programId === programId);
  return bound && bound.roleId !== formData.value.roleId;
};

// 打开编辑对话框
const openEditDialog = async (role) => {
  dialogTitle.value = "编辑角色";
  formData.value = {
    ...role,
    programIds: [],
  };
  dialogVisible.value = true;

  // 获取角色关联的专业
  try {
    const rolePrograms = await getRolePrograms(role.roleId);
    formData.value.programIds = rolePrograms.map((rp) => rp.programId);
    // 更新全局rolePrograms，用于判断专业绑定状态
    loadRolePrograms(role.roleId);
  } catch (error) {
    console.error("获取角色关联的专业失败:", error);
  }
};

// 保存角色
const saveRole = async () => {
  try {
    // 只执行分配专业操作，因为角色基本信息是只读的
    await assignPrograms(formData.value.roleId, formData.value.programIds);

    ElMessage.success("更新角色成功");
    dialogVisible.value = false;
    loadRoles();
    loadRolePrograms(formData.value.roleId);
  } catch (error) {
    let errorMsg = "更新角色失败";
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || errorMsg;
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
    console.error("保存角色失败:", error);
  }
};

// 页面加载时获取角色列表和专业列表
onMounted(() => {
  loadRoles();
  loadPrograms();
});
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
}
</style>
