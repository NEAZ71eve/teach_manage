package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.LoginRequest;
import com.example.coursemanagement.entity.LoginResponse;
import com.example.coursemanagement.entity.User;
import com.example.coursemanagement.service.UserService;
import com.example.coursemanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
                // 验证密码（当前是明文验证，后续可以改为加密验证）
                if (user.getPassword().equals(loginRequest.getPassword())) {
                    // 生成JWT令牌
                    String token = jwtUtils.generateToken(user.getUserId(), user.getUsername());
                    
                    // 设置响应信息
                    response.setSuccess(true);
                    response.setMessage("登录成功");
                    response.setToken(token);
                    response.setUser(user);
                    
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