package com.ajwlforever.community.event;

import com.ajwlforever.community.entity.Event;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Author: ajwlforever
 * function: kafka的生产者，发送Event事件
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void fireEvent(Event event)
    {
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
