package co.edu.unicauca.saberpro.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Question Service (:8083): microservicio principal, con dominio DDD puro y arquitectura hexagonal. */
@SpringBootApplication
public class QuestionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuestionServiceApplication.class, args);
    }
}
