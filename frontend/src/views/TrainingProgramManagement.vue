<template>
  <div class="training-program-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <!-- 仅改标题：标题 + 副标题（其余不动） -->
          <div class="title-wrap">
            <div class="page-title">培养方案列表</div>
            <div class="page-subtitle">
              维护专业培养方案信息，并支持课程与学期安排管理
            </div>
          </div>

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
        <el-table-column prop="effectiveYear" label="生效年份" width="80" />
        <el-table-column
          prop="description"
          label="培养方案描述"
          min-width="100"
        />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <div class="table-action-buttons">
              <el-button
                class="action-button"
                type="primary"
                size="small"
                @click="handleEditProgram(scope.row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                class="action-button"
                type="success"
                size="small"
                @click="handleManageCourses(scope.row)"
              >
                <el-icon><Menu /></el-icon>
                管理课程
              </el-button>
              <el-button
                class="action-button"
                type="danger"
                size="small"
                @click="handleDeleteProgram(scope.row.programId)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>

        <!--        <el-table-column label="操作" width="250" fixed="right">-->
        <!--          <template #default="scope">-->
        <!--            <el-button-->
        <!--              type="primary"-->
        <!--              size="small"-->
        <!--              @click="handleEditProgram(scope.row)"-->
        <!--            >-->
        <!--              <el-icon><Edit /></el-icon>-->
        <!--              编辑-->
        <!--            </el-button>-->
        <!--            <el-button-->
        <!--              type="success"-->
        <!--              size="small"-->
        <!--              @click="handleManageCourses(scope.row)"-->
        <!--            >-->
        <!--              <el-icon><Menu /></el-icon>-->
        <!--              管理课程-->
        <!--            </el-button>-->
        <!--            <el-button-->
        <!--              type="danger"-->
        <!--              size="small"-->
        <!--              @click="handleDeleteProgram(scope.row.programId)"-->
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
              <div class="header-actions">
                <el-button
                  v-if="shouldShowCourseToggle"
                  size="small"
                  @click="toggleCourseCollapse"
                >
                  <el-icon><View /></el-icon>
                  {{ showAllCourses ? "收起" : "展开" }}
                </el-button>
                <el-button type="primary" size="small" @click="handleAddCourse">
                  <el-icon><Plus /></el-icon>
                  添加课程
                </el-button>
              </div>
            </div>
          </template>
          <el-table :data="visibleProgramCourses" border stripe>
            <el-table-column prop="courseId" label="课程ID" width="80" />
            <el-table-column
              prop="courseName"
              label="课程名称"
              width="200"
              show-overflow-tooltip
            />
            <el-table-column
              prop="courseCode"
              label="课程代码"
              width="120"
              show-overflow-tooltip
            />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="courseNature" label="课程性质" width="120" />
            <el-table-column
              label="操作"
              width="110"
              fixed="right"
              align="center"
            >
              <template #default="scope">
                <el-space :size="6">
                  <el-tooltip content="编辑" placement="top">
                    <el-button
                      v-if="hasPermission?.('course:edit') ?? true"
                      circle
                      type="primary"
                      size="small"
                      @click="handleEditCourse(scope.row)"
                    >
                      <el-icon><Edit /></el-icon>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip content="删除" placement="top">
                    <el-button
                      v-if="hasPermission?.('course:delete') ?? true"
                      circle
                      type="danger"
                      size="small"
                      @click="handleDeleteCourse(scope.row.courseId)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-tooltip>
                </el-space>
              </template>
            </el-table-column>

            <!--            <el-table-column label="操作" width="150" fixed="right">-->
            <!--              <template #default="scope">-->
            <!--                <el-button-->
            <!--                  type="primary"-->
            <!--                  size="small"-->
            <!--                  @click="handleEditCourse(scope.row)"-->
            <!--                >-->
            <!--                  <el-icon><Edit /></el-icon>-->
            <!--                  编辑-->
            <!--                </el-button>-->
            <!--                <el-button-->
            <!--                  type="danger"-->
            <!--                  size="small"-->
            <!--                  @click="handleDeleteCourse(scope.row.courseId)"-->
            <!--                >-->
            <!--                  <el-icon><Delete /></el-icon>-->
            <!--                  删除-->
            <!--                </el-button>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
          </el-table>
        </el-card>

        <!-- 课程学期安排 -->
        <el-card class="mb-4">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span>课程学期安排</span>
                <span class="header-subtitle"
                  >按学期查看课程安排与统计数据</span
                >
              </div>
              <div class="header-actions">
                <el-tag effect="light" type="info"
                  >当前：第{{ selectedSemester }}学期</el-tag
                >
              </div>
            </div>
          </template>
          <el-steps :active="activeStep" simple class="semester-steps">
            <el-step v-for="i in 8" :key="i" :title="`第${i}学期`" />
          </el-steps>
          <div class="semester-toolbar">
            <div class="semester-picker">
              <span class="toolbar-label">选择学期</span>
              <el-select
                v-model="selectedSemester"
                placeholder="选择学期"
                style="width: 200px"
              >
                <el-option
                  v-for="i in 8"
                  :key="i"
                  :label="`第${i}学期`"
                  :value="i"
                />
              </el-select>
            </div>
            <el-button type="primary" @click="handleViewSemesterCourses">
              <el-icon><Search /></el-icon>
              查看课程
            </el-button>
          </div>

          <!-- 学期课程列表 -->
          <el-table
            v-if="semesterCourses.length > 0"
            :data="semesterCourses"
            border
            stripe
            class="mt-4 semester-course-table"
          >
            <el-table-column prop="courseName" label="课程名称" width="200" />
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="courseNature" label="课程性质" width="120" />
            <el-table-column label="授课教师" width="150">
              <template #default="scope">
                {{ formatTeacherNames(scope.row.teacherIds) }}
              </template>
            </el-table-column>
          </el-table>
          <div v-if="semesterStats" class="mt-4 semester-stats">
            <div class="stats-header">学期统计</div>
            <el-row :gutter="12" class="stats-grid">
              <el-col :span="8">
                <div class="stat-card">
                  <span class="stat-label">课程数量</span>
                  <span class="stat-value">{{
                    semesterStats.totalCourses
                  }}</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-card">
                  <span class="stat-label">总学分</span>
                  <span class="stat-value">{{
                    semesterStats.totalCredit
                  }}</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-card">
                  <span class="stat-label">总学时</span>
                  <span class="stat-value">{{ semesterStats.totalHours }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stat-card">
                  <span class="stat-label">理论学时</span>
                  <span class="stat-value">{{
                    semesterStats.totalTheoreticalHours
                  }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stat-card">
                  <span class="stat-label">实践学时</span>
                  <span class="stat-value">{{
                    semesterStats.totalPracticalHours
                  }}</span>
                </div>
              </el-col>
            </el-row>
            <div class="stats-tags">
              <div class="stat-group">
                <span class="stat-label">课程性质：</span>
                <el-tag
                  v-for="(count, key) in semesterStats.courseNatureCount || {}"
                  :key="`nature-${key}`"
                  class="stat-tag"
                  type="info"
                >
                  {{ key }} {{ count }}
                </el-tag>
              </div>
              <div class="stat-group">
                <span class="stat-label">课程类别：</span>
                <el-tag
                  v-for="(count, key) in semesterStats.courseCategoryCount ||
                  {}"
                  :key="`category-${key}`"
                  class="stat-tag"
                >
                  {{ key }} {{ count }}
                </el-tag>
              </div>
            </div>
          </div>
          <el-empty
            v-if="semesterCourses.length === 0 && !semesterStats"
            description="该学期暂无课程"
            class="mt-4"
          />
        </el-card>

        <!-- 导出完整课程安排 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span>完整课程安排</span>
                <span class="header-subtitle"
                  >覆盖四年八个学期的课程分布与统计</span
                >
              </div>
              <div class="header-actions">
                <el-button
                  type="success"
                  :loading="exportingFullSchedule"
                  @click="handleExportFullSchedule"
                >
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
                <el-button
                  v-if="showFullScheduleTable && shouldShowScheduleToggle"
                  @click="toggleScheduleCollapse"
                >
                  <el-icon><View /></el-icon>
                  {{ showAllScheduleRows ? "收起" : "展开" }}
                </el-button>
              </div>
            </div>
          </template>
          <div class="export-hint">
            生成课程安排后可直接下载为Excel格式（CSV），支持本地打开与归档。
          </div>

          <!-- 完整课程安排表格 -->
          <el-table
            v-if="showFullScheduleTable"
            :data="visibleFullScheduleTableData"
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
            <el-table-column label="授课教师" width="150">
              <template #default="scope">
                {{ formatTeacherNames(scope.row.teacherIds) }}
              </template>
            </el-table-column>
          </el-table>

          <div class="text-center mt-4">
            <el-button
              type="success"
              size="large"
              :loading="exportingFullSchedule"
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
          <el-select
            v-model="selectedTeacherIds"
            placeholder="请选择授课教师"
            multiple
            collapse-tags
            style="width: 100%"
          >
            <el-option
              v-for="teacher in teachers"
              :key="teacher.userId"
              :label="formatTeacherOption(teacher)"
              :value="String(teacher.userId)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input
            v-model="courseForm.description"
            placeholder="请输入课程描述"
            type="textarea"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="开设学期" required>
          <el-select
            v-model="courseForm.semesterIds"
            placeholder="请选择课程开设的学期"
            multiple
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="i in 8"
              :key="i"
              :label="`第${i}学期`"
              :value="i"
            />
          </el-select>
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
import { ref, onMounted, reactive, computed } from "vue";
import {
  Plus,
  Edit,
  Delete,
  Menu,
  Download,
  Search,
} from "@element-plus/icons-vue";
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
  getCourseStatisticsBySemester,
  getCourseSemesters,
  saveCourseSemester,
} from "../api/course";
import { getUsers } from "../api/user";

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
const semesterStats = ref(null);
const showAllCourses = ref(false);
const showAllScheduleRows = ref(false);
const courseCollapseLimit = 8;
const scheduleCollapseLimit = 12;
const exportingFullSchedule = ref(false);

// 完整课程安排表格相关状态
const showFullScheduleTable = ref(false);
const fullScheduleTableData = ref([]);
const visibleProgramCourses = computed(() => {
  if (showAllCourses.value) return programCourses.value;
  return programCourses.value.slice(0, courseCollapseLimit);
});
const shouldShowCourseToggle = computed(
  () => programCourses.value.length > courseCollapseLimit
);
const visibleFullScheduleTableData = computed(() => {
  if (showAllScheduleRows.value) return fullScheduleTableData.value;
  return fullScheduleTableData.value.slice(0, scheduleCollapseLimit);
});
const shouldShowScheduleToggle = computed(
  () => fullScheduleTableData.value.length > scheduleCollapseLimit
);

const toggleCourseCollapse = () => {
  showAllCourses.value = !showAllCourses.value;
};

const toggleScheduleCollapse = () => {
  showAllScheduleRows.value = !showAllScheduleRows.value;
};

const escapeCsvValue = (value) => {
  if (value === null || value === undefined) return "";
  const stringValue = String(value);
  if (
    stringValue.includes(",") ||
    stringValue.includes('"') ||
    stringValue.includes("\n")
  ) {
    return `"${stringValue.replace(/"/g, '""')}"`;
  }
  return stringValue;
};

const downloadCsv = (rows, filename) => {
  const content = rows
    .map((row) => row.map(escapeCsvValue).join(","))
    .join("\n");
  const blob = new Blob([`\ufeff${content}`], {
    type: "text/csv;charset=utf-8;",
  });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};

const buildFullScheduleTableData = (schedule) => {
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
  return tableData;
};

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
  semesterIds: [],
});

