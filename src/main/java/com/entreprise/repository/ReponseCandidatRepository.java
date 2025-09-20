package com.entreprise.repository;

import com.entreprise.model.ReponseCandidat;
import com.entreprise.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReponseCandidatRepository extends JpaRepository<ReponseCandidat, Long> {
    List<ReponseCandidat> findByQuestionIdQuestion(Long idQuestion);
    List<ReponseCandidat> findByReponseQuestionIdReponseQuestion(Long idReponseQuestion);
    
    List<ReponseCandidat> findByQuestion(Question question);
    
    @Query("SELECT rc FROM ReponseCandidat rc WHERE rc.question.idQuestion = :questionId")
    List<ReponseCandidat> findByQuestionId(@Param("questionId") Long questionId);
    
    @Query("SELECT rc FROM ReponseCandidat rc WHERE rc.candidat.idCandidat = :candidatId")
    List<ReponseCandidat> findByCandidatId(@Param("candidatId") Long candidatId);
    
    @Query("SELECT rc FROM ReponseCandidat rc WHERE rc.question.idQuestion = :questionId AND rc.candidat.idCandidat = :candidatId")
    ReponseCandidat findByQuestionIdAndCandidatId(@Param("questionId") Long questionId, @Param("candidatId") Long candidatId);
}
