package com.entreprise.repository;

import com.entreprise.model.StatusCandidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StatusCandidatRepository extends JpaRepository<StatusCandidat, Long> {
    List<StatusCandidat> findByCandidatIdCandidat(Long idCandidat);
    List<StatusCandidat> findByStatusIdStatus(Long idStatus);
}