const teachers = ref([]);
const selectedTeacherIds = ref([]);

const parseTeacherIds = (teacherIds) => {
  if (!teacherIds) return [];
  return teacherIds
    .split(",")
    .map((id) => id.trim())
    .filter(Boolean);
};

const teacherNameMap = computed(() => {
  const map = {};
  teachers.value.forEach((teacher) => {
    map[String(teacher.userId)] = teacher.realName || teacher.username;
  });
  return map;
});

const formatTeacherOption = (teacher) => {
  const name = teacher.realName || teacher.username;
  return name ? `${name}（${teacher.username}）` : teacher.username;
};

const formatTeacherNames = (teacherIds) => {
  const ids = parseTeacherIds(teacherIds);
  if (ids.length === 0) return "-";
  return ids
    .map((id) => teacherNameMap.value[id] || `教师${id}`)
    .join("、");
};

// 获取培养方案列表
const fetchPrograms = async () => {
  try {
    // 获取当前用户信息
    const userStr = localStorage.getItem("user");
    const user = userStr ? JSON.parse(userStr) : null;

    const response = await getTrainingPrograms(
      currentPage.value,
      pageSize.value,
      user?.programId || null // 传递专业ID，只获取当前用户专业的培养方案
    );
    programs.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error("获取培养方案列表失败");
    console.error("获取培养方案列表失败:", error);
  }
};

