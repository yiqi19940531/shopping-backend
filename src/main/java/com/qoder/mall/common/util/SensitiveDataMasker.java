package com.qoder.mall.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感信息脱敏工具类
 * 支持手机号、身份证、邮箱等敏感字段的自动脱敏
 */
public class SensitiveDataMasker {

    private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\d{17}[\\dXx]|\\d{15}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    /**
     * 手机号脱敏：保留前3后4，中间4位星号
     * 例如：13812345678 -> 138****5678
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 身份证脱敏：保留前6后4，中间星号
     * 例如：110101199001011234 -> 110101********1234
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        int length = idCard.length();
        int maskLength = length - 10;
        StringBuilder masked = new StringBuilder(idCard.substring(0, 6));
        for (int i = 0; i < maskLength; i++) {
            masked.append("*");
        }
        masked.append(idCard.substring(length - 4));
        return masked.toString();
    }

    /**
     * 邮箱脱敏：保留@前1位和@后域名，中间星号
     * 例如：example@email.com -> e****@email.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return email;
        }
        String prefix = email.substring(0, 1);
        String suffix = email.substring(atIndex);
        return prefix + "****" + suffix;
    }

    /**
     * 自动识别并脱敏文本中的敏感信息
     * 识别顺序：手机号 -> 身份证 -> 邮箱
     */
    public static String autoMask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String result = text;

        // 脱敏手机号
        Matcher phoneMatcher = PHONE_PATTERN.matcher(result);
        StringBuffer phoneBuffer = new StringBuffer();
        while (phoneMatcher.find()) {
            phoneMatcher.appendReplacement(phoneBuffer, maskPhone(phoneMatcher.group()));
        }
        phoneMatcher.appendTail(phoneBuffer);
        result = phoneBuffer.toString();

        // 脱敏身份证
        Matcher idCardMatcher = ID_CARD_PATTERN.matcher(result);
        StringBuffer idCardBuffer = new StringBuffer();
        while (idCardMatcher.find()) {
            idCardMatcher.appendReplacement(idCardBuffer, maskIdCard(idCardMatcher.group()));
        }
        idCardMatcher.appendTail(idCardBuffer);
        result = idCardBuffer.toString();

        // 脱敏邮箱
        Matcher emailMatcher = EMAIL_PATTERN.matcher(result);
        StringBuffer emailBuffer = new StringBuffer();
        while (emailMatcher.find()) {
            emailMatcher.appendReplacement(emailBuffer, maskEmail(emailMatcher.group()));
        }
        emailMatcher.appendTail(emailBuffer);
        result = emailBuffer.toString();

        return result;
    }

    /**
     * 判断字符串是否为敏感信息
     */
    public static boolean isSensitive(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(text).find()
                || ID_CARD_PATTERN.matcher(text).find()
                || EMAIL_PATTERN.matcher(text).find();
    }
}
