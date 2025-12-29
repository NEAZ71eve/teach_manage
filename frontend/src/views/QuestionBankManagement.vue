<template>
  <div class="question-bank-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
          <div class="header-actions">
            <el-select
              v-model="searchParams.questionType"
              placeholder="选择题型"
              style="width: 120px; margin-right: 10px"
            >
              <el-option label="全部题型" value="" />
              <el-option label="单选题" value="单选题" />
              <el-option label="多选题" value="多选题" />
              <el-option label="判断题" value="判断题" />
              <el-option label="填空题" value="填空题" />
              <el-option label="简答题" value="简答题" />
            </el-select>
            <el-select
              v-model="searchParams.difficulty"
              placeholder="选择难度"
              style="width: 120px; margin-right: 10px"
            >
              <el-option label="全部难度" value="" />
              <el-option label="容易" value="容易" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
            <el-button
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedQuestionIds.length === 0"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="handleAddQuestion">
              <el-icon><Plus /></el-icon>
              添加题目
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="questions"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="questionId" label="题目ID" width="80" />
        <el-table-column prop="questionType" label="题型" width="100" />
        <el-table-column prop="courseName" label="所属学科" width="150" />
        <el-table-column prop="pointName" label="知识点" width="150" />
        <el-table-column
          prop="content"
          label="题目内容"
          min-width="300"
          @click="handleViewQuestion(scope.row)"
        />
        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleViewQuestion(scope.row)"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleEditQuestion(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteQuestion(scope.row.questionId)"
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

    <!-- 题目表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="questionForm" label-width="120px">
        <el-form-item label="题型" required>
          <el-select
            v-model="questionForm.questionType"
            placeholder="请选择题型"
          >
            <el-option label="单选题" value="单选题" />
            <el-option label="多选题" value="多选题" />
            <el-option label="判断题" value="判断题" />
            <el-option label="填空题" value="填空题" />
            <el-option label="简答题" value="简答题" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属课程" required>
          <el-select v-model="questionForm.courseId" placeholder="请选择课程">
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属知识点" required>
          <el-select v-model="questionForm.pointId" placeholder="请选择知识点">
            <el-option
              v-for="point in knowledgePoints"
              :key="point.pointId"
              :label="point.pointName"
              :value="point.pointId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" required>
          <el-input
            v-model="questionForm.content"
            type="textarea"
            placeholder="请输入题目内容"
            :rows="4"
          />
        </el-form-item>

        <!-- 选项（仅客观题显示） -->
        <el-form-item
          label="选项"
          v-if="['单选题', '多选题'].includes(questionForm.questionType)"
        >
          <div
            v-for="(option, index) in options"
            :key="index"
            class="option-item"
          >
            <el-input
              v-model="option.content"
              placeholder="请输入选项内容"
              prefix-icon="el-icon--right"
            >
              <template #prefix>
                <span class="option-label"
                  >{{ String.fromCharCode(65 + index) }}.</span
                >
              </template>
            </el-input>
            <el-button
              type="danger"
              size="small"
              @click="options.splice(index, 1)"
              v-if="options.length > 2"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
          <el-button
            type="primary"
            size="small"
            @click="addOption"
            v-if="options.length < 10"
          >
            <el-icon><Plus /></el-icon>
            添加选项
          </el-button>
        </el-form-item>

        <el-form-item label="正确答案" required>
          <el-input
            v-model="questionForm.answer"
            placeholder="请输入正确答案"
            v-if="
              ['单选题', '填空题', '简答题'].includes(questionForm.questionType)
            "
          />
          <el-checkbox-group
            v-model="correctAnswers"
            v-else-if="questionForm.questionType === '多选题'"
          >
            <el-checkbox
              v-for="(option, index) in options"
              :key="index"
              :label="String.fromCharCode(65 + index)"
            >
              {{ String.fromCharCode(65 + index) }}. {{ option.content }}
            </el-checkbox>
          </el-checkbox-group>
          <el-radio-group
            v-model="questionForm.answer"
            v-else-if="questionForm.questionType === '判断题'"
          >
            <el-radio label="对" />对 <el-radio label="错" />错
          </el-radio-group>
        </el-form-item>

        <el-form-item label="难度" required>
          <el-select v-model="questionForm.difficulty" placeholder="请选择难度">
            <el-option label="容易" value="容易" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="分值" required>
          <el-input
            v-model.number="questionForm.score"
            placeholder="请输入分值"
            type="number"
            min="0.5"
            step="0.5"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveQuestion">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看题目详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="题目详情" width="800px">
      <div class="question-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="题目ID">{{
            currentQuestion?.questionId
          }}</el-descriptions-item>
          <el-descriptions-item label="题型">{{
            currentQuestion?.questionType
          }}</el-descriptions-item>
          <el-descriptions-item label="所属学科">{{
            currentQuestion?.courseName
          }}</el-descriptions-item>
          <el-descriptions-item label="知识点">{{
            currentQuestion?.pointName
          }}</el-descriptions-item>
          <el-descriptions-item label="题目内容">
            <div v-html="currentQuestion?.content"></div>
          </el-descriptions-item>
          <el-descriptions-item
            label="选项"
            v-if="['单选题', '多选题'].includes(currentQuestion?.questionType)"
          >
            <div v-if="currentQuestion?.options">
              <div
                v-for="(option, index) in JSON.parse(currentQuestion.options)"
                :key="index"
                class="option-item"
              >
                <span class="option-label"
                  >{{ String.fromCharCode(65 + index) }}.</span
                >
                <span>{{ option.content }}</span>
              </div>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="正确答案">{{
            currentQuestion?.answer
          }}</el-descriptions-item>
          <el-descriptions-item label="难度">{{
            currentQuestion?.difficulty
          }}</el-descriptions-item>
          <el-descriptions-item label="分值">{{
            currentQuestion?.score
          }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from "vue";
import { Plus, Edit, Delete, View } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAllCourses } from "../api/course";
import { getAllKnowledgePoints } from "../api/knowledgePoint";
import {
  getQuestions,
  addQuestion,
  updateQuestion,
  deleteQuestion,
  deleteQuestionBatch,
} from "../api/question";

