package demo;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.BodyInserters.*;

@SpringBootApplication
public class RemoteServiceApp {


	public static void main(String[] args) {
		SpringApplication.run(RemoteServiceApp.class, args);
	}


	@Bean
	public RouterFunction<?> routes() {
		PersonHandler handler = new PersonHandler();
		return RouterFunctions.route()
				.GET("/persons", handler::getPersons)
				.GET("/person/{id}", handler::getPerson)
				.GET("/person/{id}/hobby", handler::getPersonHobbies)
				.GET("/persons/events", handler::getPersonEvents)
				.filter(new DelayFilter())
				.build();
	}


	private static class PersonHandler {

		private static final Mono<ServerResponse> NOT_FOUND = ServerResponse.notFound().build();


		Mono<ServerResponse> getPersons(ServerRequest request) {
			return ServerResponse.ok().syncBody(PERSON_DATA.values());
		}

		Mono<ServerResponse> getPerson(ServerRequest request) {
			Long personId = Long.parseLong(request.pathVariable("id"));
			Map<String, Object> body = PERSON_DATA.get(personId);
			return body != null ? ServerResponse.ok().syncBody(body) : NOT_FOUND;
		}

		Mono<ServerResponse> getPersonHobbies(ServerRequest request) {
			Long personId = Long.parseLong(request.pathVariable("id"));
			Map<String, Object> body = HOBBY_DATA.get(personId);
			return body != null ? ServerResponse.ok().syncBody(body) : NOT_FOUND;
		}

		Mono<ServerResponse> getPersonEvents(ServerRequest request) {
			return ServerResponse.ok().body(fromServerSentEvents(
					Flux.interval(Duration.ofSeconds(2))
							.map(i -> PERSON_DATA.get((i % 10) + 1))
							.map(data -> ServerSentEvent.builder(data).build())));
		}


		private static final Map<Long, Map<String, Object>> PERSON_DATA;

		static {
			PERSON_DATA = new HashMap<>();
			addPerson(1L, "Amanda");
			addPerson(2L, "Brittany");
			addPerson(3L, "Christopher");
			addPerson(4L, "Elizabeth");
			addPerson(5L, "Hannah");
			addPerson(6L, "Joshua");
			addPerson(7L, "Kayla");
			addPerson(8L, "Lauren");
			addPerson(9L, "Matthew");
			addPerson(10L, "Megan");
		}

		private static void addPerson(Long id, String name) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("id", id);
			map.put("name", name);
			PERSON_DATA.put(id, map);
		}


		private static final Map<Long, Map<String, Object>> HOBBY_DATA;

		static {
			HOBBY_DATA = new HashMap<>();
			addHobby(1L, "Travel");
			addHobby(2L, "Coffee Roasting");
			addHobby(3L, "Puzzles");
			addHobby(4L, "3D Printing");
			addHobby(5L, "Skiing");
			addHobby(6L, "Yoga");
			addHobby(7L, "Scuba Diving");
			addHobby(8L, "Shopping");
			addHobby(9L, "Tai Chi");
			addHobby(10L, "Kombucha brewing");
		}

		private static void addHobby(Long personId, String hobby) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("personId", personId);
			map.put("hobby", hobby);
			HOBBY_DATA.put(personId, map);
		}
	}


	private static class DelayFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

		@Override
		public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
			Duration delay = request.queryParam("delay")
					.map(rawValue -> Duration.ofSeconds(Long.parseLong(rawValue)))
					.orElse(Duration.ZERO);
			return delay.isZero() ?
					next.handle(request) :
					Mono.delay(delay).flatMap(aLong -> next.handle(request));
		}
	}


}
