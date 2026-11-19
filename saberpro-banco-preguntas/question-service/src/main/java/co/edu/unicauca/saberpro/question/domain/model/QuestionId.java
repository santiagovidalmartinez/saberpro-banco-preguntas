package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.UUID;

/** VALUE OBJECT: identificador fuertemente tipado de una pregunta (se genera en el dominio). */
public final class QuestionId {
    private final UUID valor;

    private QuestionId(UUID valor) {
        this.valor = valor;
    }

    public static QuestionId nuevo() {
        return new QuestionId(UUID.randomUUID());
    }

    public static QuestionId de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException("El identificador de la pregunta es obligatorio.");
        }
        try {
            return new QuestionId(UUID.fromString(texto.trim()));
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException("El identificador de la pregunta no es válido: " + texto);
        }
    }

    public String valor() {
        return valor.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof QuestionId otro && valor.equals(otro.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
