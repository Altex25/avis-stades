package fr.nomadesduballon.avisstades;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

	private final ApplicationModules modules = ApplicationModules.of(AvisStadesApplication.class);

	@Test
	void detectsTheSixApplicationModules() {
		assertThat(modules.stream().map(ApplicationModule::getIdentifier).map(Object::toString))
			.containsExactlyInAnyOrder("users", "stadiums", "reviews", "moderation", "statistics", "notifications");
	}

	@Test
	void modulesOnlyDependOnEachOtherThroughTheirPublicApi() {
		modules.verify();
	}

}
