package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.Objects;

/** VALUE OBJECT inmutable: texto del enunciado de la pregunta. */
public final class Enunciado {
    private final String texto;

    public Enunciado(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El enunciado es obligatorio y no puede estar vacío.");
        }
        this.texto = texto.trim();
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Enunciado otro && texto.equals(otro.texto);
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
