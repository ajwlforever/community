## 牛客网论坛实战
### Redis 实现 点赞的功能
点赞包括 1.主页显示赞 2.post页面显示赞，点赞，取消赞
3.个人主页显示点赞。
#### 1 redis在ssm中的使用

redis:https://redis.io/documentation

​		https://www.runoob.com/redis/redis-java.html

spring-redis官方文档：https://docs.spring.io/spring-data/data-redis/docs/2.5.x/reference/html/#preface

spring-redis官方api:https://docs.spring.io/spring-data/data-redis/docs/2.5.x/reference/html/#preface

spring-redis实例：[spring-data-keyvalue-examples](https://github.com/spring-projects/spring-data-keyvalue-examples)

spring-redis实例文档:https://docs.spring.io/spring-data/data-keyvalue/examples/retwisj/current/

##### 1.依赖注入

```
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-redis -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

##### 2.属性设置

```
# Redis数据库索引（默认为0）
spring.redis.database=0  
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379  
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=8  
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1  
# 连接池中的最大空闲连接
spring.redis.pool.max-idle=8  
# 连接池中的最小空闲连接
spring.redis.pool.min-idle=0  
# 连接超时时间（毫秒）
spring.redis.timeout=0  
```
2.redisTemplate 的配置

```
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 选择redis作为默认缓存工具
     * @param redisTemplate
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        return rcm;
    }

    /**
     * retemplate相关配置
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 对hash类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
```
##### 4.本网站的config
```
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory)
    {
        RedisTemplate<String,Object> template   = new RedisTemplate<>();
        template.setConnectionFactory(factory);


        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();

        return template;
    }
}
```


#### 2.实现点赞功能

##### 1.redis中的数据格式

| key(like:entity:entityType:entityId)           | type   | value（....用户集合） |
| :--------------------------------------------- | ------ | --------------------- |
| like:entity:1:124    (对postid为124的帖子点赞) | string | {123,545,55,1,6}      |
| like:entity:2:124  (commentid为124的评论点赞)  | string | {123,545,55,1,6}      |
|                                                |        |                       |

##### 2.对数据进行操作

redisTemplate有详细说明，本网站用opsForSet 返回 SetOperations<K,V> 进行操作

官方api：https://docs.spring.io/spring-data/redis/docs/2.2.4.RELEASE/api/

#### 3.重构点赞功能，主页显示赞
- 重构为用用户为key，记录点赞数量

  新增数据格式

  | key(like:user:userId)             | type | count(收到赞的数量) |
  | --------------------------------- | ---- | ------------------- |
  | like:user:111                     | int  | 12                  |
  | like:user:112 (userId为112的用户) | int  | 12                  |
  |                                   |      |                     |

  LikeService 服务中对redis数据操作多次，要开启事务

  ```
    redisTemplate.execute(new SessionCallback() {
              @Override
              public Object execute(RedisOperations operations) throws DataAccessException {
                  String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                  String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                  //查询是否已经赞过
                  boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                  operations.multi();
                  if(isMember)
                  {
                  	//取消赞，和减少赞的数量
                      operations.opsForSet().remove(entityLikeKey,userId);
                      operations.opsForValue().decrement(userLikeKey);
                  }else
                  {
                      operations.opsForSet().add(entityLikeKey,userId);
                      operations.opsForValue().increment(userLikeKey);
                  }
                  return operations.exec();
              }
          });
  ```
  
#### 实现点赞中遇到的困难

 #####	 1. thymleaf th:href  的使用问题：

     ```
     现在在 community/alpha/test_a这个页面上
     <a th:href="user"></a>  // http://localhost:8080/community/alpha/user
     <a th:href="|user|"></a>  // http://localhost:8080/community/alpha/user
     <a th:href="|user/${user.id}|"></a> //http://localhost:8080/community/alpha/user/1
     <a th:href="|/user/${user.id}|"></a> //http://localhost:8080/user/1
     <a th:href="@{user}"></a>  //http://localhost:8080/community/alpha/user
     <a th:href="@{'user'+${user.id}}"></a> //http://localhost:8080/community/alpha/user1
     <a th:href="@{'/user'+${user.id}}"></a>  //http://localhost:8080/community/user1
     ```
      
     @{}加上了ContextPath 
      
     |   | 不做处理
      
     / 是根地址
##### 2.redis 事务

查询放在事务前，事务开启 operations.multi(),事务执行operations.exec();

