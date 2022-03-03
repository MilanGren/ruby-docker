package com.example.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

	// pokud nemam @slf4j, potom takto:
	// Logger log = LoggerFactory.getLogger(HelloController.class);

	@GetMapping("/")
	public String index() {
		log.debug("I AM A LOG");
		System.out.println("I AM A PRINT");
		return "Greetings from Spring Boot!";
	}

}
