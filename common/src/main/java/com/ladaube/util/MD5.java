package com.ladaube.util;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

  public static String get(File f) throws NoSuchAlgorithmException, IOException {
    InputStream is = new FileInputStream(f);
    MessageDigest digest = MessageDigest.getInstance("MD5");
    byte[] buffer = new byte[8192];
    int read = 0;
    try {
      while( (read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      return bigInt.toString(16);
    } finally {
      is.close();
    }
  }
}
