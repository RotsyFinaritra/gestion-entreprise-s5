package com.entreprise.controller;

import com.entreprise.model.*;
import com.entreprise.repository.*;
import com.entreprise.service.EmailService;
import com.entreprise.service.Test2Service;         // Calculer et assigner le résultat
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/test2")
public class Test2Controller {
    
    @Autowired
    private ReponseQuestionRepository reponseQuestionRepository;
    
    @Autowired
    private CandidatRepository candidatRepository;
    
    @Autowired
    private Test2Service test2Service;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${test2.duree-minutes:10}")
    private int dureeMinutes;
    
    @Value("${test2.seuil-reussite:60}")
    private int seuilReussite;

    @GetMapping("/questionnaire/{token}")
    public String afficherQuestionnaire(@PathVariable String token, Model model) {
        System.out.println("=== ACCÈS TEST 2 QUESTIONNAIRE ===");
        System.out.println("Token reçu: " + token);
        
        // Vérifier si le token est valide
        if (!emailService.isTokenValid(token)) {
            System.out.println("Token invalide ou expiré");
            model.addAttribute("error", "Lien expiré ou invalide");
            return "test2/erreur";
        }
        System.out.println("Token validé avec succès");
        
        // Récupérer le candidat associé au token
        Candidat candidat = emailService.getCandidatFromToken(token);
        if (candidat == null) {
            System.out.println("Candidat non trouvé pour le token");
            model.addAttribute("error", "Token invalide");
            return "test2/erreur";
        }
        System.out.println("Candidat trouvé: " + candidat.getPrenom() + " " + candidat.getNom() + " (ID: " + candidat.getIdCandidat() + ")");
        
        // Vérifier le statut du candidat
        String statutActuel = test2Service.getStatutActuelCandidat(candidat);
        System.out.println("Statut actuel du candidat: '" + statutActuel + "'");
        if (!"Invité Test 2".equals(statutActuel)) {
            System.out.println("ERREUR: Candidat non autorisé - Statut attendu: 'Invité Test 2', Statut actuel: '" + statutActuel + "'");
            model.addAttribute("error", "Vous n'êtes pas autorisé à passer ce test");
            return "test2/erreur";
        }
        System.out.println("Candidat autorisé à passer le test");
        
        // Récupérer les questions pour ce candidat
        System.out.println("=== RÉCUPÉRATION QUESTIONS ===");
        List<Question> questions = test2Service.getQuestionsForCandidat(candidat.getIdCandidat());
        System.out.println("Nombre de questions trouvées: " + questions.size());
        
        if (questions.isEmpty()) {
            System.out.println("ERREUR: Aucune question trouvée");
            System.out.println("Candidat ID: " + candidat.getIdCandidat());
            System.out.println("Offre: " + (candidat.getOffre() != null ? candidat.getOffre().getIdOffre() : "null"));
            System.out.println("Poste: " + (candidat.getOffre() != null && candidat.getOffre().getPoste() != null ? 
                candidat.getOffre().getPoste().getNom() : "null"));
            System.out.println("=============================");
            model.addAttribute("error", "Aucune question trouvée pour ce test");
            return "test2/erreur";
        }
        
        System.out.println("Questions trouvées:");
        for (Question q : questions) {
            System.out.println("  - ID: " + q.getIdQuestion() + " | Énoncé: " + q.getEnonce());
        }
        System.out.println("=============================");
        
        // Préparer les données pour l'affichage
        Map<Question, List<ReponseQuestion>> questionsReponses = new HashMap<>();
        for (Question question : questions) {
            List<ReponseQuestion> reponses = reponseQuestionRepository.findByQuestion(question);
            questionsReponses.put(question, reponses);
        }
        
        model.addAttribute("candidat", candidat);
        model.addAttribute("questionsReponses", questionsReponses);
        model.addAttribute("token", token);
        model.addAttribute("dureeMinutes", dureeMinutes);
        
        return "test2/questionnaire";
    }
    
