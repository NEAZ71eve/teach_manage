import request from "./request";

/**
 * 获取培养方案列表
 * @returns {Promise}
 */
export const getTrainingPrograms = () => {
  return request({
    url: "/training-programs",
    method: "GET",
  });
};

/**
 * 根据ID获取培养方案
 * @param {number} programId - 培养方案ID
 * @returns {Promise}
 */
export const getTrainingProgramById = (programId) => {
  return request({
    url: `/training-programs/${programId}`,
    method: "GET",
  });
};

/**
 * 新增培养方案
 * @param {object} program - 培养方案信息
 * @returns {Promise}
 */
export const addTrainingProgram = (program) => {
  return request({
    url: "/training-programs",
    method: "POST",
    data: program,
  });
};

/**
 * 更新培养方案
 * @param {number} programId - 培养方案ID
 * @param {object} program - 培养方案信息
 * @returns {Promise}
 */
export const updateTrainingProgram = (programId, program) => {
  return request({
    url: `/training-programs/${programId}`,
    method: "PUT",
    data: program,
  });
};

/**
 * 删除培养方案
 * @param {number} programId - 培养方案ID
 * @returns {Promise}
 */
export const deleteTrainingProgram = (programId) => {
  return request({
    url: `/training-programs/${programId}`,
    method: "DELETE",
  });
};
