package demo.client;

import java.time.Duration;
import java.time.Instant;

import demo.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.reactive.function.client.WebClient;

public class Step2a {

	private static final Logger logger = LoggerFactory.getLogger(Step2a.class);

	private static WebClient client = WebClient.create("http://localhost:8081?delay=2");


	public static void main(String[] args) {

		Instant start = Instant.now();

		for (int i = 1; i <= 3; i++) {
			System.out.println("Getting id=" + i);
			client.get().uri("/person/{id}", i).retrieve().bodyToMono(Person.class);
		}

		logTime(start);
	}


	private static void logTime(Instant start) {
		logger.debug("Total: " + Duration.between(start, Instant.now()).toMillis() + " millis");
	}

}
