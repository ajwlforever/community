package com.ajwlforever.community.dao;

import com.ajwlforever.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Mapper
@Repository
@Deprecated
public interface Login_ticketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true , keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select(
            {
                    "select id,user_id,ticket,status,expired ",
                    "from login_ticket where ticket=#{ticket}"
            }
    )
    LoginTicket selectByName(String ticket);

    @Select(
            {
                    "select id,user_id,ticket,status,expired ",
                    "from login_ticket where user_id=#{user_id}"
            }
    )
    LoginTicket selectByUserID(int user_id);

   @Update("update login_ticket set expired=#{expired} where ticket=#{ticket}")
   int updateExpired(String ticket, Date expired);

    @Update(
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    )
    int updateStatus(String ticket,int status);

//    @Update({
//            "<script>",
//            "update login_ticket set status=#{status} where ticket=#{ticket} ",
//            "<if test=\"ticket!=null\"> ",
//            "and 1=1 ",
//            "</if>",
//            "</script>"
//    })

}
