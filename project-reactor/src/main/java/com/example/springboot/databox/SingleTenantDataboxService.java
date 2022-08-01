package com.example.springboot.databox;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class SingleTenantDataboxService {

    private final DownloaderService downloaderService = new DownloaderService();

    private List<String> getTentantUsers(String tenant) {
        return List.of("milan-" + tenant, "uzivatel-" + tenant);
    }

    /*
    pro dany tentant vracti celkovy soucet zpracovanych zprav
     */
    public Mono<DownloadResult> process(String tenant) {

        return Flux.fromIterable(getTentantUsers(tenant))
                .flatMap(downloaderService::download)
                .collectList()
                .map(results -> results.stream().reduce((a, b) -> {
                    return DownloadResult.builder()
                            .attachmentCount(a.getAttachmentCount() + b.getAttachmentCount())
                            .error(Lists.newArrayList(Iterables.concat(a.getError(), b.getError())))
                            .build();
                }).orElse(DownloadResult.builder()
                        .build()))
                .doOnSuccess(t -> System.out.println("DONE tenant " + tenant))
                .doOnError(t -> System.out.println("ERROR tenant " + tenant));
    }

}
