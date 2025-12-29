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
            <el-statistic title="课程总数" :value="courseStatistics.totalCourses" :precision="0">
              <template #prefix>
                <el-icon class="el-icon--success"><Cpu /></el-icon>
              </template>
            </el-statistic>
          </el-card>
          
          <el-card class="stat-card">
            <el-statistic title="知识点总数" :value="knowledgePointStatistics.totalKnowledgePoints" :precision="0">
              <template #prefix>
                <el-icon class="el-icon--warning"><Document /></el-icon>
              </template>
            </el-statistic>
          </el-card>
          
          <el-card class="stat-card">
            <el-statistic title="题目总数" :value="questionBankStatistics.totalQuestions" :precision="0">
              <template #prefix>
                <el-icon class="el-icon--primary"><EditPen /></el-icon>
              </template>
            </el-statistic>
          </el-card>
          
          <el-card class="stat-card">
            <el-statistic title="试卷总数" :value="examPaperStatistics.totalPapers" :precision="0">
              <template #prefix>
                <el-icon class="el-icon--danger"><DocumentRemove /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </div>
        
        <!-- 统计图表 -->
        <div class="statistics-charts">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>课程分布</span>
              </div>
            </template>
            <div class="chart-content">
              <el-empty description="图表功能开发中..." />
            </div>
          </el-card>
          
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>知识点覆盖率</span>
              </div>
            </template>
            <div class="chart-content">
              <el-empty description="图表功能开发中..." />
            </div>
          </el-card>
          
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>题库题型分布</span>
              </div>
            </template>
            <div class="chart-content">
              <el-empty description="图表功能开发中..." />
            </div>
          </el-card>
          
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>试卷难度分布</span>
              </div>
            </template>
            <div class="chart-content">
              <el-empty description="图表功能开发中..." />
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Cpu, Document, EditPen, DocumentRemove } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

// 统计数据
const courseStatistics = ref({
  totalCourses: 0
})

const knowledgePointStatistics = ref({
  totalKnowledgePoints: 0
})

const questionBankStatistics = ref({
  totalQuestions: 0
})

const examPaperStatistics = ref({
  totalPapers: 0
})

// 获取课程统计数据
const fetchCourseStatistics = async () => {
  try {
    const response = await request({
      url: '/statistics/course',
      method: 'GET'
    })
    courseStatistics.value = response
  } catch (error) {
    ElMessage.error('获取课程统计数据失败')
    console.error('获取课程统计数据失败:', error)
  }
}

// 获取知识点统计数据
const fetchKnowledgePointStatistics = async () => {
  try {
    const response = await request({
      url: '/statistics/knowledge-point',
      method: 'GET'
    })
    knowledgePointStatistics.value = response
  } catch (error) {
    ElMessage.error('获取知识点统计数据失败')
    console.error('获取知识点统计数据失败:', error)
  }
}

// 获取题库统计数据
const fetchQuestionBankStatistics = async () => {
  try {
    const response = await request({
      url: '/statistics/question-bank',
      method: 'GET'
    })
    questionBankStatistics.value = response
  } catch (error) {
    ElMessage.error('获取题库统计数据失败')
    console.error('获取题库统计数据失败:', error)
  }
}

// 获取试卷统计数据
const fetchExamPaperStatistics = async () => {
  try {
    const response = await request({
      url: '/statistics/exam-paper',
      method: 'GET'
    })
    examPaperStatistics.value = response
  } catch (error) {
    ElMessage.error('获取试卷统计数据失败')
    console.error('获取试卷统计数据失败:', error)
  }
}

// 获取所有统计数据
const fetchAllStatistics = async () => {
  await Promise.all([
    fetchCourseStatistics(),
    fetchKnowledgePointStatistics(),
    fetchQuestionBankStatistics(),
    fetchExamPaperStatistics()
  ])
}

// 页面挂载时获取数据
onMounted(async () => {
  await fetchAllStatistics()
})
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
  height: 300px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-content {
  height: 240px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
