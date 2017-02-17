package ${mapProp.actionPath};

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

import ${mapProp.servicePath}.IService${sqlName};

/**
 * <ol>
 * date:${date} editor:${mapProp.editor}
 * <li>创建文档</li>
 * <li>${tabelComments}数据采集显示及请求流程处理类</li>
 * </ol>
 * <ol>
 *
 * @author <a href="${mapProp.author}">${mapProp.editor}</a>
 * @version 2.0
 * @since 1.6
 */
@Controller
@Namespace(value="${mapProp.strutsNamespace}")
@ParentPackage("struts-baseCfn")
public class ${sqlName}Action extends BaseAction{
    private Logger log = Logger.getLogger(this.getClass());
    @Autowired
    IService${sqlName} service${sqlName};

	public ${sqlName}Action(){
	}

    /**
     * 查询${tabelComments}记录
     */
    @Action(value="select${sqlName}", params={"bLog","1","LOGMSG","查询${tabelComments}(${sqlName})记录"}, results={@Result(name="success" , type="jsonResult")})
    public String select${sqlName}() {
        mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();

        try {
            list = service${sqlName}.select${sqlName}(mapForm);
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
     * 查询${tabelComments}记录数
     */
    @Action(value="select${sqlName}Count", params={"bLog","1","LOGMSG","查询${tabelComments}(${sqlName})记录数"}, results={@Result(name="success" , type="jsonResult")})
    public String select${sqlName}Count() {
        mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();

        try {
            mapModel.put("mapParam", mapForm);
            mapModel.put("data",service${sqlName}.select${sqlName}Count(mapForm));
        } catch (Exception e) {
            code =-1;
            msg = e.getMessage();
            log.error(msg);
        }
        return getResult();
    }

    /**
     * 新增${tabelComments}记录
     */
    @Action(value="insert${sqlName}", params={"bLog","1","LOGMSG","新增${tabelComments}(${sqlName})记录"}, results={@Result(name="success" , type="jsonResult")})
    public String insert${sqlName}() {
    	mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();

        try {
            code = (int)service${sqlName}.insert${sqlName}(mapForm);
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
     * 修改${tabelComments}记录
     * @return 导航到
     */
    @Action(value="update${sqlName}", params={"bLog","1","LOGMSG","更新${tabelComments}(${sqlName})记录"}, results={@Result(name="success" , type="jsonResult")})
    public String update${sqlName}() {
    	mapForm.clear();
        mapForm = JsonHelp.getReqParamMap();
        try {
            code = service${sqlName}.update${sqlName}(mapForm);
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
     * 删除${tabelComments}记录
     */
    @Action(value="delete${sqlName}", params={"bLog","1","LOGMSG","删除${tabelComments}(${sqlName})记录"}, results={@Result(name="success" , type="jsonResult")})
    public String delete${sqlName}() {
        String[] ids = JsonHelp.getReqParamVal("IDS").split(",", -1);

        list.clear();
        for (int i = 0; i < ids.length; i++) {
            Map mapTem = new HashMap();
            <#list mapPriKey?keys as col>
            mapTem.put("${col}",ids[i]);
            </#list>
            list.add(mapTem);
        }
        try {
            code = service${sqlName}.delete${sqlName}(list);
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
