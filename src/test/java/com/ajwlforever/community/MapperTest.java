package com.ajwlforever.community;


import com.ajwlforever.community.dao.DiscussPostMapper;
import com.ajwlforever.community.dao.Login_ticketMapper;
import com.ajwlforever.community.dao.UserMapper;
import com.ajwlforever.community.entity.DiscussPost;
import com.ajwlforever.community.entity.LoginTicket;
import com.ajwlforever.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class MapperTest {

    @Autowired(required=false)
    private UserMapper userMapper;

    @Autowired(required=false)
    private DiscussPostMapper discussPortMapper;

    @Autowired
    private Login_ticketMapper login_ticketMapper;

    //User selecy Test
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    //Inset User
    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    //Update User
    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    //Select Posts
    @Test
    public void  testSelectPosts()
    {
        List<DiscussPost> list = discussPortMapper.selectAllDiscussPosts(0,0,10);
        for(DiscussPost d : list)
        {
            System.out.println(d.toString());
        }
        System.out.println(list.size());
        int rows = discussPortMapper.selectDiscussPostRows(0);
        System.out.println("rows : "  + rows);
    }

    //Login_ticket
    @Test
    public void testInsertLogin_ticket()
    {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setId(1);
        loginTicket.setUserId(12);
        String s = "aakbjsdkjashdika";
        loginTicket.setTicket(s);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000 * 60 * 10)); //10min

        login_ticketMapper.insertLoginTicket(loginTicket);
        LoginTicket ticket = login_ticketMapper.selectByName(s);
        System.out.println(ticket);

        login_ticketMapper.updateStatus(s,1);
        ticket = login_ticketMapper.selectByName(s);
        System.out.println(ticket);

        login_ticketMapper.insertLoginTicket(loginTicket);
        ticket = login_ticketMapper.selectByName(s);
        System.out.println(ticket);

    }
}
