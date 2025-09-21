package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "statut_demande")
public class StatutDemande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_demande")
    private Long idStatutDemande;
    
    @Column(name = "libelle", nullable = false, unique = true, length = 50)
    private String libelle;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "couleur", length = 20)
    private String couleur; // Pour l'affichage (ex: "primary", "success", "danger")
    
    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "actif")
    private Boolean actif = true;
    
    // Relations
    @OneToMany(mappedBy = "statutDemande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DemandeOffre> demandesOffre;
    
    @OneToMany(mappedBy = "statutDemande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StatutOffre> statutsOffre;
    
    // Constructeurs
    public StatutDemande() {
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
    }
    
    public StatutDemande(String libelle, String description, String couleur, Integer ordreAffichage) {
        this();
        this.libelle = libelle;
        this.description = description;
        this.couleur = couleur;
        this.ordreAffichage = ordreAffichage;
    }
    
    // Getters et Setters
    public Long getIdStatutDemande() {
        return idStatutDemande;
    }
    
    public void setIdStatutDemande(Long idStatutDemande) {
        this.idStatutDemande = idStatutDemande;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCouleur() {
        return couleur;
    }
    
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    
    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }
    
    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    public List<DemandeOffre> getDemandesOffre() {
        return demandesOffre;
    }
    
    public void setDemandesOffre(List<DemandeOffre> demandesOffre) {
        this.demandesOffre = demandesOffre;
    }
    
    public List<StatutOffre> getStatutsOffre() {
        return statutsOffre;
    }
    
    public void setStatutsOffre(List<StatutOffre> statutsOffre) {
        this.statutsOffre = statutsOffre;
    }
    
    // Méthodes utilitaires
    public boolean isEnAttente() {
        return "EN_ATTENTE".equals(this.libelle);
    }
    
    public boolean isAccepte() {
        return "ACCEPTE".equals(this.libelle);
    }
    
    public boolean isRefuse() {
        return "REFUSE".equals(this.libelle);
    }
    
    @Override
    public String toString() {
        return "StatutDemande{" +
                "idStatutDemande=" + idStatutDemande +
                ", libelle='" + libelle + '\'' +
                ", description='" + description + '\'' +
                ", couleur='" + couleur + '\'' +
                ", ordreAffichage=" + ordreAffichage +
                ", actif=" + actif +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatutDemande)) return false;
        StatutDemande that = (StatutDemande) o;
        return idStatutDemande != null && idStatutDemande.equals(that.idStatutDemande);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}