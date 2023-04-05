package com.frenchtoast.iws.aws.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ImportResource("classpath:camel-context.xml")
public class IwsAwsMigrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(IwsAwsMigrationApplication.class, args);
	}

	
}
