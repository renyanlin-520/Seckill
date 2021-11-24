package com.ren.seckill.utils;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

/**
 * @author ren
 * @version 1.0
 * @date 2021/11/5 15:04
 */
@Component
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    /**
     * 第一次加密
     * @param inputPass
     * @return
     */
    public static String inputPassToFromPass(String inputPass) {

        return md5("" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4));
    }

    /**
     * 第二次加密
     * @param fromPass
     * @return
     */
    public static String fromPassToDBPass(String fromPass, String salt) {
        return md5("" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4));
    }

    /**
     * 最终要存到数据库的密文
     * @param inputPass
     * @param salt
     * @return
     */
    public static String inputPassToDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
//        Double v = Math.random() * 32;
//        int i = v.intValue();
//        System.out.println(i);
//        String s = UUID.randomUUID().toString().replaceAll("-", "");
//        System.out.println(s + "-----" + s.length());
        System.out.println(inputPassToFromPass("12345"));
        System.out.println(fromPassToDBPass("2a19bfbf9f08385246a16079bdbe167b", "1a2b3c4d"));
        System.out.println(inputPassToDBPass("12345", "1a2b3c4d"));
    }
}
