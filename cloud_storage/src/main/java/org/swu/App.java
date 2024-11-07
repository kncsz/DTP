package org.swu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        System.out.println("service started");
        // 开启服务
        SpringApplication.run(App.class, args);
    }
}