/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support.redis;

import kunlun.common.constant.Nil;
import kunlun.generator.id.support.AbstractIncrementalIdGenerator;
import kunlun.time.DateUtil;
import kunlun.util.Assert;
import kunlun.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.util.Assert.state;

/**
 * 基于 Redis 的字符串 ID 生成器.<br />
 * @see <a href="https://redis.io/commands/incrby">INCRBY key increment</a>
 * @author Zerox
 */
public class RedisIncrementalIdGenerator extends AbstractIncrementalIdGenerator {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisIncrementalIdGenerator(RedisIncrementalIdConfig config
            , StringRedisTemplate stringRedisTemplate) {
        super(config);
        this.stringRedisTemplate = stringRedisTemplate;
        String redisKeyPrefix = config.getRedisKeyPrefix();
        if (StrUtil.isBlank(redisKeyPrefix)) {
            config.setRedisKeyPrefix("_identifier:");
        }
    }

    public RedisIncrementalIdGenerator(RedisIncrementalIdConfig config) {

        this(config, Nil.<StringRedisTemplate>g());
    }

    protected StringRedisTemplate getStringRedisTemplate() {
        state(stringRedisTemplate != null
                , "In \"%s\", Please rewrite the \"getStringRedisTemplate\" method! ", getClass().getName());
        return stringRedisTemplate;
    }

    @Override
    public RedisIncrementalIdConfig getConfig() {

        return (RedisIncrementalIdConfig) super.getConfig();
    }

    @Override
    protected String buildQueryKey(Context context) {
        String redisKeyPrefix = Assert.notBlank(getConfig().getRedisKeyPrefix());
        String redisKey = redisKeyPrefix.endsWith(":") ? redisKeyPrefix : redisKeyPrefix + ":";
        return redisKey + getConfig().getName() + ":" + DateUtil.format("yyyyMMdd");
    }

    @Override
    protected Long onlyGet(Context context) {
        // Build the redis key.
        String redisKey = buildQueryKey(context);
        // Do increment.
        ValueOperations<String, String> opsForValue = getStringRedisTemplate().opsForValue();
        Integer stepLength = getConfig().getStepLength();
        Long increment;
        String valStr = opsForValue.get(redisKey);
        if (StrUtil.isBlank(valStr)) {
            increment = 0L;
        } else {
            increment = Long.parseLong(valStr);
        }
        increment += stepLength;
        // Return result.
        return increment;
    }

    @Override
    protected Long incrementAndGet(Context context) {
        // Build the redis key.
        String redisKey = buildQueryKey(context);
        // Do increment.
        ValueOperations<String, String> opsForValue = getStringRedisTemplate().opsForValue();
        Integer stepLength = getConfig().getStepLength();
        Long increment = opsForValue.increment(redisKey, stepLength);
        if (increment == null) {
            throw new IllegalStateException("An error is likely due to use pipeline / transaction. ");
        }
        // Set expire time.
        if (increment <= stepLength) {
            // In redis key, it has been fixed as one day.
            // So the expiration time only needs to be greater than one day.
            getStringRedisTemplate().expire(redisKey, ONE, TimeUnit.DAYS);
        }
        // Return result.
        return increment;
    }

}
