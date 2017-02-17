package com.base.core.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.base.core.domain.tools.BaseTools;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * <ol>
 * date:11-3-10 editor:yanghongjian
 * <li>创建文档</li>
 * <li>对象或者集合封装成JSON格式及输出辅助类</li>
 * <li>从request中获取表单数据</li>
 * <li>2015.9.30 yanghongjian 添加生成UUID的方法，解决SOA服务数据唯一性问题</li>
 * <li>2015.11.25 yanghongjian 添加更强大的cookie操作方法</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:yanghongjian@htjs.net">YangHongJian</a>
 * @version 2.0
 * @since 1.4.2
 */
public class JsonHelp extends BaseTools {
    private static Logger log = Logger.getLogger(JsonHelp.class);
    public static ApplicationContext ctx = null;

    public JsonHelp() {
        showInfo();
    }

    static {
        showInfo();
    }

    /**
     * 显示系统一些版权信息
     */
    public static void showInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\t\t\t基础系统架构 Ver2.0 Date:2012.3\n")
                .append("\t--------------------------------------------------\n")
                .append("\t\t部署环境:\n")
                .append("\t\t\t中文环境(unix/liunx)设置 export lang=zh_CN\n")
                .append("\t\t\tJDK1.4.x servlet2.4 jsp2.0\n")
                .append("\t\t系统架构:\n")
                .append("\t\t\tJavaEncode:").append(getPropertiesByKey("JAVA_CHARSET"))
                .append("\n\t\t\tweb层:struts2 Spring2.5.6 jQuery1.4 pageEncode:UTF-8")
                .append("\n\t\t\tCSRF-TOKEN-CHECK:" + (StringUtils.equals(getPropertiesByKey("CSRF_TOKEN_CHECK"), "1") ? "终止CSRF攻击" : "未检查CSRF攻击"))
                .append("\n\n\t\t\t\t\t\t*").append(getPropertiesByKey("APP_TEAM"))
                .append("*\n\t\t\t\t").append(getPropertiesByKey("APP_GROUP"))
                .append("\n\t--------------------------------------------------\n");
        log.info(sb.toString());
    }

    public static Map getSession() {
//        return (HttpSession) ActionContext.getContext().get(ServletActionContext.SESSION);
        return ActionContext.getContext().getSession();
    }

    public static Object getSessionAttribute(String key) {
        return getSession().get(key);
    }

    public static void setSessionAttribute(String key, Object obj) {
        getSession().put(key, obj);
    }
    
    public static void sayHello() {
        System.out.println("hello"+ getCurStrDate(1));
    }

    public static HttpServletRequest getRequest() {
//        return (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
        return ServletActionContext.getRequest();
    }

    public static HttpServletResponse getResponse() {
//        return (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
        return ServletActionContext.getResponse();
    }

    /**
     * 获取cookie数组
     *
     * @return 返回cookie数组, 否则返回null
     */
    public static Cookie[] getCookies() {
        return getRequest().getCookies();
    }

    /**
     * 从cookie中获取值
     *
     * @param key cookie中的键值
     * @return 返回对应的值，找不到或者不存在时返回""
     */
    public static String getCookieVal(String key) {
        Cookie[] cookies = getCookies();
        if (cookies == null)
            return "";
        Cookie cookie;
        String val = "";
        for (int i = 0; i < cookies.length; i++) {
            cookie = cookies[i];
            if (cookie.getName().equals(key)) {
                val = cookie.getValue();
                try {
                    //2015.11.25 yanghongjian 解决中文乱码
                    val = val != null ? URLDecoder.decode(val, "utf-8") : "";
                } catch (UnsupportedEncodingException e) {
                    val = "";
                }
            }
        }
        return val;
    }

    /**
     * 设置cookie
     *
     * @param mapParam {key:'',val:'',path:'/',maxage:'-1'}
     */
    public static void setCookie(Map mapParam) {
        String key = MapUtils.getString(mapParam, "key", ""), val = MapUtils.getString(mapParam, "val", "");
        try {
            key = URLEncoder.encode(key, "utf-8");
            val = URLEncoder.encode(val, "utf-8");
            Cookie ck = new Cookie(key, val);
            //参数为负数代表关闭浏览器时清除cookie，参数为0时代表删除cookie，参数为正数时代表cookie存在多少秒。
            //设置生命周期 默认-1关闭浏览器后，cookie立即失效 秒为单位
            ck.setMaxAge(MapUtils.getIntValue(mapParam, "maxage", -1));
            //设置路径 默认"/"cookie有效路径是网站根目录
            //这个路径即该工程下都可以访问该cookie 如果不设置路径，
            //那么只有设置该cookie路径及其子路径可以访问
            ck.setPath(MapUtils.getString(mapParam, "path", "/"));
            getResponse().addCookie(ck);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置cookie,默认是在网站根目录下、关闭浏览器立即失效的cookie
     *
     * @param key 键
     * @param val 值
     */
    public static void setCookie(String key, String val) {
        JsonHelp.setCookie(ArrayUtils.toMap(new String[][]{
                {"key", key},
                {"val", val}
        }));
    }

    public static boolean delCookie(String key) {
        Cookie[] cookies = getCookies();
        if (cookies == null)
            return true;

        Cookie cookie;
        for (int i = 0; i < cookies.length; i++) {
            cookie = cookies[i];
            if (cookie.getName().equals(key)) {
                cookie.setValue(null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                getResponse().addCookie(cookie);
                return true;
            }
        }
        return false;
    }

    /**
     * 通过子表的字段值生成map对象并把map对象保存到list对象中<br/>
     * <b>请注意:</b>参数中一定要XH键
     *
     * @param mapSubTab 子表map对象中的值是以","隔开的字符串
     * @param keys      关键字
     * @return 返回map集合对象
     */
    public static List createListByMap(Map mapSubTab, String keys) {
        List list = new ArrayList();
        String[] xh = ((String) mapSubTab.get(keys)).split(",", -1);
        for (int i = 0; i < xh.length; i++) {
            Map mapTem = new HashMap();
            Iterator it = mapSubTab.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                String[] vals = val.split(",", -1);
                if (vals.length < xh.length)
                    mapTem.put(key, val);
                else {
                    mapTem.put(key, vals[i]);
                }
            }
            list.add(mapTem);
        }

        return list;
    }

    /**
     * 通过子表的字段值生成map对象并把map对象保存到list对象中<br/>
     * <b>请注意:</b>参数中一定要XH键
     *
     * @param mapSubTab 子表map对象中的值是以","隔开的字符串
     * @return 返回map集合对象
     */
    public static List createListByMap(Map mapSubTab) {
        List list = new ArrayList();
        String[] xh = ((String) mapSubTab.get("XH")).split(",", -1);
        for (int i = 0; i < xh.length; i++) {
            Map mapTem = new HashMap();
            Iterator it = mapSubTab.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                String[] vals = val.split(",", -1);
                if (vals.length < xh.length)
                    mapTem.put(key, val);
                else {
                    if (key.equals("XH")) {//对空白序号自动赋空值
                        mapTem.put(key, vals[i].length() == 0 ? "" : vals[i]);
                    } else
                        mapTem.put(key, vals[i]);
                }
            }
            list.add(mapTem);
        }

        return list;
    }

    public static Map getReqFormMapAndAppInfo() {
        Map retMap = getReqFormMap();
        //注入消费者调用接口服务时需要的参数 从消费者的配置文件中读取键值
        if (isEmpty(retMap.get("APP_ID"))
                && isEmpty(retMap.get("APP_DM"))
                && isEmpty(retMap.get("APP_MC"))) {
            retMap.put("APP_DM", getPropertiesByKey("APP_DM"));
            retMap.put("APP_ID", getPropertiesByKey("APP_ID"));
            retMap.put("APP_MC", getPropertiesByKey("APP_MC"));
            retMap.put("APP_IP", JsonHelp.getRemoteIp(getRequest()));
        }
        return retMap;
    }

    /**
     * 获取所有表单值
     *
     * @return 返回Map对象
     */
    public static Map getReqFormMap() {
        Map retMap = new HashMap();
        String val = "";
        for (Iterator it = getRequest().getParameterMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry element = (Map.Entry) it.next();
            Object strKey = element.getKey();
            String[] value = (String[]) element.getValue();
            val = "";
            for (int i = 0; i < value.length; i++)
                val += reconvert(value[i]) + ',';
            val = val.substring(0, val.length() - 1);
            retMap.put(strKey, val);
        }
        return retMap;
    }


    public static Map getReqParamMap() {
        return getReqFormMap();
    }

    public static String getReqParamVal(String paramName) {
        return getReqFormVal(paramName);
    }

    /**
     * 获取表单指定值
     *
     * @param paramName 表单中的key名称
     * @return 对应的值
     */
    public static String getReqFormVal(String paramName) {
        List list = new ArrayList();
        try {
            list = Arrays.asList((String[]) getRequest().getParameterMap().get(paramName));
        } catch (NullPointerException e) {
//            log.error("读取参数->" + paramName + "=" + e.getMessage());
        }
        String val = "";
        for (int i = 0; i < list.size(); i++)
            val += ((String) list.get(i)) + ',';

        return val.length() > 0 ? (val.substring(0, val.length() - 1)) : val;
    }

    /**
     * ajax请求返回
     *
     * @param retCode 返回代码
     * @param retInfo 返回消息
     */
    public static void ajaxResponseTxt(int retCode, String retInfo) {
        Map map = new HashMap();
        map.put("data", "");
        ajaxResponseTxt(retCode, retInfo, map);
    }

    /**
     * ajax请求返回
     *
     * @param retCode 返回代码
     * @param retMsg  返回消息
     * @param map     简单的附加
     */
    public static void ajaxResponseTxt(int retCode, String retMsg, Map map) {
        List list = new ArrayList();
        list.add(map);
        ajaxResponseTxt(retCode, retMsg, list);
    }

    /**
     * ajax请求返回
     *
     * @param retCode 返回代码
     * @param retMsg  返回消息
     * @param listMap 是HashMap类型的集合封装需要转化成JSON字符串的值对
     */
    public static void ajaxResponseTxt(int retCode, String retMsg, List listMap) {
//        long begin = System.currentTimeMillis();
        HttpServletResponse response = getResponse();

        response.setHeader("Charset", "UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "No-cache");

        //使用csrf-token解决csrf安全问题
        if (StringUtils.equals(getPropertiesByKey("CSRF_TOKEN_CHECK"), "1"))
            response.setHeader(CsrfToken.getKey(), CsrfToken.setCsrfTokenInSession());
        //返回的是txt文本文件
        response.setContentType("text/json;charset=UTF-8");

        Map tempMap = new HashMap();
        tempMap.put("code", String.valueOf(retCode));
        tempMap.put("msg", retMsg);

        String strJson;
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(tempMap);

        for (Object aListMap : listMap) {
            jsonObject.putAll((Map) aListMap);
        }

        //在ResultHashMap类中完成字符集转换
        strJson = jsonObject.toString();

        String jsoncallback = JsonHelp.getReqParamVal("callback");
        strJson = jsoncallback.length() == 0 ? strJson : jsoncallback + "(" + strJson + ")";
        log.info(strJson);

        //添加输出压缩
        String encoding = getRequest().getHeader("Accept-Encoding");
        boolean b = false;
//        if (encoding != null && encoding.indexOf("gzip") != -1) {
//            response.setHeader("Content-Encoding", "gzip");
//            b = true;
//        } else if (encoding != null && encoding.indexOf("compress") != -1) {
//            response.setHeader("Content-Encoding", "compress");
//            b = true;
//        }

        if (b) {
            GZIPOutputStream gzipOut = null;
            InputStream in = null;
            try {
                gzipOut = new GZIPOutputStream(response.getOutputStream());
                //修正编码问题
                in = new ByteArrayInputStream(strJson.getBytes("UTF-8"));
                byte[] buf = new byte[2048];
                int len;
                while ((len = in.read(buf)) > 0) {
                    gzipOut.write(buf, 0, len);
                }
                gzipOut.finish();//GzipOutputStream是finish而不是flush


//                PrintWriter out = new PrintWriter(new GZIPOutputStream(response.getOutputStream()));
//                out.println(strJson);
//                out.flush();
//                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (gzipOut != null)
                        gzipOut.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.write(strJson);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null)
                    out.close();
            }
        }

        /*     try {
               PrintWriter out = response.getWriter();

               out.write(strJson);
               out.flush();
               out.close();
           } catch (IOException e) {
               log.error(e.getMessage());
           }
        */

//        System.out.println("->JsonHelp"
//                + "写out:" + (System.currentTimeMillis() - end4)
//                + "ms\t");
    }

    public static void ajaxResponse(Map mapParam) {
        HttpServletResponse response = getResponse();
        response.setHeader("Charset", "UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "No-cache");
        String cntType = isEmpty(mapParam.get("CONTENT_TYPE"))
                ? "text/json;charset=UTF-8"
                : (String) mapParam.get("CONTENT_TYPE");

        response.setContentType(cntType);

        String strRet = (String) mapParam.get("strRet");
        String jsoncallback = JsonHelp.getReqParamVal("callback");
        String strJson = jsoncallback.length() == 0 ? strRet : jsoncallback + "(" + strRet + ")";
//        log.info(strJson);
        try {
            PrintWriter out = response.getWriter();
            out.write(strJson);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * ajax请求返回
     *
     * @param strRet 返回消息
     */
    public static void ajaxResponse(String strRet) {
        Map mapParam = new HashMap();
        mapParam.put("CONTENT_TYPE", "text/json;charset=UTF-8");
        mapParam.put("strRet", strRet);
        ajaxResponse(mapParam);
    }

    /**
     * ajax请求返回
     *
     * @param strRet 返回消息
     */
    public static void ajaxResponseXml(String strRet) {
        Map mapParam = new HashMap();
        mapParam.put("CONTENT_TYPE", "text/xml;charset=UTF-8");
        mapParam.put("strRet", strRet);
        ajaxResponse(mapParam);
    }


    public static String createJsonString(int retCode, String retMsg) {
        return createJsonString(retCode, retMsg, new HashMap());
    }

    public static String createJsonString(int retCode, String retMsg, Map map) {
        List list = new ArrayList();
        list.add(map);
        return createJsonString(retCode, retMsg, list);
    }

    public static String createJsonString(int retCode, String retMsg, List listMap) {
        long begin = System.currentTimeMillis();

        Map tempMap = new HashMap();
        tempMap.put("code", String.valueOf(retCode));
        tempMap.put("msg", retMsg);

        String strJson, className;

        className = "JSONObject";
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(tempMap);
        for (Object aListMap : listMap) {
            jsonObject.putAll((Map) aListMap);
        }
        long end3 = System.currentTimeMillis();
//        System.out.println("->JsonHelp(" + className + ")准备数据:" + (end3 - end2) + "ms");
        //在ResultHashMap类中完成字符集转换
        strJson = jsonObject.toString();

        long end4 = System.currentTimeMillis();
        System.out.println("->JsonHelp(" + className + ")生成Json字符串:" + (end4 - end3) + "ms");
        return strJson;
    }

    public static String createJsonStringBak(int retCode, String retMsg, List listHashMap) {
        JSONObject jsonObject = new JSONObject();
        Map tempMap = new HashMap();
        tempMap.put("code", String.valueOf(retCode));
        tempMap.put("msg", retMsg);
        jsonObject.putAll(tempMap);
        for (Iterator it = listHashMap.iterator(); it.hasNext(); ) {
            jsonObject.putAll((Map) it.next());
        }

        return jsonObject.toString();
    }

    /**
     * 获取客户端IP
     *
     * @return 返回客户端IP
     */
    public static String getRemoteIp() {
        return getRemoteIp(getRequest());
    }

    /**
     * 获取客户端IP
     *
     * @param request web容器的请求对象
     * @return 返回客户端IP
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 通过json字符串生成Map对象
     *
     * @param strJson json字符串
     * @return 返回Map对象
     */
    public static Map getMapByJson(String strJson) {
        return strJson.isEmpty() ? null : JSON.parseObject(strJson, Map.class);
    }

    /**
     * 通过Map对象生成json字符串
     *
     * @param mapParam map对象
     * @return 返回json字符串
     */
    public static String getJsonByMap(Map mapParam) {
        return JSON.toJSONString(mapParam);
    }

    /**
     * 获取UUID解决SOA服务数据唯一性问题
     *
     * @return 不带'-'的32位字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    }

    public static void main(String[] args) {
        convert("中国");
        Map tempMap = new HashMap();
        tempMap.put("code", null);
        tempMap.put("msg", "msg");

        tempMap.put("sd", null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(tempMap);
        System.out.println(jsonObject.toString());

        String strJson = "{code:0,msg:'good',data:[{name:'',good:''}],TEST:{GOOD:'good'}}";
//        JSONObject jsonObj = JSONObject.fromObject(strJson);
//        Object temMap = JSONObject.toBean(jsonObj, Map.class);
//        System.out.println(temMap);
//        System.out.println((List) ((Map) temMap).get("data"));
        Object obj = JSON.parseObject(strJson, Map.class);
        System.out.println(obj);

        System.out.println(JsonHelp.getUUID() + "\t" + JsonHelp.getUUID().length());

    }
    public static void say() {
      System.out.println("您好，世界");
    }
    
}
