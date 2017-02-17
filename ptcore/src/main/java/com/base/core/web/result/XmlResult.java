package com.base.core.web.result;

import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * <ol>
 * <li>创建文档 2014-09-25</li>
 * <li>使用ajax方式返回xml格式数据</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class XmlResult extends StrutsResultSupport {

    /**
     * Executes the result given a final location (jsp page, action, etc) and the action invocation
     * (the state in which the action was executed). Subclasses must implement this class to handle
     * custom logic for result handling.
     *
     * @param finalLocation the location (jsp page, action, etc) to go to.
     * @param invocation    the execution state of the action.
     * @throws Exception if an error occurs while executing the result.
     */
    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        BaseAction baseAction = (BaseAction) invocation.getAction();
        Map mapHead = baseAction.getMapParam();
        Map mapBody = baseAction.getMapModel();

        mapHead.put("code", baseAction.getCode());
        mapHead.put("msg", baseAction.getMsg());
        mapHead.put("tran_date",JsonHelp.getCurStrDate(1));
        mapHead.put("tran_time",JsonHelp.getCurStrDate(2));

        Map mapRet = new HashMap();
        mapRet.put("head", mapHead);
        mapRet.put("body", mapBody);

        Map mapService = new HashMap();
        mapService.put("service", mapRet);
        JsonHelp.ajaxResponseXml(JsonHelp.mapToXml(mapService));
    }
}
