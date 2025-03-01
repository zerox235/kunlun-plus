/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.lock.support;

import kunlun.lock.LockUtil;
import kunlun.util.ThreadUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static kunlun.common.constant.Numbers.ZERO;

@Ignore
public class RedissonLockUtilTest {
    private static final Logger log = LoggerFactory.getLogger(RedissonLockUtilTest.class);
    private volatile Integer num = 100;
    private final Integer threadNum = 50;
    private ExecutorService threadPool;
    private String managerName;

    @Before
    public void init() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(threadNum, threadNum, ZERO, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        managerName = "redis";
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectTimeout(10000)
                .setDatabase(10)
                .setPassword(null);
        RedissonClient redissonClient = Redisson.create(config);
        LockUtil.registerManager(managerName, new RedissonLockManager(redissonClient));
    }

    @After
    public void destroy() {

        threadPool.shutdown();
    }

    private void bizCalc() {
        num = num + 20;
        ThreadUtil.sleepQuietly(50);
        num = num - 40;
        ThreadUtil.sleepQuietly(100);
        num = num + 20;
        num = num + 10;
        ThreadUtil.sleepQuietly(150);
        num = num - 20;
        num = num + 10;
        num = num - 1;
    }

    @Test
    public void testLock() {
        final String lockName = "testLock";
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                long millis = System.currentTimeMillis();
                for (int j = 0; j < 1000000; j++) {
                    if (num <= 0) { continue; }
                    LockUtil.lock(managerName, lockName);
                    try {
                        log.info(">> {} lock", threadName);
                        if (num > 0) {
                            bizCalc();
                            log.info("|| {} | {}", threadName, num);
                        }
                    }
                    finally {
                        log.info("<< {} unlock\n", threadName);
                        LockUtil.unlock(managerName, lockName);
                        ThreadUtil.sleepQuietly(100);
                    }
                }
                log.info("{}: {}ms", threadName, System.currentTimeMillis() - millis);
            }
        };
        for (int i = 0; i < threadNum; i++) {
            threadPool.submit(runnable);
        }
        ThreadUtil.sleepQuietly(100000);
    }

    @Test
    public void testTryLock() {
        final String lockName = "testTryLock";
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                long millis = System.currentTimeMillis();
                for (int j = 0; j < 1000000; j++) {
                    if (num <= 0) { continue; }
                    boolean tryLock = LockUtil.tryLock(managerName, lockName, 500, TimeUnit.MILLISECONDS);
                    if (!tryLock) { continue; }
                    try {
                        log.info(">> {} tryLock {}", threadName, tryLock);
                        if (num > 0) {
                            bizCalc();
                            log.info("|| {} | {}", threadName, num);
                        }
                    }
                    finally {
                        log.info("<< {} unlock\n", threadName);
                        LockUtil.unlock(managerName, lockName);
                        ThreadUtil.sleepQuietly(100);
                    }
                }
                log.info("{}: {}ms", threadName, System.currentTimeMillis() - millis);
            }
        };
        for (int i = 0; i < threadNum; i++) {
            threadPool.submit(runnable);
        }
        ThreadUtil.sleepQuietly(100000);
    }

}
