package co.edu.unicauca.saberpro.validation;

import co.edu.unicauca.saberpro.validation.api.ValidationRestController;
import co.edu.unicauca.saberpro.validation.api.ValidationRestController.ValidationRequest;
import co.edu.unicauca.saberpro.validation.api.ValidationRestController.ValidationResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationRestControllerTest {

    private final ValidationRestController c = new ValidationRestController();

    @Test
    void aceptaUnaPreguntaCorrecta() {
        ValidationResponse r = c.validar(new ValidationRequest("¿Cuál es la capital de Colombia?",
                "Bogotá es la capital y sede del gobierno nacional.", List.of("Medellín", "Bogotá", "Cali", "Popayán")));
        assertTrue(r.valido());
        assertTrue(r.errores().isEmpty());
    }

    @Test
    void rechazaEnunciadoCortoYJustificacionCorta() {
        ValidationResponse r = c.validar(new ValidationRequest("corto", "corta", List.of()));
        assertFalse(r.valido());
        assertEquals(2, r.errores().size());
    }

    @Test
    void rechazaExpresionesProhibidasSinImportarTildesNiMayusculas() {
        ValidationResponse r = c.validar(new ValidationRequest("¿Enunciado suficientemente largo?",
                "Es la respuesta correcta, ÓBVIAMENTE no, EVIDENTEMENTE sí porque es así.", List.of()));
        assertFalse(r.valido());
        assertEquals(2, r.errores().size());
        assertTrue(r.errores().stream().anyMatch(e -> e.contains("obviamente")));
        assertTrue(r.errores().stream().anyMatch(e -> e.contains("evidentemente")));
    }

    @Test
    void rechazaOpcionesRepetidasOVacias() {
        ValidationResponse r = c.validar(new ValidationRequest("¿Enunciado suficientemente largo?",
                "Justificación suficientemente larga", List.of("A", "a", " ")));
        assertFalse(r.valido());
        assertEquals(2, r.errores().size());
    }

    @Test
    void rechazaCamposNulos() {
        assertFalse(c.validar(new ValidationRequest(null, null, null)).valido());
    }

    @Test
    void healthEstaUp() {
        assertEquals("UP", c.health().get("status"));
    }
}
