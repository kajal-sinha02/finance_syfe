package com.finance.finance;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalFinanceManagerApplication {

    public static void main(String[] args) {
        // 🔄 Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("./")  // Location of .env file
                .filename(".env") // Name of the env file
                .ignoreIfMissing()
                .load();

        // 🐞 Debug print (optional)
        System.out.println("✅ DATABASE_URL loaded: " + dotenv.get("DATABASE_URL"));

        SpringApplication.run(PersonalFinanceManagerApplication.class, args);
    }
}