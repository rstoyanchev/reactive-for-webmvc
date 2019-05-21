package demo;

import java.util.LinkedHashMap;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;


@RestController
public class MainController {

	public static final Sort BY_SCORE_SORT = Sort.by(Sort.Direction.DESC, "score");

	private WebClient client;

	private AccountRepository accountRepository;


	public MainController(WebClient.Builder builder, AccountRepository repository) {
		this.client = builder.baseUrl("http://localhost:8081").build();
		this.accountRepository = repository;
	}


	@GetMapping("/persons")
	Flux<Person> getPersons() {
		return client.get().uri("/persons?delay=2").retrieve().bodyToFlux(Person.class);
	}

	@GetMapping("/person/{id}")
	Mono<Person> getPerson(@PathVariable Long id) {
		return this.client.get().uri("/person/{id}?delay=2", id).retrieve().bodyToMono(Person.class);
	}

	@GetMapping(path = "/persons/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Person> getPersonStream() {
		return this.client.get().uri("/persons/events")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(Person.class);
	}

	@GetMapping("/accounts/hobbies")
	Flux<Map<String, String>> getTopAccountHobbies() {
		return this.accountRepository.findAll(BY_SCORE_SORT)
				.take(5)
				.flatMapSequential(account -> {
					Long personId = account.getPersonId();

					Mono<String> nameMono = client.get().uri("/person/{id}?delay=2", personId)
							.retrieve()
							.bodyToMono(Person.class)
							.map(Person::getName);

					Mono<String> hobbyMono = client.get().uri("/person/{id}/hobby?delay=1", personId)
							.retrieve()
							.bodyToMono(Hobby.class)
							.map(Hobby::getHobby);

					return Mono.zip(nameMono, hobbyMono, (personName, hobby) -> {
						Map<String, String> data = new LinkedHashMap<>();
						data.put("person", personName);
						data.put("hobby", hobby);
						return data;
					});
				});
	}

}
