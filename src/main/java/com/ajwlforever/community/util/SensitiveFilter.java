package com.ajwlforever.community.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    public Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换字符
    private static final String REPLACEMENT = "***";
    //前缀树的节点

    //Root
    private TrieNode root = new TrieNode();

    @PostConstruct
    private  void init()
    {

        try(
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitve_words.txt");
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        )
        {
            String keyword = "";
            while ( (keyword=bf.readLine())!=null)
            {
                addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词资源文件出错"+e.getMessage());

            e.printStackTrace();
        }
    }

    public void addKeyword(String keyword)
    {
        TrieNode temp = root;
        for(int i=0 ;i<keyword.length();i++)
        {
            char c = keyword.charAt(i);
            TrieNode sub = temp.getSubNode(c); //节点找找看这个字符有没有
            if(sub==null)
            {
                sub = new TrieNode();
                temp.addChildren(c,sub);
            }
            temp = sub;

            if(i==keyword.length()-1)
            {
                temp.setKeyWordEnd(true);
            }
        }
    }

    private  String filter(String text)
    {
        StringBuilder after =new StringBuilder("");

        //temp
        TrieNode temp = root;
        int begin = 0,position = 0 ;
        //circle
        while(position < text.length())
        {
           char c = text.charAt(position);
            //如果是符号，跳过
            if(isSymbo(c))
            {
               //如果未开始,追加结果
                if(temp==root)
                {
                    after.append(c);
                    begin++;

                }
                position++; // 如果未开始，这里position==begain，如果开始，则是跳过这个字符position++；
                continue;
            }

            //开始搜索，子节点遍历
            temp = temp.getSubNode(c);

            //子节点没有 , begin处字符不是
            if(temp==null)
            {
                after.append(text.charAt(begin));
                begin++;
                position = begin;
                temp = root; //reset

            }else
                //前缀树到最后了，是铭感词
                if(temp.isKeyWordEnd())
                {
                    after.append(REPLACEMENT);
                    //敏感词忽略
                    position++;
                    begin = position;
                    temp =root;//reset
                }
                else
                {
                    position++; //不为空，继续搜索
                }
        }



        return after.toString();
    }

    private boolean isSymbo(char c)
    {
        // 0X2EE~0X9FFF 之间是东亚文字，不算符号。
        return !CharUtils.isAsciiAlphanumeric(c) &&( c< 0X2EE || c>0X9FFF);
    }
    private class TrieNode{


        private  boolean isKeyWordEnd;

        private Map<Character,TrieNode> children = new HashMap<>();

        public void  addChildren(Character c,TrieNode t)
        {
            //添加子节点
            children.put(c,t);
        }
        //
        public TrieNode getSubNode(Character c)
        {
            return children.get(c);
        }

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
    }
}
