/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.aop;

import kunlun.aop.ProxyHandler;
import kunlun.aop.ProxyUtil;
import kunlun.aop.support.CglibProxyHandler;
import kunlun.aop.support.SpringCglibProxyHandler;
import kunlun.common.constant.Words;
import kunlun.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * The cglib aop auto-configuration.
 * @author Kahle
 */
@Configuration
public class CglibAopAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CglibAopAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ProxyHandler proxyHandler;
        if (ClassUtil.isPresent("net.sf.cglib.proxy.MethodInterceptor")) {
            ProxyUtil.registerHandler("cglib", proxyHandler = new CglibProxyHandler());
            ProxyUtil.registerHandler(Words.DEFAULT, proxyHandler);
            log.debug("The cglib proxy handler was initialized success. ");
        }
        if (ClassUtil.isPresent("org.springframework.cglib.proxy.MethodInterceptor")) {
            ProxyUtil.registerHandler("spring-cglib", proxyHandler = new SpringCglibProxyHandler());
            ProxyUtil.registerHandler(Words.DEFAULT, proxyHandler);
            log.debug("The spring cglib proxy factory was initialized success. ");
        }
    }

}
