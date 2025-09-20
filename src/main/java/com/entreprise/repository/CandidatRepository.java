package com.entreprise.repository;

import com.entreprise.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    List<Candidat> findByOffreIdOffre(Long idOffre);
    List<Candidat> findByGenreIdSexe(Long idSexe);
}
