package org.eclipse.scout.boot.tabularius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ TabulariusServletConfiguration.class })
@ComponentScan(basePackages = "org.eclipse.scout.boot")
public class Tabularius {

	public static void main(final String[] args) {
		SpringApplication.run(Tabularius.class, args);
	}

}
