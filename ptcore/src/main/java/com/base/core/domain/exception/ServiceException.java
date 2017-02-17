package com.base.core.domain.exception;

/**
 * Date: 2015-7-7<br>
 * 服务层无法归类的异常
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class ServiceException extends Exception {
    public ServiceException() {
        super();
    }

    public ServiceException(String string) {
        super(string);
    }

    public ServiceException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }
}
