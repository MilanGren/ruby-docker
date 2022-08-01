package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MyExceptionsController {

    private Mono<String> randomErrorOrString(Integer integer) {
        int r = Utils.getRandomNumber(0, 5);
        if (r == 1) {
            return Mono.just(integer + " processed");
        } else {
            throw new RuntimeException("i am a custom exception for " + integer);
            //return Mono.just(integer + " processed");
        }
    }

    private Mono<String> randomErrorOrStringMonoError(Integer integer) {
        int r = Utils.getRandomNumber(0, 5);
        if (r == 1) {
            return Mono.just(integer + " processed");
        } else {
            return Mono.error(new RuntimeException("i am a mono error custom exception for " + integer));
            //return Mono.just(integer + " processed");
        }
    }

    Mono<Void> addExceptionMsgToList(Throwable throwable, List<String> list) {
        list.add(throwable.getMessage());
        return Mono.empty().then();
    }

    /*
        pro dany list cisel provede random generovani vyjimky a odpovidajici message prida do error listu
     */
    @PostMapping("test-exceptions0")
    public Mono<String> testExceptions0() {
        List<Integer> listInt = List.of(0, 1, 2, 3, 4, 5);
        List<String> errors = new ArrayList<>();
        Flux.fromIterable(listInt)
                .flatMap(integer -> {
                            try {
                                return randomErrorOrString(integer);
                            } catch (Exception e) {
                                return addExceptionMsgToList(e, errors);
                            }
                        }
                )
                .subscribe(s -> System.out.println(s));

        return Mono.just("number of exceptions: " + errors.size());
    }

    @PostMapping("test-exceptions1")
    public Mono<String> testExceptions1() {
        List<Integer> listInt = List.of(0, 1, 2, 3, 4, 5);

        List<Status> errors = new ArrayList<>();

        Flux.fromIterable(listInt)
                .flatMap(integer -> randomErrorOrStringMonoError(integer))
                .doOnError(error -> {
                            Status status = Status.builder()
                                    .stackTrace(Collections.singletonList(error.toString()))
                                    .build();
                            errors.add(status);
                        }
                )
                .subscribe(s -> System.out.println(s));

        return Mono.just("done, status: " + errors);
    }

    // projdou vsechny emise (emitovane veci). Tam, kde vyleti exception, se to zachuti a nepreposle se dal nic (Mono.empty())
    @PostMapping("test-exceptions2")
    public Mono<String> testExceptions2() {

        List<RuntimeException> errors = new ArrayList<>();

        Function<String, Publisher<Integer>> mapper = input -> {
            try {
                return Mono.just(Integer.parseInt(input));
            } catch (Exception e) {
                errors.add(new NumberFormatException());
                return Mono.empty();
            }
        };

        Flux.just("1", "1.5", "2").flatMap(mapper).map(integer -> integer).subscribe(integer -> System.out.println(integer));

        return Mono.just("done " + errors.size());

    }


}
