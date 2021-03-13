package com.ajwlforever.community.controller;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.entity.Message;
import com.ajwlforever.community.entity.Page;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.MessageService;
import com.ajwlforever.community.service.UserService;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/letter")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @LoginRequired
    @RequestMapping(path = "/list" , method = RequestMethod.GET)
    public String getLetterList(Model model, Page page)
    {
        User user = hostHolder.getUser();

        page.setPath("/letter/list");
        page.setLimit(10);
        page.setRows(messageService.findConversationsCount(user.getId()));
        //get conversationLisy;
        List<Message> conversationList = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        int allLetterUnreadCount = 0;
        if (conversationList!=null)
        {
            for(Message conversation : conversationList)
            {
                Map<String,Object> map  = new HashMap<>();
                map.put("conversation",conversation);
                map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                int unreadCount = messageService.findLetterUnreadCount(user.getId(),conversation.getConversationId());
                map.put("unreadCount",unreadCount);
                allLetterUnreadCount += unreadCount;
                int target  = user.getId() ==  conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                map.put("target",userService.findUserById(target));
                conversations.add(map);
            }

        }
        model.addAttribute("conversations",conversations);
        model.addAttribute("allUnreadCount",allLetterUnreadCount);

        return "/site/letter" ;
    }

    @LoginRequired
    @RequestMapping(path = "/{conversationId}",method = RequestMethod.GET)
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
    @LoginRequired
    @RequestMapping(path = "/send",method = RequestMethod.POST)
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
}
