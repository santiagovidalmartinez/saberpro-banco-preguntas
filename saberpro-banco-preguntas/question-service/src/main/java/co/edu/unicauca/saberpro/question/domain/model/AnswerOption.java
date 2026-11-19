package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.Objects;

/** VALUE OBJECT inmutable: texto de una opción y su indicador de clave. */
public final class AnswerOption {
    private final String texto;
    private final boolean esCorrecta;

    public AnswerOption(String texto, boolean esCorrecta) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El texto de la opción de respuesta es obligatorio.");
        }
        this.texto = texto.trim();
        this.esCorrecta = esCorrecta;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isEsCorrecta() {
        return esCorrecta;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AnswerOption otra
                && esCorrecta == otra.esCorrecta && texto.equals(otra.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto, esCorrecta);
    }

    @Override
    public String toString() {
        return (esCorrecta ? "[clave] " : "") + texto;
    }
}
