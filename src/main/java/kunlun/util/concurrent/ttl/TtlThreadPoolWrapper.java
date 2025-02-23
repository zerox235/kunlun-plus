/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util.concurrent.ttl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import kunlun.util.ClassUtil;
import kunlun.util.concurrent.ThreadPoolWrapper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The thread pool wrapper based on Alibaba TTL.
 * @author Kahle
 */
public class TtlThreadPoolWrapper implements ThreadPoolWrapper {
    private static final boolean HAVE_TTL;

    static {

        HAVE_TTL = ClassUtil.isPresent("com.alibaba.ttl.threadpool.TtlExecutors");
    }

    @Override
    public Executor wrap(Executor executor) {
        if (executor == null) { return null; }
        if (!HAVE_TTL) { return executor; }
        if (executor instanceof ScheduledExecutorService) {
            return TtlExecutors.getTtlScheduledExecutorService((ScheduledExecutorService) executor);
        }
        if (executor instanceof ExecutorService) {
            return TtlExecutors.getTtlExecutorService((ExecutorService) executor);
        }
        return TtlExecutors.getTtlExecutor(executor);
    }

}