// 表格数据
const questions = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedQuestionIds = ref([]);

// 搜索参数
const searchParams = reactive({
  questionType: "",
  difficulty: "",
});

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("添加题目");
const questionForm = reactive({
  questionId: null,
  courseId: null,
  pointId: null,
  questionType: "单选题",
  content: "",
  options: "",
  answer: "",
  difficulty: "中等",
  score: 5,
  teacherId: 1, // 示例教师ID，实际应从登录用户获取
});

// 查看题目详情对话框
const viewDialogVisible = ref(false);
const currentQuestion = ref(null);

// 课程和知识点数据
const courses = ref([]);
const knowledgePoints = ref([]);

// 选项管理
const options = ref([{ content: "" }, { content: "" }]);
const correctAnswers = ref([]);

// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await getAllCourses();
    courses.value = response;
    if (response.length > 0) {
      questionForm.courseId = response[0].courseId;
    }
  } catch (error) {
    ElMessage.error("获取课程列表失败");
    console.error("获取课程列表失败:", error);
  }
};

// 获取知识点列表
const fetchKnowledgePoints = async () => {
  try {
    const response = await getAllKnowledgePoints();
    knowledgePoints.value = response;
    if (response.length > 0) {
      questionForm.pointId = response[0].pointId;
    }
  } catch (error) {
    ElMessage.error("获取知识点列表失败");
    console.error("获取知识点列表失败:", error);
  }
};

