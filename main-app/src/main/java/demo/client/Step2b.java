package demo.client;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import demo.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

public class Step2b {

	private static final Logger logger = LoggerFactory.getLogger(Step2b.class);

	private static WebClient client = WebClient.create("http://localhost:8081?delay=2");

	// TODO: Also try logging (for correlated messages)..


	public static void main(String[] args) {

		Instant start = Instant.now();

		List<Mono<Person>> list = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			System.out.println("Getting " + i);
			list.add(client.get().uri("/person/{id}", i)
					.retrieve()
					.bodyToMono(Person.class)
					.doOnNext(person -> System.out.println("Got " + person)));
		}
		Mono.when(list).block();

		logTime(start);
	}


	private static void logTime(Instant start) {
		logger.debug("Total: " + Duration.between(start, Instant.now()).toMillis() + " millis");
	}

}
