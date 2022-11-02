package com.example.springboot;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VarianceController {

    public static double mean(List<Double> data) {
        double val = 0;
        for (int i = 0; i < data.size(); ++i)
            val += data.get(i);
        return val / data.size();
    }

    private Mono<List<Double>> getNumbers(double mu, double sigma) {
        final Random random = new Random();
        double rangeMax = Math.sqrt(12) * sigma / 2 + mu;
        double rangeMin = -Math.sqrt(12) * sigma / 2 + mu;

        double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();

        return Flux.range(0, 1000).flatMap(integer ->
                Mono.just(random.nextDouble())
        ).collectList();
    }

    private Mono<Double> calculateVariance(List<Double> data) {
        double mean = mean(data);
        double sum = 0;
        double diff = 0;
        for (Double d : data) {
            diff = d - mean;
            sum += (diff * diff);
        }
        return Mono.just(sum / data.size());
    }

    @PostMapping("/variance")
    public Flux<String> variance() throws InterruptedException {
        Flux.just(1).flatMap(integer -> getNumbers(0, 0.016))
                .doOnNext(doubles -> calculateVariance(doubles).subscribe(aDouble -> System.out.println("variance - all: " + aDouble)))
                .flatMap(doubles -> Flux.fromIterable(Lists.partition(doubles, 500)))
                .flatMap(doubles -> calculateVariance(doubles))
                .doOnNext(aDouble -> System.out.println("variance: " + aDouble))
                .collect(Collectors.averagingDouble(aDouble -> aDouble))
                .doOnNext(aDouble -> System.out.println("final - variance of mean: " + aDouble))
                .subscribe();
        return Flux.just("nevim");
    }
}



