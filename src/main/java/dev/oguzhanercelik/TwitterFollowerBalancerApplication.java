package dev.oguzhanercelik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TwitterFollowerBalancerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterFollowerBalancerApplication.class, args);
	}

}
