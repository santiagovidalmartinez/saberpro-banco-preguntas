package co.edu.unicauca.saberpro.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Validation Service (:8082): valida enunciado y justificación antes de crear una pregunta. */
@SpringBootApplication
public class ValidationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidationServiceApplication.class, args);
    }
}
