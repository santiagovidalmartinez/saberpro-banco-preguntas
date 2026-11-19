package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.ConsultarPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.OpcionCommand;
import co.edu.unicauca.saberpro.question.api.dto.CrearPreguntaRequest;
import co.edu.unicauca.saberpro.question.api.dto.PreguntaResponse;
import co.edu.unicauca.saberpro.question.api.dto.EnvioRevisionResponse;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.EvaluarPreguntaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * ADAPTADOR DE ENTRADA (driving): traduce HTTP/JSON a llamadas a los puertos de entrada.
 * Depende solo de interfaces del dominio, nunca del servicio concreto ni del repositorio.
 */
@RestController
@RequestMapping("/api/preguntas")
public class QuestionRestController {

    private final CrearPreguntaUseCase crear;
    private final ConsultarPreguntaUseCase consultar;
    private final EvaluarPreguntaUseCase evaluar;

    public QuestionRestController(CrearPreguntaUseCase crear, ConsultarPreguntaUseCase consultar,
                                  EvaluarPreguntaUseCase evaluar) {
        this.crear = crear;
        this.consultar = consultar;
        this.evaluar = evaluar;
    }

    @PostMapping
    public ResponseEntity<PreguntaResponse> crear(@RequestBody CrearPreguntaRequest peticion) {
        List<OpcionCommand> opciones = peticion.opciones() == null ? List.of()
                : peticion.opciones().stream().map(o -> new OpcionCommand(o.texto(), o.correcta())).toList();
        Question creada = crear.crear(
                new CrearPreguntaCommand(peticion.enunciado(), peticion.justificacion(), opciones));
        return ResponseEntity.created(URI.create("/api/preguntas/" + creada.getId().valor()))
                .body(PreguntaResponse.desde(creada));
    }

    /** 202 Accepted: la revisión se despacha de forma asíncrona a la cola de RabbitMQ. */
    @PostMapping("/{id}/enviar-revision")
    public ResponseEntity<EnvioRevisionResponse> enviarARevision(@PathVariable String id) {
        CrearPreguntaUseCase.EnvioRevision envio = crear.enviarARevision(QuestionId.de(id));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                new EnvioRevisionResponse(envio.operationId(), "ACCEPTED", PreguntaResponse.desde(envio.pregunta())));
    }

    @PostMapping("/{id}/aprobar")
    public PreguntaResponse aprobar(@PathVariable String id) {
        return PreguntaResponse.desde(evaluar.aprobar(QuestionId.de(id)));
    }

    @PostMapping("/{id}/rechazar")
    public PreguntaResponse rechazar(@PathVariable String id) {
        return PreguntaResponse.desde(evaluar.rechazar(QuestionId.de(id)));
    }

    @GetMapping("/{id}")
    public PreguntaResponse obtener(@PathVariable String id) {
        return PreguntaResponse.desde(consultar.obtener(QuestionId.de(id)));
    }

    @GetMapping
    public List<PreguntaResponse> listar() {
        return consultar.listar().stream().map(PreguntaResponse::desde).toList();
    }
}
