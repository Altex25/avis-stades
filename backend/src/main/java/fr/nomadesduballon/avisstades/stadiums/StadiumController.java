package fr.nomadesduballon.avisstades.stadiums;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public read access to the stadium catalogue.
 */
@RestController
@RequestMapping("/api/stadiums")
class StadiumController {

	private final StadiumRepository stadiums;

	StadiumController(StadiumRepository stadiums) {
		this.stadiums = stadiums;
	}

	@GetMapping
	@Transactional(readOnly = true)
	List<StadiumSummary> list() {
		return stadiums.findAllByOrderByNameAsc().stream().map(StadiumSummary::of).toList();
	}

}
