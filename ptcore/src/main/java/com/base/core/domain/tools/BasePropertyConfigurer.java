package com.base.core.domain.tools;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <ol>
 * <li>创建文档 Date: 2014-08-30</li>
 * <li>在spring中加载配置文件，代码方式获取properties的工具类，统一、简化了配置文件的读取使用</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 1.0
 * @since 1.6
 */
public class BasePropertyConfigurer extends PropertyPlaceholderConfigurer {
    private static Map<String, Object> ctxPropertiesMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {
        super.processProperties(beanFactory, props);
        //加载 properties to ctxPropertiesMap
        ctxPropertiesMap = new HashMap<String, Object>();

        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
//            System.out.println(keyStr+"="+value);
            try {
                if (value.length() != 0
                        && BaseTools.getEncoding(value).equals("ISO-8859-1")) {
                    value = new String(value.getBytes("ISO8859_1"), "GBK");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

//            System.out.println(keyStr+"="+value);
            ctxPropertiesMap.put(keyStr, value);
        }
    }

    /**
     * 获取spring加载后的配置文件对应的map
     *
     * @return 配置文件值对对象
     */
    public static Map getMapProperties() {
        return ctxPropertiesMap;
    }

    /**
     * 获取使用spring加载的配置文件中的属性
     *
     * @param key 键
     * @return 对应的值
     */
    public static Object getCntProperty(String key) {
        return ctxPropertiesMap.get(key);
    }
}
