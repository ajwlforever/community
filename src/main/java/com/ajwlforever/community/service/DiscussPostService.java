package com.ajwlforever.community.service;


import com.ajwlforever.community.dao.DiscussPostMapper;
import com.ajwlforever.community.entity.DiscussPost;
import com.ajwlforever.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private  SensitiveFilter sensitiveFilter;

    public  List<DiscussPost> selectAllDiscussPosts(int userId , int offset , int limit )
    {
      return discussPostMapper.selectAllDiscussPosts(userId, offset, limit);
    }
    public int selectDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    public int addDiscussPost(DiscussPost post)
    {
        if(post==null)
        {
            throw  new IllegalArgumentException("参数不能为空!");

        }

        // 屏蔽HTML：标记语言
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //屏蔽敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);


    }
}
