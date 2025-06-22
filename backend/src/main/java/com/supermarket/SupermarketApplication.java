package com.supermarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 超市管理系统启动类
 */
@SpringBootApplication
public class SupermarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermarketApplication.class, args);
        System.out.println("🏪 超市管理系统启动成功！");
        System.out.println("📍 访问地址: http://localhost:8080");
        System.out.println("📋 API文档: http://localhost:8080/doc.html");
    }
}