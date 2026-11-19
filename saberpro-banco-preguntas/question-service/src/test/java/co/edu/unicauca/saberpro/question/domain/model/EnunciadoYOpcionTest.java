package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnunciadoYOpcionTest {

    @Test
    void elEnunciadoNoPuedeSerNuloNiVacio() {
        assertThrows(ReglaNegocioException.class, () -> new Enunciado(null));
        assertThrows(ReglaNegocioException.class, () -> new Enunciado("   "));
    }

    @Test
    void elEnunciadoSeRecortaYEsInmutable() {
        Enunciado e = new Enunciado("  ¿Qué es un agregado?  ");
        assertEquals("¿Qué es un agregado?", e.getTexto());
        assertEquals(e, new Enunciado("¿Qué es un agregado?"));
    }

    @Test
    void laOpcionExigeTexto() {
        assertThrows(ReglaNegocioException.class, () -> new AnswerOption(null, true));
        assertThrows(ReglaNegocioException.class, () -> new AnswerOption("  ", false));
    }

    @Test
    void laOpcionGuardaTextoYClave() {
        AnswerOption o = new AnswerOption(" Bogotá ", true);
        assertEquals("Bogotá", o.getTexto());
        assertTrue(o.isEsCorrecta());
        assertEquals(o, new AnswerOption("Bogotá", true));
        assertNotEquals(o, new AnswerOption("Bogotá", false));
    }
}
