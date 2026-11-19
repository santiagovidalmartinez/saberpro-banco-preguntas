package co.edu.unicauca.saberpro.lifecycle.messaging;

/** Topología AMQP compartida con el Question Service. */
public final class MessagingConstants {
    public static final String EXCHANGE = "pregunta.exchange";
    public static final String QUEUE = "pregunta.revisar.queue";
    public static final String ROUTING_KEY = "pregunta.revisar";

    private MessagingConstants() {
    }
}
