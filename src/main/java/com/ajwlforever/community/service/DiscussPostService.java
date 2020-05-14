package com.ajwlforever.community.service;


import com.ajwlforever.community.dao.DiscussPostMapper;
import com.ajwlforever.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public  List<DiscussPost> selectAllDiscussPosts(int userId , int offset , int limit )
    {
      return discussPostMapper.selectAllDiscussPosts(userId, offset, limit);
    }
    public int selectDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
