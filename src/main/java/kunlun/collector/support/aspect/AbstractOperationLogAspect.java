/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.collector.support.aspect;

import kunlun.aop.support.aspectj.AbstractAspect;
import kunlun.collector.CollectorUtils;
import kunlun.collector.annotation.OperationLog;
import kunlun.collector.support.model.Event;
import kunlun.data.json.JsonUtils;
import kunlun.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public abstract class AbstractOperationLogAspect extends AbstractAspect {
    private static final Class<?>[] IGNORE_TYPES =
            new Class[] {HttpServletRequest.class, HttpServletResponse.class, MultipartFile.class};
    private static final Logger log = LoggerFactory.getLogger(AbstractOperationLogAspect.class);

    protected void process(JoinPoint joinPoint, Long timeSpent, Object result, Throwable th) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSign = (MethodSignature) signature;
        Method method = methodSign.getMethod();

        OperationLog opLog = method.getAnnotation(OperationLog.class);
        String  name   = opLog.name();
        boolean input  = opLog.input();
        boolean output = opLog.output();
        boolean print  = opLog.print();

        Object[] args = getArguments(joinPoint, IGNORE_TYPES);
        if (print && input) {
            log.info("The operation named \"{}\"'s input parameters is: {}", name, JsonUtils.toJsonString(args));
        }
        if (print && output) {
            log.info("The operation named \"{}\"'s output values is: {}", name, JsonUtils.toJsonString(result));
        }

        Event event = Event.ofOperationLog()
                .appendMessage(name)
                .putData("javaMethod", String.valueOf(method))
                .putData("timeSpent", timeSpent);
        if (input) { event.putData("input", args); }
        if (output) { event.putData("output", result); }
        if (th != null) {
            event.setLevel(Event.Level.ERROR)
                    .appendError(ExceptionUtils.toString(th));
        }
        CollectorUtils.collect(event);
    }

}
