package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class RedisService {


    private StringRedisTemplate redisTemplate;
    public ValueOperations<String, String> string;
    public HashOperations<String, String, String> hash;
    public ListOperations<String, String> list;
    public SetOperations<String, String> set;
    public ZSetOperations<String, String> zSet;

    @Autowired
    public RedisService(StringRedisTemplate stringRedisTemplate){
        redisTemplate = stringRedisTemplate;
        string = redisTemplate.opsForValue();
        hash = redisTemplate.opsForHash();
        list = redisTemplate.opsForList();
        set = redisTemplate.opsForSet();
        zSet = redisTemplate.opsForZSet();
    }

    public void clearAll(){
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return "clear all";
        });
    }

    /**
     * 按Hash添加实体
     */
    public <T> void hSet(String id, T entity) {
        Class<?> tClass = entity.getClass();
        Field[] fields = tClass.getDeclaredFields();
        String classLowerName = tClass.getSimpleName().toLowerCase();
        for (Field f : fields) {
            String name = f.getName();
            String getter = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            Object returnType = null;
            try {
                returnType = tClass.getMethod(getter).invoke(entity);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (returnType != null) {
                hash.put( classLowerName + ":id:" + id, name, returnType.toString());
            }
        }
        list.leftPush("ids:"+classLowerName,id);

    }

    /**
     * 分页查询,降序
     * @param key   一般是id列表
     * @param by_pattern 根据此pattern的value排序，除了常规的pattern，也可以接收hash的pattern
     * @param offset  偏移量
     * @param count  每次查询的条数
     * @return 返回分页后的id列表
     */
    public List<String> sort(String key, String by_pattern, Long offset, Long count){
        return redisTemplate.sort(
                SortQueryBuilder
                        .sort(key)
                        .by(by_pattern)
                        .alphabetical(true)
                        .order(SortParameters.Order.DESC)
                        .limit(offset,count)
                        .build()
        );
    }
}