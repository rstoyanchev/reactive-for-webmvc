package demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Hobby {

	private final Long personId;

	private final String hobby;

	@JsonCreator
	public Hobby(@JsonProperty("id") Long personId, @JsonProperty("hobby") String hobby) {
		this.personId = personId;
		this.hobby = hobby;
	}

	public Long getPersonId() {
		return personId;
	}

	public String getHobby() {
		return hobby;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Hobby hobby = (Hobby) other;
		return personId.equals(hobby.personId);
	}

	@Override
	public int hashCode() {
		return personId.hashCode();
	}

	@Override
	public String toString() {
		return "Hobby {personId=" + this.personId + ", hobby='" + this.hobby + "'}";
	}
}
