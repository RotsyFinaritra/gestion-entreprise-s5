package com.entreprise.repository;

import com.entreprise.model.StatutOffre;
import com.entreprise.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatutOffreRepository extends JpaRepository<StatutOffre, Long> {
    
    /**
     * Trouve tous les statuts d'une offre triés par date de changement (plus récent en premier)
     */
    @Query("SELECT so FROM StatutOffre so WHERE so.offre = :offre ORDER BY so.dateChangement DESC")
    List<StatutOffre> findByOffreOrderByDateChangementDesc(@Param("offre") Offre offre);
    
    /**
     * Trouve le statut le plus récent d'une offre
     */
    @Query("SELECT so FROM StatutOffre so WHERE so.offre = :offre ORDER BY so.dateChangement DESC LIMIT 1")
    Optional<StatutOffre> findLatestByOffre(@Param("offre") Offre offre);
    
    /**
     * Trouve tous les changements de statut pour une offre donnée
     */
    List<StatutOffre> findByOffre(Offre offre);
    
    /**
     * Trouve tous les changements de statut effectués par un utilisateur
     */
    @Query("SELECT so FROM StatutOffre so WHERE so.userModification.idUser = :userId ORDER BY so.dateChangement DESC")
    List<StatutOffre> findByUserModificationIdUser(@Param("userId") Long userId);
    
    /**
     * Trouve tous les changements de statut dans une période donnée
     */
    @Query("SELECT so FROM StatutOffre so WHERE so.dateChangement BETWEEN :dateDebut AND :dateFin ORDER BY so.dateChangement DESC")
    List<StatutOffre> findByDateChangementBetween(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    /**
     * Trouve tous les changements vers un statut donné
     */
    @Query("SELECT so FROM StatutOffre so WHERE so.statutDemande.libelle = :libelle ORDER BY so.dateChangement DESC")
    List<StatutOffre> findByStatutDemandeLibelle(@Param("libelle") String libelle);
    
    /**
     * Compte le nombre de changements de statut pour une offre
     */
    @Query("SELECT COUNT(so) FROM StatutOffre so WHERE so.offre = :offre")
    long countByOffre(@Param("offre") Offre offre);
    
    /**
     * Trouve l'historique complet d'une offre avec les détails
     */
    @Query("SELECT so FROM StatutOffre so " +
           "JOIN FETCH so.statutDemande " +
           "LEFT JOIN FETCH so.userModification " +
           "WHERE so.offre = :offre " +
           "ORDER BY so.dateChangement ASC")
    List<StatutOffre> findHistoriqueCompletByOffre(@Param("offre") Offre offre);
}