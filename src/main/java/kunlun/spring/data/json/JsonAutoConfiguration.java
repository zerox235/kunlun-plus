/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.data.json;

import kunlun.data.json.JsonUtil;
import kunlun.data.json.support.FastJsonProcessor;
import kunlun.data.json.support.GsonProcessor;
import kunlun.data.json.support.JacksonProcessor;
import kunlun.util.ClassLoaderUtil;
import kunlun.util.ClassUtil;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * The json tools auto-configuration.
 * @author Kahle
 */
@Configuration
public class JsonAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String JACKSON_CLASS = "com.fasterxml.jackson.databind.ObjectMapper";
    private static final String FASTJSON_CLASS = "com.alibaba.fastjson.JSON";
    private static final String GSON_CLASS = "com.google.gson.Gson";
    private static final Logger log = LoggerFactory.getLogger(JsonAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtil.getDefaultClassLoader();
        String defaultProcessor = null;
        if (ClassUtil.isPresent(JACKSON_CLASS, classLoader)) {
            JsonUtil.registerProcessor("jackson", new JacksonProcessor());
            defaultProcessor = "jackson";
        }
        if (ClassUtil.isPresent(GSON_CLASS, classLoader)) {
            JsonUtil.registerProcessor("gson", new GsonProcessor());
            if (StrUtil.isBlank(defaultProcessor)) {
                defaultProcessor = "gson";
            }
        }
        if (ClassUtil.isPresent(FASTJSON_CLASS, classLoader)) {
            JsonUtil.registerProcessor("fastjson", new FastJsonProcessor());
            if (StrUtil.isBlank(defaultProcessor)) {
                defaultProcessor = "fastjson";
            }
        }
        if (StrUtil.isNotBlank(defaultProcessor)) {
            JsonUtil.setDefaultProcessorName(defaultProcessor);
        }
        else {
            log.warn("Can not found \"jackson\" or \"gson\" or \"fastjson\", will keep default. ");
        }
    }

    @Override
    public void destroy() throws Exception {

    }

}
