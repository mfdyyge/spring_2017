package com.base.core.domain.exception;

/**
 * Date: 2005-9-19<br>
 * 删除操作异常
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class DelErrorException extends Exception {
    public DelErrorException() {
        super();
    }

    public DelErrorException(String string) {
        super(string);
    }

    public DelErrorException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public DelErrorException(Throwable throwable) {
        super(throwable);
    }
}
