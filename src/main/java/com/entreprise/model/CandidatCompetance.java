package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidat_competance")
public class CandidatCompetance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_candidat_competance")
    private Long idCandidatCompetance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidat", referencedColumnName = "id_candidat")
    private Candidat candidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competance", referencedColumnName = "id_competance")
    private Competance competance;

    // Constructeurs
    public CandidatCompetance() {}

    public CandidatCompetance(Candidat candidat, Competance competance) {
        this.candidat = candidat;
        this.competance = competance;
    }

    // Getters et Setters
    public Long getIdCandidatCompetance() {
        return idCandidatCompetance;
    }

    public void setIdCandidatCompetance(Long idCandidatCompetance) {
        this.idCandidatCompetance = idCandidatCompetance;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Competance getCompetance() {
        return competance;
    }

    public void setCompetance(Competance competance) {
        this.competance = competance;
    }
}
