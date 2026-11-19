package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionIdTest {

    @Test
    void dosIdentificadoresNuevosSonDistintos() {
        assertNotEquals(QuestionId.nuevo(), QuestionId.nuevo());
    }

    @Test
    void unIdentificadorSeReconstruyeDesdeSuTexto() {
        QuestionId id = QuestionId.nuevo();
        assertEquals(id, QuestionId.de(id.valor()));
        assertEquals(id.hashCode(), QuestionId.de(id.valor()).hashCode());
    }

    @Test
    void rechazaTextosQueNoSonUuid() {
        assertThrows(ReglaNegocioException.class, () -> QuestionId.de("123"));
        assertThrows(ReglaNegocioException.class, () -> QuestionId.de(" "));
        assertThrows(ReglaNegocioException.class, () -> QuestionId.de(null));
    }

    @Test
    void reconstruirConservaElEstadoSinRevalidarElCicloDeVida() {
        QuestionId id = QuestionId.nuevo();
        Question p = Question.reconstruir(id, new Enunciado("¿Question?"),
                new Justificacion("Justificación suficientemente larga"),
                java.util.List.of(new AnswerOption("A", true)), EstadoPregunta.APROBADA);
        assertEquals(id, p.getId());
        assertEquals(EstadoPregunta.APROBADA, p.getEstado());
        assertEquals(1, p.getOpciones().size());
    }
}
