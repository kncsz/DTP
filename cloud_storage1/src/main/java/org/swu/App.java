package org.swu;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        System.out.println("service started");
        // 开启服务
        SpringApplication.run(App.class, args);
    }

//    @Bean
//    public WebMvcConfigurer webMvcConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // 允许所有路径
//                        .allowedOrigins("*") // 允许所有来源
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
//                        .allowCredentials(false) // 是否允许携带凭证
//                        .maxAge(3600); // 预检请求的缓存时间（秒）
//            }
//
//            @Override
//            public void configureContentNegotiation(@NotNull ContentNegotiationConfigurer configurer) {
//                configurer.defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON); // 设置默认返回类型为 JSON
//            }
//        };
//    }
}
