package demo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class RemoteServicesTests {

	private static final ParameterizedTypeReference<Map<String, ?>> MAP_TYPE =
			new ParameterizedTypeReference<Map<String, ?>>() {};


	@Autowired
	WebTestClient client;

	@Test
	public void person() {

		Map<String, Object> expected = new LinkedHashMap<>();
		expected.put("id", 1);
		expected.put("name", "Amanda");

		this.client.get().uri("/person/1")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody(MAP_TYPE).isEqualTo(expected);
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

		Flux<Map<String, ?>> body = this.client.get().uri("/persons/events")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType("text/event-stream;charset=UTF-8")
				.returnResult(MAP_TYPE)
				.getResponseBody()
				.take(3);

		StepVerifier.create(body)
				.consumeNextWith(map -> assertEquals("Amanda", map.get("name")))
				.consumeNextWith(map -> assertEquals("Brittany", map.get("name")))
				.consumeNextWith(map -> assertEquals("Christopher", map.get("name")))
				.verifyComplete();
	}

	@Test
	public void hobby() {

		Map<String, Object> expected = new LinkedHashMap<>();
		expected.put("personId", 1);
		expected.put("hobby", "Travel");

		this.client.get().uri("/person/1/hobby")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
				.expectBody(MAP_TYPE).isEqualTo(expected);
	}

}
