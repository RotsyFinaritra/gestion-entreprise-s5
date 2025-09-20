package com.entreprise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reponse_candidat")
public class ReponseCandidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reponse_candidat")
    private Long idReponseCandidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", referencedColumnName = "id_question")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reponse_question", referencedColumnName = "id_reponse_question")
    private ReponseQuestion reponseQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_candidat", referencedColumnName = "id_candidat")
    private Candidat candidat;

    // Constructeurs
    public ReponseCandidat() {}

    public ReponseCandidat(Question question, ReponseQuestion reponseQuestion, Candidat candidat) {
        this.question = question;
        this.reponseQuestion = reponseQuestion;
        this.candidat = candidat;
    }

    // Getters et Setters
    public Long getIdReponseCandidat() {
        return idReponseCandidat;
    }

    public void setIdReponseCandidat(Long idReponseCandidat) {
        this.idReponseCandidat = idReponseCandidat;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ReponseQuestion getReponseQuestion() {
        return reponseQuestion;
    }

    public void setReponseQuestion(ReponseQuestion reponseQuestion) {
        this.reponseQuestion = reponseQuestion;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }
}
