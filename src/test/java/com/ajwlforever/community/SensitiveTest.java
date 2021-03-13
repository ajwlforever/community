package com.ajwlforever.community;


import com.ajwlforever.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private  SensitiveFilter sensitiveFilter;
    @Test
    public void sensitiveTes()
    {
        String text = "吃喝实打实大苏打嫖赌撒大苏打实打实的嫖娼你妈死了我草泥马fbac";
        String result = sensitiveFilter.filter(text);
        System.out.println(result);
    }
}
