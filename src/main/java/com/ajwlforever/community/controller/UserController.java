package com.ajwlforever.community.controller;

import com.ajwlforever.community.annotation.LoginRequired;
import com.ajwlforever.community.entity.User;
import com.ajwlforever.community.service.LikeService;
import com.ajwlforever.community.service.UserService;
import com.ajwlforever.community.util.CommunityUtil;
import com.ajwlforever.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String context_path;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    @LoginRequired
    public String setting() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload" ,method = RequestMethod.POST)
    public String uploadHead(MultipartFile  headerImage, Model model)
    {
        if(headerImage==null)
        {
            model.addAttribute("error","您还没选择文件");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(suffix.equals(".png")||suffix.equals(".jpg")||suffix.equals(".jpeg")||suffix.equals(".bmp")||
                suffix.equals(".gif"))
        { //文件类型是图片
          }
        else
        {
           //不是图片返回错误
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        if(StringUtils.isBlank(suffix))
        {
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        // confirm Filename
        fileName = CommunityUtil.generateUUID()+suffix;
        File dest = new File(uploadPath+"/"+fileName);
        try {
            //文件转移
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw  new RuntimeException("上传文件失败，服务器发生异常",e);

        }
        //update Url
        User user = hostHolder.getUser();
        String url = domain+context_path+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),url);


        return "/site/setting";
    }



    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHead(@PathVariable("fileName")String fileName, HttpServletResponse response)
    {
        fileName = uploadPath+"/"+fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        suffix = suffix.substring(1);
        response.setContentType("image/"+suffix);
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int b=0;
            while(  (b=fileInputStream.read(buffer))!= -1)
            {
                os.write(buffer,0,b);
            }

        } catch (FileNotFoundException e) {
            logger.error("读取图片失败");

        } catch (IOException e) {
            logger.error("读取图片失败");

        }


    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,  Model model)
    {
        User user = userService.findUserById(userId);
        if(user==null)
            throw new RuntimeException("该用户不存在");
        //用户
        model.addAttribute("user",user);
        int likeCount = likeService.findUserLikeCount(userId);

        model.addAttribute("likeCount",likeCount);
        return "/site/profile";

    }

}
