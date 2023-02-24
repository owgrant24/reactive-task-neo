package com.example.reactive.controller;

import com.example.reactive.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/v1/task")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    // 1. Обычный (синхронный) метод, возвращает строку
    @GetMapping("/1")
    public String getMessage() {
        return demoService.getMessage();
    }

    // 2. Реактивный метод, возвращает строку
    @GetMapping("/2")
    public Mono<String> getAsyncMessage() {
        return demoService.getAsyncMessage().log();
    }

    /* 3. Реактивный метод, возвращает строку, в процессе работы сервиса блокирует поток на 5 секунд.
     * Использовать при вызове scheduler
     */
    @GetMapping("/3")
    public Mono<String> getAsyncBlock5Message() {
        return demoService.getMessageWithBlockedService().log();
    }

    // 4. Реактивный метод, который внутри вызывает первый метод сервиса, получив ответ от первого, вызывает второй,
    // конкатенирует ответ от первого с ответом второго и возвращает полученную строку.
    @GetMapping("/4")
    public Mono<String> getConcatMessage() {
        Mono<String> str1 = Mono.just(demoService.getMessage())
                .subscribeOn(Schedulers.boundedElastic());
        Mono<String> str2 = demoService.getAsyncMessage()
                .subscribeOn(Schedulers.boundedElastic());
        return Mono.zip(str1, str2, (s, str) -> s.concat(" " + str)).log();
    }

    /* 5. Реактивный метод, который параллельно вызывает 5 методов сервиса, каждый из которых блокирует поток
     * на 5 секунд. Далее, дожидаемся ответа от всех 5, конкатенируем ответы в одну строку и возвращаем.
     * Общее время работы не больше 6 секунд.
     */
    @GetMapping("/5")
    public ParallelFlux<String> task5() {
        return Flux.range(1, 5)
                .flatMap(it -> demoService.getMessageWithBlockedService())
                .parallel()
                .log();
    }

    // 6. Реактивный метод, который внутри себя вызывает через Web Client 2-ой метод этого списка.
    @GetMapping("/6")
    public Mono<String> task6() {
        WebClient client = WebClient.create("http://localhost:8050");
        return client.get()
                .uri("/v1/task/2")
                .retrieve()
                .bodyToMono(String.class);
    }
}
