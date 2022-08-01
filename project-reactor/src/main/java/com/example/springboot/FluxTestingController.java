package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FluxTestingController {

    private List<Mono<?>> simulate() {

        return List.of("1", "2", "3", "4").stream().map(s ->
                {
                    System.out.println("current value: " + s + ", thread: " + Thread.currentThread().getName());
                    if (s.equals("2")) {
                        return Mono.error(new RuntimeException("my custom exception for member of value " + s));
                    } else {
                        return Mono.just(s);
                    }
                }
        ).collect(Collectors.toList());

    }

    @PostMapping("/flux-concat")
    public Mono<List<?>> smazat() {

        return Flux.concat(simulate()).doOnNext(o -> System.out.println("print: " + o)).collect(Collectors.toList());
    }

}
