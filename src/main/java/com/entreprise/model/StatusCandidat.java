package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "status_candidat")
public class StatusCandidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_candidat")
    private Long idStatusCandidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status", referencedColumnName = "id_status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidat", referencedColumnName = "id_candidat")
    private Candidat candidat;

    @Column(name = "date_modification")
    private LocalDate dateModification;

    // Constructeurs
    public StatusCandidat() {}

    public StatusCandidat(Status status, Candidat candidat, LocalDate dateModification) {
        this.status = status;
        this.candidat = candidat;
        this.dateModification = dateModification;
    }

    // Getters et Setters
    public Long getIdStatusCandidat() {
        return idStatusCandidat;
    }

    public void setIdStatusCandidat(Long idStatusCandidat) {
        this.idStatusCandidat = idStatusCandidat;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public LocalDate getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDate dateModification) {
        this.dateModification = dateModification;
    }
}
