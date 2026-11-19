package co.edu.unicauca.saberpro.question.infrastructure.config;

import co.edu.unicauca.saberpro.question.application.QuestionApplicationService;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionPort;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.JpaQuestionAdapter;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.SpringDataQuestionRepository;
import co.edu.unicauca.saberpro.question.infrastructure.memory.InMemoryQuestionAdapter;
import co.edu.unicauca.saberpro.question.infrastructure.messaging.LogNotificacionAdapter;
import co.edu.unicauca.saberpro.question.infrastructure.messaging.RabbitMQPublisher;
import co.edu.unicauca.saberpro.question.infrastructure.rest.ValidationRestClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * ÚNICO lugar donde se decide qué adaptador usa cada puerto de salida del núcleo.
 *  - saberpro.persistence = jpa | memory  (por defecto: jpa)
 *  - saberpro.messaging.enabled = true | false  (RabbitMQ real o solo log)
 *  - saberpro.validation.enabled = true | false (Validation Service real o sin validación remota)
 * Cambiar de adaptador NO toca ni una línea de los paquetes domain y application.
 */
@Configuration
public class HexagonalConfiguration {

    /** Adaptador JPA (H2 o PostgreSQL según el perfil). */
    @Bean
    @ConditionalOnProperty(name = "saberpro.persistence", havingValue = "jpa", matchIfMissing = true)
    public PreguntaRepositoryPort preguntaRepositoryPort(SpringDataQuestionRepository repositorio) {
        return new JpaQuestionAdapter(repositorio);
    }

    /** Adaptador en memoria (HashMap). */
    @Bean
    @ConditionalOnProperty(name = "saberpro.persistence", havingValue = "memory")
    public PreguntaRepositoryPort preguntaRepositoryPortEnMemoria() {
        return new InMemoryQuestionAdapter();
    }

    @Bean
    @ConditionalOnProperty(name = "saberpro.messaging.enabled", havingValue = "true", matchIfMissing = true)
    public NotificacionPort notificacionRabbit(AmqpTemplate plantilla) {
        return new RabbitMQPublisher(plantilla);
    }

    @Bean
    @ConditionalOnProperty(name = "saberpro.messaging.enabled", havingValue = "false")
    public NotificacionPort notificacionLog() {
        return new LogNotificacionAdapter();
    }

    @Bean
    @ConditionalOnProperty(name = "saberpro.validation.enabled", havingValue = "true", matchIfMissing = true)
    public ValidacionPort validacionRest(@Value("${saberpro.validation.url}") String url) {
        return new ValidationRestClient(new RestTemplate(), url);
    }

    @Bean
    @ConditionalOnProperty(name = "saberpro.validation.enabled", havingValue = "false")
    public ValidacionPort validacionDeshabilitada() {
        return (enunciado, justificacion, opciones) -> { };
    }

    /** El servicio de aplicación se construye con los puertos de salida que hayan quedado activos. */
    @Bean
    public QuestionApplicationService questionApplicationService(PreguntaRepositoryPort repositorio,
                                                                 NotificacionPort notificacion,
                                                                 ValidacionPort validacion) {
        return new QuestionApplicationService(repositorio, notificacion, validacion);
    }
}
