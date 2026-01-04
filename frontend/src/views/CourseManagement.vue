<template>
  <div class="course-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <!-- 标题区：按你要求的风格（标题 + 副标题） -->
          <div class="title-wrap">
            <div class="page-title">课程列表</div>
            <div class="page-subtitle">
              支持课程搜索、批量操作与课程信息维护
            </div>
          </div>

          <!-- 子选项卡/操作区：仅排版美化（el-space），内部控件不改 -->
          <div class="header-actions">
            <el-space :size="10" wrap>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索课程名称或代码"
                style="width: 300px; margin-right: 10px"
                clearable
                @keyup.enter="handleSearch"
              >
                <template #append>
                  <el-button @click="handleSearch">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>

              <el-button
                v-if="hasPermission('course:delete')"
                type="danger"
                @click="handleBatchDelete"
                :disabled="selectedCourseIds.length === 0"
              >
                <el-icon><Delete /></el-icon>
                批量删除
              </el-button>

              <el-button
                v-if="hasPermission('course:add')"
                type="primary"
                @click="handleAddCourse"
              >
                <el-icon><Plus /></el-icon>
                添加课程
              </el-button>
            </el-space>
          </div>
        </div>
      </template>

      <el-table
        :data="courses"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="courseId" label="课程ID" width="65" />
        <el-table-column prop="courseName" label="课程名称" width="200" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="majorId" label="专业ID" width="80" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="totalHours" label="总学时" width="80" />
        <el-table-column prop="theoreticalHours" label="理论学时" width="80" />
        <el-table-column prop="practicalHours" label="实践学时" width="80" />
        <el-table-column prop="courseType" label="课程类型" width="80" />
        <el-table-column prop="courseNature" label="课程性质" width="80" />
        <el-table-column prop="examMark" label="考试标记" width="80" />
        <el-table-column prop="courseCategory" label="课程分类" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-space :size="6">
              <el-button
                v-if="hasPermission('course:edit')"
                type="primary"
                size="small"
                @click="handleEditCourse(scope.row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>

              <el-button
                v-if="hasPermission('course:delete')"
                type="danger"
                size="small"
                @click="handleDeleteCourse(scope.row.courseId)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </el-space>
          </template>
        </el-table-column>

        <!--        <el-table-column label="操作" width="150" fixed="right">-->
        <!--          <template #default="scope">-->
        <!--            <el-button-->
        <!--              v-if="hasPermission('course:edit')"-->
        <!--              type="primary"-->
        <!--              size="small"-->
        <!--              @click="handleEditCourse(scope.row)"-->
        <!--            >-->
        <!--              <el-icon><Edit /></el-icon>-->
        <!--              编辑-->
        <!--            </el-button>-->
        <!--            <el-button-->
        <!--              v-if="hasPermission('course:delete')"-->
        <!--              type="danger"-->
        <!--              size="small"-->
        <!--              @click="handleDeleteCourse(scope.row.courseId)"-->
        <!--            >-->
        <!--              <el-icon><Delete /></el-icon>-->
        <!--              删除-->
        <!--            </el-button>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
      </el-table>

      <div class="pagination mt-4">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 课程表单对话框 -->
    <el-dialog v-model="dialogVisible" title="课程信息" width="600px">
      <el-form :model="courseForm" label-width="120px">
        <el-form-item label="课程名称" required>
          <el-input
            v-model="courseForm.courseName"
            placeholder="请输入课程名称"
          />
        </el-form-item>
        <el-form-item label="课程代码" required>
          <el-input
            v-model="courseForm.courseCode"
            placeholder="请输入课程代码"
          />
        </el-form-item>
        <el-form-item label="专业ID" required>
          <el-input
            v-model.number="courseForm.majorId"
            placeholder="请输入专业ID"
            type="number"
          />
        </el-form-item>
        <el-form-item label="培养方案ID">
          <el-select
            v-model="courseForm.programId"
            placeholder="请选择培养方案"
          >
            <el-option
              v-for="program in trainingPrograms"
              :key="program.programId"
              :label="`${program.majorName}(${program.effectiveYear})`"
              :value="program.programId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学分" required>
          <el-input
            v-model.number="courseForm.credit"
            placeholder="请输入学分"
            type="number"
            step="0.5"
          />
        </el-form-item>
        <el-form-item label="总学时" required>
          <el-input
            v-model.number="courseForm.totalHours"
            placeholder="请输入总学时"
            type="number"
            @change="calculatePracticalHours"
          />
        </el-form-item>
        <el-form-item label="理论学时" required>
          <el-input
            v-model.number="courseForm.theoreticalHours"
            placeholder="请输入理论学时"
            type="number"
            @change="calculatePracticalHours"
          />
        </el-form-item>
        <el-form-item label="实践学时" required>
          <el-input
            v-model.number="courseForm.practicalHours"
            placeholder="请输入实践学时"
            type="number"
            @change="calculateTheoreticalHours"
          />
        </el-form-item>
        <el-form-item label="课程类型" required>
          <el-select
            v-model="courseForm.courseType"
            placeholder="请选择课程类型"
          >
            <el-option label="必修课" value="必修课" />
            <el-option label="选修课" value="选修课" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程性质" required>
          <el-select
            v-model="courseForm.courseNature"
            placeholder="请选择课程性质"
          >
            <el-option label="公共基础课" value="公共基础课" />
            <el-option label="专业基础课" value="专业基础课" />
            <el-option label="专业课" value="专业课" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试标记">
          <el-input
            v-model="courseForm.examMark"
            placeholder="请输入考试标记"
          />
        </el-form-item>
        <el-form-item label="课程分类">
          <el-select
            v-model="courseForm.courseCategory"
            placeholder="请选择课程分类"
          >
            <el-option label="公共基础课" value="公共基础课" />
            <el-option label="专业基础课" value="专业基础课" />
            <el-option label="专业课" value="专业课" />
            <el-option label="选修课" value="选修课" />
            <el-option label="实践课" value="实践课" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            placeholder="请输入课程描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="关联学期">
          <el-select
            v-model="selectedSemesterIds"
            placeholder="请选择关联学期"
            multiple
            collapse-tags
            style="width: 100%"
          >
            <el-option
              v-for="semester in semesters"
              :key="semester.semesterId"
              :label="semester.semesterName"
              :value="semester.semesterId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveCourse">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import { Plus, Edit, Delete, Search } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getCourses,
  addCourse,
  updateCourse,
  deleteCourse,
  deleteBatch,
  searchCourses,
  getAllSemesters,
  getCourseSemesters,
  saveCourseSemester,
} from "../api/course";
import { getAllTrainingPrograms } from "../api/trainingProgram";

