package kunlun.spring.generator.id;

import kunlun.generator.id.support.RedisIncrementalIdConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("kunlun.id.redis-id-generator")
public class RedisIdProperties {
    private Boolean enabled;
    private String  datePattern;
    private Integer sequenceLength;
    private Integer stepLength;
    private Long    offset;
    private String redisKeyPrefix;
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
