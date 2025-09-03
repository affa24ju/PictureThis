package com.PictureThis.PictureThis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PictureThisApplication {

	public static void main(String[] args) {
		// Dotenv dotenv = Dotenv.configure().load();
		SpringApplication.run(PictureThisApplication.class, args);
	}

}
