package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CONTRATO del puerto de salida. Las mismas pruebas se ejecutan contra JpaQuestionAdapter y
 * contra InMemoryQuestionAdapter: si ambas pasan, los adaptadores son intercambiables.
 */
public abstract class QuestionRepositoryPortContractTest {

    protected abstract PreguntaRepositoryPort crearRepositorio();

    private static Question preguntaCompleta() {
        Question p = new Question();
        p.definirEnunciado("¿Cuál es la capital de Colombia?");
        p.definirJustificacion("Bogotá es la capital y sede del gobierno nacional.");
        p.agregarOpcion("Medellín", false);
        p.agregarOpcion("Bogotá", true);
        p.agregarOpcion("Cali", false);
        p.agregarOpcion("Popayán", false);
        return p;
    }

    @Test
    void guardaYRecuperaElAgregadoCompleto() {
        PreguntaRepositoryPort repo = crearRepositorio();
        Question original = preguntaCompleta();
        repo.save(original);

        Question leida = repo.findById(original.getId()).orElseThrow();
        assertEquals(original.getId(), leida.getId());
        assertEquals("¿Cuál es la capital de Colombia?", leida.getEnunciado().getTexto());
        assertEquals("Bogotá es la capital y sede del gobierno nacional.", leida.getJustificacion().getTexto());
        assertEquals(EstadoPregunta.BORRADOR, leida.getEstado());
        assertEquals(original.getOpciones(), leida.getOpciones(), "Las 4 opciones se conservan en orden");
    }

    @Test
    void devuelveVacioSiLaPreguntaNoExiste() {
        Optional<Question> r = crearRepositorio().findById(QuestionId.nuevo());
        assertTrue(r.isEmpty());
    }

    @Test
    void guardarDeNuevoActualizaElEstado() {
        PreguntaRepositoryPort repo = crearRepositorio();
        Question p = preguntaCompleta();
        repo.save(p);

        Question leida = repo.findById(p.getId()).orElseThrow();
        leida.enviarARevision();
        repo.save(leida);

        Question releida = repo.findById(p.getId()).orElseThrow();
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, releida.getEstado());
        assertEquals(4, releida.getOpciones().size(), "Actualizar no duplica ni pierde opciones");
    }

    @Test
    void listaTodasLasPreguntasGuardadas() {
        PreguntaRepositoryPort repo = crearRepositorio();
        Question a = preguntaCompleta();
        Question b = preguntaCompleta();
        repo.save(a);
        repo.save(b);

        List<Question> todas = repo.findAll();
        Set<QuestionId> ids = todas.stream().map(Question::getId).collect(Collectors.toSet());
        assertEquals(Set.of(a.getId(), b.getId()), ids);
    }

    @Test
    void guardaTambienUnBorradorIncompleto() {
        PreguntaRepositoryPort repo = crearRepositorio();
        Question borrador = new Question();
        borrador.definirEnunciado("Solo tengo enunciado");
        repo.save(borrador);

        Question leida = repo.findById(borrador.getId()).orElseThrow();
        assertEquals("Solo tengo enunciado", leida.getEnunciado().getTexto());
        assertNull(leida.getJustificacion());
        assertTrue(leida.getOpciones().isEmpty());
    }

    @Test
    void modificarLoDevueltoNoCambiaLoAlmacenadoHastaGuardar() {
        PreguntaRepositoryPort repo = crearRepositorio();
        Question p = preguntaCompleta();
        repo.save(p);

        Question copia = repo.findById(p.getId()).orElseThrow();
        copia.enviarARevision();   // sin save()

        assertEquals(EstadoPregunta.BORRADOR, repo.findById(p.getId()).orElseThrow().getEstado());
    }
}
