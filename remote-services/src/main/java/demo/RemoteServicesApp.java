package demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.BodyInserters.*;

@SpringBootApplication
public class RemoteServicesApp {

	private static final Map<Long, Map<String, Object>> PERSON_DATA = initPersonData();

	private static final Map<Long, Map<String, Object>> HOBBY_DATA = initHobbyData();

	private static final Mono<ServerResponse> NOT_FOUND = ServerResponse.notFound().build();


	public static void main(String[] args) {
		SpringApplication.run(RemoteServicesApp.class, args);
	}

	@Bean
	public RouterFunction<?> routes() {
		return RouterFunctions.route()
				.GET("/person/{id}", request -> {
					Long id = Long.parseLong(request.pathVariable("id"));
					Map<String, Object> body = PERSON_DATA.get(id);
					return body != null ? ServerResponse.ok().syncBody(body) : NOT_FOUND;
				})
				.GET("/persons", request -> {
					List<Map<String, Object>> body = new ArrayList<>(PERSON_DATA.values());
					return ServerResponse.ok().syncBody(body);
				})
				.GET("/persons/events", request -> {
					Flux<ServerSentEvent<Map<String, Object>>> stream =
							Flux.interval(Duration.ofSeconds(2), Duration.ofSeconds(1))
									.take(12)
									.map(i -> PERSON_DATA.get((i % 10) + 1))
									.map(data -> ServerSentEvent.builder(data).build());
					return ServerResponse.ok().body(fromServerSentEvents(stream));
				})
				.GET("/person/{id}/hobby", request -> {
					Long id = Long.parseLong(request.pathVariable("id"));
					Map<String, Object> body = HOBBY_DATA.get(id);
					return body != null ? ServerResponse.ok().syncBody(body) : NOT_FOUND;
				})
				.filter((request, next) -> {
					Duration delay = request.queryParam("delay")
							.map(s -> Duration.ofSeconds(Long.parseLong(s)))
							.orElse(Duration.ZERO);
					return delay.isZero() ? next.handle(request) :
							Mono.delay(delay).flatMap(aLong -> next.handle(request));
				})
				.build();
	}


	private static Map<Long, Map<String, Object>> initPersonData() {
		Map<Long, Map<String, Object>> map = new HashMap<>();
		addPerson(map, 1L, "Amanda");
		addPerson(map, 2L, "Brittany");
		addPerson(map, 3L, "Christopher");
		addPerson(map, 4L, "Elizabeth");
		addPerson(map, 5L, "Hannah");
		addPerson(map, 6L, "Joshua");
		addPerson(map, 7L, "Kayla");
		addPerson(map, 8L, "Lauren");
		addPerson(map, 9L, "Matthew");
		addPerson(map, 10L, "Megan");
		return map;
	}

	private static void addPerson(Map<Long, Map<String, Object>> persons, Long id, String name) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", id);
		map.put("name", name);
		persons.put(id, map);
	}

	private static Map<Long, Map<String, Object>> initHobbyData() {
		Map<Long, Map<String, Object>> map = new HashMap<>();
		addHobby(map, 1L, "Travel");
		addHobby(map, 2L, "Coffee Roasting");
		addHobby(map, 3L, "Puzzles");
		addHobby(map, 4L, "3D Printing");
		addHobby(map, 5L, "Skiing");
		addHobby(map, 6L, "Yoga");
		addHobby(map, 7L, "Scuba Diving");
		addHobby(map, 8L, "Shopping");
		addHobby(map, 9L, "Tai Chi");
		addHobby(map, 10L, "Kombucha brewing");
		return map;
	}

	private static void addHobby(Map<Long, Map<String, Object>> hobbies, Long personId, String hobby) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("personId", personId);
		map.put("hobby", hobby);
		hobbies.put(personId, map);
	}

}
