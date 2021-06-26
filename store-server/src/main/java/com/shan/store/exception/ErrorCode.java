package com.shan.store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {

    //公共错误状态码，common
    SUCCESS(0, "Ok"),
    COMMON_PARAM_VALID_ERROR(1001, "Illegal params"),
    COMMON_SERVER_ERROR(1000, "Server Error");

    private int code;

    private String message;

}
