package co.edu.unicauca.saberpro.lifecycle.messaging;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Memoria simple de los eventos consumidos (sirve de evidencia en la demo). */
@Component
public class EventosRecibidos {

    private final List<PreguntaRevisionEvent> eventos = new CopyOnWriteArrayList<>();

    public void agregar(PreguntaRevisionEvent e) {
        eventos.add(e);
    }

    public List<PreguntaRevisionEvent> todos() {
        return List.copyOf(eventos);
    }
}
