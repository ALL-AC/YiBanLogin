package com.isevergreen.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author JIANG
 * @since 2020-09-03
 */
public class RsaUtil {
    public static String CHARSET = "utf-8";

    /**
     * 公钥字符串转PublicKey实例
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 公钥加密
     */
    public static byte[] encryptByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }


    public static String encryptByPublicKey(String content, String publicKey) throws Exception {
        return new String(Base64.getEncoder().encode(encryptByPublicKey(content.getBytes(CHARSET), getPublicKey(publicKey))));

    }

    public static void main(String[] args) throws Exception {
        System.out.println(encryptByPublicKey("zxc13800138000", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgkQk14atd8euhXfJI8FHjGSHRgbKofvYWwaPHdYvO4uV/fObMKQhiidfTWCvIpr68Om3+EQDSEXsfpHRTk0z50u4JgC9wvkE2z9Ty4uDu2mycklDhPvka1R+aewqN4Vx0azzLuEzvm3adwZqtT05l7LrlvcCMvQUoHuAUBAeMIQIDAQAB"));
    }
}
