/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception.support;

import kunlun.common.Result;
import kunlun.core.function.Function;
import kunlun.data.CodeDefinition;
import kunlun.exception.BusinessException;
import kunlun.exception.util.SpringValidationExHandler;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import static kunlun.common.constant.Symbols.SLASH;

/**
 * The simple servlet error handler.
 * @author Kahle
 */
public class SimpleServletErrorHandler extends AbstractServletErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(SimpleServletErrorHandler.class);
    private final Function<Throwable, Result<Object>> springValidationExHandler;
    private final Boolean internalErrorPage;
    private final String  baseTemplatePath;

    public SimpleServletErrorHandler(Boolean internalErrorPage,
                                     String baseTemplatePath,
                                     Function<Throwable, Result<Object>> springValidationExHandler) {
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        Assert.notNull(internalErrorPage, "Parameter \"internalErrorPage\" must not null. ");
        if (springValidationExHandler == null) {
            springValidationExHandler = new SpringValidationExHandler();
        }
        this.internalErrorPage = internalErrorPage;
        this.baseTemplatePath  = baseTemplatePath;
        this.springValidationExHandler = springValidationExHandler;
    }

    public SimpleServletErrorHandler(Boolean internalErrorPage, String baseTemplatePath) {

        this(internalErrorPage, baseTemplatePath, null);
    }

    protected Object buildHtmlResult(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Throwable throwable) {
        // if error is null.
        Result<Object> result;
        if (throwable == null) {
            result = Result.failure("An error has occurred. (Response Status: " + response.getStatus() + ") ");
        }
        // if error not is BusinessException.
        else if (!(throwable instanceof BusinessException)) {
            result = Result.failure();
        }
        // if is BusinessException.
        else {
            BusinessException bizException = (BusinessException) throwable;
            CodeDefinition errorCode = bizException.getErrorCode();
            result = errorCode != null ? Result.failure(errorCode) : Result.failure();
        }
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
        // create html.
        String htmlString = createHtmlString(String.valueOf(result.getCode()), result.getMessage());
        // response write html.
        return writeHtmlString(request, response, htmlString);
    }

    protected Object buildOtherResult(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Throwable throwable) {
        // if error is null.
        if (throwable == null) {
            return Result.failure("An error has occurred. (Response Status: " + response.getStatus() + ") ");
        }
        // Spring validation exception.
        if (throwable instanceof BindException ||
                throwable instanceof MethodArgumentNotValidException ||
                throwable instanceof ConstraintViolationException) {
            return springValidationExHandler.apply(throwable);
        }
        // if error not is BusinessException.
        if (!(throwable instanceof BusinessException)) { return Result.failure(); }
        // if is BusinessException.
        BusinessException bizException = (BusinessException) throwable;
        CodeDefinition errorCode = bizException.getErrorCode();
        return errorCode != null ? Result.failure(errorCode) : Result.failure();
    }

}
