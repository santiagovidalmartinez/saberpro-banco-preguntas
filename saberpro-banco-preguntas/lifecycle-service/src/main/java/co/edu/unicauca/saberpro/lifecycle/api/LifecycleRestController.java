package co.edu.unicauca.saberpro.lifecycle.api;

import co.edu.unicauca.saberpro.lifecycle.messaging.EventosRecibidos;
import co.edu.unicauca.saberpro.lifecycle.messaging.PreguntaRevisionEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Consulta de los eventos procesados (evidencia para la sustentación). */
@RestController
public class LifecycleRestController {

    private final EventosRecibidos recibidos;

    public LifecycleRestController(EventosRecibidos recibidos) {
        this.recibidos = recibidos;
    }

    @GetMapping("/lifecycle/eventos")
    public List<PreguntaRevisionEvent> eventos() {
        return recibidos.todos();
    }
}
