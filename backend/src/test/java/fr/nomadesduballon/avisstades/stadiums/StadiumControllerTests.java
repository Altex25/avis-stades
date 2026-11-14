package fr.nomadesduballon.avisstades.stadiums;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import fr.nomadesduballon.avisstades.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class StadiumControllerTests {

	@Autowired
	MockMvcTester mvc;

	@Autowired
	StadiumRepository stadiums;

	@AfterEach
	void deleteStadiums() {
		stadiums.deleteAll();
	}

	@Test
	void listsStadiumsByNameWithoutAuthentication() {
		stadiums.save(new Stadium(UUID.randomUUID(), "Stade Pierre-Mauroy", "Villeneuve-d'Ascq", "FR", "LOSC", 50186));
		stadiums.save(new Stadium(UUID.randomUUID(), "Stade Bollaert-Delelis", "Lens", "FR", "RC Lens", 38223));

		assertThat(mvc.get().uri("/api/stadiums")).hasStatusOk()
			.bodyJson()
			.extractingPath("$[*].name")
			.asArray()
			.containsExactly("Stade Bollaert-Delelis", "Stade Pierre-Mauroy");
	}

	@Test
	void returnsAnEmptyListWhenNoStadiumIsReferenced() {
		assertThat(mvc.get().uri("/api/stadiums")).hasStatusOk().bodyJson().isEqualTo("[]");
	}

	@Test
	void refusesWritesOnTheCatalogue() {
		assertThat(mvc.post().uri("/api/stadiums")).hasStatus(HttpStatus.FORBIDDEN);
	}

}
