package com.base.core.domain.tools.impl;


import com.base.core.domain.tools.EncryptTools;
import com.base.core.domain.tools.IEncrypt;

/**
 * MD5和SHA的加密
 */
public class EncryptMD5SHAImpl implements IEncrypt {

    /**
     * 加密方式，默认MD5，可选SHA
     */
    private String encodeType = "MD5";

    public String getEncodeType() {
        return encodeType;

    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;

    }

    /**
     * 对src字符串进行加密,默认MD5，通过调整encodeType=SHA,将算法改成SHA
     *
     * @param src
     * @return 加密后的字符串
     */
    public String encode(String src) {
        if ("SHA".equals(encodeType)) {
            return EncryptTools.encodeSHAString(src);
        } else {
            return EncryptTools.encodeMD5String(src);
        }
    }

}
