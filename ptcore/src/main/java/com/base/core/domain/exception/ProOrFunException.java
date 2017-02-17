package com.base.core.domain.exception;

/**
 * Date: 2014-10-14<br>
 * 调用存储过程出错<br>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class ProOrFunException extends Exception {
    public ProOrFunException() {
        super();
    }

    public ProOrFunException(String string) {
        super(string);
    }

    public ProOrFunException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public ProOrFunException(Throwable throwable) {
        super(throwable);
    }
}
