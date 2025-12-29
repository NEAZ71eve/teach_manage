import request from "./request";

/**
 * 获取用户列表
 * @returns {Promise}
 */
export const getUsers = () => {
  return request({
    url: "/users",
    method: "GET",
  });
};

/**
 * 根据ID获取用户
 * @param {number} userId - 用户ID
 * @returns {Promise}
 */
export const getUserById = (userId) => {
  return request({
    url: `/users/${userId}`,
    method: "GET",
  });
};

/**
 * 新增用户
 * @param {object} user - 用户信息
 * @returns {Promise}
 */
export const addUser = (user) => {
  return request({
    url: "/users",
    method: "POST",
    data: user,
  });
};

/**
 * 更新用户
 * @param {number} userId - 用户ID
 * @param {object} user - 用户信息
 * @returns {Promise}
 */
export const updateUser = (userId, user) => {
  return request({
    url: `/users/${userId}`,
    method: "PUT",
    data: user,
  });
};

/**
 * 删除用户
 * @param {number} userId - 用户ID
 * @returns {Promise}
 */
export const deleteUser = (userId) => {
  return request({
    url: `/users/${userId}`,
    method: "DELETE",
  });
};

/**
 * 获取用户角色列表
 * @param {number} userId - 用户ID
 * @returns {Promise}
 */
export const getUserRoles = (userId) => {
  return request({
    url: `/users/${userId}/roles`,
    method: "GET",
  });
};

/**
 * 为用户分配角色
 * @param {number} userId - 用户ID
 * @param {Array<number>} roleIds - 角色ID列表
 * @returns {Promise}
 */
export const assignRoles = (userId, roleIds) => {
  return request({
    url: `/users/${userId}/roles`,
    method: "POST",
    data: roleIds,
  });
};

/**
 * 用户登录
 * @param {object} loginForm - 登录表单信息
 * @returns {Promise}
 */
export const login = (loginForm) => {
  return request({
    url: "/login",
    method: "POST",
    data: loginForm,
  });
};
