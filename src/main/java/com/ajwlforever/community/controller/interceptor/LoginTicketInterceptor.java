package com.ajwlforever.community.controller.interceptor;

import com.ajwlforever.community.entity.LoginTicket;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.UserService;
import com.ajwlforever.community.util.CookieUtil;
import com.ajwlforever.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {


    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String ticket = CookieUtil.getCookie(request,"ticket");
        if( ticket!=null )
        {
            // LoginTicket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date()))
            {
                //ticket is 有效
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
                return true;
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null&&modelAndView!=null)
        {
            modelAndView.addObject("loginUser",user);
        }
    }
}
