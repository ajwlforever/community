package com.ajwlforever.community.controller;


import com.ajwlforever.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){ return "Hello Spring Boot!!!"; }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response)
    {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements())
        {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+" : "+value);
        }

        System.out.println(request.getParameter("code"));


        response.setContentType("text/html;charset=utf-8");
        PrintWriter p  = null;
        try {
            p = response.getWriter();
            p.write("<a href=\"http://yuqixian.top\">虞启贤的播客主页</a>");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            p.close();
        }

    }


    //Get
    //students?current=1&limit=20
    @RequestMapping(path="/students" , method = RequestMethod.GET)
    @ResponseBody
    public String students(
            @RequestParam(name="current" ,required=false, defaultValue="1") int current,
            @RequestParam(name="limit" ,required=false, defaultValue="20") int limit)
    {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }
    @RequestMapping(path="/student/{id}" , method = RequestMethod.GET)
    @ResponseBody
    public String student (@PathVariable("id") int id)
    {
        System.out.println(id);
             return "a student ";

    }
    //Post 请求
    @RequestMapping(path="/student" , method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent (String name,int age)
    {
        System.out.println(name);
        System.out.println(age);
        return "a student ";

    }

    //响应HTML数据
    @RequestMapping(path = "/teacher" ,method = RequestMethod.GET)
    public ModelAndView getTeacher()
    {
        ModelAndView modelAndView  = new ModelAndView();
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age",30);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school" ,method = RequestMethod.GET)
    public String getSchool(Model model)
    {
        model.addAttribute("name", "山东农业大学");
        model.addAttribute("age",114);

        return "/demo/view";
    }

    //响应Json数据
    //Java对象 --> Json字符串 --> Js对象

    @RequestMapping(path = "/emp" ,method = RequestMethod.GET)
    @ResponseBody
    public Map<String , Object> getEmp()
    {
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",15);
        emp.put("salary",8000);
        return  emp;
    }

    @RequestMapping(path = "/emps" ,method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String , Object> > getEmps()
    {
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",15);
        emp.put("salary",8000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","李四");
        emp.put("age",16);
        emp.put("salary",9000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","王五");
        emp.put("age",17);
        emp.put("salary",10000);
        list.add(emp);

        return  list;
    }

    @RequestMapping(path = "/index" , method = RequestMethod.GET)
    public String index(Model model)
    {
        return "tempindex";
    }


    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletRequest request,HttpServletResponse response)
    {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return request.getLocalAddr().toString();
    }
    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {

        return code;
    }



}
