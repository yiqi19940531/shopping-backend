package com.qoder.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.qoder.mall.mapper")
@EnableAsync
public class ShoppingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingBackendApplication.class, args);
    }
}
