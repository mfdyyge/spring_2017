
package com.base.core.domain.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Date: 2005-7-20<br>
 * 定义嵌套的非受检查的异常类
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 */
public class NestedRuntimeException extends RuntimeException {
    private static final String CAUSED_BY = "\n出错原因: ";
    private Throwable cause;

    /**
     * @param msg
     * @param cause
     */
    public NestedRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = null;
        this.cause = cause;
    }

    /**
     * @param cause
     */
    public NestedRuntimeException(Throwable cause) {
        this.cause = null;
        this.cause = cause;
    }

    /**
     * @param msg
     */
    public NestedRuntimeException(String msg) {
        super(msg);
        cause = null;
    }

    public NestedRuntimeException() {
        cause = null;
    }

    /**
     * @return java.lang.Throwable
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * @return java.lang.String
     */
    public String toString() {
        if (cause == null) {
            return super.toString();
        } else {
            return super.toString() + "\n出错原因: " + cause.toString();
        }
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.err.println("\n出错原因: ");
        }
    }

    /**
     * @param ps
     */
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (cause != null) {
            ps.println("\n出错原因: ");
            cause.printStackTrace(ps);
        }
    }

    /**
     * @param pw
     */
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (cause != null) {
            pw.println("\n出错原因: ");
            cause.printStackTrace(pw);
        }
    }
}