// 获取用户权限
const userPermissions = ref([]);

// 检查用户是否有特定权限
const hasPermission = (permissionCode) => {
  // 获取权限列表
  const permissions = JSON.parse(localStorage.getItem("permissions") || "[]");
  const permissionCodes = permissions.map((p) => p.permissionCode);

  // 获取角色列表
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");
  const roleNames = roles.map((role) =>
    typeof role === "string" ? role : role.roleName
  );

  const isProgramTeacher =
    roleNames.includes("学院管理员") || roleNames.includes("专业负责教师");

  // 课程管理相关权限，教师角色和专业负责教师角色默认拥有
  const coursePermissions = [
    "course:add",
    "course:edit",
    "course:delete",
    "course:list",
  ];

  // 如果是课程管理相关权限，且为教师角色或专业负责教师角色，则返回true
  if (coursePermissions.includes(permissionCode) && isProgramTeacher) {
    return true;
  }

  // 其他权限按正常权限检查
  return permissionCodes.includes(permissionCode);
};

// 表格数据
const courses = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchKeyword = ref("");
const selectedCourseIds = ref([]);

// 学期数据
const semesters = ref([]);
const selectedSemesterIds = ref([]);

// 培养方案数据
const trainingPrograms = ref([]);

// 对话框
const dialogVisible = ref(false);
const courseForm = reactive({
  courseId: null,
  courseName: "",
  courseCode: "",
  majorId: 1,
  programId: null,
  credit: 3,
  totalHours: 48,
  theoreticalHours: 32,
  practicalHours: 16,
  courseType: "必修课",
  courseNature: "专业基础课",
  examMark: "",
  courseCategory: "",
  description: "",
});

// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await getCourses(currentPage.value, pageSize.value);
    courses.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error("获取课程列表失败");
    console.error("获取课程列表失败:", error);
  }
};

// 获取所有学期
const fetchSemesters = async () => {
  try {
    const response = await getAllSemesters();
    semesters.value = response;
  } catch (error) {
    ElMessage.error("获取学期列表失败");
    console.error("获取学期列表失败:", error);
  }
};

// 获取所有培养方案
const fetchTrainingPrograms = async () => {
  try {
    const response = await getAllTrainingPrograms();
    trainingPrograms.value = response;
  } catch (error) {
    ElMessage.error("获取培养方案列表失败");
    console.error("获取培养方案列表失败:", error);
  }
};

