package com.entreprise.repository;

import com.entreprise.model.ReponseQuestion;
import com.entreprise.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReponseQuestionRepository extends JpaRepository<ReponseQuestion, Long> {
    List<ReponseQuestion> findByQuestionIdQuestion(Long idQuestion);
    
    List<ReponseQuestion> findByQuestion(Question question);
}
