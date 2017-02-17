package com.base.core.web.actions;


import com.base.core.domain.tools.BaseTools;
import com.base.core.web.JsonHelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <ol>
 * date:11-8-18 editor:yanghongjian
 * <li>创建文档</li>
 * <li>展示层Action基础类</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class BaseAction {
    //返回dispatcher类型
    public static String SUCCESS = "success";
    public static String ERROR = "error";

    protected String strJson = "";
    protected int code = 0;
    protected String msg = "操作成功";

    protected Map mapForm = new HashMap();
    protected List list = new ArrayList();
    protected Map mapParam = new HashMap();

    public Map getMapForm() {
        return mapForm;
    }

    public void setMapForm(Map mapForm) {
        this.mapForm = mapForm;
    }

    public Map getMapParam() {
        return mapParam;
    }

    public void setMapParam(Map mapParam) {
        this.mapParam = mapParam;
    }

    //放置数据
    protected Map mapModel = new HashMap();

    public Map getMapModel() {
        return mapModel;
    }

    public void setMapModel(Map mapModel) {
        this.mapModel = mapModel;
    }

    public BaseAction() {
        //获取spring的上下文对象
        BaseTools.getInstanceCtx();
    }

    /**
     * 从表单中获取返回视图类型(RESULT_TYPE)
     *
     * @return 默认返回 success
     */
    protected String getResult() {
        String result = JsonHelp.getReqParamVal("RESULT_TYPE");
        return result.length() == 0 ? SUCCESS : result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String getStrJson() {
        return strJson;
    }

    public void setStrJson(String strJson) {
        this.strJson = strJson;
    }

}
