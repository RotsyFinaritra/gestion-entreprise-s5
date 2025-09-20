package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "poste_competance")
public class PosteCompetance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_poste_competance")
    private Long idPosteCompetance;

    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "id_poste", referencedColumnName = "id_poste")
    private Poste poste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competance", referencedColumnName = "id_competance")
    private Competance competance;

    // Constructeurs
    public PosteCompetance() {}

    public PosteCompetance(Poste poste, Competance competance) {
        this.poste = poste;
        this.competance = competance;
    }

    // Getters et Setters
    public Long getIdPosteCompetance() {
        return idPosteCompetance;
    }

    public void setIdPosteCompetance(Long idPosteCompetance) {
        this.idPosteCompetance = idPosteCompetance;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public Competance getCompetance() {
        return competance;
    }

    public void setCompetance(Competance competance) {
        this.competance = competance;
    }
}
