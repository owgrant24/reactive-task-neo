package com.example.reactive.service;

import com.example.reactive.util.WordUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class DemoService {

    public String getMessage() {
        return WordUtil.generateWord();
    }

    public Mono<String> getAsyncMessage() {
        return Mono.just(WordUtil.generateWord());
    }

    public Mono<String> getMessageWithBlockedService() {
        Scheduler scheduler = Schedulers.newBoundedElastic(5, 10, "MyThreadGroup");
        return Mono.just(WordUtil.generateWord())
                .delayElement(Duration.ofSeconds(5), scheduler);
    }
}
