package com.qoder.mall.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 结构化日志工具类
 * 
 * 功能特性：
 * 1. 结构化JSON输出
 * 2. 自动注入traceId/spanId/requestId（从MDC）
 * 3. 敏感信息自动脱敏（手机号、身份证、邮箱）
 * 4. 统一业务码与错误码字段
 * 5. 记录耗时与关键入参摘要
 * 6. 提供info/warn/error/audit方法
 * 7. 支持异步输出（通过Logback AsyncAppender）
 * 8. 支持惰性参数（Supplier）
 */
public class LogKit {

    private static final Logger logger = LoggerFactory.getLogger(LogKit.class);
    private static final DateTimeFormatter ISO_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .withZone(ZoneId.of("UTC"));

    /**
     * 记录INFO级别日志
     * 
     * @param bizCode 业务码
     * @param message 日志消息
     * @param params 参数键值对（key1, value1, key2, value2, ...）
     */
    public static void info(String bizCode, String message, Object... params) {
        if (logger.isInfoEnabled()) {
            LogEntry entry = buildLogEntry("INFO", bizCode, null, message, null, null, params);
            logger.info(entry.toJsonString());
        }
    }

    /**
     * 记录INFO级别日志（支持惰性参数）
     * 
     * @param bizCode 业务码
     * @param message 日志消息
     * @param paramsSupplier 参数供应器（仅在日志级别启用时调用）
     */
    public static void info(String bizCode, String message, Supplier<Object[]> paramsSupplier) {
        if (logger.isInfoEnabled()) {
            Object[] params = paramsSupplier != null ? paramsSupplier.get() : new Object[0];
            info(bizCode, message, params);
        }
    }

