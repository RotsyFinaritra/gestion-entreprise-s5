package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "formation")
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formation")
    private Long idFormation;

    @Column(name = "nom")
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departement")
    private User departement;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PosteFormation> posteFormations;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FormationCandidat> formationCandidats;

    // Constructeurs
    public Formation() {}

    public Formation(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdFormation() {
        return idFormation;
    }

    public void setIdFormation(Long idFormation) {
        this.idFormation = idFormation;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public User getDepartement() {
        return departement;
    }

    public void setDepartement(User departement) {
        this.departement = departement;
    }

    public List<PosteFormation> getPosteFormations() {
        return posteFormations;
    }

    public void setPosteFormations(List<PosteFormation> posteFormations) {
        this.posteFormations = posteFormations;
    }

    public List<FormationCandidat> getFormationCandidats() {
        return formationCandidats;
    }

    public void setFormationCandidats(List<FormationCandidat> formationCandidats) {
        this.formationCandidats = formationCandidats;
    }
}
