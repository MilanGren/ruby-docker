package com.example.springboot.databox;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
public class DataboxController {

    @PostMapping("refresh")
    public Mono<String> download() {

        DataboxService databoxService = new DataboxService();

        return databoxService.processTenants().map(status -> status.toString());
    }

}
