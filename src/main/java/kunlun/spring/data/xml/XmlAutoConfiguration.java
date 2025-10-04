/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.data.xml;

import kunlun.data.xml.XmlUtil;
import kunlun.data.xml.support.XStreamXmlProcessor;
import kunlun.util.ClassLoaderUtil;
import kunlun.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static kunlun.data.xml.XmlUtil.getDefaultProcessorName;

/**
 * The xml tools auto-configuration.
 * @author Kahle
 */
@Configuration
public class XmlAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(XmlAutoConfiguration.class);
    private static final String X_STREAM_CLASS = "com.thoughtworks.xstream.XStream";

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtil.getDefaultClassLoader();
        if (ClassUtil.isPresent(X_STREAM_CLASS, classLoader)) {
            XStreamXmlProcessor xmlProcessor = new XStreamXmlProcessor();
            XmlUtil.registerProcessor(getDefaultProcessorName(), xmlProcessor);
            XmlUtil.registerProcessor("xstream", xmlProcessor);
        }
//        else if (ClassUtil.isPresent("", classLoader)) {
//            XmlUtils.registerHandler();
//        }
        else { log.warn("Can not found any implementation, will keep default. "); }
    }

    @Override
    public void destroy() throws Exception {

    }

}
