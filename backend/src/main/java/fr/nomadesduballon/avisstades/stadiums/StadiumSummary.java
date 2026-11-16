package fr.nomadesduballon.avisstades.stadiums;

import java.util.UUID;

/**
 * Stadium as shown in a list.
 *
 * @param id identifier of the stadium
 * @param name name of the stadium
 * @param city city where the stadium stands
 * @param residentClub club playing there, if any
 */
record StadiumSummary(UUID id, String name, String city, String residentClub) {

	static StadiumSummary of(Stadium stadium) {
		return new StadiumSummary(stadium.getId(), stadium.getName(), stadium.getCity(), stadium.getResidentClub());
	}

}
