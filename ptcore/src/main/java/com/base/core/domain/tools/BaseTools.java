package com.base.core.domain.tools;

import com.alibaba.dubbo.common.utils.NetUtils;
import com.base.core.domain.exception.NoSuchBeanException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期:2011-1-17<br/>
 * 描述:辅助工具类<br/>
 * <p/>
 * 历史记录:
 * <ol>
 * 日期:2011-1-17 作者:yanghongjian
 * <li>创建功能代码</li>
 * 日期:2014-4-3 作者:yanghongjian
 * <li>解决读取数据库数据时字符集转换的问题</li>
 * <li>递归加载配置文件</li>
 * 日期:2014-8-30作者:yanghongjian
 * <li>在spring中加载配置文件，代码方式获取properties的工具类，统一、简化了配置文件的读取使用</li>
 * <li>添加map转化为bean方法</li>
 * <li>添加使用bean的属性生成Map对象</li>
 * 日期:2015-7-21 作者:yanghongjian
 * <li>添加字符串格式化方法</li>
 * </ol>
 * <p/>
 * <a href="mailto:12719889@qq.com">YangHongJian</a>
 *
 * @version 2.0
 * @since 1.6
 */
public class BaseTools {
    private static Logger log = Logger.getLogger(BaseTools.class);
    public static ApplicationContext ctx = null;
    protected static HashMap mapProperties = new HashMap();

    protected BaseTools() {
        showInfo();
    }

    /**
     * 从web.xml容器中获取spring的上下文对象
     *
     * @return 返回spring的上下文对象
     */
    public static ApplicationContext getInstanceCtx() {
        if (ctx == null) {
            log.info("从web.xml容器中获取spring的上下文对象...");
            ctx = ContextLoader.getCurrentWebApplicationContext();
            mapProperties = (HashMap) BasePropertyConfigurer.getMapProperties();
            log.info("获取成功.");
            showInfo();
        }

        return ctx;
    }

    /**
     * 加载指定的spring配置，并生成上下文对象
     *
     * @param springXml spring的主配置文件
     * @return 返回spring的上下文对象
     */
    public static ApplicationContext getInstanceCtx(String springXml) {
        if (ctx == null) {
            log.info("加载" + springXml + "...");
            ctx = new ClassPathXmlApplicationContext(springXml);
            log.info("加载完毕.");
            mapProperties = (HashMap) BasePropertyConfigurer.getMapProperties();
            showInfo();
        }
        return ctx;
    }

    /**
     * 显示系统一些版权信息
     */
    public static void showInfo() {
//        String dbmsCharset = BaseTools.getPropertiesByKey("DBMS_CHARSET");
//        if (!("UTF-8".equals(dbmsCharset)))
//            dbmsCharset = dbmsCharset + "(AMERICAN_AMERICA.US7ASCII)";
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t\t\t$APP_MC$ Ver$APP_VER$ 创建日期:$APP_CJRQ$\n")
                .append("\t-------------------------------------------------------\n")
                .append("\t\t部署环境:\n")
                .append("\t\t\t中文环境(unix/liunx)设置 export lang=zh_CN\n")
                .append("\t\t\tjetty-6.1.26 JDK1.6.x servlet2.4 jsp2.0\n")
                .append("\t\t系统架构:\n")
                .append("\t\t\tJavaEncode:$JAVA_CHARSET$\n")
                .append("\t\t\tweb层:struts2.3.16.3 jQuery1.9 pageEncode:UTF-8\n")
                .append("\t\t\t分布式接口:Dubbo.2.5.2\t\n")
                .append("\t\t\tbo层:Spring-3.2.11\n")
                .append("\t\t\tdao层:ibatis-2.3.4.726");
        if (BaseTools.getPropertiesByKey("DBMS_TYPE").length() > 0)
            sb.append("\n\t\t数据源:\n\t\t\t数据库类型:$DBMS_TYPE$")
                    .append("\n\t\t\t数据库字符集:").append(MapUtils.getString(BaseTools.mapProperties, "DBMS_CHARSET", "AMERICAN_AMERICA.US7ASCII"))
                    .append("\n\t\t\t数据库地址:$DBMS_IP$ \t $DBMS_USER$/$DBMS_PASS$");
        sb.append("\n\n\t\t$APP_GROUP$\t$APP_TEAM$")
                .append("\n\t-------------------------------------------------------\n");
        log.info(BaseTools.format(sb.toString(), BaseTools.mapProperties));
    }

