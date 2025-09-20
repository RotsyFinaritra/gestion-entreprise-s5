package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "entretien")
public class Entretien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entretien")
    private Long idEntretien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidat", referencedColumnName = "id_candidat")
    private Candidat candidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_offre", referencedColumnName = "id_offre")
    private Offre offre;

    @Column(name = "date_envoi_mail", nullable = false)
    private LocalDate dateEnvoiMail;

    @Column(name = "date_heure_entretien", nullable = false)
    private LocalDateTime dateHeureEntretien;

    @Column(name = "duree_entretien", nullable = false)
    private Integer dureeEntretien; // en minutes

    @Column(name = "statut", length = 50, nullable = false)
    private String statut; // programmé, confirmé, terminé, annulé

    @Column(name = "lieu_entretien", length = 255)
    private String lieuEntretien;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    // Constructeurs
    public Entretien() {}

    public Entretien(Candidat candidat, Offre offre, LocalDate dateEnvoiMail, 
                     LocalDateTime dateHeureEntretien, Integer dureeEntretien, String statut) {
        this.candidat = candidat;
        this.offre = offre;
        this.dateEnvoiMail = dateEnvoiMail;
        this.dateHeureEntretien = dateHeureEntretien;
        this.dureeEntretien = dureeEntretien;
        this.statut = statut;
    }

    // Getters et Setters
    public Long getIdEntretien() {
        return idEntretien;
    }

    public void setIdEntretien(Long idEntretien) {
        this.idEntretien = idEntretien;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public LocalDate getDateEnvoiMail() {
        return dateEnvoiMail;
    }

    public void setDateEnvoiMail(LocalDate dateEnvoiMail) {
        this.dateEnvoiMail = dateEnvoiMail;
    }

    public LocalDateTime getDateHeureEntretien() {
        return dateHeureEntretien;
    }

    public void setDateHeureEntretien(LocalDateTime dateHeureEntretien) {
        this.dateHeureEntretien = dateHeureEntretien;
    }

    public Integer getDureeEntretien() {
        return dureeEntretien;
    }

    public void setDureeEntretien(Integer dureeEntretien) {
        this.dureeEntretien = dureeEntretien;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getLieuEntretien() {
        return lieuEntretien;
    }

    public void setLieuEntretien(String lieuEntretien) {
        this.lieuEntretien = lieuEntretien;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    @Override
    public String toString() {
        return "Entretien{" +
                "idEntretien=" + idEntretien +
                ", candidat=" + (candidat != null ? candidat.getPrenom() + " " + candidat.getNom() : "null") +
                ", offre=" + (offre != null ? offre.getIdOffre() : "null") +
                ", dateEnvoiMail=" + dateEnvoiMail +
                ", dateHeureEntretien=" + dateHeureEntretien +
                ", dureeEntretien=" + dureeEntretien +
                ", statut='" + statut + '\'' +
                ", lieuEntretien='" + lieuEntretien + '\'' +
                '}';
    }
}
