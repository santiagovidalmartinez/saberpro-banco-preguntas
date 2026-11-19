package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR DE SALIDA (JPA): implementa el puerto del dominio con Spring Data JPA sobre H2.
 * No se anota como componente: HexagonalConfiguration decide si se activa.
 */
public class JpaQuestionAdapter implements PreguntaRepositoryPort {

    private final SpringDataQuestionRepository repositorio;

    public JpaQuestionAdapter(SpringDataQuestionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional
    public Question save(Question pregunta) {
        QuestionRecord guardada = repositorio.save(QuestionDomainMapper.aEntidad(pregunta));
        return QuestionDomainMapper.aDominio(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(QuestionId id) {
        return repositorio.findById(id.valor()).map(QuestionDomainMapper::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll() {
        return repositorio.findAll().stream().map(QuestionDomainMapper::aDominio).toList();
    }
}
