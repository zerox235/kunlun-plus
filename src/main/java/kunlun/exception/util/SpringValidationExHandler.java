package kunlun.exception.util;

import cn.hutool.core.collection.CollUtil;
import kunlun.common.Errors;
import kunlun.common.Result;
import kunlun.core.function.Function;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

public class SpringValidationExHandler implements Function<Throwable, Result<Object>> {

    protected Result<Object> handleBindingResult(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return Result.failure(Errors.badRequest);
        }
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        boolean isFirst = true;
        StringBuilder builder = new StringBuilder();
        for (ObjectError error : allErrors) {
            if (isFirst) { isFirst = false; }
            else { builder.append("，"); }
            builder.append(error.getDefaultMessage());
        }
        return Result.failure(builder.toString());
    }

    @Override
    public Result<Object> apply(Throwable throwable) {
        if (throwable instanceof BindException) {
            BindException ex = (BindException) throwable;
            return handleBindingResult(ex.getBindingResult());
        }
        else if (throwable instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) throwable;
            return handleBindingResult(ex.getBindingResult());
        }
        else if (throwable instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) throwable;
            Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
            if (CollUtil.isEmpty(constraintViolations)) {
                return Result.failure(Errors.badRequest);
            }
            boolean isFirst = true;
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<?> violation : constraintViolations) {
                if (isFirst) { isFirst = false; }
                else { builder.append("，"); }
                builder.append(violation.getMessage());
            }
            return Result.failure(builder.toString());
        } else {
            throw new Error("Only the specified exception processing is supported! ");
        }
    }
}
