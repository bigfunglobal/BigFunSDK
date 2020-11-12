package com.bigfun.tm.encrypt;


import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


public class RSAEncrypt {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjHfZczWx3pKpUepg9xh70CroHFTlvdz6to6rDl3w0/e1qUZByh/vB7HVD2wQ32bPks/1l2WDvMcmup50qbR145x9FxZJmCeIVNJLH018mLOUcFfH49TjdoVFJIkzjimboNGCCo/mxdiUehuee/3pYpZ3yOS4l1IBAs5f/mHybCwIDAQAB";
    //    public static String privateKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjHfZczWx3pKpUepg9xh70CroHFTlvdz6to6rDl3w0/e1qUZByh/vB7HVD2wQ32bPks/1l2WDvMcmup50qbR145x9FxZJmCeIVNJLH018mLOUcFfH49TjdoVFJIkzjimboNGCCo/mxdiUehuee/3pYpZ3yOS4l1IBAs5f/mHybCwIDAQAB";

    public static void main(TextView tv) throws Exception {
        RSAEncrypt rsa = new RSAEncrypt();
        String key = randomNum();//aes 加密数据 随机32位key
        String params = AES2561.encrypt("数据", key);
        String aos = rsa.encrypt(key, getPublicKey(publicKey));// 随机生成key rsa加密
    }

    public static String randomNum() {
        return "";
    }

    /**
     * 将base64编码后的公钥字符串转成PublicKey实例
     *
     * @param publicKey 公钥字符
     * @return publicKEY
     * @throws Exception exception
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 将base64编码后的私钥字符串转成PrivateKey实例
     *
     * @param privateKey 私钥字符串
     * @return 私钥对象
     * @throws Exception exception
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * RSA加密
     *
     * @param content   待加密文本
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception exception
     */
    public static String encrypt(String content, PublicKey publicKey) throws Exception {
        String encode = Base64.encode(content.getBytes());

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//java默认"RSA"="RSA/ECB/PKCS1Padding"RSA/None/PKCS1Padding
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] data = encode.getBytes();
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return new String(Base64.encode(encryptedData));
    }

    public static String encrypt(String content, PrivateKey privateKey) throws Exception {
        String encode = Base64.encode(content.getBytes());

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//java默认"RSA"="RSA/ECB/PKCS1Padding"RSA/None/PKCS1Padding
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] data = encode.getBytes();
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return new String(Base64.encode(encryptedData));
    }

    /**
     * RSA解密
     *
     * @param content    密文
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception exception
     */
    public static String decrypt(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedData = Base64.decode(content);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, "UTF-8");
    }
}
