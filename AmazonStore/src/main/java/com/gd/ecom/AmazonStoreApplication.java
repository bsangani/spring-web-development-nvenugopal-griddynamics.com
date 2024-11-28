package com.gd.ecom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
@SpringBootApplication
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 1000)
public class AmazonStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmazonStoreApplication.class, args);
	}
}
