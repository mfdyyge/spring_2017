package com.base.core.web.interceptor;

import com.base.core.web.JsonHelp;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.collections.MapUtils;

/**
 * <ol>
 * date:11-11-15 editor:yanghongjian
 * <li>创建文档</li>
 * <li>校验合法操作的拦截器</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class SessionInterceptor extends AbstractInterceptor {
    public SessionInterceptor() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        String method = actionInvocation.getProxy().getMethod();

        String curUserId = JsonHelp.getReqParamVal("CUR_USERID");
        if ((!(method.equals("checkLogin") || method.equals("loginout")))
                && (!(curUserId.equals("-1") || curUserId.equals("-2")))
                && (MapUtils.isEmpty(JsonHelp.getMapByJson(JsonHelp.getCookieVal("CUR_USER"))))) {
            JsonHelp.ajaxResponseTxt(-3, "COOKIE失效,请重新登录!");
            return null;
        } else
            //执行目标方法 (调用下一个拦截器, 或执行Action)
            return actionInvocation.invoke();
    }

    public String interceptOld(ActionInvocation actionInvocation) throws Exception {
        String method = actionInvocation.getProxy().getMethod();

        String curUserId = JsonHelp.getReqParamVal("CUR_USERID");
        if ((!(method.equals("checkLogin") || method.equals("loginout")))
                && (!(curUserId.equals("-1") || curUserId.equals("-2")))
                && (null == JsonHelp.getSession().get("CUR_USER"))) {
            JsonHelp.ajaxResponseTxt(-3, "COOKIE失效,请重新登录!");
            return null;
        } else
            //执行目标方法 (调用下一个拦截器, 或执行Action)
            return actionInvocation.invoke();
    }
}
