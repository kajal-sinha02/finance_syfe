package com.finance.finance;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalFinanceManagerApplication {
    public static void main(String[] args) {
        // Load .env
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .filename(".env")
                .ignoreIfMissing()
                .load();

        // Inject into system properties so Spring can resolve ${...}
        dotenv.entries().forEach(entry -> {
            if (System.getProperty(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });

        // Optional debug logs
        System.out.println("✅ Injected DATABASE_URL = " + System.getProperty("DATABASE_URL"));

        SpringApplication.run(PersonalFinanceManagerApplication.class, args);
    }
}
