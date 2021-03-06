package com.base.core.domain.tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * <ol>
 * date:11-11-20 editor:yanghongjian
 * <li>创建文档</li>
 * <li>该继承类只用于ibatis的返回map</li>
 * <li>解决数据库表字段值为null时的问题</li>
 * <li>解决字符集转码问题</li>
 * date:2011-12-30 editor:yanghongjian
 * <li>解决使用weblogic数据源时clob字段转换问题</li>
 * </ol>
 * <ol>
 *
 * @author <a href="mailto:12719889@qq.com">yanghongjian</a>
 * @version 3.0
 * @since 1.4.2
 */
public class ResultHashMapOracle extends HashMap {
    public Object put(Object o, Object o1) {
        if (o1 == null)//解决数据库表字段值为null时的问题
            return super.put(o, "");
        else { //解决字符集转码问题
            if (o1 instanceof String)
                return super.put(o, BaseTools.convert((String) o1));
            else if (o1 instanceof BigDecimal) {
                return super.put(o, o1.toString());
            } else if (o1 instanceof Timestamp) {
                return super.put(o, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Timestamp(((Timestamp) o1).getTime())));
            } else if (o1 instanceof java.sql.Date) {
                return super.put(o, (new SimpleDateFormat("yyyy-MM-dd")).format(o1));
            } else if (o1.getClass().isArray()) {
                byte[] bs = (byte[]) o1;
                return super.put(o, BaseTools.convert(new String(bs, 0, bs.length)));
            } else if (o1 instanceof oracle.sql.CLOB) {
//                System.out.println(">>>>>>>>>>>>>oracle.sql.CLOB");
                String rtn = "";
                oracle.sql.CLOB clob = (oracle.sql.CLOB) o1;
                InputStream input = null;
                try {
                    input = clob.getAsciiStream();
                    int len = (int) clob.length();
                    byte[] by = new byte[len];
                    int i;
                    while (-1 != (i = input.read(by, 0, by.length))) {
                        input.read(by, 0, i);
                    }
                    rtn = new String(by);
                    rtn = clob.getSubString((long) 1, (int) clob.length());
                } catch (Exception e) {
                    e.printStackTrace();
                    rtn = "";
                } finally {
                    try {
                        if (input != null)
                            input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return super.put(o, BaseTools.convert(rtn));
            } else if ("weblogic.jdbc.wrapper.Clob_oracle_sql_CLOB".equals(o1.getClass().getName())) {
//                System.out.println(">>>>>>>>>>>>>weblogic.jdbc.wrapper.Clob_oracle_sql_CLOB");
                String rtn = "";
                InputStream input = null;
                try {
                    Method method = o1.getClass().getMethod("getVendorObj", new Class[]{});
                    oracle.sql.CLOB clob = (oracle.sql.CLOB) method.invoke(o1, new Object[]{});
                    input = clob.getAsciiStream();
                    int len = (int) clob.length();
                    byte[] by = new byte[len];
                    int i;
                    while (-1 != (i = input.read(by, 0, by.length))) {
                        input.read(by, 0, i);
                    }
                    rtn = new String(by);
                    rtn = clob.getSubString((long) 1, (int) clob.length());

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null)
                            input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return super.put(o, BaseTools.convert(rtn));
            }

        }
        return super.put(o, o1);
    }
}
