package com.base.core.domain.exception;

/**
 * Date: 2005-7-14<br>
 * 在使用SERVICE LOCATOR 模式时对更底层的异常的包装
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @since 1.4.2
 */
public class ServiceLocatorException extends RuntimeException {
    private Exception exception;

    /**
     * 使用详细的文字描述创建ServiceLocatorException
     *
     * @param message 详细的描述文字
     */
    public ServiceLocatorException(String message) {
        this(message, null);
    }

    /**
     * 使用详细的文字描述创建ServiceLocatorException,同时该异常包装其它的exception异常
     *
     * @param message   描述文字
     * @param exception 异常对象
     */
    public ServiceLocatorException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }

    /**
     * 创建一个包装其它exception异常的 ServiceLocatorException
     *
     * @param exception 被包装异常
     */
    public ServiceLocatorException(Exception exception) {
        this(null, exception);
    }

    /**
     * 获取被包装的异常
     *
     * @return 返回被包装前的异常对象
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * 获取最先出错的异常
     *
     * @return 最先出错的异常对象
     */
    public Exception getRootCause() {
        if (exception instanceof ServiceLocatorException)
            return ((ServiceLocatorException) exception).getRootCause();
        return exception == null ? this : exception;
    }

    public String toString() {
        if (exception instanceof ServiceLocatorException)
            return exception.toString();
        return exception == null ? super.toString() : exception.toString();
    }
}
