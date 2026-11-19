package co.edu.unicauca.saberpro.lifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Lifecycle Service (:8084): consume los eventos de revisión publicados por el Question Service. */
@SpringBootApplication
public class LifecycleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifecycleServiceApplication.class, args);
    }
}
