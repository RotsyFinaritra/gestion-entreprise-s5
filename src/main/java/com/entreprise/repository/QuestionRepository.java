package com.entreprise.repository;

import com.entreprise.model.Question;
import com.entreprise.model.Competance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCompetanceIdCompetance(Long idCompetance);
    
    List<Question> findByCompetance(Competance competance);
    
    @Query("SELECT q FROM Question q WHERE q.competance IN :competances")
    List<Question> findByCompetanceIn(@Param("competances") List<Competance> competances);
    
    @Query("SELECT q FROM Question q WHERE q.competance.idCompetance IN :competanceIds")
    List<Question> findByCompetanceIds(@Param("competanceIds") List<Long> competanceIds);
}
