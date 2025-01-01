/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.aspect;

import kunlun.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The aspect for @Permission base on aspectj.
 * @see kunlun.core.annotation.Permission
 * @see kunlun.security.support.aspect.AbstractPermissionAspect
 * @author Kahle
 */
@Aspect
public class PermissionAspect extends AbstractPermissionAspect {
    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);

    @Pointcut("@annotation(kunlun.core.annotation.Permission)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // The main processing logic.
        try {
            process(joinPoint);
        } catch (Exception e) {
            throw new BusinessException("Permission check error! ", e);
        }
        // Execute the proxy's method.
        return joinPoint.proceed();
    }

}
