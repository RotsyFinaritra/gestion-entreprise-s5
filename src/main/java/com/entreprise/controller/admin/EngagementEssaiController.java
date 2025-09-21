package com.entreprise.controller.admin;

import com.entreprise.model.*;
import com.entreprise.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/engagements")
public class EngagementEssaiController {

    @Autowired
    private EntretienService entretienService;

    @Autowired
    private UserService userService;

    @Autowired
    private OffreService offreService;

    @Autowired
    private ContratPdfService contratPdfService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NoteEntretienService noteEntretienService;

    /**
     * Valider la sélection des candidats pour engagement à l'essai
     */
    @PostMapping("/valider-selection/{offreId}")
    @ResponseBody
    public ResponseEntity<String> validerSelection(@PathVariable Long offreId,
                                                 @RequestBody SelectionRequest request,
                                                 HttpSession session) {
        
        if (!userService.isAdmin(session)) {
            return ResponseEntity.status(403).body("Accès refusé");
        }

        try {
            List<Long> entretiensIds = request.getEntretiensIds();
            
            if (entretiensIds == null || entretiensIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Aucun candidat sélectionné");
            }
            
            // Vérifier que l'offre existe et forcer le refresh depuis la base
            Optional<Offre> offreOpt = offreService.findById(offreId);
            if (offreOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Offre introuvable");
            }
            
            Offre offre = offreOpt.get();
            
            // Forcer le refresh de l'entité depuis la base de données
            try {
                // Re-fetch l'offre pour s'assurer d'avoir les bonnes valeurs
                offre = offreService.findById(offreId).orElseThrow();
            } catch (Exception e) {
                System.out.println("Erreur lors du refresh de l'offre: " + e.getMessage());
            }
            
            // Debug: Afficher les valeurs pour diagnostic
            System.out.println("DEBUG - ID de l'offre demandée: " + offreId);
            System.out.println("DEBUG - ID de l'offre récupérée: " + offre.getIdOffre());
            System.out.println("DEBUG - Nom du poste: " + offre.getPoste().getNom());
            System.out.println("DEBUG - Nombre candidats sélectionnés: " + entretiensIds.size());
            System.out.println("DEBUG - Nombre requis par l'offre: " + offre.getNbrPersonne());
            System.out.println("DEBUG - Type nbrPersonne: " + (offre.getNbrPersonne() != null ? offre.getNbrPersonne().getClass() : "null"));
            System.out.println("DEBUG - Égalité: " + (entretiensIds.size() == offre.getNbrPersonne()));
            
            // Vérifier le nombre de candidats sélectionnés
            Integer nbrPersonneRequis = offre.getNbrPersonne();
            if (nbrPersonneRequis == null || entretiensIds.size() != nbrPersonneRequis.intValue()) {
                return ResponseEntity.badRequest().body(
                    String.format("Le nombre de candidats sélectionnés (%d) ne correspond pas au besoin de l'offre (%s)", 
                                 entretiensIds.size(), nbrPersonneRequis));
            }

            // Mettre à jour le statut des entretiens sélectionnés
            int candidatsEngagesAvecSucces = 0;
            for (Long entretienId : entretiensIds) {
                Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
                if (entretienOpt.isPresent()) {
                    Entretien entretien = entretienOpt.get();
                    
                    // Vérifier que l'entretien appartient bien à cette offre
                    if (!entretien.getOffre().getIdOffre().equals(offreId)) {
                        System.out.println("WARNING - Entretien " + entretienId + " n'appartient pas à l'offre " + offreId);
                        continue;
                    }
                    
                    // Vérifier que le candidat n'est pas déjà engagé
                    if ("engagement_essai".equals(entretien.getStatut())) {
                        System.out.println("INFO - Candidat " + entretien.getCandidat().getNom() + " déjà engagé à l'essai");
                        continue;
                    }
                    
                    entretien.setStatut("engagement_essai");
                    entretien.setCommentaire("Candidat sélectionné pour engagement à l'essai - " + 
                                           LocalDateTime.now().toString());
                    entretienService.save(entretien);
                    candidatsEngagesAvecSucces++;
                    System.out.println("SUCCESS - Candidat " + entretien.getCandidat().getNom() + " engagé à l'essai");
                } else {
                    System.out.println("ERROR - Entretien " + entretienId + " introuvable");
                }
            }

            return ResponseEntity.ok("Candidats engagés avec succès (" + candidatsEngagesAvecSucces + "/" + entretiensIds.size() + ")");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'engagement: " + e.getMessage());
        }
    }

