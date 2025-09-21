package com.entreprise.repository;

import com.entreprise.model.NoteEntretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteEntretienRepository extends JpaRepository<NoteEntretien, Long> {

    // Trouver les notes par entretien
    List<NoteEntretien> findByEntretienIdEntretienOrderBySectionOrdreAffichage(Long entretienId);

    // Trouver les notes par candidat
    @Query("SELECT n FROM NoteEntretien n " +
           "JOIN n.entretien e " +
           "WHERE e.candidat.idCandidat = :candidatId " +
           "ORDER BY n.section.ordreAffichage")
    List<NoteEntretien> findByCandidatId(@Param("candidatId") Long candidatId);

    // Trouver les notes par offre
    @Query("SELECT n FROM NoteEntretien n " +
           "JOIN n.entretien e " +
           "WHERE e.offre.idOffre = :offreId " +
           "ORDER BY e.candidat.nom, e.candidat.prenom, n.section.ordreAffichage")
    List<NoteEntretien> findByOffreId(@Param("offreId") Long offreId);

    // Vérifier si une note existe pour un entretien et une section
    Optional<NoteEntretien> findByEntretienIdEntretienAndSectionIdSection(Long entretienId, Long sectionId);

    // Calculer la moyenne des notes d'un entretien
    @Query("SELECT AVG(n.noteObtenue) FROM NoteEntretien n WHERE n.entretien.idEntretien = :entretienId")
    Double calculateMoyenneByEntretien(@Param("entretienId") Long entretienId);

    // Calculer le total des notes d'un entretien
    @Query("SELECT SUM(n.noteObtenue) FROM NoteEntretien n WHERE n.entretien.idEntretien = :entretienId")
    Double calculateTotalByEntretien(@Param("entretienId") Long entretienId);
}
