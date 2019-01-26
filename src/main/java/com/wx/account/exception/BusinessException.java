package com.wx.account.exception;

import com.wx.account.config.error.ErrorCodeProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by supermrl on 2019/1/26.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private String errorCode;

    private String errorMessage;

    private Object[] params;


    public BusinessException(final String errorCode) {
        this(errorCode, ErrorCodeProperties.init().getErrorMessage(errorCode), null);
    }

    public BusinessException(final String errorCode, final Object[] params) {
        this(errorCode, ErrorCodeProperties.init().getErrorMessages(errorCode, params) ,null);
    }

    public BusinessException(final String errorCode, final String errorMessage) {
        this(errorCode, errorMessage, null);
    }

    public BusinessException(final String errorCode, final String errorMessage, final Object[] params) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.params = params;
    }

//    public static Supplier<BusinessException> of(final String errorCode) {
//        return () -> new BusinessException(errorCode);
//    }
//
//    public static Supplier<BusinessException> of(final String errorCode, final String errorMessage) {
//        return () -> new BusinessException(errorCode, errorMessage);
//    }
//
//    public static Supplier<BusinessException> of(final String errorCode, final String errorMessage, final Object[] params) {
//        return () -> new BusinessException(errorCode, errorMessage, params);
//    }
//
//    public String getErrorCode() {
//        return this.errorCode;
//    }
//
//    public String getErrorMessage() {
//        return this.errorMessage;
//    }
//
//    public Object[] getParams() {
//        return this.params;
//    }

}
