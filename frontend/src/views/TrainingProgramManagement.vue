<template>
  <div class="training-program-management">
    <!-- 直接显示课程管理界面 -->
    <div class="course-management-container">
      <!-- 课程列表 -->
      <el-card class="mb-4">
        <template #header>
          <div class="card-header">
            <span>{{ currentProgram.majorName }} - 课程列表</span>
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
                导出HTML
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
          生成课程安排后可直接下载为HTML格式，支持本地打开与归档。
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
            导出四年八个学期完整课程安排（HTML）
          </el-button>
        </div>
      </el-card>
    </div>

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

// 课程管理相关状态
const currentProgram = reactive({});
const programCourses = ref([]);
const visibleProgramCourses = computed(() => {
  if (showAllCourses.value) return programCourses.value;
  return programCourses.value.slice(0, courseCollapseLimit);
});
const shouldShowCourseToggle = computed(
  () => programCourses.value.length > courseCollapseLimit
);
const showAllCourses = ref(false);
const courseCollapseLimit = 8;

// 课程学期安排相关状态
const activeStep = ref(0);
const selectedSemester = ref(1);
const semesterCourses = ref([]);
const semesterStats = ref(null);
const showAllScheduleRows = ref(false);
const scheduleCollapseLimit = 12;
const exportingFullSchedule = ref(false);

// 完整课程安排表格相关状态
const showFullScheduleTable = ref(false);
const fullScheduleTableData = ref([]);
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

const downloadHtml = (content, filename) => {
  const blob = new Blob([content], {
    type: "text/html;charset=utf-8;",
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

const escapeHtml = (value) =>
  String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");

const buildExportHtml = ({ program, tableData }) => {
  const title = program?.majorName
    ? `${program.majorName} 培养方案完整课程安排`
    : "培养方案完整课程安排";
  const rows = tableData
    .map(
      (row, index) => `
        <tr>
          <td>${index + 1}</td>
          <td>${escapeHtml(row.semester)}</td>
          <td>${escapeHtml(row.courseName)}</td>
          <td>${escapeHtml(row.courseCode)}</td>
          <td>${escapeHtml(row.credit)}</td>
          <td>${escapeHtml(row.totalHours)}</td>
          <td>${escapeHtml(row.theoreticalHours)}</td>
          <td>${escapeHtml(row.practicalHours)}</td>
          <td>${escapeHtml(row.courseType)}</td>
          <td>${escapeHtml(row.courseNature)}</td>
          <td>${escapeHtml(row.examMark)}</td>
          <td>${escapeHtml(row.courseCategory)}</td>
          <td>${escapeHtml(formatTeacherNames(row.teacherIds))}</td>
        </tr>
      `
    )
    .join("");

  return `
    <!DOCTYPE html>
    <html lang="zh-CN">
    <head>
      <meta charset="utf-8" />
      <title>${escapeHtml(title)}</title>
      <style>
        body { font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif; color: #1f2329; margin: 24px; }
        h1 { font-size: 20px; margin-bottom: 8px; }
        .meta { margin-bottom: 16px; line-height: 1.6; }
        table { width: 100%; border-collapse: collapse; font-size: 12px; }
        th, td { border: 1px solid #dcdfe6; padding: 6px 8px; text-align: center; }
        th { background: #f5f7fa; font-weight: 600; }
        tbody tr:nth-child(even) { background: #fafafa; }
      </style>
    </head>
    <body>
      <h1>${escapeHtml(title)}</h1>
      <div class="meta">
        <div>专业名称：${escapeHtml(program?.majorName || "-")}</div>
        <div>学制：${escapeHtml(program?.duration || "-")} 年</div>
        <div>总学分：${escapeHtml(program?.totalCredit || "-")}</div>
        <div>生效年份：${escapeHtml(program?.effectiveYear || "-")}</div>
        <div>培养方案描述：${escapeHtml(program?.description || "-")}</div>
        <div>导出时间：${escapeHtml(new Date().toLocaleString())}</div>
      </div>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>学期</th>
            <th>课程名称</th>
            <th>课程代码</th>
            <th>学分</th>
            <th>总学时</th>
            <th>理论学时</th>
            <th>实践学时</th>
            <th>课程类型</th>
            <th>课程性质</th>
            <th>考核方式</th>
            <th>课程类别</th>
            <th>授课教师</th>
          </tr>
        </thead>
        <tbody>
          ${rows}
        </tbody>
      </table>
    </body>
    </html>
  `;
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

    const fileBaseName = currentProgram.majorName
      ? `${currentProgram.majorName}_完整课程安排`
      : "培养方案_完整课程安排";
    const htmlContent = buildExportHtml({
      program: currentProgram,
      tableData,
    });
    downloadHtml(htmlContent, `${fileBaseName}.html`);

    ElMessage.success("完整课程安排已导出");
  } catch (error) {
    ElMessage.error("导出完整课程安排失败");
    console.error("导出完整课程安排失败:", error);
  } finally {
    exportingFullSchedule.value = false;
  }
};

// 获取当前用户负责的培养方案
const fetchUserProgram = async () => {
  try {
    // 获取当前用户信息
    const userStr = localStorage.getItem("user");
    const user = userStr ? JSON.parse(userStr) : null;
    
    if (!user) {
      ElMessage.error("请先登录");
      return;
    }
    
    // 获取用户负责的培养方案
    const response = await getTrainingPrograms(1, 10, null, user.userId);
    const programs = response.records || [];
    
    if (programs.length > 0) {
      // 使用第一个培养方案作为当前方案
      Object.assign(currentProgram, programs[0]);
      // 获取该培养方案的课程
      await fetchProgramCourses(programs[0].programId);
    } else {
      ElMessage.warning("您没有负责的培养方案");
    }
  } catch (error) {
    ElMessage.error("获取培养方案失败");
    console.error("获取培养方案失败:", error);
  }
};

// 页面挂载时获取数据
onMounted(() => {
  fetchUserProgram();
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