    /**
     * 通过beanID获取bean实例
     *
     * @param beanID bean的代码
     * @return 返回对应的实例
     * @throws NoSuchBeanException bean没有定义
     */
    public static Object getBean(String beanID)
            throws NoSuchBeanException {
        Object obj;

        if (BaseTools.ctx.containsBeanDefinition(beanID)) {
            obj = BaseTools.ctx.getBean(beanID);
        } else {
            log.info("beanID=" + beanID + "没有定义");
            throw new NoSuchBeanException(beanID + "没有定义!");
        }
        return obj;
    }

    /**
     * 将map转化为bean
     * <pre>
     * 案例:
     * Bean bean = (Bean)BaseTools.mapToBean(srcMap,Bean.class);
     * </pre>
     *
     * @param srcMap 初始化的map 注意map中的key要和bean的属性命名一致
     * @param type   bean的类名
     * @return 返回赋值后的bean对象
     */
    public static Object mapToBean(Map srcMap, Class type) {
        Object bean = null;
        try {
            bean = type.newInstance();
            BaseTools.mapToBean(srcMap, bean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return bean;
    }

    /**
     * 将map转化为bean
     *
     * @param srcMap   初始化的map 注意map中的key要和bean的属性命名一致
     * @param destBean bean对象
     * @return 返回赋值后的bean对象
     */
    public static Object mapToBean(Map srcMap, Object destBean) {
        try {
            //将Map中的值设入bean中
            BeanUtils.populate(destBean, srcMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destBean;
    }

    /**
     * 使用bean的属性生成Map对象
     *
     * @param srcBean bean对象
     * @return map对象
     */
    public static Map beanToMap(Object srcBean) {
        Map destMap;
        try {
            destMap = BeanUtils.describe(srcBean);
        } catch (Exception e) {
            destMap = null;
            log.error(e.getMessage());
        }
        return destMap;
    }

    /**
     * 从系统架构的配置文件moonsun-ptcore.properties中获取指定的键值
     *
     * @param key 键名
     * @return 键值
     */
    public static String getPropertiesByKey(String key) {
        if (mapProperties == null || mapProperties.isEmpty()) {
            mapProperties = (HashMap) BasePropertyConfigurer.getMapProperties();
            if (mapProperties == null) {
                Properties baseProperties = new Properties();
                try {
                    baseProperties.load(BaseTools.class.getResourceAsStream("/base-ptcore.properties"));
                    mapProperties = new HashMap();
                    Enumeration en = baseProperties.propertyNames();
                    while (en.hasMoreElements()) {
                        String k = (String) en.nextElement();
                        mapProperties.put(k, baseProperties.getProperty(k));
                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        String val = "";
        if (mapProperties != null)
            val = mapProperties.get(key) != null ? (String) mapProperties.get(key) : "";

        return val;
    }

    private static int seq = 0;
    private static final int MAX_PER_SECOND = 1000;

    /**
     * 生成15位的数字流水号(16位以后会出现科学计数影响业务操作)
     * <p>
     * <I>生成规则为:</I><b>yyMMddHHmmss+1位顺序号</b>
     * </p>
     *
     * @return 15位流水号
     */
    public static synchronized String getNextSeq() {
        seq++;
//        String strSeq = String.valueOf(seq %= MAX_PER_SECOND);
//        String temSeq = "000";
//        return (new SimpleDateFormat("yyyyMMddHHmm").format(new Date()))
//                + (temSeq.substring(strSeq.length()) + strSeq);
        return (new SimpleDateFormat("yyMMddHHmmss").format(new Date()))
                + String.format("%03d", seq %= MAX_PER_SECOND);
    }

    /**
     * 取当前时间的毫秒数做为序列号
     *
     * @return 13位流水号
     */
    public static synchronized String getMsNextSeq() {
        return String.valueOf((new Date().getTime()));
    }


    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return 返回true为空
     */
    public static boolean isEmpty(Object obj) {
        return obj == null || obj.equals("");
    }

    /**
     * 复制map对象中的值
     *
     * @param map    接受第二个参数中map的值
     * @param mapAdd 需要取值的map对象
     */
    public static void mapCopy(Map map, Map mapAdd) {
        if (mapAdd == null)
            return;
        if (map == null)
            map = new HashMap();
        map.putAll(mapAdd);
    }

    public static String getMapByKey(Map mapParam, String key) {
        Object val = mapParam.get(key);
        if (val instanceof String)
            return val.toString();
        String retStr = "";
        if (val != null) {
            if (val instanceof Timestamp) //定义格式并转化成字符型日期格式
                retStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Timestamp(((Timestamp) val).getTime()));
            if (val instanceof java.sql.Date) //定义格式并转化成字符型日期格式
                retStr = (new SimpleDateFormat("yyyy-MM-dd")).format(val);
            if (val instanceof BigDecimal)
                retStr = val.toString();
        }
        return retStr;
    }

    /**
     * 字符串编码转换为指定编码
     *
     * @param str 被处理的字符串
     * @return String 将数据库中读出的数据进行转换
     */
    public static String convert(String str) {
        String javaCharset = BaseTools.getPropertiesByKey("JAVA_CHARSET");
        javaCharset = 0 != javaCharset.trim().length() ? javaCharset : "GBK";

        String dbmsCharset = BaseTools.getPropertiesByKey("DBMS_CHARSET");
        dbmsCharset = 0 != dbmsCharset.trim().length() ? dbmsCharset : "ISO8859_1";
        if ("UTF-8".equals(dbmsCharset) && javaCharset.equals(dbmsCharset))
            return str.trim();

        String str1 = "";
        if (str == null)
            str = "";
        str = str.trim();
        if (str.length() > 0) {
            try {
                //经过多次测试源代码的编码是UTF-8时，从ISO8859_1数据库获取数据后的转换要使用GBK
                javaCharset = javaCharset.equals("UTF-8") ? "GBK" : javaCharset;
                str1 = new String(str.getBytes("ISO8859_1"), javaCharset);
//                str1 = new String(str.getBytes("ISO8859_1"), "GBK");
            } catch (Exception e) {
                str1 = str;
            }
        }
        return str1;
    }

    /**
     * 字符串编码反转换为指定编码
     *
     * @param str 被处理的字符串
     * @return String 将jsp提交的数据进行转换放入数据库中
     */
    public static String reconvert(String str) {
        String javaCharset = BaseTools.getPropertiesByKey("JAVA_CHARSET");
        javaCharset = 0 != javaCharset.trim().length() ? javaCharset : "GBK";

        String dbmsCharset = BaseTools.getPropertiesByKey("DBMS_CHARSET");
        dbmsCharset = 0 != dbmsCharset.trim().length() ? dbmsCharset : "ISO8859_1";
        if ("UTF-8".equals(dbmsCharset) && javaCharset.equals(dbmsCharset))
            return str.trim();

        String str1 = "";
        if (str == null)
            str = "";
        str = str.trim();
        if (str.length() > 0) {
            try {
                //经过多次测试源代码的编码是UTF-8时，要保存到ISO8859_1数据库时要转换要使用GBK
                javaCharset = javaCharset.equals("UTF-8") ? "GBK" : javaCharset;
                str1 = new String(str.getBytes(javaCharset), dbmsCharset);
            } catch (Exception e) {
                str1 = str;
            }
        }
        return str1;
    }

    /**
     * 替换字符串模式中的默认占位符#,$号
     *
     * @param tplStr   模式字符串
     * @param mapParam 替换参数，注意区分大小写
     * @return 返回替换好的字符串
     */
    public static String format(String tplStr, Map mapParam) {
        return format(tplStr, mapParam, "#,$");
    }


    /**
     * 替换字符串模式中的占位符
     *
     * @param tplStr      模式字符串
     * @param mapParam    替换参数，注意区分大小写
     * @param placeholder 多个占位符使用,号隔开
     * @return 返回替换好的字符串
     */
    public static String format(String tplStr, Map mapParam, String placeholder) {
        String[] phs = StringUtils.split(placeholder, ",");
        for (int i = 0; i < phs.length; i++) {
            if (tplStr.indexOf(phs[i]) != -1) {
                //生成匹配模式的正则表达式
                Pattern pattern = Pattern.compile(String.format("\\%s(" + StringUtils.join(mapParam.keySet(), "|") + ")\\%s", phs[i], phs[i]));
                Matcher matcher = pattern.matcher(tplStr);

                //两个方法：appendReplacement, appendTail
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, mapParam.get(matcher.group(1)).toString());
                }
                matcher.appendTail(sb);
                tplStr = sb.toString();
            }
        }

        return tplStr;
    }

    /**
     * 取得当前日期
     *
     * @param type 格式类型 0:yyyy-MM-dd 1:yyyy-MM-dd HH:mm:ss 2:yyyyMMdd 3:HHmmss 4:yyyyMMddHHmmss
     * @return String 返回当前年月日
     */
    public static String getCurStrDate(int type) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        switch (type) {
            case 0:
                pattern = "yyyy-MM-dd";
                break;
            case 1:
                pattern = "yyyy-MM-dd HH:mm:ss";
                break;
            case 2:
                pattern = "yyyyMMdd";
                break;
            case 3:
                pattern = "HHmmss";
                break;
            case 4:
                pattern = "yyyyMMddHHmmss";
                break;
        }
        return (new SimpleDateFormat(pattern)).format(new Date());
    }

    /**
     * 返回日期类型的值
     *
     * @param strDate 字符串类型日期
     * @param type    格式类型 0:yyyy-MM-dd 1:yyyy-MM-dd HH:mm:ss
     * @return 日期类型的值
     */
    public static Date getDateByStr(String strDate, int type) {
        type = strDate.length() == 10 ? 0 : 1;
        Date curDate = null;
        try {
            curDate = (new SimpleDateFormat(type == 0 ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss")).parse(strDate);

        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return curDate;
    }

    /**
     * 获取本月的起始日期
     *
     * @return 返回本月起始日期
     */
    public static String getCurrentDateBegin() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        return String.valueOf(y) + "-" + (m < 10 ? "0" + String.valueOf(m) : String.valueOf(m)) + "-01";
    }

    /**
     * 获取本月的起始日期
     *
     * @return 返回本月起始日期
     */
    public static String getCurrentDateEnd() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        return String.valueOf(y) + "-" + (m < 10 ? "0" + String.valueOf(m) : String.valueOf(m)) + "-" + (String.valueOf(daysInMonth(y, m)));
    }

    /**
     * 返回一个月的最后一天
     *
     * @param nd 年
     * @param yf 月
     * @return 返回一个月的最后一天
     */
    public static int daysInMonth(int nd, int yf) {
        switch (yf) {
            case 1: // '\001'
            case 3: // '\003'
            case 5: // '\005'
            case 7: // '\007'
            case 8: // '\b'
            case 10: // '\n'
            case 12: // '\f'
                return 31;

            case 4: // '\004'
            case 6: // '\006'
            case 9: // '\t'
            case 11: // '\013'
                return 30;
        }
        return nd % 100 == 0 || nd % 4 != 0 ? 28 : 29;
    }

    /**
     * 在字符前或后加指定字符到指定长度
     *
     * @param str    待处理字符串
     * @param s      指定添加字符
     * @param ic     指定长度
     * @param before 是否在前面添加
     * @return String 补足位数的字符串
     */
    public static String addStr(String str, String s, int ic, boolean before) {
        if (str == null) str = "";
        while (str.length() < ic) {
            if (before)
                str = s + str;
            else
                str = str + s;
        }
        return str;
    }

    /**
     * 自定义时间显示方式
     *
     * @param type 类型<br>
     *             1 : yyyy-mm-dd<br>
     *             2 : yyyy-mm-dd hh:mm:ss<br>
     *             3 : yyyy年mm月dd日
     * @param date 日期
     * @return String <br>
     * 1: 返回yyyy-mm-dd<br>
     * 2: 返回yyyy-mm-dd hh:mm:ss<br>
     * 3: XXXX年XX月XX日
     * 4: XX月XX日
     * 5: XX月XX日 XX时XX分XX秒
     * 6：返回yyyymm
     * 7：返回yyyymmdd
     * 8：返回HHmmssSSS
     */
    public static String getDate(String date, int type) {
        if (date == null || date.equals("")) return "";
        String y = date.substring(0, 4);
        String m = date.substring(5, 7);
        String d = date.substring(8, 10);
        SimpleDateFormat df;
        String str;
        switch (type) {
            case 1:
                df = new SimpleDateFormat("yyyy-MM-dd");
                str = df.format(date);
                break;
            case 2:
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                str = df.format(date);
                break;
            case 3:
                str = String.valueOf(y) + "年" + addStr(String.valueOf(m), "0", 2, true) + "月" + addStr(String.valueOf(d), "0", 2, true) + "日";
                break;
            case 4:
                str = addStr(String.valueOf(m), "0", 2, true) + "月" + addStr(String.valueOf(d), "0", 2, true) + "日";
                break;
            case 5:
                str = y + '-' + m + '-' + d;
                break;
            case 6:
                str = String.valueOf(y) + addStr(String.valueOf(m), "0", 2, true);
                break;
            case 7:
                str = String.valueOf(y) + addStr(String.valueOf(m), "0", 2, true) + addStr(String.valueOf(d), "0", 2, true);
                break;
            case 8:
                df = new SimpleDateFormat("HHmmssSSS");
                str = df.format(date);
                break;
            default:
                str = "";
        }
        return str;
    }

    /**
     * 返回日期类型的值
     *
     * @param strDate 字符串类型的值 格式为yyyy-MM-dd
     * @return 日期类型的值
     */
    public static Date getDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = null;
        try {
            curDate = format.parse(strDate);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return curDate;
    }

    /**
     * 获取两日期之间的天数
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return int 前日期大于后日期的天数
     */
    public static long getDayBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        return (date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24;
    }

    /**
     * 获取两日期之间的天数
     *
     * @param start 日期1
     * @param end   日期2
     * @return int 前日期大于后日期的天数
     */
    public static long getDayBetween(String start, String end) {
        Date date1 = BaseTools.getDate(start);
        Date date2 = BaseTools.getDate(end);
        return (date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24;
    }

    /**
     * 生产规范的xml字符串参数,默认把节点名转成大写
     *
     * @param mapParam map参数对象
     * @return xml字符串参数
     */
    public static String getXmlByMap(Map mapParam) {
        return getXmlByMap(mapParam, true);
    }

    /**
     * 生产规范的xml字符串参数  map参数对象
     *
     * @param mapParam  map参数对象
     * @param upperCase 是否把节点名转成大写
     * @return xml字符串参数
     */
    public static String getXmlByMap(Map mapParam, boolean upperCase) {
        return getXmlByMap(mapParam, upperCase, "params");
    }

    public static String getXmlByMap(Map mapParam, boolean upperCase, String root) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<" + root + ">");
        String key;
        for (Iterator it = mapParam.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry element = (Map.Entry) it.next();
            Object strKey = element.getKey();
            Object strVal = element.getValue();
            if (strVal instanceof Timestamp) {
                strVal = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Timestamp(((Timestamp) strVal).getTime()));
            } else if (strVal instanceof java.sql.Date)
                strVal = (new SimpleDateFormat("yyyy-MM-dd")).format(strVal);
            else if (strVal instanceof Date)
                strVal = (new SimpleDateFormat("yyyy-MM-dd")).format(strVal);

            key = upperCase ? strKey.toString().toUpperCase() : strKey.toString();
            sb.append("<").append(key).append(">")
                    .append(strVal != null ? "<![CDATA[" + strVal + "]]>" : "")
                    .append("</").append(key).append(">");
        }
        return sb.append("</" + root + ">").toString();
    }

    /**
     * 生产规范的xml字符串参数
     *
     * @param obj 参数对象
     * @return xml字符串参数
     */
    public static String getXmlByObj(Object obj) {
        StringBuffer sb = new StringBuffer();
        Method[] methods = obj.getClass().getDeclaredMethods();
        //遍历方法集合
        try {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<params>");
            String methName;
            for (int i = 0; i < methods.length; i++) {
                methName = methods[i].getName();
                if (methName.startsWith("get")) {
                    Object object = methods[i].invoke(obj, new Object[]{});
                    if (object instanceof Timestamp) {
                        object = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Timestamp(((Timestamp) object).getTime()));
                    } else if (object instanceof java.sql.Date)
                        object = (new SimpleDateFormat("yyyy-MM-dd")).format(object);
                    if (object instanceof Date)
                        object = (new SimpleDateFormat("yyyy-MM-dd")).format(object);

                    methName = methName.substring(3).toLowerCase();
                    sb.append("<").append(methName.toUpperCase()).append(">")
                            .append(object != null ? "<![CDATA[" + object + "]]>" : "")
                            .append("</").append(methName.toUpperCase()).append(">");
                }
            }
            sb.append("</params>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 将一个字符串转化为输入流
     */
    public static InputStream getStringStream(String sInputString) {
        if (sInputString != null && !sInputString.trim().equals("")) {
            try {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * xml转换为Map对象
     *
     * @param strXml xml字符串
     * @return 返回map对象
     */
    public static Map xmlToMap(String strXml) {
        return XmlTools.xmlToMap(strXml);
    }

    /**
     * map对象转化为xml
     *
     * @param mapParam map对象
     * @return xml字符串
     */
    public static String mapToXml(Map mapParam) {
        return XmlTools.mapToXml(mapParam);
    }

    /**
     * 将一个输入流转化为字符串
     */
    public static String getStreamString(InputStream tInputStream) {
        if (tInputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null) {
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取UUID，去除-符号
     *
     * @return 返回UUID
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 判断字符串什么编码类型
     *
     * @param str 字符串
     * @return 返回编码名称 GB2312|ISO-8859-1|UTF-8|GBK
     */
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 获取本机IP地址
     *
     * @return 返回IP地址
     */
    public static String getCurIp() {
        String strIp;
        try {
            strIp = NetUtils.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            strIp = "";
            log.error(e.getMessage());
        }
        return strIp;
    }

    /**
     * 获取客户端的mac地址
     *
     * @param ip 客户端的ip地址
     * @return 返回mac地址
     */
    public static String getMacByIp(String ip) {
        String str;
        String macAddress = "";
        try {
            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
            InputStreamReader ir = new InputStreamReader(p.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    if (str.indexOf("MAC Address") > 1) {
                        macAddress = str.substring(
                                str.indexOf("MAC Address") + 14, str.length());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return macAddress;
    }

    public static void main(String[] args) {
//        System.out.println(BaseTools.getCurStrDate(2));
//        System.out.println(BaseTools.getCurStrDate(3));
//        System.out.println(BaseTools.getCurIp());
//        System.out.println(BaseTools.getMacByIp(BaseTools.getCurIp()));
        for (int i = 0; i < 120; i++) {
            System.out.println(BaseTools.getNextSeq() + "\t" + BaseTools.getNextSeq().length());
        }

//        System.out.println(BaseTools.getMsNextSeq());
    }
}
