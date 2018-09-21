package demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

	private final Long id;

	private final int score;

	private final Long personId;

	@JsonCreator
	public Account(@JsonProperty("id") Long id, @JsonProperty("score") int score,
			@JsonProperty("personId") Long personId) {

		this.id = id;
		this.score = score;
		this.personId = personId;
	}

	public Long getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public Long getPersonId() {
			return personId;
	}

	@Override
	public String toString() {
		return "Account {id=" + this.id + ", score='" + this.score + "'}";
	}
}
