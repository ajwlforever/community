package com.ajwlforever.community;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes =  CommunityApplication.class)
public class RedisTest {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    @Test
    public void testStrings()
    {
        String redisKey ="test:count";
        template.opsForValue().set(redisKey,1);

        System.out.println(template.opsForValue().get(redisKey));
        System.out.println(template.opsForValue().increment(redisKey));
        System.out.println(template.opsForValue().decrement(redisKey,2));
    }


    @Test
    public  void testHash()
    {
        String redisKey ="test:user";

        template.opsForHash().put(redisKey,"id",1);
        template.opsForHash().put(redisKey,"username","ajwl");


        System.out.println(template.opsForHash().get(redisKey,"id"));
        System.out.println(template.opsForHash().get(redisKey,"username"));

    }

    @Test
    public void testList()
    {
        String redisKey ="test:ids";
        template.opsForList().leftPush(redisKey,101);
        template.opsForList().leftPush(redisKey,102);
        template.opsForList().leftPush(redisKey,103);
        template.opsForList().leftPush(redisKey,104);


        System.out.println("size:"+template.opsForList().size(redisKey));
        System.out.println("index 1 :"+template.opsForList().index(redisKey,0));
        System.out.println("range 0 2 :"+template.opsForList().range(redisKey,0,2));

        System.out.println(template.opsForList().leftPop(redisKey));
        System.out.println(template.opsForList().leftPop(redisKey));
        System.out.println(template.opsForList().leftPop(redisKey));

    }
    @Test
    public void testSet()
    {
        String redisKey = "test:teachers";
        template.opsForSet().add(redisKey,"aaa","bbb","ccc","ddd");
        System.out.println(template.opsForSet().size(redisKey));

        System.out.println(template.opsForSet().pop(redisKey));
        System.out.println(template.opsForSet().pop(redisKey));

        System.out.println(template.opsForSet().members(redisKey));
    }


    @Test
    public void TestZsets()
    {
        String redisKey = "test:students";
        template.opsForZSet().add(redisKey,"aaa",12 );
        template.opsForZSet().add(redisKey,"bbb",123 );
        template.opsForZSet().add(redisKey,"ccc",1 );
        template.opsForZSet().add(redisKey,"ddd",11 );

        System.out.println(  template.opsForZSet().zCard(redisKey));
        System.out.println(  template.opsForZSet().score(redisKey,"aaa"));
        System.out.println(  template.opsForZSet().rank(redisKey,"aaa"));
        System.out.println(  template.opsForZSet().reverseRank(redisKey,"aaa"));
        System.out.println(  template.opsForZSet().range(redisKey,0,2));  //从小到大
        System.out.println(  template.opsForZSet().reverseRange(redisKey,0,2));


    }

    @Test
    public void testKeys()
    {
        System.out.println(template.hasKey("test:user"));
        template.delete("test:user");
        System.out.println(template.hasKey("test:user"));
        template.expire("test:student",10, TimeUnit.SECONDS);

    }

    //多次访问一个key
    @Test
    public  void testBound()
    {
        String redisKey = "test:count";
        BoundValueOperations operations = template.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTransactional()
    {
        Object obj = template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey ="test:tx";
                redisOperations.multi();  //start transaction
                redisOperations.opsForSet().add(redisKey,"zzz");
                redisOperations.opsForSet().add(redisKey,"sad");
                redisOperations.opsForSet().add(redisKey,"zzfz");
                redisOperations.opsForSet().add(redisKey,"asfdafg");

                System.out.println( redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec(); //commit
            }
        });

        System.out.println(obj);
    }

}
