package com.base.core.domain.exception;


/**
 * Date: 2005-7-20<br>
 * 定义界面层使用的非受检查的异常类,该类将被在出错界面进行处理
 * <p/>
 * 定义成RuntimeException,是为了在struts的action中顺利捕获到。
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 */
public class BeanActionException extends NestedRuntimeException {

    /**
     * @param s         出错信息
     * @param throwable 异常
     */
    public BeanActionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * @param throwable 异常
     */
    public BeanActionException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param s 出错信息
     */
    public BeanActionException(String s) {
        super(s);
    }

    public BeanActionException() {
        super();
    }
}
