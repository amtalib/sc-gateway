package bpi.api.gateway;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author Abdulhamid Talib
 * @since 2025-03-07
 * 
 * Main thread.
 * 
 * Depedencies:
 * 		1. Spring sc-eureka
 * 		2. Redis for the pub/sub (Required if logging to DB is activated)
 * 		3. Token API
 * 		4. Logger API (required if activated)
 * 
 * Spring cloud gateway: Supports the following
 * 		1. Routing of requests via YML configuration.
 * 		2. Round-robin load balancing  
 * 		3. Automatic fail-over
 * 		4. Circuit-breaker
 * 
 * Additional functionalities:
 * 		1. Validates API-Token - PENDING(integrate to token-api)
 * 		2. Generates unique TranRefNo and add to headers
 * 		3. Logs transactions - PENDING(integrate to logger api and pub/sub)
 * 		4. Rate limiter - limits specific user requests to 10 per second. - ONGOING
 * 		  
 * */
//Register as Spring Boot Application
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	CommandLineRunner runner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Beans loaded in ApplicationContext: ");
			for (String beanName : ctx.getBeanDefinitionNames() ) {
				System.out.println("-> " + beanName);
			}
		};
	} // End method
 
}

 