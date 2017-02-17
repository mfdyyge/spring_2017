package com.base.core.aop;

import com.alibaba.dubbo.rpc.RpcContext;
import com.base.core.bo.BoBase;
import com.base.core.bo.IBo;
import com.base.core.domain.tools.BaseTools;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;
import java.util.Map;

/**
 * <ol>
 * <li>创建文档 Date: 2014-08-26</li>
 * <li>服务提供者：接口服务调用切面,完成身份验证和调用记录</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 1.0
 * @since 1.6
 */
public class AopProviderAspect {
    private Logger logger = Logger.getLogger("YPT");
    private IBo iBo;

    public AopProviderAspect() {

    }


    /**
     * 定义环绕通知：记录接口服务调用
     *
     * @param pjp 接入点对象
     * @return 返回对象
     * @throws Throwable 出错
     */
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        iBo = (BoBase) BaseTools.getInstanceCtx().getBean("boBase");

        long excTime = System.currentTimeMillis();
        Object[] args = pjp.getArgs();

        boolean bApp = false;
        String appDm = "", appId = "", appMc = "", appIp = "";
        Map mapParam = null;
        //从参数列表中获取参数对象
        for (Object obj : args) {
            if (obj instanceof Map) {
                mapParam = (Map) obj;
                bApp = BaseTools.isEmpty(mapParam.get("APP_ID"));
            } else if (obj instanceof List) {
                if (((List) obj).size() > 0) {
                    if ((((List) obj).get(0)) instanceof Map) {
                        mapParam = (Map) ((List) obj).get(0);
                        bApp = BaseTools.isEmpty(mapParam.get("APP_ID"));
                    }
                }
            }
            appId = bApp ? BaseTools.getPropertiesByKey("APP_ID") : BaseTools.getMapByKey(mapParam, "APP_ID");
            appDm = bApp ? BaseTools.getPropertiesByKey("APP_DM") : BaseTools.getMapByKey(mapParam, "APP_DM");
            appMc = bApp ? BaseTools.getPropertiesByKey("APP_MC") : BaseTools.getMapByKey(mapParam, "APP_MC");
            appIp = bApp ? BaseTools.getCurIp() : BaseTools.getMapByKey(mapParam, "APP_IP");

            if (mapParam != null) {
                //当获取不到调用者的APP_ID就使用默认的
                MDC.put("APP_DM", appDm);
                MDC.put("APP_ID", appId);
                MDC.put("APP_MC", appMc);
                MDC.put("APP_IP", appIp);
                break;
            }
        }
        String rpcParam = RpcContext.getContext().getAttachment("YPT_RPC_PARAM");
//        logger.info("--------"+rpcParam);
        Object retVal = pjp.proceed();
        RpcContext.getContext().clearAttachments();

        MDC.put("JK_CLASS", pjp.getTarget().getClass().getName());
        MDC.put("JK_MOTHOD", pjp.getSignature().getName());
        //当前接口服务的监管信息
        MDC.put("SERVICE_APP_ID", BaseTools.getPropertiesByKey("APP_ID"));
        MDC.put("SERVICE_APP_DM", BaseTools.getPropertiesByKey("APP_DM"));
        MDC.put("SERVICE_APP_MC", BaseTools.getPropertiesByKey("APP_MC"));
        MDC.put("SERVICE_APP_IP", BaseTools.getCurIp());

        excTime = System.currentTimeMillis() - excTime;
        MDC.put("PROCESS_TIME", excTime);

        String logMsg = pjp.getTarget().getClass().getName() + "."
                + pjp.getSignature().getName()
                + " process time:" + excTime
                + "\t[APP_ID=" + appId + "\tAPP_DM=" + appDm + "\t APP_MC=" + appMc + "\tAPP_IP=" + MDC.get("APP_IP") + "]"
                + "->{SERVICE_APP_ID=" + MDC.get("SERVICE_APP_ID")
                + "\tSERVICE_APP_DM=" + MDC.get("SERVICE_APP_DM")
                + "\t SERVICE_APP_MC=" + MDC.get("SERVICE_APP_MC")
                + "\t SERVICE_APP_IP=" + MDC.get("SERVICE_APP_IP") + "}";
        logger.info(logMsg);

        MDC.put("LOG_LEVEL", logger.getLevel().toString());
        MDC.put("LOG_MSG", logMsg);
        MDC.put("BZ","正常调用");
        MDC.put("XH", BaseTools.getNextSeq());

        if (BaseTools.getPropertiesByKey("LOG_DB").equals("1")
                && (excTime >= (Integer.parseInt(BaseTools.getPropertiesByKey("LOG_DB_PROC_TIEM")))))
            iBo.save("base.insertT_YFW_PRC_LOG", MDC.getContext());

        MDC.remove("APP_DM");
        MDC.remove("APP_ID");
        MDC.remove("APP_MC");
        MDC.remove("APP_IP");
        MDC.remove("JK_CLASS");
        MDC.remove("JK_MOTHOD");
        MDC.remove("PROCESS_TIME");
        MDC.remove("SERVICE_APP_ID");
        MDC.remove("SERVICE_APP_DM");
        MDC.remove("SERVICE_APP_MC");
        MDC.remove("SERVICE_APP_IP");
        return retVal;
    }

    public void doThrowing(JoinPoint jp, Throwable ex) {
        MDC.put("APP_ID", BaseTools.getPropertiesByKey("APP_ID"));
        MDC.put("APP_DM", BaseTools.getPropertiesByKey("APP_DM"));
        MDC.put("APP_MC", BaseTools.getPropertiesByKey("APP_MC"));

        Object[] args = jp.getArgs();
        String appId, appDm, appMc;
        //从参数列表中获取参数对象
        for (Object obj : args) {//查看参数值
//            System.out.println("-->args:" + obj.toString());
            if (obj instanceof Map) {
                appId = BaseTools.getMapByKey((Map) obj, "APP_ID");
                appDm = BaseTools.getMapByKey((Map) obj, "APP_DM");
                appMc = BaseTools.getMapByKey((Map) obj, "APP_MC");
                MDC.put("APP_ID", appId.length() != 0 ? appId : MDC.get("APP_ID"));
                MDC.put("APP_DM", appDm.length() != 0 ? appDm : MDC.get("APP_DM"));
                MDC.put("APP_MC", appMc.length() != 0 ? appMc : MDC.get("APP_MC"));
                break;
            }
        }

        MDC.put("JK_CLASS", jp.getTarget().getClass().getName());
        MDC.put("JK_MOTHOD", jp.getSignature().getName());

        MDC.put("PROCESS_TIME", "0");

        logger.error(jp.getTarget().getClass().getName() + "."
                + jp.getSignature().getName()
                + " 操作错误:" + ex.getMessage());

        MDC.remove("APP_ID");
        MDC.remove("APP_DM");
        MDC.remove("APP_MC");
        MDC.remove("JK_CLASS");
        MDC.remove("JK_MOTHOD");
        MDC.remove("PROCESS_TIME");

        System.out.println(jp.getTarget().getClass().getName() + "."
                + jp.getSignature().getName() + " throw exception->" + ex.getMessage());

    }
}
