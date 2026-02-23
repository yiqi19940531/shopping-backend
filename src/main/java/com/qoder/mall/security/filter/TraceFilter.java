package com.qoder.mall.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 链路追踪过滤器
 * 
 * 功能：
 * 1. 为每个请求生成唯一的traceId（UUID去横线）
 * 2. 生成requestId（时间戳+序列号）
 * 3. 初始化spanId为1
 * 4. 将ID写入MDC，供日志系统使用
 * 5. 请求结束时清理MDC
 * 
 * 优先级：HIGHEST_PRECEDENCE，确保在所有其他过滤器之前执行
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String REQUEST_ID = "requestId";
    private static final AtomicLong REQUEST_SEQUENCE = new AtomicLong(0);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 生成traceId（UUID去横线）
            String traceId = generateTraceId(request);
            
            // 生成requestId
            String requestId = generateRequestId();
            
            // 初始化spanId
            String spanId = "1";

            // 写入MDC
            MDC.put(TRACE_ID, traceId);
            MDC.put(SPAN_ID, spanId);
            MDC.put(REQUEST_ID, requestId);

            // 将traceId写入响应头，方便前端关联
            response.setHeader("X-Trace-Id", traceId);
            response.setHeader("X-Request-Id", requestId);

            // 继续过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 清理MDC，避免内存泄漏
            MDC.clear();
        }
    }

    /**
     * 生成traceId
     * 优先从请求头获取（支持分布式追踪），否则生成新的UUID
     */
    private String generateTraceId(HttpServletRequest request) {
        // 尝试从请求头获取（支持上游服务传递traceId）
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId != null && !traceId.isEmpty()) {
            return traceId;
        }
        
        // 生成新的traceId（UUID去横线）
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成requestId（时间戳 + 6位序列号）
     * 格式：yyyyMMddHHmmssSSS + 6位递增序列号
     */
    private String generateRequestId() {
        long sequence = REQUEST_SEQUENCE.incrementAndGet() % 1000000;
        long timestamp = System.currentTimeMillis();
        return String.format("%d%06d", timestamp, sequence);
    }

    /**
     * 增加spanId（用于嵌套调用追踪）
     * 外部可以调用此方法来增加spanId
     */
    public static void incrementSpanId() {
        String currentSpanId = MDC.get(SPAN_ID);
        if (currentSpanId != null) {
            try {
                int spanId = Integer.parseInt(currentSpanId);
                MDC.put(SPAN_ID, String.valueOf(spanId + 1));
            } catch (NumberFormatException e) {
                // 如果解析失败，保持原值
            }
        }
    }

    /**
     * 获取当前traceId
     */
    public static String getCurrentTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 获取当前spanId
     */
    public static String getCurrentSpanId() {
        return MDC.get(SPAN_ID);
    }

    /**
     * 获取当前requestId
     */
    public static String getCurrentRequestId() {
        return MDC.get(REQUEST_ID);
    }
}
