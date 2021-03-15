package com.ajwlforever.community.service;


import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.util.ComunityConstant;
import com.ajwlforever.community.util.HostHolder;
import com.ajwlforever.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements ComunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;



    public void follow(int userId, int entityType, int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations op) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,userId);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                op.multi();
                // 用户关注了啥
                op.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());

                //某实体的粉丝+1
                op.opsForZSet().add(followerKey,userId,System.currentTimeMillis());


                op.exec();

                return null;
            }
        });
    }

    public void unfollow(int userId, int entityType, int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations op) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,userId);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                op.multi();
                // 用户关注了啥
                op.opsForZSet().remove(followeeKey,entityId);

                //某实体的粉丝+1
                op.opsForZSet().remove(followerKey,userId);


                op.exec();

                return null;
            }
        });
    }
    // 得到用户关注了多少
    public long getFolloweeCount(int userId,int entityType)
    {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,userId);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    //得到该实体的粉丝数量
    public long getFollowerCount(int entityType, int entityId)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询用户是否关注了某个实体
    public boolean isFollow(int userId,int entityType,int entityId)
    {
        User user = hostHolder.getUser();
        if(user==null) return false;

        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,userId);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }

    //查询用户的关注列表
    public List<Map<String,Object>> getFollowees(int userId, int offset,int limit)
    {
        String folooweeKey = RedisKeyUtil.getFolloweeKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(folooweeKey,offset,offset+limit-1);

        User loginUser = hostHolder.getUser();

        if(targetIds == null) return null;
        List<Map<String,Object>> list = new ArrayList<>();

        for(Integer i : targetIds)
        {
            User user = userService.findUserById(i);
            Map<String,Object> map = new HashMap<>();
            map.put("isFollowed",isFollow(loginUser.getId(),ENTITY_TYPE_USER,i)); //看登录用户是否关注了这个人
            map.put("user",user);
            map.put("followTime",new Date(redisTemplate.opsForZSet().score(folooweeKey,i).longValue()));
            list.add(map);

        }
        return list;
    }

    //查询用户的粉丝列表
    public List<Map<String,Object>> getFollowers(int entityId, int offset,int limit)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,entityId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset+limit-1);

        if(targetIds == null) return null;
        List<Map<String,Object>> list = new ArrayList<>();
        User loginUser = hostHolder.getUser();
        for(Integer i : targetIds)
        {
            User user = userService.findUserById(i);
            Map<String,Object> map = new HashMap<>();
            map.put("isFollowed",isFollow(loginUser.getId(),ENTITY_TYPE_USER,i)); //看登录用户是否关注了这个人
            map.put("user",user);
            map.put("followTime",new Date(redisTemplate.opsForZSet().score(followerKey,i).longValue()));
            list.add(map);

        }
        return list;
    }
}
