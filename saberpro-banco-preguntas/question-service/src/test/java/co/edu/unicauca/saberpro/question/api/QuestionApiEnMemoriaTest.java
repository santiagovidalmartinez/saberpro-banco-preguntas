package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.infrastructure.memory.InMemoryQuestionAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** PASO 2 y 3 del taller: se activa InMemoryQuestionAdapter y las MISMAS pruebas siguen pasando. */
@SpringBootTest(properties = {"saberpro.persistence=memory", "spring.datasource.url=jdbc:h2:mem:test-memory",
        "saberpro.messaging.enabled=false", "saberpro.validation.enabled=false"})
@AutoConfigureMockMvc
class QuestionApiEnMemoriaTest extends QuestionApiContractTest {

    @Test
    void elAdaptadorActivoEsEnMemoria() {
        assertInstanceOf(InMemoryQuestionAdapter.class, adaptadorActivo);
    }
}
