package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AGGREGATE ROOT del Banco de Preguntas (Java puro, sin frameworks).
 *
 * Invariantes:
 *  - Solo en BORRADOR se pueden modificar enunciado, justificación y opciones.
 *  - Nunca más de 4 opciones ni opciones con texto repetido.
 *  - Para pasar a PENDIENTE_REVISION: enunciado, justificación, exactamente 4 opciones y exactamente 1 clave.
 */
public class Question {
    public static final int NUMERO_OPCIONES = 4;

    private final QuestionId id;
    private Enunciado enunciado;
    private Justificacion justificacion;
    private final List<AnswerOption> opciones = new ArrayList<>();
    private EstadoPregunta estado = EstadoPregunta.BORRADOR;

    public Question() {
        this(QuestionId.nuevo());
    }

    public Question(QuestionId id) {
        if (id == null) {
            throw new ReglaNegocioException("La pregunta debe tener identificador.");
        }
        this.id = id;
    }

    /**
     * Reconstruye una pregunta ya existente a partir de su estado persistido.
     * Lo usan los adaptadores de salida (JPA, memoria) para devolver agregados al núcleo.
     */
    public static Question reconstruir(QuestionId id, Enunciado enunciado, Justificacion justificacion,
                                       List<AnswerOption> opciones, EstadoPregunta estado) {
        Question p = new Question(id);
        if (opciones != null) {
            if (opciones.size() > NUMERO_OPCIONES) {
                throw new ReglaNegocioException("Una pregunta no puede tener más de 4 opciones.");
            }
            p.opciones.addAll(opciones);
        }
        p.enunciado = enunciado;
        p.justificacion = justificacion;
        p.estado = estado == null ? EstadoPregunta.BORRADOR : estado;
        return p;
    }

    // ---------------------------------------------------------------- comandos
    public void definirEnunciado(String texto) {
        exigirBorrador("el enunciado");
        this.enunciado = new Enunciado(texto);
    }

    public void definirJustificacion(String texto) {
        exigirBorrador("la justificación");
        this.justificacion = new Justificacion(texto);
    }

    public void agregarOpcion(String texto, boolean esCorrecta) {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo se pueden modificar opciones en estado Borrador.");
        }
        if (this.opciones.size() >= NUMERO_OPCIONES) {
            throw new ReglaNegocioException("Una pregunta no puede tener más de 4 opciones.");
        }
        AnswerOption nueva = new AnswerOption(texto, esCorrecta);
        boolean repetida = opciones.stream()
                .anyMatch(o -> o.getTexto().equalsIgnoreCase(nueva.getTexto()));
        if (repetida) {
            throw new ReglaNegocioException("No se permiten opciones con el mismo texto.");
        }
        this.opciones.add(nueva);
    }

    public void enviarARevision() {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo una pregunta en Borrador puede enviarse a revisión.");
        }
        validarInvariantesParaEnvio();
        this.estado = EstadoPregunta.PENDIENTE_REVISION;
    }

    public void aprobar() {
        exigirPendiente("aprobada");
        this.estado = EstadoPregunta.APROBADA;
    }

    public void rechazar() {
        exigirPendiente("rechazada");
        this.estado = EstadoPregunta.RECHAZADA;
    }

    // ------------------------------------------------------------ invariantes
    private void validarInvariantesParaEnvio() {
        if (this.enunciado == null) {
            throw new ReglaNegocioException("La pregunta debe tener un enunciado.");
        }
        if (this.justificacion == null) {
            throw new ReglaNegocioException("La pregunta debe tener una justificación.");
        }
        if (this.opciones.size() != NUMERO_OPCIONES) {
            throw new ReglaNegocioException("Debe tener exactamente 4 opciones de respuesta.");
        }
        long correctas = opciones.stream().filter(AnswerOption::isEsCorrecta).count();
        if (correctas != 1) {
            throw new ReglaNegocioException("Debe existir exactamente 1 opción correcta.");
        }
    }

    private void exigirBorrador(String que) {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo se puede modificar " + que + " en estado Borrador.");
        }
    }

    private void exigirPendiente(String resultado) {
        if (this.estado != EstadoPregunta.PENDIENTE_REVISION) {
            throw new ReglaNegocioException(
                    "Solo una pregunta pendiente de revisión puede ser " + resultado + ".");
        }
    }

    // ---------------------------------------------------------------- consultas
    public QuestionId getId() {
        return id;
    }

    public Enunciado getEnunciado() {
        return enunciado;
    }

    public Justificacion getJustificacion() {
        return justificacion;
    }

    /** Vista de solo lectura: nadie puede saltarse los invariantes modificando la lista. */
    public List<AnswerOption> getOpciones() {
        return Collections.unmodifiableList(opciones);
    }

    public EstadoPregunta getEstado() {
        return estado;
    }
}
