package kunlun.spring.generator.id;

import kunlun.generator.id.support.redis.RedisIncrementalIdConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("kunlun.id.redis-id-generator")
public class RedisIdProperties {
    /**
     * 是否启用基于 redis 的 ID 生成器.
     */
    private Boolean enabled;
    /**
     * ID 生成时的 日期格式（默认 yyyyMMdd）.
     */
    private String  datePattern = "yyyyMMdd";
    /**
     * 生成的 ID 中的自增序列的长度（前置补0，默认 8）.
     */
    private Integer sequenceLength = 8;
    /**
     * 每次自增时的步长（默认 1）.
     */
    private Integer stepLength = 1;
    /**
     * 自增序列的偏移量，即默认加一个数，让其看起来不是从0开始的（默认 0）.
     */
    private Long    offset = 0L;
    /**
     * Redis 的 key 的前缀（默认“_identifier:”）.
     */
    private String redisKeyPrefix;
    /**
     * ID 生成器的配置（key 为生成器名称，value 为配置对象（其他可以走前面配置的，唯有前缀必须走这个））.
     */
    private Map<String, RedisIncrementalIdConfig> configs;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public Integer getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(Integer sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public Integer getStepLength() {
        return stepLength;
    }

    public void setStepLength(Integer stepLength) {
        this.stepLength = stepLength;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
    }

    public Map<String, RedisIncrementalIdConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, RedisIncrementalIdConfig> configs) {
        this.configs = configs;
    }
}
