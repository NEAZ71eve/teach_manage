package com.example.coursemanagement.interceptor;

import com.example.coursemanagement.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * JWT拦截器，用于验证令牌和检查权限
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许OPTIONS请求通过，用于CORS预检
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取请求头中的Authorization
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"未提供有效的认证令牌\"}");
            return false;
        }

        // 提取token
        token = token.substring(7);

        try {
            // 验证token
            if (!jwtUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"令牌已过期或无效\"}");
                return false;
            }

            // 获取用户权限
            List<String> permissions = jwtUtils.getPermissionsFromToken(token);
            List<String> roles = jwtUtils.getRolesFromToken(token);

            // 检查请求路径和方法，验证权限
            String requestURI = request.getRequestURI();
            String method = request.getMethod();

            // 权限检查逻辑
            boolean hasPermission = checkPermission(requestURI, method, permissions, roles);
            if (!hasPermission) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"没有权限执行此操作\"}");
                return false;
            }

            // 将用户信息存储到请求属性中，供后续使用
            request.setAttribute("userId", jwtUtils.getUserIdFromToken(token));
            request.setAttribute("username", jwtUtils.getUsernameFromToken(token));
            request.setAttribute("roles", roles);
            request.setAttribute("permissions", permissions);

            return true;
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"令牌已过期\"}");
            return false;
        } catch (MalformedJwtException | SignatureException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"无效的令牌格式\"}");
            return false;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"认证失败\"}");
            return false;
        }
    }

    /**
     * 检查用户是否有权限访问该资源
     */
    private boolean checkPermission(String requestURI, String method, List<String> permissions, List<String> roles) {
        // 系统管理员和学院管理员拥有所有权限
        boolean isAdmin = roles.contains("系统管理员") || roles.contains("学院管理员");
        if (isAdmin) {
            return true;
        }

        // 权限映射，将请求路径和方法映射到对应的权限代码
        String permissionCode = getPermissionCode(requestURI, method);
        if (permissionCode == null) {
            // 如果没有找到对应的权限代码，默认允许访问
            return true;
        }

        // 检查用户是否拥有该权限
        return permissions.contains(permissionCode);
    }

    /**
     * 根据请求路径和方法获取对应的权限代码
     */
    private String getPermissionCode(String requestURI, String method) {
        // 简化的权限映射，实际应用中应该从数据库或配置文件加载
        if (requestURI.startsWith("/course")) {
            if (method.equals("GET")) {
                if (requestURI.endsWith("/page") || requestURI.equals("/course")) {
                    return "course:list";
                }
            } else if (method.equals("POST")) {
                return "course:add";
            } else if (method.equals("PUT")) {
                return "course:edit";
            } else if (method.equals("DELETE")) {
                return "course:delete";
            }
        } else if (requestURI.startsWith("/training-program")) {
            if (method.equals("GET")) {
                return "trainingProgram:list";
            } else if (method.equals("POST")) {
                return "trainingProgram:add";
            } else if (method.equals("PUT")) {
                return "trainingProgram:edit";
            } else if (method.equals("DELETE")) {
                return "trainingProgram:delete";
            }
        } else if (requestURI.startsWith("/knowledge-point")) {
            if (method.equals("GET")) {
                return "knowledge-point:list";
            } else if (method.equals("POST")) {
                return "knowledge-point:add";
            } else if (method.equals("PUT")) {
                return "knowledge-point:edit";
            } else if (method.equals("DELETE")) {
                return "knowledge-point:delete";
            }
        } else if (requestURI.startsWith("/question-bank")) {
            if (method.equals("GET")) {
                return "question:list";
            } else if (method.equals("POST")) {
                return "question:add";
            } else if (method.equals("PUT")) {
                return "question:edit";
            } else if (method.equals("DELETE")) {
                return "question:delete";
            }
        } else if (requestURI.startsWith("/exam-papers")) {
            if (method.equals("GET")) {
                return "examPaper:list";
            } else if (method.equals("POST")) {
                if (requestURI.endsWith("/generate")) {
                    return "examPaper:generate";
                }
                return "examPaper:add";
            } else if (method.equals("PUT")) {
                return "examPaper:edit";
            } else if (method.equals("DELETE")) {
                return "examPaper:delete";
            }
        } else if (requestURI.startsWith("/semesters")) {
            if (method.equals("GET")) {
                return "semester:list";
            } else if (method.equals("POST")) {
                return "semester:add";
            } else if (method.equals("PUT")) {
                return "semester:edit";
            } else if (method.equals("DELETE")) {
                return "semester:delete";
            }
        } else if (requestURI.equals("/statistics")) {
            if (method.equals("GET")) {
                return "statistics:view";
            }
        }

        return null;
    }
}