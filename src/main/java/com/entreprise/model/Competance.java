package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "competance")
public class Competance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_competance")
    private Long idCompetance;

    @Column(name = "nom")
    private String nom;

    @OneToMany(mappedBy = "competance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PosteCompetance> posteCompetances;

    @OneToMany(mappedBy = "competance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CandidatCompetance> candidatCompetances;

    @OneToMany(mappedBy = "competance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;

    // Constructeurs
    public Competance() {}

    public Competance(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdCompetance() {
        return idCompetance;
    }

    public void setIdCompetance(Long idCompetance) {
        this.idCompetance = idCompetance;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<PosteCompetance> getPosteCompetances() {
        return posteCompetances;
    }

    public void setPosteCompetances(List<PosteCompetance> posteCompetances) {
        this.posteCompetances = posteCompetances;
    }

    public List<CandidatCompetance> getCandidatCompetances() {
        return candidatCompetances;
    }

    public void setCandidatCompetances(List<CandidatCompetance> candidatCompetances) {
        this.candidatCompetances = candidatCompetances;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
