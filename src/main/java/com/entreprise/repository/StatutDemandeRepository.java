package com.entreprise.repository;

import com.entreprise.model.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatutDemandeRepository extends JpaRepository<StatutDemande, Long> {
    
    /**
     * Trouve un statut par son libellé
     */
    Optional<StatutDemande> findByLibelle(String libelle);
    
    /**
     * Trouve tous les statuts actifs triés par ordre d'affichage
     */
    @Query("SELECT sd FROM StatutDemande sd WHERE sd.actif = true ORDER BY sd.ordreAffichage ASC")
    List<StatutDemande> findAllActiveOrderByOrdreAffichage();
    
    /**
     * Trouve tous les statuts triés par ordre d'affichage
     */
    @Query("SELECT sd FROM StatutDemande sd ORDER BY sd.ordreAffichage ASC")
    List<StatutDemande> findAllOrderByOrdreAffichage();
    
    /**
     * Trouve le statut "En attente"
     */
    @Query("SELECT sd FROM StatutDemande sd WHERE sd.libelle = 'EN_ATTENTE'")
    Optional<StatutDemande> findStatutEnAttente();
    
    /**
     * Trouve le statut "Accepté"
     */
    @Query("SELECT sd FROM StatutDemande sd WHERE sd.libelle = 'ACCEPTE'")
    Optional<StatutDemande> findStatutAccepte();
    
    /**
     * Trouve le statut "Refusé"
     */
    @Query("SELECT sd FROM StatutDemande sd WHERE sd.libelle = 'REFUSE'")
    Optional<StatutDemande> findStatutRefuse();
    
    /**
     * Vérifie si un libellé existe déjà
     */
    boolean existsByLibelle(String libelle);
    
    /**
     * Compte le nombre de statuts actifs
     */
    @Query("SELECT COUNT(sd) FROM StatutDemande sd WHERE sd.actif = true")
    long countActiveStatuts();
}