package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "reponse_question")
public class ReponseQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reponse_question")
    private Long idReponseQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", referencedColumnName = "id_question")
    private Question question;

    @Column(name = "choix")
    private String choix;

    @Column(name = "valeur")
    private Boolean valeur;

    @OneToMany(mappedBy = "reponseQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReponseCandidat> reponseCandidats;

    // Constructeurs
    public ReponseQuestion() {}

    public ReponseQuestion(Question question, String choix, Boolean valeur) {
        this.question = question;
        this.choix = choix;
        this.valeur = valeur;
    }

    // Getters et Setters
    public Long getIdReponseQuestion() {
        return idReponseQuestion;
    }

    public void setIdReponseQuestion(Long idReponseQuestion) {
        this.idReponseQuestion = idReponseQuestion;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getChoix() {
        return choix;
    }

    public void setChoix(String choix) {
        this.choix = choix;
    }

    public Boolean getValeur() {
        return valeur;
    }

    public void setValeur(Boolean valeur) {
        this.valeur = valeur;
    }

    public List<ReponseCandidat> getReponseCandidats() {
        return reponseCandidats;
    }

    public void setReponseCandidats(List<ReponseCandidat> reponseCandidats) {
        this.reponseCandidats = reponseCandidats;
    }
}
