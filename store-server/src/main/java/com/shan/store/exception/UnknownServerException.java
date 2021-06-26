package com.shan.store.exception;

import java.io.Serializable;

/**
 * 服务未知异常
 */
public class UnknownServerException extends StoreException implements Serializable {

    public UnknownServerException() {
        super();
    }

    public UnknownServerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnknownServerException(ErrorCode errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
