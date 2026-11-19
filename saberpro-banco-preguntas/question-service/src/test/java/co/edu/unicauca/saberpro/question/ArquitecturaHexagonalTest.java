package co.edu.unicauca.saberpro.question;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica la regla de dependencias del hexágono leyendo los imports de los fuentes. */
class ArquitecturaHexagonalTest {

    private static final Path RAIZ = Path.of("src/main/java/co/edu/unicauca/saberpro/question");

    private static final List<String> FRAMEWORKS = List.of(
            "org.springframework", "jakarta.persistence", "javax.persistence", "org.hibernate",
            "com.fasterxml.jackson", "lombok");

    private static List<Path> fuentes(String paquete) throws IOException {
        Path dir = RAIZ.resolve(paquete);
        assertTrue(Files.exists(dir), "Falta el paquete " + paquete + " (ejecutar desde la raíz del proyecto)");
        try (Stream<Path> s = Files.walk(dir)) {
            return s.filter(p -> p.toString().endsWith(".java")).toList();
        }
    }

    private static void prohibir(String paquete, List<String> prohibidos) throws IOException {
        for (Path f : fuentes(paquete)) {
            String codigo = Files.readString(f);
            for (String p : prohibidos) {
                assertFalse(codigo.contains("import " + p), paquete + "/" + f.getFileName() + " importa " + p);
            }
        }
    }

    @Test
    void elDominioNoDependeDeFrameworksNiDeCapasExternas() throws IOException {
        prohibir("domain", FRAMEWORKS);
        prohibir("domain", List.of("co.edu.unicauca.saberpro.question.application", "co.edu.unicauca.saberpro.question.infrastructure"));
    }

    @Test
    void elDominioNoUsaAnotacionesTecnicas() throws IOException {
        for (Path f : fuentes("domain")) {
            String codigo = Files.readString(f);
            for (String a : List.of("@Entity", "@Table", "@Id", "@Component", "@Service", "@Autowired", "@Bean")) {
                assertFalse(codigo.contains(a), f.getFileName() + " usa " + a);
            }
        }
    }

    @Test
    void laAplicacionNoDependeDeFrameworksNiDeInfraestructura() throws IOException {
        prohibir("application", FRAMEWORKS);
        prohibir("application", List.of("co.edu.unicauca.saberpro.question.infrastructure"));
    }

    @Test
    void losPuertosEstanEnDomainPorts() {
        assertTrue(Files.exists(RAIZ.resolve("domain/ports/inbound/CrearPreguntaUseCase.java")));
        assertTrue(Files.exists(RAIZ.resolve("domain/ports/inbound/EvaluarPreguntaUseCase.java")));
        assertTrue(Files.exists(RAIZ.resolve("domain/ports/outbound/PreguntaRepositoryPort.java")));
        assertTrue(Files.exists(RAIZ.resolve("domain/ports/outbound/NotificacionPort.java")));
    }

    @Test
    void elModeloDeDominioTieneLasClasesDelEnunciado() {
        for (String c : List.of("Question", "AnswerOption", "Enunciado", "Justificacion")) {
            assertTrue(Files.exists(RAIZ.resolve("domain/model/" + c + ".java")), "Falta domain.model." + c);
        }
        assertTrue(Files.exists(RAIZ.resolve("domain/exception/ReglaNegocioException.java")));
    }

    @Test
    void elPublicadorRabbitImplementaElPuertoDeNotificacion() throws IOException {
        assertTrue(Files.readString(RAIZ.resolve("infrastructure/messaging/RabbitMQPublisher.java"))
                .contains("implements NotificacionPort"));
    }

    @Test
    void elMicroservicioTieneDockerfileBasadoEnOpenJdk17() throws IOException {
        Path dockerfile = Path.of("Dockerfile");
        assertTrue(Files.exists(dockerfile), "Falta el Dockerfile en la raíz del microservicio");
        String c = Files.readString(dockerfile);
        assertTrue(c.contains("FROM eclipse-temurin:17-jdk-jammy"));
        assertTrue(c.contains("EXPOSE 8083"));
        assertTrue(c.contains("ENTRYPOINT"));
    }

    @Test
    void losPuertosSonInterfaces() throws IOException {
        for (String f : List.of("domain/ports/inbound/CrearPreguntaUseCase.java",
                "domain/ports/inbound/ConsultarPreguntaUseCase.java",
                "domain/ports/inbound/EvaluarPreguntaUseCase.java",
                "domain/ports/outbound/PreguntaRepositoryPort.java",
                "domain/ports/outbound/NotificacionPort.java",
                "domain/ports/outbound/ValidacionPort.java")) {
            assertTrue(Files.readString(RAIZ.resolve(f)).contains("public interface"), f + " debe ser una interfaz");
        }
    }

    @Test
    void ambosAdaptadoresDeSalidaImplementanElMismoPuerto() throws IOException {
        for (String f : List.of("infrastructure/jpa/JpaQuestionAdapter.java",
                "infrastructure/memory/InMemoryQuestionAdapter.java")) {
            assertTrue(Files.readString(RAIZ.resolve(f)).contains("implements PreguntaRepositoryPort"), f);
        }
    }

    @Test
    void elControladorSoloDependeDePuertosDeEntrada() throws IOException {
        String codigo = Files.readString(RAIZ.resolve("api/QuestionRestController.java"));
        assertFalse(codigo.contains("QuestionApplicationService"), "El controlador no debe conocer el servicio concreto");
        assertFalse(codigo.contains("PreguntaRepositoryPort"), "El controlador no debe tocar la persistencia");
        assertFalse(codigo.contains("infrastructure.jpa"));
        assertFalse(codigo.contains("infrastructure.memory"));
    }
}