    /**
     * Liste des candidats engagés à l'essai
     */
    @GetMapping
    public String listEngagementsEssai(Model model,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder aux engagements");
            return "redirect:/login";
        }

        try {
            // Récupérer tous les entretiens avec statut "engagement_essai"
            List<Entretien> engagementsEssai = entretienService.findByStatut("engagement_essai");
            
            // Grouper par offre
            Map<Offre, List<Entretien>> engagementsParOffre = engagementsEssai.stream()
                .filter(e -> e.getOffre() != null)
                .collect(Collectors.groupingBy(Entretien::getOffre));

            model.addAttribute("engagementsParOffre", engagementsParOffre);
            model.addAttribute("totalEngagements", engagementsEssai.size());
            model.addAttribute("activeSection", "engagements-essai");
            model.addAttribute("pageTitle", "Engagements à l'essai");

            return "admin/engagements_essai/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du chargement des engagements: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    /**
     * Détail des engagements pour une offre spécifique
     */
    @GetMapping("/offre/{offreId}")
    public String detailEngagementsOffre(@PathVariable Long offreId,
                                        Model model,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder aux engagements");
            return "redirect:/login";
        }

        try {
            // Récupérer l'offre
            Optional<Offre> offreOpt = offreService.findById(offreId);
            if (offreOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Offre introuvable");
                return "redirect:/admin/engagements";
            }
            
            Offre offre = offreOpt.get();

            // Récupérer les engagements pour cette offre
            List<Entretien> candidatsEngages = entretienService.findByStatut("engagement_essai")
                .stream()
                .filter(e -> e.getOffre() != null && 
                           e.getOffre().getIdOffre().equals(offreId))
                .collect(Collectors.toList());
            
            // Debug pour vérifier les données
            System.out.println("DEBUG - Offre ID: " + offreId);
            System.out.println("DEBUG - Candidats avec statut engagement_essai: " + 
                             entretienService.findByStatut("engagement_essai").size());
            System.out.println("DEBUG - Candidats engagés pour cette offre: " + candidatsEngages.size());
            
            // Calculer les notes moyennes pour chaque entretien
            Map<Long, Double> notesMoyennes = new HashMap<>();
            for (Entretien e : candidatsEngages) {
                System.out.println("  - Candidat: " + e.getCandidat().getPrenom() + " " + e.getCandidat().getNom() + 
                                 " (Entretien ID: " + e.getIdEntretien() + ", Offre ID: " + e.getOffre().getIdOffre() + ")");
                
                // Calculer la note moyenne pour cet entretien
                Double noteMoyenne = noteEntretienService.calculateMoyenneByEntretien(e.getIdEntretien());
                if (noteMoyenne != null) {
                    notesMoyennes.put(e.getIdEntretien(), noteMoyenne);
                    System.out.println("    Note moyenne: " + String.format("%.1f/20", noteMoyenne));
                } else {
                    notesMoyennes.put(e.getIdEntretien(), 0.0);
                    System.out.println("    Aucune note trouvée");
                }
            }

            model.addAttribute("offre", offre);
            model.addAttribute("candidatsEngages", candidatsEngages);
            model.addAttribute("notesMoyennes", notesMoyennes);
            model.addAttribute("totalCandidats", candidatsEngages.size());
            model.addAttribute("activeSection", "engagements-essai");
            model.addAttribute("pageTitle", "Engagements - " + offre.getPoste().getNom() + " (Offre #" + offre.getIdOffre() + ")");

            return "admin/engagements_essai/detail_offre";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du chargement des détails: " + e.getMessage());
            return "redirect:/admin/engagements";
        }
    }