    /**
     * 记录WARN级别日志
     * 
     * @param bizCode 业务码
     * @param message 日志消息
     * @param params 参数键值对
     */
    public static void warn(String bizCode, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            LogEntry entry = buildLogEntry("WARN", bizCode, null, message, null, null, params);
            logger.warn(entry.toJsonString());
        }
    }

    /**
     * 记录WARN级别日志（支持惰性参数）
     */
    public static void warn(String bizCode, String message, Supplier<Object[]> paramsSupplier) {
        if (logger.isWarnEnabled()) {
            Object[] params = paramsSupplier != null ? paramsSupplier.get() : new Object[0];
            warn(bizCode, message, params);
        }
    }

    /**
     * 记录ERROR级别日志
     * 
     * @param bizCode 业务码
     * @param message 日志消息
     * @param throwable 异常对象
     * @param params 参数键值对
     */
    public static void error(String bizCode, String message, Throwable throwable, Object... params) {
        if (logger.isErrorEnabled()) {
            String errorCode = extractErrorCode(params);
            LogEntry entry = buildLogEntry("ERROR", bizCode, errorCode, message, throwable, null, params);
            logger.error(entry.toJsonString());
        }
    }

    /**
     * 记录ERROR级别日志（无异常）
     */
    public static void error(String bizCode, String message, Object... params) {
        error(bizCode, message, null, params);
    }

    /**
     * 记录ERROR级别日志（支持惰性参数）
     */
    public static void error(String bizCode, String message, Throwable throwable, Supplier<Object[]> paramsSupplier) {
        if (logger.isErrorEnabled()) {
            Object[] params = paramsSupplier != null ? paramsSupplier.get() : new Object[0];
            error(bizCode, message, throwable, params);
        }
    }

    /**
     * 记录审计日志（AUDIT级别，使用INFO输出）
     * 审计日志用于记录重要业务操作，如支付、登录、权限变更等
     * 
     * @param bizCode 业务码
     * @param action 操作动作
     * @param params 参数键值对（建议包含userId、costMs等）
     */
    public static void audit(String bizCode, String action, Object... params) {
        if (logger.isInfoEnabled()) {
            Long costMs = extractCostMs(params);
            LogEntry entry = buildLogEntry("AUDIT", bizCode, null, action, null, costMs, params);
            logger.info(entry.toJsonString());
        }
    }

    /**
     * 记录审计日志（支持惰性参数）
     */
    public static void audit(String bizCode, String action, Supplier<Object[]> paramsSupplier) {
        if (logger.isInfoEnabled()) {
            Object[] params = paramsSupplier != null ? paramsSupplier.get() : new Object[0];
            audit(bizCode, action, params);
        }
    }

    /**
     * 构建日志条目对象
     */
    private static LogEntry buildLogEntry(String level, String bizCode, String errorCode,
                                          String message, Throwable throwable, Long costMs,
                                          Object... params) {
        LogEntry entry = new LogEntry();
        entry.setTimestamp(ISO_FORMATTER.format(Instant.now()));
        entry.setLevel(level);
        entry.setTraceId(MDC.get("traceId"));
        entry.setSpanId(MDC.get("spanId"));
        entry.setRequestId(MDC.get("requestId"));
        entry.setBizCode(bizCode);
        entry.setErrorCode(errorCode);
        entry.setMessage(message);
        entry.setThread(Thread.currentThread().getName());

        // 设置调用者信息
        StackTraceElement caller = getCallerStackTrace();
        if (caller != null) {
            entry.setClassName(caller.getClassName());
            entry.setMethodName(caller.getMethodName());
        }

        // 解析参数
        if (params != null && params.length > 0) {
            Map<String, Object> paramMap = parseParams(params);
            
            // 提取costMs（如果在参数中）
            if (costMs == null && paramMap.containsKey("costMs")) {
                Object costValue = paramMap.get("costMs");
                if (costValue instanceof Number) {
                    entry.setCostMs(((Number) costValue).longValue());
                    paramMap.remove("costMs");
                }
            } else if (costMs != null) {
                entry.setCostMs(costMs);
            }

            // 脱敏处理
            paramMap = maskSensitiveParams(paramMap);
            
            if (!paramMap.isEmpty()) {
                entry.setParams(paramMap);
            }
        }

        // 处理异常堆栈
        if (throwable != null) {
            entry.setException(getStackTraceAsString(throwable));
        }

        return entry;
    }

    /**
     * 解析参数数组为Map（key1, value1, key2, value2, ...）
     */
    private static Map<String, Object> parseParams(Object... params) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        
        if (params.length % 2 != 0) {
            paramMap.put("_raw_params", params);
            return paramMap;
        }

        for (int i = 0; i < params.length; i += 2) {
            String key = String.valueOf(params[i]);
            Object value = params[i + 1];
            
            // 支持惰性参数
            if (value instanceof Supplier) {
                value = ((Supplier<?>) value).get();
            }
            
            paramMap.put(key, value);
        }
        
        return paramMap;
    }

    /**
     * 对参数进行敏感信息脱敏
     */
    private static Map<String, Object> maskSensitiveParams(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return params;
        }

        Map<String, Object> maskedParams = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value == null) {
                maskedParams.put(key, null);
                continue;
            }

            String valueStr = String.valueOf(value);
            
            // 根据key名称判断是否需要脱敏
            String lowerKey = key.toLowerCase();
            if (lowerKey.contains("phone") || lowerKey.contains("mobile") || lowerKey.contains("tel")) {
                maskedParams.put(key, SensitiveDataMasker.maskPhone(valueStr));
            } else if (lowerKey.contains("idcard") || lowerKey.contains("id_card") || lowerKey.contains("identity")) {
                maskedParams.put(key, SensitiveDataMasker.maskIdCard(valueStr));
            } else if (lowerKey.contains("email") || lowerKey.contains("mail")) {
                maskedParams.put(key, SensitiveDataMasker.maskEmail(valueStr));
            } else if (SensitiveDataMasker.isSensitive(valueStr)) {
                // 自动检测并脱敏
                maskedParams.put(key, SensitiveDataMasker.autoMask(valueStr));
            } else {
                maskedParams.put(key, value);
            }
        }
        
        return maskedParams;
    }

    /**
     * 从参数中提取errorCode
     */
    private static String extractErrorCode(Object... params) {
        if (params == null || params.length < 2) {
            return null;
        }
        
        for (int i = 0; i < params.length - 1; i += 2) {
            String key = String.valueOf(params[i]);
            if ("errorCode".equals(key) || "errCode".equals(key)) {
                return String.valueOf(params[i + 1]);
            }
        }
        
        return null;
    }

    /**
     * 从参数中提取costMs
     */
    private static Long extractCostMs(Object... params) {
        if (params == null || params.length < 2) {
            return null;
        }
        
        for (int i = 0; i < params.length - 1; i += 2) {
            String key = String.valueOf(params[i]);
            if ("costMs".equals(key) || "cost".equals(key)) {
                Object value = params[i + 1];
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
            }
        }
        
        return null;
    }

    /**
     * 获取调用者堆栈信息（跳过LogKit自身的堆栈）
     */
    private static StackTraceElement getCallerStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // 跳过getStackTrace、getCallerStackTrace、buildLogEntry和LogKit的公共方法
        for (int i = 0; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!className.equals(LogKit.class.getName()) 
                    && !className.equals(Thread.class.getName())) {
                return stackTrace[i];
            }
        }
        
        return null;
    }

    /**
     * 将异常堆栈转换为字符串
     */
    private static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
