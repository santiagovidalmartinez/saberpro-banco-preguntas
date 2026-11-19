package co.edu.unicauca.saberpro.question.infrastructure.rest;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.question.domain.exception.ServicioNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** El adaptador REST hacia el Validation Service, probado sin levantar el servicio real. */
class ValidationRestClientTest {

    private MockRestServiceServer servidor;
    private ValidationRestClient cliente;

    @BeforeEach
    void preparar() {
        RestTemplate http = new RestTemplate();
        servidor = MockRestServiceServer.bindTo(http).build();
        cliente = new ValidationRestClient(http, "http://validation-service:8082");
    }

    @Test
    void aceptaCuandoElServicioDiceValido() {
        servidor.expect(requestTo("http://validation-service:8082/validate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"valido\":true,\"errores\":[]}", MediaType.APPLICATION_JSON));
        assertDoesNotThrow(() -> cliente.validar("¿Enunciado válido?", "Justificación suficientemente larga", List.of("A", "B")));
        servidor.verify();
    }

    @Test
    void lanzaReglaNegocioConLosErroresDelServicio() {
        servidor.expect(requestTo("http://validation-service:8082/validate"))
                .andRespond(withSuccess("{\"valido\":false,\"errores\":[\"error uno\",\"error dos\"]}", MediaType.APPLICATION_JSON));
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> cliente.validar("x", "y", List.of()));
        assertEquals("error uno | error dos", ex.getMessage());
    }

    @Test
    void lanzaServicioNoDisponibleSiElServicioFalla() {
        servidor.expect(requestTo("http://validation-service:8082/validate")).andRespond(withServerError());
        assertThrows(ServicioNoDisponibleException.class, () -> cliente.validar("x", "y", List.of()));
    }

    @Test
    void lanzaServicioNoDisponibleSiLaRespuestaEsVacia() {
        servidor.expect(requestTo("http://validation-service:8082/validate"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        assertThrows(ServicioNoDisponibleException.class, () -> cliente.validar("x", "y", List.of()));
    }

    private static org.springframework.test.web.client.ResponseCreator withStatus(HttpStatus s) {
        return org.springframework.test.web.client.response.MockRestResponseCreators.withStatus(s);
    }
}
