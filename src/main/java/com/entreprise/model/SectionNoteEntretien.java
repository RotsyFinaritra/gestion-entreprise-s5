package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "section_note_entretien")
public class SectionNoteEntretien {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_section")
    private Long idSection;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_poste", referencedColumnName = "id_poste")
    private Poste poste;
    
    @Column(name = "nom_section", nullable = false)
    private String nomSection;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "note_max", nullable = false)
    private Integer noteMax;
    
    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;
    
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NoteEntretien> noteEntretiens;
    
    // Constructeurs
    public SectionNoteEntretien() {}
    
    public SectionNoteEntretien(Poste poste, String nomSection, String description, 
                               Integer noteMax, Integer ordreAffichage) {
        this.poste = poste;
        this.nomSection = nomSection;
        this.description = description;
        this.noteMax = noteMax;
        this.ordreAffichage = ordreAffichage;
    }
    
    // Getters et Setters
    public Long getIdSection() {
        return idSection;
    }
    
    public void setIdSection(Long idSection) {
        this.idSection = idSection;
    }
    
    public Poste getPoste() {
        return poste;
    }
    
    public void setPoste(Poste poste) {
        this.poste = poste;
    }
    
    public String getNomSection() {
        return nomSection;
    }
    
    public void setNomSection(String nomSection) {
        this.nomSection = nomSection;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getNoteMax() {
        return noteMax;
    }
    
    public void setNoteMax(Integer noteMax) {
        this.noteMax = noteMax;
    }
    
    public Integer getOrdreAffichage() {
        return ordreAffichage;
    }
    
    public void setOrdreAffichage(Integer ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }
    
    public List<NoteEntretien> getNoteEntretiens() {
        return noteEntretiens;
    }
    
    public void setNoteEntretiens(List<NoteEntretien> noteEntretiens) {
        this.noteEntretiens = noteEntretiens;
    }
}
