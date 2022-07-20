package com.example.springboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PdfvalidController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleAny(Exception e) {
        log.warn("Returning HTTP 400 Bad Request", e);
    }

    public Flux<Wrapper> preproc(FilePart filePart) {

        System.out.println("preproc " + filePart.filename() + ": " + Thread.currentThread().getName());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        return DataBufferUtils.write(filePart.content(), baos).map(dataBuffer -> new Wrapper(dataBuffer.asInputStream(), filePart.filename()));

    }

    public Mono<ResultPdfaValidation> isValid(Wrapper wrapper) {

        System.out.println("isValid " + wrapper.filename + ": " + Thread.currentThread().getName());

        PDFValidatorVERA pdfvalidator = new PDFValidatorVERA();

        ResultPdfaValidation result = new ResultPdfaValidation(wrapper.filename);

        //pdfvalidator.validate(wrapper.inputStream, "1b", result);

        return Mono.just(result);

    }


    @PostMapping("/pdfvalid")
    public Mono<String> pdfvalid(@RequestPart("files") List<FilePart> files) throws InterruptedException {
        List<Disposable> disposables = new ArrayList<>();
        files.stream().forEach(filePart_ ->
                disposables.add(
                        //preproc(filePart_)
                        Flux.just(filePart_)

                                .publishOn(Schedulers.boundedElastic())
                                .flatMap(filePart -> preproc(filePart))
                                .flatMap(wrapper -> isValid(wrapper))
                                .log()
                                .subscribe(resultPdfaValidation -> System.out.println(resultPdfaValidation))
                )
        );

        if (true) {
            boolean allDisposed = false;
            while (!allDisposed) {
                //disposables.stream().forEach(disposable -> System.out.print(" disposed: " + disposable.isDisposed()));
                //System.out.println("\n--");
                if (!disposables.stream().filter(disposable -> !disposable.isDisposed()).findAny().isPresent()) {
                    allDisposed = true;
                    disposables.stream().forEach(disposable -> System.out.print(" disposed: " + disposable.isDisposed()));
                    System.out.println("\n--");
                }
            }
        }

        return Mono.just("all done");

    }


    public static class Wrapper {
        public InputStream inputStream;
        public String filename;

        public Wrapper(InputStream inputStream, String filename) {
            this.inputStream = inputStream;
            this.filename = filename;
        }

    }


}
