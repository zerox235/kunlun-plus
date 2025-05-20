/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.data.property;

import kunlun.data.property.PropertyUtil;
import kunlun.data.property.support.SpringEnvPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * PropertySourceAutoConfiguration
 * @author Kahle
 */
@Configuration
public class PropertySourceAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(PropertySourceAutoConfiguration.class);

    @Autowired
    public PropertySourceAutoConfiguration(Environment env) {
        if (env != null) {
            PropertyUtil.registerSource("springEnv", new SpringEnvPropertySource(env));
        }
    }

}
