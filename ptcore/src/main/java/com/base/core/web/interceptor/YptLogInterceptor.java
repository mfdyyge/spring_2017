package com.base.core.web.interceptor;

import com.base.core.bo.IBo;
import com.base.core.domain.exception.SaveException;
import com.base.core.domain.tools.BaseTools;
import com.base.core.web.JsonHelp;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * <ol>
 * date:2016-03-21 editor:dingshuangbo
 * <li>创建文档</li>
 * <li>使用拦截器实现云平台记录系统日志</li>
 * <li>取日志参数的顺序是 strtus配置文件>页面请求参数</li>
 * </ol>
 *
 * @author <a href="mailto:2449709309@qq.com">dingshuangbo</a>
 * @version 3.0
 * @since 1.4
 */
public class YptLogInterceptor extends AbstractInterceptor {
    private Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    protected IBo boBase;
    private Map mapModel = new HashMap();

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
     * @param invocation
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        long excTime = System.currentTimeMillis();
        String msg = "执行成功", bLog = "0", ret = null;
        int excCode=1;
        long execTime =0;
        try {
        	Map paramMap =JsonHelp.getReqParamMap();
    		/*多数据源配置 暂时关闭
    		 * String dataSource =MapUtils.getString(paramMap, "DATASOURCE");
    		if (StringUtils.isNotEmpty(dataSource)) {
    			DataSourceHandle.setDataSourceType(dataSource);
    		}*/
            ret = invocation.invoke();
        } catch (Exception e) {
            msg = "执行失败:" + e.getMessage();
            excCode=0;
            logger.error(msg);
        }
        execTime = System.currentTimeMillis() - excTime;
        /*多数据源配置 暂时关闭
         * DataSourceHandle.setDataSourceType(BaseTools.getPropertiesByKey("DEFAULT_DATASCOURCE"));*/
        Map mapCurUser = JsonHelp.getMapByJson(JsonHelp.getCookieVal("CUR_USER"));
        /**
         * 记录系统日志
         */
        if(StringUtils.equals(BaseTools.getPropertiesByKey("LOG_DB"), "1")&&execTime>Long.parseLong(BaseTools.getPropertiesByKey("LOG_DB_PROC_TIEM"))){
        	Map xtMap = new HashMap();
        	xtMap.put("ID", BaseTools.getNextSeq());
        	xtMap.put("USERID", MapUtils.getString(mapCurUser, "SERVICEID", "-1"));
        	xtMap.put("USERNAME", MapUtils.getString(mapCurUser, "SERVICENAME", "未登录"));
        	xtMap.put("EXECUTECLASS", invocation.getProxy().getConfig().getClassName());
        	xtMap.put("EXECUTEMETHOD", invocation.getProxy().getMethod());
        	xtMap.put("EXECUTETIME", execTime);
        	xtMap.put("EXECUTECODE", excCode);
        	xtMap.put("SYSTEMMSG", msg);
            boBase.insert("log.insertSYSTEM_LOG", xtMap);   
        }

        Map mapTem = invocation.getProxy().getConfig().getParams();
        
        
        if (mapTem.get("logTpl") != null) {
            mapTem = JsonHelp.getMapByJson((String) mapTem.get("logTpl"));
        }
        
        mapModel = JsonHelp.getReqParamMap();
        mapTem.putAll(mapModel);
        
       
        try {
            if (MapUtils.getString(mapTem, "bLog", "0").equals("1")) {
                Map mapLog = new HashMap();
                mapLog.put("ID", BaseTools.getNextSeq());
                mapLog.put("USERID", MapUtils.getString(mapCurUser, "SERVICEID", "-1"));
                mapLog.put("USERNAME", MapUtils.getString(mapCurUser, "SERVICENAME", "未登录"));
                mapLog.put("IP", JsonHelp.getRemoteIp());
                mapLog.put("EXECUTECLASS", invocation.getProxy().getConfig().getClassName());
                mapLog.put("EXECUTEMETHOD", invocation.getProxy().getMethod());
                mapLog.put("EXECUTETIME", execTime);
                mapLog.put("EXECUTECODE", excCode);
                mapLog.put("BUSINESSMSG", MapUtils.getString(mapTem, "LOGMSG", "")+","+msg);
                mapLog.put("USERMSG", MapUtils.getString(mapTem, "BUSINESS_DETAIL_MSG", ""));
                boBase.insert("log.insertBUSINESS_LOG", mapLog);
            }
        } catch (SaveException e) {
            logger.error(e.getMessage());
        }
        //执行目标方法 (调用下一个拦截器, 或执行Action)
        return ret;
    }


    /**
     * Does nothing
     */
    @Override
    public void destroy() {
        super.destroy();
    }
}
