/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring;

import kunlun.spring.util.RequestContextUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The request context holder tools.
 * @author Kahle
 * @see RequestContextHolder
 * @see RequestAttributes
 */
@Deprecated
public class RequestContextUtils {

    public static HttpServletRequest getRequest() {

        return RequestContextUtil.getRequest();
    }

    public static HttpServletResponse getResponse() {

        return RequestContextUtil.getResponse();
    }

}
