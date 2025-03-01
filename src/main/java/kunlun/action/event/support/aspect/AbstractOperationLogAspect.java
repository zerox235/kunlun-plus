/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.event.support.aspect;

import kunlun.action.ActionUtil;
import kunlun.action.event.Event;
import kunlun.aop.support.aspectj.AbstractAspect;
import kunlun.core.annotation.OperationLog;
import kunlun.data.json.JsonUtil;
import kunlun.exception.ExceptionUtil;
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
            log.info("The operation named \"{}\"'s input parameters is: {}", name, JsonUtil.toJsonString(args));
        }
        if (print && output) {
            log.info("The operation named \"{}\"'s output values is: {}", name, JsonUtil.toJsonString(result));
        }

        Event event = Event.ofOperationLog()
                .appendMessage(name)
                .putData("javaMethod", String.valueOf(method))
                .putData("timeSpent", timeSpent);
        if (input) { event.putData("input", args); }
        if (output) { event.putData("output", result); }
        if (th != null) {
            event.setLevel(Event.Level.ERROR)
                    .appendError(ExceptionUtil.toString(th));
        }
        ActionUtil.execute(event);
    }

}
