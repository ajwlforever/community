package com.ajwlforever.community.controller;

import com.ajwlforever.community.entity.Comment;
import com.ajwlforever.community.entity.DiscussPost;
import com.ajwlforever.community.entity.Page;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.CommentService;
import com.ajwlforever.community.service.DiscussPostService;
import com.ajwlforever.community.service.LikeService;
import com.ajwlforever.community.service.UserService;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.ComunityConstant;
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
@RequestMapping("/post")
public class DiscussPostController {


    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;


    @RequestMapping(path = "/add" , method = RequestMethod.POST)
    @ResponseBody
    public  String addPost(String title,String content)
    {
        if(StringUtils.isBlank(title)||StringUtils.isBlank(content))
        {
            return CommunityUtil.toJsonString(500,"主题或内容不得为空！");
        }
        User user = hostHolder.getUser();
        if(user == null)
        {
            return CommunityUtil.toJsonString(403,"您尚未登录");

        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return CommunityUtil.toJsonString(0,"发布成功!");
    }


    @RequestMapping(path = "/{postId}",method = RequestMethod.GET)
    public String getpost(@PathVariable("postId") int postId, Model model, Page page)
    {
        //帖子
        DiscussPost post = discussPostService.selectDiscussPostById(postId);
        if(post ==null) return "/site/error/404.html";
        model.addAttribute("post",post);
        //用户

       User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //点赞
        long likeCount  = likeService.findEntityLikeCount(1,postId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),1,postId);

        model.addAttribute("likeCount",likeCount);
        model.addAttribute("likeStatus",likeStatus);

        //评论的处理
        //每页五条
        page.setLimit(5);
        page.setPath("/post/"+postId);
        page.setRows(post.getCommentCount());

        List<Comment> comments =  commentService.findCommentsByEntity(ComunityConstant.ENTITY_TYPE_POST,postId,page.getOffset(),page.getLimit());

        // 所有的 评论和谁的评论， 各种回复的封装
        List<Map<String,Object>>  commentViews = new ArrayList<>();
        if(comments!=null)
        {
            for(Comment comment : comments)
            {
                Map<String ,Object> commentMap = new HashMap<>();
                commentMap.put("comment",comment);
                commentMap.put("user",userService.findUserById(comment.getUserId()));
                // 评论的点赞 与 用户是否点赞
                likeCount = likeService.findEntityLikeCount(2,comment.getId());
                likeStatus = likeService.findEntityLikeStatus(user.getId(),2,comment.getId());
                commentMap.put("likeCount",likeCount);
                commentMap.put("likeStatus",likeStatus);

                List<Comment> replyComments =  commentService.findCommentsByEntity(ComunityConstant.ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);

                List<Map<String,Object>> replycommentViews = new ArrayList<>();
                for(Comment replyComent : replyComments)
                {
                    Map<String ,Object> replycommentMap = new HashMap<>();
                    replycommentMap.put("comment",replyComent);
                    replycommentMap.put("user",userService.findUserById(replyComent.getUserId()));
                    //评论中的 是否为评论中的评论
                    User target =  replyComent.getTargetId() ==0? null:userService.findUserById(replyComent.getTargetId());
                    replycommentMap.put("target",target);

                    //评论de 回复
                    likeCount = likeService.findEntityLikeCount(2,replyComent.getId());
                    likeStatus = likeService.findEntityLikeStatus(user.getId(),2,replyComent.getId());
                    replycommentMap.put("likeCount",likeCount);
                    replycommentMap.put("likeStatus",likeStatus);

                    replycommentViews.add(replycommentMap);

                }
                commentMap.put("replys",replycommentViews);


                //回复的数量
                int replyCount = commentService.findCommentCount(ComunityConstant.ENTITY_TYPE_COMMENT,comment.getId());
                commentMap.put("replyCount",replyCount);
                commentViews.add(commentMap);
            }
            model.addAttribute("comments",commentViews);
        }
        return "/site/discuss-detail.html";
    }

}
