package com.base.core.domain.exception;

/**
 * Date:2010-4-6<br>
 * Session失效异常
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class SessionInvalidationException extends Exception {
    public SessionInvalidationException() {
        super();
    }

    public SessionInvalidationException(String s) {
        super(s);
    }

    public SessionInvalidationException(Throwable throwable) {
        super(throwable);
    }

    public SessionInvalidationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
