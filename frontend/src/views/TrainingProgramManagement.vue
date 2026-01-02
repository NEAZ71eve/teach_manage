<template>
  <div class="training-program-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>培养方案列表</span>
          <div class="header-actions">
            <el-button
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedProgramIds.length === 0"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="handleAddProgram">
              <el-icon><Plus /></el-icon>
              添加培养方案
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="programs"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="programId" label="方案ID" width="80" />
        <el-table-column prop="majorName" label="专业名称" width="200" />
        <el-table-column prop="duration" label="学制" width="80" />
        <el-table-column prop="totalCredit" label="总学分" width="80" />
        <el-table-column prop="effectiveYear" label="生效年份" width="120" />
        <el-table-column prop="description" label="培养方案描述" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditProgram(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleManageCourses(scope.row)"
            >
              <el-icon><Menu /></el-icon>
              管理课程
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteProgram(scope.row.programId)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
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

    <!-- 培养方案表单对话框 -->
    <el-dialog v-model="dialogVisible" title="培养方案信息" width="600px">
      <el-form :model="programForm" label-width="120px">
        <el-form-item label="专业名称" required>
          <el-input
            v-model="programForm.majorName"
            placeholder="请输入专业名称"
          />
        </el-form-item>
        <el-form-item label="学制" required>
          <el-input
            v-model.number="programForm.duration"
            placeholder="请输入学制（年）"
            type="number"
            min="1"
            max="8"
          />
        </el-form-item>
        <el-form-item label="总学分" required>
          <el-input
            v-model.number="programForm.totalCredit"
            placeholder="请输入总学分要求"
            type="number"
            step="0.5"
            min="0"
          />
        </el-form-item>
        <el-form-item label="生效年份" required>
          <el-input
            v-model.number="programForm.effectiveYear"
            placeholder="请输入生效年份"
            type="number"
            min="2000"
            max="2100"
          />
        </el-form-item>
        <el-form-item label="培养方案描述">
          <el-input
            v-model="programForm.description"
            placeholder="请输入培养方案描述"
            type="textarea"
            rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveProgram">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 课程管理对话框 -->
    <el-dialog
      v-model="courseDialogVisible"
      :title="`${currentProgram.majorName} - 课程管理`"
      width="900px"
    >
      <div class="course-management-container">
        <!-- 课程列表 -->
        <el-card class="mb-4">
          <template #header>
            <div class="card-header">
              <span>课程列表</span>
              <el-button type="primary" size="small" @click="handleAddCourse">
                <el-icon><Plus /></el-icon>
                添加课程
              </el-button>
            </div>
          </template>
          <el-table :data="programCourses" border stripe>
            <el-table-column prop="courseId" label="课程ID" width="80" />
            <el-table-column prop="courseName" label="课程名称" width="200" />
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="courseNature" label="课程性质" width="120" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEditCourse(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteCourse(scope.row.courseId)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 课程学期安排 -->
        <el-card class="mb-4">
          <template #header>
            <div class="card-header">
              <span>课程学期安排</span>
            </div>
          </template>
          <el-steps :active="activeStep" simple>
            <el-step v-for="i in 8" :key="i" :title="`第${i}学期`" />
          </el-steps>
          <div class="mt-4">
            <el-select
              v-model="selectedSemester"
              placeholder="选择学期"
              style="width: 200px; margin-right: 10px"
            >
              <el-option
                v-for="i in 8"
                :key="i"
                :label="`第${i}学期`"
                :value="i"
              />
            </el-select>
            <el-button type="primary" @click="handleViewSemesterCourses"
              >查看课程</el-button
            >
          </div>

          <!-- 学期课程列表 -->
          <el-table
            v-if="semesterCourses.length > 0"
            :data="semesterCourses"
            border
            stripe
            class="mt-4"
          >
            <el-table-column prop="courseName" label="课程名称" width="200" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="courseNature" label="课程性质" width="120" />
            <el-table-column prop="teacherIds" label="授课教师" width="150" />
          </el-table>
          <el-empty v-else description="该学期暂无课程" class="mt-4" />
        </el-card>

        <!-- 导出完整课程安排 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>完整课程安排</span>
              <div class="header-actions">
                <el-button type="success" @click="handleExportFullSchedule">
                  <el-icon><Download /></el-icon>
                  导出Excel
                </el-button>
                <el-button
                  type="primary"
                  @click="showFullScheduleTable = !showFullScheduleTable"
                >
                  <el-icon><View /></el-icon>
                  {{ showFullScheduleTable ? "隐藏表格" : "显示表格" }}
                </el-button>
              </div>
            </div>
          </template>

          <!-- 完整课程安排表格 -->
          <el-table
            v-if="showFullScheduleTable"
            :data="fullScheduleTableData"
            border
            stripe
            style="margin-bottom: 20px"
          >
            <el-table-column prop="semester" label="学期" width="100" />
            <el-table-column prop="courseName" label="课程名称" width="200" />
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column
              prop="theoreticalHours"
              label="理论学时"
              width="100"
            />
            <el-table-column
              prop="practicalHours"
              label="实践学时"
              width="100"
            />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="courseNature" label="课程性质" width="120" />
            <el-table-column prop="examMark" label="考核方式" width="100" />
            <el-table-column
              prop="courseCategory"
              label="课程类别"
              width="120"
            />
            <el-table-column prop="teacherIds" label="授课教师" width="150" />
          </el-table>

          <div class="text-center mt-4">
            <el-button
              type="success"
              size="large"
              @click="handleExportFullSchedule"
            >
              <el-icon><Download /></el-icon>
              导出四年八个学期完整课程安排
            </el-button>
          </div>
        </el-card>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 课程表单对话框 -->
    <el-dialog
      v-model="courseFormVisible"
      :title="courseForm.courseId ? '编辑课程' : '添加课程'"
      width="600px"
    >
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
        <el-form-item label="学分" required>
          <el-input
            v-model.number="courseForm.credit"
            placeholder="请输入学分"
            type="number"
            step="0.5"
            min="0"
          />
        </el-form-item>
        <el-form-item label="总学时" required>
          <el-input
            v-model.number="courseForm.totalHours"
            placeholder="请输入总学时"
            type="number"
            min="1"
          />
        </el-form-item>
        <el-form-item label="理论学时" required>
          <el-input
            v-model.number="courseForm.theoreticalHours"
            placeholder="请输入理论学时"
            type="number"
            min="0"
          />
        </el-form-item>
        <el-form-item label="实践学时" required>
          <el-input
            v-model.number="courseForm.practicalHours"
            placeholder="请输入实践学时"
            type="number"
            min="0"
          />
        </el-form-item>
        <el-form-item label="课程类型" required>
          <el-select
            v-model="courseForm.courseType"
            placeholder="请选择课程类型"
          >
            <el-option label="公共课" value="公共课" />
            <el-option label="基础课" value="基础课" />
            <el-option label="专业课" value="专业课" />
            <el-option label="选修课" value="选修课" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程性质" required>
          <el-select
            v-model="courseForm.courseNature"
            placeholder="请选择课程性质"
          >
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核方式" required>
          <el-select v-model="courseForm.examMark" placeholder="请选择考核方式">
            <el-option label="考试" value="考试" />
            <el-option label="考查" value="考查" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类别" required>
          <el-select
            v-model="courseForm.courseCategory"
            placeholder="请选择课程类别"
          >
            <el-option label="通识教育" value="通识教育" />
            <el-option label="专业教育" value="专业教育" />
            <el-option label="实践教学" value="实践教学" />
          </el-select>
        </el-form-item>
        <el-form-item label="授课教师">
          <el-input
            v-model="courseForm.teacherIds"
            placeholder="请输入授课教师ID，多个用逗号分隔"
          />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input
            v-model="courseForm.description"
            placeholder="请输入课程描述"
            type="textarea"
            rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseFormVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveCourse">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import { Plus, Edit, Delete, Menu, Download } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getTrainingPrograms,
  addTrainingProgram,
  updateTrainingProgram,
  deleteTrainingProgram,
  deleteProgramBatch,
} from "../api/trainingProgram";
import {
  getCoursesByProgram,
  addCourse,
  updateCourse,
  deleteCourse,
  getProgramFullSchedule,
} from "../api/course";

// 表格数据
const programs = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedProgramIds = ref([]);

// 对话框
const dialogVisible = ref(false);
const programForm = reactive({
  programId: null,
  majorName: "",
  duration: 4,
  totalCredit: 160,
  effectiveYear: new Date().getFullYear(),
  description: "",
});

// 课程管理相关状态
const courseDialogVisible = ref(false);
const currentProgram = reactive({});
const programCourses = ref([]);

// 课程学期安排相关状态
const activeStep = ref(0);
const selectedSemester = ref(1);
const semesterCourses = ref([]);

// 完整课程安排表格相关状态
const showFullScheduleTable = ref(false);
const fullScheduleTableData = ref([]);

// 课程表单相关状态
const courseFormVisible = ref(false);
const courseForm = reactive({
  courseId: null,
  courseName: "",
  courseCode: "",
  programId: null,
  credit: 3.0,
  totalHours: 48,
  theoreticalHours: 32,
  practicalHours: 16,
  courseType: "专业课",
  courseNature: "必修",
  examMark: "考试",
  courseCategory: "专业教育",
  teacherIds: "",
  description: "",
});

// 获取培养方案列表
const fetchPrograms = async () => {
  try {
    const response = await getTrainingPrograms(
      currentPage.value,
      pageSize.value
    );
    programs.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error("获取培养方案列表失败");
    console.error("获取培养方案列表失败:", error);
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchPrograms();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchPrograms();
};

// 选择处理
const handleSelectionChange = (selection) => {
  selectedProgramIds.value = selection.map((item) => item.programId);
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedProgramIds.value.length} 条培养方案记录吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deleteProgramBatch(selectedProgramIds.value);
    ElMessage.success("批量删除成功");
    selectedProgramIds.value = [];
    fetchPrograms();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
      console.error("批量删除失败:", error);
    }
  }
};

