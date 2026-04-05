package com.madgod.library.utils;

import com.madgod.library.entity.User;
import com.madgod.library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Calendar;

@Component
public class TokenUtils {

    private static IUserService userService;

    @Autowired
    public TokenUtils(IUserService userService) {
        TokenUtils.userService = userService;
    }

    /**
     * 生成token
     * @param user
     * @return
     */
    public static String genToken(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 2);
        return JWT.create()
                .withExpiresAt(calendar.getTime())
                .withAudience(user.getId().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));
    }

    /**
     * 获取token中的用户信息
     * @return
     */
    public static User getUser(String token) {
        try {
            if(token.isEmpty()){
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                token = request.getHeader("token");
            }
            String aud = JWT.decode(token).getAudience().get(0);
            Integer userId = Integer.valueOf(aud);
            User user = userService.getById(userId);
            JWT.require(Algorithm.HMAC256(user.getPassword())).build().verify(token);
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}