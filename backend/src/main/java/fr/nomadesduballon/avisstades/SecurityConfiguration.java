package fr.nomadesduballon.avisstades;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP security of the single deployable: the health probe and the read-only catalogue are public, the PWA is
 * served on {@code GET}, and every other request is denied until accounts exist.
 */
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) {
		return http
			.authorizeHttpRequests(requests -> requests
				.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
				.requestMatchers("/actuator/**").denyAll()
				.requestMatchers(HttpMethod.GET, "/api/stadiums").permitAll()
				.requestMatchers("/api/**").denyAll()
				.requestMatchers(HttpMethod.GET, "/**").permitAll()
				.requestMatchers(HttpMethod.HEAD, "/**").permitAll()
				.anyRequest().denyAll())
			.csrf(csrf -> csrf.spa())
			.httpBasic(basic -> basic.disable())
			.formLogin(form -> form.disable())
			.build();
	}

}
