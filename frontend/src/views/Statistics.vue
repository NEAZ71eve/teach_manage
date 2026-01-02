<template>
  <div class="statistics">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>系统统计与报表</span>
        </div>
      </template>

      <div class="statistics-dashboard">
        <!-- 统计卡片 -->
        <div class="statistics-cards">
          <el-card class="stat-card">
            <el-statistic
              title="课程总数"
              :value="courseStatistics.totalCourses"
              :precision="0"
            >
              <template #prefix>
                <el-icon class="el-icon--success"><Cpu /></el-icon>
              </template>
            </el-statistic>
          </el-card>

          <el-card class="stat-card">
            <el-statistic
              title="知识点总数"
              :value="knowledgePointStatistics.totalKnowledgePoints"
              :precision="0"
            >
              <template #prefix>
                <el-icon class="el-icon--warning"><Document /></el-icon>
              </template>
            </el-statistic>
          </el-card>

          <el-card class="stat-card">
            <el-statistic
              title="题目总数"
              :value="questionBankStatistics.totalQuestions"
              :precision="0"
            >
              <template #prefix>
                <el-icon class="el-icon--primary"><EditPen /></el-icon>
              </template>
            </el-statistic>
          </el-card>

          <el-card class="stat-card">
            <el-statistic
              title="试卷总数"
              :value="examPaperStatistics.totalPapers"
              :precision="0"
            >
              <template #prefix>
                <el-icon class="el-icon--danger"><DocumentRemove /></el-icon>
              </template>
            </el-statistic>
          </el-card>

          <el-card class="stat-card">
            <el-statistic
              title="课程平均学分"
              :value="courseStatistics.averageCredit"
              :precision="1"
            >
              <template #prefix>
                <el-icon class="el-icon--success"><Cpu /></el-icon>
              </template>
            </el-statistic>
          </el-card>

          <el-card class="stat-card">
            <el-statistic
              title="试卷平均总分"
              :value="examPaperStatistics.averageTotalScore"
              :precision="1"
            >
              <template #prefix>
                <el-icon class="el-icon--primary"><DocumentRemove /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </div>

        <!-- 题型分布表格 -->
        <el-card class="mb-4">
          <template #header>
            <div class="card-header">
              <span>题型分布</span>
            </div>
          </template>
          <el-table :data="questionTypeDist" style="width: 100%">
            <el-table-column
              prop="type"
              label="题型"
              width="120"
              :formatter="formatQuestionType"
            >
            </el-table-column>
            <el-table-column prop="count" label="数量" width="100">
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 统计分析报表 -->
        <div class="statistics-charts">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>课程分布（培养方案）</span>
              </div>
            </template>
            <el-table v-if="courseByProgram.length" :data="courseByProgram">
              <el-table-column prop="programName" label="培养方案" />
              <el-table-column prop="courseCount" label="课程数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>

          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>课程分布（课程性质）</span>
              </div>
            </template>
            <el-table v-if="courseByNature.length" :data="courseByNature">
              <el-table-column prop="courseNature" label="课程性质" />
              <el-table-column prop="courseCount" label="数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>

          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>知识点覆盖（课程维度）</span>
              </div>
            </template>
            <el-table v-if="knowledgePointByCourse.length" :data="knowledgePointByCourse">
              <el-table-column prop="courseName" label="课程" />
              <el-table-column prop="knowledgePointCount" label="知识点数量" width="120" />
              <el-table-column prop="questionCount" label="题目数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>

          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>知识点覆盖（知识点维度）</span>
              </div>
            </template>
            <el-table v-if="knowledgePointCoverage.length" :data="knowledgePointCoverage">
              <el-table-column prop="kpName" label="知识点" />
              <el-table-column prop="courseName" label="课程" />
              <el-table-column prop="questionCount" label="题目数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>

          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>题目难度分布</span>
              </div>
            </template>
            <el-table v-if="questionDifficultyDist.length" :data="questionDifficultyDist">
              <el-table-column prop="difficulty" label="难度" />
              <el-table-column prop="count" label="数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>

          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>试卷类型分布</span>
              </div>
            </template>
            <el-table v-if="paperTypeDist.length" :data="paperTypeDist">
              <el-table-column prop="type" label="类型" />
              <el-table-column prop="count" label="数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>

          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>试卷课程分布</span>
              </div>
            </template>
            <el-table v-if="papersByCourse.length" :data="papersByCourse">
              <el-table-column prop="courseName" label="课程" />
              <el-table-column prop="paperCount" label="试卷数量" width="120" />
            </el-table>
            <el-empty v-else description="暂无数据" />
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
  Cpu,
  Document,
  EditPen,
  DocumentRemove,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from "../api/request";

