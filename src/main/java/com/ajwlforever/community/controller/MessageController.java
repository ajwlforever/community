package com.ajwlforever.community.controller;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.entity.Message;
import com.ajwlforever.community.entity.Page;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.MessageService;
import com.ajwlforever.community.service.UserService;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.ComunityConstant;
import com.ajwlforever.community.util.HostHolder;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller

public class MessageController implements ComunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;



    //私信列表
    @LoginRequired
    @RequestMapping(path = "/letter/list" , method = RequestMethod.GET)
    public String getLetterList(Model model, Page page)
    {
        User user = hostHolder.getUser();

        page.setPath("/letter/list");
        page.setLimit(10);
        page.setRows(messageService.findConversationsCount(user.getId()));
        //get conversationLisy;
        List<Message> conversationList = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();

        if (conversationList!=null)
        {
            for(Message conversation : conversationList)
            {
                Map<String,Object> map  = new HashMap<>();
                map.put("conversation",conversation);
                map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                int unreadCount = messageService.findLetterUnreadCount(user.getId(),conversation.getConversationId());
                map.put("unreadCount",unreadCount);
                int target  = user.getId() ==  conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                map.put("target",userService.findUserById(target));
                conversations.add(map);
            }

        }
        model.addAttribute("conversations",conversations);
        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);


        return "/site/letter" ;
    }

    @LoginRequired
    @RequestMapping(path = "/letter/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(Model model, @PathVariable("conversationId") String conversationId,Page page)
    {
        User user = hostHolder.getUser();
        page.setPath("/letter/"+conversationId);
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));


        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null)
        {
            for(Message letter : letterList)
            {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);
        model.addAttribute("target",getTarget(conversationId));


        return "/site/letter-detail";
    }

    public User getTarget(String conversationId)
    {
        String[] ids = conversationId.split("_");
        int id1 = Integer.parseInt(ids[0]);
        int id2 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==id1)
        {
            return userService.findUserById(id2);
        }
        return userService.findUserById(id1);
    }

    //发送私信
    @LoginRequired
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        if(StringUtils.isBlank(content)||StringUtils.isBlank(toName))
        {
            return CommunityUtil.toJsonString(500,"用户名或私信内容不得为空");
        }
        User toUser = userService.selectByName(toName);
        if(toUser==null) {
            return CommunityUtil.toJsonString(403,"没有这个用户");
        }
        User user = hostHolder.getUser();
        //send
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(toUser.getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        String conversationId = message.getFromId()>message.getToId()? message.getToId()+"_"+message.getFromId():
                message.getFromId()+"_"+message.getToId();

        message.setConversationId(conversationId);
        messageService.addMessage(message);
        return CommunityUtil.toJsonString(0,"发送成功!");

    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model)
    {
        User user = hostHolder.getUser();
        //查询评论类的通知
        Message  conmentMessage = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String ,Object> messageVO = new HashMap<>();
        if(conmentMessage!=null) {
            //要放入视图中的数据
            messageVO.put("message",conmentMessage);
            String content = HtmlUtils.htmlUnescape(conmentMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            //查询此用户所有评论通知的数量
            int commentNoticeCount = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            //因为要复用，key去掉comment前缀
            messageVO.put("noticeCount",commentNoticeCount);
            int commentNoticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVO.put("noticeUnreadCount",commentNoticeUnreadCount);

        }else
            messageVO.put("message",null);
        model.addAttribute("commentNotice",messageVO);

        //查询点赞类的通知  复用
        Message  likeMessage = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        messageVO = new HashMap<>();
        if(likeMessage!=null) {
            //要放入视图中的数据
            messageVO.put("message",likeMessage);
            String content = HtmlUtils.htmlUnescape(likeMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);
            // user 对一个 entity 做出的动作
            messageVO.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            //messageVO.put("postId", data.get("postId"));
            //查询此用户所有点赞通知的数量
            int liketNoticeCount = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            //因为要复用，key去掉comment前缀
            messageVO.put("noticeCount",liketNoticeCount);
            int likeNoticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVO.put("noticeUnreadCount",likeNoticeUnreadCount);

        }else
            messageVO.put("message",null);
        model.addAttribute("likeNotice",messageVO);


        //查询关注类的通知
        Message followMessage = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if(followMessage!=null) {
            //要放入视图中的数据
            messageVO.put("message",followMessage);
            String content = HtmlUtils.htmlUnescape(followMessage.getContent());
            Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);
            // user 对一个 entity 做出的动作
            messageVO.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            //messageVO.put("postId", data.get("postId"));
            //查询此用户所有点赞通知的数量
            int followNoticeCount = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            //因为要复用，key去掉comment前缀
            messageVO.put("noticeCount",followNoticeCount);
            int followNoticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVO.put("noticeUnreadCount",followNoticeUnreadCount);

        }else
            messageVO.put("message",null);

        model.addAttribute("followNotice",messageVO);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";

    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model)
    {
        if(topic.equals(TOPIC_FOLLOW)||topic.equals(TOPIC_LIKE)||topic.equals(TOPIC_COMMENT)) {

        User hostUser = hostHolder.getUser();

        page.setRows(messageService.findNoticeCount(hostUser.getId(),topic));
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);

        List<Message> notices = messageService.selectNotices(hostUser.getId(),topic,page.getOffset(),page.getLimit());

        List<Map<String, Object>> noticeVOList = new ArrayList<>();

        if(notices != null)
        {
            //聚合notice
            for(Message notice : notices)
            {
                Map<String, Object> noticeVO = new HashMap<>();
                //通知
                noticeVO.put("notice",notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content);
                noticeVO.put("user", userService.findUserById((Integer)data.get("userId")));
                noticeVO.put("entityType", data.get("entityType"));
                noticeVO.put("entityId", data.get("entityId"));
                noticeVO.put("postId", data.get("postId"));

                //通知的作者 系统用户
                noticeVO.put("fromUser",userService.findUserById(1));
                noticeVOList.add(noticeVO);
            }
        }

        model.addAttribute("notices",noticeVOList);
        //设置已经读

        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty())
        {
            messageService.updateStatus(ids,1);
        }
        }
        else
            throw  new RuntimeException("系统通知主题错误");
        return "/site/notice-detail";
    }
    public List<Integer> getLetterIds( List<Message> notices)
    {
        List<Integer> res = new ArrayList<>();
        for(Message m : notices)
        {
            res.add(m.getId());
        }
        return res;
    }
}
