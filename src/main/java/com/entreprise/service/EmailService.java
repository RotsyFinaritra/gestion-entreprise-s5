package com.entreprise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;

import com.entreprise.model.Candidat;
import com.entreprise.model.Entretien;
import com.entreprise.repository.CandidatRepository;

import jakarta.mail.internet.MimeMessage;

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

    @Autowired
    private ContratPdfService contratPdfService;

    public void envoyerContratEssai(Entretien entretien) throws Exception {
        try {
            // Générer le PDF du contrat
            byte[] contratPdf = contratPdfService.genererContratEssai(entretien);
            
            // Créer le message email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Configuration de base de l'email
            helper.setTo(entretien.getCandidat().getEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Contrat d'essai - " + entretien.getOffre().getPoste().getNom());
            
            // Corps de l'email
            String htmlContent = construireCorpsEmailContrat(entretien);
            helper.setText(htmlContent, true);
            
            // Ajouter le PDF en pièce jointe
            String nomFichier = "contrat_essai_" + 
                               entretien.getCandidat().getNom().toLowerCase() + "_" +
                               entretien.getCandidat().getPrenom().toLowerCase() + ".pdf";
            helper.addAttachment(nomFichier, new ByteArrayResource(contratPdf));
            
            // Envoyer l'email
            mailSender.send(message);
            
            System.out.println("Contrat envoyé avec succès à " + entretien.getCandidat().getEmail());
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du contrat : " + e.getMessage());
            throw new Exception("Erreur lors de l'envoi du contrat par email : " + e.getMessage(), e);
        }
    }
    
    private String construireCorpsEmailContrat(Entretien entretien) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                             color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .info-box { background: #f8f9fa; padding: 15px; border-left: 4px solid #667eea; 
                               margin: 20px 0; border-radius: 5px; }
                    .footer { background: #f1f1f1; padding: 15px; text-align: center; 
                             font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🎉 Félicitations !</h1>
                    <h2>Vous avez été sélectionné(e) pour un poste à l'essai</h2>
                </div>
                
                <div class="content">
                    <p>Bonjour <strong>""" + entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom() + """
                    </strong>,</p>
                    
                    <p>Nous avons le plaisir de vous informer que suite à votre entretien, vous avez été 
                    sélectionné(e) pour le poste de <strong>""" + entretien.getOffre().getPoste().getNom() + """
                    </strong> au sein de notre entreprise.</p>
                    
                    <div class="info-box">
                        <h3>📋 Détails du poste :</h3>
                        <ul>
                            <li><strong>Poste :</strong> """ + entretien.getOffre().getPoste().getNom() + """
                            </li>
                            <li><strong>Localisation :</strong> """ + entretien.getOffre().getLocal().getNom() + """
                            </li>
                            <li><strong>Période d'essai :</strong> 3 mois (renouvelable)</li>
                        </ul>
                    </div>
                    
                    <p>Vous trouverez en pièce jointe votre contrat de travail à l'essai. 
                    Veuillez le lire attentivement et nous le retourner signé dans les plus brefs délais.</p>
                    
                    <div class="info-box">
                        <h3>📞 Prochaines étapes :</h3>
                        <ol>
                            <li>Examiner le contrat ci-joint</li>
                            <li>Le signer et nous le retourner</li>
                            <li>Nous vous contacterons pour fixer votre date de prise de poste</li>
                        </ol>
                    </div>
                    
                    <p>Si vous avez des questions concernant ce contrat ou votre intégration, 
                    n'hésitez pas à nous contacter.</p>
                    
                    <p>Encore une fois, félicitations pour cette sélection !</p>
                    
                    <p>Cordialement,<br>
                    <strong>L'équipe Ressources Humaines</strong><br>
                    Gestion Entreprise S5</p>
                </div>
                
                <div class="footer">
                    <p>Cet email a été généré automatiquement. 
                    En cas de problème, veuillez contacter notre service RH.</p>
                </div>
            </body>
            </html>
            """;
    }

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