    /**
     * Générer le contrat d'essai en PDF
     */
    @GetMapping("/contrat-pdf/{entretienId}")
    public ResponseEntity<byte[]> genererContratPDF(@PathVariable Long entretienId,
                                                   HttpSession session) {
        
        if (!userService.isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
            if (entretienOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Entretien entretien = entretienOpt.get();
            
            // Générer le PDF avec le service spécialisé
            byte[] contratPdf = contratPdfService.genererContratEssai(entretien);
            
            String nomFichier = "contrat_essai_" + 
                               entretien.getCandidat().getNom().toLowerCase() + "_" +
                               entretien.getCandidat().getPrenom().toLowerCase() + ".pdf";
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomFichier + "\"")
                .body(contratPdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body(("Erreur lors de la génération du PDF : " + e.getMessage()).getBytes());
        }
    }

    /**
     * Envoyer le contrat par email
     */
    @PostMapping("/envoyer-contrat/{entretienId}")
    @ResponseBody
    public ResponseEntity<String> envoyerContrat(@PathVariable Long entretienId,
                                               HttpSession session) {
        
        if (!userService.isAdmin(session)) {
            return ResponseEntity.status(403).body("Accès refusé");
        }

        try {
            Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
            if (entretienOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Entretien introuvable");
            }
            
            Entretien entretien = entretienOpt.get();
            
            // Envoyer le contrat par email avec le service spécialisé
            emailService.envoyerContratEssai(entretien);
            
            return ResponseEntity.ok("Contrat envoyé avec succès à " + entretien.getCandidat().getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    /**
     * Générer tous les contrats d'une offre en PDF
     */
    @GetMapping("/contrats-offre-pdf/{offreId}")
    public ResponseEntity<byte[]> genererContratsOffrePDF(@PathVariable Long offreId,
                                                        HttpSession session) {
        if (!userService.isAdmin(session)) {
            return ResponseEntity.status(403)
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body("Accès refusé".getBytes());
        }

        try {
            List<Entretien> candidatsEngages = entretienService.findByStatut("engagement_essai")
                .stream()
                .filter(e -> e.getOffre() != null && e.getOffre().getIdOffre().equals(offreId))
                .collect(Collectors.toList());
            
            if (candidatsEngages.isEmpty()) {
                return ResponseEntity.status(404)
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body("Aucun candidat engagé pour cette offre".getBytes());
            }

            // Générer un ZIP contenant tous les contrats
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos);
                
                for (Entretien entretien : candidatsEngages) {
                    byte[] contratPdf = contratPdfService.genererContratEssai(entretien);
                    
                    String nomFichier = "contrat_essai_" + 
                                       entretien.getCandidat().getNom().toLowerCase() + "_" +
                                       entretien.getCandidat().getPrenom().toLowerCase() + ".pdf";
                    
                    java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(nomFichier);
                    zos.putNextEntry(zipEntry);
                    zos.write(contratPdf);
                    zos.closeEntry();
                }
                
                zos.close();
                
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"contrats_offre_" + offreId + ".zip\"")
                    .body(baos.toByteArray());
            }
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .header("Content-Type", "text/plain; charset=UTF-8")
                .body(("Erreur lors de la génération des contrats : " + e.getMessage()).getBytes());
        }
    }

    /**
     * Envoyer tous les contrats d'une offre par email
     */
    @PostMapping("/envoyer-contrats-offre/{offreId}")
    @ResponseBody
    public ResponseEntity<String> envoyerContratsOffre(@PathVariable Long offreId,
                                                     HttpSession session) {
        if (!userService.isAdmin(session)) {
            return ResponseEntity.status(403).body("Accès refusé");
        }

        try {
            List<Entretien> candidatsEngages = entretienService.findByStatut("engagement_essai")
                .stream()
                .filter(e -> e.getOffre() != null && e.getOffre().getIdOffre().equals(offreId))
                .collect(Collectors.toList());
            
            if (candidatsEngages.isEmpty()) {
                return ResponseEntity.status(404).body("Aucun candidat engagé pour cette offre");
            }

            int envoyesAvecSucces = 0;
            StringBuilder erreurs = new StringBuilder();
            
            for (Entretien entretien : candidatsEngages) {
                try {
                    emailService.envoyerContratEssai(entretien);
                    envoyesAvecSucces++;
                } catch (Exception e) {
                    erreurs.append("Erreur pour ").append(entretien.getCandidat().getEmail())
                           .append(": ").append(e.getMessage()).append("; ");
                }
            }
            
            String message = envoyesAvecSucces + " contrat(s) envoyé(s) avec succès sur " + candidatsEngages.size();
            if (erreurs.length() > 0) {
                message += ". Erreurs: " + erreurs.toString();
            }

            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'envoi des contrats: " + e.getMessage());
        }
    }
}
