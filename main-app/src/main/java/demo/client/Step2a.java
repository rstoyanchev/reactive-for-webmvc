package demo.client;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import demo.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

public class Step2a {

	private static final Logger logger = LoggerFactory.getLogger(Step2a.class);

	private static WebClient client = WebClient.create("http://localhost:8081?delay=2");


	public static void main(String[] args) {

		Instant start = Instant.now();

		List<Mono<Person>> monos = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			Mono<Person> personMono = client.get().uri("/person/{id}", i)
					.retrieve()
					.bodyToMono(Person.class);

			monos.add(personMono);
		}

		logTime(start);
	}


	private static void logTime(Instant start) {
		logger.debug("Elapsed time: " + Duration.between(start, Instant.now()).toMillis() + "ms");
	}

}
