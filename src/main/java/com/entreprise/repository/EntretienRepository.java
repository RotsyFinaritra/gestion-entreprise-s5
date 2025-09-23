package com.entreprise.repository;

import com.entreprise.model.Entretien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntretienRepository extends JpaRepository<Entretien, Long> {

    // Trouver les entretiens par candidat
    List<Entretien> findByCandidatIdCandidat(Long candidatId);

    // Trouver les entretiens par offre
    List<Entretien> findByOffreIdOffre(Long offreId);

    // Trouver les entretiens par statut
    List<Entretien> findByStatut(String statut);

    // Trouver les entretiens par date
    List<Entretien> findByDateHeureEntretienBetween(LocalDateTime debut, LocalDateTime fin);

    // Trouver les entretiens d'un candidat pour une offre spécifique
    Optional<Entretien> findByCandidatIdCandidatAndOffreIdOffre(Long candidatId, Long offreId);

    // Vérifier les conflits d'horaires - approche simplifiée pour éviter les fonctions de base de données
    @Query("SELECT e FROM Entretien e WHERE " +
           "e.dateHeureEntretien BETWEEN :debut AND :fin AND " +
           "e.statut IN ('programmé', 'confirmé')")
    List<Entretien> findEntretiensEnConflit(@Param("debut") LocalDateTime debut, 
                                          @Param("fin") LocalDateTime fin);

    // Trouver les entretiens programmés pour une date spécifique
    List<Entretien> findByDateHeureEntretienBetweenAndStatutInOrderByDateHeureEntretien(
        LocalDateTime debut, LocalDateTime fin, List<String> statuts);

    // Compter les entretiens par offre et statut
    @Query("SELECT COUNT(e) FROM Entretien e WHERE e.offre.idOffre = :offreId AND e.statut = :statut")
    Long countByOffreAndStatut(@Param("offreId") Long offreId, @Param("statut") String statut);

    // Trouver les entretiens avec leurs candidats et offres
    @Query("SELECT e FROM Entretien e " +
           "LEFT JOIN FETCH e.candidat c " +
           "LEFT JOIN FETCH e.offre o " +
           "WHERE e.offre.idOffre = :offreId " +
           "ORDER BY e.dateHeureEntretien")
    List<Entretien> findByOffreWithCandidatAndOffre(@Param("offreId") Long offreId);
    
    // Trouver les entretiens par département
    @Query("SELECT e FROM Entretien e " +
           "LEFT JOIN FETCH e.candidat c " +
           "LEFT JOIN FETCH e.offre o " +
           "LEFT JOIN FETCH o.poste p " +
           "WHERE p.departement = :departement " +
           "ORDER BY e.dateHeureEntretien DESC")
    List<Entretien> findByDepartement(@Param("departement") com.entreprise.model.User departement);
}
