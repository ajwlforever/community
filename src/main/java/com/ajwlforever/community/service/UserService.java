package com.ajwlforever.community.service;

import com.ajwlforever.community.dao.Login_ticketMapper;
import com.ajwlforever.community.dao.UserMapper;
import com.ajwlforever.community.entity.LoginTicket;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.ComunityConstant;
import com.ajwlforever.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements ComunityConstant {

    @Autowired(required=false)
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String doMain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private Login_ticketMapper login_ticketMapper;

//register check
    public Map<String,Object> register(User user)
    {
        Map<String,Object> map = new HashMap<>();

        if(user == null)
        {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername()))
        {
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword()))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }if(StringUtils.isBlank(user.getEmail()))
        {
        map.put("emailMsg","邮箱名不能为空");
        return map;
        }


        //验证
        User u = selectByName(user.getUsername());
        if(u!=null)
        {
            // 可以改为异步验证
            map.put("usernameMsg","该账号已存在");
            return map;
        }

        u = selectByEmail(user.getEmail());
        if(u!=null)
        {
            // 可以改为异步验证
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("https://q4.qlogo.cn/g?b=qq&nk=2353350597&s=140");
        user.setCreateTime(new Date());
        insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url= doMain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();

        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;

    }


    //login check
    public Map<String,Object> login(String username, String password, int expiredSeconds)
    {
        // 先验证属性
        Map<String,Object> map = new HashMap<>();


        if(StringUtils.isBlank(username ))
        {
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        // 从数据库中取出user
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        //验证是否激活

        if(user.getStatus() == 0)
        {
            map.put("usernameMsg","该账号未激活");
            return map;
        }

        //验证密码
        String p = CommunityUtil.md5(password+user.getSalt());
        if(!p.equals(user.getPassword()))
        {
            map.put("passwordMsg","密码错误");
            return map;
        }
        //登陆成功
        //生成凭证

        LoginTicket loginTicket = login_ticketMapper.selectByUserID(user.getId());

        if(loginTicket==null)
        {
            loginTicket = new LoginTicket();
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(CommunityUtil.generateUUID());
            loginTicket.setStatus(0);
            loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds * 1000));
            login_ticketMapper.insertLoginTicket(loginTicket);
        }
         else
        {
            String t = loginTicket.getTicket();
            login_ticketMapper.updateStatus(t,0);
            login_ticketMapper.updateExpired(t,new Date(System.currentTimeMillis()+expiredSeconds * 1000));
        }
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    //activation
    public int activation(int userId,String code)
    {
        User u = userMapper.selectById(userId);
        if(u.getStatus()==1)
        {
            //already active
            return ACTIVATION_REPEAT;
        }else
        if(u.getActivationCode().equals(code))
        {
            updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        return ACTIVATION_FAILURE;
    }

    public void logout(String ticket)
    {
        login_ticketMapper.updateStatus(ticket,1);
    }


    public LoginTicket findLoginTicket(String ticket)
    {
        return login_ticketMapper.selectByName(ticket);

    }
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
    public User selectByName(String username){ return userMapper.selectByName(username);};
    public User selectByEmail(String email){return userMapper.selectByEmail(email);};
    public int insertUser(User user){return userMapper.insertUser(user);};
    public int updateStatus(int id, int status){return userMapper.updateStatus(id,status);};
    public int updateHeader(int id, String headerUrl){return userMapper.updateHeader(id, headerUrl);};
    public int updatePassword(int id, String password){return userMapper.updatePassword(id,password);};


}
