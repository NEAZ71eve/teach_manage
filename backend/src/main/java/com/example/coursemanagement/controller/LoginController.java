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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    
    // 密码编码器，用于密码验证
    private final PasswordEncoder passwordEncoder;
    
    // 构造函数注入
    public LoginController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
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
                
                // 验证密码（支持明文和BCrypt加密密码，以及默认密码）
                boolean passwordValid = false;
                
                // 检查是否是默认密码（用于开发测试）
                if (loginRequest.getPassword().equals("admin123") && user.getUsername().equals("admin")) {
                    passwordValid = true;
                } 
                // 检查是否是BCrypt加密密码
                else if (user.getPassword().startsWith("$2a$")) {
                    // BCrypt加密密码，使用PasswordEncoder验证
                    passwordValid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
                } 
                // 明文密码验证
                else {
                    // 明文密码（仅用于开发测试）
                    passwordValid = user.getPassword().equals(loginRequest.getPassword());
                }
                
                if (passwordValid) {
                    // 获取用户的角色列表
                    List<Integer> roleIds = userRoleService.findByUserId(user.getUserId()).stream()
                            .map(role -> role.getRoleId())
                            .collect(Collectors.toList());
                    
                    // 为admin用户添加默认角色（如果没有角色）
                    if (roleIds.isEmpty() && user.getUsername().equals("admin")) {
                        // 默认添加系统管理员角色
                        roleIds.add(1);
                    }
                    
                    // 获取角色信息
                    List<Role> roles = new ArrayList<>();
                    for (Integer roleId : roleIds) {
                        Optional<Role> roleOptional = roleService.findById(roleId);
                        roleOptional.ifPresent(roles::add);
                    }
                    
                    // 如果没有角色，为admin用户添加默认角色信息
                    if (roles.isEmpty() && user.getUsername().equals("admin")) {
                        Role adminRole = new Role();
                        adminRole.setRoleId(1);
                        adminRole.setRoleName("系统管理员");
                        adminRole.setRoleDesc("拥有系统所有权限");
                        roles.add(adminRole);
                    }
                    
                    // 获取角色的权限列表
                    Set<Integer> permissionIdSet = new HashSet<>();
                    for (Integer roleId : roleIds) {
                        List<Integer> rolePermissionIds = rolePermissionService.findByRoleId(roleId).stream()
                                .map(permission -> permission.getPermissionId())
                                .collect(Collectors.toList());
                        permissionIdSet.addAll(rolePermissionIds);
                    }
                    
                    // 获取所有权限（为admin用户默认分配所有权限）
                    List<Permission> permissions = new ArrayList<>();
                    if (user.getUsername().equals("admin")) {
                        // 直接生成默认权限，不依赖数据库
                        List<Permission> defaultPermissions = new ArrayList<>();
                        
                        // 添加课程管理权限
                        Permission courseListPerm = new Permission();
                        courseListPerm.setPermissionId(1);
                        courseListPerm.setPermissionName("查看课程列表");
                        courseListPerm.setPermissionCode("course:list");
                        defaultPermissions.add(courseListPerm);
                        
                        Permission courseAddPerm = new Permission();
                        courseAddPerm.setPermissionId(2);
                        courseAddPerm.setPermissionName("新增课程");
                        courseAddPerm.setPermissionCode("course:add");
                        defaultPermissions.add(courseAddPerm);
                        
                        Permission courseEditPerm = new Permission();
                        courseEditPerm.setPermissionId(3);
                        courseEditPerm.setPermissionName("编辑课程");
                        courseEditPerm.setPermissionCode("course:edit");
                        defaultPermissions.add(courseEditPerm);
                        
                        Permission courseDeletePerm = new Permission();
                        courseDeletePerm.setPermissionId(4);
                        courseDeletePerm.setPermissionName("删除课程");
                        courseDeletePerm.setPermissionCode("course:delete");
                        defaultPermissions.add(courseDeletePerm);
                        
                        // 添加用户管理权限
                        Permission userListPerm = new Permission();
                        userListPerm.setPermissionId(5);
                        userListPerm.setPermissionName("查看用户列表");
                        userListPerm.setPermissionCode("user:list");
                        defaultPermissions.add(userListPerm);
                        
                        Permission userAddPerm = new Permission();
                        userAddPerm.setPermissionId(6);
                        userAddPerm.setPermissionName("新增用户");
                        userAddPerm.setPermissionCode("user:add");
                        defaultPermissions.add(userAddPerm);
                        
                        Permission userEditPerm = new Permission();
                        userEditPerm.setPermissionId(7);
                        userEditPerm.setPermissionName("编辑用户");
                        userEditPerm.setPermissionCode("user:edit");
                        defaultPermissions.add(userEditPerm);
                        
                        Permission userDeletePerm = new Permission();
                        userDeletePerm.setPermissionId(8);
                        userDeletePerm.setPermissionName("删除用户");
                        userDeletePerm.setPermissionCode("user:delete");
                        defaultPermissions.add(userDeletePerm);
                        
                        // 添加角色管理权限
                        Permission roleListPerm = new Permission();
                        roleListPerm.setPermissionId(9);
                        roleListPerm.setPermissionName("查看角色列表");
                        roleListPerm.setPermissionCode("role:list");
                        defaultPermissions.add(roleListPerm);
                        
                        // 添加权限管理权限
                        Permission permListPerm = new Permission();
                        permListPerm.setPermissionId(10);
                        permListPerm.setPermissionName("查看权限列表");
                        permListPerm.setPermissionCode("permission:list");
                        defaultPermissions.add(permListPerm);
                        
                        // 添加培养方案管理权限
                        Permission programListPerm = new Permission();
                        programListPerm.setPermissionId(11);
                        programListPerm.setPermissionName("查看培养方案列表");
                        programListPerm.setPermissionCode("training-program:list");
                        defaultPermissions.add(programListPerm);
                        
                        // 添加知识点管理权限
                        Permission kpListPerm = new Permission();
                        kpListPerm.setPermissionId(12);
                        kpListPerm.setPermissionName("查看知识点列表");
                        kpListPerm.setPermissionCode("knowledge-point:list");
                        defaultPermissions.add(kpListPerm);
                        
                        // 添加题库管理权限
                        Permission questionListPerm = new Permission();
                        questionListPerm.setPermissionId(13);
                        questionListPerm.setPermissionName("查看题库列表");
                        questionListPerm.setPermissionCode("question:list");
                        defaultPermissions.add(questionListPerm);
                        
                        // 添加试卷管理权限
                        Permission paperListPerm = new Permission();
                        paperListPerm.setPermissionId(14);
                        paperListPerm.setPermissionName("查看试卷列表");
                        paperListPerm.setPermissionCode("exam-paper:list");
                        defaultPermissions.add(paperListPerm);
                        
                        // 添加统计分析权限
                        Permission statsPerm = new Permission();
                        statsPerm.setPermissionId(15);
                        statsPerm.setPermissionName("查看统计数据");
                        statsPerm.setPermissionCode("statistics:view");
                        defaultPermissions.add(statsPerm);
                        
                        // 添加实践项目管理权限
                        Permission practiceListPerm = new Permission();
                        practiceListPerm.setPermissionId(16);
                        practiceListPerm.setPermissionName("查看实践项目列表");
                        practiceListPerm.setPermissionCode("practice-project:list");
                        defaultPermissions.add(practiceListPerm);
                        
                        Permission practiceAddPerm = new Permission();
                        practiceAddPerm.setPermissionId(17);
                        practiceAddPerm.setPermissionName("新增实践项目");
                        practiceAddPerm.setPermissionCode("practice-project:add");
                        defaultPermissions.add(practiceAddPerm);
                        
                        Permission practiceEditPerm = new Permission();
                        practiceEditPerm.setPermissionId(18);
                        practiceEditPerm.setPermissionName("编辑实践项目");
                        practiceEditPerm.setPermissionCode("practice-project:edit");
                        defaultPermissions.add(practiceEditPerm);
                        
                        Permission practiceDeletePerm = new Permission();
                        practiceDeletePerm.setPermissionId(19);
                        practiceDeletePerm.setPermissionName("删除实践项目");
                        practiceDeletePerm.setPermissionCode("practice-project:delete");
                        defaultPermissions.add(practiceDeletePerm);
                        
                        // 添加学期管理权限
                        Permission semesterListPerm = new Permission();
                        semesterListPerm.setPermissionId(20);
                        semesterListPerm.setPermissionName("查看学期列表");
                        semesterListPerm.setPermissionCode("semester:list");
                        defaultPermissions.add(semesterListPerm);
                        
                        Permission semesterAddPerm = new Permission();
                        semesterAddPerm.setPermissionId(21);
                        semesterAddPerm.setPermissionName("新增学期");
                        semesterAddPerm.setPermissionCode("semester:add");
                        defaultPermissions.add(semesterAddPerm);
                        
                        Permission semesterEditPerm = new Permission();
                        semesterEditPerm.setPermissionId(22);
                        semesterEditPerm.setPermissionName("编辑学期");
                        semesterEditPerm.setPermissionCode("semester:edit");
                        defaultPermissions.add(semesterEditPerm);
                        
                        Permission semesterDeletePerm = new Permission();
                        semesterDeletePerm.setPermissionId(23);
                        semesterDeletePerm.setPermissionName("删除学期");
                        semesterDeletePerm.setPermissionCode("semester:delete");
                        defaultPermissions.add(semesterDeletePerm);
                        
                        // 添加学期课表权限
                        Permission semesterSchedulePerm = new Permission();
                        semesterSchedulePerm.setPermissionId(24);
                        semesterSchedulePerm.setPermissionName("查看学期课表");
                        semesterSchedulePerm.setPermissionCode("semester-schedule:view");
                        defaultPermissions.add(semesterSchedulePerm);
                        
                        // 添加完整的角色管理权限
                        Permission roleAddPerm = new Permission();
                        roleAddPerm.setPermissionId(25);
                        roleAddPerm.setPermissionName("新增角色");
                        roleAddPerm.setPermissionCode("role:add");
                        defaultPermissions.add(roleAddPerm);
                        
                        Permission roleEditPerm = new Permission();
                        roleEditPerm.setPermissionId(26);
                        roleEditPerm.setPermissionName("编辑角色");
                        roleEditPerm.setPermissionCode("role:edit");
                        defaultPermissions.add(roleEditPerm);
                        
                        Permission roleDeletePerm = new Permission();
                        roleDeletePerm.setPermissionId(27);
                        roleDeletePerm.setPermissionName("删除角色");
                        roleDeletePerm.setPermissionCode("role:delete");
                        defaultPermissions.add(roleDeletePerm);
                        
                        // 添加完整的权限管理权限
                        Permission permAddPerm = new Permission();
                        permAddPerm.setPermissionId(28);
                        permAddPerm.setPermissionName("新增权限");
                        permAddPerm.setPermissionCode("permission:add");
                        defaultPermissions.add(permAddPerm);
                        
                        Permission permEditPerm = new Permission();
                        permEditPerm.setPermissionId(29);
                        permEditPerm.setPermissionName("编辑权限");
                        permEditPerm.setPermissionCode("permission:edit");
                        defaultPermissions.add(permEditPerm);
                        
                        Permission permDeletePerm = new Permission();
                        permDeletePerm.setPermissionId(30);
                        permDeletePerm.setPermissionName("删除权限");
                        permDeletePerm.setPermissionCode("permission:delete");
                        defaultPermissions.add(permDeletePerm);
                        
                        permissions = defaultPermissions;
                    } else {
                        // 获取权限信息
                        for (Integer permissionId : permissionIdSet) {
                            Optional<Permission> permissionOptional = permissionService.findById(permissionId);
                            permissionOptional.ifPresent(permissions::add);
                        }
                        
                        // 确保所有用户至少有基本的查看权限
                        if (permissions.isEmpty()) {
                            // 添加基本的查看权限
                            List<Permission> basicPermissions = new ArrayList<>();
                            
                            // 添加课程查看权限
                            Permission courseListPerm = new Permission();
                            courseListPerm.setPermissionId(1);
                            courseListPerm.setPermissionName("查看课程列表");
                            courseListPerm.setPermissionCode("course:list");
                            basicPermissions.add(courseListPerm);
                            
                            // 添加培养方案查看权限
                            Permission programListPerm = new Permission();
                            programListPerm.setPermissionId(11);
                            programListPerm.setPermissionName("查看培养方案列表");
                            programListPerm.setPermissionCode("training-program:list");
                            basicPermissions.add(programListPerm);
                            
                            // 添加知识点查看权限
                            Permission kpListPerm = new Permission();
                            kpListPerm.setPermissionId(12);
                            kpListPerm.setPermissionName("查看知识点列表");
                            kpListPerm.setPermissionCode("knowledge-point:list");
                            basicPermissions.add(kpListPerm);
                            
                            // 添加题库查看权限
                            Permission questionListPerm = new Permission();
                            questionListPerm.setPermissionId(13);
                            questionListPerm.setPermissionName("查看题库列表");
                            questionListPerm.setPermissionCode("question:list");
                            basicPermissions.add(questionListPerm);
                            
                            // 添加试卷查看权限
                            Permission paperListPerm = new Permission();
                            paperListPerm.setPermissionId(14);
                            paperListPerm.setPermissionName("查看试卷列表");
                            paperListPerm.setPermissionCode("exam-paper:list");
                            basicPermissions.add(paperListPerm);
                            
                            // 添加实践项目查看权限
                            Permission practiceListPerm = new Permission();
                            practiceListPerm.setPermissionId(16);
                            practiceListPerm.setPermissionName("查看实践项目列表");
                            practiceListPerm.setPermissionCode("practice-project:list");
                            basicPermissions.add(practiceListPerm);
                            
                            // 添加学期查看权限
                            Permission semesterListPerm = new Permission();
                            semesterListPerm.setPermissionId(20);
                            semesterListPerm.setPermissionName("查看学期列表");
                            semesterListPerm.setPermissionCode("semester:list");
                            basicPermissions.add(semesterListPerm);
                            
                            // 添加学期课表查看权限
                            Permission semesterSchedulePerm = new Permission();
                            semesterSchedulePerm.setPermissionId(24);
                            semesterSchedulePerm.setPermissionName("查看学期课表");
                            semesterSchedulePerm.setPermissionCode("semester-schedule:view");
                            basicPermissions.add(semesterSchedulePerm);
                            
                            permissions = basicPermissions;
                        } else {
                            // 对于普通教师和学生，移除管理权限（如add, edit, delete）
                            List<String> roleNames = roles.stream()
                                    .map(Role::getRoleName)
                                    .collect(Collectors.toList());
                            
                            // 检查是否是普通教师或学生角色
                            boolean isNormalUser = roleNames.stream().anyMatch(roleName -> 
                                    roleName.contains("教师") || roleName.contains("学生") || roleName.equals("teacher") || roleName.equals("student")
                            );
                            
                            if (isNormalUser) {
                                // 过滤掉管理权限，只保留查看权限
                                permissions = permissions.stream()
                                        .filter(permission -> {
                                            String permissionCode = permission.getPermissionCode();
                                            // 只保留查看权限（以:list或:view结尾的权限）
                                            return permissionCode.endsWith(":list") || permissionCode.endsWith(":view");
                                        })
                                        .collect(Collectors.toList());
                            }
                        }
                    }
                    
                    // 提取角色名称列表
                    List<String> roleNames = roles.stream()
                            .map(role -> role.getRoleName())
                            .collect(Collectors.toList());
                    
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
}