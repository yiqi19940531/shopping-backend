package com.qoder.mall.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.Map;

/**
 * 结构化日志条目对象
 * 用于构建JSON格式的日志输出
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntry {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 时间戳（ISO-8601格式）
     */
    private String timestamp;

    /**
     * 日志级别：INFO, WARN, ERROR, AUDIT
     */
    private String level;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 跨度ID
     */
    private String spanId;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 业务码
     */
    private String bizCode;

    /**
     * 错误码（可选）
     */
    private String errorCode;

    /**
     * 日志消息
     */
    private String message;

    /**
     * 耗时（毫秒，可选）
     */
    private Long costMs;

    /**
     * 参数摘要
     */
    private Map<String, Object> params;

    /**
     * 异常堆栈（仅error级别）
     */
    private String exception;

    /**
     * 线程名
     */
    private String thread;

    /**
     * 调用类名
     */
    private String className;

    /**
     * 调用方法名
     */
    private String methodName;

    /**
     * 转换为JSON字符串
     */
    public String toJsonString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return String.format("{\"error\":\"Failed to serialize log entry: %s\"}", e.getMessage());
        }
    }

    /**
     * 转换为格式化的JSON字符串（带缩进）
     */
    public String toPrettyJsonString() {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return String.format("{\"error\":\"Failed to serialize log entry: %s\"}", e.getMessage());
        }
    }
}
