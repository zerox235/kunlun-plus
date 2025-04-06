/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.util;

import kunlun.util.ArrayUtil;
import kunlun.util.Assert;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * The spring project "ApplicationContext" tools.
 * @author Kahle
 * @see org.springframework.context.ApplicationContext
 */
public class SpringUtil {
    private static ConfigurableApplicationContext applicationContext = null;

    // region ----- get / set application context

    public static ConfigurableApplicationContext getApplicationContext() {

        return Assert.notNull(applicationContext);
    }

    public static void setApplicationContext(ConfigurableApplicationContext context) {

        SpringUtil.applicationContext = context;
    }

    // endregion ----- get / set application context


    // region ----- get environment and bean factory

    public static Environment getEnvironment() {

        return getApplicationContext() != null ? getApplicationContext().getEnvironment() : null;
    }

    public static ListableBeanFactory getBeanFactory() {

        return getApplicationContext();
    }

    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {

        return getApplicationContext().getBeanFactory();
    }

    // endregion ----- get environment and bean factory


    // region ----- get env property

    public static String getProperty(String key) {

        return getEnvironment() != null ? getEnvironment().getProperty(key) : null;
    }

    public static String getProperty(String key, String defaultValue) {

        return getEnvironment() != null ? getEnvironment().getProperty(key, defaultValue) : null;
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {

        return getEnvironment() != null ? getEnvironment().getProperty(key, targetType, defaultValue) : null;
    }

    // endregion ----- get env property


    // region ----- get common property

    public static String getApplicationName() {

        return getProperty("spring.application.name");
    }

    public static String getActiveProfile() {

        return getProperty("spring.profiles.active");
    }

    // endregion ----- get common property


    // region ----- get bean

    public static Object getBean(String name, Object... args) {
        if (ArrayUtil.isEmpty(args)) {
            return getApplicationContext().getBean(name);
        }
        return getApplicationContext().getBean(name, args);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {

        return getApplicationContext().getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, Object... args) {
        if (ArrayUtil.isEmpty(args)) {
            return getApplicationContext().getBean(requiredType);
        }
        return getApplicationContext().getBean(requiredType, args);
    }

//    @SuppressWarnings("unchecked")
//    public static <T> T getBean(TypeReference<T> reference) {
//    }

    public static boolean containsBean(String name) {

        return getApplicationContext().containsBean(name);
    }

    public static String[] getBeanNamesForType(Class<?> type) {

        return getApplicationContext().getBeanNamesForType(type);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {

        return getApplicationContext().getBeansOfType(type);
    }

    // endregion ----- get bean


    // region ----- register / unregister bean

    public static <T> void registerBean(String beanName, T bean) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    public static void unregisterBean(String beanName) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        Assert.isInstanceOf(DefaultSingletonBeanRegistry.class, factory
                , "Can not unregister bean, the bean factory not is bean registry! ");
        ((DefaultSingletonBeanRegistry) factory).destroySingleton(beanName);
    }

    // endregion ----- register / unregister bean


    // region ----- publish event

    public static void publishEvent(ApplicationEvent event) {

        getApplicationContext().publishEvent(event);
    }

    public static void publishEvent(Object event) {

        getApplicationContext().publishEvent(event);
    }

    // endregion ----- publish event

}
