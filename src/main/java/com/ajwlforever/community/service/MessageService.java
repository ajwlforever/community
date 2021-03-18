package com.ajwlforever.community.service;


import com.ajwlforever.community.dao.MessageMapper;
import com.ajwlforever.community.entity.Message;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations (int userId,int offset,int limit) {
        return  messageMapper.selectConversations(userId,offset,limit);
    }

    public  int findConversationsCount(int userId) {
        return messageMapper.selectConversationCount(userId);

    }

    public List<Message> findLetters(String coversationId,int offset,int limit) {
        return  messageMapper.selectLetters(coversationId,offset,limit);

    }
    public int findLetterCount(String conversationId)
    {
        return  messageMapper.selectLetterCount(conversationId);
    }
    public  int findLetterUnreadCount(int userId,String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
      return    messageMapper.insertMessage(message);
    }
    public int updateStatus(List<Integer>ids , int status){
        return  messageMapper.updateStatus(ids,status);
    }

    public Message findLatestNotice(int userId, String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId,topic);

    }
    //如果没有topic，就是查询全部主题的未读消息数量
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.sekectNoticeUnreadCount(userId,topic);
    }

    public List<Message> selectNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

}
