package com.entreprise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.entreprise.model.Candidat;
import com.entreprise.repository.CandidatRepository;
import com.entreprise.repository.CandidatRepository;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@entreprise.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Autowired
    private CandidatRepository candidatRepository;

    public void envoyerLienTest2(Candidat candidat) {
        try {
            System.out.println("=== ENVOI EMAIL TEST 2 ===");
            System.out.println("Candidat: " + candidat.getPrenom() + " " + candidat.getNom());
            System.out.println("Email: " + candidat.getEmail());
            System.out.println("Mode mail: " + (fromEmail != null ? "REAL" : "MOCK"));
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(candidat.getEmail());
            message.setSubject("Félicitations ! Invitation au Test 2 - " + candidat.getOffre().getPoste().getNom());
            
            String contenu = String.format(
                "Bonjour %s %s,\n\n" +
                "Félicitations ! Vous avez réussi le premier test pour le poste de %s.\n\n" +
                "Nous vous invitons maintenant à passer le deuxième test qui consiste en un questionnaire technique.\n\n" +
                "Veuillez cliquer sur le lien suivant pour accéder au test :\n" +
                "%s/test2/questionnaire/%s\n\n" +
                "Ce test évalue vos compétences techniques et doit être complété dans les 24 heures.\n\n" +
                "Bonne chance !\n\n" +
                "L'équipe de recrutement",
                candidat.getPrenom(),
                candidat.getNom(),
                candidat.getOffre().getPoste().getNom(),
                baseUrl,
                generateToken(candidat)
            );
            
            message.setText(contenu);
            System.out.println("Contenu email préparé, envoi en cours...");
            mailSender.send(message);
            
            System.out.println("Email envoyé avec succès à : " + candidat.getEmail());
            System.out.println("=========================");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + candidat.getEmail() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void envoyerEmail(String destinataire, String sujet, String contenu) {
        try {
            System.out.println("=== ENVOI EMAIL GENERIQUE ===");
            System.out.println("Destinataire: " + destinataire);
            System.out.println("Sujet: " + sujet);
            System.out.println("Mode mail: " + (fromEmail != null ? "REAL" : "MOCK"));
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinataire);
            message.setSubject(sujet);
            message.setText(contenu);
            
            System.out.println("Contenu email préparé, envoi en cours...");
            mailSender.send(message);
            
            System.out.println("Email envoyé avec succès à : " + destinataire);
            System.out.println("=========================");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + destinataire + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateToken(Candidat candidat) {
        // Génère un token simple basé sur l'ID du candidat et un timestamp
        // En production, utiliser un système plus sécurisé comme JWT
        long timestamp = System.currentTimeMillis();
        return candidat.getIdCandidat() + "_" + timestamp + "_" + (candidat.getEmail().hashCode() & 0x7fffffff);
    }
    
    public Long getCandidatIdFromToken(String token) {
        try {
            String[] parts = token.split("_");
            if (parts.length >= 1) {
                return Long.parseLong(parts[0]);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing du token : " + e.getMessage());
        }
        return null;
    }
    
    public boolean isTokenValid(String token, Long candidatId) {
        try {
            String[] parts = token.split("_");
            if (parts.length == 3) {
                Long tokenCandidatId = Long.parseLong(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                long currentTime = System.currentTimeMillis();
                
                // Token valide si c'est le bon candidat et moins de 24h
                boolean isCorrectCandidat = tokenCandidatId.equals(candidatId);
                boolean isNotExpired = (currentTime - timestamp) < (24 * 60 * 60 * 1000); // 24h en ms
                
                return isCorrectCandidat && isNotExpired;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du token : " + e.getMessage());
        }
        return false;
    }
    
    public boolean isTokenValid(String token) {
        Long candidatId = getCandidatIdFromToken(token);
        if (candidatId == null) {
            return false;
        }
        return isTokenValid(token, candidatId);
    }
    
    public Candidat getCandidatFromToken(String token) {
        Long candidatId = getCandidatIdFromToken(token);
        if (candidatId == null) {
            return null;
        }
        return candidatRepository.findById(candidatId).orElse(null);
    }
}
