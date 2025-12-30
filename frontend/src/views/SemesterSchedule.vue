<template>
  <div class="semester-schedule">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>学期课程表</span>
          <div class="header-actions">
            <el-select
              v-model="selectedSemesterId"
              placeholder="选择学期"
              style="width: 200px; margin-right: 10px"
            >
              <el-option
                v-for="semester in semesters"
                :key="semester.semesterId"
                :label="semester.semesterName"
                :value="semester.semesterId"
              />
            </el-select>

            <!-- 新增：视角选择 -->
            <el-radio-group
              v-model="viewMode"
              size="small"
              style="margin-right: 10px"
            >
              <el-radio :label="'class'">班级视角</el-radio>
              <el-radio :label="'teacher'">教师视角</el-radio>
            </el-radio-group>

            <!-- 班级选择 -->
            <el-select
              v-if="viewMode === 'class'"
              v-model="selectedClassName"
              placeholder="选择班级"
              style="width: 150px; margin-right: 10px"
            >
              <el-option
                v-for="className in classNames"
                :key="className"
                :label="className"
                :value="className"
              />
            </el-select>

            <!-- 教师选择 -->
            <el-select
              v-if="viewMode === 'teacher'"
              v-model="selectedTeacher"
              placeholder="选择教师"
              style="width: 150px; margin-right: 10px"
            >
              <el-option
                v-for="teacher in teacherNames"
                :key="teacher"
                :label="teacher"
                :value="teacher"
              />
            </el-select>

            <el-button type="primary" @click="fetchWeekSchedule">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button
              type="success"
              @click="handleExport"
              :disabled="
                !selectedSemesterId ||
                (viewMode === 'class' && !selectedClassName) ||
                (viewMode === 'teacher' && !selectedTeacher)
              "
            >
              <el-icon><Download /></el-icon> 导出课表
            </el-button>
          </div>
        </div>
      </template>

      <!-- 周课表展示 -->
      <div class="week-schedule-container">
        <div class="week-header">
          <div class="week-day" v-for="day in weekDays" :key="day.value">
            <div class="day-label">{{ day.label }}</div>
          </div>
        </div>
        <div class="schedule-content">
          <div class="time-slot" v-for="slot in timeSlots" :key="slot.section">
            <div class="slot-time">{{ slot.time }}</div>
          </div>
          <div class="course-grid">
            <div
              class="course-cell"
              v-for="course in weekSchedule"
              :key="course.scheduleId"
              :style="getCourseCellStyle(course)"
            >
              <div class="course-info">
                <div class="course-name">
                  {{ course.courseName || "未命名课程" }}
                </div>
                <div class="course-teacher">{{ course.teacher }}</div>
                <div class="course-classroom">{{ course.classroom }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { Search, Download } from "@element-plus/icons-vue";
import {
  getCurrentSemester,
  getAllSemesters,
  getWeekScheduleByClass,
  getWeekScheduleByTeacher,
  getCourseSchedules,
} from "../api/semester";

// 周几选项
const weekDays = [
  { value: 1, label: "周一" },
  { value: 2, label: "周二" },
  { value: 3, label: "周三" },
  { value: 4, label: "周四" },
  { value: 5, label: "周五" },
  { value: 6, label: "周六" },
  { value: 7, label: "周日" },
];

// 时间段选项
const timeSlots = [
  { section: 1, time: "08:00-08:45" },
  { section: 2, time: "08:55-09:40" },
  { section: 3, time: "10:00-10:45" },
  { section: 4, time: "10:55-11:40" },
  { section: 5, time: "14:00-14:45" },
  { section: 6, time: "14:55-15:40" },
  { section: 7, time: "16:00-16:45" },
  { section: 8, time: "16:55-17:40" },
  { section: 9, time: "19:00-19:45" },
  { section: 10, time: "19:55-20:40" },
];

// 班级名称列表
const classNames = ref([]);

// 教师名称列表
const teacherNames = ref([]);

// 获取班级列表
const fetchClassNames = async () => {
  try {
    const response = await getCourseSchedules(selectedSemesterId.value);
    // 从课程表数据中提取不重复的班级名称
    const uniqueClassNames = [
      ...new Set(response.map((item) => item.className)),
    ];
    classNames.value = uniqueClassNames;
  } catch (error) {
    ElMessage.error("获取班级列表失败");
    console.error("获取班级列表失败:", error);
  }
};

// 获取教师列表
const fetchTeacherNames = async () => {
  try {
    const response = await getCourseSchedules(selectedSemesterId.value);
    // 从课程表数据中提取不重复的教师名称
    const uniqueTeacherNames = [
      ...new Set(response.map((item) => item.teacher)),
    ];
    teacherNames.value = uniqueTeacherNames;
  } catch (error) {
    ElMessage.error("获取教师列表失败");
    console.error("获取教师列表失败:", error);
  }
};

// 学期列表
const semesters = ref([]);
const selectedSemesterId = ref(null);
const selectedClassName = ref("");
const selectedTeacher = ref("");
const viewMode = ref("class"); // 默认班级视角

// 周课表数据
const weekSchedule = ref([]);

// 获取所有学期
const fetchSemesters = async () => {
  try {
    const response = await getAllSemesters();
    semesters.value = response;

    // 默认选择当前学期
    const currentSemester = await getCurrentSemester();
    if (currentSemester) {
      selectedSemesterId.value = currentSemester.semesterId;
    } else if (semesters.value.length > 0) {
      selectedSemesterId.value = semesters.value[0].semesterId;
    }

    // 获取班级和教师列表
    if (selectedSemesterId.value) {
      await Promise.all([fetchClassNames(), fetchTeacherNames()]);
    }
  } catch (error) {
    ElMessage.error("获取学期列表失败");
    console.error("获取学期列表失败:", error);
  }
};

// 监听学期选择变化
watch(selectedSemesterId, async (newValue) => {
  if (newValue) {
    await Promise.all([fetchClassNames(), fetchTeacherNames()]);
    // 重置选择
    selectedClassName.value = "";
    selectedTeacher.value = "";
    // 清空课表数据
    weekSchedule.value = [];
  }
});

// 获取周课表
const fetchWeekSchedule = async () => {
  if (!selectedSemesterId.value) {
    ElMessage.warning("请选择学期");
    return;
  }

  try {
    if (viewMode.value === "class") {
      if (!selectedClassName.value) {
        ElMessage.warning("请选择班级");
        return;
      }
      const response = await getWeekScheduleByClass(
        selectedSemesterId.value,
        selectedClassName.value
      );
      weekSchedule.value = response;
    } else {
      if (!selectedTeacher.value) {
        ElMessage.warning("请选择教师");
        return;
      }
      const response = await getWeekScheduleByTeacher(
        selectedSemesterId.value,
        selectedTeacher.value
      );
      weekSchedule.value = response;
    }
  } catch (error) {
    ElMessage.error("获取周课表失败");
    console.error("获取周课表失败:", error);
  }
};

// 计算课程格子的样式
const getCourseCellStyle = (course) => {
  return {
    gridColumn: course.weekDay,
    gridRow: course.classSection,
    backgroundColor: getRandomColor(),
    border: "1px solid #e0e0e0",
    borderRadius: "4px",
    padding: "8px",
    overflow: "hidden",
  };
};

// 生成随机颜色
const getRandomColor = () => {
  const colors = [
    "#FFE4E1",
    "#E6F3FF",
    "#E8F5E9",
    "#FFF3E0",
    "#F3E5F5",
    "#E0F7FA",
    "#FFF8E1",
    "#FCE4EC",
    "#E8F5E8",
    "#F3E5F5",
  ];
  return colors[Math.floor(Math.random() * colors.length)];
};

// 处理课程表导出
const handleExport = () => {
  if (!selectedSemesterId.value) {
    ElMessage.warning("请先选择学期");
    return;
  }

  if (viewMode.value === "class") {
    if (!selectedClassName.value) {
      ElMessage.warning("请先选择班级");
      return;
    }
    window.open(
      `/api/course-schedule/export?semesterId=${selectedSemesterId.value}&className=${selectedClassName.value}`,
      "_blank"
    );
  } else {
    // 教师视角导出（这里可以根据实际需求扩展导出功能）
    ElMessage.info("教师视角导出功能开发中");
  }
};

// 页面挂载时获取学期列表
onMounted(async () => {
  await fetchSemesters();
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
}

.mb-4 {
  margin-bottom: 16px;
}

.week-schedule-container {
  display: flex;
  flex-direction: column;
  margin-top: 20px;
}

.week-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  gap: 10px;
  margin-bottom: 10px;
}

.week-day {
  background-color: #f0f2f5;
  padding: 10px;
  text-align: center;
  font-weight: bold;
  border-radius: 4px;
}

.schedule-content {
  display: flex;
  gap: 10px;
}

.time-slot {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.slot-time {
  width: 80px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  background-color: #f0f2f5;
  border-radius: 4px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(10, 60px);
  gap: 10px;
  flex: 1;
}

.course-cell {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.course-cell:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.course-info {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.course-name {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 90%;
}

.course-teacher {
  font-size: 12px;
  color: #606266;
  margin-bottom: 2px;
}

.course-classroom {
  font-size: 12px;
  color: #606266;
}
</style>
