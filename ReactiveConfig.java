package com.tradingbot.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


@Configuration
public class ReactiveConfig {
@Bean(name = "blockingScheduler")
public Scheduler blockingScheduler() {
return Schedulers.newBoundedElastic(64, Integer.MAX_VALUE, "blocking");
}
}