// 添加培养方案
const handleAddProgram = () => {
  Object.assign(programForm, {
    programId: null,
    majorName: "",
    duration: 4,
    totalCredit: 160,
    effectiveYear: new Date().getFullYear(),
    description: "",
  });
  dialogVisible.value = true;
};

// 编辑培养方案
const handleEditProgram = (row) => {
  Object.assign(programForm, row);
  dialogVisible.value = true;
};

// 保存培养方案
const handleSaveProgram = async () => {
  try {
    console.log("保存培养方案数据:", programForm);
    // 移除空的description字段，避免后端处理问题
    const formData = { ...programForm };
    if (!formData.description) {
      delete formData.description;
    }
    if (formData.programId) {
      // 更新培养方案
      await updateTrainingProgram(formData.programId, formData);
      ElMessage.success("培养方案更新成功");
    } else {
      // 添加培养方案
      const response = await addTrainingProgram(formData);
      console.log("添加培养方案响应:", response);
      ElMessage.success("培养方案添加成功");
    }
    dialogVisible.value = false;
    fetchPrograms();
  } catch (error) {
    console.error("保存培养方案失败详细信息:", error);
    console.error("错误状态:", error.response?.status);
    console.error("错误数据:", error.response?.data);
    ElMessage.error(
      programForm.programId ? "培养方案更新失败" : "培养方案添加失败"
    );
    console.error("保存培养方案失败:", error);
  }
};

