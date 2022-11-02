package com.example.springboot.db;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FrontaMemberRepository extends ReactiveCrudRepository<Fronta0, Long> {

    Flux<Fronta0> findByStatus(String status);

}
