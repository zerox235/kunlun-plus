/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The spring project "ApplicationContext" tools.
 * @author Kahle
 * @see org.springframework.context.ApplicationContext
 */
public class SpringUtil {
    private static ApplicationContext context = null;

    public static ApplicationContext getApplicationContext() {

        return context;
    }

    public static void setApplicationContext(ApplicationContext context) {

        SpringUtil.context = context;
    }

    public static Object getBean(String name) {

        return getApplicationContext().getBean(name);
    }

    public static Object getBean(String name, Object... args) {

        return getApplicationContext().getBean(name, args);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {

        return getApplicationContext().getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) {

        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, Object... args) {

        return getApplicationContext().getBean(requiredType, args);
    }

    public static void registerBean(String name, BeanDefinitionBuilder beanDefinitionBuilder) {
        // Beans registered by this method can only be obtained by "ApplicationContext.getBean"
        //      and cannot be obtained by "Autowired".
        // "BeanDefinitionBuilder.rootBeanDefinition(clazz);"      parent   bean
        // "BeanDefinitionBuilder.childBeanDefinition(clazz);"     child    bean
        // "BeanDefinitionBuilder.genericBeanDefinition(clazz);"   generic  bean
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) getApplicationContext();
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
    }

}
