package com.entreprise.service;

import com.entreprise.model.DemandeOffre;
import com.entreprise.model.Poste;
import com.entreprise.model.StatutDemande;
import com.entreprise.model.User;
import com.entreprise.repository.DemandeOffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DemandeOffreService {
    
    @Autowired
    private DemandeOffreRepository demandeOffreRepository;
    
    @Autowired
    private StatutDemandeService statutDemandeService;
    
    /**
     * Trouve toutes les demandes
     */
    public List<DemandeOffre> findAll() {
        return demandeOffreRepository.findAllWithDetails();
    }
    
    /**
     * Trouve une demande par son ID
     */
    public Optional<DemandeOffre> findById(Long id) {
        return demandeOffreRepository.findById(id);
    }
    
    /**
     * Trouve toutes les demandes d'un département
     */
    public List<DemandeOffre> findByDepartement(User departement) {
        return demandeOffreRepository.findByDepartementWithDetails(departement);
    }
    
    /**
     * Trouve les demandes par statut
     */
    public List<DemandeOffre> findByStatut(StatutDemande statut) {
        return demandeOffreRepository.findByStatutDemandeOrderByDateCreationDesc(statut);
    }
    
    /**
     * Trouve toutes les demandes en attente
     */
    public List<DemandeOffre> findDemandesEnAttente() {
        return demandeOffreRepository.findDemandesEnAttente();
    }
    
    /**
     * Trouve toutes les demandes acceptées
     */
    public List<DemandeOffre> findDemandesAcceptees() {
        return demandeOffreRepository.findDemandesAcceptees();
    }
    
    /**
     * Trouve toutes les demandes refusées
     */
    public List<DemandeOffre> findDemandesRefusees() {
        return demandeOffreRepository.findDemandesRefusees();
    }
    
    /**
     * Sauvegarde une demande d'offre
     */
    public DemandeOffre save(DemandeOffre demandeOffre) {
        if (demandeOffre.getDateCreation() == null) {
            demandeOffre.setDateCreation(LocalDateTime.now());
        }
        demandeOffre.setDateModification(LocalDateTime.now());
        
        // Si c'est une nouvelle demande, définir le statut en attente
        if (demandeOffre.getIdDemandeOffre() == null && demandeOffre.getStatutDemande() == null) {
            StatutDemande statutEnAttente = statutDemandeService.getStatutEnAttente();
            demandeOffre.setStatutDemande(statutEnAttente);
        }
        
        return demandeOffreRepository.save(demandeOffre);
    }
    
    /**
     * Crée une nouvelle demande d'offre
     */
    public DemandeOffre creerDemande(Poste poste, User departement, String titreOffre, String descriptionPoste, String justification) {
        // Vérifier s'il n'y a pas déjà une demande en attente pour ce poste
        if (demandeOffreRepository.existsDemandeEnAttenteForPoste(poste)) {
            throw new RuntimeException("Une demande est déjà en attente pour ce poste");
        }
        
        DemandeOffre demande = new DemandeOffre(poste, departement, titreOffre, descriptionPoste);
        demande.setJustification(justification);
        demande.setPriorite("NORMALE");
        
        return save(demande);
    }
    
    /**
     * Met à jour une demande existante
     */
    public DemandeOffre mettreAJour(DemandeOffre demandeOffre) {
        demandeOffre.setDateModification(LocalDateTime.now());
        return demandeOffreRepository.save(demandeOffre);
    }
    
    /**
     * Accepte une demande d'offre
     */
    public DemandeOffre accepterDemande(Long idDemande, User userRh, String commentaireRh) {
        Optional<DemandeOffre> demandeOpt = demandeOffreRepository.findById(idDemande);
        if (demandeOpt.isPresent()) {
            DemandeOffre demande = demandeOpt.get();
            
            StatutDemande statutAccepte = statutDemandeService.getStatutAccepte();
            demande.setStatutDemande(statutAccepte);
            demande.setUserTraitement(userRh);
            demande.setCommentaireRh(commentaireRh);
            demande.setDateTraitement(LocalDateTime.now());
            demande.setDateModification(LocalDateTime.now());
            
            return demandeOffreRepository.save(demande);
        }
        throw new RuntimeException("Demande non trouvée");
    }
    
    /**
     * Refuse une demande d'offre
     */
    public DemandeOffre refuserDemande(Long idDemande, User userRh, String commentaireRh) {
        Optional<DemandeOffre> demandeOpt = demandeOffreRepository.findById(idDemande);
        if (demandeOpt.isPresent()) {
            DemandeOffre demande = demandeOpt.get();
            
            StatutDemande statutRefuse = statutDemandeService.getStatutRefuse();
            demande.setStatutDemande(statutRefuse);
            demande.setUserTraitement(userRh);
            demande.setCommentaireRh(commentaireRh);
            demande.setDateTraitement(LocalDateTime.now());
            demande.setDateModification(LocalDateTime.now());
            
            return demandeOffreRepository.save(demande);
        }
        throw new RuntimeException("Demande non trouvée");
    }
    
    /**
     * Supprime une demande
     */
    public void deleteById(Long id) {
        demandeOffreRepository.deleteById(id);
    }
    
    /**
     * Compte les demandes par statut pour un département
     */
    public long countByDepartementAndStatut(User departement, String statut) {
        return demandeOffreRepository.countByDepartementAndStatut(departement, statut);
    }
    
    /**
     * Compte toutes les demandes en attente
     */
    public long countDemandesEnAttente() {
        return demandeOffreRepository.countDemandesEnAttente();
    }
    
    /**
     * Trouve les demandes urgentes en attente
     */
    public List<DemandeOffre> findDemandesUrgentesEnAttente() {
        return demandeOffreRepository.findDemandesUrgentesEnAttente();
    }
    
    /**
     * Trouve les demandes pour un poste donné
     */
    public List<DemandeOffre> findByPoste(Poste poste) {
        return demandeOffreRepository.findByPosteOrderByDateCreationDesc(poste);
    }
    
    /**
     * Vérifie si une demande peut être modifiée (seulement si en attente)
     */
    public boolean canModify(Long idDemande) {
        Optional<DemandeOffre> demandeOpt = demandeOffreRepository.findById(idDemande);
        if (demandeOpt.isPresent()) {
            DemandeOffre demande = demandeOpt.get();
            return demande.isEnAttente();
        }
        return false;
    }
    
    /**
     * Vérifie si une demande peut être supprimée (seulement si en attente)
     */
    public boolean canDelete(Long idDemande) {
        return canModify(idDemande);
    }
    
    /**
     * Change la priorité d'une demande
     */
    public DemandeOffre changerPriorite(Long idDemande, String nouvellePriorite) {
        Optional<DemandeOffre> demandeOpt = demandeOffreRepository.findById(idDemande);
        if (demandeOpt.isPresent()) {
            DemandeOffre demande = demandeOpt.get();
            if (demande.isEnAttente()) {
                demande.setPriorite(nouvellePriorite);
                demande.setDateModification(LocalDateTime.now());
                return demandeOffreRepository.save(demande);
            } else {
                throw new RuntimeException("Impossible de modifier une demande déjà traitée");
            }
        }
        throw new RuntimeException("Demande non trouvée");
    }
}