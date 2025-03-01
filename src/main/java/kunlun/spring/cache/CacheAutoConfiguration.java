package kunlun.spring.cache;

import cn.hutool.core.collection.CollUtil;
import kunlun.cache.CacheUtil;
import kunlun.cache.support.*;
import kunlun.data.bean.BeanUtil;
import kunlun.spring.cache.CacheProperties.JdbcConfig;
import kunlun.spring.cache.CacheProperties.RedisConfig;
import kunlun.spring.cache.CacheProperties.SimpleConfig;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

import static kunlun.util.ObjUtil.cast;

/**
 * The cache tools autoconfiguration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({CacheProperties.class})
@ConditionalOnProperty(name = "kunlun.cache.enabled", havingValue = "true")
public class CacheAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);
    private final CacheProperties cacheProperties;

    @Resource
    private ApplicationContext applicationContext;

    @Autowired
    public CacheAutoConfiguration(CacheProperties cacheProperties) {
        Assert.notNull(cacheProperties, "Parameter \"cacheProperties\" must not null. ");
        this.cacheProperties = cacheProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // simple
        List<SimpleConfig> simple = cacheProperties.getSimple();
        if (CollUtil.isNotEmpty(simple)) {
            for (SimpleConfig config : simple) {
                String cacheName = config.getName();
                CacheUtil.registerCache(cacheName, new SpringSimpleCache(config));
            }
        }
        // redis
        List<RedisConfig> redis = cacheProperties.getRedis();
        if (CollUtil.isNotEmpty(redis)) {
            RedisTemplate<String, Object> redisTemplate =
                    cast(applicationContext.getBean("redisTemplate", RedisTemplate.class));
            for (RedisConfig config : redis) {
                RedisCacheConfig cacheConfig = BeanUtil.beanToBean(config, RedisCacheConfig.class);
                RedisTemplateCache redisCache = new RedisTemplateCache(cacheConfig, redisTemplate);
                CacheUtil.registerCache(config.getName(), redisCache);
            }
        }
        // jdbc
        List<JdbcConfig> jdbc = cacheProperties.getJdbc();
        if (CollUtil.isNotEmpty(jdbc)) {
            JdbcTemplate jdbcTemplate = applicationContext.getBean("jdbcTemplate", JdbcTemplate.class);
            for (JdbcConfig config : jdbc) {
                JdbcCacheConfig cacheConfig = BeanUtil.beanToBean(config, JdbcCacheConfig.class);
                JdbcTemplateCache jdbcCache = new JdbcTemplateCache(cacheConfig, jdbcTemplate);
                CacheUtil.registerCache(config.getName(), jdbcCache);
            }
        }
    }

}
