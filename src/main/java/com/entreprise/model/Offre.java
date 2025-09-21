package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "offre")
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_offre")
    private Long idOffre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_poste", referencedColumnName = "id_poste")
    private Poste poste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", referencedColumnName = "id_formation")
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_local", referencedColumnName = "id_local")
    private Local local;

    @Column(name = "mission", columnDefinition = "TEXT")
    private String mission;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column(name = "dateFin")
    private LocalDate dateFin;

    @Column(name = "age_min")
    private Integer ageMin;

    @Column(name = "age_max")
    private Integer ageMax;

    @Column(name = "nbrPersonne")
    private Integer nbrPersonne;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Candidat> candidats;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande_offre")
    private DemandeOffre demandeOffre; // Demande d'offre qui a généré cette offre
    
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StatutOffre> statutsOffre; // Historique des changements de statut

    // Constructeurs
    public Offre() {}

    // Getters et Setters
    public Long getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(Long idOffre) {
        this.idOffre = idOffre;
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

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public Integer getNbrPersonne() {
        return nbrPersonne;
    }

    public void setNbrPersonne(Integer nbrPersonne) {
        this.nbrPersonne = nbrPersonne;
    }

    public List<Candidat> getCandidats() {
        return candidats;
    }

    public void setCandidats(List<Candidat> candidats) {
        this.candidats = candidats;
    }
    
    public DemandeOffre getDemandeOffre() {
        return demandeOffre;
    }
    
    public void setDemandeOffre(DemandeOffre demandeOffre) {
        this.demandeOffre = demandeOffre;
    }
    
    public List<StatutOffre> getStatutsOffre() {
        return statutsOffre;
    }
    
    public void setStatutsOffre(List<StatutOffre> statutsOffre) {
        this.statutsOffre = statutsOffre;
    }
}
