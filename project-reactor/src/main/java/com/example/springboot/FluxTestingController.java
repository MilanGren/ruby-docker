package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Math.atan;
import static java.lang.Math.cbrt;
import static java.lang.Math.tan;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FluxTestingController {

    String var = "nevim";

/*    @Autowired
    private EmptyService emptyService;*/

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

    /*
        podstata je ta, ze vsechny veci v ramci 'simulate' dobehnou, tzn. nezdechne to vyhozeni exception
     */
    @PostMapping("/flux-concat")
    public Mono<List<Object>> fluxConcat() {

        return Flux.concat(simulate())
                .doOnNext(o -> System.out.println("print: " + o)).collect(Collectors.toList())
                .doOnError(throwable -> System.out.println("error: " + throwable.getMessage()));
    }

    @PostMapping("/mono-defer")
    public Mono<String> monoDefer() {
        Mono<String> mono = Mono.defer(() -> Mono.just(var));
        var = "zdar";
        return mono.flatMap(s -> Mono.just(s));
    }

    /*
        flux-delay0 a flux-delay1 by mely fungovat stejne
     */
    @PostMapping("/flux-delay0")
    public Flux<String> fluxDelay0() {
        Flux.just("RESTARTED", "UNHEALTHY", "HEALTHY", "DISK_SPACE_LOW", "OOM_DETECTED", "CRASHED", "UNKNOWN")
                .delayElements(Duration.ofSeconds(3))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(n ->
                        System.out.println("print: " + n)

                ).subscribe();

        return Flux.just("zadano na zpracovani .. ");

    }

    @PostMapping("/flux-delay1")
    public Flux<String> fluxDelay1() {
        Flux.just("RESTARTED", "UNHEALTHY", "HEALTHY", "DISK_SPACE_LOW", "OOM_DETECTED", "CRASHED", "UNKNOWN")
                .publishOn(Schedulers.boundedElastic())
                .flatMap(s -> delayAndReturn(s))
                .doOnNext(n ->
                        {
                            System.out.println("print: " + n);
                        }

                ).subscribe();

        return Flux.just("zadano na zpracovani .. ");
    }

    @PostMapping("/nevim2")
    public Flux<Object> nevim2() {

        return Flux.range(0, 10)
                .flatMap(integer -> Mono
                        .fromCallable(() -> delayAndReturnInteger(integer))
                        .publishOn(Schedulers.boundedElastic()))
                .flatMap(integer -> {
                    System.out.println("after delayAndReturnInteger method: " + integer);
                    return Mono.just(integer);
                });
    }

    @PostMapping("/nevim")
    public Flux<String> nevim() throws InterruptedException {

        //emptyService.print();

        ExecutorService e = Executors.newSingleThreadExecutor();
        Scheduler shared = Schedulers.fromExecutorService(e);

        List<Integer> list = new ArrayList<>();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Flux.range(0, 5)
                        //.publishOn(shared)
                        .flatMap(integer -> Mono
                                .fromCallable(() -> expensiveMethod(integer))
                                .publishOn(Schedulers.boundedElastic())
                        )

                        //.publishOn(shared)
                        .map(integer -> {
                            System.out.println("after expensiveMethod: " + integer + ", thread: " + Thread.currentThread().getName());
                            return integer;
                        })
                        .subscribe(integer -> list.add(integer));
            }
        });
        t1.start();
        t1.join();

        return Flux.just("nevim");
    }

    private Flux<Integer> breedWithRandomDelay() {

        try {
            int slp = Utils.getRandomNumber(5, 8);
            System.out.println(Thread.currentThread().getName() + " will sleep for " + slp + " seconds");
            TimeUnit.SECONDS.sleep(slp);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("sleep done");

        return Flux.range(0, 5);

    }

    private Integer delayAndReturnInteger(Integer integer) {
        try {
            System.out.println(Thread.currentThread().getName() + " will sleep for 2 seconds");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return integer;
    }

    private Mono<String> delayAndReturn(String s) {
        try {
            System.out.println("will sleep");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Mono.just(s);
    }


    private Integer expensiveMethod(Integer integer) {
        System.out.println("starting expensiveMethod for " + integer + ": " + Thread.currentThread().getName());
        for (int j = 0; j < 1_0; j++) {
            for (int i = 0; i < 1_000_000; i++) {
                double d = tan(atan(tan(atan(tan(atan(tan(atan(tan(atan(123456789.123456789))))))))));
                cbrt(d);
            }
        }
        System.out.println("done expensiveMethod for " + integer);
        return integer;
    }

    private Mono<Integer> expensiveMethodWrapper(Integer integer) {
        return Mono
                .fromCallable(() -> expensiveMethod(integer))
                .subscribeOn(Schedulers.boundedElastic());
    }


}
