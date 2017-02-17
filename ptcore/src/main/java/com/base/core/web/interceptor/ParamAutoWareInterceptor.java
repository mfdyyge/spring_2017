package com.base.core.web.interceptor;

import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;


/**
 * <ol>
 * date:11-8-18 editor:yanghongjian
 * <li>创建文档</li>
 * <li>自动封装表单提交的值</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class ParamAutoWareInterceptor extends AbstractInterceptor {
    public ParamAutoWareInterceptor() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Object oAction = actionInvocation.getProxy().getAction();
        if (oAction instanceof BaseAction) {
            BaseAction action = (BaseAction) oAction;
            action.setMapForm(JsonHelp.getReqParamMap());
        }
        //执行目标方法 (调用下一个拦截器, 或执行Action)
        return actionInvocation.invoke();
    }
}
