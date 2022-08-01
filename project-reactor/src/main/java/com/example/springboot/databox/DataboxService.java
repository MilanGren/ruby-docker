package com.example.springboot.databox;

import com.example.springboot.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DataboxService {

    private final SingleTenantDataboxService singleTenantDataboxService = new SingleTenantDataboxService();

    public Mono<Status> processTenants() {

        AtomicLong attachmentCount = new AtomicLong(0);
        return Flux.fromIterable(List.of("tenant1", "tenant2")).flatMap(tenant -> {
                    return singleTenantDataboxService.process(tenant);
                }).map(result -> attachmentCount.addAndGet(result.getAttachmentCount()))
                .last(0L)
                .map(total -> Status.builder()
                        .newMessageCount(attachmentCount.get())
                        .build())
                .doOnSuccess(status -> System.out.println("Finished processing: " + status.getNewMessageCount()))
                .doOnError(
                        error -> Status.builder()
                                .stackTrace(Collections.singletonList(error.toString()))
                                .build()
                );


    }


}
