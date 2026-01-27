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
        Map<String, Triple<MessageListener, Object, Method>> methodMap = new LinkedHashMap<String, Triple<MessageListener, Object, Method>>();
        Map<String, Object> beans = appContext.getBeansOfType(Object.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            processClassLevelAnnotation(entry.getKey(), bean, bean.getClass());
            processMethodLevelAnnotations(methodMap, entry.getKey(), bean, bean.getClass());
        }
        //
        if (MapUtil.isNotEmpty(methodMap)) {
            for (Map.Entry<String, Triple<MessageListener, Object, Method>> entry : methodMap.entrySet()) {
                MessageListener listener = entry.getValue().getLeft();
                Object bean = entry.getValue().getMiddle();
                Method method = entry.getValue().getRight();
//                MessageListener listener = entry.getKey();
                ActionUtil.execute(listener.manager(), Subscribe.Builder.of(listener.topic())
                        .setSubExpression(listener.subExpression())
                        .setMessageListener(new MethodBasedMessageListener(method, bean))
                        .build());
            }
        }
    }

    protected void processClassLevelAnnotation(String beanName, Object bean, Class<?> beanClass) {
        MessageListener annotation = AnnotationUtils.findAnnotation(beanClass, MessageListener.class);
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

    protected void processMethodLevelAnnotations(final Map<String, Triple<MessageListener, Object, Method>> methodMap,
                                                 String beanName, final Object bean, Class<?> beanClass) {
        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(@NonNull Method method) throws IllegalArgumentException {
                MessageListener annotation = AnnotationUtils.findAnnotation(method, MessageListener.class);
                if (annotation != null) {
                    // MessageListener 的实例的 hashCode 是一样的，但是本质上他们的 class 的 name 是不一样的
                    String annoClass = annotation.getClass().getName();
                    int hashCode = System.identityHashCode(annotation);
                    annoClass = annoClass + hashCode;
                    Triple<MessageListener, Object, Method> pair = methodMap.get(annoClass);
                    if (pair == null || isAopMethod(method)) {
                        methodMap.put(annoClass, new TripleImpl<MessageListener, Object, Method>(annotation, bean, method));
                    }
                }
            }
        });
    }

}
