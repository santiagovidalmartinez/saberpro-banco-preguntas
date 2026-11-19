package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;

import java.util.List;
import java.util.Optional;

/**
 * PUERTO DE SALIDA (driven): lo que el núcleo necesita para persistir preguntas.
 * No menciona JPA, SQL ni ninguna tecnología: solo operaciones puras del dominio.
 */
public interface PreguntaRepositoryPort {

    Question save(Question pregunta);

    Optional<Question> findById(QuestionId id);

    List<Question> findAll();
}
