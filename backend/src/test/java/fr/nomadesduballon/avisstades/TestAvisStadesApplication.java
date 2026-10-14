package fr.nomadesduballon.avisstades;

import org.springframework.boot.SpringApplication;

public class TestAvisStadesApplication {

	public static void main(String[] args) {
		SpringApplication.from(AvisStadesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
