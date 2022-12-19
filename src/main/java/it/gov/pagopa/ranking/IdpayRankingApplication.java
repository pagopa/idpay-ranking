package it.gov.pagopa.ranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IdpayRankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdpayRankingApplication.class, args);
	}

}
