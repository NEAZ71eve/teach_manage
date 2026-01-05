package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.LoginRequest;
import com.example.coursemanagement.entity.LoginResponse;
import com.example.coursemanagement.entity.Permission;
import com.example.coursemanagement.entity.Role;
import com.example.coursemanagement.entity.User;
import com.example.coursemanagement.service.*;
import com.example.coursemanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private PermissionService permissionService;
    
    
    
    /**
     * 用户登录
     */
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();
        
        try {
            // 根据用户名查询用户
            Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // 验证密码（支持明文密码和默认密码）
                boolean passwordValid = false;
                
                // 检查是否是默认密码（用于开发测试）
                if (loginRequest.getPassword().equals("admin123") && user.getUsername().equals("admin")) {
                    passwordValid = true;
                } 
                // 明文密码验证
                else {
                    // 明文密码验证
                    passwordValid = user.getPassword().equals(loginRequest.getPassword());
                }
                
                if (passwordValid) {
                    // 获取用户的角色列表
                    List<Integer> roleIds = userRoleService.findByUserId(user.getUserId()).stream()
                            .map(role -> role.getRoleId())
                            .collect(Collectors.toList());
                    
                    // 获取角色信息
                    List<Role> roles = new ArrayList<>();
                    for (Integer roleId : roleIds) {
                        Optional<Role> roleOptional = roleService.findById(roleId);
                        roleOptional.ifPresent(roles::add);
                    }

                    if (user.getUsername().equals("admin")) {
                        Role adminRole = roleService.findById(1).orElseGet(() -> {
                            Role fallback = new Role();
                            fallback.setRoleId(1);
                            fallback.setRoleName("系统管理员");
                            fallback.setRoleDesc("拥有系统所有权限");
                            return fallback;
                        });
                        roles = new ArrayList<>();
                        roles.add(adminRole);
                        roleIds = new ArrayList<>();
                        roleIds.add(adminRole.getRoleId());
                    }

                    // 获取角色的权限列表
                    Set<Integer> permissionIdSet = new HashSet<>();
                    for (Integer roleId : roleIds) {
                        List<Integer> rolePermissionIds = rolePermissionService.findByRoleId(roleId).stream()
                                .map(permission -> permission.getPermissionId())
                                .collect(Collectors.toList());
                        permissionIdSet.addAll(rolePermissionIds);
                    }

                    List<String> roleNames = roles.stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());

                    boolean isSystemAdmin = roleNames.contains("系统管理员");
                    boolean isProgramTeacher = roleNames.contains("学院管理员") || roleNames.contains("专业负责教师");
                    boolean isNormalTeacher = roleNames.contains("教师") && !isProgramTeacher;

                    List<Permission> permissions = new ArrayList<>();
                    if (isSystemAdmin) {
                        permissions = buildUserManagementPermissions();
                    } else if (isProgramTeacher) {
                        permissions = buildProgramTeacherPermissions();
                    } else if (isNormalTeacher) {
                        permissions = buildNormalTeacherPermissions();
                    } else {
                        for (Integer permissionId : permissionIdSet) {
                            Optional<Permission> permissionOptional = permissionService.findById(permissionId);
                            permissionOptional.ifPresent(permissions::add);
                        }
                    }
                    
                    // 提取权限代码列表
                    List<String> permissionCodes = permissions.stream()
                            .map(permission -> permission.getPermissionCode())
                            .collect(Collectors.toList());
                    
                    // 生成JWT令牌，包含角色和权限信息
                    String token = jwtUtils.generateToken(user.getUserId(), user.getUsername(), roleNames, permissionCodes);
                    
                    // 设置响应信息
                    response.setSuccess(true);
                    response.setMessage("登录成功");
                    response.setToken(token);
                    response.setUser(user);
                    response.setRoles(roles);
                    response.setPermissions(permissions);
                    
                    return ResponseEntity.ok(response);
                } else {
                    response.setSuccess(false);
                    response.setMessage("密码错误");
                    return ResponseEntity.ok(response);
                }
            } else {
                response.setSuccess(false);
                response.setMessage("用户不存在");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // 捕获所有异常，返回详细的错误信息
            response.setSuccess(false);
            response.setMessage("登录失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        }
    }

    private Permission buildPermission(Integer id, String name, String code) {
        Permission permission = new Permission();
        permission.setPermissionId(id);
        permission.setPermissionName(name);
        permission.setPermissionCode(code);
        return permission;
    }

    private List<Permission> buildUserManagementPermissions() {
        return Arrays.asList(
                buildPermission(1, "查看用户列表", "user:list"),
                buildPermission(2, "新增用户", "user:add"),
                buildPermission(3, "编辑用户", "user:edit"),
                buildPermission(4, "删除用户", "user:delete")
        );
    }

    private List<Permission> buildProgramTeacherPermissions() {
        return Arrays.asList(
                buildPermission(13, "查看课程列表", "course:list"),
                buildPermission(14, "新增课程", "course:add"),
                buildPermission(15, "编辑课程", "course:edit"),
                buildPermission(16, "删除课程", "course:delete"),
                buildPermission(201, "查看知识点列表", "knowledge-point:list"),
                buildPermission(202, "新增知识点", "knowledge-point:add"),
                buildPermission(203, "编辑知识点", "knowledge-point:edit"),
                buildPermission(204, "删除知识点", "knowledge-point:delete"),
                buildPermission(301, "查看题库列表", "question:list"),
                buildPermission(302, "新增题库题目", "question:add"),
                buildPermission(303, "编辑题库题目", "question:edit"),
                buildPermission(304, "删除题库题目", "question:delete"),
                buildPermission(17, "查看培养方案列表", "training-program:list"),
                buildPermission(18, "新增培养方案", "training-program:add"),
                buildPermission(19, "编辑培养方案", "training-program:edit"),
                buildPermission(20, "删除培养方案", "training-program:delete"),
                buildPermission(101, "查看课程安排", "course-schedule:list"),
                buildPermission(102, "新增课程安排", "course-schedule:add"),
                buildPermission(103, "编辑课程安排", "course-schedule:edit"),
                buildPermission(104, "删除课程安排", "course-schedule:delete")
        );
    }

    private List<Permission> buildNormalTeacherPermissions() {
        return Arrays.asList(
                buildPermission(201, "查看知识点列表", "knowledge-point:list"),
                buildPermission(202, "新增知识点", "knowledge-point:add"),
                buildPermission(203, "编辑知识点", "knowledge-point:edit"),
                buildPermission(204, "删除知识点", "knowledge-point:delete"),
                buildPermission(301, "查看题库列表", "question:list"),
                buildPermission(302, "新增题库题目", "question:add"),
                buildPermission(303, "编辑题库题目", "question:edit"),
                buildPermission(304, "删除题库题目", "question:delete"),
                buildPermission(401, "查看试卷列表", "exam-paper:list"),
                buildPermission(402, "新增试卷", "exam-paper:add"),
                buildPermission(403, "编辑试卷", "exam-paper:edit"),
                buildPermission(404, "删除试卷", "exam-paper:delete")
        );
    }
}
