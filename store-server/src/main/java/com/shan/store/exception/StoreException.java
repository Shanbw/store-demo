package com.shan.store.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class StoreException extends RuntimeException implements Serializable {

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    public StoreException(){

    }

    public StoreException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public StoreException(ErrorCode errorCode, String errorMsg) {
        this.code = errorCode.getCode();
        this.message = errorMsg;
    }

}
