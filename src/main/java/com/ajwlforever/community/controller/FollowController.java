package com.ajwlforever.community.controller;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.FollowService;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {

    @Autowired
    private FollowService  followService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/follow")
    @LoginRequired
    @ResponseBody
    public String follow(int entityType, int entityId)
    {
        User user = hostHolder.getUser();

        followService.follow(user.getId(),entityType,entityId);

        return CommunityUtil.toJsonString(0,"已关注");
    }

    @PostMapping("/unfollow")
    @LoginRequired
    @ResponseBody
    public String unfollow(int entityType, int entityId)
    {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.toJsonString(0,"已取消关注");
    }

}
