package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "formation_candidat")
public class FormationCandidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formation_candidat")
    private Long idFormationCandidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", referencedColumnName = "id_formation")
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidat", referencedColumnName = "id_candidat")
    private Candidat candidat;

    // Constructeurs
    public FormationCandidat() {}

    public FormationCandidat(Formation formation, Candidat candidat) {
        this.formation = formation;
        this.candidat = candidat;
    }

    // Getters et Setters
    public Long getIdFormationCandidat() {
        return idFormationCandidat;
    }

    public void setIdFormationCandidat(Long idFormationCandidat) {
        this.idFormationCandidat = idFormationCandidat;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }
}
