package com.shan.store.dto;

import com.shan.store.exception.ErrorCode;
import com.shan.store.exception.StoreException;
import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class ApiResult {

    private int code;
    private Object data;
    private String message;

    private ApiResult(int statusCode, Object data, String message) {
        this.code = statusCode;
        this.data = data;
        this.message = message;
    }

    public static ApiResult success(Object data) {
        return new ApiResult(ErrorCode.SUCCESS.getCode(), data, ErrorCode.SUCCESS.getMessage());
    }

    public static ApiResult success() {
        return new ApiResult(ErrorCode.SUCCESS.getCode(), null, ErrorCode.SUCCESS.getMessage());
    }

    public static ApiResult exception(StoreException exception) {
        return new ApiResult(exception.getCode(), null, exception.getMessage());
    }

    public static ApiResult build(ErrorCode errorCode, String errMsg) {
        return new ApiResult(errorCode.getCode(), null, errMsg);
    }

}
