package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PrikladyController {

    private final Path basePath = Paths.get("./tmp");


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleAny(Exception e) {
        log.warn("Returning HTTP 400 Bad Request", e);
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private String blockingWebClientGet(String url) {

        try {
            TimeUnit.SECONDS.sleep(getRandomNumber(1, 3));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return url;
    }

    private Mono<Integer> add(List<Integer> list, Integer value) {
        return Mono.just(myAdd(list, value));
    }

    private Integer myAdd(List<Integer> list, Integer value) {
        list.add(value);
        try {
            System.out.println("thread: " + Thread.currentThread().getName() + ", will use: " + value);
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Integer.valueOf(1);
    }


    @PostMapping("multi")
    public Mono<Void> upload(@RequestPart("files") Flux<FilePart> partFlux) {

        return partFlux
                .publishOn(Schedulers.boundedElastic())
//                .doOnNext(fp -> System.out.println(fp.filename()))
                .flatMap(fp -> fp.transferTo(basePath.resolve(fp.filename())))
                .then();
    }


    @PostMapping("/priklad0")
    public Mono<List<Integer>> priklad0(@RequestHeader(name = "Domain", required = false, defaultValue = "") String tenant) throws InterruptedException {

        List<Integer> listInt = List.of(0, 1, 2, 3);
        var f = Flux.fromIterable(listInt);
        List<Integer> newListInt = new ArrayList<>();

        /*


            1. publikuju integer, nad kterym publikuju metodu pridat. Zaroven skrze doOnNext publikuju dalsi metodu navazujici na predhozi vzdy pro dany integer
            2. diky '.publishOn(Schedulers.boundedElastic())' se to provede asynchronne, takze velikost pole newListInt bude 0
                        ... lze vyhodit '.publishOn(Schedulers.boundedElastic())', potom velikost newListInt bude > 0
        */
        f.publishOn(Schedulers.boundedElastic()) // diky tomu to jede async. Muzu dat is Schedulers.single(). Pokud dam Schedulers.immediate(), zustava to blokovane!
                .log()
                .flatMap(integer -> add(newListInt, integer))
                .doOnNext(integer -> newListInt.add(9))
                .subscribe(integer -> System.out.println(Thread.currentThread().getName()));

        //TimeUnit.SECONDS.sleep(6);

        System.out.println(newListInt);
        System.out.println("Size is: " + newListInt.size());

        // na vystupu bude [0,9,1,9,2,9,3,9]
        return Mono.just(newListInt);

    }

    @PostMapping("/priklad1")
    public Mono<List<Integer>> priklad1(@RequestHeader(name = "Domain", required = false, defaultValue = "") String tenant) throws InterruptedException {

        List<Integer> listInt = List.of(0, 1, 2, 3);
        var f = Flux.fromIterable(listInt);
        List<Integer> newListInt = new ArrayList<>();

        f.parallel(2)
                .runOn(Schedulers.parallel())
                .flatMap(integer -> add(newListInt, integer))
                .doOnNext(integer -> newListInt.add(9))
                .subscribe(integer -> System.out.println(Thread.currentThread().getName()));

        //TimeUnit.SECONDS.sleep(6);

        System.out.println(newListInt);
        System.out.println("Size is: " + newListInt.size());

        // na vystupu bude naprikld [1, 9, 9, 3, 2, 9] - chybi 0, protoze se nezapsala, protoze v tu chvili bylo blokovano jinymi threadem
        return Mono.just(newListInt);

    }

    @PostMapping("/priklad2")
    public Mono<String> priklad2(@RequestHeader(name = "Domain", required = false, defaultValue = "") String tenant) {

        Flux.fromIterable(List.of("a", "b", "c")) //contains A, B and C
                .publishOn(Schedulers.boundedElastic())
                .map(url -> blockingWebClientGet(url))
                .subscribe(body -> System.out.println(Thread.currentThread().getName() + " from first list, got " + body));

        Flux.fromIterable(List.of("d", "e")) //contains D and E
                .publishOn(Schedulers.boundedElastic())
                .map(url -> blockingWebClientGet(url))
                .subscribe(body -> System.out.println(Thread.currentThread().getName() + " from second list, got " + body));

        return Mono.just("im ok");

    }


}
