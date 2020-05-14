package com.ajwlforever.community.controller;


import com.ajwlforever.community.dao.DiscussPostMapper;
import com.ajwlforever.community.dao.UserMapper;
import com.ajwlforever.community.entity.DiscussPost;
import com.ajwlforever.community.entity.Page;
import com.ajwlforever.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    首页的控制器
 *  author : ajwlforever
 *   version : 1.0
 */
@Controller
public class HomeController {

    @Autowired(required=false)
    private DiscussPostMapper discussPostMapper;

    @Autowired(required=false)
    private UserMapper userMapper;


    @RequestMapping(path = "/index" , method = RequestMethod.GET)
    public String Index(Model model, Page page)
    {
        page.setRows(discussPostMapper.selectDiscussPostRows(0));
        page.setPath("/index");
        System.out.println("row:" + page.getRows());
        List<DiscussPost> list = discussPostMapper.selectAllDiscussPosts(0,page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list !=null )
        {
            for( DiscussPost discussPost1 : list)
            {
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost1);
                User user = userMapper.selectById(discussPost1.getUserId());
                map.put("user",user);
                discussPosts.add(map);

            }

        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

}
