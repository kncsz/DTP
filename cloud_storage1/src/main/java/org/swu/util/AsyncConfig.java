//package org.swu.util;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Configuration
//@EnableAsync
//public class AsyncConfig {
//
//    @Bean(name = "fileUploadExecutor")
//    public Executor fileUploadExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(8); // 核心线程数
//        executor.setMaxPoolSize(16); // 最大线程数
//        executor.setQueueCapacity(50); // 队列容量
//        executor.setKeepAliveSeconds(60); // 线程空闲存活时间
//        executor.setThreadNamePrefix("FileUploadExecutor-"); // 线程名称前缀
//
//        // 拒绝策略：由调用者线程执行任务
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//
//        executor.initialize();
//        return executor;
//    }
//}
