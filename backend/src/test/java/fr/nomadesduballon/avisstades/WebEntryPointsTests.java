package fr.nomadesduballon.avisstades;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class WebEntryPointsTests {

	@Autowired
	MockMvcTester mvc;

	@Test
	void exposesTheHealthProbeWithoutDetails() {
		assertThat(mvc.get().uri("/actuator/health")).hasStatusOk()
			.bodyJson()
			.hasPathSatisfying("$.status", status -> assertThat(status).asString().isEqualTo("UP"))
			.doesNotHavePath("$.components")
			.doesNotHavePath("$.details");
		assertThat(mvc.get().uri("/actuator/health/readiness")).hasStatusOk();
	}

	@Test
	void hidesTheOtherActuatorEndpoints() {
		assertThat(mvc.get().uri("/actuator/env")).hasStatus(HttpStatus.FORBIDDEN);
		assertThat(mvc.get().uri("/actuator/modulith")).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	void deniesTheApiOutsideThePublicCatalogue() {
		assertThat(mvc.get().uri("/api/reviews")).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	void deniesWritesOutsideTheApiEvenWithAValidCsrfToken() {
		var token = mvc.get().uri("/").exchange().getResponse().getCookie("XSRF-TOKEN");

		assertThat(mvc.post().uri("/moderation").cookie(token).header("X-XSRF-TOKEN", token.getValue()))
			.hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	void issuesACsrfCookieReadableByThePwa() {
		var token = mvc.get().uri("/").exchange().getResponse().getCookie("XSRF-TOKEN");

		assertThat(token).isNotNull();
		assertThat(token.isHttpOnly()).isFalse();
		assertThat(token.getValue()).isNotBlank();
	}

	@Test
	void sendsHstsAndSecureCookiesBehindTheTlsProxy() {
		var result = mvc.get().uri("/").header("X-Forwarded-Proto", "https").exchange();

		assertThat(result).hasHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
		assertThat(result.getResponse().getCookie("XSRF-TOKEN").getSecure()).isTrue();
	}

	@Test
	void forwardsClientSideRoutesToThePwa() {
		assertThat(mvc.get().uri("/stades/stade-bollaert-delelis")).hasForwardedUrl("/index.html");
		assertThat(mvc.get().uri("/moderation")).hasForwardedUrl("/index.html");
	}

	@Test
	void doesNotForwardFilesOrApiPaths() {
		assertThat(mvc.get().uri("/manifest.webmanifest").exchange().getResponse().getForwardedUrl()).isNull();
		assertThat(mvc.get().uri("/api")).hasStatus(HttpStatus.FORBIDDEN);
	}

}
