package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleRedisApplication {

    private static final Logger logger = LoggerFactory.getLogger(ExampleRedisApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ExampleRedisApplication.class, args).close();
    }


    @Bean
    CommandLineRunner init(RedisService redisService){
        return args -> {
            redisService.clearAll();

            User user1 = new User("1", "tom", 14);
            User user2 = new User("2", "jerry", 24);
            User user3 = new User("3", "kitty", 53);
            User user4 = new User("4", "mickey", 21);
            User user5 = new User("5", "donald", 30);

            redisService.hSet(user1.getId(), user1);
            redisService.hSet(user2.getId(), user2);
            redisService.hSet(user3.getId(), user3);
            redisService.hSet(user4.getId(), user4);
            redisService.hSet(user5.getId(), user5);

            logger.info("sort by age limit 3,id list={}",redisService.sort("ids:user","user:id:*->age",0L,3L).toString());
            logger.info("sort by age limit 4,id list={}",redisService.sort("ids:user","user:id:*->age",0L,4L).toString());

            logger.info("sort by name limit 3,id list={}",redisService.sort("ids:user","user:id:*->name",0L,3L).toString());
            logger.info("sort by name limit 3,id list={}",redisService.sort("ids:user","user:id:*->name",0L,4L).toString());
        };
    }
}
