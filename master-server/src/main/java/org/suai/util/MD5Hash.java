package org.suai.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {
    public static String getHash(String str) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(str.getBytes(StandardCharsets.UTF_8));
            String res = new BigInteger(1, m.digest()).toString(16);
            StringBuilder sb = new StringBuilder(32);
            for (int i = 0, count = 32 - res.length(); i < count; i++) {
                sb.append("0");
            }

            return sb.append(res).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
