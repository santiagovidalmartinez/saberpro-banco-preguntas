package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * VALUE OBJECT inmutable: justificación de la clave correcta.
 * Reglas: mínimo 15 caracteres (sin contar espacios de los extremos) y sin palabras prohibidas.
 */
public final class Justificacion {
    public static final int LONGITUD_MINIMA = 15;

    /** Expresiones que no aportan argumento pedagógico (se comparan sin tildes ni mayúsculas). */
    public static final List<String> PALABRAS_PROHIBIDAS =
            List.of("obviamente", "evidentemente", "porque si", "es trivial");

    private final String texto;

    public Justificacion(String texto) {
        if (texto == null || texto.trim().length() < LONGITUD_MINIMA) {
            throw new ReglaNegocioException("La justificación debe contener al menos 15 caracteres.");
        }
        String normalizado = normalizar(texto);
        for (String prohibida : PALABRAS_PROHIBIDAS) {
            if (normalizado.contains(prohibida)) {
                throw new ReglaNegocioException(
                        "La justificación no puede contener la expresión prohibida: \"" + prohibida + "\".");
            }
        }
        this.texto = texto.trim();
    }

    private static String normalizar(String s) {
        String sinTildes = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT);
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Justificacion otra && texto.equals(otra.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto);
    }

    @Override
    public String toString() {
        return texto;
    }
}
