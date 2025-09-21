package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statut_offre")
public class StatutOffre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_offre")
    private Long idStatutOffre;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_offre", nullable = false)
    private Offre offre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_demande", nullable = false)
    private StatutDemande statutDemande;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_modification")
    private User userModification; // Utilisateur qui a effectué le changement
    
    // Attributs
    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;
    
    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;
    
    @Column(name = "motif_changement", length = 255)
    private String motifChangement;
    
    @Column(name = "ancien_statut", length = 50)
    private String ancienStatut; // Pour tracer l'historique
    
    @Column(name = "nouveau_statut", length = 50)
    private String nouveauStatut;
    
    // Constructeurs
    public StatutOffre() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public StatutOffre(Offre offre, StatutDemande statutDemande, User userModification, String commentaire) {
        this();
        this.offre = offre;
        this.statutDemande = statutDemande;
        this.userModification = userModification;
        this.commentaire = commentaire;
        this.nouveauStatut = statutDemande.getLibelle();
    }
    
    // Getters et Setters
    public Long getIdStatutOffre() {
        return idStatutOffre;
    }
    
    public void setIdStatutOffre(Long idStatutOffre) {
        this.idStatutOffre = idStatutOffre;
    }
    
    public Offre getOffre() {
        return offre;
    }
    
    public void setOffre(Offre offre) {
        this.offre = offre;
    }
    
    public StatutDemande getStatutDemande() {
        return statutDemande;
    }
    
    public void setStatutDemande(StatutDemande statutDemande) {
        this.statutDemande = statutDemande;
    }
    
    public User getUserModification() {
        return userModification;
    }
    
    public void setUserModification(User userModification) {
        this.userModification = userModification;
    }
    
    public LocalDateTime getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public String getMotifChangement() {
        return motifChangement;
    }
    
    public void setMotifChangement(String motifChangement) {
        this.motifChangement = motifChangement;
    }
    
    public String getAncienStatut() {
        return ancienStatut;
    }
    
    public void setAncienStatut(String ancienStatut) {
        this.ancienStatut = ancienStatut;
    }
    
    public String getNouveauStatut() {
        return nouveauStatut;
    }
    
    public void setNouveauStatut(String nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }
    
    // Méthodes utilitaires
    public String getFormattedDateChangement() {
        return dateChangement != null ? dateChangement.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    public String getUserName() {
        return userModification != null ? userModification.getUsername() : "Système";
    }
    
    @Override
    public String toString() {
        return "StatutOffre{" +
                "idStatutOffre=" + idStatutOffre +
                ", offre=" + (offre != null ? offre.getIdOffre() : null) +
                ", statutDemande=" + (statutDemande != null ? statutDemande.getLibelle() : null) +
                ", dateChangement=" + dateChangement +
                ", ancienStatut='" + ancienStatut + '\'' +
                ", nouveauStatut='" + nouveauStatut + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatutOffre)) return false;
        StatutOffre that = (StatutOffre) o;
        return idStatutOffre != null && idStatutOffre.equals(that.idStatutOffre);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}