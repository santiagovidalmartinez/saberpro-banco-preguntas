package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;

import java.util.List;

/** PUERTO DE ENTRADA (driving): consultas sobre el banco de preguntas. */
public interface ConsultarPreguntaUseCase {

    Question obtener(QuestionId id);

    List<Question> listar();
}
