package kunlun.spring.generator.id;

import cn.hutool.core.util.StrUtil;
import kunlun.generator.id.IdUtil;
import kunlun.generator.id.support.RedisIncrementalIdConfig;
import kunlun.generator.id.support.RedisIncrementalIdGenerator;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({RedisIdProperties.class})
@ConditionalOnProperty(name = "kunlun.id.redis-id-generator.enabled", havingValue = "true")
public class RedisIdAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(RedisIdAutoConfiguration.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisIdProperties redisIdProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, RedisIncrementalIdConfig> configs = redisIdProperties.getConfigs();
        for (Map.Entry<String, RedisIncrementalIdConfig> entry : configs.entrySet()) {
            RedisIncrementalIdConfig idConfig = entry.getValue();
            String name = entry.getKey();
            handle(idConfig, Assert.notBlank(name));
            RedisIncrementalIdGenerator idGenerator =
                    new RedisIncrementalIdGenerator(idConfig, stringRedisTemplate);
            IdUtil.registerGenerator(name, idGenerator);
        }
    }

    private void handle(RedisIncrementalIdConfig idConfig, String name) {
        if (StrUtil.isBlank(idConfig.getName())) {
            idConfig.setName(name);
        }
        if (StrUtil.isBlank(idConfig.getDatePattern())) {
            idConfig.setDatePattern(redisIdProperties.getDatePattern());
        }
        if (idConfig.getSequenceLength() == null) {
            idConfig.setSequenceLength(redisIdProperties.getSequenceLength());
        }
        if (idConfig.getStepLength() == null) {
            idConfig.setStepLength(redisIdProperties.getStepLength());
        }
        if (idConfig.getOffset() == null) {
            idConfig.setOffset(redisIdProperties.getOffset());
        }
        if (StrUtil.isBlank(idConfig.getRedisKeyPrefix())) {
            idConfig.setRedisKeyPrefix(redisIdProperties.getRedisKeyPrefix());
        }
    }

}
