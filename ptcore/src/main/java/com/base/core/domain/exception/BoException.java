package com.base.core.domain.exception;

/**
 * Date: 2015-7-7<br>
 * 业务处理层无法归类的异常
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class BoException extends Exception {
    public BoException() {
        super();
    }

    public BoException(String string) {
        super(string);
    }

    public BoException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public BoException(Throwable throwable) {
        super(throwable);
    }
}
