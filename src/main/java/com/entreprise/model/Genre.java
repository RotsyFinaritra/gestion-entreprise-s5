package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sexe")
    private Long idSexe;

    @Column(name = "nom")
    private String nom;

    // Constructeurs
    public Genre() {}

    public Genre(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdSexe() {
        return idSexe;
    }

    public void setIdSexe(Long idSexe) {
        this.idSexe = idSexe;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
