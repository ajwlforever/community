package com.ajwlforever.community.service;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.dao.Login_ticketMapper;
import com.ajwlforever.community.dao.UserMapper;
import com.ajwlforever.community.entity.LoginTicket;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.ComunityConstant;
import com.ajwlforever.community.util.MailClient;
import com.ajwlforever.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate redisTemplate;

//    @Autowired
//    private Login_ticketMapper login_ticketMapper;

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

//        LoginTicket loginTicket = login_ticketMapper.selectByUserID(user.getId());
//
//        if(loginTicket==null)
//        {
//            loginTicket = new LoginTicket();
//            loginTicket.setUserId(user.getId());
//            loginTicket.setTicket(CommunityUtil.generateUUID());
//            loginTicket.setStatus(0);
//            loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds * 1000));
//            login_ticketMapper.insertLoginTicket(loginTicket);
//        }
//         else
//        {
//            String t = loginTicket.getTicket();
//            login_ticketMapper.updateStatus(t,0);
//            login_ticketMapper.updateExpired(t,new Date(System.currentTimeMillis()+expiredSeconds * 1000));
//        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds * 1000));

        //存放在redis
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

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
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket =(LoginTicket) redisTemplate.opsForValue().get(redisKey);

        loginTicket.setStatus(1); //shixiao
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }


    public LoginTicket findLoginTicket(String ticket)
    {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }


    public User findUserById(int id) {
        User user = getCache(id);
        if(user==null)
        {
            user = initCache(id);
        }
        return user;
    }
    public User selectByName(String username){ return userMapper.selectByName(username);};
    public User selectByEmail(String email){return userMapper.selectByEmail(email);};
    public int insertUser(User user){return userMapper.insertUser(user);};
    public int updateStatus(int id, int status){
        int rows =  userMapper.updateStatus(id,status);
        clearCache(id);
        return rows;
    };
    public int updateHeader(int id, String headerUrl){
        int rows =  userMapper.updateHeader(id, headerUrl);
        clearCache(id);
        return rows;
    };
    public int updatePassword(int id, String password){
        int rows =  userMapper.updatePassword(id,password);
        clearCache(id);
        return rows ;
    };


}