const fetchTeachers = async () => {
  try {
    const response = await getUsers();
    teachers.value = (response || []).filter(
      (user) => user.roleName === "教师"
    );
  } catch (error) {
    ElMessage.error("获取教师列表失败");
    console.error("获取教师列表失败:", error);
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
  showAllCourses.value = false;
  showAllScheduleRows.value = false;
  semesterCourses.value = [];
  semesterStats.value = null;
  activeStep.value = 0;
  selectedSemester.value = 1;
  await fetchProgramCourses(program.programId);
  courseDialogVisible.value = true;
};

// 获取培养方案的课程列表
const fetchProgramCourses = async (programId) => {
  try {
    const courses = await getCoursesByProgram(programId);
    programCourses.value = courses;
    showAllCourses.value = false;
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
    semesterIds: [],
  });
  selectedTeacherIds.value = [];
  courseFormVisible.value = true;
};

// 编辑课程
const handleEditCourse = async (course) => {
  Object.assign(courseForm, course);
  selectedTeacherIds.value = parseTeacherIds(course.teacherIds);
  courseForm.semesterIds = [];
  try {
    // 获取课程关联的学期ID列表
    const semesters = await getCourseSemesters(course.courseId);
    courseForm.semesterIds = semesters.map((semester) => semester.semesterId);
  } catch (error) {
    console.error("获取课程学期关联失败:", error);
  }
  courseFormVisible.value = true;
};

