package com.ajwlforever.community.controller;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.entity.Comment;
import com.ajwlforever.community.entity.DiscussPost;
import com.ajwlforever.community.entity.Event;
import com.ajwlforever.community.event.EventProducer;
import com.ajwlforever.community.service.CommentService;
import com.ajwlforever.community.service.DiscussPostService;
import com.ajwlforever.community.util.ComunityConstant;
import com.ajwlforever.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements ComunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;


    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment  ) {

        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        // 发送系统通知

        Event event = new Event();
        event.setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_POST)
        {
            DiscussPost post = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT)
        {
            Comment t = commentService.selectByCommentId(comment.getEntityId());
            event.setEntityUserId(t.getUserId());
         }
        eventProducer.fireEvent(event);

        return "redirect:/post/" + discussPostId;
    }

}
