package com.entreprise.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "note_entretien")
public class NoteEntretien {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_note")
    private Long idNote;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entretien", referencedColumnName = "id_entretien")
    private Entretien entretien;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section", referencedColumnName = "id_section")
    private SectionNoteEntretien section;
    
    @Column(name = "note_obtenue", nullable = false)
    private Double noteObtenue;
    
    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;
    
    @Column(name = "date_evaluation")
    private LocalDateTime dateEvaluation;
    
    // Constructeurs
    public NoteEntretien() {}
    
    public NoteEntretien(Entretien entretien, SectionNoteEntretien section, 
                        Double noteObtenue, String commentaire) {
        this.entretien = entretien;
        this.section = section;
        this.noteObtenue = noteObtenue;
        this.commentaire = commentaire;
        this.dateEvaluation = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdNote() {
        return idNote;
    }
    
    public void setIdNote(Long idNote) {
        this.idNote = idNote;
    }
    
    public Entretien getEntretien() {
        return entretien;
    }
    
    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }
    
    public SectionNoteEntretien getSection() {
        return section;
    }
    
    public void setSection(SectionNoteEntretien section) {
        this.section = section;
    }
    
    public Double getNoteObtenue() {
        return noteObtenue;
    }
    
    public void setNoteObtenue(Double noteObtenue) {
        this.noteObtenue = noteObtenue;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }
    
    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }
}
