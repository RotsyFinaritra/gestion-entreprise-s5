package com.entreprise.controller.admin;

import com.entreprise.model.*;
import com.entreprise.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/notes-entretien")
public class NoteEntretienController {

    @Autowired
    private NoteEntretienService noteService;

    @Autowired
    private EntretienService entretienService;

    @Autowired
    private SectionNoteEntretienService sectionService;

    @Autowired
    private OffreService offreService;

    @GetMapping("/entretien/{entretienId}")
    public String notesByEntretien(@PathVariable Long entretienId, Model model) {
        Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
        if (entretienOpt.isEmpty()) {
            return "redirect:/admin/entretiens/list";
        }

        Entretien entretien = entretienOpt.get();
        Long posteId = entretien.getOffre().getPoste().getIdPoste();
        
        List<SectionNoteEntretien> sections = sectionService.findByPosteId(posteId);
        Map<Long, NoteEntretien> notesMap = noteService.getNotesMapByEntretien(entretienId);

        model.addAttribute("entretien", entretien);
        model.addAttribute("sections", sections);
        model.addAttribute("notesMap", notesMap);
        model.addAttribute("pageTitle", "Noter l'entretien");
        model.addAttribute("pageDescription", "Évaluation de " + entretien.getCandidat().getPrenom() 
                          + " " + entretien.getCandidat().getNom());
        model.addAttribute("activeSection", "entretiens");
        return "admin/note_entretien/form";
    }

    @PostMapping("/save/{entretienId}")
    public String saveNotes(@PathVariable Long entretienId,
                           @RequestParam Map<String, String> params,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<Entretien> entretienOpt = entretienService.findById(entretienId);
            if (entretienOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Entretien non trouvé");
                return "redirect:/admin/entretiens/list";
            }

            Entretien entretien = entretienOpt.get();
            Long offreId = entretien.getOffre().getIdOffre();

            // Traiter chaque note
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.startsWith("note_") && !value.isEmpty()) {
                    Long sectionId = Long.parseLong(key.replace("note_", ""));
                    Double noteValue = Double.parseDouble(value);
                    String commentaire = params.get("commentaire_" + sectionId);

                    Optional<SectionNoteEntretien> sectionOpt = sectionService.findById(sectionId);
                    if (sectionOpt.isPresent()) {
                        // Vérifier si une note existe déjà
                        Optional<NoteEntretien> existingNote = noteService.findByEntretienAndSection(entretienId, sectionId);
                        
                        NoteEntretien note;
                        if (existingNote.isPresent()) {
                            note = existingNote.get();
                            note.setNoteObtenue(noteValue);
                            note.setCommentaire(commentaire);
                        } else {
                            note = new NoteEntretien(entretien, sectionOpt.get(), noteValue, commentaire);
                        }
                        
                        noteService.save(note);
                    }
                }
            }

            // Marquer l'entretien comme terminé si ce n'est pas déjà fait
            if (!"terminé".equals(entretien.getStatut())) {
                entretien.setStatut("terminé");
                entretienService.save(entretien);
            }

            redirectAttributes.addFlashAttribute("success", "Notes enregistrées avec succès");
            return "redirect:/admin/entretiens/by-offre/" + offreId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement des notes : " + e.getMessage());
            return "redirect:/admin/notes-entretien/entretien/" + entretienId;
        }
    }

    @GetMapping("/resultats/{offreId}")
    public String resultatsParOffre(@PathVariable Long offreId, Model model) {
        Optional<Offre> offreOpt = offreService.findById(offreId);
        if (offreOpt.isEmpty()) {
            return "redirect:/admin/entretiens/list";
        }

        Offre offre = offreOpt.get();
        List<Entretien> entretiens = entretienService.findByOffreId(offreId);
        
        // Filtrer seulement les entretiens terminés
        List<Entretien> entretiensTermines = entretiens.stream()
                .filter(e -> "terminé".equals(e.getStatut()))
                .toList();

        model.addAttribute("offre", offre);
        model.addAttribute("entretiens", entretiensTermines);
        model.addAttribute("pageTitle", "Résultats Entretiens");
        model.addAttribute("pageDescription", "Résultats pour " + offre.getPoste().getNom());
        model.addAttribute("activeSection", "resultats-entretiens");
        return "admin/note_entretien/resultats";
    }

    @GetMapping("/liste-resultats")
    public String listeResultats(Model model) {
        List<Offre> offres = offreService.findAll();
        model.addAttribute("offres", offres);
        model.addAttribute("pageTitle", "Résultats des Entretiens");
        model.addAttribute("pageDescription", "Consulter les résultats des entretiens par offre");
        model.addAttribute("activeSection", "resultats-entretiens");
        return "admin/note_entretien/liste_resultats";
    }
}
