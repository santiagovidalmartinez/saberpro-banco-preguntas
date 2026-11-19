package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.JpaQuestionAdapter;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.SpringDataQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/** El adaptador JPA (H2) cumple EXACTAMENTE el mismo contrato que el adaptador en memoria. */
@DataJpaTest
class JpaQuestionAdapterTest extends QuestionRepositoryPortContractTest {

    @Autowired
    private SpringDataQuestionRepository springData;

    @Override
    protected PreguntaRepositoryPort crearRepositorio() {
        return new JpaQuestionAdapter(springData);
    }
}
