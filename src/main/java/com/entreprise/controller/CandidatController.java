package com.entreprise.controller;

import com.entreprise.model.Candidat;
import com.entreprise.service.CandidatService;
import com.entreprise.service.GenreService;
import com.entreprise.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Comparator;

@Controller
@RequestMapping("/candidats")
public class CandidatController {
    
    @Autowired
    private CandidatService candidatService;
    
    @Autowired
    private GenreService genreService;
    
    @Autowired
    private OffreService offreService;
    
    @GetMapping
    public String listCandidats(Model model) {
        model.addAttribute("candidats", candidatService.findAll());
        model.addAttribute("activeSection", "candidats");
        return "candidat/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("candidat", new Candidat());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("offres", offreService.findAll());
        return "candidat/form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return candidatService.findById(id)
            .map(candidat -> {
                model.addAttribute("candidat", candidat);
                model.addAttribute("genres", genreService.findAll());
                model.addAttribute("offres", offreService.findAll());
                return "candidat/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Candidat introuvable");
                return "redirect:/candidats";
            });
    }
    
    @GetMapping("/detail/{id}")
    public String showDetailCandidat(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return candidatService.findById(id)
            .map(candidat -> {
                // Trier les statuts par date de modification décroissante (plus récent en premier)
                if (candidat.getStatusCandidats() != null) {
                    candidat.getStatusCandidats().sort(
                        Comparator.comparing(sc -> sc.getDateModification(), 
                        Comparator.reverseOrder())
                    );
                }
                
                model.addAttribute("candidat", candidat);
                model.addAttribute("activeSection", "candidats");
                return "candidat/detail";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Candidat introuvable");
                return "redirect:/candidats";
            });
    }
    
    @PostMapping
    public String saveCandidat(@ModelAttribute Candidat candidat, RedirectAttributes redirectAttributes) {
        try {
            if (candidat.getDateDepot() == null) {
                candidat.setDateDepot(LocalDate.now());
            }
            candidatService.save(candidat);
            redirectAttributes.addFlashAttribute("success", "Candidat enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement");
        }
        return "redirect:/candidats";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCandidat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            candidatService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Candidat supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/candidats";
    }
}
