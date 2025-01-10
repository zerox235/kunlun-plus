/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.event.support.aspect;

import cn.hutool.core.date.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class OperationLogAspect extends AbstractOperationLogAspect {
    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    @Pointcut("@annotation(kunlun.core.annotation.OperationLog)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // Start stop watch.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // Execute the proxy's method.
        Object result = joinPoint.proceed();
        // Stop stop watch.
        stopWatch.stop();
        long timeMillis = stopWatch.getLastTaskTimeMillis();
        // The main processing logic.
        try {
            process(joinPoint, timeMillis, result, null);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
        // End.
        return result;
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "th")
    public void afterThrowing(JoinPoint joinPoint, Throwable th) {
        try {
            process(joinPoint, null, null, th);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}