// 保存课程
const handleSaveCourse = async () => {
  try {
    courseForm.teacherIds = selectedTeacherIds.value.join(",");
    let courseId = courseForm.courseId;
    if (courseId) {
      // 更新课程
      await updateCourse(courseId, courseForm);
      ElMessage.success("课程更新成功");
    } else {
      // 添加课程
      const response = await addCourse(courseForm);
      courseId = response.courseId;
      ElMessage.success("课程添加成功");
    }

    // 保存课程学期关联
    if (courseId) {
      await saveCourseSemester(courseId, courseForm.semesterIds);
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
    semesterStats.value = await getCourseStatisticsBySemester(
      currentProgram.programId,
      selectedSemester.value
    );
  } catch (error) {
    ElMessage.error("获取学期课程失败");
    console.error("获取学期课程失败:", error);
  }
};

// 导出完整课程安排
const handleExportFullSchedule = async () => {
  try {
    exportingFullSchedule.value = true;
    const schedule = await getProgramFullSchedule(currentProgram.programId);
    const tableData = buildFullScheduleTableData(schedule);
    if (tableData.length === 0) {
      ElMessage.warning("暂无可导出的课程安排");
      return;
    }

    fullScheduleTableData.value = tableData;
    showFullScheduleTable.value = true;
    showAllScheduleRows.value = false;

    const header = [
      "学期",
      "课程名称",
      "课程代码",
      "学分",
      "总学时",
      "理论学时",
      "实践学时",
      "课程类型",
      "课程性质",
      "考核方式",
      "课程类别",
      "授课教师",
    ];
    const rows = tableData.map((course) => [
      course.semester,
      course.courseName,
      course.courseCode,
      course.credit,
      course.totalHours,
      course.theoreticalHours,
      course.practicalHours,
      course.courseType,
      course.courseNature,
      course.examMark,
      course.courseCategory,
      course.teacherIds,
    ]);
    const fileBaseName = currentProgram.majorName
      ? `${currentProgram.majorName}_完整课程安排`
      : "培养方案_完整课程安排";
    downloadCsv([header, ...rows], `${fileBaseName}.csv`);

    ElMessage.success("完整课程安排已导出");
  } catch (error) {
    ElMessage.error("导出完整课程安排失败");
    console.error("导出完整课程安排失败:", error);
  } finally {
    exportingFullSchedule.value = false;
  }
};

// 页面挂载时获取培养方案列表
onMounted(() => {
  fetchPrograms();
  fetchTeachers();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-subtitle {
  font-size: 12px;
  color: #909399;
}

.stats-tags {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-group {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.stat-label {
  font-size: 13px;
  color: #606266;
}

.stat-tag {
  margin-bottom: 4px;
}

.semester-steps {
  margin-top: 6px;
}

.semester-toolbar {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f6f8fb;
  border-radius: 10px;
}

.semester-picker {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-label {
  font-size: 13px;
  color: #606266;
}

.semester-course-table {
  border-radius: 10px;
}

.semester-stats {
  padding: 16px;
  border-radius: 12px;
  background: #f9fafc;
  border: 1px solid #ebeef5;
}

.stats-header {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.stats-grid {
  margin-bottom: 12px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.export-hint {
  padding: 12px 16px;
  margin-bottom: 12px;
  border-radius: 10px;
  background: #f5f7fa;
  color: #606266;
  font-size: 13px;
}

/* 新增：仅标题风格（不影响其它区域） */
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
}

.pagination {
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
.table-action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-button {
  min-width: 64px;
  justify-content: center;
}
</style>
