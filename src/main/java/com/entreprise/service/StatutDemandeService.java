package com.entreprise.service;

import com.entreprise.model.StatutDemande;
import com.entreprise.repository.StatutDemandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StatutDemandeService {
    
    @Autowired
    private StatutDemandeRepository statutDemandeRepository;
    
    /**
     * Trouve tous les statuts actifs
     */
    public List<StatutDemande> findAllActive() {
        return statutDemandeRepository.findAllActiveOrderByOrdreAffichage();
    }
    
    /**
     * Trouve tous les statuts
     */
    public List<StatutDemande> findAll() {
        return statutDemandeRepository.findAllOrderByOrdreAffichage();
    }
    
    /**
     * Trouve un statut par son ID
     */
    public Optional<StatutDemande> findById(Long id) {
        return statutDemandeRepository.findById(id);
    }
    
    /**
     * Trouve un statut par son libellé
     */
    public Optional<StatutDemande> findByLibelle(String libelle) {
        return statutDemandeRepository.findByLibelle(libelle);
    }
    
    /**
     * Sauvegarde un statut
     */
    public StatutDemande save(StatutDemande statutDemande) {
        if (statutDemande.getDateCreation() == null) {
            statutDemande.setDateCreation(LocalDateTime.now());
        }
        return statutDemandeRepository.save(statutDemande);
    }
    
    /**
     * Supprime un statut
     */
    public void deleteById(Long id) {
        statutDemandeRepository.deleteById(id);
    }
    
    /**
     * Désactive un statut (soft delete)
     */
    public void desactiver(Long id) {
        Optional<StatutDemande> statutOpt = statutDemandeRepository.findById(id);
        if (statutOpt.isPresent()) {
            StatutDemande statut = statutOpt.get();
            statut.setActif(false);
            statutDemandeRepository.save(statut);
        }
    }
    
    /**
     * Réactive un statut
     */
    public void reactiver(Long id) {
        Optional<StatutDemande> statutOpt = statutDemandeRepository.findById(id);
        if (statutOpt.isPresent()) {
            StatutDemande statut = statutOpt.get();
            statut.setActif(true);
            statutDemandeRepository.save(statut);
        }
    }
    
    /**
     * Récupère le statut "En attente"
     */
    public StatutDemande getStatutEnAttente() {
        return statutDemandeRepository.findStatutEnAttente()
                .orElseThrow(() -> new RuntimeException("Statut EN_ATTENTE non trouvé"));
    }
    
    /**
     * Récupère le statut "Accepté"
     */
    public StatutDemande getStatutAccepte() {
        return statutDemandeRepository.findStatutAccepte()
                .orElseThrow(() -> new RuntimeException("Statut ACCEPTE non trouvé"));
    }
    
    /**
     * Récupère le statut "Refusé"
     */
    public StatutDemande getStatutRefuse() {
        return statutDemandeRepository.findStatutRefuse()
                .orElseThrow(() -> new RuntimeException("Statut REFUSE non trouvé"));
    }
    
    /**
     * Vérifie si un libellé existe déjà
     */
    public boolean existsByLibelle(String libelle) {
        return statutDemandeRepository.existsByLibelle(libelle);
    }
    
    /**
     * Crée les statuts par défaut s'ils n'existent pas
     */
    public void createDefaultStatuts() {
        if (!existsByLibelle("EN_ATTENTE")) {
            StatutDemande enAttente = new StatutDemande("EN_ATTENTE", "Demande en attente de traitement", "warning", 1);
            save(enAttente);
        }
        
        if (!existsByLibelle("ACCEPTE")) {
            StatutDemande accepte = new StatutDemande("ACCEPTE", "Demande acceptée et traitée", "success", 2);
            save(accepte);
        }
        
        if (!existsByLibelle("REFUSE")) {
            StatutDemande refuse = new StatutDemande("REFUSE", "Demande refusée", "danger", 3);
            save(refuse);
        }
    }
    
    /**
     * Compte le nombre de statuts actifs
     */
    public long countActiveStatuts() {
        return statutDemandeRepository.countActiveStatuts();
    }
    
    /**
     * Valide qu'un statut peut être supprimé (pas de demandes associées)
     */
    public boolean canDelete(Long id) {
        Optional<StatutDemande> statutOpt = statutDemandeRepository.findById(id);
        if (statutOpt.isPresent()) {
            StatutDemande statut = statutOpt.get();
            // Vérifier s'il y a des demandes associées
            return statut.getDemandesOffre() == null || statut.getDemandesOffre().isEmpty();
        }
        return false;
    }
}