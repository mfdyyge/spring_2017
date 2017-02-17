package com.base.core.aop;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSONObject;
import com.base.core.domain.tools.BaseTools;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <ol>
 * <li>创建文档 Date: 2014-08-27</li>
 * <li>服务消费者切面：调用服务接口前添加安全参数</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 1.0
 * @since 1.6
 */
public class AopConsumerAspect {
    //    private Logger logger = Logger.getLogger(this.getClass());
    private Logger logger = Logger.getLogger("YPT_RPC");

    /**
     * 定义环绕通知：消费者调用接口服务时添加安全参数
     *
     * @param pjp 接入点对象
     * @return 返回对象
     * @throws Throwable 出错
     */
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long excTime = System.currentTimeMillis();

        int iArg = 0;
        Object[] args = pjp.getArgs();
        Map mapArgs = null;
        //从参数列表中获取参数对象
        for (Object obj : args) {//查看参数值
            if (obj instanceof Map) {
                mapArgs = (Map) obj;
                if (BaseTools.isEmpty(mapArgs.get("APP_ID"))) {
                    mapArgs.put("APP_DM", BaseTools.getPropertiesByKey("APP_DM"));
                    mapArgs.put("APP_ID", BaseTools.getPropertiesByKey("APP_ID"));
                    mapArgs.put("APP_MC", BaseTools.getPropertiesByKey("APP_MC"));
                }
                mapArgs.put("APP_IP", BaseTools.getCurIp());
                args[iArg] = mapArgs;
                break;
            } else if (obj instanceof List) {
                List list = (List) obj;
                if (list.size() > 0) {
                    if (list.get(0) instanceof Map) {
                        mapArgs = (Map) list.get(0);
                        if (BaseTools.isEmpty(mapArgs.get("APP_ID"))) {
                            mapArgs.put("APP_DM", BaseTools.getPropertiesByKey("APP_DM"));
                            mapArgs.put("APP_ID", BaseTools.getPropertiesByKey("APP_ID"));
                            mapArgs.put("APP_MC", BaseTools.getPropertiesByKey("APP_MC"));
                        }
                        mapArgs.put("APP_IP", BaseTools.getCurIp());
                        //取代原值
                        list.set(0, mapArgs);
                        args[iArg] = list;
                        break;
                    }
                }
            }
            ++iArg;
        }
        //隐式传参，后面的远程调用都会隐式将这些参数发送到服务器端，类似cookie，用于框架集成，不建议常规业务使用
        Map mapTem = new HashMap();
        mapTem.put("APP_DM", BaseTools.getPropertiesByKey("APP_DM"));
        mapTem.put("APP_ID", BaseTools.getPropertiesByKey("APP_ID"));
        mapTem.put("APP_MC", BaseTools.getPropertiesByKey("APP_MC"));
        RpcContext.getContext().setAttachment("YPT_RPC_PARAM", JSONObject.toJSONString(mapTem));
//        logger.info(">>>>>YPT_RPC_PARAM="+RpcContext.getContext().getAttachment("GK_PARAM"));

        Object retVal = pjp.proceed(args);//注意：每发起RPC调用，上下文状态会变化
        boolean isConsumerSide = RpcContext.getContext().isConsumerSide(); // 本端是否为消费端，这里会返回true
        String serverIP = RpcContext.getContext().getRemoteHost(); // 获取最后一次调用的提供方IP地址
        String application = RpcContext.getContext().getUrl().getParameter("application"); // 获取当前服务配置信息，所有配置信息都将转换为URL的参数
        logger.info(isConsumerSide+"\t"+serverIP+"\t"+application);

        excTime = System.currentTimeMillis() - excTime;
        logger.info(pjp.getTarget().getClass().getName() + "."
                + pjp.getSignature().getName()
                + " process time:" + excTime
                + "\t[APP_ID=" + mapArgs.get("APP_ID") + "\tAPP_DM=" + mapArgs.get("APP_DM")
                + "\tAPP_MC=" + mapArgs.get("APP_MC") + "\tAPP_IP=" + mapArgs.get("APP_IP") + "]");
        return retVal;
    }
}
