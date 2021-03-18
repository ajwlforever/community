package com.ajwlforever.community;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class KafkaTests {

    @Autowired
    private  KafkaProducer kafkaProducer;

    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMesage("test","hello");
        kafkaProducer.sendMesage("test","are you ok?");

        Thread.sleep(100*100);
    }
}
@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMesage(String topic,String content)
    {
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics={"test"})
    public void handleMessage(ConsumerRecord consumerRecord)
    {
        System.out.println(consumerRecord.value());
    }
}