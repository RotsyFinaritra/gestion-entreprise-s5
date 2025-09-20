package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "local")
public class Local {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Long idLocal;

    @Column(name = "nom")
    private String nom;

    // Constructeurs
    public Local() {}

    public Local(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
