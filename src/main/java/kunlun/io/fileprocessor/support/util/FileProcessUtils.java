package kunlun.io.fileprocessor.support.util;

import kunlun.io.fileprocessor.FileProcessor;
import kunlun.spring.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class FileProcessUtils {
    private static final Logger log = LoggerFactory.getLogger(FileProcessUtils.class);

    public static void processThreadPool(FileProcessor.ProcContext<?, ?> context) {
        if (context.getThreadPool() != null) { return; }
        String beanName = "fileProcessorThreadPool";
        try {
            if (SpringUtil.containsBean(beanName)) {
                context.setThreadPool(SpringUtil.getBean(beanName, ExecutorService.class));
                context.setAsync(true);
            }
        } catch (Exception e) { log.debug("Get thread pool error. ", e); }
    }

}
