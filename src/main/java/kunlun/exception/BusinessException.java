/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception;

import kunlun.common.Errors;
import kunlun.data.CodeDefinition;
import kunlun.util.ArrayUtil;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The business exception.
 * @author Kahle
 */
public class BusinessException extends UncheckedException {
    private static final Logger log = LoggerFactory.getLogger(BusinessException.class);
    private static volatile CodeDefinition defaultErrorCode;

    public static CodeDefinition getDefaultErrorCode() {
        if (defaultErrorCode != null) { return defaultErrorCode; }
        synchronized (BusinessException.class) {
            if (defaultErrorCode != null) { return defaultErrorCode; }
            setDefaultErrorCode(Errors.internalServerError);
        }
        return defaultErrorCode;
    }

    public static void setDefaultErrorCode(CodeDefinition defaultErrorCode) {
        Assert.notNull(defaultErrorCode, "Parameter \"defaultErrorCode\" must not null. ");
        log.debug("Set default error code: {}", defaultErrorCode);
        BusinessException.defaultErrorCode = defaultErrorCode;
    }

    protected static String renderMessage(CodeDefinition errorCode, Object... arguments) {
        Assert.notNull(errorCode, "Parameter \"errorCode\" must not null. ");
        if (ArrayUtil.isEmpty(arguments)) { return errorCode.getDescription(); }
        return Assert.renderMessage(errorCode.getDescription(), arguments);
    }

    /**
     * Note: The description in the error code is for reference only;
     *  The message in the exception is the actual one.
     */
    private final CodeDefinition errorCode;
    private final Object[] arguments;

    public BusinessException(String message, Object... arguments) {
        super(Assert.renderMessage(message, arguments));
        this.errorCode = getDefaultErrorCode();
        this.arguments = arguments;
    }

    public BusinessException(String message, Throwable cause, Object... arguments) {
        super(Assert.renderMessage(message, arguments), cause);
        this.errorCode = getDefaultErrorCode();
        this.arguments = arguments;
    }

    public BusinessException(CodeDefinition errorCode, Object... arguments) {
        super(renderMessage(errorCode, arguments));
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public BusinessException(CodeDefinition errorCode, Throwable cause, Object... arguments) {
        super(renderMessage(errorCode, arguments), cause);
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public CodeDefinition getErrorCode() {

        return errorCode;
    }

    public Object[] getArguments() {

        return arguments;
    }

}
