package demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

	private final Long id;

	private final String name;

	@JsonCreator
	public Person(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Person person = (Person) other;
		return id.equals(person.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "Person {id=" + this.id + ", name='" + this.name + "'}";
	}
}
