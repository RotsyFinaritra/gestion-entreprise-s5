package com.entreprise.controller;

import com.entreprise.model.PosteCompetance;
import com.entreprise.service.PosteCompetanceService;
import com.entreprise.service.PosteService;
import com.entreprise.service.CompetanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/poste-competances")
public class PosteCompetanceController {
    
    @Autowired
    private PosteCompetanceService posteCompetanceService;
    
    @Autowired
    private PosteService posteService;
    
    @Autowired
    private CompetanceService competanceService;
    
    @GetMapping
    public String listPosteCompetances(Model model) {
        model.addAttribute("posteCompetances", posteCompetanceService.findAll());
        model.addAttribute("activeSection", "poste-competances");
        return "poste_competance/list";
    }
    
    @GetMapping("/new")
    public String newPosteCompetance(Model model) {
        model.addAttribute("posteCompetance", new PosteCompetance());
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("competances", competanceService.findAll());
        model.addAttribute("activeSection", "poste-competances");
        return "poste_competance/form";
    }
    
    @PostMapping
    public String savePosteCompetance(@ModelAttribute PosteCompetance posteCompetance, 
                                      RedirectAttributes redirectAttributes) {
        try {
            // Vérifier si la relation existe déjà
            if (posteCompetanceService.existsByPosteAndCompetance(
                    posteCompetance.getPoste().getIdPoste(), 
                    posteCompetance.getCompetance().getIdCompetance())) {
                redirectAttributes.addFlashAttribute("error", 
                    "Cette relation poste-compétence existe déjà.");
                return "redirect:/poste-competances/new";
            }
            
            posteCompetanceService.save(posteCompetance);
            redirectAttributes.addFlashAttribute("success", 
                "Relation poste-compétence créée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la création de la relation: " + e.getMessage());
        }
        return "redirect:/poste-competances";
    }
    
    @GetMapping("/edit/{id}")
    public String editPosteCompetance(@PathVariable Long id, Model model) {
        posteCompetanceService.findById(id).ifPresentOrElse(
            posteCompetance -> {
                model.addAttribute("posteCompetance", posteCompetance);
                model.addAttribute("postes", posteService.findAll());
                model.addAttribute("competances", competanceService.findAll());
                model.addAttribute("activeSection", "poste-competances");
            },
            () -> model.addAttribute("error", "Relation poste-compétence non trouvée")
        );
        return "poste_competance/form";
    }
    
    @PostMapping("/update/{id}")
    public String updatePosteCompetance(@PathVariable Long id, 
                                        @ModelAttribute PosteCompetance posteCompetance, 
                                        RedirectAttributes redirectAttributes) {
        try {
            posteCompetance.setIdPosteCompetance(id);
            posteCompetanceService.save(posteCompetance);
            redirectAttributes.addFlashAttribute("success", 
                "Relation poste-compétence modifiée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la modification: " + e.getMessage());
        }
        return "redirect:/poste-competances";
    }
    
    @PostMapping("/delete/{id}")
    public String deletePosteCompetance(@PathVariable Long id, 
                                        RedirectAttributes redirectAttributes) {
        try {
            posteCompetanceService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", 
                "Relation poste-compétence supprimée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/poste-competances";
    }
    
    @GetMapping("/by-poste/{posteId}")
    public String getCompetancesByPoste(@PathVariable Long posteId, Model model) {
        model.addAttribute("posteCompetances", posteCompetanceService.findByPosteId(posteId));
        model.addAttribute("poste", posteService.findById(posteId).orElse(null));
        model.addAttribute("activeSection", "poste-competances");
        return "poste_competance/by_poste";
    }
    
    @GetMapping("/by-competance/{competanceId}")
    public String getPostesByCompetance(@PathVariable Long competanceId, Model model) {
        model.addAttribute("posteCompetances", posteCompetanceService.findByCompetanceId(competanceId));
        model.addAttribute("competance", competanceService.findById(competanceId).orElse(null));
        model.addAttribute("activeSection", "poste-competances");
        return "poste_competance/by_competance";
    }
}
