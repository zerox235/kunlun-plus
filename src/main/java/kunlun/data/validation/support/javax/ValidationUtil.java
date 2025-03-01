/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.validation.support.javax;

import kunlun.util.Assert;
import kunlun.util.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static kunlun.util.ObjUtil.cast;

/**
 * The javax validator based validation tools.
 * @author Kahle
 * @see javax.validation.Validator
 */
public class ValidationUtil {
    private static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);
    private static volatile ResultHandler resultHandler;
    private static volatile Validator validator;

    public static Validator getValidator() {
        if (validator != null) { return validator; }
        synchronized (ValidationUtil.class) {
            if (validator != null) { return validator; }
            ValidationUtil.setValidator(Validation
                    .buildDefaultValidatorFactory().getValidator());
            return validator;
        }
    }

    public static void setValidator(Validator validator) {
        Assert.notNull(validator, "Parameter \"validator\" must not null. ");
        log.info("Set javax validator: {}", validator.getClass().getName());
        ValidationUtil.validator = validator;
    }

    public static ResultHandler getResultHandler() {
        if (resultHandler != null) { return resultHandler; }
        synchronized (ValidationUtil.class) {
            if (resultHandler != null) { return resultHandler; }
            ValidationUtil.setResultHandler(new InnerResultHandler());
            return resultHandler;
        }
    }

    public static void setResultHandler(ResultHandler handler) {
        Assert.notNull(handler, "Parameter \"handler\" must not null. ");
        log.info("Set result handler: {}", handler.getClass().getName());
        ValidationUtil.resultHandler = handler;
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {

        return getValidator().validate(object, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName,
                                                                   Class<?>... groups) {
        return getValidator().validateProperty(object, propertyName, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName,
                                                                Object value, Class<?>... groups) {
        return getValidator().validateValue(beanType, propertyName, value, groups);
    }

    public static <T> void validateToThrow(T object, Class<?>... groups) {

        getResultHandler().handle(validate(object, groups));
    }

    public static <T> void validatePropertyToThrow(T object, String propertyName,
                                                                   Class<?>... groups) {
        getResultHandler().handle(validateProperty(object, propertyName, groups));
    }

    public static <T> void validateValueToThrow(Class<T> beanType, String propertyName,
                                                                Object value, Class<?>... groups) {
        getResultHandler().handle(validateValue(beanType, propertyName, value, groups));
    }

    /**
     * The inner result handler of the javax validator.
     * @author Kahle
     */
    public static class InnerResultHandler implements ResultHandler {
        @Override
        public Object handle(Object result) {
            Set<ConstraintViolation<?>> set = cast(result);
            if (CollUtil.isEmpty(set)) {
                return null;
            }
            throw new ConstraintViolationException(set);
        }
    }

}
