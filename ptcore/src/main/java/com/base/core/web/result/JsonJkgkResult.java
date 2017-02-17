package com.base.core.web.result;

import com.alibaba.fastjson.JSON;
import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * <ol>
 * date:2014-9-25 editor:yanghongjian
 * <li>创建文档</li>
 * <li>接口服务管控专用的使用ajax方式返回Json格式数据</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class JsonJkgkResult extends StrutsResultSupport {

    protected void doExecute(String s, ActionInvocation actionInvocation) throws Exception {
//        long begin = System.currentTimeMillis();
        BaseAction baseAction = (BaseAction) actionInvocation.getAction();
        Map mapHead = baseAction.getMapParam();
        Map mapBody = baseAction.getMapModel();

        mapHead.put("code", baseAction.getCode());
        mapHead.put("msg", baseAction.getMsg());
        mapHead.put("tran_date", JsonHelp.getCurStrDate(2));
        mapHead.put("tran_time", JsonHelp.getCurStrDate(3));

        Map mapRet = new HashMap();
        mapRet.put("head", mapHead);
        mapRet.put("body", mapBody);

        Map mapService = new HashMap();
        mapService.put("service", mapRet);
        JsonHelp.ajaxResponseXml(JSON.toJSONString(mapService));

//        long end = System.currentTimeMillis();
//        System.out.println("--------------------------------\n>>" + baseAction.getClass().getName()
//                + "执行时间\najaxResponse:" + (end - begin) + "ms");
    }
}
