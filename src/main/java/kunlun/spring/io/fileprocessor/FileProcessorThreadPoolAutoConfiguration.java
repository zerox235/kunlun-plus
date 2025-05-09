package kunlun.spring.io.fileprocessor;

import kunlun.util.concurrent.SimpleThreadFactory;
import kunlun.util.concurrent.ThreadPoolUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties({FileProcessorThreadPoolProperties.class})
@ConditionalOnProperty(name = "kunlun.file-processor.thread-pool.enabled", havingValue = "true")
public class FileProcessorThreadPoolAutoConfiguration {
    private static final Integer DEF_QUEUE_CAPACITY = 20;

    @Resource
    private FileProcessorThreadPoolProperties threadPoolProperties;

    @Bean
    @ConditionalOnMissingBean(name = "fileProcessorThreadPool")
    public ExecutorService fileProcessorThreadPool() {
        String threadNamePrefix = "file-processor-executor";
        ThreadFactory threadFactory = new SimpleThreadFactory(threadNamePrefix, Boolean.FALSE);
        Integer corePoolSize = threadPoolProperties.getCorePoolSize();
        Integer maxPoolSize = threadPoolProperties.getMaxPoolSize();
        Integer keepAliveTime = threadPoolProperties.getKeepAliveTime();
        TimeUnit timeUnit = threadPoolProperties.getTimeUnit();
        if (keepAliveTime == null) {
            keepAliveTime = 10;
            timeUnit = TimeUnit.MINUTES;
        }
        Integer queueCapacity = threadPoolProperties.getQueueCapacity();
        if (queueCapacity == null) { queueCapacity = DEF_QUEUE_CAPACITY; }
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                new ArrayBlockingQueue<Runnable>(queueCapacity), threadFactory, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                throw new IllegalStateException("Too many pending tasks, please try again later! ");
            }
        });
        return ThreadPoolUtil.wrap(threadPool);
    }

}
