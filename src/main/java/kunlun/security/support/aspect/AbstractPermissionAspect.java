/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.aspect;

import cn.hutool.core.util.StrUtil;
import kunlun.aop.support.aspectj.AbstractAspect;
import kunlun.core.annotation.Permission;
import kunlun.exception.BusinessException;
import kunlun.security.SecurityUtils;
import kunlun.security.support.AbstractDataController;
import kunlun.security.support.AbstractSecurityContext;
import kunlun.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import static kunlun.security.support.AbstractDataController.CONTEXT_KEY;

/**
 * The abstract aspect for @Permission base on aspectj.
 * @see kunlun.core.annotation.Permission
 * @author Kahle
 */
public abstract class AbstractPermissionAspect extends AbstractAspect {

    protected void process(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSign = (MethodSignature) signature;
        Method method = methodSign.getMethod();

        Permission permission = method.getAnnotation(Permission.class);
        String value = permission.value();
        String access = permission.access();
        String data = permission.data();
        if (StringUtils.isNotBlank(value)) {
            if (StringUtils.isBlank(access)) {
                access = value;
            }
            if (StringUtils.isBlank(data)) {
                data = value;
            }
        }
        Object userType = SecurityUtils.getUserType();
        Object userId = SecurityUtils.getUserId();

        if (StrUtil.isNotBlank(access)) {
            if (!SecurityUtils.hasPermission(userId, userType, access)) {
                throw new BusinessException("Permission Denied! ");
            }
        }

        if (StrUtil.isNotBlank(data)) {
            AbstractDataController.Context context = new AbstractDataController.Context();
            context.setPermission(data);
            context.setUserId(userId);
            context.setUserType(userType);
            context.setUserGroups(SecurityUtils.getUserGroups("nested"));
            context.setRule(SecurityUtils.getDataController().getRule(data, userId, userType));
            ((AbstractSecurityContext) SecurityUtils.getContext()).setProperty(CONTEXT_KEY, context);
        }
    }

}
