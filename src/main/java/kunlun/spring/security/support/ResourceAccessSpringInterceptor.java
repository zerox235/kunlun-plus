/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.security.support;

import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.util.Assert;
import kunlun.util.StrUtil;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static kunlun.common.constant.Numbers.ZERO;

/**
 * Support for resource access pre handler based on spring interceptor.
 * @author Kahle
 */
public class ResourceAccessSpringInterceptor extends HandlerInterceptorAdapter {
    private static final String ACCESS_TYPE = "spring-servlet-interceptor";
    private static final String DEF_TOKEN_NAME = "authorization";
    private final ResourceAccessPreHandler accessPreHandler;
    private final String tokenName;

    public ResourceAccessSpringInterceptor(ResourceAccessPreHandler accessPreHandler, String tokenName) {
        Assert.notNull(accessPreHandler, "Parameter \"accessPreHandler\" must not null. ");
        Assert.notBlank(tokenName, "Parameter \"tokenName\" must not blank. ");
        this.accessPreHandler = accessPreHandler;
        this.tokenName = tokenName;
    }

    public ResourceAccessSpringInterceptor(ResourceAccessPreHandler accessPreHandler) {

        this(accessPreHandler, DEF_TOKEN_NAME);
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // Get the token from the request header.
        String token = request.getHeader(tokenName);
        // Try to obtain the token from the request parameters.
        // Support both uppercase and lowercase letters.
        if (StrUtil.isBlank(token)) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                if (tokenName.equalsIgnoreCase(entry.getKey())) {
                    token = entry.getValue()[ZERO];
                }
            }
        }
        // Obtain the request path.
        String path = request.getServletPath();
        return (Boolean) accessPreHandler.handle(ACCESS_TYPE, path, token, request, response);
    }

}