// 统计数据
const courseStatistics = ref({
  totalCourses: 0,
  averageCredit: 0,
  courseByProgram: [],
  courseByNature: [],
});

const knowledgePointStatistics = ref({
  totalKnowledgePoints: 0,
  coverageByKnowledgePoint: [],
  coverageByCourse: [],
});

const questionBankStatistics = ref({
  totalQuestions: 0,
  questionTypeDistribution: [],
  questionDifficultyDistribution: [],
});

const examPaperStatistics = ref({
  totalPapers: 0,
  averageTotalScore: 0,
  paperTypeDistribution: [],
  papersByCourse: [],
});

const questionTypeDist = ref([]); // 用于表格数据
const questionDifficultyDist = ref([]);
const courseByProgram = ref([]);
const courseByNature = ref([]);
const knowledgePointCoverage = ref([]);
const knowledgePointByCourse = ref([]);
const paperTypeDist = ref([]);
const papersByCourse = ref([]);

// 获取课程统计数据
const fetchCourseStatistics = async () => {
  try {
    const response = await request({
      url: "/statistics/course",
      method: "GET",
    });
    courseStatistics.value = response;
    courseByProgram.value = response.courseByProgram || [];
    courseByNature.value = response.courseByNature || [];
  } catch (error) {
    ElMessage.error("获取课程统计数据失败");
    console.error("获取课程统计数据失败:", error);
  }
};

// 获取知识点统计数据
const fetchKnowledgePointStatistics = async () => {
  try {
    const response = await request({
      url: "/statistics/knowledge-point",
      method: "GET",
    });
    knowledgePointStatistics.value = response;
    knowledgePointCoverage.value = response.coverageByKnowledgePoint || [];
    knowledgePointByCourse.value = response.coverageByCourse || [];
  } catch (error) {
    ElMessage.error("获取知识点统计数据失败");
    console.error("获取知识点统计数据失败:", error);
  }
};

// 获取题库统计数据
const fetchQuestionBankStatistics = async () => {
  try {
    const response = await request({
      url: "/statistics/question-bank",
      method: "GET",
    });
    questionBankStatistics.value = response;
    // 提取题型分布数据用于表格
    questionTypeDist.value = response.questionTypeDistribution || [];
    questionDifficultyDist.value =
      response.questionDifficultyDistribution || [];
  } catch (error) {
    ElMessage.error("获取题库统计数据失败");
    console.error("获取题库统计数据失败:", error);
  }
};

// 获取试卷统计数据
const fetchExamPaperStatistics = async () => {
  try {
    const response = await request({
      url: "/statistics/exam-paper",
      method: "GET",
    });
    examPaperStatistics.value = response;
    paperTypeDist.value = response.paperTypeDistribution || [];
    papersByCourse.value = response.papersByCourse || [];
  } catch (error) {
    ElMessage.error("获取试卷统计数据失败");
    console.error("获取试卷统计数据失败:", error);
  }
};

// 获取所有统计数据
const fetchAllStatistics = async () => {
  await Promise.all([
    fetchCourseStatistics(),
    fetchKnowledgePointStatistics(),
    fetchQuestionBankStatistics(),
    fetchExamPaperStatistics(),
  ]);
};

// 将题型编号转换为名称
const formatQuestionType = (row, column, cellValue) => {
  const typeMap = {
    1: "单选题",
    2: "多选题",
    3: "判断题",
    4: "填空题",
    5: "简答题",
  };
  return typeMap[cellValue] || "其他";
};

// 页面挂载时获取数据
onMounted(async () => {
  await fetchAllStatistics();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-4 {
  margin-bottom: 16px;
}

.statistics-dashboard {
  width: 100%;
}

.statistics-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  min-width: 200px;
  max-width: 300px;
}

.statistics-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  height: auto;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

</style>
