package com.qoder.mall.common.util;

import java.util.function.Supplier;

/**
 * LogKit使用示例
 * 
 * 本类展示了LogKit日志工具的各种使用场景和最佳实践
 */
public class LogKitExample {

    /**
     * 示例1：基本的INFO日志
     */
    public void example1_BasicInfo() {
        LogKit.info("USER_LOGIN", "用户登录", 
                "userId", 12345L, 
                "username", "zhangsan",
                "ip", "192.168.1.100");
    }

    /**
     * 示例2：带耗时的审计日志
     */
    public void example2_AuditWithCost() {
        long startTime = System.currentTimeMillis();
        
        // 执行业务逻辑
        doSomeBusinessLogic();
        
        long costMs = System.currentTimeMillis() - startTime;
        LogKit.audit("ORDER_CREATE", "创建订单", 
                "userId", 12345L,
                "orderNo", "OD202402230001",
                "amount", 299.99,
                "costMs", costMs);
    }

    /**
     * 示例3：敏感信息自动脱敏
     */
    public void example3_SensitiveDataMasking() {
        // 手机号会自动脱敏为：138****5678
        LogKit.info("USER_REGISTER", "用户注册",
                "phone", "13812345678",
                "email", "zhangsan@example.com",
                "idCard", "110101199001011234");
        
        // 输出JSON示例：
        // {"phone": "138****5678", "email": "z****@example.com", "idCard": "110101********1234"}
    }

    /**
     * 示例4：WARN级别日志
     */
    public void example4_WarnLog() {
        LogKit.warn("STOCK_LOW", "库存不足警告",
                "productId", 100L,
                "productName", "iPhone 15",
                "currentStock", 5,
                "threshold", 10);
    }

    /**
     * 示例5：ERROR级别日志（带异常）
     */
    public void example5_ErrorWithException() {
        try {
            // 可能抛出异常的代码
            riskyOperation();
        } catch (Exception e) {
            LogKit.error("DB_ERROR", "数据库操作失败", e,
                    "errorCode", "DB001",
                    "operation", "insertOrder",
                    "orderId", 123456L);
        }
    }

    /**
     * 示例6：惰性参数（避免不必要的计算）
     */
    public void example6_LazyParameters() {
        // 只有在DEBUG级别开启时，expensiveCalculation()才会被调用
        LogKit.info("CACHE_HIT", "缓存命中", () -> new Object[]{
                "key", "user:12345",
                "value", expensiveCalculation(),  // 惰性计算
                "ttl", 3600
        });
    }

    /**
     * 示例7：在Controller中使用
     */
    public void example7_InController() {
        long startTime = System.currentTimeMillis();
        
        try {
            // 处理请求
            String result = processRequest();
            
            long costMs = System.currentTimeMillis() - startTime;
            LogKit.info("API_SUCCESS", "接口调用成功",
                    "api", "/api/orders/create",
                    "costMs", costMs);
                    
        } catch (Exception e) {
            long costMs = System.currentTimeMillis() - startTime;
            LogKit.error("API_ERROR", "接口调用失败", e,
                    "api", "/api/orders/create",
                    "errorCode", "API001",
                    "costMs", costMs);
        }
    }

    /**
     * 示例8：链路追踪信息自动注入
     * 
     * TraceFilter会自动为每个请求生成traceId/spanId/requestId
     * 这些信息会自动注入到每条日志中，无需手动传递
     */
    public void example8_AutoTracing() {
        // 日志会自动包含：
        // {
        //   "traceId": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
        //   "spanId": "1",
        //   "requestId": "1708675200000000001",
        //   "bizCode": "PAYMENT_PROCESS",
        //   "message": "处理支付"
        // }
        LogKit.info("PAYMENT_PROCESS", "处理支付",
                "orderId", 123456L,
                "amount", 299.99);
    }

    /**
     * 示例9：JSON结构化输出
     * 
     * LogKit输出的所有日志都是标准JSON格式，便于日志收集系统（如ELK）解析
     */
    public void example9_JsonOutput() {
        LogKit.info("DATA_SYNC", "数据同步完成",
                "table", "tb_order",
                "rows", 1000,
                "costMs", 2500L);
        
        // 输出JSON示例：
        // {
        //   "timestamp": "2024-02-23T08:30:00.123Z",
        //   "level": "INFO",
        //   "traceId": "xxx",
        //   "spanId": "1",
        //   "requestId": "xxx",
        //   "bizCode": "DATA_SYNC",
        //   "message": "数据同步完成",
        //   "costMs": 2500,
        //   "params": {
        //     "table": "tb_order",
        //     "rows": 1000
        //   },
        //   "thread": "http-nio-8080-exec-1",
        //   "className": "com.qoder.mall.service.impl.SyncServiceImpl",
        //   "methodName": "syncOrderData"
        // }
    }

    /**
     * 示例10：在异步方法中使用
     * 
     * 注意：异步方法中MDC会被清空，需要手动传递链路信息
     * 或使用支持MDC传递的异步框架（如Spring的@Async）
     */
    public void example10_AsyncMethod() {
        // 在异步方法中，TraceFilter的MDC可能已清空
        // 但LogKit仍然可以正常工作，只是traceId等字段可能为空
        LogKit.info("ASYNC_TASK", "异步任务执行",
                "taskId", "TASK001",
                "status", "processing");
    }

    // ===========================================
    // 辅助方法
    // ===========================================

    private void doSomeBusinessLogic() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void riskyOperation() throws Exception {
        throw new RuntimeException("模拟异常");
    }

    private String expensiveCalculation() {
        // 模拟耗时计算
        return "calculated_value";
    }

    private String processRequest() {
        return "success";
    }
}
