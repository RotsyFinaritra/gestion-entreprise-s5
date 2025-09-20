package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_experience")
    private Long idExperience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_poste", referencedColumnName = "id_poste")
    private Poste poste;

    @Column(name = "description")
    private String description;

    // Constructeurs
    public Experience() {}

    public Experience(Poste poste, String description) {
        this.poste = poste;
        this.description = description;
    }

    // Getters et Setters
    public Long getIdExperience() {
        return idExperience;
    }

    public void setIdExperience(Long idExperience) {
        this.idExperience = idExperience;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
