package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "poste")
public class Poste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_poste")
    private Long idPoste;

    @Column(name = "nom")
    private String nom;

    @OneToMany(mappedBy = "poste", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Experience> experiences;

    @OneToMany(mappedBy = "poste", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Profil> profils;

    @OneToMany(mappedBy = "poste", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Offre> offres;

    @OneToMany(mappedBy = "poste", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PosteCompetance> posteCompetances;

    @OneToMany(mappedBy = "poste", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PosteFormation> posteFormations;

    // Constructeurs
    public Poste() {}

    public Poste(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdPoste() {
        return idPoste;
    }

    public void setIdPoste(Long idPoste) {
        this.idPoste = idPoste;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    public List<Profil> getProfils() {
        return profils;
    }

    public void setProfils(List<Profil> profils) {
        this.profils = profils;
    }

    public List<Offre> getOffres() {
        return offres;
    }

    public void setOffres(List<Offre> offres) {
        this.offres = offres;
    }

    public List<PosteCompetance> getPosteCompetances() {
        return posteCompetances;
    }

    public void setPosteCompetances(List<PosteCompetance> posteCompetances) {
        this.posteCompetances = posteCompetances;
    }

    public List<PosteFormation> getPosteFormations() {
        return posteFormations;
    }

    public void setPosteFormations(List<PosteFormation> posteFormations) {
        this.posteFormations = posteFormations;
    }
}
