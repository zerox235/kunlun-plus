/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support.redis;

import kunlun.generator.id.support.IncrementalIdConfig;

/**
 * 基于 Redis 的 ID 生成器的配置类.<br />
 * @author Zerox
 */
public class RedisIncrementalIdConfig extends IncrementalIdConfig {
    private String redisKeyPrefix;

    public String getRedisKeyPrefix() {

        return redisKeyPrefix;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {

        this.redisKeyPrefix = redisKeyPrefix;
    }

}
