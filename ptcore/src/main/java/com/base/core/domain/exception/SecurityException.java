package com.base.core.domain.exception;

/**
 * <ol>
 * <li>创建文档 Date: 2014-08-27</li>
 * <li>访问安全原则控制异常</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 1.0
 * @since 1.6
 */
public class SecurityException extends Exception {
    public SecurityException() {
        super();
    }


    public SecurityException(String message) {
        super(message);
    }


    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }


    public SecurityException(Throwable cause) {
        super(cause);
    }

    public String getMessage() {
        if (this.getCause() != null && this.getCause().getCause() != null) {
            return this.getCause().getCause().getMessage();
        } else {
            return super.getMessage();
        }
    }
}
