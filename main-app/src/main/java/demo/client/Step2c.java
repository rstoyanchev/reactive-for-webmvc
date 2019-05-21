package demo.client;

import java.time.Duration;
import java.time.Instant;

import demo.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.web.reactive.function.client.WebClient;

public class Step2c {

	private static final Logger logger = LoggerFactory.getLogger(Step2c.class);

	private static WebClient client = WebClient.create("http://localhost:8081?delay=2");


	public static void main(String[] args) {

		Instant start = Instant.now();

		Flux.range(1, 3)
				.doOnNext(i -> System.out.println("Getting id=" + i))
				.flatMap(i -> client.get().uri("/person/{id}", i).retrieve().bodyToMono(Person.class))
				.doOnNext(person -> System.out.println("Got " + person))
				.blockLast();

		logTime(start);
	}


	private static void logTime(Instant start) {
		logger.debug("Total: " + Duration.between(start, Instant.now()).toMillis() + " millis");
	}

}