// 删除培养方案
const handleDeleteProgram = async (programId) => {
  try {
    await ElMessageBox.confirm("确定要删除该培养方案记录吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteTrainingProgram(programId);
    ElMessage.success("培养方案删除成功");
    fetchPrograms();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("培养方案删除失败");
      console.error("删除培养方案失败:", error);
    }
  }
};

// 管理课程
const handleManageCourses = async (program) => {
  Object.assign(currentProgram, program);
  await fetchProgramCourses(program.programId);
  courseDialogVisible.value = true;
};

// 获取培养方案的课程列表
const fetchProgramCourses = async (programId) => {
  try {
    const courses = await getCoursesByProgram(programId);
    programCourses.value = courses;
  } catch (error) {
    ElMessage.error("获取课程列表失败");
    console.error("获取课程列表失败:", error);
  }
};

// 添加课程
const handleAddCourse = () => {
  Object.assign(courseForm, {
    courseId: null,
    courseName: "",
    courseCode: "",
    programId: currentProgram.programId,
    credit: 3.0,
    totalHours: 48,
    theoreticalHours: 32,
    practicalHours: 16,
    courseType: "专业课",
    courseNature: "必修",
    examMark: "考试",
    courseCategory: "专业教育",
    teacherIds: "",
    description: "",
  });
  courseFormVisible.value = true;
};

