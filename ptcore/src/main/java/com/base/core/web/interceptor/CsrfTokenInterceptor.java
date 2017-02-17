package com.base.core.web.interceptor;

import com.base.core.web.CsrfToken;
import com.base.core.web.JsonHelp;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;

/**
 * <ol>
 * <li>创建文档 2014-12-19</li>
 * <li>防止CSRF攻击(Cross Site Request Forgery)的拦截器</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class CsrfTokenInterceptor extends AbstractInterceptor {
    /**
     * Does nothing
     */
    @Override
    public void init() {
        super.init();
    }

    /**
     * Override to handle interception
     *
     * @param actionInvocation
     */
    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (StringUtils.equals(JsonHelp.getPropertiesByKey("CSRF_TOKEN_CHECK"), "1")) {
            if (!actionInvocation.getProxy().getMethod().equals("checkLogin")) {
                //获取http请求头部标示
                String csrfTokenReq = JsonHelp.getRequest().getHeader(CsrfToken.getKey());
                System.out.println(CsrfToken.getKey() + "=" + csrfTokenReq);
                if (StringUtils.isBlank(csrfTokenReq)) {
                    //如果不符则终止请求
                    JsonHelp.ajaxResponseTxt(-3, "终止不安全的请求,请重新登录!");
                    return null;
                } else if (csrfTokenReq.equals(CsrfToken.getCsrfTokenInSession())) {
                    // 如果符合，则清除session中相应的token，以防止再次使用它
                    CsrfToken.removeCsrfTokenInSession();
                } else {
                    //如果不符则终止请求
                    JsonHelp.ajaxResponseTxt(-3, "请求" + CsrfToken.getKey() + "失效,请重新登录!");
                    return null;
                }
            }
        }
        //执行目标方法 (调用下一个拦截器, 或执行Action)
        return actionInvocation.invoke();
    }
}
