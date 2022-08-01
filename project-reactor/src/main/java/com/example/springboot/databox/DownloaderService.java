package com.example.springboot.databox;

import com.example.springboot.Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DownloaderService {


    private List<String> getListOfReceivedMessages(String user) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            list.add(user + " message" + i);
        }
        return list;
    }

    private Mono<Integer> processEnvelope(String envelope) {
        int i = Utils.getRandomNumber(0, 3);
        System.out.println("random number " + i + " for envelope " + envelope);
        if (i == 0) {
            return Mono.error(new RuntimeException("dowloading envelope error, message: " + envelope));
        } else {
            return Mono.just(1);
        }
    }

    public Mono<DownloadResult> download(String user) {

        Function<String, Mono<DownloadResult>> mapper = input -> {
            List<Mono<Integer>> messages = getListOfReceivedMessages(input).stream().map(envelope -> processEnvelope(envelope)).collect(Collectors.toList());
            return Flux.concat(messages)
                    .reduce(0, Integer::sum).flatMap(integer -> {
                        return Mono.just(DownloadResult.builder().attachmentCount(integer).build());
                    });
        };

        return Mono.just(user).flatMap(mapper);

/*        return Mono.just(user).flatMap(input -> {
            return Flux.concat(getListOfReceivedMessages().stream().map(envelope -> processEnvelope(envelope)).collect(Collectors.toList()))
                    .reduce(0, Integer::sum).flatMap(integer -> {
                        return Mono.just(DownloadResult.builder().attachmentCount(integer).build());
                    });
        });*/

    }
}
