package com.entreprise.controller;

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

@Controller
@RequestMapping("/note-entretien")
public class NoteEntretienFormController {

    @Autowired
    private NoteEntretienService noteService;

    @Autowired
    private EntretienService entretienService;

    @Autowired
    private SectionNoteEntretienService sectionService;

    @Autowired
    private UserService userService;

    /**
     * Afficher le formulaire de notation d'un entretien
     */
    @GetMapping("/form/{entretienId}")
    public String showNoteForm(@PathVariable Long entretienId, 
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent noter les entretiens");
            return "redirect:/login";
        }

        try {
            // Récupérer l'entretien
            Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
            if (entretienOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Entretien introuvable");
                return "redirect:/entretiens";
            }

            Entretien entretien = entretienOpt.get();
            
            // Vérifier que l'entretien a un poste associé
            if (entretien.getOffre() == null || entretien.getOffre().getPoste() == null) {
                redirectAttributes.addFlashAttribute("error", 
                    "Impossible de noter cet entretien : aucun poste associé");
                return "redirect:/entretiens";
            }

            // Récupérer les sections de notation pour le poste
            List<SectionNoteEntretien> sections = sectionService.findByPosteId(
                entretien.getOffre().getPoste().getIdPoste()
            );

            if (sections.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", 
                    "Aucune section de notation configurée pour ce poste. Veuillez d'abord créer des sections.");
                return "redirect:/admin/sections-notation/new";
            }

            // Récupérer les notes existantes
            Map<Long, NoteEntretien> notesExistantes = noteService.getNotesMapByEntretien(entretienId);

            // Calculer la moyenne actuelle si des notes existent
            Double moyenneActuelle = noteService.calculateMoyenneByEntretien(entretienId);

            model.addAttribute("entretien", entretien);
            model.addAttribute("sections", sections);
            model.addAttribute("notesExistantes", notesExistantes);
            model.addAttribute("moyenneActuelle", moyenneActuelle);
            model.addAttribute("pageTitle", "Noter l'entretien");
            model.addAttribute("pageDescription", "Évaluation de " + 
                entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom());

            return "note_entretien/form";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors du chargement du formulaire de notation: " + e.getMessage());
            return "redirect:/entretiens";
        }
    }

    /**
     * Sauvegarder les notes de l'entretien
     */
    @PostMapping("/save/{entretienId}")
    public String saveNotes(@PathVariable Long entretienId,
                           @RequestParam Map<String, String> allParams,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", 
                "Accès refusé. Seuls les administrateurs peuvent noter les entretiens");
            return "redirect:/login";
        }

        try {
            // Vérifier que l'entretien existe
            Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
            if (entretienOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Entretien introuvable");
                return "redirect:/entretiens";
            }

            Entretien entretien = entretienOpt.get();
            
            // Récupérer les sections
            List<SectionNoteEntretien> sections = sectionService.findByPosteId(
                entretien.getOffre().getPoste().getIdPoste()
            );

            int notesTraitees = 0;

            // Traiter chaque section
            for (SectionNoteEntretien section : sections) {
                String noteParam = "note_" + section.getIdSection();
                String commentaireParam = "commentaire_" + section.getIdSection();
                
                String noteStr = allParams.get(noteParam);
                String commentaire = allParams.get(commentaireParam);
                
                if (noteStr != null && !noteStr.trim().isEmpty()) {
                    try {
                        double noteValue = Double.parseDouble(noteStr);
                        
                        // Valider que la note ne dépasse pas le maximum
                        if (noteValue > section.getNoteMax()) {
                            redirectAttributes.addFlashAttribute("error", 
                                "La note pour '" + section.getNomSection() + "' ne peut pas dépasser " + section.getNoteMax());
                            return "redirect:/note-entretien/form/" + entretienId;
                        }
                        
                        if (noteValue < 0) {
                            redirectAttributes.addFlashAttribute("error", 
                                "La note pour '" + section.getNomSection() + "' ne peut pas être négative");
                            return "redirect:/note-entretien/form/" + entretienId;
                        }

                        // Créer ou mettre à jour la note
                        NoteEntretien note = new NoteEntretien();
                        note.setEntretien(entretien);
                        note.setSection(section);
                        note.setNoteObtenue(noteValue);
                        note.setCommentaire(commentaire != null ? commentaire.trim() : "");

                        noteService.save(note);
                        notesTraitees++;

                    } catch (NumberFormatException e) {
                        redirectAttributes.addFlashAttribute("error", 
                            "Note invalide pour '" + section.getNomSection() + "'. Veuillez saisir un nombre valide.");
                        return "redirect:/note-entretien/form/" + entretienId;
                    }
                }
            }

            if (notesTraitees > 0) {
                // Mettre à jour le statut de l'entretien à "terminé"
                entretien.setStatut("terminé");
                entretienService.save(entretien);
                
                redirectAttributes.addFlashAttribute("success", 
                    "Notation sauvegardée avec succès ! " + notesTraitees + " note(s) enregistrée(s). L'entretien est maintenant marqué comme terminé.");
            } else {
                redirectAttributes.addFlashAttribute("warning", 
                    "Aucune note n'a été saisie. Veuillez remplir au moins une section.");
                return "redirect:/note-entretien/form/" + entretienId;
            }

            // Rediriger vers les détails de l'entretien ou la liste
            return "redirect:/entretiens/" + entretienId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la sauvegarde: " + e.getMessage());
            return "redirect:/note-entretien/form/" + entretienId;
        }
    }
}
