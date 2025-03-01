/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import kunlun.common.Errors;
import kunlun.common.Result;
import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.data.json.JsonUtil;
import kunlun.exception.ExceptionUtil;
import kunlun.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static kunlun.common.constant.Numbers.*;

public class SimpleResourceAccessPreHandler implements ResourceAccessPreHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleResourceAccessPreHandler.class);

    private final List<String> ignoredUrls;
    private final boolean showLog;

    public SimpleResourceAccessPreHandler(boolean showLog, List<String> ignoredUrls) {
        this.ignoredUrls = ignoredUrls;
        this.showLog = showLog;
    }

    protected boolean isIgnoredUrl(String requestUrl) {
        //
        if (requestUrl.endsWith(".js")
                || requestUrl.endsWith(".css")
                || requestUrl.endsWith(".html")
                || requestUrl.endsWith(".ico")
                || requestUrl.contains("swagger-")
                || requestUrl.endsWith("api-docs")
                || requestUrl.contains("error")) {
            return true;
        }
        //
        if (CollUtil.isEmpty(ignoredUrls)) { return false; }
        for (String url : ignoredUrls) {
            if (requestUrl.equalsIgnoreCase(url.trim())) {
                return true;
            }
        }
        return false;
    }

    protected void write(HttpServletResponse response, Result<Object> result) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JsonUtil.toJsonString("jackson", result));
        }
        catch (Exception e) {
            throw ExceptionUtil.wrap(e);
        }
    }

    protected boolean doHandle(HttpServletRequest request,
                               HttpServletResponse response,
                               String requestUrl,
                               String token) {
        if (StrUtil.isBlank(requestUrl)) {
            write(response, Result.failure(Errors.notFound));
            return false;
        }
        requestUrl = requestUrl.trim();

        if (showLog) {
            log.info(">>>> resource access -> request url: {}, ignored urls: {}"
                    , requestUrl, JSON.toJSONString(ignoredUrls));
        }

        //
        if (isIgnoredUrl(requestUrl)) { return true; }

        int verifyToken = SecurityUtil.getTokenManager().verifyToken(token);
        if (verifyToken != ONE) {
            // not token
            if (verifyToken == MINUS_ONE) {
                write(response, Result.failure(Errors.noLogin));
            } else if (verifyToken == MINUS_TWO || verifyToken == MINUS_THREE) {
                write(response, Result.failure(Errors.noLogin));
            } else {
                write(response, Result.failure(Errors.unauthorized));
            }
            return false;
        }

        //
        return true;
    }

    @Override
    public Object handle(Object type, Object resource, String token, Object... arguments) {
        log.debug("Resource access pre handler: type={}, token={}, url={}", type, token, resource);
        String requestUrl = resource != null ? String.valueOf(resource) : null;
        HttpServletResponse response = (HttpServletResponse) arguments[1];
        HttpServletRequest request = (HttpServletRequest) arguments[0];
        try {
            return doHandle(request, response, requestUrl, token);
        }
        catch (Exception e) {
            log.error("Resource access pre handler error", e);
            write(response, Result.failure());
            return false;
        }
    }

}
