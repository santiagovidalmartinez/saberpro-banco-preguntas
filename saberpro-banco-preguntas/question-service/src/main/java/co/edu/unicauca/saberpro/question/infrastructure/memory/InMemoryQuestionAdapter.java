package co.edu.unicauca.saberpro.question.infrastructure.memory;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ADAPTADOR DE SALIDA (memoria): implementa el MISMO puerto con un HashMap, sin base de datos.
 * Guarda y devuelve copias para comportarse como una persistencia real: modificar el objeto
 * devuelto no cambia lo almacenado hasta volver a llamar a save().
 */
public class InMemoryQuestionAdapter implements PreguntaRepositoryPort {

    private final Map<QuestionId, Question> almacen = new ConcurrentHashMap<>();

    @Override
    public Question save(Question pregunta) {
        almacen.put(pregunta.getId(), copiar(pregunta));
        return copiar(pregunta);
    }

    @Override
    public Optional<Question> findById(QuestionId id) {
        return Optional.ofNullable(almacen.get(id)).map(InMemoryQuestionAdapter::copiar);
    }

    @Override
    public List<Question> findAll() {
        List<Question> todas = new ArrayList<>();
        for (Question p : almacen.values()) {
            todas.add(copiar(p));
        }
        return todas;
    }

    private static Question copiar(Question p) {
        return Question.reconstruir(p.getId(), p.getEnunciado(), p.getJustificacion(),
                p.getOpciones(), p.getEstado());
    }
}
