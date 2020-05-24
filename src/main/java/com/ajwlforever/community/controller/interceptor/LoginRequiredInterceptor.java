package com.ajwlforever.community.controller.interceptor;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    //get handler
        if(handler instanceof HandlerMethod)
        {
            HandlerMethod m = (HandlerMethod)  handler;
            Method method = m.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired != null && hostHolder.getUser()==null)
            {
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }

        }

        return true;
    }
}
