package com.ajwlforever.community.controller;


import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.UserService;
import com.ajwlforever.community.util.ComunityConstant;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/*
    loogin
 */
@Controller
public class LoginController implements ComunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;


    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 注册界面
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String resgiter(Model model)
    {
        return "/site/register";
    }

    // 登录界面
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String login(Model model)
    {
        return "/site/login";
    }

    //注册的表单提交
    @RequestMapping(path= "/register" , method = RequestMethod.POST)
    public String check(Model model, User user)
    {
        Map<String,Object> map = userService.register(user);
        if(map==null || map.isEmpty())
        {
            //activation success
            model.addAttribute("msg","注册成功，我们已经望您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";

        }else {
            // failure back to register
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 激活验证
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model,@PathVariable("userId") int userId, @PathVariable("code") String code)
    {
        int result = userService.activation(userId,code);
        if(result==ACTIVATION_REPEAT)
        {
            model.addAttribute("msg","无效操作，该账号已经激活！");
            model.addAttribute("target","/index");

        }else
            if(result==ACTIVATION_SUCCESS)
            {
                model.addAttribute("msg","激活成功，您的账号已经可以正常使用！");
                model.addAttribute("target","/login");
            }else
            {
                model.addAttribute("msg","激活失败，账号不存在或激活码不正确！");
                model.addAttribute("target","/register");
            }
            return "/site/operate-result";
    }


    //
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session)
    {
            String  text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha",text);
        //图片输出到浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());

            e.printStackTrace();
        }


    }

    @RequestMapping(path = "/login" , method = RequestMethod.POST)
    public String login(String username, String password,String code, boolean rememberMe, Model model,
                        HttpSession session,HttpServletResponse response)
    {
        //shijian
        String kaptcha =  (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code) ||StringUtils.isBlank(kaptcha) || !kaptcha.equals(code))
        {
            model.addAttribute("codeMsg","验证码不正确");
            //验证码不对
            return "/site/login";
        }

        //检查账号密码

        int exprireTimes = rememberMe ? DEFAULT_EXPIRED_TIME : REMEMBER_EXPIRED_TIME;
        Map<String,Object> map = userService.login(username,password,exprireTimes);
        if(map.containsKey("ticket"))
        {
            //验证通过 登录成功
            Cookie cookie = new Cookie("ticket",(String)map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(exprireTimes);
            response.addCookie(cookie);

            return "redirect:/index";

        }
        else
        {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path="/logout" ,method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket)
    {
            userService.logout(ticket);
            return "redirect:/login";
    }

}
