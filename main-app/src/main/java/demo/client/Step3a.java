package demo.client;

import demo.Person;

import org.springframework.web.reactive.function.client.WebClient;

public class Step3a {

	private static WebClient client = WebClient.create("http://localhost:8081");


	public static void main(String[] args) {

		client.get().uri("/persons/events")
				.retrieve()
				.bodyToFlux(Person.class)
				.doOnNext(person -> System.out.println("Got " + person))
				.take(4)
				.blockLast();
	}

}
