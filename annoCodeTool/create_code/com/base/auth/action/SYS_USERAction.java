package com.base.auth.action;

import com.base.core.domain.exception.DelErrorException;
import com.base.core.domain.exception.SaveException;
import com.base.core.domain.tools.EncryptTools;
import com.base.core.web.JsonHelp;
import com.base.core.web.actions.BaseAction;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONPath;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import java.util.HashMap;
import java.util.Map;

import com.base.auth.service.IServiceSYS_USER;

/**
 * <ol>
 * date:2016-12-20 editor:dingshuangbo
 * <li>创建文档</li>
 * <li>用户表数据采集显示及请求流程处理类</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:2449709309@qq.com">dingshuangbo</a>
 * @version 2.0
 * @since 1.6
 */
@Controller
@Namespace(value="/auth")
@ParentPackage("struts-baseCfn")
public class SYS_USERAction extends BaseAction{
    private Logger log = Logger.getLogger(this.getClass());
    @Autowired
    IServiceSYS_USER serviceSYS_USER;

	public SYS_USERAction(){
	}

    /**
     * 查询用户表记录
     */
    @Action(value="selectSYS_USER", params={"bLog","1","LOGMSG","查询用户表(SYS_USER)记录"}, results={@Result(name="success" , type="jsonResult")})
    public String selectSYS_USER() {
        mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();

        try {
            list = serviceSYS_USER.selectSYS_USER(mapForm);
 			mapModel.put("Rows", list);
            mapModel.put("Total", JSONPath.contains(list, "$[0].TOTAL") ? JSONPath.eval(list, "$[0].TOTAL") : "0");
            mapModel.put("mapParam", mapForm);
        } catch (Exception e) {
            code =-1;
            msg = e.getMessage();
            log.error(msg);
        }
        return getResult();
    }
    
    /**
     * 查询用户表记录数
     */
    @Action(value="selectSYS_USERCount", params={"bLog","1","LOGMSG","查询用户表(SYS_USER)记录数"}, results={@Result(name="success" , type="jsonResult")})
    public String selectSYS_USERCount() {
        mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();

        try {
            mapModel.put("mapParam", mapForm);
            mapModel.put("data",serviceSYS_USER.selectSYS_USERCount(mapForm));
        } catch (Exception e) {
            code =-1;
            msg = e.getMessage();
            log.error(msg);
        }
        return getResult();
    }

    /**
     * 新增用户表记录
     */
    @Action(value="insertSYS_USER", params={"bLog","1","LOGMSG","新增用户表(SYS_USER)记录"}, results={@Result(name="success" , type="jsonResult")})
    public String insertSYS_USER() {
    	mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();

        try {
            code = (int)serviceSYS_USER.insertSYS_USER(mapForm);
        }catch (Exception e) {
            code = -1;
            msg = e.getMessage();
            log.error(msg);
        }

        //重新设置查询条件
        mapForm.clear();
        return getResult();
    }

    /**
     * 修改用户表记录
     * @return 导航到
     */
    @Action(value="updateSYS_USER", params={"bLog","1","LOGMSG","更新用户表(SYS_USER)记录"}, results={@Result(name="success" , type="jsonResult")})
    public String updateSYS_USER() {
    	mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();
        try {
            code = serviceSYS_USER.updateSYS_USER(mapForm);
        }catch (Exception e) {
            code = -1;
            msg = e.getMessage();
            log.error(msg);
        }

        //重新设置查询条件
        mapForm.clear();
        return getResult();
    }

    /**
     * 删除用户表记录
     */
    @Action(value="deleteSYS_USER", params={"bLog","1","LOGMSG","删除用户表(SYS_USER)记录"}, results={@Result(name="success" , type="jsonResult")})
    public String deleteSYS_USER() {
        String[] ids = JsonHelp.getReqParamVal("IDS").split(",", -1);

        list.clear();
        for (int i = 0; i < ids.length; i++) {
            Map mapTem = new HashMap();
            mapTem.put("ID",ids[i]);
            list.add(mapTem);
        }
        try {
            code = serviceSYS_USER.deleteSYS_USER(list);
            code = code > 0 ? 2 : -1;
        }catch (Exception e) {
            code = -1;
            msg = e.getMessage();
            log.error(msg);
        }
        list.clear();
        return getResult();
    }
}
