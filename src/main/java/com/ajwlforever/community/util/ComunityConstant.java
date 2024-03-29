package com.ajwlforever.community.util;

public interface ComunityConstant {

    /*
    activation success
     */
    int ACTIVATION_SUCCESS = 0;
    /*
    activation repeat
     */
    int ACTIVATION_REPEAT = 1;
    /*
    activation failure
     */
    int ACTIVATION_FAILURE = 2;

    /*
    正常的到期时间
     */
    int DEFAULT_EXPIRED_TIME = 3600 * 12;

    /*
    长一点的
     */
    int REMEMBER_EXPIRED_TIME = 3600 * 24 * 7;


    /**
     * 实体类型: 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型: 评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /*
     实体类型: 用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 系统用户的Id
     */
    int SYSTEM_USERID = 1;
}
