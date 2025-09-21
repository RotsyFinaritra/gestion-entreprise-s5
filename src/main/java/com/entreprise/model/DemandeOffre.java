package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_offre")
public class DemandeOffre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande_offre")
    private Long idDemandeOffre;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_poste", nullable = false)
    private Poste poste;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departement", nullable = false)
    private User departement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_demande", nullable = false)
    private StatutDemande statutDemande;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_traitement")
    private User userTraitement; // RH qui traite la demande
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_local")
    private Local local;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation")
    private Formation formation;
    
    @OneToOne(mappedBy = "demandeOffre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Offre offre; // L'offre créée si la demande est acceptée
    
    // Attributs
    @Column(name = "titre_offre", nullable = false, length = 255)
    private String titreOffre;
    
    @Column(name = "description_poste", columnDefinition = "TEXT")
    private String descriptionPoste;
    
    @Column(name = "description_entreprise", columnDefinition = "TEXT")
    private String descriptionEntreprise;
    
    @Column(name = "type_contrat", length = 50)
    private String typeContrat; // CDI, CDD, Stage, etc.
    
    @Column(name = "niveau_experience", length = 100)
    private String niveauExperience; // Junior, Senior, Expert, etc.
    
    @Column(name = "salaire_min")
    private Double salaireMin;
    
    @Column(name = "salaire_max")
    private Double salaireMax;
    
    @Column(name = "age_min")
    private Integer ageMin;
    
    @Column(name = "age_max")
    private Integer ageMax;
    
    @Column(name = "nbr_personne")
    private Integer nbrPersonne;
    
    @Column(name = "avantages", columnDefinition = "TEXT")
    private String avantages;
    
    @Column(name = "date_limite_candidature")
    private LocalDateTime dateLimiteCandidature;
    
    @Column(name = "priorite", length = 20)
    private String priorite = "NORMALE"; // BASSE, NORMALE, HAUTE, URGENTE
    
    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification; // Pourquoi cette demande
    
    @Column(name = "commentaire_departement", columnDefinition = "TEXT")
    private String commentaireDepartement;
    
    @Column(name = "commentaire_rh", columnDefinition = "TEXT")
    private String commentaireRh; // Commentaire du RH lors du traitement
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Constructeurs
    public DemandeOffre() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }
    
    public DemandeOffre(Poste poste, User departement, String titreOffre, String descriptionPoste) {
        this();
        this.poste = poste;
        this.departement = departement;
        this.titreOffre = titreOffre;
        this.descriptionPoste = descriptionPoste;
    }
    
    // Getters et Setters
    public Long getIdDemandeOffre() {
        return idDemandeOffre;
    }
    
    public void setIdDemandeOffre(Long idDemandeOffre) {
        this.idDemandeOffre = idDemandeOffre;
    }
    
    public Poste getPoste() {
        return poste;
    }
    
    public void setPoste(Poste poste) {
        this.poste = poste;
    }
    
    public User getDepartement() {
        return departement;
    }
    
    public void setDepartement(User departement) {
        this.departement = departement;
    }
    
    public StatutDemande getStatutDemande() {
        return statutDemande;
    }
    
    public void setStatutDemande(StatutDemande statutDemande) {
        this.statutDemande = statutDemande;
    }
    
    public User getUserTraitement() {
        return userTraitement;
    }
    
    public void setUserTraitement(User userTraitement) {
        this.userTraitement = userTraitement;
    }
    
    public Offre getOffre() {
        return offre;
    }
    
    public void setOffre(Offre offre) {
        this.offre = offre;
    }
    
    public String getTitreOffre() {
        return titreOffre;
    }
    
    public void setTitreOffre(String titreOffre) {
        this.titreOffre = titreOffre;
    }
    
    public String getDescriptionPoste() {
        return descriptionPoste;
    }
    
    public void setDescriptionPoste(String descriptionPoste) {
        this.descriptionPoste = descriptionPoste;
    }
    
    public String getDescriptionEntreprise() {
        return descriptionEntreprise;
    }
    
    public void setDescriptionEntreprise(String descriptionEntreprise) {
        this.descriptionEntreprise = descriptionEntreprise;
    }
    
    public String getTypeContrat() {
        return typeContrat;
    }
    
    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }
    
    public String getNiveauExperience() {
        return niveauExperience;
    }
    
    public void setNiveauExperience(String niveauExperience) {
        this.niveauExperience = niveauExperience;
    }
    
    public Double getSalaireMin() {
        return salaireMin;
    }
    
    public void setSalaireMin(Double salaireMin) {
        this.salaireMin = salaireMin;
    }
    
    public Double getSalaireMax() {
        return salaireMax;
    }
    
    public void setSalaireMax(Double salaireMax) {
        this.salaireMax = salaireMax;
    }
    
    public Local getLocal() {
        return local;
    }
    
    public void setLocal(Local local) {
        this.local = local;
    }
    
    public Formation getFormation() {
        return formation;
    }
    
    public void setFormation(Formation formation) {
        this.formation = formation;
    }
    
    public Integer getAgeMin() {
        return ageMin;
    }
    
    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }
    
    public Integer getAgeMax() {
        return ageMax;
    }
    
    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }
    
    public Integer getNbrPersonne() {
        return nbrPersonne;
    }
    
    public void setNbrPersonne(Integer nbrPersonne) {
        this.nbrPersonne = nbrPersonne;
    }
    
    public String getAvantages() {
        return avantages;
    }
    
    public void setAvantages(String avantages) {
        this.avantages = avantages;
    }
    
    public LocalDateTime getDateLimiteCandidature() {
        return dateLimiteCandidature;
    }
    
    public void setDateLimiteCandidature(LocalDateTime dateLimiteCandidature) {
        this.dateLimiteCandidature = dateLimiteCandidature;
    }
    
    public String getPriorite() {
        return priorite;
    }
    
    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }
    
    public String getJustification() {
        return justification;
    }
    
    public void setJustification(String justification) {
        this.justification = justification;
    }
    
    public String getCommentaireDepartement() {
        return commentaireDepartement;
    }
    
    public void setCommentaireDepartement(String commentaireDepartement) {
        this.commentaireDepartement = commentaireDepartement;
    }
    
    public String getCommentaireRh() {
        return commentaireRh;
    }
    
    public void setCommentaireRh(String commentaireRh) {
        this.commentaireRh = commentaireRh;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }
    
    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    // Méthodes utilitaires
    public String getFormattedDateCreation() {
        return dateCreation != null ? dateCreation.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getFormattedDateTraitement() {
        return dateTraitement != null ? dateTraitement.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getFormattedDateLimiteCandidature() {
        return dateLimiteCandidature != null ? dateLimiteCandidature.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
    
    public String getNomDepartement() {
        return departement != null ? departement.getNomDepartement() : "";
    }
    
    public String getNomPoste() {
        return poste != null ? poste.getNom() : "";
    }
    
    public String getLibelleStatut() {
        return statutDemande != null ? statutDemande.getLibelle() : "";
    }
    
    public String getCouleurStatut() {
        return statutDemande != null ? statutDemande.getCouleur() : "secondary";
    }
    
    public boolean isEnAttente() {
        return statutDemande != null && statutDemande.isEnAttente();
    }
    
    public boolean isAcceptee() {
        return statutDemande != null && statutDemande.isAccepte();
    }
    
    public boolean isRefusee() {
        return statutDemande != null && statutDemande.isRefuse();
    }
    
    public String getPlagesSalaire() {
        if (salaireMin != null && salaireMax != null) {
            return String.format("%.0f - %.0f €", salaireMin, salaireMax);
        } else if (salaireMin != null) {
            return String.format("À partir de %.0f €", salaireMin);
        } else if (salaireMax != null) {
            return String.format("Jusqu'à %.0f €", salaireMax);
        }
        return "Non spécifié";
    }
    
    public String getPlagesAge() {
        if (ageMin != null && ageMax != null) {
            return String.format("%d - %d ans", ageMin, ageMax);
        } else if (ageMin != null) {
            return String.format("À partir de %d ans", ageMin);
        } else if (ageMax != null) {
            return String.format("Jusqu'à %d ans", ageMax);
        }
        return "Non spécifié";
    }
    
    public String getFormattedNbrPersonne() {
        if (nbrPersonne != null) {
            return nbrPersonne == 1 ? "1 personne" : nbrPersonne + " personnes";
        }
        return "Non spécifié";
    }
    
    public String getNomLocal() {
        return local != null ? local.getNom() : "Non spécifié";
    }
    
    public String getNomFormation() {
        return formation != null ? formation.getNom() : "Non spécifiée";
    }
    
    @Override
    public String toString() {
        return "DemandeOffre{" +
                "idDemandeOffre=" + idDemandeOffre +
                ", titreOffre='" + titreOffre + '\'' +
                ", poste=" + (poste != null ? poste.getNom() : null) +
                ", departement=" + (departement != null ? departement.getNomDepartement() : null) +
                ", statutDemande=" + (statutDemande != null ? statutDemande.getLibelle() : null) +
                ", dateCreation=" + dateCreation +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemandeOffre)) return false;
        DemandeOffre that = (DemandeOffre) o;
        return idDemandeOffre != null && idDemandeOffre.equals(that.idDemandeOffre);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}