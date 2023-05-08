package com.itonse.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();    // Thread Pool 생성
        int n = Runtime.getRuntime().availableProcessors();  // 코어개수
        threadPool.setPoolSize(n);   // 코어개수로 스레드풀 사이즈 설정
        threadPool.initialize();

        taskRegistrar.setTaskScheduler(threadPool);   // 스케쥴러에서 스레드풀 사용
    }
}
