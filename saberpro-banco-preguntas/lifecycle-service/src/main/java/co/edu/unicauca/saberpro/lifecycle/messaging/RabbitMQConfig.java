package co.edu.unicauca.saberpro.lifecycle.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara la misma topología que el Question Service (es idempotente) y usa JSON. */
@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange preguntaExchange() {
        return new DirectExchange(MessagingConstants.EXCHANGE);
    }

    @Bean
    public Queue preguntaRevisarQueue() {
        return new Queue(MessagingConstants.QUEUE, true);
    }

    @Bean
    public Binding preguntaBinding(Queue preguntaRevisarQueue, DirectExchange preguntaExchange) {
        return BindingBuilder.bind(preguntaRevisarQueue).to(preguntaExchange).with(MessagingConstants.ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
