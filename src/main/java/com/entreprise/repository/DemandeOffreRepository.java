package com.entreprise.repository;

import com.entreprise.model.DemandeOffre;
import com.entreprise.model.User;
import com.entreprise.model.Poste;
import com.entreprise.model.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeOffreRepository extends JpaRepository<DemandeOffre, Long> {
    
    /**
     * Trouve toutes les demandes d'un département triées par date de création (plus récente en premier)
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.departement = :departement ORDER BY do.dateCreation DESC")
    List<DemandeOffre> findByDepartementOrderByDateCreationDesc(@Param("departement") User departement);
    
    /**
     * Trouve toutes les demandes par statut
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.statutDemande = :statut ORDER BY do.dateCreation DESC")
    List<DemandeOffre> findByStatutDemandeOrderByDateCreationDesc(@Param("statut") StatutDemande statut);
    
    /**
     * Trouve toutes les demandes en attente
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.statutDemande.libelle = 'EN_ATTENTE' ORDER BY do.dateCreation ASC")
    List<DemandeOffre> findDemandesEnAttente();
    
    /**
     * Trouve toutes les demandes acceptées
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.statutDemande.libelle = 'ACCEPTE' ORDER BY do.dateTraitement DESC")
    List<DemandeOffre> findDemandesAcceptees();
    
    /**
     * Trouve toutes les demandes refusées
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.statutDemande.libelle = 'REFUSE' ORDER BY do.dateTraitement DESC")
    List<DemandeOffre> findDemandesRefusees();
    
    /**
     * Trouve les demandes pour un poste donné
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.poste = :poste ORDER BY do.dateCreation DESC")
    List<DemandeOffre> findByPosteOrderByDateCreationDesc(@Param("poste") Poste poste);
    
    /**
     * Trouve les demandes traitées par un utilisateur RH
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.userTraitement = :userRh ORDER BY do.dateTraitement DESC")
    List<DemandeOffre> findByUserTraitementOrderByDateTraitementDesc(@Param("userRh") User userRh);
    
    /**
     * Trouve les demandes créées dans une période donnée
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.dateCreation BETWEEN :dateDebut AND :dateFin ORDER BY do.dateCreation DESC")
    List<DemandeOffre> findByDateCreationBetween(@Param("dateDebut") LocalDateTime dateDebut, @Param("dateFin") LocalDateTime dateFin);
    
    /**
     * Compte les demandes par statut pour un département
     */
    @Query("SELECT COUNT(do) FROM DemandeOffre do WHERE do.departement = :departement AND do.statutDemande.libelle = :statut")
    long countByDepartementAndStatut(@Param("departement") User departement, @Param("statut") String statut);
    
    /**
     * Compte toutes les demandes en attente
     */
    @Query("SELECT COUNT(do) FROM DemandeOffre do WHERE do.statutDemande.libelle = 'EN_ATTENTE'")
    long countDemandesEnAttente();
    
    /**
     * Trouve les demandes avec les détails complets (avec jointures)
     */
    @Query("SELECT do FROM DemandeOffre do " +
           "JOIN FETCH do.poste " +
           "JOIN FETCH do.departement " +
           "JOIN FETCH do.statutDemande " +
           "LEFT JOIN FETCH do.userTraitement " +
           "ORDER BY do.dateCreation DESC")
    List<DemandeOffre> findAllWithDetails();
    
    /**
     * Trouve les demandes d'un département avec détails
     */
    @Query("SELECT do FROM DemandeOffre do " +
           "JOIN FETCH do.poste " +
           "JOIN FETCH do.statutDemande " +
           "LEFT JOIN FETCH do.userTraitement " +
           "WHERE do.departement = :departement " +
           "ORDER BY do.dateCreation DESC")
    List<DemandeOffre> findByDepartementWithDetails(@Param("departement") User departement);
    
    /**
     * Trouve les demandes par priorité
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.priorite = :priorite ORDER BY do.dateCreation ASC")
    List<DemandeOffre> findByPriorite(@Param("priorite") String priorite);
    
    /**
     * Trouve les demandes urgentes non traitées
     */
    @Query("SELECT do FROM DemandeOffre do WHERE do.priorite = 'URGENTE' AND do.statutDemande.libelle = 'EN_ATTENTE' ORDER BY do.dateCreation ASC")
    List<DemandeOffre> findDemandesUrgentesEnAttente();
    
    /**
     * Vérifie s'il existe déjà une demande en attente pour un poste donné
     */
    @Query("SELECT COUNT(do) > 0 FROM DemandeOffre do WHERE do.poste = :poste AND do.statutDemande.libelle = 'EN_ATTENTE'")
    boolean existsDemandeEnAttenteForPoste(@Param("poste") Poste poste);
}