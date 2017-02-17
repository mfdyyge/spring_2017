package com.base.core.domain.exception;

/**
 * Date: 2005-9-28<br>
 * 保存数据时出错，包括添加、更新<br>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class SaveException extends Exception {
    public SaveException() {
        super();
    }

    public SaveException(String string) {
        super(string);
    }

    public SaveException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public SaveException(Throwable throwable) {
        super(throwable);
    }
}
