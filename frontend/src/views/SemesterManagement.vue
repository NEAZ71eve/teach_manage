<template>
  <div class="semester-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>学期管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAddSemester">
              <el-icon><Plus /></el-icon>
              添加学期
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="semesters"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="semesterId" label="学期ID" width="80" />
        <el-table-column prop="semesterName" label="学期名称" width="200" />
        <el-table-column prop="startDate" label="开始日期" width="150" />
        <el-table-column prop="endDate" label="结束日期" width="150" />
        <el-table-column prop="isCurrent" label="是否当前学期" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.isCurrent ? 'success' : 'info'">
              {{ scope.row.isCurrent ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEditSemester(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteSemester(scope.row.semesterId)"
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

    <!-- 学期表单对话框 -->
    <el-dialog v-model="dialogVisible" title="学期信息" width="600px">
      <el-form :model="semesterForm" label-width="120px">
        <el-form-item label="学年" required>
          <el-select v-model="academicYear" placeholder="请选择学年">
            <el-option
              v-for="year in availableYears"
              :key="year"
              :label="year + '-' + (parseInt(year) + 1)"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" required>
          <el-select v-model="semesterType" placeholder="请选择学期">
            <el-option label="第一学期" :value="1" />
            <el-option label="第二学期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期名称" required>
          <el-input
            v-model="semesterForm.semesterName"
            placeholder="学期名称将自动生成"
            readonly
          />
        </el-form-item>
        <el-form-item label="开始日期" required>
          <el-date-picker
            v-model="semesterForm.startDate"
            type="date"
            placeholder="开始日期将自动生成"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束日期" required>
          <el-date-picker
            v-model="semesterForm.endDate"
            type="date"
            placeholder="结束日期将自动生成"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="是否当前学期">
          <el-switch v-model="semesterForm.isCurrent" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveSemester">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from "vue";
import { Plus, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getSemesters,
  addSemester,
  updateSemester,
  deleteSemester,
  deleteBatchSemesters,
} from "../api/semester";

// 表格数据
const semesters = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedSemesterIds = ref([]);

// 对话框
const dialogVisible = ref(false);
const semesterForm = reactive({
  semesterId: null,
  semesterName: "",
  semesterCode: "",
  startDate: "",
  endDate: "",
  status: "",
  isCurrent: false,
});

// 学年和学期选择
const currentYear = new Date().getFullYear();
const availableYears = ref([]);
for (let i = currentYear - 5; i <= currentYear + 5; i++) {
  availableYears.value.push(i.toString());
}
const academicYear = ref(currentYear.toString());
const semesterType = ref(1);

// 根据学年和学期自动生成学期信息
const generateSemesterInfo = () => {
  if (!academicYear.value || !semesterType.value) return;

  const year = parseInt(academicYear.value);
  const nextYear = year + 1;
  const semesterName = `${year}-${nextYear}学年第${semesterType.value}学期`;

  // 生成默认日期范围（YYYY-MM-DD格式字符串）
  let startDate, endDate;
  if (semesterType.value === 1) {
    // 第一学期：9月1日到次年1月15日
    startDate = `${year}-09-01`;
    endDate = `${nextYear}-01-15`;
  } else {
    // 第二学期：2月20日到6月30日
    startDate = `${nextYear}-02-20`;
    endDate = `${nextYear}-06-30`;
  }

  // 生成学期代码，格式：年-年-学期
  const semesterCode = `${year}-${nextYear}-${semesterType.value}`;

  // 设置默认状态为'未开始'
  const currentDate = new Date();
  let status = "未开始";
  if (currentDate >= new Date(startDate) && currentDate <= new Date(endDate)) {
    status = "进行中";
  } else if (currentDate > new Date(endDate)) {
    status = "已结束";
  }

  semesterForm.semesterName = semesterName;
  semesterForm.semesterCode = semesterCode;
  semesterForm.startDate = startDate;
  semesterForm.endDate = endDate;
  semesterForm.status = status;
};

// 监听学年和学期变化，自动更新学期信息
watch([academicYear, semesterType], () => {
  generateSemesterInfo();
});

// 获取学期列表
const fetchSemesters = async () => {
  try {
    const response = await getSemesters(currentPage.value, pageSize.value);
    semesters.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error("获取学期列表失败");
    console.error("获取学期列表失败:", error);
  }
};

// 选择变化
const handleSelectionChange = (selection) => {
  selectedSemesterIds.value = selection.map((item) => item.semesterId);
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchSemesters();
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
  fetchSemesters();
};

// 添加学期
const handleAddSemester = () => {
  Object.assign(semesterForm, {
    semesterId: null,
    semesterName: "",
    semesterCode: "",
    startDate: "",
    endDate: "",
    status: "",
    isCurrent: false,
  });
  // 设置默认值
  academicYear.value = currentYear.toString();
  semesterType.value = 1;
  // 自动生成学期信息
  generateSemesterInfo();
  dialogVisible.value = true;
};

// 编辑学期
const handleEditSemester = (row) => {
  Object.assign(semesterForm, row);

  // 解析学年和学期类型
  const name = row.semesterName;
  const yearMatch = name.match(/(\d{4})-(\d{4})学年第(\d)学期/);
  if (yearMatch) {
    academicYear.value = yearMatch[1];
    semesterType.value = parseInt(yearMatch[3]);
  } else {
    academicYear.value = currentYear.toString();
    semesterType.value = 1;
  }

  dialogVisible.value = true;
};

// 保存学期
const handleSaveSemester = async () => {
  try {
    if (semesterForm.semesterId) {
      // 更新学期
      await updateSemester(semesterForm.semesterId, semesterForm);
      ElMessage.success("学期更新成功");
    } else {
      // 添加学期
      await addSemester(semesterForm);
      ElMessage.success("学期添加成功");
    }
    dialogVisible.value = false;
    fetchSemesters();
  } catch (error) {
    ElMessage.error(semesterForm.semesterId ? "学期更新失败" : "学期添加失败");
    console.error("保存学期失败:", error);
  }
};

// 删除学期
const handleDeleteSemester = async (semesterId) => {
  try {
    await ElMessageBox.confirm("确定要删除该学期记录吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteSemester(semesterId);
    ElMessage.success("学期删除成功");
    fetchSemesters();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("学期删除失败");
      console.error("删除学期失败:", error);
    }
  }
};

// 批量删除学期
const handleBatchDelete = async () => {
  if (selectedSemesterIds.value.length === 0) {
    ElMessage.warning("请选择要删除的学期");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedSemesterIds.value.length} 个学期吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deleteBatchSemesters(selectedSemesterIds.value);
    ElMessage.success("学期删除成功");
    fetchSemesters();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("学期删除失败");
      console.error("批量删除学期失败:", error);
    }
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

.mt-4 {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
