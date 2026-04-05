package com.madgod.library.utils;

import com.madgod.library.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public final class CurrentUserUtils {
    public static User getCurrentUser() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return null;
        }
        Object user = servletAttrs.getRequest().getAttribute("currentUser");
        return (user instanceof User) ? (User) user : null;
    }
}
