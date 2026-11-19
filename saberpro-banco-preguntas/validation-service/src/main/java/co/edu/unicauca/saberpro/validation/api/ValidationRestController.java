package co.edu.unicauca.saberpro.validation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Validación síncrona (REST). Siempre responde 200 con {"valido": true|false, "errores": [...]};
 * el Question Service traduce un "valido=false" en HTTP 400 para el cliente.
 */
@RestController
public class ValidationRestController {

    static final int ENUNCIADO_MINIMO = 10;
    static final int JUSTIFICACION_MINIMA = 15;
    static final List<String> PROHIBIDAS = List.of("obviamente", "evidentemente", "porque si", "es trivial");

    public record ValidationRequest(String enunciado, String justificacion, List<String> opciones) {
    }

    public record ValidationResponse(boolean valido, List<String> errores) {
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "validation-service", "status", "UP");
    }

    @PostMapping("/validate")
    public ValidationResponse validar(@RequestBody ValidationRequest r) {
        List<String> errores = new ArrayList<>();

        if (r.enunciado() == null || r.enunciado().trim().length() < ENUNCIADO_MINIMO) {
            errores.add("El enunciado debe tener al menos " + ENUNCIADO_MINIMO + " caracteres.");
        }
        if (r.justificacion() == null || r.justificacion().trim().length() < JUSTIFICACION_MINIMA) {
            errores.add("La justificación debe contener al menos " + JUSTIFICACION_MINIMA + " caracteres.");
        } else {
            String norm = normalizar(r.justificacion());
            for (String p : PROHIBIDAS) {
                if (norm.contains(p)) {
                    errores.add("La justificación contiene la expresión prohibida: \"" + p + "\".");
                }
            }
        }
        if (r.opciones() != null) {
            Set<String> vistas = new HashSet<>();
            for (String o : r.opciones()) {
                if (o == null || o.isBlank()) {
                    errores.add("Hay una opción de respuesta vacía.");
                } else if (!vistas.add(o.trim().toLowerCase(Locale.ROOT))) {
                    errores.add("Opción de respuesta repetida: \"" + o.trim() + "\".");
                }
            }
        }
        return new ValidationResponse(errores.isEmpty(), errores);
    }

    static String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
