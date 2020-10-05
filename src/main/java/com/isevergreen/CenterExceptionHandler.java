package com.isevergreen;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理中心
 *
 * @author Jiang
 */
@RestControllerAdvice
public class CenterExceptionHandler {
    /**
     * 所有的自行抛出的异常
     */
    @ExceptionHandler(Exception.class)
    String exception(Exception e) {
        return e.getMessage();
    }

    /**
     * 所有的自行抛出的异常
     */
    @ExceptionHandler(RuntimeException.class)
    String runtimeException(RuntimeException e) {
        return e.getMessage();
    }
}
