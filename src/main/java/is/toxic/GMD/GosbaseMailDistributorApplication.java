package is.toxic.GMD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@PropertySource("file:config/application.yml")
public class GosbaseMailDistributorApplication {

	public static void main(String[] args) {
		SpringApplication.run(GosbaseMailDistributorApplication.class, args);
	}

}
