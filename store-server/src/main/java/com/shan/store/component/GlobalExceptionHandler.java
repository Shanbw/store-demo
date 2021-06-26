package com.shan.store.component;

import com.shan.store.dto.ApiResult;
import com.shan.store.exception.ErrorCode;
import com.shan.store.exception.StoreException;
import com.shan.store.exception.UnknownServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常捕获器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public ApiResult illegalArgumentHandler(Exception e) {
        return ApiResult.build(ErrorCode.COMMON_PARAM_VALID_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ApiResult exceptionHandler(HttpServletResponse response, Throwable t) {
        LOGGER.error("Throwable:", t);
        if (t instanceof StoreException) {
            if (((StoreException) t).getCode() == ErrorCode.COMMON_SERVER_ERROR.getCode()) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return ApiResult.exception((StoreException) t);
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResult.exception(new UnknownServerException(ErrorCode.COMMON_SERVER_ERROR));
        }
    }

}
