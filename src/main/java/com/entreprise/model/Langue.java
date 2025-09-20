package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "langue")
public class Langue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_langue")
    private Long idLangue;

    @Column(name = "nom")
    private String nom;

    // Constructeurs
    public Langue() {}

    public Langue(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdLangue() {
        return idLangue;
    }

    public void setIdLangue(Long idLangue) {
        this.idLangue = idLangue;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
