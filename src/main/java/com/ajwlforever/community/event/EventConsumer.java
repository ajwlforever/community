package com.ajwlforever.community.event;

import com.ajwlforever.community.entity.Event;
import com.ajwlforever.community.entity.Message;
import com.ajwlforever.community.service.MessageService;
import com.ajwlforever.community.util.ComunityConstant;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * author: ajwlforever
 * function: kafka的消费者
 */

@Component
public class EventConsumer implements ComunityConstant {
    private  static  final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_COMMENT})
    public void handleEvent(ConsumerRecord record)
    {
        if(record==null||record.value()==null)
        {
            logger.error("消息的内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null)
        {
            logger.error("消息格式错误");
            return;
        }
        ///通过事件构建消息

        Message message  = new Message();
        message.setFromId(SYSTEM_USERID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        //内容为操作用户的username， 帖子的详情页面
        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if(!event.getData().isEmpty())
        {
            for (Map.Entry<String,Object> entry : event.getData().entrySet())
            {
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);


    }
}
