package com.ajwlforever.community.util;

public class RedisKeyUtil {
    private static  final String SPLIT =":";
    private static final String PREFIX_ENTITY_LIKE ="like:entity";
    private static final String PREFIX_USER_LIKE ="like:user";

    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_KAPATCH = "kaptch";

    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    //like:entity:entityType:entityId -> set(UserId)
    public static String getEntityLikeKey(int entityType,int entityId)
    {
        return  PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //like:user
    public static String getUserLikeKey(int userId)
    {
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    // 关注了谁， 某人关注了谁  用户关注了睡
    // followee:userId:entityType   -> zset(entityId,now) 按时间排序
    public static String getFolloweeKey(int entityType,int userId)
    {
        return  PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体粉丝
    //follower:entityType:entityId  ->zset(userId,now)
    public static final String getFollowerKey(int entityType,int entityId)
    {
        return  PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //验证码  kaptch:owner --> (text)
    public static final String getKaptchKey(String owner)
    {
        return PREFIX_KAPATCH+SPLIT+owner;
    }
    //Ticket ticket:(ticket) --> set(class LoginTicket)
    public static final String getTicketKey(String ticket)
    {
        return PREFIX_TICKET+SPLIT+ticket;
    }
    public static final String getUserKey(int userId )
    {
        return PREFIX_USER+SPLIT+userId;
    }

}
