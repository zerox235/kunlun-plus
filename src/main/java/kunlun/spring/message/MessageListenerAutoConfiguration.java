package kunlun.spring.message;

import kunlun.action.ActionUtil;
import kunlun.data.tuple.Triple;
import kunlun.data.tuple.TripleImpl;
import kunlun.message.annotation.MessageListener;
import kunlun.message.model.Subscribe;
import kunlun.message.support.MethodBasedMessageListener;
import kunlun.util.MapUtil;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class MessageListenerAutoConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private ApplicationContext appContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext appContext) {

        this.appContext = appContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 从 Spring 容器中获取含有 @MessageListener 注解的对象
        Map<String, Triple<MessageListener, Object, Method>> methodMap = new LinkedHashMap<String, Triple<MessageListener, Object, Method>>();
        Map<String, Object> beans = appContext.getBeansOfType(Object.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            processClassLevelAnnotation(entry.getKey(), entry.getValue());
            processMethodLevelAnnotations(entry.getKey(), entry.getValue(), methodMap);
        }
        // 将方法注解对应的方法进行注册
        if (MapUtil.isNotEmpty(methodMap)) {
            for (Map.Entry<String, Triple<MessageListener, Object, Method>> entry : methodMap.entrySet()) {
                MessageListener listener = entry.getValue().getLeft();
                Object bean = entry.getValue().getMiddle();
                Method method = entry.getValue().getRight();
                ActionUtil.execute(listener.manager(), Subscribe.Builder.of(listener.topic())
                        .setSubExpression(listener.subExpression())
                        .setMessageListener(new MethodBasedMessageListener(method, bean))
                        .build());
            }
        }
    }

    /**
     * 处理类级别的注解.<br />
     * @param beanName Spring 的 bean 名称
     * @param bean     Spring 的 bean 对象
     */
    protected void processClassLevelAnnotation(String beanName, Object bean) {
        MessageListener annotation = AnnotationUtils.findAnnotation(bean.getClass(), MessageListener.class);
        if (annotation != null) {
            if (bean instanceof kunlun.message.MessageListener) {
                ActionUtil.execute(annotation.manager(), Subscribe.Builder.of(annotation.topic())
                        .setSubExpression(annotation.subExpression())
                        .setMessageListener(bean)
                        .build());
            } else {
                throw new IllegalStateException("Bean '" + beanName + "' has @MessageListener on class level " +
                        "but does not implement MessageListener interface");
            }
        }
    }

    protected boolean isAopMethod(@NonNull Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        String className = declaringClass.getName();
        return className.contains("$$EnhancerBySpringCGLIB$$") ||
                className.contains("$$FastClassBySpringCGLIB$$") ||
                className.startsWith("com.sun.proxy.$Proxy");
    }

    /**
     * 处理方法级别的注解.<br />
     * @param beanName Spring 的 bean 名称
     * @param bean     Spring 的 bean 对象
     * @param methodMap 被增加注解的方法的 Map
     */
    protected void processMethodLevelAnnotations(final String beanName, final Object bean,
                                                 final Map<String, Triple<MessageListener, Object, Method>> methodMap) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(@NonNull Method method) throws IllegalArgumentException {
                MessageListener annotation = AnnotationUtils.findAnnotation(method, MessageListener.class);
                if (annotation == null) { return; }
                // MessageListener 的实例的 hashCode 是一样的，但是本质上他们的 class 的 name 是不一样的
                // 所以不能直接拿 MessageListener 的实例作为 Map 的 Key
                String annoClass = annotation.getClass().getName();
                int hashCode = System.identityHashCode(annotation);
                annoClass = annoClass + hashCode;
                // 判断数据是否已经存在
                Triple<MessageListener, Object, Method> pair = methodMap.get(annoClass);
                if (pair == null || isAopMethod(method)) {
                    methodMap.put(annoClass, new TripleImpl<MessageListener, Object, Method>(annotation, bean, method));
                }
            }
        });
    }

}
