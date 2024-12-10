package org.swu.api;

import org.swu.exception.IpAccessTooFrequentException;
import org.swu.exception.InvalidFileException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IpAccessTooFrequentException.class)
    public String handleIpAccessTooFrequentException(IpAccessTooFrequentException ex) {
        return "错误：" + ex.getMessage();
    }

    @ExceptionHandler(InvalidFileException.class)
    public String handleInvalidFileException(InvalidFileException ex) {
        return "错误：" + ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex) {
        return "服务器发生未知错误，请联系管理员：" + ex.getMessage();
    }
}

