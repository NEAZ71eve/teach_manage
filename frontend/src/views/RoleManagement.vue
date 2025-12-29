<template>
  <div class="role-management">
    <h2>角色管理</h2>
    <div class="operation-bar">
      <el-button type="primary" @click="openAddDialog">新增角色</el-button>
    </div>
    <el-table :data="roles" style="width: 100%">
      <el-table-column prop="roleId" label="角色ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="120" />
      <el-table-column prop="roleDesc" label="角色描述" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="danger"
            size="small"
            @click="deleteRole(scope.row.roleId)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="角色名称" required>
          <el-input
            v-model="formData.roleName"
            placeholder="请输入角色名称（如：计算机教师、数学教师）"
          />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input
            v-model="formData.roleDesc"
            placeholder="请输入角色描述（如：负责计算机专业课程管理）"
            type="textarea"
            rows="3"
          />
        </el-form-item>
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
import { ref, onMounted, computed, watch } from "vue";
import { ElMessage, ElNotification } from "element-plus";
import {
  getRoles,
  addRole,
  updateRole,
  deleteRole as deleteRoleApi,
  assignPrograms,
  getRolePrograms,
  getAllRolePrograms,
} from "../api/role";
import { getAllTrainingPrograms } from "../api/trainingProgram";

const roles = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref("新增角色");
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
  return formData.value.roleName && formData.value.roleName.includes("教师");
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

// 获取所有角色-专业关联
const loadAllRolePrograms = async () => {
  try {
    const response = await getAllRolePrograms();
    rolePrograms.value = response;
  } catch (error) {
    console.error("获取角色-专业关联失败:", error);
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

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = "新增角色";
  formData.value = {
    roleId: null,
    roleName: "",
    roleDesc: "",
    programIds: [],
  };
  dialogVisible.value = true;
  loadAllRolePrograms();
};

// 打开编辑对话框
const openEditDialog = async (role) => {
  dialogTitle.value = "编辑角色";
  formData.value = {
    ...role,
    programIds: [],
  };
  dialogVisible.value = true;

  // 获取所有角色-专业关联
  loadAllRolePrograms();

  // 获取角色关联的专业
  try {
    const rolePrograms = await getRolePrograms(role.roleId);
    formData.value.programIds = rolePrograms.map((rp) => rp.programId);
  } catch (error) {
    console.error("获取角色关联的专业失败:", error);
  }
};

// 保存角色
const saveRole = async () => {
  try {
    let roleId;
    if (formData.value.roleId) {
      // 更新角色
      await updateRole(formData.value.roleId, {
        roleName: formData.value.roleName,
        roleDesc: formData.value.roleDesc,
      });
      roleId = formData.value.roleId;
    } else {
      // 新增角色
      const result = await addRole({
        roleName: formData.value.roleName,
        roleDesc: formData.value.roleDesc,
      });
      roleId = result;
    }

    // 分配专业
    await assignPrograms(roleId, formData.value.programIds);

    ElMessage.success(formData.value.roleId ? "更新角色成功" : "新增角色成功");
    dialogVisible.value = false;
    loadRoles();
    loadAllRolePrograms();
  } catch (error) {
    let errorMsg = formData.value.roleId ? "更新角色失败" : "新增角色失败";
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || errorMsg;
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
    console.error("保存角色失败:", error);
  }
};

// 删除角色
const deleteRole = async (roleId) => {
  try {
    const response = await deleteRoleApi(roleId);
    if (response > 0) {
      ElMessage.success("删除角色成功");
      loadRoles();
      loadAllRolePrograms();
    } else {
      ElMessage.error("删除角色失败");
    }
  } catch (error) {
    ElMessage.error("删除角色失败");
    console.error("删除角色失败:", error);
  }
};

// 页面加载时获取角色列表和专业列表
onMounted(() => {
  loadRoles();
  loadPrograms();
  loadAllRolePrograms();
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
