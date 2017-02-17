package com.base.core.web.result;

import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StrutsResultSupport;

/**
 * <ol>
 * date:11-11-14 editor:yanghongjian
 * <li>创建文档</li>
 * <li></li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class JsonNoCodeResult extends StrutsResultSupport {
    protected void doExecute(String s, ActionInvocation actionInvocation) throws Exception {
//        HttpServletResponse response = ServletActionContext.getResponse();
//        response.setHeader("Charset", "UTF-8");
//        //返回的是txt文本文件
//        response.setContentType("text/json;charset=UTF-8");
//
//        PrintWriter output = response.getWriter();
//        JSONObject js = JSONObject.fromObject(((BaseAction) actionInvocation.getAction()).getStrJson());
//        js.write(output);
//        log.zixun(this.getClass() + "->" + js.toString());
        JsonHelp.ajaxResponse(((BaseAction) actionInvocation.getAction()).getStrJson());
    }
}
