package kunlun.spring.io.fileprocessor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties("kunlun.file-processor.thread-pool")
public class FileProcessorThreadPoolProperties {
    /**
     * 是否开启文件处理线程池.
     */
    private Boolean enabled;
    /**
     * 核心线程池大小.
     */
    private Integer corePoolSize;
    /**
     * 最大线程池大小.
     */
    private Integer maxPoolSize;
    /**
     * 线程存活时间.
     */
    private Integer keepAliveTime;
    /**
     * 线程存活时间的单位.
     */
    private TimeUnit timeUnit;
    /**
     * 线程池队列大小.
     */
    private Integer queueCapacity;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Integer keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
