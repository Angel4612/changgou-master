package com.changgou.exception;

import lombok.Data;

import java.rmi.MarshalException;

@Data
public class ChangGouException extends RuntimeException {
    private Integer errorCode;
    private String errorMsg;

    public ChangGouException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }


}
