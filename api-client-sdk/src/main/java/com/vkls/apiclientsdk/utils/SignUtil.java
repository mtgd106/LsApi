package com.vkls.apiclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

//body为用户参数，secretKey为密钥
//通过算法生成不可解密的值用作签名
public class SignUtil {
    public static String getSign(String body, String secretKey) {
        String content = body + "." + secretKey;
        return DigestUtil.md5Hex(content);
    }
}
