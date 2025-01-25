/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.aspect;

import cn.hutool.core.util.StrUtil;
import kunlun.aop.support.aspectj.AbstractAspect;
import kunlun.core.annotation.Permission;
import kunlun.data.json.JsonUtils;
import kunlun.exception.BusinessException;
import kunlun.security.SecurityUtils;
import kunlun.security.support.AbstractDataController;
import kunlun.security.support.AbstractSecurityContext;
import kunlun.security.support.util.DataScope;
import kunlun.util.CollectionUtils;
import kunlun.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import static kunlun.security.support.AbstractDataController.CONTEXT_KEY;
import static kunlun.security.support.AbstractSqlBasedDataController.SimpleRule;

/**
 * The abstract aspect for @Permission base on aspectj.
 * @see kunlun.core.annotation.Permission
 * @author Kahle
 */
public abstract class AbstractPermissionAspect extends AbstractAspect {

    protected void process(JoinPoint joinPoint) {
        // Process the parameter of the Aspect.
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSign = (MethodSignature) signature;
        Method method = methodSign.getMethod();
        // Extract annotation data and process default value.
        Permission permission = method.getAnnotation(Permission.class);
        String value  = permission.value();
        String access = permission.access();
        String data   = permission.data();
        String config = permission.config();
        if (StringUtils.isNotBlank(value)) {
            access = StringUtils.isBlank(access) ? value : access;
            data   = StringUtils.isBlank(data) ? value : data;
        }
        // Get the current login user information.
        Object userType = SecurityUtils.getUserType();
        Object userId = SecurityUtils.getUserId();
        // Check access permission.
        if (StrUtil.isNotBlank(access)) {
            if (!SecurityUtils.hasPermission(userId, userType, access)) {
                throw new BusinessException("Permission Denied! ");
            }
        }
        // Build the context of the data permission.
        if (StrUtil.isNotBlank(data)) {
            AbstractDataController.Context context = new AbstractDataController.Context();
            context.setPermission(data);
            context.setUserId(userId);
            context.setUserType(userType);
            context.setUserGroups(SecurityUtils.getUserGroups("nested"));
            // Build data permission rule.
            SimpleRule rule = null;
            if (StringUtils.isNotBlank(config)) {
                rule = JsonUtils.parseObject(config, SimpleRule.class);
            }
            if (rule == null) { rule = new SimpleRule(); }
            // Get the rule for the current login user.
            SimpleRule userRule = SecurityUtils.getDataController().getRule(data, userId, userType);
            // The user rule can override annotation rule.
            if (userRule != null) {
                if (userRule.getDataScope() != null && DataScope.ALL.equals(userRule.getDataScope())) {
                    rule.setDataScope(userRule.getDataScope());
                }
                if (CollectionUtils.isNotEmpty(userRule.getDataConfigs())) {
                    rule.setDataConfigs(userRule.getDataConfigs());
                }
            }
            context.setRule(rule);
            ((AbstractSecurityContext) SecurityUtils.getContext()).setProperty(CONTEXT_KEY, context);
        }
    }

}
