package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.question.domain.exception.PreguntaNoEncontradaException;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.ConsultarPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.EvaluarPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionPort;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación: implementa los puertos de entrada y orquesta el dominio usando
 * únicamente puertos de salida. Java puro: no es un componente de Spring; se registra en
 * HexagonalConfiguration.
 */
public class QuestionApplicationService
        implements CrearPreguntaUseCase, ConsultarPreguntaUseCase, EvaluarPreguntaUseCase {

    private final PreguntaRepositoryPort repositorio;
    private final NotificacionPort notificacion;
    private final ValidacionPort validacion;

    public QuestionApplicationService(PreguntaRepositoryPort repositorio,
                                      NotificacionPort notificacion,
                                      ValidacionPort validacion) {
        this.repositorio = repositorio;
        this.notificacion = notificacion;
        this.validacion = validacion;
    }

    @Override
    public Question crear(CrearPreguntaCommand comando) {
        List<OpcionCommand> opciones = comando.opciones() == null ? List.of() : comando.opciones();
        validacion.validar(comando.enunciado(), comando.justificacion(),
                opciones.stream().map(OpcionCommand::texto).toList());

        Question pregunta = new Question();
        pregunta.definirEnunciado(comando.enunciado());
        pregunta.definirJustificacion(comando.justificacion());
        for (OpcionCommand opcion : opciones) {
            pregunta.agregarOpcion(opcion.texto(), opcion.correcta());
        }
        return repositorio.save(pregunta);
    }

    @Override
    public EnvioRevision enviarARevision(QuestionId id) {
        Question pregunta = buscar(id);
        pregunta.enviarARevision();
        Question guardada = repositorio.save(pregunta);
        String operationId = UUID.randomUUID().toString();
        notificacion.publicarEnvioARevision(guardada, operationId);
        return new EnvioRevision(operationId, guardada);
    }

    @Override
    public Question aprobar(QuestionId id) {
        Question pregunta = buscar(id);
        pregunta.aprobar();
        return repositorio.save(pregunta);
    }

    @Override
    public Question rechazar(QuestionId id) {
        Question pregunta = buscar(id);
        pregunta.rechazar();
        return repositorio.save(pregunta);
    }

    @Override
    public Question obtener(QuestionId id) {
        return buscar(id);
    }

    @Override
    public List<Question> listar() {
        return repositorio.findAll();
    }

    private Question buscar(QuestionId id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new PreguntaNoEncontradaException(id.valor()));
    }
}
