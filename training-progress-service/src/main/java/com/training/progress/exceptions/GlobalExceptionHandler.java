package com.training.progress.exceptions;

import com.training.common.entity.MsgRespond;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Set;

/**
 * 全局异常拦截
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    // 捕获 MethodArgumentNotValidException 异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public MsgRespond handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn(e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError error : allErrors) {
            errorMsg.append(error.getDefaultMessage()).append(";");
        }
        return MsgRespond.fail(errorMsg.toString());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public MsgRespond handConstraintViolationException(ConstraintViolationException e) {
        logger.warn(e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getMessage()).append(";");
        }
        return MsgRespond.fail(errorMsg.toString());
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class, IllegalArgumentException.class})
    public MsgRespond handleInternalServerError(Exception e, RuntimeException re, IllegalArgumentException ae) {
        logger.error(e);
        logger.error(re);
        logger.error(ae);
        return MsgRespond.fail("内部服务错误，请稍后重试");
    }

    // 捕获400 Bad Request异常
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public MsgRespond handleBadRequestException(Exception e) {
        logger.warn(e);
        return MsgRespond.fail("请求参数错误或类型不匹配");
    }

    // 捕获405 请求方法错误异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public MsgRespond HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn(e);
        return MsgRespond.fail("请求方法错误");
    }

    // 捕获404 Not Found异常
    @ExceptionHandler(NoHandlerFoundException.class)
    public MsgRespond handleNotFoundException(NoHandlerFoundException e) {
        logger.warn(e);
        return MsgRespond.fail("无法找到请求接口");
    }
}
