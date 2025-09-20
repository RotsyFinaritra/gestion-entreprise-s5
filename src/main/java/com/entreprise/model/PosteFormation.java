package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "poste_formation")
public class PosteFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_poste_formation")
    private Long idPosteFormation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_poste", referencedColumnName = "id_poste")
    private Poste poste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", referencedColumnName = "id_formation")
    private Formation formation;

    @Column(name = "niveau")
    private String niveau;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Constructeurs
    public PosteFormation() {}

    public PosteFormation(Poste poste, Formation formation) {
        this.poste = poste;
        this.formation = formation;
    }

    public PosteFormation(Poste poste, Formation formation, String niveau, String description) {
        this.poste = poste;
        this.formation = formation;
        this.niveau = niveau;
        this.description = description;
    }

    // Getters et Setters
    public Long getIdPosteFormation() {
        return idPosteFormation;
    }

    public void setIdPosteFormation(Long idPosteFormation) {
        this.idPosteFormation = idPosteFormation;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
