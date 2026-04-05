package com.madgod.library.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madgod.library.entity.User;
import com.madgod.library.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 静态资源、Swagger、非controller方法 等直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response, "未登录或token缺失");
            return false;
        }

        User user = TokenUtils.getUser(token);
        if (user == null) {
            writeUnauthorized(response, "token无效或已过期");
            return false;
        }

        // 给方法权限
        Privilege privilege = getPrivilege(handlerMethod);
        Integer role = user.getRole();
        if (privilege != null && (role == null || role < privilege.value())) {
            writeForbidden(response, "权限不足");
            return false;
        }

        request.setAttribute("currentUser", user);
        return true;
    }

    private Privilege getPrivilege(HandlerMethod handlerMethod) {
        // 寻找方法上的Privilege注解
        Privilege methodAnnotation = handlerMethod.getMethodAnnotation(Privilege.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return handlerMethod.getBeanType().getAnnotation(Privilege.class);
    }

    private String resolveToken(HttpServletRequest request) {
        // 获取相应token
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }
        return request.getHeader("token");
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(Result.error(401, message)));
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(Result.error(403, message)));
    }
}
