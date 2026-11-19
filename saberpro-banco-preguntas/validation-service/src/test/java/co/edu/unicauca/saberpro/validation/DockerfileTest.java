package co.edu.unicauca.saberpro.validation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DockerfileTest {
    @Test
    void tieneDockerfileConImagenOpenJdk17YPuerto8082() throws IOException {
        String c = Files.readString(Path.of("Dockerfile"));
        assertTrue(c.contains("FROM eclipse-temurin:17-jdk-jammy"));
        assertTrue(c.contains("EXPOSE 8082"));
        assertTrue(c.contains("ENTRYPOINT"));
    }
}
