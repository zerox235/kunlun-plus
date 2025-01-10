/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.event.support;

import kunlun.action.event.Event;
import kunlun.action.event.EventCollector;
import kunlun.servlet.RequestUtils;
import kunlun.spring.RequestContextUtils;
import kunlun.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * The abstract web event collector.
 * @author Kahle
 */
public abstract class AbstractWebEventCollector extends EventCollector {

    /**
     * Get the value from the event data based on the key.
     * @param trackData The event data
     * @param key The key
     * @param defaultValue The default value
     * @return The value
     */
    protected String takeOut(Map<?, ?> trackData, Object key, String defaultValue) {
        Object valueObj = trackData.get(key);
        if (valueObj == null) { return defaultValue; }
        String value = String.valueOf(valueObj);
        trackData.remove(key);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * Process and fill the client information.
     * @param event The event record passed in
     * @param request The HTTP request object
     */
    protected void clientInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        if (request == null) { return; }
        // The client app id.
        String clientAppIdName = takeOut(event.getData(), "clientAppIdName", "appId");
        String clientAppId = request.getHeader(clientAppIdName);
        if (StringUtils.isNotBlank(clientAppId)) {
            event.putData("clientAppId", clientAppId);
        }
        // The client device id.
        String clientDeviceIdName = takeOut(event.getData(), "clientDeviceIdName", "deviceId");
        String clientDeviceId = request.getHeader(clientDeviceIdName);
        if (StringUtils.isNotBlank(clientDeviceId)) {
            event.putData("clientDeviceId", clientDeviceId);
        }
        // The client net address.
        event.putData("clientNetAddress", RequestUtils.getRemoteAddress(request));
        // The client user agent.
        event.putData("clientUserAgent", RequestUtils.getUserAgent(request));
    }

    /**
     * Process and fill the user information.
     * @param event The event record passed in
     * @param request The HTTP request object
     */
    protected void userInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        if (request == null) { return; }
        // The token.
        String tokenName = takeOut(event.getData(), "tokenName", "authorization");
        String token = request.getHeader(tokenName);
        if (StringUtils.isNotBlank(token)) {
            event.putData("token", token);
        }
    }

    /**
     * Process and fill the location information.
     * @param event The event record passed in
     * @param request The HTTP request object
     */
    protected void locationInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        //if (request == null) { return; }

    }

    /**
     * Process and fill the request information.
     * @param event The event record passed in
     * @param request The HTTP request object
     */
    protected void requestInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        if (request == null) { return; }
        event.putData("requestApiUri", request.getRequestURI());
        //track.putData("requestApiName", EMPTY_STRING)
        event.putData("requestMethod", request.getMethod());
        event.putData("requestUrl", String.valueOf(request.getRequestURL()));
        event.putData("requestReferer", RequestUtils.getReferer(request));
    }

    /**
     * Process and fill the other information.
     * @param event The event record passed in
     */
    protected void otherInfo(Event event, HttpServletRequest request, HttpServletResponse response) {
        //if (request == null) { return; }

    }

    @Override
    protected void process(Event event) {
        // Filling common properties has already been done.
        // Get the HTTP request and response objects.
        HttpServletResponse response = RequestContextUtils.getResponse();
        HttpServletRequest request = RequestContextUtils.getRequest();
        // Process server information (such as "serverName", "serverAppName").
        // The server information is fixed in "commonProperties".
        // Process client information.
        clientInfo(event, request, response);
        // Process principal information.
        userInfo(event, request, response);
        // Process location information.
        locationInfo(event, request, response);
        // Process request information.
        requestInfo(event, request, response);
        // Process other information.
        otherInfo(event, request, response);
    }

}
