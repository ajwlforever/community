package com.ajwlforever.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID()
    {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //Md5加密
    public static String md5(String key)
    {
        if(StringUtils.isBlank(key))
        {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());

    }

    //随机头像Url
    public String headUrl()
    {
        while(true)
        {

            int qq = 312;
         String result = "";
        String charset = "UTF-8";
        String callurl = "http://www.webxml.com.cn/webservices/qqOnlineWebService.asmx/qqCheckOnline?qqCode=";

        try {
            /*
             * 通过输入QQ号码（String）检测QQ在线状态。 返回数据（String）Y = 在线；N = 离线 ；E =
             * QQ号码错误
             */
            java.net.URL url = new java.net.URL(callurl + qq);
            java.net.URLConnection connection = url.openConnection();
            connection.connect();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection
                            .getInputStream(), charset));
            String line;
            /*
             * 返回的格式 <?xml version="1.0" encoding="utf-8"?> <string
             * xmlns="http://WebXml.com.cn/">E</string>
             */
            while ((line = reader.readLine()) != null) {
                result += line;
                result += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int len = result.indexOf("\">");
        if (len != -1) {
            String qStruts = result.substring(len + 2, len + 3);
            if (qStruts.equals("E")) {
                System.out.println("QQ号码错误");
            } else if (qStruts.equals("Y")) {
                System.out.println("在线");
            } else if (qStruts.equals("N")) {
                System.out.println("离线");
            }
        } else {
            System.out.println("服务器繁忙、请从试!");
        }

    }

    }

    public static String toJsonString(int code, String msg, Map<String,Object> map)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);

        if(map!=null)
        {
            for(String key:map.keySet())
            {
                jsonObject.put(key,map.get(key));
            }
        }

        return jsonObject.toJSONString();
    }

    public static String toJsonString(int code ,String msg)
    {
        return  toJsonString(code,msg,null);
    }
    public static String toJsonString(int code  )
    {
        return  toJsonString(code,null,null);
    }


    public static void main(String[] args) {
        int code = 0;
        String msg = "41564654";
        System.out.println( CommunityUtil.toJsonString(code,msg) );

    }
}
