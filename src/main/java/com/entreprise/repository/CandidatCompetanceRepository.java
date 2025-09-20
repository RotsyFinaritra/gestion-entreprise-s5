package com.entreprise.repository;

import com.entreprise.model.CandidatCompetance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidatCompetanceRepository extends JpaRepository<CandidatCompetance, Long> {
    List<CandidatCompetance> findByCandidatIdCandidat(Long idCandidat);
    List<CandidatCompetance> findByCompetanceIdCompetance(Long idCompetance);
}
