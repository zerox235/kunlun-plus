/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.support;

import kunlun.action.ActionUtil;
import kunlun.common.Result;
import kunlun.common.constant.Nil;
import kunlun.data.CodeDefinition;
import kunlun.data.Event;
import kunlun.exception.BusinessException;
import kunlun.exception.ExceptionUtil;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static kunlun.common.constant.Symbols.SLASH;
import static kunlun.util.StrUtil.isNotBlank;

/**
 * The simple servlet error handler.
 * @author Kahle
 */
public class SimpleServletErrorHandler extends AbstractServletErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleServletErrorHandler.class);
    private final Boolean internalErrorPage;
    private final String  baseTemplatePath;

    public SimpleServletErrorHandler(Boolean internalErrorPage, String baseTemplatePath) {
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        Assert.notNull(internalErrorPage, "Parameter \"internalErrorPage\" must not null. ");
        this.internalErrorPage = internalErrorPage;
        this.baseTemplatePath  = baseTemplatePath;
    }

    protected String transformValidation(Throwable throwable) {
        StringBuilder builder = new StringBuilder(); String separator = "，";
        boolean first = true;
        if (throwable instanceof MethodArgumentNotValidException ||
                throwable instanceof BindException) {
            BindingResult bindingResult = null;
            if (throwable instanceof MethodArgumentNotValidException) {
                bindingResult = ((MethodArgumentNotValidException) throwable).getBindingResult();
            }
            if (throwable instanceof BindException) {
                bindingResult = ((BindException) throwable).getBindingResult();
            }
            if (!bindingResult.hasErrors()) { return null; }
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (first) { first = false; } else { builder.append(separator); }
                builder.append(error.getDefaultMessage());
            }
            return String.valueOf(builder);
        } else if (throwable instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) throwable;
            if (CollUtil.isEmpty(ex.getConstraintViolations())) { return null; }
            for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                if (first) { first = false; } else { builder.append(separator); }
                builder.append(violation.getMessage());
            }
            return String.valueOf(builder);
        } else { return null; }
    }

    protected String transformUsual(Throwable throwable) {
        String msg;
        if (throwable instanceof IllegalArgumentException ||
                throwable instanceof IllegalStateException) {
            return isNotBlank(msg = throwable.getMessage()) ? msg : throwable.getClass().getSimpleName();
        } else { return null; }
    }

    protected String transformOther(Throwable throwable) {

        return null;
    }

    @Override
    public Result<Object> transform(HttpServletRequest request, HttpServletResponse response, Throwable th) {
        if (th == null) {
            // if error is null.
            record("null-error", request, response, Nil.<Throwable>g());
            return Result.failure("An error has occurred. (Response Status: " + response.getStatus() + ") ");
        } else if (!(th instanceof BusinessException)) {
            // if error not is BusinessException.
            String msg;
            //
            if (isNotBlank(msg = transformUsual(th))) {
                record("not-biz-usual", request, response, th); return Result.failure(msg);
            }
            // Spring validation exception.
            if (isNotBlank(msg = transformValidation(th))) {
                record("not-biz-valid", request, response, th); return Result.failure(msg);
            }
            // .
            if (isNotBlank(msg = transformOther(th))) {
                record("not-biz-other", request, response, th); return Result.failure(msg);
            }
            // the fallback logic
            record("not-biz-actual", request, response, th);
            return Result.failure();
        } else {
            // if is BusinessException.
            BusinessException bizException = (BusinessException) th;
            CodeDefinition errorCode = bizException.getErrorCode();
            String msg = bizException.getMessage();
            // The message in the exception is the actual one.
            record("biz-error", request, response, th);
            return errorCode != null ? Result.failure(errorCode.getCode(), msg) : Result.failure(msg);
        }
    }

    @Override
    public void record(String label, HttpServletRequest request, HttpServletResponse response, Throwable th) {
        String respStatus = String.valueOf(response.getStatus());
        ActionUtil.execute(Event.ofRunLog()
                .setLevel(Event.Level.ERROR)
                .setModule("error-collector")
                .appendMessage(th != null ? th.getMessage() : respStatus)
                .appendError(th != null ? ExceptionUtil.toString(th) : respStatus)
                .putData("error-label", label)
        );
    }

    @Override
    public Object forHtml(HttpServletRequest request, HttpServletResponse response, Throwable th) {
        // transform error to result.
        Result<Object> result = transform(request, response, th);
        // if error page.
        int responseStatus = response.getStatus();
        if (!internalErrorPage) {
            String viewPath = baseTemplatePath + SLASH + responseStatus;
            ModelAndView modelAndView = new ModelAndView(viewPath);
            modelAndView.addObject("responseStatus", responseStatus);
            modelAndView.addObject("errorCode", result.getCode());
            modelAndView.addObject("errorMessage", result.getMessage());
            return modelAndView;
        }
        // response write html.
        return writeHtml(response, String.valueOf(result.getCode()), result.getMessage());
    }

}