// 编辑课程
const handleEditCourse = (course) => {
  Object.assign(courseForm, course);
  courseFormVisible.value = true;
};

// 保存课程
const handleSaveCourse = async () => {
  try {
    if (courseForm.courseId) {
      // 更新课程
      await updateCourse(courseForm.courseId, courseForm);
      ElMessage.success("课程更新成功");
    } else {
      // 添加课程
      await addCourse(courseForm);
      ElMessage.success("课程添加成功");
    }
    courseFormVisible.value = false;
    await fetchProgramCourses(currentProgram.programId);
  } catch (error) {
    ElMessage.error(courseForm.courseId ? "课程更新失败" : "课程添加失败");
    console.error("保存课程失败:", error);
  }
};

// 删除课程
const handleDeleteCourse = async (courseId) => {
  try {
    await ElMessageBox.confirm("确定要删除该课程吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteCourse(courseId);
    ElMessage.success("课程删除成功");
    await fetchProgramCourses(currentProgram.programId);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("课程删除失败");
      console.error("删除课程失败:", error);
    }
  }
};

// 查看学期课程
const handleViewSemesterCourses = async () => {
  activeStep.value = selectedSemester.value - 1;
  try {
    const schedule = await getProgramFullSchedule(currentProgram.programId);
    semesterCourses.value = schedule[selectedSemester.value] || [];
  } catch (error) {
    ElMessage.error("获取学期课程失败");
    console.error("获取学期课程失败:", error);
  }
};

// 导出完整课程安排
const handleExportFullSchedule = async () => {
  try {
    const schedule = await getProgramFullSchedule(currentProgram.programId);

    // 生成表格数据
    const tableData = [];
    for (let semester = 1; semester <= 8; semester++) {
      const courses = schedule[semester] || [];
      courses.forEach((course) => {
        tableData.push({
          semester: `第${semester}学期`,
          courseName: course.courseName,
          courseCode: course.courseCode,
          credit: course.credit,
          totalHours: course.totalHours,
          theoreticalHours: course.theoreticalHours,
          practicalHours: course.practicalHours,
          courseType: course.courseType,
          courseNature: course.courseNature,
          examMark: course.examMark,
          courseCategory: course.courseCategory,
          teacherIds: course.teacherIds,
        });
      });
    }

    fullScheduleTableData.value = tableData;
    showFullScheduleTable.value = true;

    // 这里可以添加导出Excel的逻辑
    ElMessage.success("完整课程安排已生成");
    console.log("完整课程安排表格数据:", tableData);
  } catch (error) {
    ElMessage.error("导出完整课程安排失败");
    console.error("导出完整课程安排失败:", error);
  }
};

// 页面挂载时获取培养方案列表
onMounted(() => {
  fetchPrograms();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.course-management-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.text-center {
  text-align: center;
}

/* 确保步骤组件正确显示 */
:deep(.el-steps) {
  margin-bottom: 20px;
}

/* 确保学期选择器和按钮正确排列 */
.semester-selection {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
</style>
