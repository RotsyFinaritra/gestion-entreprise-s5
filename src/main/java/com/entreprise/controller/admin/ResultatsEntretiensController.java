package com.entreprise.controller.admin;

import com.entreprise.model.*;
import com.entreprise.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/resultats-entretiens")
public class ResultatsEntretiensController {

    @Autowired
    private EntretienService entretienService;

    @Autowired
    private NoteEntretienService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private OffreService offreService;

    /**
     * Liste des résultats d'entretiens groupés par offre
     */
    @GetMapping
    public String listResultatsEntretiens(Model model, 
                                         HttpSession session, 
                                         RedirectAttributes redirectAttributes) {
        
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder aux résultats");
            return "redirect:/login";
        }

        try {
            // Récupérer tous les entretiens terminés
            List<Entretien> entretiensTermines = entretienService.findByStatut("terminé");
            
            // Grouper les entretiens par offre au lieu de par poste
            Map<Offre, List<Entretien>> entretiensParOffre = entretiensTermines.stream()
                .filter(e -> e.getOffre() != null)
                .collect(Collectors.groupingBy(Entretien::getOffre));

            // Calculer les moyennes pour chaque entretien
            Map<Long, Double> moyennesEntretiens = entretiensTermines.stream()
                .collect(Collectors.toMap(
                    Entretien::getIdEntretien,
                    e -> noteService.calculateMoyenneByEntretien(e.getIdEntretien())
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Trier les candidats par moyenne décroissante dans chaque offre
            entretiensParOffre.forEach((offre, entretiens) -> {
                entretiens.sort((e1, e2) -> Double.compare(
                    moyennesEntretiens.getOrDefault(e2.getIdEntretien(), 0.0),
                    moyennesEntretiens.getOrDefault(e1.getIdEntretien(), 0.0)
                ));
            });

            // Calculer les statistiques par offre
            Map<Offre, Double> moyennesParOffre = entretiensParOffre.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .mapToDouble(e -> moyennesEntretiens.getOrDefault(e.getIdEntretien(), 0.0))
                        .filter(m -> m > 0)
                        .average()
                        .orElse(0.0)
                ));

            model.addAttribute("entretiensParOffre", entretiensParOffre);
            model.addAttribute("moyennesEntretiens", moyennesEntretiens);
            model.addAttribute("moyennesParOffre", moyennesParOffre);
            model.addAttribute("totalEntretiens", entretiensTermines.size());
            
            // Calculer les statistiques globales
            double moyenneGenerale = moyennesEntretiens.isEmpty() ? 0.0 : 
                moyennesEntretiens.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double meilleurScore = moyennesEntretiens.isEmpty() ? 0.0 : 
                moyennesEntretiens.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                
            model.addAttribute("moyenneGenerale", moyenneGenerale);
            model.addAttribute("meilleurScore", meilleurScore);
            model.addAttribute("activeSection", "resultats-entretiens");
            model.addAttribute("pageTitle", "Résultats des entretiens");

            return "admin/resultats_entretiens/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du chargement des résultats: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    /**
     * Détail des résultats pour une offre spécifique
     */
    @GetMapping("/offre/{offreId}")
    public String detailResultatsOffre(@PathVariable Long offreId,
                                      Model model,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder aux résultats");
            return "redirect:/login";
        }

        try {
            // Récupérer l'offre
            Optional<Offre> offreOpt = offreService.findById(offreId);
            if (offreOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Offre introuvable");
                return "redirect:/admin/resultats-entretiens";
            }
            
            Offre offre = offreOpt.get();

            // Récupérer les entretiens terminés pour cette offre
            List<Entretien> entretiensOffre = entretienService.findByStatut("terminé")
                .stream()
                .filter(e -> e.getOffre() != null && 
                           e.getOffre().getIdOffre().equals(offreId))
                .collect(Collectors.toList());

            // Calculer les moyennes et récupérer les notes détaillées
            Map<Long, Double> moyennesEntretiens = entretiensOffre.stream()
                .collect(Collectors.toMap(
                    Entretien::getIdEntretien,
                    e -> noteService.calculateMoyenneByEntretien(e.getIdEntretien())
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // Trier par moyenne DÉCROISSANTE (meilleurs en premier)
            List<Entretien> entretiensOrdonnes = entretiensOffre.stream()
                .sorted((e1, e2) -> Double.compare(
                    moyennesEntretiens.getOrDefault(e2.getIdEntretien(), 0.0),
                    moyennesEntretiens.getOrDefault(e1.getIdEntretien(), 0.0)
                ))
                .collect(Collectors.toList());

            // Calculer la moyenne générale de l'offre
            double moyenneOffre = moyennesEntretiens.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

            // Calculer les statistiques détaillées
            double meilleurScore = moyennesEntretiens.isEmpty() ? 0.0 : 
                moyennesEntretiens.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            double scoreMin = moyennesEntretiens.isEmpty() ? 0.0 : 
                moyennesEntretiens.values().stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            long admissibles = moyennesEntretiens.values().stream().filter(note -> note >= 10.0).count();
            long excellence = moyennesEntretiens.values().stream().filter(note -> note >= 16.0).count();

            model.addAttribute("offre", offre);
            
            // Debug: Vérifier la valeur nbrPersonne dans le controller d'affichage
            System.out.println("DEBUG Controller - Offre ID: " + offre.getIdOffre());
            System.out.println("DEBUG Controller - nbrPersonne: " + offre.getNbrPersonne());
            System.out.println("DEBUG Controller - Offre complète: " + offre.toString());
            
            model.addAttribute("entretiensOffre", entretiensOrdonnes);
            model.addAttribute("moyennesEntretiens", moyennesEntretiens);
            model.addAttribute("moyenneOffre", moyenneOffre);
            model.addAttribute("totalCandidats", entretiensOffre.size());
            model.addAttribute("meilleurScore", meilleurScore);
            model.addAttribute("scoreMin", scoreMin);
            model.addAttribute("admissibles", admissibles);
            model.addAttribute("excellence", excellence);
            model.addAttribute("activeSection", "resultats-entretiens");
            model.addAttribute("pageTitle", "Résultats - " + offre.getPoste().getNom() + " (Offre #" + offre.getIdOffre() + ")");

            return "admin/resultats_entretiens/detail_offre";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du chargement des détails: " + e.getMessage());
            return "redirect:/admin/resultats-entretiens";
        }
    }
}
