package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.question.domain.exception.PreguntaNoEncontradaException;
import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.question.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.OpcionCommand;
import co.edu.unicauca.saberpro.question.infrastructure.memory.InMemoryQuestionAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.saberpro.question.domain.exception.ServicioNoDisponibleException;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionPort;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.EnvioRevision;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Prueba del caso de uso sin Spring, sin JPA y sin base de datos: solo el adaptador en memoria. */
class QuestionApplicationServiceTest {

    private QuestionApplicationService servicio;
    private final List<String> notificados = new ArrayList<>();
    private boolean validacionAprueba;

    @BeforeEach
    void preparar() {
        notificados.clear();
        validacionAprueba = true;
        NotificacionPort notificacion = (pregunta, operationId) -> notificados.add(pregunta.getId().valor() + "/" + operationId);
        ValidacionPort validacion = (enunciado, justificacion, opciones) -> {
            if (!validacionAprueba) {
                throw new ReglaNegocioException("rechazada por el Validation Service");
            }
        };
        servicio = new QuestionApplicationService(new InMemoryQuestionAdapter(), notificacion, validacion);
    }

    static CrearPreguntaCommand comandoCompleto() {
        return new CrearPreguntaCommand("¿Cuál es la capital de Colombia?",
                "Bogotá es la capital y sede del gobierno nacional.",
                List.of(new OpcionCommand("Medellín", false), new OpcionCommand("Bogotá", true),
                        new OpcionCommand("Cali", false), new OpcionCommand("Popayán", false)));
    }

    @Test
    void creaUnaPreguntaEnBorradorYLaPersiste() {
        Question creada = servicio.crear(comandoCompleto());
        assertEquals(EstadoPregunta.BORRADOR, creada.getEstado());
        assertEquals(4, creada.getOpciones().size());
        assertEquals(creada.getId(), servicio.obtener(creada.getId()).getId());
    }

    @Test
    void enviaARevisionUnaPreguntaCompleta() {
        Question creada = servicio.crear(comandoCompleto());
        EnvioRevision envio = servicio.enviarARevision(creada.getId());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, envio.pregunta().getEstado());
        assertNotNull(envio.operationId());
        assertEquals(List.of(creada.getId().valor() + "/" + envio.operationId()), notificados,
                "Se publicó exactamente un evento");
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, servicio.obtener(creada.getId()).getEstado());
    }

    @Test
    void noEnviaARevisionUnaPreguntaIncompletaYSuEstadoNoCambia() {
        Question creada = servicio.crear(new CrearPreguntaCommand("¿Enunciado?",
                "Una justificación suficientemente larga", List.of(new OpcionCommand("A", true))));
        assertThrows(ReglaNegocioException.class, () -> servicio.enviarARevision(creada.getId()));
        assertEquals(EstadoPregunta.BORRADOR, servicio.obtener(creada.getId()).getEstado());
    }

    @Test
    void noCreaUnaPreguntaConJustificacionCorta() {
        CrearPreguntaCommand malo = new CrearPreguntaCommand("¿Enunciado?", "corta", List.of());
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(malo));
        assertTrue(servicio.listar().isEmpty(), "Nada se guarda si una regla falla");
    }

    @Test
    void noCreaUnaPreguntaConMasDeCuatroOpciones() {
        CrearPreguntaCommand malo = new CrearPreguntaCommand("¿Enunciado?", "Justificación suficientemente larga",
                List.of(new OpcionCommand("A", true), new OpcionCommand("B", false), new OpcionCommand("C", false),
                        new OpcionCommand("D", false), new OpcionCommand("E", false)));
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(malo));
    }

    @Test
    void obtenerUnaPreguntaInexistenteLanzaExcepcionDeDominio() {
        assertThrows(PreguntaNoEncontradaException.class, () -> servicio.obtener(QuestionId.nuevo()));
        assertThrows(PreguntaNoEncontradaException.class, () -> servicio.enviarARevision(QuestionId.nuevo()));
    }

    @Test
    void listaTodasLasPreguntas() {
        servicio.crear(comandoCompleto());
        servicio.crear(comandoCompleto());
        assertEquals(2, servicio.listar().size());
    }

    @Test
    void siElValidationServiceRechazaNoSeGuardaNada() {
        validacionAprueba = false;
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(comandoCompleto()));
        assertTrue(servicio.listar().isEmpty());
    }

    @Test
    void siFallaLaValidacionRemotaSePropagaServicioNoDisponible() {
        ValidacionPort caida = (e, j, o) -> { throw new ServicioNoDisponibleException("caído"); };
        QuestionApplicationService s = new QuestionApplicationService(new InMemoryQuestionAdapter(), (p, id) -> { }, caida);
        assertThrows(ServicioNoDisponibleException.class, () -> s.crear(comandoCompleto()));
    }

    @Test
    void noPublicaEventoSiLaPreguntaNoPuedeEnviarse() {
        Question incompleta = servicio.crear(new CrearPreguntaCommand("¿Enunciado?",
                "Una justificación suficientemente larga", List.of(new OpcionCommand("A", true))));
        assertThrows(ReglaNegocioException.class, () -> servicio.enviarARevision(incompleta.getId()));
        assertTrue(notificados.isEmpty(), "Sin evento si el dominio rechaza el envío");
    }

    @Test
    void apruebaYRechazaSoloPreguntasPendientes() {
        Question a = servicio.crear(comandoCompleto());
        assertThrows(ReglaNegocioException.class, () -> servicio.aprobar(a.getId()));
        servicio.enviarARevision(a.getId());
        assertEquals(EstadoPregunta.APROBADA, servicio.aprobar(a.getId()).getEstado());

        Question b = servicio.crear(comandoCompleto());
        servicio.enviarARevision(b.getId());
        assertEquals(EstadoPregunta.RECHAZADA, servicio.rechazar(b.getId()).getEstado());
    }
}
