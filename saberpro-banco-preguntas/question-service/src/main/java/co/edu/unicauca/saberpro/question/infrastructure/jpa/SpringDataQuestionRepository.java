package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/** Detalle técnico de Spring Data: solo lo usa el adaptador JPA. */
public interface SpringDataQuestionRepository extends JpaRepository<QuestionRecord, String> {
}
