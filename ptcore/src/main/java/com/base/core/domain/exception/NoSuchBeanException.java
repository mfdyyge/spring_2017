package com.base.core.domain.exception;

/**
 * 日期:2011-1-17<br/>
 * 描述:Bean没有定义异常<br/>
 * <p/>
 * 历史记录:
 * <ol>
 * 日期:2011-1-17 作者:yanghongjian
 * <li>创建功能代码</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class NoSuchBeanException extends RuntimeException {
    public NoSuchBeanException() {
    }

    public NoSuchBeanException(String message) {
        super(message);
    }

    public NoSuchBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBeanException(Throwable cause) {
        super(cause);
    }
}
