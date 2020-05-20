package com.ajwlforever.community;


import com.ajwlforever.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendMail()
    {
        mailClient.sendMail("2353350597@qq.com","相信自己","<h1>你的未来由你自己定义</h1>" +
                "<h2>Your future is defined by you!</h2>");
    }


    @Test
    public void sendHTMLMail()
    {
        Context context = new Context();
        context.setVariable("username","ajwlforever");

        String content  = templateEngine.process("/mail/activation",context);
        mailClient.sendMail("2353350597@qq.com","相信自己",content);

    }
}
