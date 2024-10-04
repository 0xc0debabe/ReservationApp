package reservation.hmw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class HmwApplication {

	public static void main(String[] args) {
		SpringApplication.run(HmwApplication.class, args);
	}

}
