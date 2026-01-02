import request from './request'

/**
 * 获取所有学期
 * @returns {Promise}
 */
export const getAllSemesters = () => {
  return request({
    url: '/semester',
    method: 'GET'
  })
}

/**
 * 获取学期列表（带分页）
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @returns {Promise}
 */
export const getSemesters = (page, pageSize) => {
  return request({
    url: `/semester/page?page=${page}&limit=${pageSize}`,
    method: 'GET'
  })
}

/**
 * 根据ID获取学期
 * @param {number} semesterId - 学期ID
 * @returns {Promise}
 */
export const getSemesterById = (semesterId) => {
  return request({
    url: `/semester/${semesterId}`,
    method: 'GET'
  })
}

/**
 * 获取当前学期
 * @returns {Promise}
 */
export const getCurrentSemester = () => {
  return request({
    url: '/semester/current',
    method: 'GET'
  })
}

/**
 * 新增学期
 * @param {object} semester - 学期信息
 * @returns {Promise}
 */
export const addSemester = (semester) => {
  return request({
    url: '/semester',
    method: 'POST',
    data: semester
  })
}

/**
 * 更新学期
 * @param {number} semesterId - 学期ID
 * @param {object} semester - 学期信息
 * @returns {Promise}
 */
export const updateSemester = (semesterId, semester) => {
  return request({
    url: `/semester/${semesterId}`,
    method: 'PUT',
    data: semester
  })
}

/**
 * 删除学期
 * @param {number} semesterId - 学期ID
 * @returns {Promise}
 */
export const deleteSemester = (semesterId) => {
  return request({
    url: `/semester/${semesterId}`,
    method: 'DELETE'
  })
}

/**
 * 批量删除学期
 * @param {Array} semesterIds - 学期ID数组
 * @returns {Promise}
 */
export const deleteBatchSemesters = (semesterIds) => {
  return request({
    url: '/semester/batch',
    method: 'DELETE',
    data: semesterIds
  })
}

/**
 * 获取课程表（周课表）
 * @param {number} semesterId - 学期ID
 * @param {number} weekDay - 星期几（1-7）
 * @returns {Promise}
 */
export const getWeekSchedule = (semesterId, weekDay) => {
  return request({
    url: `/course-schedule/week-schedule?semesterId=${semesterId}&weekDay=${weekDay}`,
    method: 'GET'
  })
}

/**
 * 根据班级获取周课表
 * @param {number} semesterId - 学期ID
 * @param {string} className - 班级名称
 * @returns {Promise}
 */
export const getWeekScheduleByClass = (semesterId, className) => {
  return request({
    url: `/course-schedule/week-schedule/class?semesterId=${semesterId}&className=${encodeURIComponent(className)}`,
    method: 'GET'
  })
}

/**
 * 根据教师获取周课表
 * @param {number} semesterId - 学期ID
 * @param {string} teacher - 教师姓名
 * @param {number} [weekDay] - 星期几（可选）
 * @returns {Promise}
 */
export const getWeekScheduleByTeacher = (semesterId, teacher, weekDay) => {
  let url = `/course-schedule/week-schedule/teacher?semesterId=${semesterId}&teacher=${encodeURIComponent(teacher)}`
  if (weekDay) {
    url += `&weekDay=${weekDay}`
  }
  return request({
    url: url,
    method: 'GET'
  })
}

/**
 * 获取课程表列表
 * @param {number} semesterId - 学期ID
 * @returns {Promise}
 */
export const getCourseSchedules = (semesterId) => {
  return request({
    url: `/course-schedule/semester/${semesterId}`,
    method: 'GET'
  })
}

/**
 * 添加课程表
 * @param {object} schedule - 课程表信息
 * @returns {Promise}
 */
export const addCourseSchedule = (schedule) => {
  return request({
    url: '/course-schedule',
    method: 'POST',
    data: schedule
  })
}

/**
 * 更新课程表
 * @param {number} scheduleId - 课程表ID
 * @param {object} schedule - 课程表信息
 * @returns {Promise}
 */
export const updateCourseSchedule = (scheduleId, schedule) => {
  return request({
    url: `/course-schedule/${scheduleId}`,
    method: 'PUT',
    data: schedule
  })
}

/**
 * 删除课程表
 * @param {number} scheduleId - 课程表ID
 * @returns {Promise}
 */
export const deleteCourseSchedule = (scheduleId) => {
  return request({
    url: `/course-schedule/${scheduleId}`,
    method: 'DELETE'
  })
}

/**
 * 批量删除课程表
 * @param {Array} scheduleIds - 课程表ID数组
 * @returns {Promise}
 */
export const deleteBatchCourseSchedules = (scheduleIds) => {
  return request({
    url: '/course-schedule/batch',
    method: 'DELETE',
    data: scheduleIds
  })
}