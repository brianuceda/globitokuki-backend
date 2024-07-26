package site.globitokuki.globitokuki_backend.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableCaching
public class CacheConfig {
  @Value("${REDIS_HOST}")
  private String redisHost;
  @Value("${REDIS_PORT}")
  private String redisPort;
  @Value("${REDIS_PASSWORD}")
  private String redisPassword;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
    redisConfig.setHostName(String.valueOf(redisHost));
    redisConfig.setPort(Integer.parseInt(redisPort));
    redisConfig.setPassword(RedisPassword.of(this.redisPassword));
    return new LettuceConnectionFactory(redisConfig);
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
    return RedisCacheManager.builder(redisConnectionFactory)
      .cacheDefaults(defaultCacheConfig)
      .build();
  }
}
