package com.qoder.mall.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class OrderNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderNoGenerator() {}

    public static String generate(Long userId) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String userPart = String.format("%04d", userId % 10000);
        String randomPart = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return timestamp + userPart + randomPart;
    }
}
