package com.entreprise.repository;

import com.entreprise.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByPosteIdPoste(Long idPoste);
    List<Offre> findByFormationIdFormation(Long idFormation);
    List<Offre> findByLocalIdLocal(Long idLocal);
}
