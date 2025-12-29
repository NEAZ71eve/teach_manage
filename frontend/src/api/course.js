import request from './request'

/**
 * 获取课程列表（带分页）
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @returns {Promise}
 */
export const getCourses = (page, pageSize) => {
  return request({
    url: `/course/page?page=${page}&limit=${pageSize}`,
    method: 'GET'
  })
}

/**
 * 获取所有课程
 * @returns {Promise}
 */
export const getAllCourses = () => {
  return request({
    url: '/course',
    method: 'GET'
  })
}

/**
 * 获取课程详情
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const getCourseById = (courseId) => {
  return request({
    url: `/course/${courseId}`,
    method: 'GET'
  })
}

/**
 * 添加课程
 * @param {object} course - 课程信息
 * @returns {Promise}
 */
export const addCourse = (course) => {
  return request({
    url: '/course',
    method: 'POST',
    data: course
  })
}

/**
 * 更新课程
 * @param {number} courseId - 课程ID
 * @param {object} course - 课程信息
 * @returns {Promise}
 */
export const updateCourse = (courseId, course) => {
  return request({
    url: `/course/${courseId}`,
    method: 'PUT',
    data: course
  })
}

/**
 * 删除课程
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const deleteCourse = (courseId) => {
  return request({
    url: `/course/${courseId}`,
    method: 'DELETE'
  })
}

/**
 * 批量删除课程
 * @param {Array} ids - 课程ID数组
 * @returns {Promise}
 */
export const deleteBatch = (ids) => {
  return request({
    url: '/course/batch',
    method: 'DELETE',
    data: ids
  })
}

/**
 * 搜索课程
 * @param {string} keyword - 搜索关键词
 * @returns {Promise}
 */
export const searchCourses = (keyword) => {
  return request({
    url: `/course/search?keyword=${keyword}`,
    method: 'GET'
  })
}

/**
 * 获取所有学期列表
 * @returns {Promise}
 */
export const getAllSemesters = () => {
  return request({
    url: '/course/semesters/all',
    method: 'GET'
  })
}

/**
 * 获取课程关联的学期ID列表
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const getCourseSemesters = (courseId) => {
  return request({
    url: `/course/${courseId}/semesters`,
    method: 'GET'
  })
}

/**
 * 保存课程学期关联
 * @param {number} courseId - 课程ID
 * @param {Array} semesterIds - 学期ID数组
 * @returns {Promise}
 */
export const saveCourseSemester = (courseId, semesterIds) => {
  return request({
    url: `/course/${courseId}/semesters`,
    method: 'POST',
    data: semesterIds
  })
}

/**
 * 根据学期ID获取课程列表
 * @param {number} semesterId - 学期ID
 * @returns {Promise}
 */
export const getCoursesBySemester = (semesterId) => {
  return request({
    url: `/course/by-semester/${semesterId}`,
    method: 'GET'
  })
}
