package com.base.core.domain.tools;

/**
 * 定义密码加密接口，针对不同的系统可以选择不同加密机制
 */
public interface IEncrypt {

    /**
     * 对src字符串进行加密
     * @param src
     * @return 加密后的字符串
     */
    public String encode(String src);
}
