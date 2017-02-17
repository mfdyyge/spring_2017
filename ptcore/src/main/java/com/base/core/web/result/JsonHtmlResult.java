package com.base.core.web.result;

import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * date:11-11-13 editor:yanghongjian
 * <li>创建文档</li>
 * <li>使用ajax方式返回JSP生成后的html页面</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class JsonHtmlResult extends StrutsResultSupport {
    private String location;

    public JsonHtmlResult() {
        super();
    }

    public JsonHtmlResult(String location) {
        super(location);
    }

    public JsonHtmlResult(String location, boolean parse, boolean encode) {
        super(location, parse, encode);
    }

    protected void doExecute(String s, ActionInvocation actionInvocation) throws Exception {
        this.location = s;
        this.htmlResult(actionInvocation);
    }

    public void htmlResult(ActionInvocation invocation) {
        BaseAction baseAction = (BaseAction) invocation.getAction();

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        //解决返回html页面插件中文乱码问题
        response.setContentType("application/json;charset=" + request.getCharacterEncoding());

        StrutsRequestWrapper requestWrapper = new StrutsRequestWrapper(request);

        String viewName = request.getParameter("viewName") == null ?
                this.location : request.getParameter("viewName");
        String[] viewNames = viewName.split(";");
        List resultList = new ArrayList();
        Map mapResult = new HashMap();
        try {
            for (int i = 0; i < viewNames.length; i++) {
                String vName = viewNames[i];
                RequestDispatcher dispatcher = request.getRequestDispatcher(vName);
                ResponseWrapper responseWrapper = new ResponseWrapper(response);
                dispatcher.include(requestWrapper, responseWrapper);
                resultList.add(responseWrapper.getContent().trim());
                responseWrapper.finalize();
                mapResult.put("data", resultList);
            }
        } catch (Exception e) {
            baseAction.setCode(-1);
            baseAction.setMsg(e.getMessage());
            mapResult.put("data", resultList);
        }
        JsonHelp.ajaxResponseTxt(baseAction.getCode(), baseAction.getMsg(), mapResult);
    }

}