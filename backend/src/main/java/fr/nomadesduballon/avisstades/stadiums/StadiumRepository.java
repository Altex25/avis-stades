package fr.nomadesduballon.avisstades.stadiums;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence of stadiums.
 */
interface StadiumRepository extends JpaRepository<Stadium, UUID> {

	List<Stadium> findAllByOrderByNameAsc();

}