    @PostMapping("/sauvegarde/{token}")
    public ResponseEntity<String> sauvegarderProgression(@PathVariable String token, 
                                                        @RequestParam Map<String, String> reponses) {
        try {
            // Valider le token
            Long candidatId = test2Service.validateToken(token);
            if (candidatId == null) {
                return ResponseEntity.badRequest().body("Token invalide");
            }
            
            // Sauvegarder les réponses en cours
            Optional<Candidat> candidatOpt = candidatRepository.findById(candidatId);
            if (candidatOpt.isPresent()) {
                test2Service.sauvegarderReponsesProgression(candidatId, reponses);
                return ResponseEntity.ok("Progression sauvegardée");
            }
            
            return ResponseEntity.badRequest().body("Candidat non trouvé");
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde progression: " + e.getMessage());
            return ResponseEntity.status(500).body("Erreur de sauvegarde");
        }
    }
    
    @PostMapping("/soumettre/{token}")
    public String soumettreReponses(@PathVariable String token, 
                                   @RequestParam Map<String, String> reponses,
                                   @RequestParam(required = false) String soumissionAutomatique,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        // Vérifier si le token est valide
        if (!emailService.isTokenValid(token)) {
            model.addAttribute("error", "Lien expiré ou invalide");
            return "test2/erreur";
        }
        
        // Récupérer le candidat associé au token
        Candidat candidat = emailService.getCandidatFromToken(token);
        String statutActuel = test2Service.getStatutActuelCandidat(candidat);
        if (candidat == null || !"Invité Test 2".equals(statutActuel)) {
            model.addAttribute("error", "Erreur lors de la soumission");
            return "test2/erreur";
        }
        
        try {
            // Convertir les réponses Map<String, String> vers Map<Long, Long>
            Map<Long, Long> reponsesConverties = new HashMap<>();
            for (Map.Entry<String, String> entry : reponses.entrySet()) {
                if (entry.getKey().startsWith("question_")) {
                    Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                    Long reponseId = Long.parseLong(entry.getValue());
                    reponsesConverties.put(questionId, reponseId);
                }
            }
            
            String typesoumission = "true".equals(soumissionAutomatique) ? "AUTOMATIQUE" : "MANUELLE";
            System.out.println("Type de soumission: " + typesoumission);
            
            // Sauvegarder les réponses
            test2Service.sauvegarderReponses(candidat.getIdCandidat(), reponsesConverties, token);
            
            // Calculer et assigner le résultat
            double score = test2Service.calculerResultatTest2(candidat);
            
            // Préparer les données pour l'affichage des résultats
            model.addAttribute("candidat", candidat);
            model.addAttribute("score", score);
            model.addAttribute("seuilReussite", seuilReussite);
            model.addAttribute("totalQuestions", reponsesConverties.size());
            model.addAttribute("soumissionAutomatique", soumissionAutomatique);
            
            return "test2/termine";
            
        } catch (Exception e) {
            System.err.println("Erreur soumission test: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la soumission du test: " + e.getMessage());
            return "test2/erreur";
        }
    }
    
    @GetMapping("/resultat/{token}")
    public String afficherResultat(@PathVariable String token, Model model) {
        // Vérifier si le token est valide
        if (!emailService.isTokenValid(token)) {
            model.addAttribute("error", "Lien expiré ou invalide");
            return "test2/erreur";
        }
        
        // Récupérer le candidat associé au token
        Candidat candidat = emailService.getCandidatFromToken(token);
        if (candidat == null) {
            model.addAttribute("error", "Token invalide");
            return "test2/erreur";
        }
        
        // Vérifier que le test a été complété
        String statutActuel = test2Service.getStatutActuelCandidat(candidat);
        if (!"Admis Test 2".equals(statutActuel) && 
            !"Recalé Test 2".equals(statutActuel)) {
            model.addAttribute("error", "Le test n'a pas encore été complété");
            return "test2/erreur";
        }
        
        model.addAttribute("candidat", candidat);
        model.addAttribute("statut", statutActuel);
        
        return "test2/resultat";
    }
}
