package com.entreprise.repository;

import com.entreprise.model.FormationCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormationCandidatRepository extends JpaRepository<FormationCandidat, Long> {
    List<FormationCandidat> findByCandidatIdCandidat(Long idCandidat);
    List<FormationCandidat> findByFormationIdFormation(Long idFormation);
}