// 获取题目列表
const fetchQuestions = async () => {
  try {
    const response = await getQuestions(
      currentPage.value,
      pageSize.value,
      searchParams
    );
    questions.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error("获取题目列表失败");
    console.error("获取题目列表失败:", error);
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchQuestions();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchQuestions();
};

// 选择处理
const handleSelectionChange = (selection) => {
  selectedQuestionIds.value = selection.map((item) => item.questionId);
};

// 添加选项
const addOption = () => {
  options.value.push({ content: "" });
};

// 题型变化时重置相关字段
watch(
  () => questionForm.questionType,
  (newType) => {
    if (newType === "单选题" || newType === "多选题") {
      options.value = [{ content: "" }, { content: "" }];
      correctAnswers.value = [];
    } else if (newType === "判断题") {
      questionForm.answer = "对";
    } else {
      questionForm.answer = "";
    }
  }
);

// 监听搜索参数变化，自动触发查询
watch(
  searchParams,
  (newParams) => {
    currentPage.value = 1; // 重置到第一页
    fetchQuestions();
  },
  { deep: true }
);

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedQuestionIds.value.length} 道题目吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deleteQuestionBatch(selectedQuestionIds.value);
    ElMessage.success("批量删除成功");
    selectedQuestionIds.value = [];
    fetchQuestions();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
      console.error("批量删除失败:", error);
    }
  }
};

// 添加题目
const handleAddQuestion = () => {
  dialogTitle.value = "添加题目";
  resetQuestionForm();
  dialogVisible.value = true;
};

// 编辑题目
const handleEditQuestion = (row) => {
  dialogTitle.value = "编辑题目";
  Object.assign(questionForm, row);

  // 处理选项
  if (["单选题", "多选题"].includes(row.questionType)) {
    options.value = row.options
      ? JSON.parse(row.options)
      : [{ content: "" }, { content: "" }];
  }

  // 处理多选题答案
  if (row.questionType === "多选题") {
    correctAnswers.value = row.answer ? row.answer.split("") : [];
  }

  dialogVisible.value = true;
};

// 保存题目
const handleSaveQuestion = async () => {
  try {
    // 处理多选题答案
    if (questionForm.questionType === "多选题") {
      questionForm.answer = correctAnswers.value.sort().join("");
    }

    // 处理选项
    if (["单选题", "多选题"].includes(questionForm.questionType)) {
      questionForm.options = JSON.stringify(
        options.value.filter((opt) => opt.content)
      );
    }

    if (questionForm.questionId) {
      // 更新题目
      await updateQuestion(questionForm.questionId, questionForm);
      ElMessage.success("题目更新成功");
    } else {
      // 添加题目
      await addQuestion(questionForm);
      ElMessage.success("题目添加成功");
    }
    dialogVisible.value = false;
    fetchQuestions();
  } catch (error) {
    ElMessage.error(questionForm.questionId ? "题目更新失败" : "题目添加失败");
    console.error("保存题目失败:", error);
  }
};

// 删除题目
const handleDeleteQuestion = async (questionId) => {
  try {
    await ElMessageBox.confirm("确定要删除该题目吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteQuestion(questionId);
    ElMessage.success("题目删除成功");
    fetchQuestions();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("题目删除失败");
      console.error("删除题目失败:", error);
    }
  }
};

// 重置题目表单
const resetQuestionForm = () => {
  Object.assign(questionForm, {
    questionId: null,
    courseId: courses.value.length > 0 ? courses.value[0].courseId : null,
    pointId:
      knowledgePoints.value.length > 0
        ? knowledgePoints.value[0].pointId
        : null,
    questionType: "单选题",
    content: "",
    options: "",
    answer: "",
    difficulty: "中等",
    score: 5,
    teacherId: 1,
  });
  options.value = [{ content: "" }, { content: "" }];
  correctAnswers.value = [];
};

// 查看题目详情
const handleViewQuestion = (row) => {
  currentQuestion.value = row;
  viewDialogVisible.value = true;
};

// 页面挂载时获取数据
onMounted(async () => {
  await fetchCourses();
  await fetchKnowledgePoints();
  await fetchQuestions();
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

.mt-4 {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-label {
  width: 20px;
  text-align: center;
}
</style>
