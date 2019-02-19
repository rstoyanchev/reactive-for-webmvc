package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

// Before running the tests:
// Make sure RemoteServices app is running!!

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainAppTests {

	@Autowired
	WebTestClient client;

	@Test
	public void person() {
		this.client.get().uri("/person/1")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody(Person.class).isEqualTo(new Person(1L, "Amanda"));
	}

	@Test
	public void persons() {
		this.client.get().uri("/persons")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(10)
				.jsonPath("$[0].name").isEqualTo("Amanda")
				.jsonPath("$[1].name").isEqualTo("Brittany")
				.jsonPath("$[2].name").isEqualTo("Christopher")
				.jsonPath("$[3].name").isEqualTo("Elizabeth")
				.jsonPath("$[4].name").isEqualTo("Hannah")
				.jsonPath("$[5].name").isEqualTo("Joshua")
				.jsonPath("$[6].name").isEqualTo("Kayla")
				.jsonPath("$[7].name").isEqualTo("Lauren")
				.jsonPath("$[8].name").isEqualTo("Matthew")
				.jsonPath("$[9].name").isEqualTo("Megan");
	}

	@Test
	public void personsEvents() {

		Flux<Person> body = this.client.get().uri("/persons/events")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType("text/event-stream;charset=UTF-8")
				.returnResult(Person.class)
				.getResponseBody()
				.take(3);

		StepVerifier.create(body)
				.expectNext(new Person(1L, "Amanda"))
				.expectNext(new Person(2L, "Brittany"))
				.expectNext(new Person(3L, "Christopher"))
				.verifyComplete();
	}

	@Test
	public void hobby() {
		this.client.get().uri("/accounts/hobbies")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(5)
				.jsonPath("$[0].person").isEqualTo("Hannah")
				.jsonPath("$[0].hobby").isEqualTo("Skiing")
				.jsonPath("$[4].person").isEqualTo("Kayla")
				.jsonPath("$[4].hobby").isEqualTo("Scuba Diving");
	}
}
