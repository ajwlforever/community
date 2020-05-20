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
}
