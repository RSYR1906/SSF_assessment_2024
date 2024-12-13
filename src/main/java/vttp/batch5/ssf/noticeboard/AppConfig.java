package vttp.batch5.ssf.noticeboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

/*
 * IMPORTANT *** DO NOT MODIFY THIS CLASS ***
 */
@Configuration
public class AppConfig {

    @Value("${noticeboard.db.host}")
    private String dbHost;

    @Value("${noticeboard.db.port}")
    private Integer dbPort;

    @Value("${noticeboard.db.database}")
    private Integer dbNumber;

    @Value("${noticeboard.db.username}")
    private String dbUsername;

    @Value("${noticeboard.db.password}")
    private String dbPassword;

    @Bean("notice")
    public RedisTemplate<String, Object> createRedisTemplate() {
        /*
         * IMPORTANT *** DO NOT MODIFY THIS METHOD ***
         */

        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(dbHost, dbPort);
        config.setDatabase(dbNumber);
        if (!dbUsername.trim().equals("")) {
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
        }
        final JedisClientConfiguration jedisClient = JedisClientConfiguration
                .builder().build();

        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config,
                jedisClient);
        jedisFac.afterPropertiesSet();

        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisFac);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        return template;
    }
}
