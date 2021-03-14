package com.ajwlforever.community.service;

import com.ajwlforever.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //d点赞
    public void like(int userId, int entityType, int entityId,int entityUserId)
    {
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
////
////        //查询是否已经赞过
////        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
////
////
////        if(isMember)
////        {
////            redisTemplate.opsForSet().remove(entityLikeKey,userId);
////
////        }else
////            redisTemplate.opsForSet().add(entityLikeKey,userId);

        //redis 事务

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //查询是否已经赞过
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();
                if(isMember)
                {
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else
                {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);

    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ==true? 1:0;
    }

    //查询某人的总被点赞数量
    public  int findUserLikeCount(int userId)
    {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer res = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return  res== null? 0:res.intValue();
    }
}
