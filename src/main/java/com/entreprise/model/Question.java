package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_question")
    private Long idQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competance", referencedColumnName = "id_competance")
    private Competance competance;

    @Column(name = "enonce")
    private String enonce;

    @Column(name = "note")
    private Integer note;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReponseQuestion> reponseQuestions;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReponseCandidat> reponseCandidats;

    // Constructeurs
    public Question() {}

    public Question(Competance competance, String enonce, Integer note) {
        this.competance = competance;
        this.enonce = enonce;
        this.note = note;
    }

    // Getters et Setters
    public Long getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(Long idQuestion) {
        this.idQuestion = idQuestion;
    }

    public Competance getCompetance() {
        return competance;
    }

    public void setCompetance(Competance competance) {
        this.competance = competance;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public List<ReponseQuestion> getReponseQuestions() {
        return reponseQuestions;
    }

    public void setReponseQuestions(List<ReponseQuestion> reponseQuestions) {
        this.reponseQuestions = reponseQuestions;
    }

    public List<ReponseCandidat> getReponseCandidats() {
        return reponseCandidats;
    }

    public void setReponseCandidats(List<ReponseCandidat> reponseCandidats) {
        this.reponseCandidats = reponseCandidats;
    }
}
