package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.Enunciado;
import co.edu.unicauca.saberpro.question.domain.model.Justificacion;
import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;

import java.util.List;

/** Traduce entre el agregado de dominio y los registros JPA. */
final class QuestionDomainMapper {

    private QuestionDomainMapper() {
    }

    static QuestionRecord aEntidad(Question p) {
        QuestionRecord e = new QuestionRecord(
                p.getId().valor(),
                p.getEnunciado() == null ? null : p.getEnunciado().getTexto(),
                p.getJustificacion() == null ? null : p.getJustificacion().getTexto(),
                p.getEstado());
        for (AnswerOption o : p.getOpciones()) {
            e.getOpciones().add(new OptionRecord(o.getTexto(), o.isEsCorrecta(), e));
        }
        return e;
    }

    static Question aDominio(QuestionRecord e) {
        List<AnswerOption> opciones = e.getOpciones().stream()
                .map(o -> new AnswerOption(o.getTexto(), o.isCorrecta()))
                .toList();
        return Question.reconstruir(
                QuestionId.de(e.getId()),
                e.getEnunciado() == null ? null : new Enunciado(e.getEnunciado()),
                e.getJustificacion() == null ? null : new Justificacion(e.getJustificacion()),
                opciones,
                e.getEstado());
    }
}
