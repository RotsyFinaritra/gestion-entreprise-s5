package com.entreprise.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;
    
    @Column(name = "username", unique = true, length = 50)
    private String username;
    
    @Column(name = "password", length = 255)
    private String password;
    
    @Column(name = "role", length = 50)
    private String role;
    
    // Propriétés spécifiques au département
    @Column(name = "nom_departement", length = 100)
    private String nomDepartement;
    
    @Column(name = "description_departement", columnDefinition = "TEXT")
    private String descriptionDepartement;
    
    @Column(name = "email_departement", length = 100)
    private String emailDepartement;
    
    // Relation OneToMany avec les postes
    @OneToMany(mappedBy = "departement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Poste> postes;
    
    // Constructeurs
    public User() {}
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public User(String username, String password, String role, String nomDepartement, String descriptionDepartement, String emailDepartement) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nomDepartement = nomDepartement;
        this.descriptionDepartement = descriptionDepartement;
        this.emailDepartement = emailDepartement;
    }
    
    // Getters et Setters
    public Long getIdUser() {
        return idUser;
    }
    
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getNomDepartement() {
        return nomDepartement;
    }
    
    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }
    
    public String getDescriptionDepartement() {
        return descriptionDepartement;
    }
    
    public void setDescriptionDepartement(String descriptionDepartement) {
        this.descriptionDepartement = descriptionDepartement;
    }
    
    public String getEmailDepartement() {
        return emailDepartement;
    }
    
    public void setEmailDepartement(String emailDepartement) {
        this.emailDepartement = emailDepartement;
    }
    
    public List<Poste> getPostes() {
        return postes;
    }
    
    public void setPostes(List<Poste> postes) {
        this.postes = postes;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", nomDepartement='" + nomDepartement + '\'' +
                ", emailDepartement='" + emailDepartement + '\'' +
                '}';
    }
}
