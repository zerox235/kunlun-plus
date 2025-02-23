/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.codec;

import kunlun.codec.CodecUtils;
import kunlun.codec.support.ApacheBase64;
import kunlun.codec.support.Java8Base64;
import kunlun.util.ClassLoaderUtil;
import kunlun.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static kunlun.codec.CodecUtils.BASE64;

/**
 * The base64 auto-configuration.
 * @see org.apache.commons.codec.binary.Base64
 * @author Kahle
 */
@Configuration
public class Base64AutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BASE64 = "org.apache.commons.codec.binary.Base64";
    private static final String JAVA_BASE64 = "java.util.Base64";
    private static final Logger log = LoggerFactory.getLogger(Base64AutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtil.getDefaultClassLoader();
        if (ClassUtil.isPresent(APACHE_BASE64, classLoader)) {
            // If have Apache Commons Codec, to use it.
            CodecUtils.registerCodec(BASE64, new ApacheBase64());
        }
        else if (ClassUtil.isPresent(JAVA_BASE64, classLoader)) {
            // If have "java.util.Base64", to use it.
            CodecUtils.registerCodec(BASE64, new Java8Base64());
        }
        log.info("The base64 tools was initialized success. ");
    }

    @Override
    public void destroy() throws Exception {

    }

}
