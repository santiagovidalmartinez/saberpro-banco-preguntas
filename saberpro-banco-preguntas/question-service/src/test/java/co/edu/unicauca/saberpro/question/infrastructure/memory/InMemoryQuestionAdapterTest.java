package co.edu.unicauca.saberpro.question.infrastructure.memory;

import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.QuestionRepositoryPortContractTest;

/** El adaptador en memoria cumple el contrato del puerto (sin Spring, sin base de datos). */
class InMemoryQuestionAdapterTest extends QuestionRepositoryPortContractTest {
    @Override
    protected PreguntaRepositoryPort crearRepositorio() {
        return new InMemoryQuestionAdapter();
    }
}