// 获取课程关联的学期
const fetchCourseSemesters = async (courseId) => {
  try {
    const response = await getCourseSemesters(courseId);
    selectedSemesterIds.value = response;
  } catch (error) {
    ElMessage.error("获取课程关联学期失败");
    console.error("获取课程关联学期失败:", error);
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchCourses();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchCourses();
};

// 搜索处理
const handleSearch = async () => {
  if (searchKeyword.value) {
    try {
      const response = await searchCourses(searchKeyword.value);
      courses.value = response;
      total.value = response.length;
    } catch (error) {
      ElMessage.error("搜索课程失败");
      console.error("搜索课程失败:", error);
    }
  } else {
    // 如果搜索关键词为空，重新加载所有课程
    fetchCourses();
  }
};

// 选择处理
const handleSelectionChange = (selection) => {
  selectedCourseIds.value = selection.map((item) => item.courseId);
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedCourseIds.value.length} 条课程记录吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deleteBatch(selectedCourseIds.value);
    ElMessage.success("批量删除成功");
    selectedCourseIds.value = [];
    fetchCourses();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
      console.error("批量删除失败:", error);
    }
  }
};

// 添加课程
const handleAddCourse = () => {
  Object.assign(courseForm, {
    courseId: null,
    courseName: "",
    courseCode: "",
    majorId: 1,
    programId: null,
    credit: 3,
    totalHours: 48,
    theoreticalHours: 32,
    practicalHours: 16,
    courseType: "必修课",
    courseNature: "专业基础课",
    examMark: "",
    courseCategory: "",
    description: "",
  });
  selectedSemesterIds.value = [];
  dialogVisible.value = true;
};

// 编辑课程
const handleEditCourse = async (row) => {
  Object.assign(courseForm, row);
  // 获取课程关联的学期
  await fetchCourseSemesters(row.courseId);
  dialogVisible.value = true;
};

// 自动计算总学时
const calculateTotalHours = () => {
  courseForm.totalHours =
    courseForm.theoreticalHours + courseForm.practicalHours;
};

// 自动计算实践学时
const calculatePracticalHours = () => {
  courseForm.practicalHours =
    courseForm.totalHours - courseForm.theoreticalHours;
};

// 自动计算理论学时
const calculateTheoreticalHours = () => {
  courseForm.theoreticalHours =
    courseForm.totalHours - courseForm.practicalHours;
};

// 保存课程
const handleSaveCourse = async () => {
  try {
    // 验证学时关系
    if (
      courseForm.theoreticalHours + courseForm.practicalHours !==
      courseForm.totalHours
    ) {
      ElMessage.error("理论学时+实践学时必须等于总学时");
      return;
    }

    if (courseForm.courseId) {
      // 更新课程
      await updateCourse(courseForm.courseId, courseForm);
      // 保存课程学期关联
      await saveCourseSemester(courseForm.courseId, selectedSemesterIds.value);
      ElMessage.success("课程更新成功");
    } else {
      // 添加课程
      await addCourse(courseForm);
      // 获取新添加的课程ID（这里简化处理，实际应该从返回结果中获取）
      // 先重新获取课程列表，然后找到最新添加的课程
      await fetchCourses();
      const newCourse = courses.value.find(
        (c) => c.courseCode === courseForm.courseCode
      );
      if (newCourse) {
        await saveCourseSemester(newCourse.courseId, selectedSemesterIds.value);
      }
      ElMessage.success("课程添加成功");
    }
    dialogVisible.value = false;
    fetchCourses();
  } catch (error) {
    ElMessage.error(courseForm.courseId ? "课程更新失败" : "课程添加失败");
    console.error("保存课程失败:", error);
    console.error("错误详情:", error.response?.data || error.message);
  }
};

// 删除课程
const handleDeleteCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm("确定要删除该课程记录吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteCourse(courseId);
    ElMessage.success("课程删除成功");
    fetchCourses();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("课程删除失败");
      console.error("删除课程失败:", error);
    }
  }
};

// 页面挂载时获取课程列表、学期列表和培养方案列表
onMounted(async () => {
  await Promise.all([
    fetchCourses(),
    fetchSemesters(),
    fetchTrainingPrograms(),
  ]);
});
</script>

<style scoped>
/* 原样保留 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 原样保留（但 header-actions 内部布局由 el-space 控制，不影响其它） */
.header-actions {
  display: flex;
  align-items: center;
}

/* 新增：标题与副标题风格（只影响 header 标题区） */
.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.page-title {
  font-size: 16px;
  font-weight: 900;
  color: rgba(0, 0, 0, 0.86);
  line-height: 1.2;
}

.page-subtitle {
  font-size: 12px;
  color: #909399;
  line-height: 1.2;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.table-action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-button {
  min-width: 64px;
  justify-content: center;
}
</style>
