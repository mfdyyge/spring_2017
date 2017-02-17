package com.base.core.domain.tools;

/**
 * <ol>
 * date:13-12-11 editor:YangHongJian
 * <li>创建文档<li>
 * <li>回调类，用来处理http请求完毕后的逻辑<li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 3.0
 * @see HttpHelp
 * @since 1.6
 */
public interface HttpCallback {
    /**
     * http请求返回后调用该接口
     *
     * @param response http请求返回值
     */
    void execute(String response);
}
