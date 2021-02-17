package com.example.actuatorservice;

import java.util.concurrent.atomic.AtomicLong;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Control {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	private MeterRegistry meterRegistry;
	private Counter mcounter ;

	public Control(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
		mcounter = this.meterRegistry.counter("beer.orders", "type", "light"); // 1 - create a counter
		mcounter.increment();
		System.out.println("OK");
	}

	@GetMapping("/hello")
	@ResponseBody
	public Greeting sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
		mcounter.increment();
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

}
