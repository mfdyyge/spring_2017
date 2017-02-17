package com.base.core.web.result;

import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StrutsResultSupport;

/**
 * <ol>
 * date:11-11-14 editor:yanghongjian
 * <li>创建文档</li>
 * <li>使用ajax方式返回Json格式数据</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class JsonResult extends StrutsResultSupport {

    protected void doExecute(String s, ActionInvocation actionInvocation) throws Exception {
//        long begin = System.currentTimeMillis();
        BaseAction baseAction = (BaseAction) actionInvocation.getAction();
        JsonHelp.ajaxResponseTxt(baseAction.getCode(), baseAction.getMsg(), baseAction.getMapModel());

//        long end = System.currentTimeMillis();
//        System.out.println("--------------------------------\n>>" + baseAction.getClass().getName()
//                + "执行时间\najaxResponse:" + (end - begin) + "ms");
    }
}
