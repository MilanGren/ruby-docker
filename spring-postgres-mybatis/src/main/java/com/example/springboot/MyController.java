package com.example.springboot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final Program program;

    public MyController(Program program) {
        this.program = program;
    }


    @PostMapping("/spring-postgres")
    public String springPostgres() {
        try {
            return program.springPostgresMybatisTransactionTemplate();
        } catch (Exception e) {
            return "supressing throwing exception";
        }
    }

}
