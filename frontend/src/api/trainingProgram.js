import request from './request'

/**
 * 获取培养方案列表（带分页）
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @param {number|null} programId - 专业ID，用于过滤特定专业的培养方案
 * @returns {Promise}
 */
export const getTrainingPrograms = (page, pageSize, programId = null) => {
  let url = `/training-program/page?page=${page}&limit=${pageSize}`;
  if (programId) {
    url += `&programId=${programId}`;
  }
  return request({
    url: url,
    method: 'GET'
  })
}

/**
 * 获取所有培养方案
 * @returns {Promise}
 */
export const getAllTrainingPrograms = () => {
  return request({
    url: '/training-program',
    method: 'GET'
  })
}

/**
 * 获取培养方案详情
 * @param {number} programId - 培养方案ID
 * @returns {Promise}
 */
export const getTrainingProgramById = (programId) => {
  return request({
    url: `/training-program/${programId}`,
    method: 'GET'
  })
}

/**
 * 添加培养方案
 * @param {object} program - 培养方案信息
 * @returns {Promise}
 */
export const addTrainingProgram = (program) => {
  console.log('发送添加培养方案请求:', program);
  return request({
    url: '/training-program',
    method: 'POST',
    data: program
  })
}

/**
 * 更新培养方案
 * @param {number} programId - 培养方案ID
 * @param {object} program - 培养方案信息
 * @returns {Promise}
 */
export const updateTrainingProgram = (programId, program) => {
  return request({
    url: `/training-program/${programId}`,
    method: 'PUT',
    data: program
  })
}

/**
 * 删除培养方案
 * @param {number} programId - 培养方案ID
 * @returns {Promise}
 */
export const deleteTrainingProgram = (programId) => {
  return request({
    url: `/training-program/${programId}`,
    method: 'DELETE'
  })
}

/**
 * 批量删除培养方案
 * @param {Array} ids - 培养方案ID数组
 * @returns {Promise}
 */
export const deleteProgramBatch = (ids) => {
  return request({
    url: '/training-program/batch',
    method: 'DELETE',
    data: ids
  })
}
