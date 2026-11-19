package co.edu.unicauca.saberpro.question.infrastructure.messaging;

/** Topología AMQP compartida con el Lifecycle Service. */
public final class MessagingConstants {
    public static final String EXCHANGE = "pregunta.exchange";
    public static final String QUEUE = "pregunta.revisar.queue";
    public static final String ROUTING_KEY = "pregunta.revisar";

    private MessagingConstants() {
    }
}
