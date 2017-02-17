package com.base.core.web;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * <ol>
 * <li>创建文档 2014-12-19</li>
 * <li>用于生成一个唯一的ID，来防止CSRF攻击(Cross Site Request Forgery)</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.0
 * @since 1.6
 */
public class CsrfToken {
    public static final String DEFAULT_TOKEN_KEY = "x-csrf-token";
    public static final int DEFAULT_MAX_TOKENS = 8;
    public static final String CSRF_TOKEN_SEPARATOR = "/";

    /**
     * 获取csrfToken储存主键
     *
     * @return 返回储存主键
     */
    public static String getKey() {
        String key = JsonHelp.getPropertiesByKey("CSRF_TOKEN_KEY");
        return StringUtils.isBlank(key) ? DEFAULT_TOKEN_KEY : key;
    }

    /**
     * 获取允许的最大token数量
     *
     * @return 返回最大token数量
     */
    public static int getMaxTokens() {
        String key = JsonHelp.getPropertiesByKey("CSRF_TOKEN_MAX");
        return StringUtils.isBlank(key) ? DEFAULT_MAX_TOKENS : Integer.parseInt(key);
    }

    /**
     * 设置csrfToken字符串
     *
     * @return 返回csrfToken字符串
     */
    public static String setCsrfTokenInSession() {
        String token = generateCsrfToken();
        setCsrfTokensInSession(token);
        System.out.println("set " + getKey() + "=" + token);
        return token;
    }

    /**
     * 生成MD5加密 返回十六进制的字符串
     *
     * @return 返回加密后的csrfToken字符串
     */
    public static String generateCsrfToken() {
        HttpSession session = JsonHelp.getRequest().getSession();
        return DigestUtils.md5Hex(session.getId()
                + session.getCreationTime());
    }

    /**
     * 查询当前的csrfToken值
     *
     * @return 返回当前的值
     */
    public static String getCsrfTokenInSession() {
        return generateCsrfToken();
    }

    /**
     * 在session中保存csrfToken(token的寿命和session相同)
     *
     * @param csrfToken token
     */
    public static void setCsrfTokensInSession(String csrfToken) {
        JsonHelp.getRequest().getSession().setAttribute(getKey(), csrfToken);
    }

    public static void removeCsrfTokenInSession() {
        JsonHelp.getRequest().getSession().removeAttribute(getKey());
    }

    /**
     * 从session中获取tokens列表(token的寿命和session相同)
     *
     * @return 返回tokens列表
     */
    public static String getCsrfTokensInSession() {
        return (String) JsonHelp.getRequest().getSession().getAttribute(getKey());
    }
}
