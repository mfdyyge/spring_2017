package com.base.core.domain.exception;

/**
 * Date: 2005-8-17<br>
 * 处理Dao层的受检异常,服务层必须做处理
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class DaoException extends Exception {
    public DaoException() {
        super();
    }

    public DaoException(String string) {
        super(string);
    }

    public DaoException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public DaoException(Throwable throwable) {
        super(throwable);
    }

    public String getMessage() {
        if (this.getCause() != null && this.getCause().getCause() != null) {
            return this.getCause().getCause().getMessage();
        } else {
            return super.getMessage();
        }
    }
}
