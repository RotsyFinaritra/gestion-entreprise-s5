package com.entreprise.controller;

import com.entreprise.model.Entretien;
import com.entreprise.model.Offre;
import com.entreprise.model.SectionNoteEntretien;
import com.entreprise.model.NoteEntretien;
import com.entreprise.service.EntretienService;
import com.entreprise.service.UserService;
import com.entreprise.service.OffreService;
import com.entreprise.service.SectionNoteEntretienService;
import com.entreprise.service.NoteEntretienService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/entretiens")
public class EntretienController {

    @Autowired
    private EntretienService entretienService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OffreService offreService;
    
    @Autowired
    private SectionNoteEntretienService sectionNoteEntretienService;
    
    @Autowired
    private NoteEntretienService noteEntretienService;

    /**
     * Liste de tous les entretiens pour l'administration
     */
    @GetMapping
    public String listEntretiens(Model model, 
                                HttpSession session, 
                                RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        List<Entretien> entretiens = entretienService.findAll();
        
        // Ajouter les moyennes des notes pour chaque entretien
        Map<Long, Double> moyennesNotes = noteEntretienService.calculateMoyennesForMultipleEntretiens(
            entretiens.stream().map(Entretien::getIdEntretien).toList()
        );
        
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("moyennesNotes", moyennesNotes);
        model.addAttribute("activeSection", "entretiens");
        
        return "entretien/list";
    }

    /**
     * Liste des entretiens par offre
     */
    @GetMapping("/offre/{offreId}")
    public String listEntretiensByOffre(@PathVariable Long offreId,
                                      Model model,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        Optional<Offre> offreOpt = offreService.findById(offreId);
        if (offreOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Offre introuvable");
            return "redirect:/entretiens";
        }

        Offre offre = offreOpt.get();
        List<Entretien> entretiens = entretienService.findByOffreId(offreId);
        
        // Statistiques
        Map<String, Object> statistiques = entretienService.getStatistiquesEntretiens(offreId);
        
        // Moyennes des notes
        Map<Long, Double> moyennesNotes = noteEntretienService.calculateMoyennesForMultipleEntretiens(
            entretiens.stream().map(Entretien::getIdEntretien).toList()
        );

        model.addAttribute("offre", offre);
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("statistiques", statistiques);
        model.addAttribute("moyennesNotes", moyennesNotes);
        model.addAttribute("activeSection", "entretiens");
        
        return "entretien/list-by-offre";
    }

    /**
     * Détail d'un entretien
     */
    @GetMapping("/{id}")
    public String detailEntretien(@PathVariable Long id,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        Optional<Entretien> entretienOpt = entretienService.findById(id);
        if (entretienOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Entretien introuvable");
            return "redirect:/entretiens";
        }

        Entretien entretien = entretienOpt.get();
        
        // Récupérer les sections de notation pour le poste
        List<SectionNoteEntretien> sections = sectionNoteEntretienService.findByPosteId(
            entretien.getOffre().getPoste().getIdPoste()
        );
        
        // Récupérer les notes existantes
        List<NoteEntretien> notes = noteEntretienService.findByEntretienId(id);
        Map<Long, NoteEntretien> notesMap = noteEntretienService.getNotesMapByEntretien(id);
        
        // Calculer la moyenne
        Double moyenne = noteEntretienService.calculateMoyenneByEntretien(id);

        model.addAttribute("entretien", entretien);
        model.addAttribute("sections", sections);
        model.addAttribute("notes", notes);
        model.addAttribute("notesMap", notesMap);
        model.addAttribute("moyenne", moyenne);
        model.addAttribute("activeSection", "entretiens");
        
        return "entretien/detail";
    }

    /**
     * Mettre à jour le statut d'un entretien
     */
    @PostMapping("/{id}/statut")
    public String updateStatutEntretien(@PathVariable Long id,
                                      @RequestParam String statut,
                                      @RequestParam(required = false) String commentaire,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }

        try {
            Optional<Entretien> entretienOpt = entretienService.findById(id);
            if (entretienOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Entretien introuvable");
                return "redirect:/entretiens";
            }

            Entretien entretien = entretienOpt.get();
            entretien.setStatut(statut);
            
            if (commentaire != null && !commentaire.trim().isEmpty()) {
                entretien.setCommentaire(commentaire);
            }
            
            entretienService.save(entretien);
            
            redirectAttributes.addFlashAttribute("success", 
                "Statut de l'entretien mis à jour avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la mise à jour du statut: " + e.getMessage());
        }

        return "redirect:/entretiens/" + id;
    }

    /**
     * Annuler un entretien
     */
    @PostMapping("/{id}/annuler")
    public String annulerEntretien(@PathVariable Long id,
                                 @RequestParam String raison,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }

        try {
            entretienService.annulerEntretien(id, raison);
            redirectAttributes.addFlashAttribute("success", 
                "Entretien annulé avec succès. Un email a été envoyé au candidat.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de l'annulation: " + e.getMessage());
        }

        return "redirect:/entretiens";
    }

    /**
     * Supprimer un entretien
     */
    @GetMapping("/{id}/supprimer")
    public String supprimerEntretien(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent effectuer cette action");
            return "redirect:/login";
        }

        try {
            Optional<Entretien> entretienOpt = entretienService.findById(id);
            if (entretienOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Entretien introuvable");
                return "redirect:/entretiens";
            }

            Entretien entretien = entretienOpt.get();
            Long offreId = entretien.getOffre().getIdOffre();
            
            entretienService.deleteById(id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Entretien supprimé avec succès");
                
            return "redirect:/entretiens/offre/" + offreId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la suppression: " + e.getMessage());
            return "redirect:/entretiens";
        }
    }

    /**
     * Afficher le formulaire d'évaluation rapide depuis la liste des entretiens
     */
    @GetMapping("/{id}/noter")
    public String noterEntretienRapide(@PathVariable Long id,
                                     Model model,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent accéder à cette page");
            return "redirect:/login";
        }

        // Rediriger vers le formulaire complet de notation
        return "redirect:/note-entretien/form/" + id;
    }
}
