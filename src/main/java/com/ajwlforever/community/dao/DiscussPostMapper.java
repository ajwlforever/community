package com.ajwlforever.community.dao;

import com.ajwlforever.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public  interface DiscussPostMapper {


    // 找所有帖子，userId为0是所有，>0是具体用户的帖子
     List<DiscussPost> selectAllDiscussPosts(int userId , int offset , int limit );

     //动态sql 只有一个Param要@Param
     int selectDiscussPostRows(int userId);
}
