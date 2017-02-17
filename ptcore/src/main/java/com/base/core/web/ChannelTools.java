package com.base.core.web;

import com.alibaba.fastjson.JSONPath;
import com.base.core.domain.tools.HttpCallback;
import com.base.core.domain.tools.HttpHelp;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <ol>
 * <li>创建文档 2014-11-26</li>
 * <li>月太软件接口服务管控平台通道传输调用辅助工具类</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public final class ChannelTools extends JsonHelp {
    private static Logger log = Logger.getLogger(ChannelTools.class);
    private static HttpHelp httpHelp = HttpHelp.getInstance();

    public ChannelTools() {
    }

    /**
     * parse key-value pair.
     *
     * @param str           string.
     * @param itemSeparator item separator.
     * @return key-value map;
     */
    private static Map<String, String> parseKeyValuePair(String str, String itemSeparator) {
        String[] tmp = str.split(itemSeparator);
        Map<String, String> map = new HashMap<String, String>(tmp.length);
        for (int i = 0; i < tmp.length; i++) {
            Matcher matcher = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)").matcher(tmp[i]);
            if (matcher.matches() == false)
                continue;
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

    public static String getQueryStringValue(String qs, String key) {
        Map<String, String> map = parseQueryString(qs);
        return map.get(key);
    }

    /**
     * 解析请求参数为map对象
     *
     * @param qs url中的请求参数
     * @return 返回map对象
     */
    public static Map<String, String> parseQueryString(String qs) {
        if (qs == null || qs.length() == 0)
            return new HashMap<String, String>();
        return parseKeyValuePair(qs, "\\&");
    }

    /**
     * 把map对象生成url请求字符串
     *
     * @param ps 请求参数
     * @return 返回url请求字符串
     */
    public static String toQueryString(Map<String, String> ps) {
        StringBuilder buf = new StringBuilder();
        if (ps != null && ps.size() > 0) {
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(ps).entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && key.length() > 0
                        && value != null && value.length() > 0) {
                    if (buf.length() > 0) {
                        buf.append("&");
                    }
                    buf.append(key);
                    buf.append("=");
                    buf.append(value);
                }
            }
        }
        return buf.toString();
    }

    /**
     * 根据参数生成通道传输请求报文
     *
     * @param mapParam 报文参数
     * @return 返回请求报文字符串
     */
    public static String buildChannelReqXml(Map mapParam) {
        String channelId = JSONPath.contains(mapParam, "$.channel_id")
                ? JSONPath.eval(mapParam, "$.channel_id").toString()
                : getPropertiesByKey("CHANNEL_ID");
        String appId = JSONPath.contains(mapParam, "$.app_id")
                ? JSONPath.eval(mapParam, "$.app_id").toString()
                : getPropertiesByKey("APP_ID");
        String appDm = JSONPath.contains(mapParam, "$.app_dm")
                ? JSONPath.eval(mapParam, "$.app_dm").toString()
                : getPropertiesByKey("APP_DM");
        String appMc = JSONPath.contains(mapParam, "$.app_mc")
                ? JSONPath.eval(mapParam, "$.app_mc").toString()
                : getPropertiesByKey("APP_MC");
        String tranRet = JSONPath.contains(mapParam, "$.tran_ret")
                ? JSONPath.eval(mapParam, "$.tran_ret").toString()
                : getPropertiesByKey("TRAN_RET");

        mapParam.remove("channel_id");
        mapParam.remove("app_id");
        mapParam.remove("app_id");
        mapParam.remove("app_mc");
        mapParam.remove("tran_ret");

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<service>\n" +
                "<head>\n" +
                "\t<channel_id>").append(channelId).append("</channel_id>\n" +
                "\t<app_id>").append(appId).append("</app_id>\n" +
                "\t<app_dm>").append(appDm).append("</app_dm>\n" +
                "\t<app_mc>").append(appMc).append("</app_mc>\n" +
                "\t<tran_id>").append(mapParam.get("tran_id")).append("</tran_id>\n" +
                "\t<tran_ret>").append(tranRet.length() == 0 ? "json" : tranRet).append("</tran_ret>\n" +
                "\t<tran_seq>").append(getUuid()).append("</tran_seq>\n" +
                "\t<tran_date>").append(getCurStrDate(2)).append("</tran_date>\n" +
                "\t<tran_time>").append(getCurStrDate(3)).append("</tran_time>\n" +
                "\t<expand>\n" +
                "\t\t<name></name>\n" +
                "\t\t<value></value>\n" +
                "\t</expand>\n" +
                "</head>\n");
        mapParam.remove("tran_id");
        sb.append("<body>\n\t<param key=\"type\" value=\"map\">")
                .append(mapToXml(mapParam).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""))
                .append("\t</param>\n</body>\n" +
                        "</service>");

        return sb.toString();
    }

    /**
     * 自个拼装或者手动拼装报文调用通道传输
     *
     * @param mapParam 传输需要的参数
     * @return 返回请求结果
     */
    public static Map doChannelService(Map mapParam) {
        if (mapParam != null) {
            String sendXml = buildChannelReqXml(mapParam);
            mapParam.clear();
            mapParam.put("SEND_XML", sendXml);
        } else
            mapParam = null;

        String url = JSONPath.contains(mapParam, "$.CHANNEL_URL")
                ? JSONPath.eval(mapParam, "$.CHANNEL_URL").toString()
                : getPropertiesByKey("CHANNEL_URL");
        mapParam.remove("CHANNEL_URL");

        String strRet = httpHelp.syncRequest(url,HttpHelp.HttpMethod.POST, mapParam);
        if (StringUtils.isBlank(strRet))
            return null;
        return getMapByJson(strRet);
    }
    /**
     * 自个拼装或者手动拼装报文调用通道传输
     *
     * @param url      请求连接+请求报文
     * @param mapParam 传输需要的参数
     * @return 返回请求结果
     */
    public static Map doChannelService(String url, Map mapParam) {
        if (mapParam != null) {
            String sendXml = buildChannelReqXml(mapParam);
            mapParam.clear();
            mapParam.put("SEND_XML", sendXml);
        } else
            mapParam = null;

        String strRet = httpHelp.syncRequest(url, HttpHelp.HttpMethod.POST, mapParam);
        if (StringUtils.isBlank(strRet))
            return null;
        return getMapByJson(strRet);
    }

    /**
     * 自个拼装或者手动拼装报文调用通道传输
     *
     * @param url         请求连接+请求报文
     * @param callbackObj 回调对象
     */
    public static void doChannelService(String url, final HttpCallback callbackObj) {
        httpHelp.syncRequest(url, HttpHelp.HttpMethod.POST, null, callbackObj);
    }

    /**
     * 调用通道传输
     * <Pre>
     * mapParam.put("CHANNEL_URL", "http://10.1.0.166:7010/doService.do");
     * mapParam.put("channel_id", "MS-HN-JKFWGKPT-MOBILE");
     * 其他业务参数...
     * mapParam.put("tran_id", "iServiceCateInfo.selectT_INFO_MSG");
     * </Pre>
     *
     * @param mapParam    传输需要的参数
     * @param callbackObj 回调对象
     */
    public static void doChannelService(Map mapParam, final HttpCallback callbackObj) {
        String url = JSONPath.contains(mapParam, "$.CHANNEL_URL")
                ? JSONPath.eval(mapParam, "$.CHANNEL_URL").toString()
                : getPropertiesByKey("CHANNEL_URL");

        mapParam.remove("CHANNEL_URL");
        doChannelService(url, mapParam, callbackObj);
    }

    /**
     * 调用通道传输
     *
     * @param url         接口服务的url
     * @param mapParam    传输需要的参数
     * @param callbackObj 回调对象
     */
    public static void doChannelService(String url, Map mapParam, final HttpCallback callbackObj) {
        String sendXml = buildChannelReqXml(mapParam);

        mapParam.clear();
        mapParam.put("SEND_XML", sendXml);
        httpHelp.syncRequest(url, HttpHelp.HttpMethod.POST, mapParam, callbackObj);
    }

    public static void main(String[] args) {
        Map mapParam = new HashMap();
        mapParam.put("channel_id", "MS-HN-JKFWGKPT-MOBILE");
        mapParam.put("tran_id", "iServiceCateInfo.selectT_INFO_MSG");
        mapParam.put("CREATER", "20140515000073407");
        mapParam.put("CHECK_STATE", "0");
        mapParam.put("userType", "Z");
        Map mapTem = new HashMap();
        mapTem.put("TAB1", "TAB1");
        mapTem.put("TAB2", "TAB2");
        mapParam.put("TAB", mapTem);
        //xml报文生成有问题
        List params = new ArrayList();
        params.add(mapTem);
        mapParam.put("params", params);

        System.out.println(buildChannelReqXml(mapParam));
    }
}
