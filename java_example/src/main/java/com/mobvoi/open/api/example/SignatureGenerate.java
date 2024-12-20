package com.mobvoi.open.api.example;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author ：mobvoi
 * Date: 2024/12/19
 * Time: 14:32
 * Description:Signature生成的工具类
 */

public class SignatureGenerate {
    private static final String appkey = "你的appkey";
    private static final String secret = "你的secret";
    public static void main(String[] args) throws Exception{

        long current = System.currentTimeMillis();
        String timestamp = String.valueOf(current / 1000);
        String signature = md5(appkey, secret, timestamp);
        System.out.println("appkey="+appkey);
        System.out.println("timestamp="+timestamp);
        System.out.println("signature="+signature);
    }

    private static String md5(String appkey, String secret, String timestamp) {
        String plainText = String.join("+", appkey, secret, timestamp);
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("not found the md5");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
