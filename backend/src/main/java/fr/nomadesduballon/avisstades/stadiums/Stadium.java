package fr.nomadesduballon.avisstades.stadiums;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Football stadium referenced by the platform. The geographic location is not mapped yet.
 */
@Entity
class Stadium {

	@Id
	private UUID id;

	private String name;

	private String city;

	private String country;

	@Column(name = "resident_club")
	private String residentClub;

	private Integer capacity;

	protected Stadium() {
	}

	Stadium(UUID id, String name, String city, String country, String residentClub, Integer capacity) {
		this.id = id;
		this.name = name;
		this.city = city;
		this.country = country;
		this.residentClub = residentClub;
		this.capacity = capacity;
	}

	UUID getId() {
		return id;
	}

	String getName() {
		return name;
	}

	String getCity() {
		return city;
	}

	String getResidentClub() {
		return residentClub;
	}

}
