//Copyright 2020 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.tool;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author qhsong
 * @since 2020-07-21
 */
public class SignatureUtil {

  public static String getSignature(String appkey, String secret, String timestamp) {
    String raw = String.join("+", appkey, secret, timestamp);
    return md5(raw);
  }

  private static String md5(String plainText) {
    byte[] secretBytes = null;
    try {
      secretBytes = MessageDigest.getInstance("md5").digest(
          plainText.getBytes());
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
