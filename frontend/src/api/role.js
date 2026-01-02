import request from "./request";

/**
 * 获取角色列表
 * @returns {Promise}
 */
export const getRoles = () => {
  return request({
    url: "/roles",
    method: "GET",
  });
};

/**
 * 根据ID获取角色
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export const getRoleById = (roleId) => {
  return request({
    url: `/roles/${roleId}`,
    method: "GET",
  });
};

/**
 * 新增角色
 * @param {object} role - 角色信息
 * @returns {Promise}
 */
export const addRole = (role) => {
  return request({
    url: "/roles",
    method: "POST",
    data: role,
  });
};

/**
 * 更新角色
 * @param {number} roleId - 角色ID
 * @param {object} role - 角色信息
 * @returns {Promise}
 */
export const updateRole = (roleId, role) => {
  return request({
    url: `/roles/${roleId}`,
    method: "PUT",
    data: role,
  });
};

/**
 * 删除角色
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export const deleteRole = (roleId) => {
  return request({
    url: `/roles/${roleId}`,
    method: "DELETE",
  });
};

/**
 * 获取角色权限列表
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export const getRolePermissions = (roleId) => {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: "GET",
  });
};

/**
 * 为角色分配权限
 * @param {number} roleId - 角色ID
 * @param {Array<number>} permissionIds - 权限ID列表
 * @returns {Promise}
 */
export const assignPermissions = (roleId, permissionIds) => {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: "POST",
    data: permissionIds,
  });
};

/**
 * 获取角色关联的专业列表
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export const getRolePrograms = (roleId) => {
  return request({
    url: `/roles/${roleId}/programs`,
    method: "GET",
  });
};

/**
 * 为角色分配专业
 * @param {number} roleId - 角色ID
 * @param {Array<number>} programIds - 专业ID列表
 * @returns {Promise}
 */
export const assignPrograms = (roleId, programIds) => {
  return request({
    url: `/roles/${roleId}/programs`,
    method: "POST",
    data: programIds,
  });
};

/**
 * 获取所有角色-专业关联
 * @returns {Promise}
 */
export const getAllRolePrograms = () => {
  return request({
    url: `/roles/programs`,
    method: "GET",
  });
};
