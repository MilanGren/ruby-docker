package com.example.springboot.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DatabaseController {

    private final FrontaMemberRepository repository;


    @PostMapping("/database-testing")
    public Flux<Fronta0> databaseTesting() {
        return repository.findByStatus("WAITING");
        //return repository.
    }

}
