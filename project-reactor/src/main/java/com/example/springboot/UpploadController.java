package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UpploadController {

    private final Path basePath = Paths.get("./tmp");


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleAny(Exception e) {
        log.warn("Returning HTTP 400 Bad Request", e);
    }


    private Mono<FilePart> fileProcessing(FilePart filePart) {
        try {
            int slpLimit = Utils.getRandomNumber(2, 6);
            int slp = 0;
            while (slp < slpLimit) {
                System.out.printf("  .. " + filePart.filename() + " in progress");
                TimeUnit.SECONDS.sleep(1);
                slp += 1;
            }
            System.out.println("");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(filePart.filename() + " processing done");
        return Mono.just(filePart);
    }

    @PostMapping("async-multi-uppload")
    public Mono<String> asyncMultiUpload(@RequestPart("files") List<FilePart> files) {

        List<Disposable> disposables = files.stream().map(filePart -> Flux.just(filePart)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(fp0 -> fileProcessing(fp0))
                .subscribe(fp1 -> System.out.println(fp1.filename() + " finished"))
        ).collect(Collectors.toList());

        return Mono.just("hotovo");


    }

    @PostMapping("multi-uppload")
    public Mono<Void> mutliUppload(@RequestPart("files") Flux<FilePart> partFlux) {

        return partFlux
                .publishOn(Schedulers.boundedElastic())
//                .doOnNext(fp -> System.out.println(fp.filename()))
                .flatMap(fp -> fp.transferTo(basePath.resolve(fp.filename())))
                .then();


    }


    /*

        analogie Stream<..> a Flux<..>
        .. pokud je Flux v argumentu, potom se v ramci metody k nemu chovam podobne, jako kdyz je Stream v argumentu

     */

    private Stream<String> method(Stream<String> stream) {
        return stream.map(s -> s + "-x");
    }

    private Flux<FilePart> method(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .publishOn(Schedulers.boundedElastic())
                .flatMap(fp0 -> fileProcessing(fp0));

    }

    @PostMapping("/analogie")
    public List<String> smazat() {
        List<String> list = List.of("1", "2", "3");
        return method(list.stream()).collect(Collectors.toList());
    }


    @PostMapping("async-multi-uppload2")
    public Mono<String> asyncMultiUpload2(@RequestPart("files") List<FilePart> files) {

        List<Disposable> disposables = files.stream().map(filePart -> method(Flux.just(filePart))
                .subscribe(fp1 -> System.out.println(fp1.filename() + " finished"))
        ).collect(Collectors.toList());

        return Mono.just("hotovo");


    }

}
