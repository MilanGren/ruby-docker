package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NeutronsController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleAny(Exception e) {
        log.warn("Returning HTTP 400 Bad Request", e);
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void block() {
        try {
            TimeUnit.SECONDS.sleep(getRandomNumber(1, 3));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Flux<NeutronPair> generateNeutrons(int neutron) {

        int min = 1;
        int max = 4;

        int numberOfNeutrons = (int) ((Math.random() * (max - min)) + min);

        List<NeutronPair> list = new ArrayList<>();
        for (int ind = min; ind <= numberOfNeutrons; ind++) {
            list.add(new NeutronPair(neutron, ind));
        }

        block();

        return Flux.fromIterable(list);

    }


    @PostMapping("/neutrons")
    public Mono<String> neutrons(@RequestHeader(name = "Domain", required = false, defaultValue = "") String tenant) throws InterruptedException {

        List<Integer> neutrons = List.of(1, 2);
        var f = Flux.fromIterable(neutrons);

        List<Disposable> disposables = new ArrayList<>();

        for (Integer neutron : neutrons) {
            disposables.add(
                    Flux.just(neutron)
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap(integer -> generateNeutrons(integer))
                            .doOnNext(neutronPair -> neutronPair.text = "random text.. ")
                            .subscribe(neutronPair -> System.out.println(Thread.currentThread().getName() + ": " + neutronPair.toString()))

            );

        }

        if (true) {
            boolean allDisposed = false;
            while (!allDisposed) {
                if (!disposables.stream().filter(disposable -> !disposable.isDisposed()).findAny().isPresent()) {
                    allDisposed = true;
                    disposables.stream().forEach(disposable -> System.out.println("disposed: " + disposable.isDisposed()));
                }
            }
        }

        return Mono.just("all done");

    }

    public static class NeutronPair {

        public int parentNo;
        public int childNo;

        public String text;

        public NeutronPair(int parentNo, int childNo) {
            this.parentNo = parentNo;
            this.childNo = childNo;
        }

        public String toString() {
            return "parentNo: " + parentNo + ", childNo" + childNo + ", text: " + text;
        }

    }


}
